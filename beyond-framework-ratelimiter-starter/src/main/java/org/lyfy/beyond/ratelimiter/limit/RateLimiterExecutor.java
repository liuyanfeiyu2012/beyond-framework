package org.lyfy.beyond.ratelimiter.limit;


import org.lyfy.beyond.ratelimiter.entity.RateLimitEntity;

public interface RateLimiterExecutor {

    boolean tryAccess(RateLimitEntity limitEntity);

}
