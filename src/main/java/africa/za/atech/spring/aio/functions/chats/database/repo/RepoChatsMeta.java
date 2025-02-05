package africa.za.atech.spring.aio.functions.chats.database.repo;


import africa.za.atech.spring.aio.functions.chats.database.model.ChatsMeta;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface RepoChatsMeta extends ListCrudRepository<ChatsMeta, Long> {

    Optional<ChatsMeta> findByChatsId(long chatsId);

    @Transactional
    void deleteByIdIn(List<Long> ids);

}
