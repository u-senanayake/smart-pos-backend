package lk.udcreations.user.controller;

import lk.udcreations.user.config.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        // TODO replace with real DB validation
        if ("admin_user".equals(username) && "1234".equals(password)) {
            String token = jwtUtil.generateToken(username);
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @GetMapping("/user-info")
    public ResponseEntity<String> userInfo(@RequestHeader("Auth") String authHeader) {
        String token = authHeader.substring(6);
        String username = jwtUtil.extractUsername(token);
        return ResponseEntity.ok("User: " + username);
    }
}