package rest.pkbe.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import rest.pkbe.api.dto.request.auth.LoginRequest;
import rest.pkbe.api.dto.response.auth.AuthResponse;
import rest.pkbe.config.JwtService;
import rest.pkbe.domain.model.User;
import rest.pkbe.domain.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    IUserService userService;
    @Autowired
    JwtService jwtService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest req) {
        
        User user = userService.authenticate(req.getEmail(), req.getPassword());
        String token = jwtService.generateToken(user);
        
        return ResponseEntity.ok(new AuthResponse(token));
    }
    

}
