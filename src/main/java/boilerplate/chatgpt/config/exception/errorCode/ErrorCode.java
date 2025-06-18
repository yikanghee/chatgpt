package boilerplate.chatgpt.config.exception.errorCode;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus getHttpStatus();
    String name();
    String getMessage();
}
