package africa.za.atech.spring.aio.functions.users.dto;

import lombok.Data;

@Data
public class RegisterDTO {

    private String username;
    private String name;
    private String surname;
    private String emailAddress;

    public RegisterDTO build(BulkRegistrationDTO dao) {
        this.username = dao.getUsername();
        this.name = dao.getName();
        this.surname = dao.getSurname();
        this.emailAddress = dao.getEmailAddress();
        return this;
    }
}
