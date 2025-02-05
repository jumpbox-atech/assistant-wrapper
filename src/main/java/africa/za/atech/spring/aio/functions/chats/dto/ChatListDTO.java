package africa.za.atech.spring.aio.functions.chats.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ChatListDTO {

    private String chatId;
    private String username;
    @DateTimeFormat(pattern = "dd MMM yyyy HH:mm")
    private LocalDateTime date;
    private String description;

    public ChatListDTO build(String chatId, String username, LocalDateTime date, String description) {
        this.chatId = chatId;
        this.username = username;
        this.date = date;
        this.description = description;
        return this;
    }

}
