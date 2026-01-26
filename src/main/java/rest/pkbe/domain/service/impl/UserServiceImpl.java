package rest.pkbe.domain.service.impl;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Transactional
    @Override
    public User register(User user) {
        logger.info("Registrando usuario en la base de datos...");
        /**
         * Registra un usuario, si ya hay un correo ligado se rechaza la solicitud
         * El usuario que se recibe ya tiene la contraseña encriptada por nuestro passwordEncoder
         */
        if(userRepository.existsByEmail(user.getEmail())){
            logger.error("Intento de registro fallido: Correo registrado");
            throw new IllegalArgumentException("El email ya está registrado");
        }
        logger.info("Usuario registrado exitosamente [!]");
        return userRepository.save(user);
    }

    @Override
    public String[] authenticate(String email, String password){
        logger.info("Validando credenciales...");
        // Creamos un token sin autenticar
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        // El manager se encarga de autenticarlo: llama internamente a UserDetailsServiceConfig -> loadByUsername (email) y si falla manda una exception
        authenticationManager.authenticate(authToken);
        // Ahora yo genero el token JWT y lo devuelvo: buscamos al usuario
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> {
                logger.warn("Intento de login fallido: Email no encontrado");
                return new UsernameNotFoundException("Email inválido");
            });
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
        logger.info("Login exitoso: Generando sesión [!]");
        return res;
    }

    @Override
    public String[] refreshSession(String refreshToken){
        logger.info("Renovando sesión...");
        /**
         * Del token recibido sacamos el id del usuario y buscamos en la BD una coincidencia entre el token y el usuario y validamos que no haya expirado
         * Si se cumplen estos puntos avanzamos a crear un token de refresco y un token de acceso nuevos.
        */
        Long userId = jwtService.extractUserId(refreshToken);
        RefreshToken oldToken = refreshTokenRepository.findByTokenAndUserId(refreshToken, userId)
            .orElseThrow(() -> {
                logger.error("Intento de renovación de sesión fallida: Credenciales inválidas");
                return new BadCredentialsException("Credenciales inválidas");
            });
        if(jwtService.isTokenExpired(refreshToken)){
            logger.error("Intento de renovación de sesión fallida: Token expirado");
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
        
        logger.info("Sesión renovada exitosamente: Token de refresco generado [!]");
        String [] res = { newAccessToken, newRefreshToken };
        return res;
    }
    
    // @Override
    // public void delete(Long id){
    //     logger.debug("[>] TEST [<] Eliminando usuario para probar la seguridad de los tokens de acceso");
    //     userRepository.deleteById(id);
    //     logger.debug("[>] TEST [<] Usuario eliminado, ahora intente usar su token...");
    // }

}
