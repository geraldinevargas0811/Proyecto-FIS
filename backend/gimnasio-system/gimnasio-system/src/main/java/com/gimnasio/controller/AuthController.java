package com.gimnasio.controller;

import com.gimnasio.dto.AuthResponse;
import com.gimnasio.dto.ErrorResponse;
import com.gimnasio.dto.LoginRequest;
import com.gimnasio.dto.LogoutRequest;
import com.gimnasio.dto.MessageResponse;
import com.gimnasio.dto.RefreshTokenRequest;
import com.gimnasio.dto.UserResponse;
import com.gimnasio.model.Usuario;
import com.gimnasio.security.JwtService;
import com.gimnasio.security.RefreshTokenRevocationService;
import com.gimnasio.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final RefreshTokenRevocationService refreshTokenRevocationService;

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioService usuarioService,
                          JwtService jwtService,
                          RefreshTokenRevocationService refreshTokenRevocationService) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.refreshTokenRevocationService = refreshTokenRevocationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getContrasena())
            );


            Usuario usuario = usuarioService.findByCorreo(request.getCorreo())
                    .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

            return ResponseEntity.ok(buildAuthResponse(usuario));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Unauthorized",
                    "Credenciales invalidas",
                    httpRequest.getRequestURI()
            ));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest request, HttpServletRequest httpRequest) {
        try {
            String refreshToken = request.getRefreshToken();

            if (!jwtService.isRefreshTokenValid(refreshToken)) {
                throw new BadCredentialsException("Refresh token invalido");
            }

            String tokenId = jwtService.extractTokenId(refreshToken);
            if (refreshTokenRevocationService.isRevoked(tokenId)) {
                throw new BadCredentialsException("Refresh token revocado");
            }

            String correo = jwtService.extractUsername(refreshToken);
            Usuario usuario = usuarioService.findByCorreo(correo)
                    .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

            refreshTokenRevocationService.revoke(tokenId);

            return ResponseEntity.ok(buildAuthResponse(usuario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Unauthorized",
                    "Refresh token invalido o expirado",
                    httpRequest.getRequestURI()
            ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestBody(required = false) LogoutRequest request) {
        if (request != null && request.getRefreshToken() != null && !request.getRefreshToken().isBlank()) {
            try {
                refreshTokenRevocationService.revoke(jwtService.extractTokenId(request.getRefreshToken()));
            } catch (Exception ignored) {
                // Logout must be idempotent for the client.
            }
        }

        return ResponseEntity.ok(new MessageResponse("Sesion cerrada correctamente"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Unauthorized",
                    "No autenticado",
                    request.getRequestURI()
            ));
        }

        Usuario usuario = usuarioService.findByCorreo(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(new UserResponse(usuario));
    }

    private AuthResponse buildAuthResponse(Usuario usuario) {
        return new AuthResponse(
                jwtService.generateAccessToken(usuario),
                jwtService.generateRefreshToken(usuario),
                jwtService.getAccessTokenExpirationMs(),
                jwtService.getRefreshTokenExpirationMs(),
                new UserResponse(usuario)
        );
    }
}
