package org.lyfy.beyond.ratelimiter.config.condition;


import org.lyfy.beyond.ratelimiter.constant.Constant;

public class GuavaLimitCondition extends TypeCondition {

    public GuavaLimitCondition() {
        super(Constant.LIMIT_TYPE, Constant.LIMIT_TYPE_GUAVA);
    }
}
