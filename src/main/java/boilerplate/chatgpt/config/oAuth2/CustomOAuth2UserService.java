package boilerplate.chatgpt.config.oAuth2;

import boilerplate.chatgpt.app.uesr.entity.User;
import boilerplate.chatgpt.app.uesr.entity.UserRole;
import boilerplate.chatgpt.app.uesr.repository.UserRepository;
import boilerplate.chatgpt.config.dto.OAuth2Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final OAuthResponseFactory oAuthResponseFactory;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = oAuthResponseFactory.getOAuth2Response(registrationId, oAuth2User.getAttributes());

        String email = oAuth2Response.getEmail();

        Optional<User> byUser = userRepository.findByEmail(email);

        if (byUser.isEmpty()) {
            User user = User.builder()
                    .userName(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .password("")
                    .isSocial(1L)
                    .role(UserRole.USER)
                    .build();
            userRepository.save(user);
        }
        return oAuth2User;
    }
}
