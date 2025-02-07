package africa.za.atech.spring.aio.functions.assistant.database.model;

import africa.za.atech.spring.aio.functions.assistant.Llm;
import africa.za.atech.spring.aio.functions.assistant.dto.AssistantDTO;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Transactional
@Table(schema = "public", name = "assistants")
public class Assistants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "masked_id")
    private String maskedId;

    @Column(name = "organisation_id")
    private long organisationId;

    @Column(name = "created_datetime")
    private LocalDateTime createdDateTime;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "external_type")
    private String externalType;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "additional_instructions")
    private String additionalInstructions;

    @Column(name = "external_organisation_id")
    private String externalOrganisationId;

    @Column(name = "external_assistant_id")
    private String externalAssistantId;

    @Column(name = "external_api_key")
    private String externalApiKey;

    @Column(name = "updated_datetime")
    private LocalDateTime updateDatetime;

    @Column(name = "updated_by")
    private String updateBy;

    public Assistants buildInsert(String loggedInUser, long organisationId, AssistantDTO form) {
        this.maskedId = UUID.randomUUID().toString();
        this.createdDateTime = LocalDateTime.now();
        this.createdBy = loggedInUser;
        this.organisationId = organisationId;
        this.externalOrganisationId = form.getExternalOrganisationId().trim();
        this.externalAssistantId = form.getExternalAssistantId().trim();
        this.externalType = Llm.OPEN_AI.getValue();
        this.name = form.getName().trim();
        this.description = form.getDescription().trim();
        if (!form.getAdditionalInstructions().isEmpty()) {
            this.additionalInstructions = form.getAdditionalInstructions().trim();
        }
        this.externalApiKey = form.getExternalApiKey();
        return this;
    }
}

