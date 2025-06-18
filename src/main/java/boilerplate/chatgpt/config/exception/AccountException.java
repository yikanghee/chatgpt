package boilerplate.chatgpt.config.exception;

import boilerplate.chatgpt.config.exception.errorCode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AccountException extends RuntimeException {
    private final ErrorCode errorCode;
}
