package lk.udcreations.user.controller;

import lk.udcreations.user.config.JwtUtil;
import lk.udcreations.user.entity.Users;
import lk.udcreations.user.service.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UsersService usersService;

    public AuthController(JwtUtil jwtUtil, UsersService usersService) {
        this.jwtUtil = jwtUtil;
        this.usersService = usersService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Users users = usersService.findUserByUsername(username);
        if (users.getUsername().equals(username) && usersService.passwordMatches(password, users.getPassword())) {
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