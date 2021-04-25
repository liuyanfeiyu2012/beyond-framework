package org.lyfy.beyond.annotation;

import org.lyfy.beyond.condition.matcher.OnNotUatEnvCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnNotUatEnvCondition.class)
public @interface ConditionOnNotUatEnv {
}
