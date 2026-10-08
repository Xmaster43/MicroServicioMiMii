package com.mimi.AuthService.service;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.mimi.AuthService.dto.LoginRequest;
import com.mimi.AuthService.dto.LoginResponse;
import com.mimi.AuthService.dto.UserAuthDTO;
import com.mimi.AuthService.dto.UserDTO;
import com.mimi.AuthService.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    @Value("${userservice.url:http://localhost:8084}")
    private String userServiceUrl;

    public LoginResponse login(String email, String password) {

        try {
            String url = userServiceUrl + "/api/users/validate";

            UserAuthDTO usuario = restTemplate.postForObject(
                    url,
                    new LoginRequest(email, password),
                    UserAuthDTO.class);

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

            return new LoginResponse(
                    true,
                    "Login exitoso",
                    usuario,
                    token);

        } catch (HttpClientErrorException.Unauthorized e) {
            return new LoginResponse(
                    true,
                    "Credenciales inválidas",
                    null,
                    null);

        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResponse(
                    false,
                    "Error conectando a UserService",
                    null,
                    null);
        }
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

            UserDTO usuario = restTemplate.getForObject(url, UserDTO.class);

            if (usuario == null) {
                return Map.of("error", "Usuario no encontrado");
            }

            return usuario;

        } catch (Exception e) {
            return Map.of("error", "Token inválido o expirado");
        }
    }

}
