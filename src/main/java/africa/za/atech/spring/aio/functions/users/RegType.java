package africa.za.atech.spring.aio.functions.users;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RegType {

    PUBLIC("public"),
    PRIVATE("private");

    private final String value;
}
