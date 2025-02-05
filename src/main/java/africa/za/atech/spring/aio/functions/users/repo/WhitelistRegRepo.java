package africa.za.atech.spring.aio.functions.users.repo;

import africa.za.atech.spring.aio.functions.users.model.RegistrationWhitelist;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface WhitelistRegRepo extends ListCrudRepository<RegistrationWhitelist, Long> {

    Optional<RegistrationWhitelist> findByUsernameIgnoreCase(String username);

}
