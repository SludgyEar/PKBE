package rest.pkbe.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import rest.pkbe.api.dto.request.auth.CreateUserRequest;
import rest.pkbe.api.dto.request.auth.LoginRequest;
import rest.pkbe.api.dto.response.auth.AuthResponse;
import rest.pkbe.domain.model.User;
import rest.pkbe.domain.service.IUserService;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private IUserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest req) {
        logger.info("Iniciando POST /login - Validando Credenciales");
        /**
         * A través de un DTO recibimos las credenciales de un usuario para autenticarlo
         * Si la autenticación es exitosa, se regresa un token de acceso y un token de refresco, ambos Strings
         * Creamos una cookie para mandar el token de refresco y envíamos el token de acceso en la respuesta JSON
         */
        String [] response = userService.authenticate(req.getEmail(), req.getPassword());
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", response[1])
            .httpOnly(true)
            .secure(false) // solo se envía por https
            .path("/") // disponible en toda la aplicación
            .maxAge(7 * 24 * 60 * 60)
            .sameSite("Lax")
            .build();
        
        logger.info("Operación POST /login - Finalizada");
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(new AuthResponse(response[0]));
    }

    @PostMapping("/register")
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody CreateUserRequest userRequest) throws URISyntaxException{
        logger.info("Iniciando POST /register - Registrando Usuario");
        /**
         * A través de un DTO recibimos los datos necesarios para crear un usuario
         * Asignamos los datos a una variable nueva y lo guardamos en la base de datos para que se le asigne un id
         */
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userRequest.getPasswordHash()));

        User saved = userService.register(user);
        logger.info("Operación POST /register - Finalizada");
        return ResponseEntity.created(new URI("/users/" + saved.getId())).build();
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue String refreshToken) {
        logger.info("Iniciando GET /refresh - Renovando Sesión.");
        String[] response = userService.refreshSession(refreshToken);
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", response[1])
                .httpOnly(true)
                .secure(false) // solo se envía por https
                .path("/") // disponible en toda la aplicación
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();
                logger.info("Operación GET /refresh - Finalizada");
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(new AuthResponse(response[0]));
    }
    
    

}
