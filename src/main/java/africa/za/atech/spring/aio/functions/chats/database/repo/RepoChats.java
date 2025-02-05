package africa.za.atech.spring.aio.functions.chats.database.repo;


import africa.za.atech.spring.aio.functions.chats.database.model.Chats;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface RepoChats extends ListCrudRepository<Chats, Long> {

    @Query(value = "select * from chats where created_by = ?1 and purge = false", nativeQuery = true)
    List<Chats> findUsersActiveChats(String username);

    @Query(value = "select * from chats where created_by = ?1 and masked_id = ?2 and purge = false", nativeQuery = true)
    Optional<Chats> findByUserChatAndMaskedId(String username, String maskedId);

    Optional<Chats> findByMaskedId(String maskedId);

}
