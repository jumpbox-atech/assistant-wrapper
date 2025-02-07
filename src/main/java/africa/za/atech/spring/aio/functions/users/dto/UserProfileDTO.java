package africa.za.atech.spring.aio.functions.users.dto;

import africa.za.atech.spring.aio.functions.users.model.Users;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileDTO {

    private long id;
    private LocalDateTime createdDateTime;
    private String username;
    private String maskedId;
    private String name;
    private String surname;
    private String emailAddress;
    private String password;
    private boolean disabled;
    private String roles;
    private String newPassword;
    private String confirmPassword;
    private String assistantId;
    private String organisationMaskedId;
    private String departmentMaskedId;

    public UserProfileDTO build(String organisationMaskedId, Users dto) {
        this.id = dto.getId();
        this.createdDateTime = dto.getCreatedDateTime();
        this.username = dto.getUsername();
        this.maskedId = dto.getMaskedId();
        this.name = dto.getName();
        this.surname = dto.getSurname();
        this.emailAddress = dto.getEmailAddress();
        this.password = "";
        this.disabled = dto.isDisabled();
        this.roles = dto.getRole();
        this.newPassword = "";
        this.confirmPassword = "";
        this.assistantId = dto.getCustomPropertyA();

        this.organisationMaskedId = organisationMaskedId;
        return this;
    }
}
