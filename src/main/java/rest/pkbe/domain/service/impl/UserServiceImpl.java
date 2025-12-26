package rest.pkbe.domain.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import rest.pkbe.config.JwtService;
import rest.pkbe.domain.model.User;
import rest.pkbe.domain.repository.UserRepository;
import rest.pkbe.domain.service.IUserService;

@Service
public class UserServiceImpl implements IUserService{
    
    @Autowired
    private UserRepository userRepository;
    // @Autowired
    // private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    
    @Transactional
    @Override
    public User register(User user) {
        /**
         * Registra un usuario, si ya hay un correo ligado se rechaza la solicitud
         * El usuario que se recibe ya tiene la contraseña encriptada por nuestro passwordEncoder
         */
        if(userRepository.existsByEmail(user.getEmail())){
            throw new IllegalArgumentException("El email ya está registrado");
        }
        return userRepository.save(user);
    }

    @Override
    public String authenticate(String email, String password){
        // Creamos un token sin auth
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        // El manager se encarga de autenticarlo: llama internamente a UserDetailsServiceConfig -> loadByUsername (email) y si falla manda una exception
        authenticationManager.authenticate(authToken);
        // Ahora yo genero el token JWT y lo devuelvo: buscamos al usuario
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email inválido"));
        String token = jwtService.generateToken(user);
        return token;
    }
    
}
