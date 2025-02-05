package africa.za.atech.spring.aio.functions.users.repo;

import africa.za.atech.spring.aio.functions.users.model.Users;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface UsersRepo extends ListCrudRepository<Users, Long> {

    Optional<Users> findByUsernameIgnoreCase(String username);

    Optional<Users> findAllByMaskedId(String maskedId);

}
