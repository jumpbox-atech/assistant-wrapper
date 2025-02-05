package africa.za.atech.spring.aio.functions.chats.database.model;

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
@Table(schema = "public", name = "chats_meta")
public class ChatsMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "chats_id")
    private long chatsId;

    @Column(name = "openai_message_collection_object")
    private String openaiMessageCollectionObject;

    public ChatsMeta buildInsert(long chatsId, String messageCollection) {
        this.chatsId = chatsId;
        this.openaiMessageCollectionObject = messageCollection;
        return this;
    }
}

