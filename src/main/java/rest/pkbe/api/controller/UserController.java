package rest.pkbe.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import rest.pkbe.api.dto.request.CreateUserRequest;
import rest.pkbe.domain.model.User;
import rest.pkbe.domain.service.impl.UserServiceImpl;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServiceImpl userServiceImpl;
    
    @PostMapping
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody CreateUserRequest userRequest) throws URISyntaxException{
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPasswordHash(userRequest.getPasswordHash());

        User saved = userServiceImpl.register(user);
        
        return ResponseEntity.created(new URI("/users/" + saved.getId())).build();
    }
    

}
