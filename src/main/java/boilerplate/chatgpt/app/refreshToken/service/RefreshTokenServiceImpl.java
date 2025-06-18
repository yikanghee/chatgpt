package boilerplate.chatgpt.app.refreshToken.service;

import boilerplate.chatgpt.app.refreshToken.dto.TokenDto;
import boilerplate.chatgpt.app.refreshToken.entity.RefreshToken;
import boilerplate.chatgpt.app.refreshToken.repository.RefreshTokenRedisRepository;
import boilerplate.chatgpt.config.jwt.JwtService;
import boilerplate.chatgpt.config.exception.AccountException;
import boilerplate.chatgpt.config.exception.errorCode.UserErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final JwtService jwtService;

    @Override
    public void refreshToken(HttpServletResponse response,String accessToken, String refreshToken) {
        Long userId = jwtService.getUserId(accessToken).getUserId();
        log.info("userId: {}", userId);

        RefreshToken token = refreshTokenRedisRepository.findById(String.valueOf(userId))
                .orElseThrow(() -> new AccountException(
                        UserErrorCode.REFRESH_TOKEN_NOT_FOUND
                ));

        if (!token.getToken().equals(refreshToken)) {
            throw new AccountException(UserErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        String newAccessToken = jwtService.createAccessToken(userId);
        String newRefreshToken = jwtService.createRefreshToken(userId, false, newAccessToken);

        saveCookie(newAccessToken, response, "X-AUTH-TOKEN");
        saveCookie(newRefreshToken, response, "REFRESH-TOKEN");
    }
    // 쿠키 저장
    private void saveCookie(String token, HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS를 사용하는 경우에만 true로 설정
        cookie.setPath("/");

        response.addCookie(cookie);
    }
}
