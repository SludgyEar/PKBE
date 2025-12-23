package rest.pkbe.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import rest.pkbe.api.dto.request.CreateUserRequest;
import rest.pkbe.domain.model.User;
import rest.pkbe.domain.service.IUserService;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/users")
/**
 * Endpoint que atiende las peticiones relacionadas con los usuarios
 */
public class UserController {

    @Autowired
    private IUserService userService;
    
    @PostMapping
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody CreateUserRequest userRequest) throws URISyntaxException{
        /**
         * A través de un DTO recibimos los datos necesarios para crear un usuario
         * Asignamos los datos a una variable nueva y lo guardamos en la base de datos para que se le asigne un id
         */
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPasswordHash(userRequest.getPasswordHash());

        User saved = userService.register(user);
        
        return ResponseEntity.created(new URI("/users/" + saved.getId())).build();
    }
    

}
