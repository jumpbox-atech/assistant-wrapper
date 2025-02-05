package africa.za.atech.spring.aio.functions.assistant.dto;

import lombok.Data;

@Data
public class AssistantDTO {

    private long id;
    private String username;
    private String name;
    private String uniqueName;
    private String description;
    private String additionalInstructions;
    private String organisationId;
    private String assistantId;
    private String apiKey;
    private boolean disabled;

}
