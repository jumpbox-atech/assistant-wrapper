package africa.za.atech.spring.aio.functions.users.dto;

import jakarta.annotation.Nonnull;
import lombok.Data;

@Data
public class WhitelistRegDTO {

    private String username;
    private String name;
    private String surname;
    private String emailAddress;
    private String customPropertyA;
    private String customPropertyB;
    private String customPropertyC;
    private String customPropertyD;

    public WhitelistRegDTO buildInsert(String username,
                                       String name,
                                       String surname,
                                       String emailAddress,
                                       @Nonnull String customPropertyA,
                                       @Nonnull String customPropertyB,
                                       @Nonnull String customPropertyC,
                                       @Nonnull String customPropertyD) {

        this.username = username;
        this.name = name;
        this.surname = surname;
        this.emailAddress = emailAddress;
        this.customPropertyA = customPropertyA;
        this.customPropertyB = customPropertyB;
        this.customPropertyC = customPropertyC;
        this.customPropertyD = customPropertyD;
        return this;
    }
}

