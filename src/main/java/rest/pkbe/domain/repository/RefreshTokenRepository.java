package rest.pkbe.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rest.pkbe.domain.model.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{
    
    Optional<RefreshToken> findByTokenAndUserId(String token, Long userId);
}
