package boilerplate.chatgpt.config.oAuth2;

import boilerplate.chatgpt.config.dto.GoogleResponse;
import boilerplate.chatgpt.config.dto.KakaoResponse;
import boilerplate.chatgpt.config.dto.NaverResponse;
import boilerplate.chatgpt.config.dto.OAuth2Response;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OAuthResponseFactory {

    public static OAuth2Response getOAuth2Response(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "naver" -> new NaverResponse(attributes);
            case "google" -> new GoogleResponse(attributes);
            case "kakao" -> new KakaoResponse(attributes);
            default -> null;
        };
    }
}
