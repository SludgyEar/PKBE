package rest.pkbe.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import rest.pkbe.model.User;
import rest.pkbe.repository.UserRepository;
import rest.pkbe.service.IUserService;

@Service
public class UserServiceImpl implements IUserService{
    
    @Autowired
    private UserRepository userRepository;
    
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
    
}
