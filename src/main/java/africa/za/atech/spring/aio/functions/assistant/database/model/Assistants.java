package africa.za.atech.spring.aio.functions.assistant.database.model;

import africa.za.atech.spring.aio.functions.assistant.dto.AssistantDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

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

    @Column(name = "created_datetime")
    private LocalDateTime createdDateTime;

    @JsonIgnore
    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "openai_organisation_id")
    private String openaiOrganisationId;

    @Column(name = "openai_assistant_id")
    private String openaiAssistantId;

    @Column(name = "name")
    private String name;

    @Column(name = "unique_name")
    private String uniqueName;

    @Column(name = "description")
    private String description;

    @Column(name = "additional_instructions")
    private String additionalInstructions;

    @JsonIgnore
    @Column(name = "api_key")
    private String apiKey;

    @JsonIgnore
    @Column(name = "update_datetime")
    private LocalDateTime updateDatetime;

    @JsonIgnore
    @Column(name = "update_by")
    private String updateBy;

    @JsonIgnore
    @Column(name = "disabled")
    private boolean disabled;

    @JsonIgnore
    @Column(name = "disabled_by")
    private String disabledBy;

    @JsonIgnore
    @Column(name = "disabled_datetime")
    private LocalDateTime disabledDatetime;


    public Assistants buildInsert(String createdBy, LocalDateTime createdDateTime, AssistantDTO dao) {
        this.createdDateTime = createdDateTime;
        this.createdBy = createdBy;
        this.name = dao.getName();
        this.uniqueName = this.name.toLowerCase();
        this.description = dao.getDescription();
        if (dao.getAdditionalInstructions() == null) {
            this.additionalInstructions = "";
        } else {
            this.additionalInstructions = dao.getAdditionalInstructions();
        }
        this.openaiOrganisationId = dao.getOrganisationId();
        this.openaiAssistantId = dao.getAssistantId();
        this.apiKey = dao.getApiKey();
        this.disabled = false;
        return this;
    }
}

