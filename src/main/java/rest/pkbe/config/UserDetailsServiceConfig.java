package rest.pkbe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import rest.pkbe.domain.model.User;
import rest.pkbe.domain.repository.UserRepository;


@Service
public class UserDetailsServiceConfig implements UserDetailsService {

    @Autowired
    private UserRepository userpreRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userpreRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Correo no registrado"));
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPasswordHash())
            .build();
    }
    
}
