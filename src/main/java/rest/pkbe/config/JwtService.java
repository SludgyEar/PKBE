package rest.pkbe.config;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    // Método para extraer id del token
    public Long extractUserId(String token){
        return Long.valueOf(
            Jwts.parser()
                .verifyWith(getSignInKey())   
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject()
        );
    }

}
