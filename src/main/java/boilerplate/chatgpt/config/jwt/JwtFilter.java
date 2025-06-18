package boilerplate.chatgpt.config.jwt;

import boilerplate.chatgpt.app.uesr.entity.User;
import boilerplate.chatgpt.app.uesr.entity.UserRole;
import boilerplate.chatgpt.app.uesr.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> accessToken = findAccessToken(request, "X-AUTH-TOKEN");

        if(accessToken.isEmpty()) {
            log.info("accessToken is empty");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("tokenValue: {}", accessToken.get());

        try {
            TokenInfo tokenInfo = jwtService.getUserId(accessToken.get());

            if (!authenticationUser(request, response, tokenInfo.getUserId())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized");
                return;
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean authenticationUser(HttpServletRequest request, HttpServletResponse response, Long userId) {
        Optional<User> byId = userRepository.findById(userId);

        if (byId.isEmpty()) {
            return false;
        }

        User user = byId.get();
        UserRole role = user.getRole();

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role.name()));
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        log.info("유저 인증 완료");

        return true;
    }

    private Optional<String> findAccessToken(HttpServletRequest request, String accessToken) {
        Cookie[] cookies = request.getCookies();

        return cookies == null ? Optional.empty() : Arrays.stream(cookies)
                .filter(cookie -> accessToken.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue);
    }
}
