package africa.za.atech.spring.aio.functions.users.model;

import africa.za.atech.spring.aio.functions.users.dto.OrganisationDTO;
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
@Table(schema = "public", name = "organisation")
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uid")
    private String uid;

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


    public Organisation buildInsert(String loggedInUser, OrganisationDTO organisationDTO) {
        this.uid = UUID.randomUUID().toString();
        this.createdDateTime = LocalDateTime.now();
        this.createdBy = loggedInUser;
        this.name = organisationDTO.getName();
        this.disabled = false;
        return this;
    }
}

