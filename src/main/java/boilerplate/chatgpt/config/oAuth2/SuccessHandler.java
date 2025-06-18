package boilerplate.chatgpt.config.oAuth2;

import boilerplate.chatgpt.app.uesr.entity.User;
import boilerplate.chatgpt.app.uesr.repository.UserRepository;
import boilerplate.chatgpt.config.jwt.JwtService;
import boilerplate.chatgpt.config.dto.OAuth2Response;
import boilerplate.chatgpt.config.exception.AccountException;
import boilerplate.chatgpt.config.exception.errorCode.UserErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final OAuthResponseFactory oAuthResponseFactory;

    /**
     * Called when a user has been successfully authenticated.
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     *                       the authentication process.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("oauth2 성공 핸들러 동작");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // OAuth2User의 속성에서 필요한 정보를 가져옵니다.
        String registrationId = getRegistrationId(authentication);

        // OAuth2Response 객체를 생성합니다.
        OAuth2Response oAuth2Response = oAuthResponseFactory.getOAuth2Response(registrationId, oAuth2User.getAttributes());


        String email = oAuth2Response.getEmail();
        log.info("email = {}", email);

        Optional<User> byEmail = userRepository.findByEmail(email);
        byEmail
                .orElseThrow(() -> new AccountException(UserErrorCode.USER_NOT_FOUND));

        handleToken(response, byEmail);
    }

    private static String getRegistrationId(Authentication authentication) {
        String registrationId = null;
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            registrationId = oauthToken.getAuthorizedClientRegistrationId();
            System.out.println("registrationId = " + registrationId);
        }
        return registrationId;
    }

    private void handleToken(HttpServletResponse response, Optional<User> byEmail) {
        Long userId = byEmail.get().getId();

        // 액세스 토큰과 리프레시 토큰을 생성합니다.
        String accessToken = jwtService.createAccessToken(userId);
        String refreshToken = jwtService.createRefreshToken(userId, true, accessToken);

        // 쿠키에 토큰 저장
        Cookie accessTokenCookie = new Cookie("X-AUTH-TOKEN", accessToken);
        accessTokenCookie.setHttpOnly(true);
//        accessTokenCookie.setSecure(true); // HTTPS를 사용하는 경우에만 true로 설정
        accessTokenCookie.setPath("/");

        Cookie refreshTokenCookie = new Cookie("REFRESH-TOKEN", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setSecure(true); // HTTPS를 사용하는 경우에만 true로 설정
        refreshTokenCookie.setPath("/");

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
}
