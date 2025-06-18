package boilerplate.chatgpt.app.refreshToken.repository;

import boilerplate.chatgpt.app.refreshToken.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {
}
