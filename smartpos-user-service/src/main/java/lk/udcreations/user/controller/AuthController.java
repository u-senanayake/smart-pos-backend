package lk.udcreations.user.controller;

import lk.udcreations.common.dto.auth.LoginResponseDTO;
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
    public ResponseEntity<LoginResponseDTO> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        LoginResponseDTO response = new LoginResponseDTO();
        Users users = usersService.findUserByUsername(username);

        if (users.getUsername().equals(username) && usersService.passwordMatches(password, users.getPassword())) {
            String token = jwtUtil.generateToken(username);

            response.setToken(token);
            response.setUser(usersService.convertToDTO(users));
            return ResponseEntity.ok(response);
        } else {
            response.setToken(null);
            response.setUser(null);
        }
        return ResponseEntity.status(401).body(response);
    }

    @GetMapping("/user-info")
    public ResponseEntity<String> userInfo(@RequestHeader("Auth") String authHeader) {
        String token = authHeader.substring(6);
        String username = jwtUtil.extractUsername(token);
        return ResponseEntity.ok("User: " + username);
    }
}