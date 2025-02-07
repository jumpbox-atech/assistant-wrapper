package africa.za.atech.spring.aio.functions.assistant.database.repo;

import africa.za.atech.spring.aio.functions.assistant.database.model.Assistants;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface RepoAssistants extends ListCrudRepository<Assistants, Long> {

    Optional<Assistants> findByMaskedId(String maskedId);

    Optional<Assistants> findByOrganisationIdAndNameIgnoreCase(long organisationId, String name);

    Optional<Assistants> findByOrganisationIdAndMaskedId(long organisationId, String maskedId);
}
