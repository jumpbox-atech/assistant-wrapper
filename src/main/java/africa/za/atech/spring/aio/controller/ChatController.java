package africa.za.atech.spring.aio.controller;

import africa.za.atech.spring.aio.exceptions.GenericException;
import africa.za.atech.spring.aio.functions.chats.ChatService;
import africa.za.atech.spring.aio.functions.chats.dto.ChatHistoryDTO;
import africa.za.atech.spring.aio.functions.chats.dto.ChatListDTO;
import africa.za.atech.spring.aio.functions.chats.dto.ChatRequestDTO;
import africa.za.atech.spring.aio.functions.chats.dto.ChatResponseDTO;
import africa.za.atech.spring.aio.functions.users.UsersService;
import africa.za.atech.spring.aio.functions.users.dto.UserProfileDTO;
import africa.za.atech.spring.aio.utils.Alert;
import africa.za.atech.spring.aio.utils.HelperTools;
import africa.za.atech.spring.aio.utils.OutputTool;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final UsersService service;
    private final ChatService chatService;
    private List<Alert> alertList;

    @GetMapping("/chat")
    public String showChatPage(
            Model model,
            RedirectAttributes redirectAttributes) throws GenericException {
        alertList = new ArrayList<>(1);

        // Mandatory models
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfileDTO userProfileDTO = service.getProfile(loggedInUser);
        model.addAttribute("profileObject", userProfileDTO);

        if (userProfileDTO.getAssistantId().equalsIgnoreCase("null")) {
            alertList.add(new Alert().build(Alert.AlertType.WARNING, "No assistants are assigned. Please contact support."));
            redirectAttributes.addFlashAttribute("alertList", alertList);
            return "redirect:/home?chat=warn";
        }
        // Add users list of chats
        List<ChatHistoryDTO> chatHistory = new ArrayList<>();

        // Add new object with the users assigned bot
        ChatRequestDTO formObject = new ChatRequestDTO();
        formObject.setUsername(loggedInUser);
        formObject.setAssistant(userProfileDTO.getAssistantId());

        model.addAttribute("greeting",
                HelperTools.getString("static/html/chat-init.html")
                        .replace("~NAME~", userProfileDTO.getName())
                        .replace("~ASSISTANT~", userProfileDTO.getAssistantId()));
        model.addAttribute("chatHistory", chatHistory);
        model.addAttribute("formObject", formObject);

        return "chat/chat";
    }

    @GetMapping("/chat/list")
    public String chatManagement(Model model) {

        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ChatListDTO> chatListDTOS = chatService.getUserChatList("", loggedInUser);
        model.addAttribute("recordList", chatListDTOS);
        return "chat/chat_list";
    }

    @GetMapping("/chat/rename")
    public String getChatDetails(
            Model model,
            @RequestParam(value = "id") String chatId) {

        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        ChatListDTO chatListDTO = (ChatListDTO) chatService.getUserChatToRename("", loggedInUser, chatId).getObject();
        model.addAttribute("formObject", chatListDTO);
        return "chat/chat_rename";
    }

    @PostMapping("/chat/rename")
    public String renameChat(
            @Validated @ModelAttribute("formObject") ChatListDTO formObject,
            RedirectAttributes redirectAttributes) {
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        alertList = new ArrayList<>(1);

        OutputTool outputTool = chatService.renameUserChat("", loggedInUser, formObject);
        alertList.add(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment()));
        redirectAttributes.addFlashAttribute("alertList", alertList);
        return "redirect:/chat/list?updated=true";
    }

    @GetMapping("/chat/delete/{chatId}")
    public String deleteChat(
            @PathVariable(name = "chatId") String maskedId,
            RedirectAttributes redirectAttributes) {
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        alertList = new ArrayList<>(1);

        OutputTool outputTool = chatService.deleteUserChat("", loggedInUser, maskedId);
        if (outputTool.getResult().equals(OutputTool.Result.EXCEPTION)) {
            alertList.add(new Alert().build(Alert.AlertType.DANGER, outputTool.getComment()));
            redirectAttributes.addFlashAttribute("alertList", alertList);
            return "redirect:/chat/list?deleted=no";
        }
        alertList.add(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment()));
        redirectAttributes.addFlashAttribute("alertList", alertList);
        return "redirect:/chat/list?deleted=true";
    }

    @PostMapping("/chat/process")
    public String processQuestion(@Validated @ModelAttribute("question") ChatRequestDTO form) throws InterruptedException, JsonProcessingException {

        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String requestReference = loggedInUser + "_" + UUID.randomUUID().toString().replaceAll("-", "");

        alertList = new ArrayList<>(1);
        boolean isNewChat = form.getChatId() == null || form.getChatId().isBlank();

        if (form.getDescription().isBlank()) {
            int sizeOfQuestion = form.getQuestion().length();
            if (sizeOfQuestion > 21) {
                form.setDescription(form.getQuestion().substring(0, 20) + " ...");
            } else {
                form.setDescription(form.getQuestion() + " ...");
            }
        }

        // TODO: Manage failures
        OutputTool outputTool = chatService.processQuestion(requestReference, form, isNewChat);
        if (outputTool.getResult().equals(OutputTool.Result.TIMEOUT)) {
            // what to do on timeout
            alertList.add(new Alert().build(Alert.AlertType.DANGER, "Communication timeout. Please retry."));
        }
        if (outputTool.getResult().equals((OutputTool.Result.EXCEPTION))) {
            // what to do if it fails
            alertList.add(new Alert().build(Alert.AlertType.DANGER, "Communication error. Please retry."));
        }

        ChatResponseDTO response = (ChatResponseDTO) outputTool.getObject();
        return "redirect:/chat/continue?id=" + response.getChatId();
    }

    @GetMapping("/chat/continue")
    public String getChatToContinue(
            Model model,
            @RequestParam(value = "id") String chatId,
            @RequestParam(value = "type", required = false) boolean typeWrite) throws GenericException {
        // Mandatory models
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfileDTO userProfileDTO = service.getProfile(loggedInUser);
        model.addAttribute("profileObject", userProfileDTO);
        alertList = new ArrayList<>(1);

        // Add users list of chats
        ChatResponseDTO storedResponse = chatService.getStoredChat("", loggedInUser, chatId);
        Collections.reverse(storedResponse.getChatHistoryDTOS());

        // Add new object with the users assigned bot and chatId
        ChatRequestDTO formObject = new ChatRequestDTO();
        formObject.setUsername(loggedInUser);
        formObject.setName(userProfileDTO.getName());
        formObject.setAssistant(userProfileDTO.getAssistantId());
        formObject.setChatId(storedResponse.getChatId());
        formObject.setQuestion(null);

        // Not being used
        String answer;
        if (typeWrite) {
            // Provide answer field for typing functionality
            answer = storedResponse.getChatHistoryDTOS().get(0).getText();
            model.addAttribute("answer", answer);
            // Removing answer from history
            storedResponse.getChatHistoryDTOS().remove(0);
        }

        model.addAttribute("chatHistory", storedResponse.getChatHistoryDTOS());
        model.addAttribute("formObject", formObject);
        return "chat/chat";
    }

}
