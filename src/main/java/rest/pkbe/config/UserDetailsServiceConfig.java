package rest.pkbe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import rest.pkbe.domain.repository.UserRepository;


/**
 * Implementación personalizada de {@link UserDetailsService} para la autenticación en Spring Security.
 * El objetivo de implementar {@code UserDetailsService} es proporcionar un mecanismo para cargar los detalles
 * de un usuario (como email y contraseña) desde la base de datos, permitiendo a Spring Security autenticar
 * usuarios de manera flexible y desacoplada del almacenamiento subyacente.
 * En este caso, se busca un usuario por su email y se construye un objeto {@code UserDetails} requerido por
 * el framework de seguridad.
 */
@Service
public class UserDetailsServiceConfig implements UserDetailsService {

    @Autowired
    private UserRepository userpreRepository;

    /**
     * Carga los detalles de un usuario a partir de su email.
     * Este método es invocado automáticamente por Spring Security durante el proceso de autenticación.
     * Si el usuario no existe, lanza una excepción.
     *
     * @param email el email del usuario a buscar
     * @return un objeto UserDetails con la información necesaria para la autenticación
     * @throws UsernameNotFoundException si el usuario no se encuentra registrado
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userpreRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Correo no registrado"));
    }
    
}
