package boilerplate.chatgpt.app.refreshToken.service;


import boilerplate.chatgpt.app.refreshToken.dto.TokenDto;
import jakarta.servlet.http.HttpServletResponse;

public interface RefreshTokenService {

    void refreshToken(HttpServletResponse response, String accessToken, String refreshToken);


}
