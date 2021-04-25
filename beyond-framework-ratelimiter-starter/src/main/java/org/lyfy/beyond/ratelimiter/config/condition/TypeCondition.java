package org.lyfy.beyond.ratelimiter.config.condition;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class TypeCondition implements Condition {

    private String key;

    private String value;

    public TypeCondition(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String value = context.getEnvironment().getProperty(key);
        if (value == null) {
            return false;
        }
        return value.equals(this.value);
    }
}
