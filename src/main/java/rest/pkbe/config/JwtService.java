package rest.pkbe.config;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import rest.pkbe.domain.model.User;


@Service
public class JwtService {
    
@Value("${jwt.secret}")
private String secret;

@Value("${jwt.expiration}")
private long expirationMs;

    // Método para obtener la llave
    private SecretKey getSignInKey(){
        // decodificamos la secret key
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Método para generar tokens
    public String generateToken(User user){
        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("email", user.getEmail())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(getSignInKey(), Jwts.SIG.HS256)
            .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        // Sacamos el email del token
        final String emailFromToken = extractEmail(token);
        boolean isUserMatch = emailFromToken.equals(userDetails.getUsername());
        boolean isTokenExpired = isTokenExpired(token);
        return isUserMatch && !isTokenExpired;
    }

    private boolean isTokenExpired(String token) {
        return extracExpiration(token).before(new Date());
    }

    // Método para extraer todos los claims
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Método para extraer la fecha de expiración
    public Date extracExpiration(String token){
        return extractClaims(token).getExpiration();    // Extrae la expiración
    }

    // Método para extraer el email del token
    public String extractEmail(String token){
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("email", String.class);    // En un claim tenemos guardado el email
    }

    // Método para extraer id del token
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

}
