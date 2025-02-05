package africa.za.atech.spring.aio.functions.chats.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatResponseDTO {

    @Expose
    @JsonProperty("chat_id")
    private String chatId;

    @Expose
    private String answer;

    @Expose
    @JsonProperty("chat_history")
    private List<ChatHistoryDTO> chatHistoryDTOS;

    @Expose
    @JsonProperty("last_message")
    private String lastMessage;

    private LocalDateTime responseDatetime;

}
