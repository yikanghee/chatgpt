package boilerplate.chatgpt.app.uesr.repository;

import boilerplate.chatgpt.app.uesr.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

}
