package africa.za.atech.spring.aio.functions.chats;

import africa.za.atech.spring.aio.functions.assistant.AssistantService;
import africa.za.atech.spring.aio.functions.assistant.database.model.Assistants;
import africa.za.atech.spring.aio.functions.chats.database.model.Chats;
import africa.za.atech.spring.aio.functions.chats.database.model.ChatsHistory;
import africa.za.atech.spring.aio.functions.chats.database.model.ChatsMeta;
import africa.za.atech.spring.aio.functions.chats.database.repo.RepoChats;
import africa.za.atech.spring.aio.functions.chats.database.repo.RepoChatsHistory;
import africa.za.atech.spring.aio.functions.chats.database.repo.RepoChatsMeta;
import africa.za.atech.spring.aio.functions.chats.dto.ChatHistoryDTO;
import africa.za.atech.spring.aio.functions.chats.dto.ChatListDTO;
import africa.za.atech.spring.aio.functions.chats.dto.ChatRequestDTO;
import africa.za.atech.spring.aio.functions.chats.dto.ChatResponseDTO;
import africa.za.atech.spring.aio.utils.HelperTools;
import africa.za.atech.spring.aio.utils.OutputTool;
import africa.za.atech.spring.aio.utils.openai.OpenAiClient;
import africa.za.atech.spring.aio.utils.openai.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static africa.za.atech.spring.aio.utils.HelperTools.wrapVar;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    @Value("${atech.app.openai.api.assistant.response.poll-max-retry}")
    private int pollMaxRetry;
    @Value("${atech.app.openai.api.assistant.response.poll-sleep-duration}")
    private int pollSleepTime;

    private final AssistantService assistantService;
    private final RepoChats repoChats;
    private final RepoChatsMeta repoChatsMeta;
    private final RepoChatsHistory repoChatsHistory;

    public List<ChatListDTO> getUserChatList(String username) {
        List<Chats> chats = repoChats.findUsersActiveChats(username);
        List<ChatListDTO> list = new ArrayList<>(chats.size());
        for (Chats c : chats) {
            list.add(new ChatListDTO().build(c.getMaskedId(), username, c.getCreatedDateTime(), c.getDescription()));
        }
        Collections.reverse(list);
        return list;
    }

    /**
     * @return ChatListDTO in object if successful
     */
    public OutputTool getUserChatToRename(String username, String chatId) {
        Optional<Chats> chats = repoChats.findByUserChatAndMaskedId(username, chatId);
        ChatListDTO dao = new ChatListDTO().build(chats.get().getMaskedId(), username, chats.get().getCreatedDateTime(), chats.get().getDescription());
        return new OutputTool().build(OutputTool.Result.SUCCESS, "", dao);
    }

    public ChatResponseDTO getStoredChat(String username, String chatId) {
        Optional<Chats> chatRecord = repoChats.findByUserChatAndMaskedId(username, chatId);
        ChatsHistory localHistory = repoChatsHistory.findByChatsId(chatRecord.get().getId()).get();

        JsonArray jsonArray = HelperTools.getJsonArray(localHistory.getHistory());

        List<ChatHistoryDTO> chatHistory = new ArrayList<>();
        for (JsonElement m : jsonArray) {
            chatHistory.add(new ChatHistoryDTO().buildForResponse(m));
        }

        ChatResponseDTO chatResponseDTO = new ChatResponseDTO();
        chatResponseDTO.setChatId(chatRecord.get().getMaskedId());
        chatResponseDTO.setResponseDatetime(LocalDateTime.now());
        chatResponseDTO.setChatHistoryDTOS(chatHistory);
        chatResponseDTO.setLastMessage(chatRecord.get().getOpenaiLastMessageId());

        return chatResponseDTO;
    }

    public OutputTool renameUserChat(String username, ChatListDTO chatListDTO) {
        Optional<Chats> chat = repoChats.findByUserChatAndMaskedId(username, chatListDTO.getChatId());
        chat.get().setDescription(chatListDTO.getDescription().trim());
        repoChats.save(chat.get());
        return new OutputTool().build(OutputTool.Result.SUCCESS, "Chat renamed successfully", null);
    }

    public OutputTool deleteUserChat(String username, String chatId) {
        Optional<Chats> chat = repoChats.findByUserChatAndMaskedId(username, chatId);
        if (chat.isEmpty()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Unable to find chat with id: " + HelperTools.wrapVar(chatId), null);
        }
        Optional<ChatsMeta> chatsMeta = repoChatsMeta.findByChatsId(chat.get().getId());
        Optional<ChatsHistory> chatsHistory = repoChatsHistory.findByChatsId(chat.get().getId());

        repoChats.delete(chat.get());
        chatsMeta.ifPresent(repoChatsMeta::delete);
        chatsHistory.ifPresent(repoChatsHistory::delete);

        return new OutputTool().build(OutputTool.Result.SUCCESS, "Chat deleted successfully.", null);
    }

    public OutputTool deleteAllUsersChats(String username) {
        List<Chats> chat = repoChats.findAllByCreatedBy(username);
        List<Long> chatIds = new ArrayList<>();
        List<Long> historyIds = new ArrayList<>();
        List<Long> metaIds = new ArrayList<>();

        for (Chats c : chat) {
            chatIds.add(c.getId());
            historyIds.add(repoChatsHistory.findByChatsId(c.getId()).get().getId());
            metaIds.add(repoChatsMeta.findByChatsId(c.getId()).get().getId());
        }

        repoChatsMeta.deleteByIdIn(metaIds);
        repoChatsHistory.deleteByIdIn(historyIds);
        repoChats.deleteAllByIdIn(chatIds);
        return new OutputTool().build(OutputTool.Result.SUCCESS, "User chat list deleted successfully.", null);
    }

    public OutputTool processQuestion(String REQUEST_REFERENCE, ChatRequestDTO chatRequestDTO, boolean isNewChat) throws JsonProcessingException, InterruptedException {

        Assistants assistant = (Assistants) assistantService.getAssistant(chatRequestDTO.getAssistant()).getObject();

        LocalDateTime requestTime = LocalDateTime.now();

        String THREAD_ID;
        String RUN_ID;
        String POLLING_STATUS = "";

        // Step 1: create or use existing thread
        if (isNewChat) {
            // New Chat
            // Step 1: Create thread with openai
            log.info("{} - STEP 1 [NEW]: Create thread with openai", REQUEST_REFERENCE);
            Response step1Response = OpenAiClient.createThread(REQUEST_REFERENCE, assistant);
            if (step1Response.getStatusCode() != 200) {
                return new OutputTool().build(OutputTool.Result.EXCEPTION, "Unable to create a new thread", null);
            }
            THREAD_ID = step1Response.getBody().jsonPath().getString("id");
        } else {
            // Existing chat
            // Step 1: Create thread with openai
            Optional<Chats> existingChatLookup = repoChats.findByMaskedId(chatRequestDTO.getChatId());
            if (existingChatLookup.isEmpty()) {
                return new OutputTool().build(OutputTool.Result.EXCEPTION, "Unable to find existing thread for chat id: " + wrapVar(chatRequestDTO.getChatId()), null);
            }
            THREAD_ID = existingChatLookup.get().getOpenaiThreadId();
            REQUEST_REFERENCE = existingChatLookup.get().getMaskedId();
            log.info("{} - STEP 1 [EXISTING]: Existing openai thread identified", REQUEST_REFERENCE);
        }


        // Step 2: Create message using the thread_id
        log.info("{} - STEP 2: Create message using thread_id: {}", REQUEST_REFERENCE, THREAD_ID);
        Response step2Response = OpenAiClient.addMessage(REQUEST_REFERENCE, THREAD_ID, assistant, Role.USER, chatRequestDTO.getQuestion());
        if (step2Response.getStatusCode() != 200) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Unable to create a message using thread_id: " + wrapVar(THREAD_ID), null);
        }

        // Step 3: Post run using the thread_id
        log.info("{} - STEP 3: Post run using the thread_id: {}", REQUEST_REFERENCE, THREAD_ID);
        Response step3Response = OpenAiClient.runThread(REQUEST_REFERENCE, THREAD_ID, assistant, "");
        if (step3Response.getStatusCode() != 200) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Unable to create a run using thread_id: " + wrapVar(THREAD_ID), null);
        }
        RUN_ID = step3Response.getBody().jsonPath().getString("id");

        // Step 4: Poll to check if answer has been received
        int iterations = 0;
        while (!POLLING_STATUS.equals("completed") && iterations <= pollMaxRetry) {
            Response step4PollingResponse = OpenAiClient.getRunStatus(REQUEST_REFERENCE, THREAD_ID, RUN_ID, assistant);
            if (step4PollingResponse.getStatusCode() != 200) {
                return new OutputTool().build(OutputTool.Result.EXCEPTION,
                        "Failure in polling for answer on thread: "
                                + wrapVar(THREAD_ID) + " and run id: " + wrapVar(RUN_ID), null);
            }
            POLLING_STATUS = step4PollingResponse.getBody().jsonPath().getString("status");
            log.info("{} - STEP 4: Polling [{}/{}] with current status: {}", REQUEST_REFERENCE, iterations, pollMaxRetry, POLLING_STATUS.toUpperCase());

            iterations++;
            Thread.sleep(pollSleepTime);
        }

        if (!POLLING_STATUS.equals("completed")) {
            return new OutputTool().build(OutputTool.Result.TIMEOUT, "Max retries reached while polling for answer on thread " +
                    wrapVar(THREAD_ID) + "and run id " + wrapVar(RUN_ID), null);
        }

        // Step 5: Getting messages and answer to populate to the response object
        log.info("{} - STEP 5: Getting messages and answer to populate to the response object", REQUEST_REFERENCE);
        Response finalResponse = OpenAiClient.getMessages(REQUEST_REFERENCE, THREAD_ID, assistant);

        String ANSWER = finalResponse.getBody().jsonPath().getString("data[0].content[0].text.value");
        String FIRST_MESSAGE_ID = finalResponse.getBody().jsonPath().getString("first_id");
        String LAST_MESSAGE_ID = finalResponse.getBody().jsonPath().getString("last_id");

        JsonArray messageArray = HelperTools.getJsonArray(finalResponse.getBody().asString(), "data");
        // Build response and local history
        List<ChatHistoryDTO> chatHistory = new ArrayList<>();
        List<ChatHistoryDTO> responseChatHistory = new ArrayList<>();
        for (JsonElement m : messageArray) {
            // Build history for database in plain text
            ChatHistoryDTO historyDAO = new ChatHistoryDTO().build(chatRequestDTO.getUsername(), REQUEST_REFERENCE, m.toString());
            chatHistory.add(historyDAO);
            // Build history for response with html formatting
            responseChatHistory.add(new ChatHistoryDTO().buildForResponse(historyDAO));
        }

        // Build local history
        String LOCAL_HISTORY = new ObjectMapper().writeValueAsString(chatHistory);

        // Local storage of chat info
        Chats chatRecord;
        ChatsMeta chatsMetaRecord;
        ChatsHistory chatsHistoryRecord;
        if (isNewChat) {
            chatRecord = new Chats()
                    .buildInsert(requestTime, REQUEST_REFERENCE, chatRequestDTO.getUsername(), chatRequestDTO.getDescription(),
                            assistant.getOpenaiAssistantId(), THREAD_ID, FIRST_MESSAGE_ID, LAST_MESSAGE_ID);
            repoChats.save(chatRecord);
            repoChatsMeta.save(new ChatsMeta().buildInsert(chatRecord.getId(), finalResponse.getBody().asString()));
            repoChatsHistory.save(new ChatsHistory().buildInsert(chatRecord.getId(), LOCAL_HISTORY));
        } else {
            chatRecord = repoChats.findByMaskedId(chatRequestDTO.getChatId()).get();
            chatRecord.setUpdateDatetime(requestTime);
            chatRecord.setDescription(chatRequestDTO.getDescription());
            chatRecord.setOpenaiLastMessageId(LAST_MESSAGE_ID);

            chatsMetaRecord = repoChatsMeta.findByChatsId(chatRecord.getId()).get();
            chatsMetaRecord.setOpenaiMessageCollectionObject(finalResponse.getBody().asString());

            chatsHistoryRecord = repoChatsHistory.findByChatsId(chatRecord.getId()).get();
            chatsHistoryRecord.setHistory(LOCAL_HISTORY);

            repoChats.save(chatRecord);
            repoChatsMeta.save(chatsMetaRecord);
            repoChatsHistory.save(chatsHistoryRecord);
        }

        // Build response
        ChatResponseDTO chatResponseDTO = new ChatResponseDTO();
        chatResponseDTO.setChatId(REQUEST_REFERENCE);
        chatResponseDTO.setResponseDatetime(requestTime);
        // Set answer formatted
        chatResponseDTO.setAnswer(HelperTools.toHtml(chatHistory.get(0).getText()));
        chatResponseDTO.setChatHistoryDTOS(responseChatHistory);
        chatResponseDTO.setLastMessage(LAST_MESSAGE_ID);

        return new OutputTool().build(OutputTool.Result.SUCCESS, "", chatResponseDTO);
    }

}
