package boilerplate.chatgpt.config.exception;

import boilerplate.chatgpt.config.exception.errorCode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GptException extends RuntimeException{
    private final ErrorCode errorCode;
}
