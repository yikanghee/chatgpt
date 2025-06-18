package boilerplate.chatgpt.config;

import boilerplate.chatgpt.config.jwt.JwtFilter;
import boilerplate.chatgpt.config.oAuth2.CustomOAuth2UserService;
import boilerplate.chatgpt.config.oAuth2.SuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final SuccessHandler oauth2SuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/v1/test",
                                "/api/v1/auth/reissue",
                                "/api/v1/signup",
                                "/api/v1/login",
                                "/oauth2/**",
                                "/login/**",
                                "/login/oauth2/callback/**",
                                "/css/**","/images/**","/js/**","/favicon.ico",
                                "/oauth2/authorization/**").permitAll()
                        .anyRequest().authenticated()
                )

                .oauth2Login(
                        oauth ->{ oauth.userInfoEndpoint(
                                userInfo -> userInfo.userService(customOAuth2UserService)
                        );
                            oauth.successHandler(oauth2SuccessHandler);
                        }
                )
        ;
        return http.build();

    }
}
