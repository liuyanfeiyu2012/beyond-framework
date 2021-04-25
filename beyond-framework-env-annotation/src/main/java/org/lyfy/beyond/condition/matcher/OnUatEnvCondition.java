package org.lyfy.beyond.condition.matcher;

import org.lyfy.beyond.util.EnvUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OnUatEnvCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        return EnvUtil.isUatEnv();
    }
}
