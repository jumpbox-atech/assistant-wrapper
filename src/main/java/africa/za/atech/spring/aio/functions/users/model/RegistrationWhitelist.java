package africa.za.atech.spring.aio.functions.users.model;

import africa.za.atech.spring.aio.functions.users.dto.WhitelistRegDTO;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(schema = "public", name = "registration_whitelist")
@Transactional
public class RegistrationWhitelist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "created_datetime")
    private LocalDateTime createdDateTime;

    @Column(name = "username")
    private String username;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "custom_property_a")
    private String customPropertyA;

    @Column(name = "custom_property_b")
    private String customPropertyB;

    @Column(name = "custom_property_c")
    private String customPropertyC;

    @Column(name = "custom_property_d")
    private String customPropertyD;

    @Column(name = "inserted_by_username")
    private String insertedByUsername;

    @Column(name = "registered")
    private boolean registered;

    public RegistrationWhitelist buildInsert(String insertedByUsername, WhitelistRegDTO whitelistRegDTO) {
        this.createdDateTime = LocalDateTime.now();
        this.username = whitelistRegDTO.getUsername();
        this.name = whitelistRegDTO.getName();
        this.surname = whitelistRegDTO.getSurname();
        this.emailAddress = whitelistRegDTO.getEmailAddress();
        this.customPropertyA = whitelistRegDTO.getCustomPropertyA();
        this.customPropertyB = whitelistRegDTO.getCustomPropertyB();
        this.customPropertyC = whitelistRegDTO.getCustomPropertyC();
        this.customPropertyD = whitelistRegDTO.getCustomPropertyD();
        this.insertedByUsername = insertedByUsername;
        this.registered = false;
        return this;
    }
}

