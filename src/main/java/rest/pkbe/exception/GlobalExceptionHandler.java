package rest.pkbe.exception;


import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

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
        ExceptionResponse response = new ExceptionResponse();

        response.setTimeStamp(LocalDateTime.now());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setError("Sin acceso");
        response.setMessage("Credenciales inválidas");
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
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

        ExceptionResponse response = new ExceptionResponse();
        response.setTimeStamp(LocalDateTime.now());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setError("Token inválido");
        response.setMessage(ex.getMessage());
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body(response);
    }

    /**
     * Maneja excepción cuando las credenciales proporcionadas son incorrectas.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 401 y mensaje de token inválido
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request){
        ExceptionResponse response = new ExceptionResponse();

        response.setTimeStamp(LocalDateTime.now());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setError("Sin acceso");
        response.setMessage("Credenciales inválidas");
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * Maneja excepción cuando el JWT ha expirado.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 401 y mensaje de credenciales inválidas
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> handleExpiredToken(ExpiredJwtException ex, HttpServletRequest request){
        ExceptionResponse response = new ExceptionResponse();

        response.setTimeStamp(LocalDateTime.now());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setError("Sin acceso");
        response.setMessage("Credenciales inválidas");
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * Maneja excepción cuando la firma del JWT es inválida.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 401 y mensaje de credenciales inválidas
     */
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ExceptionResponse> handleSignatureJwtException(SignatureException ex, HttpServletRequest request){
        ExceptionResponse response = new ExceptionResponse();

        response.setTimeStamp(LocalDateTime.now());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setError("Sin acceso");
        response.setMessage("Credenciales inválidas");
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * Maneja excepción cuando el JWT está mal formado.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 401 y mensaje de credenciales inválidas
     */
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ExceptionResponse> handleMalformedToken(MalformedJwtException ex, HttpServletRequest request){
        ExceptionResponse response = new ExceptionResponse();

        response.setTimeStamp(LocalDateTime.now());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setError("Sin acceso");
        response.setMessage("Credenciales inválidas");
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);

    }

    /**
     * Maneja excepciones generales de autenticación.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 401 y mensaje de credenciales inválidas
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request){
        ExceptionResponse response = new ExceptionResponse();

        response.setTimeStamp(LocalDateTime.now());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setError("Sin acceso");
        response.setMessage("Credenciales inválidas");
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);

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
        ExceptionResponse response = new ExceptionResponse();

        response.setTimeStamp(LocalDateTime.now());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setError("Error en la construcción del recurso");
        response.setMessage("No se pudo generar la URI de confirmación: "+ ex.getReason());
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    /**
     * Maneja excepción de validación de constraints (por ejemplo, anotaciones @Validated).
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 400 y detalle de los campos inválidos
     */
    @ExceptionHandler(ConstraintViolationException.class) // Cuando un @Validated falla
    public ResponseEntity<ExceptionResponse> handleConstraintValidation(ConstraintViolationException ex, HttpServletRequest request){
        ExceptionResponse response = new ExceptionResponse();

        String detailMessage = ex.getConstraintViolations().stream()
                .map(violation -> {
                    String path = violation.getPropertyPath().toString();
                    String field = path.substring(path.lastIndexOf('.') + 1);
                    return field + ": " + violation.getMessage();
                })
                .collect(Collectors.joining(", "));

        response.setTimeStamp(LocalDateTime.now());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError("Datos inválidos");
        response.setMessage("Validación de argumentos fallida: " + detailMessage);
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
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
        ExceptionResponse response = new ExceptionResponse();

        response.setTimeStamp(LocalDateTime.now());
        response.setStatus(HttpStatus.CONFLICT.value());
        response.setError("Error de integridad de datos");
        response.setMessage("No se puede completar la operación por restricciones existentes");
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(response);
    }

    /**
     * Maneja excepción cuando los argumentos de un método no son válidos (fallo de validación en @RequestBody, etc).
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 400 y detalle de los campos inválidos
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request){
        ExceptionResponse response = new ExceptionResponse();

            String detailMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

        response.setTimeStamp(LocalDateTime.now());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError("Datos inválidos");
        response.setMessage("Validación de datos fallida: "+detailMessage);
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(response);
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
        ExceptionResponse response = new ExceptionResponse();
        
        response.setTimeStamp(LocalDateTime.now());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setError("Usuario no encontrado");
        response.setMessage(ex.getMessage());
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    /**
     * Maneja excepción personalizada cuando un recurso no es encontrado.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 404 y mensaje de recurso no encontrado
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request){
        ExceptionResponse response = new ExceptionResponse();

        response.setTimeStamp(LocalDateTime.now());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setError("Recurso no encontrado");
        response.setMessage(ex.getMessage());
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    /**
     * Maneja excepción cuando se recibe un argumento ilegal o inválido.
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta con estado 400 y mensaje de datos inválidos
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request){
        ExceptionResponse response = new ExceptionResponse();

        response.setTimeStamp(LocalDateTime.now());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError("Petición inválida");
        response.setMessage("Datos inválidos: " + ex.getMessage());
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
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
        ExceptionResponse response = new ExceptionResponse();

        response.setTimeStamp(LocalDateTime.now());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setError("Erro interno de servidor");
        response.setMessage("Ocurrió un error inesperado en el servidor. Intente más tarde");
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

}
