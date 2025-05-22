package wirebarley.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisLockUtils {

    private final StringRedisTemplate redisTemplate;
    private static final long LOCK_TIMEOUT_SECONDS = 5;
    private static final long LOCK_LEASE_TIME_SECONDS = 10;
    private static final long LOCK_RETRY_DELAY_MS = 100;

    public String acquireLockWithRetry(String lockKey) {
        String clientId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < LOCK_TIMEOUT_SECONDS * 1000) {
            boolean acquired = Objects.requireNonNull(redisTemplate.opsForValue().setIfAbsent(lockKey, clientId, Duration.ofSeconds(LOCK_LEASE_TIME_SECONDS)));
            if (acquired) {
                return clientId;
            }
            try {
                Thread.sleep(LOCK_RETRY_DELAY_MS);
            } catch (InterruptedException e) {
                return null;
            }
        }
        return null; // 타임아웃
    }

    public boolean releaseLock(String lockKey, String clientId) {
        String currentClientId = redisTemplate.opsForValue().get(lockKey);
        if (clientId != null && clientId.equals(currentClientId)) {
            return Boolean.TRUE.equals(redisTemplate.delete(lockKey));
        }
        return false;
    }

}
