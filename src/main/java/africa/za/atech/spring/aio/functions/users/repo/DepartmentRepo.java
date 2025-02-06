package africa.za.atech.spring.aio.functions.users.repo;

import africa.za.atech.spring.aio.functions.users.model.Department;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepo extends ListCrudRepository<Department, Long> {

    List<Department> findAllByOrganisationId(Long organisationId);

    Optional<Department> findByMaskedId(String maskedId);

    @Transactional
    void deleteAllByMaskedId(String maskedId);
}
