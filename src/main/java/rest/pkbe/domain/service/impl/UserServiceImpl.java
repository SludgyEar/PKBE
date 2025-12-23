package rest.pkbe.domain.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import rest.pkbe.domain.model.User;
import rest.pkbe.domain.repository.UserRepository;
import rest.pkbe.domain.service.IUserService;

@Service
public class UserServiceImpl implements IUserService{
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    @Override
    public User register(User user) {
        /**
         * Registra un usuario, si ya hay un correo ligado se rechaza la solicitud
         */
        if(userRepository.existsByEmail(user.getEmail())){
            throw new IllegalArgumentException("El email ya está registrado");
        }
        return userRepository.save(user);
    }

    @Override
    public User authenticate(String email, String password){
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
        if(!passwordEncoder.matches(password, user.getPasswordHash())){
            throw new RuntimeException("Credenciales inválidas");
        }
        return user;
    }
    
}
