package africa.za.atech.spring.aio.utils.openai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RunThreadMessage {

    @JsonProperty("assistant_id")
    private String assistantId;
    @JsonProperty("additional_instructions")
    private String additionalInstructions;

    public RunThreadMessage build(String assistantId, String additionalInstruction) {
        RunThreadMessage payload = new RunThreadMessage();
        payload.setAssistantId(assistantId);
        payload.setAdditionalInstructions(additionalInstruction);
        return payload;
    }

}
