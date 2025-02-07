package africa.za.atech.spring.aio.functions.assistant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Llm {

    OPEN_AI("OpenAi");

    private final String value;
}
