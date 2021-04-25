package org.lyfy.beyond.ratelimiter.config.condition;


import org.lyfy.beyond.ratelimiter.constant.Constant;

public class RedisLimitCondition extends TypeCondition {

    public RedisLimitCondition() {
        super(Constant.LIMIT_TYPE, Constant.LIMIT_TYPE_REDIS);
    }
}
