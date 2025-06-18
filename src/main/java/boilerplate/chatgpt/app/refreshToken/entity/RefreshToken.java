package boilerplate.chatgpt.app.refreshToken.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "refresh_token", timeToLive = 3600) // 7 days
public class RefreshToken {

    @Id
    private String userId;
    private String token;
}
