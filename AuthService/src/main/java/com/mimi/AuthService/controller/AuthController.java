package com.mimi.AuthService.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mimi.AuthService.dto.LoginRequest;
import com.mimi.AuthService.dto.LoginResponse;
import com.mimi.AuthService.security.JwtUtil;
import com.mimi.AuthService.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para login, perfil y autorización por roles")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    // ===========================================================
    // LOGIN
    // ===========================================================
    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales del usuario y retorna un JWT junto con los datos del usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Credenciales incorrectas o datos inválidos", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Credenciales del usuario", required = true, content = @Content(schema = @Schema(implementation = LoginRequest.class), examples = @ExampleObject(value = """
                    {
                      "email": "usuario@mail.com",
                      "password": "123456"
                    }
                    """))) LoginRequest request) {

        LoginResponse response = authService.login(request.getEmail(), request.getPassword());

        if (!response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }

        if (response.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return ResponseEntity.ok(response);
    }

    // ===========================================================
    // ADMIN-ONLY
    // ===========================================================
    @Operation(summary = "Endpoint solo para ADMIN", description = "Valida el JWT y permite acceso únicamente si el rol del usuario es ADMIN (rolId = 2).", security = {
            @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Acceso permitido"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol ADMIN")
    })
    @GetMapping("/admin-only")
    public ResponseEntity<String> adminOnly(
            @Parameter(description = "Solicitud HTTP que contiene el header Authorization") HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token faltante");
        }

        String token = authHeader.substring(7);

        Long rolId;
        try {
            rolId = jwtUtil.extractRole(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        if (rolId != 2L) {
            return ResponseEntity.status(403).body("Acceso denegado: solo ADMIN");
        }

        return ResponseEntity.ok("Bienvenido ADMIN ✔");
    }

    // ===========================================================
    // PROFILE (/me)
    // ===========================================================
    @Operation(summary = "Obtener el usuario autenticado", description = "Retorna la información del usuario usando el JWT.", security = {
            @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario autenticado retornado correctamente", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "UsuarioEjemplo", value = """
                    {
                        "id": 1,
                        "nombre": "Bastián",
                        "email": "bastian@mail.com",
                        "rol": 2
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "error": "Token inválido"
                    }
                    """)))
    })
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        return ResponseEntity.ok(authService.me(request));
    }
}
