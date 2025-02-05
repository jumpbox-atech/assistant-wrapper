package africa.za.atech.spring.aio.functions.chats.database.repo;


import africa.za.atech.spring.aio.functions.chats.database.model.ChatsHistory;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface RepoChatsHistory extends ListCrudRepository<ChatsHistory, Long> {

    Optional<ChatsHistory> findByChatsId(long chatsId);

}
