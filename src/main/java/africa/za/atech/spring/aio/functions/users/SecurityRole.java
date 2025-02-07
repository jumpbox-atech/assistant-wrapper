package africa.za.atech.spring.aio.functions.users;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SecurityRole {

    USER("USER"),
    MANAGER("MANAGER"),
    ORG_ADMIN("ORG_ADMIN"),
    ADMIN("ADMIN");

    private final String value;
}
