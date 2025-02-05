package africa.za.atech.spring.aio.functions.chats.dto;

import africa.za.atech.spring.aio.utils.HelperTools;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.gson.JsonElement;
import io.restassured.path.json.JsonPath;
import lombok.Data;

@Data
@JsonRootName("chat")
public class ChatHistoryDTO {

    @JsonProperty("chat_id")
    private String chatId;
    @JsonProperty("message_id")
    private String messageId;
    private String username;
    @JsonProperty("assistant_id")
    private String assistantId;
    @JsonProperty("thread_id")
    private String threadId;
    @JsonProperty("run_id")
    private String runId;
    private String role;
    private String text;

    public ChatHistoryDTO build(String username, String chatId, String message) {
        this.chatId = chatId;
        this.messageId = JsonPath.with(message).getString("id");
        this.username = username;
        this.assistantId = JsonPath.with(message).getString("assistant_id");
        this.threadId = JsonPath.with(message).getString("thread_id");
        this.runId = JsonPath.with(message).getString("run_id");
        this.role = JsonPath.with(message).getString("role");
        this.text = JsonPath.with(message).getString("content[0].text.value");
        return this;
    }

    public ChatHistoryDTO buildForResponse(ChatHistoryDTO dao) {
        this.chatId = dao.getChatId();
        this.messageId = dao.getMessageId();
        this.username = dao.getUsername();
        this.assistantId = dao.getAssistantId();
        this.threadId = dao.getThreadId();
        this.runId = dao.getRunId();
        this.role = dao.getRole();
        this.text = HelperTools.toHtml(dao.getText());
        return this;
    }

    public ChatHistoryDTO buildForResponse(JsonElement m) {
        this.chatId = (JsonPath.with(m.toString()).getString("chat_id"));
        this.messageId = (JsonPath.with(m.toString()).getString("message_id"));
        this.username = (JsonPath.with(m.toString()).getString("username"));
        this.assistantId = (JsonPath.with(m.toString()).getString("assistant_id"));
        this.threadId = (JsonPath.with(m.toString()).getString("thread_id"));
        this.runId = (JsonPath.with(m.toString()).getString("run_id"));
        this.role = (JsonPath.with(m.toString()).getString("role"));
        this.text = HelperTools.toHtml(JsonPath.with(m.toString()).getString("text"));
        return this;
    }

}
