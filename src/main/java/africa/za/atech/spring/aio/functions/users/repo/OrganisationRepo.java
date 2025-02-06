package africa.za.atech.spring.aio.functions.users.repo;

import africa.za.atech.spring.aio.functions.users.model.Organisation;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface OrganisationRepo extends ListCrudRepository<Organisation, Long> {

    Optional<Organisation> findAllByMaskedId(String maskedId);

    Optional<Organisation> findByNameIgnoreCase(String name);

    Optional<Organisation> findByName(String name);
}
