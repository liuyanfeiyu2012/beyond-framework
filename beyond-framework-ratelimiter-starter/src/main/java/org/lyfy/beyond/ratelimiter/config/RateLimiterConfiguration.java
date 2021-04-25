package org.lyfy.beyond.ratelimiter.config;


import org.lyfy.beyond.ratelimiter.config.condition.GuavaLimitCondition;
import org.lyfy.beyond.ratelimiter.config.condition.RedisLimitCondition;
import org.lyfy.beyond.ratelimiter.limit.GuavaRateLimiterExecutor;
import org.lyfy.beyond.ratelimiter.limit.RateLimiterExecutor;
import org.lyfy.beyond.ratelimiter.limit.RedisRateLimiterExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RateLimiterConfiguration {

    private static final String RATELIMIT_REDIS_TEMPLATE_NAME = "ratelimitStringRedisTemplate";

    @Bean
    @Conditional(GuavaLimitCondition.class)
    public RateLimiterExecutor guavaLimiterExecutor() {
        return new GuavaRateLimiterExecutor();
    }

    /**
     * 使用redis类型的限流，必须在应用中声明一个名为"ratelimitStringRedisTemplate"的StringRedisTemplate Bean.
     */
    @Bean
    @Conditional(RedisLimitCondition.class)
    @ConditionalOnBean(StringRedisTemplate.class)
    public RateLimiterExecutor redisLimiterExecutor(@Qualifier(RATELIMIT_REDIS_TEMPLATE_NAME) StringRedisTemplate stringRedisTemplate) {
        return new RedisRateLimiterExecutor(stringRedisTemplate);
    }
}
