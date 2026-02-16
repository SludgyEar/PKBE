package rest.pkbe.exception;


import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import rest.pkbe.exception.dto.ExceptionResponse;
import rest.pkbe.exception.exceptions.ResourceNotFoundException;

/**
 * Manejador global de excepciones para la API REST de PKBE.
 *
 * Esta clase captura y gestiona excepciones lanzadas por los controladores,
 * devolviendo respuestas estructuradas y mensajes personalizados según el tipo de error.
 * Utiliza anotaciones de Spring para interceptar excepciones comunes de seguridad,
 * validación, integridad de datos y recursos no encontrados.
 *
 * Cada método maneja un tipo específico de excepción y construye una respuesta
 * consistente con detalles como timestamp, status, error, mensaje y path.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Para evitar repetir código, creamos una función que inicialice una respuesta JSON para
     * el error que se haya presentado.
     * Por defecto, tienen el timestamp al momento de su creación, añadimos sus valores para enviarla
     */
    private ExceptionResponse buildResponse(int status, String error, String message, String path){

        ExceptionResponse response = new ExceptionResponse();
        response.setTimeStamp(LocalDateTime.now());
        response.setStatus(status);
        response.setError(error);
        response.setMessage(message);
        response.setPath(path);

        logger.error(
            "Solicitud fallida... \nTimeStamp: {}\tStatus: {}\nError: {}\tMessage: {}\nPath: {}",
            LocalDateTime.now(), status, error, message, path);
        return response;
    }


    // =====================
    // Excepciones relacionadas con JWT y autenticación
    // =====================
    /**
     * Maneja excepciones generales de JWT (token inválido, manipulado, etc).
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 401 y mensaje de credenciales inválidas
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ExceptionResponse> handleJwtException(JwtException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                    buildResponse(HttpStatus.UNAUTHORIZED.value(), "Sin acceso", "Credenciales inválidas", 
                        request.getRequestURI())
                    );
    }

    /**
     * Maneja excepción cuando las credenciales han expirado.
     * Limpia la cookie de refreshToken.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 401 y cookie limpia
     */
    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<ExceptionResponse> handleCredentialsExpiredToken(CredentialsExpiredException ex, HttpServletRequest request){
        ResponseCookie cleanCookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(0)
                    .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body(
                    buildResponse(HttpStatus.UNAUTHORIZED.value(), "Token inválido", "Credenciales inválidas",
                            request.getRequestURI())
                );
    }

    /**
     * Maneja excepción cuando las credenciales proporcionadas son incorrectas.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 401 y mensaje de token inválido
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildResponse(HttpStatus.UNAUTHORIZED.value(), "Sin acceso", "Credenciales inválidas",
                        request.getRequestURI()));
    }

    /**
     * Maneja excepción cuando el JWT ha expirado.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 401 y mensaje de credenciales inválidas
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> handleExpiredToken(ExpiredJwtException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildResponse(HttpStatus.UNAUTHORIZED.value(), "Sin acceso", "Credenciales inválidas",
                        request.getRequestURI()));
    }

    /**
     * Maneja excepción cuando la firma del JWT es inválida.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 401 y mensaje de credenciales inválidas
     */
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ExceptionResponse> handleSignatureJwtException(SignatureException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildResponse(HttpStatus.UNAUTHORIZED.value(), "Sin acceso", "Credenciales inválidas",
                        request.getRequestURI()));
    }

    /**
     * Maneja excepción cuando el JWT está mal formado.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 401 y mensaje de credenciales inválidas
     */
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ExceptionResponse> handleMalformedToken(MalformedJwtException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildResponse(HttpStatus.UNAUTHORIZED.value(), "Sin acceso", "Credenciales inválidas",
                        request.getRequestURI()));

    }

    /**
     * Maneja excepciones generales de autenticación.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 401 y mensaje de credenciales inválidas
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildResponse(HttpStatus.UNAUTHORIZED.value(), "Sin acceso", "Credenciales inválidas",
                        request.getRequestURI()));

    }

    // =====================
    // Excepciones al crear recursos
    // =====================
    /**
     * Maneja excepción cuando ocurre un error al construir una URI.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 500 y mensaje de error de construcción de recurso
     */
    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity<ExceptionResponse> handleURISyntaxException(URISyntaxException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                        "Error en la construcción del recurso", "No se pudo generar la URI de confirmación: "+ ex.getReason(),
                        request.getRequestURI()));
    }

    /**
     * Maneja excepción de validación de constraints (por ejemplo, anotaciones @Validated).
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 400 y detalle de los campos inválidos
     */
    @ExceptionHandler(ConstraintViolationException.class) // Cuando un @Validated falla
    public ResponseEntity<ExceptionResponse> handleConstraintValidation(ConstraintViolationException ex, HttpServletRequest request){
        String detailMessage = ex.getConstraintViolations().stream()
                .map(violation -> {
                    String path = violation.getPropertyPath().toString();
                    String field = path.substring(path.lastIndexOf('.') + 1);
                    return field + ": " + violation.getMessage();
                })
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    buildResponse(HttpStatus.BAD_REQUEST.value(), "Datos inválidos", "Validación de argumentos fallida" + detailMessage, request.getRequestURI())
                );
    }

    /**
     * Maneja excepción cuando se viola la integridad de datos en la base (restricciones únicas, foráneas, etc).
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 409 y mensaje de conflicto de integridad
     */
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class) // Cuando se viola integridad de base de datos
    public ResponseEntity<ExceptionResponse> handleHibernateConstraintValidation(
            org.springframework.dao.DataIntegrityViolationException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                    buildResponse(HttpStatus.CONFLICT.value(), "Error de integridad de datos", "No se puede completar la operación por restricciones existentes", request.getRequestURI())
                );
    }

    /**
     * Maneja excepción cuando los argumentos de un método no son válidos (fallo de validación en @RequestBody, etc).
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 400 y detalle de los campos inválidos
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request){

            String detailMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                        buildResponse(HttpStatus.BAD_REQUEST.value(), "Datos inválidos", 
                                "Validación de datos fallida: " + detailMessage, request.getRequestURI())
                    );
    }

    // =====================
    // Excepciones al localizar recursos
    // =====================
    /**
     * Maneja excepción cuando un usuario no es encontrado en el sistema.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 404 y mensaje de usuario no encontrado
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildResponse(HttpStatus.NOT_FOUND.value(), "Usuario no encontrado", ex.getMessage(), request.getRequestURI()));
    }

    /**
     * Maneja excepción personalizada cuando un recurso no es encontrado.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 404 y mensaje de recurso no encontrado
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildResponse(HttpStatus.NOT_FOUND.value(), "Recurso no encontrado", ex.getMessage(), 
                        request.getRequestURI()));
    }

    /**
     * Maneja excepción cuando se recibe un argumento ilegal o inválido.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 400 y mensaje de datos inválidos
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildResponse(HttpStatus.BAD_REQUEST.value(), "Petición inválida", 
                        "Datos inválidos: " + ex.getMessage(), request.getRequestURI()));
    }

    // =====================
    // Excepción general (catch-all)
    // =====================
    /**
     * Maneja cualquier excepción no controlada, devolviendo un error genérico de servidor.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 500 y mensaje genérico de error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGeneralException(Exception ex, HttpServletRequest request){
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno de servidor", 
                        "Ocurrió un error inesperado en el servidor. Intente más tarde.", request.getRequestURI()));
    }

}
