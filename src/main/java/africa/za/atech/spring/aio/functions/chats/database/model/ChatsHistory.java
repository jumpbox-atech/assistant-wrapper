package africa.za.atech.spring.aio.functions.chats.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Transactional
@Table(schema = "public", name = "chats_history")
public class ChatsHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long id;

    @JsonIgnore
    @Column(name = "chats_id")
    private long chatsId;

    @Column(name = "history")
    private String history;

    public ChatsHistory buildInsert(long chatsId, String history) {
        this.chatsId = chatsId;
        this.history = history;
        return this;
    }
}

