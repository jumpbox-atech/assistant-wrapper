package africa.za.atech.spring.aio.functions.assistant.dto;

import africa.za.atech.spring.aio.functions.assistant.database.model.Assistants;
import africa.za.atech.spring.aio.functions.users.model.Organisation;
import lombok.Data;

@Data
public class AssistantDTO {

    private String uid;
    private String organisationUid;
    private String organisationName;
    private String username;
    private String name;
    private String description;
    private String additionalInstructions;
    private String externalOrganisationId;
    private String externalAssistantId;
    private String externalApiKey;

    public AssistantDTO build(Assistants assistants) {
        this.uid = assistants.getMaskedId();
        this.username = assistants.getCreatedBy();
        this.name = assistants.getName();
        this.description = assistants.getDescription();
        this.additionalInstructions = assistants.getAdditionalInstructions();
        this.externalOrganisationId = assistants.getExternalOrganisationId();
        this.externalAssistantId = assistants.getExternalAssistantId();
        this.externalApiKey = "masked";
        return this;
    }

    public AssistantDTO build(Organisation organisation, Assistants assistants) {
        this.uid = assistants.getMaskedId();
        this.organisationUid = organisation.getUid();
        this.organisationName = organisation.getName();
        this.username = assistants.getCreatedBy();
        this.name = assistants.getName();
        this.description = assistants.getDescription();
        this.additionalInstructions = assistants.getAdditionalInstructions();
        this.externalOrganisationId = assistants.getExternalOrganisationId();
        this.externalAssistantId = assistants.getExternalAssistantId();
        this.externalApiKey = "masked";
        return this;
    }

}
