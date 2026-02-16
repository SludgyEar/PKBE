package rest.pkbe.config.auth;

import java.io.IOException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import rest.pkbe.config.jwt.JwtService;
// import lombok.RequiredArgsConstructor;
import rest.pkbe.domain.repository.BlacklistedTokenRepository;

/**
 * Filtro de autenticación JWT que intercepta cada petición HTTP.
 *
 * Extrae el token JWT del encabezado Authorization, valida el token y, si es válido,
 * establece la autenticación en el contexto de seguridad de Spring.
 * Permite la autenticación sin estado en la aplicación.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    // Servicio para operaciones con JWT (generar, validar, extraer datos)
    private final JwtService jwtService;
    // Servicio para cargar los detalles del usuario desde la base de datos
    private final UserDetailsServiceConfig userDetailsServiceConfig;
    // Interfaz que intercepta y resuelve excepciones que ocurren durante la ejecución de una petición HTTP (antes de llegar al controller correspondiente (middleware))
    private final HandlerExceptionResolver resolver;
    // Repositorio para comprobar el estado del token de acceso
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(
        JwtService jwtService,
        UserDetailsServiceConfig userDetailsServiceConfig,
        @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
        BlacklistedTokenRepository blacklistedTokenRepository){

        this.jwtService = jwtService;
        this.userDetailsServiceConfig = userDetailsServiceConfig;
        this.resolver = resolver;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    /**
     * Intercepta cada petición HTTP para procesar la autenticación basada en JWT.
     *
     * - Extrae el token JWT del encabezado Authorization.
     * - Valida el token y extrae el email del usuario.
     * - Si el usuario no está autenticado y el token es válido, establece la autenticación en el contexto de Spring.
     * - Si no hay token o no es válido, la petición sigue sin autenticación.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
        throws ServletException, IOException {
            logger.info("Petición de autenticación aceptada. Procesando solicitud...");
            try {
                
                final String authHeader = request.getHeader("Authorization");
                final String jwt;
                final String userEmail;

                // Si no hay encabezado Authorization o no comienza con 'Bearer ', continuar sin
                // procesar JWT
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    logger.debug("> Token inexistente: Abortando operación");
                    filterChain.doFilter(request, response);
                    return;
                }
                // Extraer el token JWT del encabezado
                jwt = authHeader.substring(7);
                logger.debug("> Token capturado");
                // Extraer el jti del token
                String jti = jwtService.extractJti(jwt);
                // Comprobar si el token no está baneado
                if(blacklistedTokenRepository.existsById(jti)){
                    logger.error("El token ha sido revocado.");
                    throw new CredentialsExpiredException("El token es inválido");
                }
                // Extraer el email del usuario desde el token
                userEmail = jwtService.extractEmail(jwt);
                // Si se extrajo un email y el usuario aún no está autenticado en el contexto
                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Cargar los detalles del usuario desde la base de datos
                    UserDetails userDetails = this.userDetailsServiceConfig.loadUserByUsername(userEmail);
                    // Verificar si el token es válido para el usuario
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        // Crear el objeto de autenticación y establecerlo en el contexto de seguridad
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        logger.debug("> Token válido");
                    }
                }
                // Continuar con la cadena de filtros
                logger.info("Autenticación en proceso...");
                filterChain.doFilter(request, response);

            } catch (Exception ex) {
                /**
                 * Como la excepción se produce antes de llegar a un controller, la capturamos y con ayuda del resolver
                 * la desenmascaramos para que el ControllerAdvice la maneje correctamente con los métodos que nosotros
                 * definimos para las posibles excepciones que se pueden producir
                 * ExpiredJwtEx.., SignatureEx.., MalformedJwtEx.., etc.
                 */
                logger.error("Proceso de Autenticación fallido: Credenciales inválidas");
                logger.error(ex.getMessage());
                logger.debug("Limpiando SecurityContextHolder...");
                SecurityContextHolder.clearContext();
                resolver.resolveException(request, response, null, ex);
            }
    }
    
}
