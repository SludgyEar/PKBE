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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private IUserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest req) {
        String token = userService.authenticate(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody CreateUserRequest userRequest) throws URISyntaxException{
        /**
         * A través de un DTO recibimos los datos necesarios para crear un usuario
         * Asignamos los datos a una variable nueva y lo guardamos en la base de datos para que se le asigne un id
         */
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userRequest.getPasswordHash()));

        User saved = userService.register(user);
        
        return ResponseEntity.created(new URI("/users/" + saved.getId())).build();
    }
    

}
