package boilerplate.chatgpt.app.uesr.cotroller;

import boilerplate.chatgpt.app.uesr.entity.User;
import boilerplate.chatgpt.app.uesr.repository.UserRepository;
import boilerplate.chatgpt.config.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class GetController {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public GetController(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @GetMapping("/api/v1/test")
    public String test() {
        return "Hello, World!";
    }

    @GetMapping("/api/v1/test1")
    public String test1() {

        User currentMember = getCurrentMember();

        log.info("Current Member: {}", currentMember);

        return "Hello, World!";
    }

    private User getCurrentMember() {
        Long currentUserId = jwtService.getCurrentUserId();
        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
