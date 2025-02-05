package africa.za.atech.spring.aio.functions.assistant.database.repo;

import africa.za.atech.spring.aio.functions.assistant.database.model.Assistants;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface RepoAssistants extends ListCrudRepository<Assistants, Long> {

    Optional<Assistants> findById(long id);

    Optional<Assistants> findByUniqueName(String name);

    List<Assistants> findByDisabledIsFalse();

}
