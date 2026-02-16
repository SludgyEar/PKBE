package rest.pkbe.config.auth;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint{

    // Interfaz que intercepta y resuelve excepciones que ocurren durante la ejecución de una petición HTTP (antes de llegar al controller correspondiente (middleware))
    private final HandlerExceptionResolver resolver;
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

    public CustomAuthenticationEntryPoint(
        @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver){
        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        /**
         * Delegamos la excepción a nuestro GlobalExceptionHandler
         * Dado que la excepción ocurre en un middleware, tenemos que usar el resolver para que 
         * la excepción pueda ser atendida en un ControllerAdvice
         */
        logger.error("Petición no autorizada frenada: Abortando operación");
        resolver.resolveException(request, response, null, authException);
    }
    
}
