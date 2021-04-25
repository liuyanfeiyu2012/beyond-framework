package org.lyfy.beyond.ratelimiter.limit;

import org.lyfy.beyond.ratelimiter.entity.RateLimitEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RedisRateLimiterExecutor implements RateLimiterExecutor {

    private static final String LIMIT_LUA_SCRIPT = " local key = KEYS[1]"
            + "\nlocal limit = tonumber(ARGV[1])"
            + "\nlocal curentLimit = tonumber(redis.call('get', key) or \"0\")"
            + "\nif curentLimit + 1 > limit then"
            + "\nreturn 0"
            + "\nelse"
            + "\n redis.call(\"INCRBY\", key, 1)"
            + "\nredis.call(\"EXPIRE\", key, ARGV[2])"
            + "\nreturn curentLimit + 1"
            + "\nend";

    private StringRedisTemplate stringRedisTemplate;

    public RedisRateLimiterExecutor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean tryAccess(RateLimitEntity limitEntity) {
        String key = limitEntity.getKey();
        int seconds = limitEntity.getSeconds();
        int limitCount = limitEntity.getLimitNum();
        List<String> keys = new ArrayList<>();
        keys.add(key);
        RedisScript<Long> redisScript = new DefaultRedisScript<>(LIMIT_LUA_SCRIPT, Long.class);
        Long count = stringRedisTemplate.execute(redisScript, keys, "" + limitCount, "" + seconds);
        log.debug("Access Try Count Is {}, For Key={}", count, key);
        return count != null && count != 0;
    }
}
