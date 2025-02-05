package africa.za.atech.spring.aio.functions.users.dto;

import lombok.Data;

@Data
public class BulkRegistrationDTO {

    private String username;
    private String name;
    private String surname;
    private String emailAddress;
    private String assistantName;
    private boolean assistantNameValidated;
    private String whitelistResult;
    private String registrationResult;

    public BulkRegistrationDTO build(String[] sa) {
        this.username = sa[0].trim().toLowerCase();
        this.name = sa[1].trim();
        this.surname = sa[2].trim();
        this.emailAddress = sa[3].trim();
        this.assistantName = sa[4].trim();
        return this;
    }
}
