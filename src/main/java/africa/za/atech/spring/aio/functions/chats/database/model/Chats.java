package africa.za.atech.spring.aio.functions.chats.database.model;

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
@Table(schema = "public", name = "chats")
public class Chats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "created_datetime")
    private LocalDateTime createdDateTime;

    @Column(name = "masked_id")
    private String maskedId;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "description")
    private String description;

    @Column(name = "update_datetime")
    private LocalDateTime updateDatetime;

    @Column(name = "openai_assistant_id")
    private String openaiAssistantId;

    @Column(name = "openai_thread_id")
    private String openaiThreadId;

    @Column(name = "openai_first_message_id")
    private String openaiFirstMessageId;

    @Column(name = "openai_last_message_id")
    private String openaiLastMessageId;

    @Column(name = "purge")
    private boolean purge;

    public Chats buildInsert(
            LocalDateTime createdDateTime,
            String maskedId,
            String createdBy,
            String description,
            String openaiAssistantId,
            String openaiThreadId,
            String openaiFirstMessageId,
            String openaiLastMessageId
    ) {
        this.createdDateTime = createdDateTime;
        this.maskedId = maskedId;
        this.createdBy = createdBy;
        this.description = description;
        this.updateDatetime = createdDateTime;
        this.openaiAssistantId = openaiAssistantId;
        this.openaiThreadId = openaiThreadId;
        this.openaiFirstMessageId = openaiFirstMessageId;
        this.openaiLastMessageId = openaiLastMessageId;
        this.purge = false;
        return this;
    }
}

