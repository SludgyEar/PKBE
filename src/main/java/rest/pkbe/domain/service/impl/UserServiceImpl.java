package rest.pkbe.domain.service.impl;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import rest.pkbe.config.JwtService;
import rest.pkbe.domain.model.RefreshToken;
import rest.pkbe.domain.model.User;
import rest.pkbe.domain.repository.RefreshTokenRepository;
import rest.pkbe.domain.repository.UserRepository;
import rest.pkbe.domain.service.IUserService;

@Service
public class UserServiceImpl implements IUserService{
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
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
    public String[] authenticate(String email, String password){
        // Creamos un token sin autenticar
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        // El manager se encarga de autenticarlo: llama internamente a UserDetailsServiceConfig -> loadByUsername (email) y si falla manda una exception
        authenticationManager.authenticate(authToken);
        // Ahora yo genero el token JWT y lo devuelvo: buscamos al usuario
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Email inválido"));
        String token = jwtService.generateToken(user);

        // Este token solo será guardado en la base de datos para refrescar la sesión
        String refreshToken = jwtService.generateRefreshToken(user);
        RefreshToken persistencedRefreshToken = new RefreshToken();
        persistencedRefreshToken.setToken(refreshToken);
        persistencedRefreshToken.setUser(user);
        // Creando LocalDateTime en base a un Date
        Date expirationDate = jwtService.extracExpiration(refreshToken);
        LocalDateTime expirLocalDateTime = LocalDateTime.ofInstant(expirationDate.toInstant(), ZoneId.systemDefault());
        persistencedRefreshToken.setExpirationDate(expirLocalDateTime);
        // Guardamos token de refresco en la base de datos
        refreshTokenRepository.save(persistencedRefreshToken);
        String [] res = {token, refreshToken};
        return res;
    }

    @Override
    public String[] refreshSession(String refreshToken){ // Tenemos que devolver un token de acceso y un token de refresco
        /**
         * Del token recibido sacamos el id del usuario y buscamos en la BD una coincidencia entre el token y el usuario y validamos que no haya expirado
         * Si se cumplen estos puntos avanzamos a crear un token de refresco y un token de acceso nuevos.
        */
        Long userId = jwtService.extractUserId(refreshToken);
        RefreshToken oldToken = refreshTokenRepository.findByTokenAndUserId(refreshToken, userId)
            .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        if(jwtService.isTokenExpired(refreshToken)){
            throw new CredentialsExpiredException("El token no es válido. Valida tus credenciales");
        }

        /**
         * Como la sesion se valida por el token de refresco:
         * Creamos un nuevo token de acceso y un nuevo token de refresco
         * Borramos el oldRefreshToken de la bd y agregamos uno nuevo
         */
        String newAccessToken = jwtService.generateToken(oldToken.getUser());   // listo para retornar
        String newRefreshToken = jwtService.generateRefreshToken(oldToken.getUser());

        RefreshToken persistencedRefreshToken = new RefreshToken();
        persistencedRefreshToken.setToken(newRefreshToken);
        persistencedRefreshToken.setUser(oldToken.getUser());
        // Calculamos la fecha de expiración
        Date expirationDate = jwtService.extracExpiration(newRefreshToken);
        LocalDateTime expirLocalDateTime = LocalDateTime.ofInstant(expirationDate.toInstant(), ZoneId.systemDefault());
        persistencedRefreshToken.setExpirationDate(expirLocalDateTime);

        // Borramos el token de refresco usado
        refreshTokenRepository.delete(oldToken);
        // Agregamos el nuevo
        refreshTokenRepository.save(persistencedRefreshToken);

        String [] res = { newAccessToken, newRefreshToken };
        return res;
    }
    
}
