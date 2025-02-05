package africa.za.atech.spring.aio.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@Slf4j
@ResponseStatus(HttpStatus.ACCEPTED)
public class GenericException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public GenericException(String message) {
        super(message);
        log.warn(message);
    }
}
