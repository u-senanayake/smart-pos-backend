package lk.udcreations.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lk.udcreations.common.dto.auth.LoginResponseDTO;
import lk.udcreations.user.config.JwtUtil;
import lk.udcreations.user.entity.Users;
import lk.udcreations.user.service.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Authentication API", description = "Endpoints for user authentication and user info retrieval")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UsersService usersService;

    public AuthController(JwtUtil jwtUtil, UsersService usersService) {
        this.jwtUtil = jwtUtil;
        this.usersService = usersService;
    }

    @Operation(
            summary = "User login",
            description = "Authenticate user and return JWT token and user details")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid username or password",
                    content = @Content(mediaType = "application/json"))})
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login credentials (username and password)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Map.class)))
            @RequestBody Map<String, String> body) {
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

    @Operation(
            summary = "Get user info from JWT token",
            description = "Extract username from JWT token in the Auth header")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User info retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid token",
                    content = @Content(mediaType = "application/json"))})
    @GetMapping("/user-info")
    public ResponseEntity<String> userInfo(
            @Parameter(
                    description = "Auth header containing the JWT token (e.g., 'Bearer <token>')",
                    required = true,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Auth") String authHeader) {
        String token = authHeader.substring(6);
        String username = jwtUtil.extractUsername(token);
        return ResponseEntity.ok("User: " + username);
    }
}