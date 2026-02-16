package rest.pkbe.config.jwt;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import rest.pkbe.domain.model.User;


/**
 * Servicio para la gestión de tokens JWT en la aplicación.
 * Proporciona métodos para generar, validar y extraer información de los tokens JWT,
 * facilitando la autenticación y autorización basada en tokens.
 * Utiliza la clave secreta y el tiempo de expiración definidos en las propiedades.
 */
@Service
public class JwtService {
    
@Value("${jwt.secret}")
private String secret;

@Value("${jwt.expiration}")
private long expirationMs;

@Value("${jwt.refresh-expiration}")
private long refreshExpirationMs;

    // Método para obtener la llave
    /**
     * Obtiene la clave secreta para firmar y verificar los tokens JWT.
     * @return clave secreta en formato SecretKey
     */
    private SecretKey getSignInKey(){
        // decodificamos la secret key
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Método para generar tokens
    /**
     * Genera un token JWT para el usuario proporcionado.
     * @param user usuario para el cual se genera el token
     * @return token JWT como String
     */
    public String generateToken(User user){
        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("email", user.getEmail())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(getSignInKey(), Jwts.SIG.HS256)
            .id(UUID.randomUUID().toString())
            .compact();
    }

    // Método para generar un token de refresco de sesión
    /**
     * Genera un token JWT para que el usuario proporcionado refresque su sesión
     * @param user usuario para el cual se genera el token
     * @return token JWT como String
     */
    public String generateRefreshToken(User user){
        return Jwts.builder()
            .subject(user.getId().toString())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
            .signWith(getSignInKey(), Jwts.SIG.HS256)
            .compact();
    }

    /**
     * Valida si el token es correcto y corresponde al usuario indicado.
     * @param token token JWT a validar
     * @param userDetails detalles del usuario autenticado
     * @return true si el token es válido y pertenece al usuario, false en caso contrario
     */
    public boolean isTokenValid(String token, UserDetails userDetails){
        // Sacamos el email del token
        final String emailFromToken = extractEmail(token);
        boolean isUserMatch = emailFromToken.equals(userDetails.getUsername());
        boolean isTokenExpired = isTokenExpired(token);
        return isUserMatch && !isTokenExpired;
    }

    /**
     * Verifica si el token ha expirado.
     * @param token token JWT
     * @return true si el token está expirado, false si aún es válido
     */
    public boolean isTokenExpired(String token) {
        return extracExpiration(token).before(new Date());
    }

    // Método para extraer todos los claims
    /**
     * Extrae todos los claims (información) del token JWT.
     * @param token token JWT
     * @return objeto Claims con la información contenida en el token
     */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Método para extraer la fecha de expiración
    /**
     * Extrae la fecha de expiración del token JWT.
     * @param token token JWT
     * @return fecha de expiración
     */
    public Date extracExpiration(String token){
        return extractClaims(token).getExpiration();    // Extrae la expiración
    }

    // Método para extraer el email del token
    /**
     * Extrae el email del usuario almacenado en el token JWT.
     * @param token token JWT
     * @return email del usuario
     */
    public String extractEmail(String token){
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("email", String.class);    // En un claim tenemos guardado el email
        // return extractClaims(token).get("email").toString();
    }

    // Método para extraer id del token
    /**
     * Extrae el id de usuario almacenado en el subject del token JWT.
     * @param token token JWT
     * @return id de usuario Long
     */
    public Long extractUserId(String token){
        return Long.valueOf(
            Jwts.parser()
                .verifyWith(getSignInKey())   
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject()   // En el subject tenemos el id de usuario
        );
    }
    /**
     * Extrae el id del token para identificarlo
     * @param token
     * @return id de token String
     */
    public String extractJti(String token){
        return extractClaims(token).getId();
    }
}
