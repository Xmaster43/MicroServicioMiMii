package com.mimi.AuthService.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.mimi.AuthService.dto.LoginRequest;
import com.mimi.AuthService.dto.LoginResponse;
import com.mimi.AuthService.dto.StreamEventDTO;
import com.mimi.AuthService.dto.UserAuthDTO;
import com.mimi.AuthService.dto.UserDTO;
import com.mimi.AuthService.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String AUTH_TOPIC = "mimii.auth.eventos";

    private final RestClient restClient;
    private final JwtUtil jwtUtil;
    private final KafkaEventProducer kafkaEventProducer;

    @Value("${userservice.url:http://localhost:8084}")
    private String userServiceUrl;

    @CircuitBreaker(name = "userService", fallbackMethod = "loginFallback")
    public LoginResponse login(String email, String password) {
        String url = userServiceUrl + "/api/users/validate";

        UserAuthDTO usuario = restClient.post()
                .uri(url)
                .body(new LoginRequest(email, password))
                .retrieve()
                .body(UserAuthDTO.class);

        if (usuario == null) {
            return new LoginResponse(
                    true,
                    "Credenciales inválidas",
                    null,
                    null);
        }

        String token = jwtUtil.generateToken(
                usuario.getCorreo(),
                usuario.getRolId());

        kafkaEventProducer.publicar(
                AUTH_TOPIC,
                usuario.getCorreo(),
                StreamEventDTO.builder()
                        .eventId(UUID.randomUUID().toString())
                        .sourceService("AuthService")
                        .eventType("USER_LOGIN")
                        .entityId(usuario.getCorreo())
                        .payload("{\"correo\":\"" + usuario.getCorreo() + "\",\"rolId\":" + usuario.getRolId() + "}")
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        return new LoginResponse(
                true,
                "Login exitoso",
                usuario,
                token);
    }

    private LoginResponse loginFallback(String email, String password, Throwable t) {
        return new LoginResponse(
                false,
                "UserService no disponible",
                null,
                null);
    }

    public Object me(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Map.of("error", "Token faltante");
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtUtil.extractEmail(token);
            String url = userServiceUrl + "/api/users/by-email/" + email;

            UserDTO usuario = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(UserDTO.class);

            if (usuario == null) {
                return Map.of("error", "Usuario no encontrado");
            }

            return usuario;
        } catch (Exception e) {
            return Map.of("error", "Token inválido o expirado");
        }
    }
}