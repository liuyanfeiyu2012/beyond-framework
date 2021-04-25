package org.lyfy.beyond.ratelimiter.config.condition;

import org.lyfy.beyond.ratelimiter.constant.Constant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;


public class AopLimitCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String limitType = conditionContext.getEnvironment().getProperty(Constant.LIMIT_TYPE);
        return StringUtils.isNotEmpty(limitType);
    }
}
