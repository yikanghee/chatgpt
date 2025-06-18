package boilerplate.chatgpt.config.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode{

    INVAILD_USER(HttpStatus.BAD_REQUEST, "Invalid user"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "User already exists"),
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "Password mismatch"),

    // Refresh
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Refresh token not found"),


    // security
    OAUTH2_NOT_FOUND(HttpStatus.NOT_FOUND, "OAuth2 not found"),
    OAUTH2_NOT_MATCH(HttpStatus.UNAUTHORIZED, "OAuth2 not match"),
    OAUTH2_NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "OAuth2 Login Again"),
    OAUTH2_NOT_SUPPORT(HttpStatus.UNAUTHORIZED, "OAuth2 not support"),

    ;
    private final HttpStatus httpStatus;
    private final String message;
}
