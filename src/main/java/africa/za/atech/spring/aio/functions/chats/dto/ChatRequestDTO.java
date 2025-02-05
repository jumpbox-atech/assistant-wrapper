package africa.za.atech.spring.aio.functions.chats.dto;

import lombok.Data;

@Data
public class ChatRequestDTO {

    private String chatId;
    private String username;
    private String name;
    private String description;
    private String assistant;
    private String question;

}
