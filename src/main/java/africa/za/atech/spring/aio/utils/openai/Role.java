package africa.za.atech.spring.aio.utils.openai;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant");

    private final String value;

}
