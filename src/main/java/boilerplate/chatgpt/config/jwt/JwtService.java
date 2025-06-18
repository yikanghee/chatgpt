package boilerplate.chatgpt.config.jwt;

import boilerplate.chatgpt.app.refreshToken.entity.RefreshToken;
import boilerplate.chatgpt.app.refreshToken.repository.RefreshTokenRedisRepository;
import boilerplate.chatgpt.app.uesr.entity.User;
import boilerplate.chatgpt.app.uesr.repository.UserRepository;
import boilerplate.chatgpt.config.exception.AccountException;
import boilerplate.chatgpt.config.exception.errorCode.ErrorCode;
import boilerplate.chatgpt.config.exception.errorCode.UserErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long expirationTime;
    @Value("${jwt.refresh.expiration}")
    private long refreshExpirationTime;

    private final UserRepository userRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    // 액세스 토큰 생성
    public String createAccessToken(Long userId) {

        Claims claims = Jwts.claims();

        claims.put("userId", userId);
        String access = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return access;
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(Long userId, boolean check, String accessToken) {
        Claims claims = Jwts.claims();

        Optional<User> byId = userRepository.findById(userId);
        if (byId.isEmpty() && check) {
            return "유저 없음";
        } else if (byId.isEmpty() && !check) {
            return "유저 데이터 없음";
        }

        User user = byId.get();

        String refresh = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        // Save to Redis using RefreshTokenRedisRepository
        RefreshToken refreshToken = new RefreshToken(String.valueOf(userId), refresh);
        log.info("refreshToken = {}", refreshToken);
        RefreshToken save = refreshTokenRedisRepository.save(refreshToken);
        log.info("저장된 리프레시 토큰: {}", save);

        return refresh;
    }

    public TokenInfo getUserId(String tokenValue) {

        try {
            Long userId = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(tokenValue)
                    .getBody().get("userId", Long.class);
            return TokenInfo.builder()
                    .userId(userId)
                    .isExpired(false).build();
        } catch (ExpiredJwtException e) {
            // 만료된 JWT에서도 userId는 가져옴.
            Long userId = e.getClaims().get("userId", Long.class);
            return TokenInfo.builder()
                    .userId(userId)
                    .isExpired(true).build();
        }
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof User) {
                return ((User) principal).getId();
            } else if (principal instanceof Long) {
                return (Long) principal;
            }
        }
        throw new AccountException(UserErrorCode.OAUTH2_NOT_FOUND);
    }
}
