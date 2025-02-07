package africa.za.atech.spring.aio.functions.users.dto;

import africa.za.atech.spring.aio.functions.users.model.Users;
import lombok.Data;

@Data
public class UserProfileDTO {

    private String uid;
    private String username;
    private String name;
    private String surname;
    private String emailAddress;
    private String password;
    private boolean disabled;
    private String roles;
    private String newPassword;
    private String confirmPassword;
    private String assistantId;
    private String organisationUuid;
    private String organisationName;
    private String departmentMaskedId;

    public UserProfileDTO build(String organisationName, Users record) {
        this.uid = record.getUid();
        this.username = record.getUsername();
        this.name = record.getName();
        this.surname = record.getSurname();
        this.emailAddress = record.getEmailAddress();
        this.password = "";
        this.disabled = record.isDisabled();
        this.roles = record.getRole();
        this.newPassword = "";
        this.confirmPassword = "";
        this.assistantId = record.getAssistantsUuids();
        this.organisationUuid = record.getOrganisationUid();
        this.organisationName = organisationName;
        return this;
    }
}
