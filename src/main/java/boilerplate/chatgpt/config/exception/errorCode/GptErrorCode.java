package boilerplate.chatgpt.config.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GptErrorCode implements ErrorCode{


    GPT_FAIL_INTERRUPTED(HttpStatus.INTERNAL_SERVER_ERROR, "GPT processing was interrupted"),
    GPT_FAIL_REQUEST(HttpStatus.INTERNAL_SERVER_ERROR, "GPT request failed"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
