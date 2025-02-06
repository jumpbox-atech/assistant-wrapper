package africa.za.atech.spring.aio.functions.users.model;

import africa.za.atech.spring.aio.functions.users.dto.DepartmentDTO;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Transactional
@Table(schema = "public", name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "masked_id")
    private String maskedId;

    @Column(name = "organisation_id")
    private Long organisationId;

    @Column(name = "created_datetime")
    private LocalDateTime createdDateTime;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "name")
    private String name;

    @Column(name = "disabled")
    private boolean disabled;

    @Column(name = "updated_datetime")
    private LocalDateTime updateDatetime;

    @Column(name = "updated_by")
    private String updateBy;

    public Department buildInsert(String loggedInUser, Long organisationId, DepartmentDTO departmentDTO) {
        this.maskedId = UUID.randomUUID().toString();
        this.organisationId = organisationId;
        this.createdDateTime = LocalDateTime.now();
        this.createdBy = loggedInUser;
        this.name = departmentDTO.getName();
        this.disabled = false;
        return this;
    }
}

