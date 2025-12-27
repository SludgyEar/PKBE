package rest.pkbe.config;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Filtro de autenticación JWT que intercepta cada petición HTTP.
 *
 * Extrae el token JWT del encabezado Authorization, valida el token y, si es válido,
 * establece la autenticación en el contexto de seguridad de Spring.
 * Permite la autenticación sin estado en la aplicación.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    // Servicio para operaciones con JWT (generar, validar, extraer datos)
    private final JwtService jwtService;
    // Servicio para cargar los detalles del usuario desde la base de datos
    private final UserDetailsServiceConfig userDetailsServiceConfig;

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
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Si no hay encabezado Authorization o no comienza con 'Bearer ', continuar sin procesar JWT
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        // Extraer el token JWT del encabezado
        jwt = authHeader.substring(7);
        // Extraer el email del usuario desde el token
        userEmail = jwtService.extractEmail(jwt);

        // Si se extrajo un email y el usuario aún no está autenticado en el contexto
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
            // Cargar los detalles del usuario desde la base de datos
            UserDetails userDetails = this.userDetailsServiceConfig.loadUserByUsername(userEmail);
            // System.out.println("*************************");
            // System.out.println("userDetails: " + userDetails);
            // System.out.println("*************************");
            // Verificar si el token es válido para el usuario
            if(jwtService.isTokenValid(jwt, userDetails)){
                // Crear el objeto de autenticación y establecerlo en el contexto de seguridad
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
    
}
