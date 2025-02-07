package africa.za.atech.spring.aio.functions.users.model;

import africa.za.atech.spring.aio.functions.users.SecurityRole;
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
@Table(schema = "public", name = "users")
@Transactional
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "created_datetime")
    private LocalDateTime createdDateTime;

    @Column(name = "username")
    private String username;

    @Column(name = "masked_id")
    private String maskedId;

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

    @Column(name = "custom_property_a")
    private String customPropertyA;

    @Column(name = "custom_property_b")
    private String customPropertyB;

    @Column(name = "custom_property_c")
    private String customPropertyC;

    @Column(name = "custom_property_d")
    private String customPropertyD;

    @Column(name = "inserted_by_username")
    private String insertedBy;

    // TODO: Need to set org on insert or set to for an unallocated user
    @Column(name = "organisation_id")
    private long organisationId;

    public Users buildInsert(LocalDateTime createdDateTime,
                             String username,
                             String name,
                             String surname,
                             String emailAddress,
                             SecurityRole role,
                             String password,
                             String customPropertyA,
                             String customPropertyB,
                             String customPropertyC,
                             String customPropertyD,
                             String insertedBy) {
        this.maskedId = UUID.randomUUID().toString();
        this.createdDateTime = createdDateTime;
        this.username = username.toLowerCase();
        this.name = name;
        this.surname = surname;
        this.emailAddress = emailAddress;
        this.role = role.getValue();
        this.password = password;
        this.disabled = false;
        this.mfaDisabled = true;
        this.mfaSecret = "null";
        this.customPropertyA = customPropertyA;
        this.customPropertyB = customPropertyB;
        this.customPropertyC = customPropertyC;
        this.customPropertyD = customPropertyD;
        this.insertedBy = insertedBy;
        this.organisationId = 0L;
        return this;
    }
}

