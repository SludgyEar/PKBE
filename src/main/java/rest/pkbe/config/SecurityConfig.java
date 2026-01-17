package rest.pkbe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
/**
 * Configuración principal de seguridad para la aplicación.
 * Define la política de autenticación, autorización, manejo de sesiones y filtros de seguridad.
 * Utiliza JWT para autenticación sin estado y permite acceso libre a endpoints de autenticación.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Servicio para cargar usuarios desde la base de datos
    private final UserDetailsServiceConfig userDetailsService;
    // Filtro para validar JWT en cada petición
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Bean para codificar contraseñas usando BCrypt.
     */
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura la cadena de filtros de seguridad de Spring Security.
     *   Deshabilita CSRF (no necesario para APIs REST con JWT)
     *   Permite acceso libre a /auth/** y /error
     *   Requiere autenticación para cualquier otro endpoint
     *   Establece la política de sesión como Stateless
     *   Agrega el filtro JWT antes del filtro de autenticación por usuario/contraseña
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll() 
                .requestMatchers("/error").permitAll()
                .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    /**
     * Bean que define el proveedor de autenticación usando el servicio personalizado y el encoder de contraseñas.
     */
    @Bean
    AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Bean para obtener el AuthenticationManager de la configuración de Spring Security.
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
}
