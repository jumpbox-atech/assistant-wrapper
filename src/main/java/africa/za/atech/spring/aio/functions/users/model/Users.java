package africa.za.atech.spring.aio.functions.users.model;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(schema = "public", name = "users")
@Transactional
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uid")
    private String uid;

    @Column(name = "organisation_uid")
    private String organisationUid;

    @Column(name = "created_datetime")
    private LocalDateTime createdDateTime;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "username")
    private String username;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "role")
    private String role;

    @Column(name = "password")
    private String password;

    @Column(name = "disabled")
    private boolean disabled;

    @Column(name = "mfa_disabled")
    private boolean mfaDisabled;

    @Column(name = "mfa_secret")
    private String mfaSecret;

    @Column(name = "assistants_uuids")
    private String assistantsUuids;

    @Column(name = "updated_datetime")
    private LocalDateTime updatedDateTime;

    @Column(name = "updated_by")
    private String updated_by;

    public Users buildInsert(String organisationUuid,
                             String createdBy,
                             String username,
                             String name,
                             String surname,
                             String emailAddress,
                             String role,
                             String password,
                             List<String> assistantsUuids) {
        this.uid = UUID.randomUUID().toString();
        this.organisationUid = organisationUuid;
        this.createdDateTime = LocalDateTime.now();
        this.createdBy = createdBy;
        this.username = username.toLowerCase();
        this.name = name;
        this.surname = surname;
        this.emailAddress = emailAddress;
        this.role = role;
        this.password = password;
        this.disabled = false;
        this.mfaDisabled = true;
        this.mfaSecret = "";
        this.assistantsUuids = Arrays.toString(assistantsUuids.toArray());
        return this;
    }
}

