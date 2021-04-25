package org.lyfy.beyond.ratelimiter.limit;

import org.lyfy.beyond.ratelimiter.entity.RateLimitEntity;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
public class GuavaRateLimiterExecutor implements RateLimiterExecutor {

    private Map<String, RateLimiter> rateLimiterMap = Maps.newConcurrentMap();

    @Override
    public boolean tryAccess(RateLimitEntity limitEntity) {
        RateLimiter rateLimiter = getRateLimiter(limitEntity);
        if (rateLimiter == null) {
            return false;
        }
        boolean access = rateLimiter.tryAcquire(1);
        log.debug(limitEntity.getKey() + " access:{}", access);
        return access;
    }

    private RateLimiter getRateLimiter(RateLimitEntity limitEntity) {
        if (limitEntity == null) {
            return null;
        }
        String key = limitEntity.getKey();
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        RateLimiter rl = rateLimiterMap.get(key);
        if (rl == null) {
            Double limitNum = Double.valueOf(String.valueOf(limitEntity.getLimitNum()));
            Double permitsPerSecond = limitNum / limitEntity.getSeconds();
            RateLimiter newRateLimiter = RateLimiter.create(permitsPerSecond);
            rl = rateLimiterMap.putIfAbsent(key, newRateLimiter);
            if (rl == null) {
                rl = newRateLimiter;
            }
        }
        return rl;
    }
}
