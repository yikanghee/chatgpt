package boilerplate.chatgpt.app.refreshToken.controller;

import boilerplate.chatgpt.app.refreshToken.service.RefreshTokenService;
import boilerplate.chatgpt.common.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RefreshTokenService refreshTokenService;

    @GetMapping("/reissue")
    public ResponseEntity<ApiResponse<Void>> refreshToken(@CookieValue(value = "X-AUTH-TOKEN") String accessToken,
                                                          @CookieValue("REFRESH-TOKEN") String refreshToken, HttpServletResponse response) {
        refreshTokenService.refreshToken(response, accessToken, refreshToken);
        return ResponseEntity.ok(ApiResponse.createSuccessNoContent("토큰 재발급 완료"));
    }


}
