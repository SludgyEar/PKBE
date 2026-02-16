package rest.pkbe.config.tasks;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import rest.pkbe.domain.repository.BlacklistedTokenRepository;

@Component
@RequiredArgsConstructor
public class BlackListCleanupTask {
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Scheduled(cron = "0 0 */2 * * *")
    @Transactional
    public void cleanExpiredTokens(){
        blacklistedTokenRepository.deleteByExpirationDateBefore(LocalDateTime.now());
    }
}
