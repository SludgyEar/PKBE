package rest.pkbe.domain.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import rest.pkbe.domain.model.BlacklistedToken;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, String>{
    boolean existsById(@NonNull String jti);
    void deleteByExpirationDateBefore(LocalDateTime now);
}
