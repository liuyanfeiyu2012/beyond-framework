package org.lyfy.beyond.annotation;

import org.lyfy.beyond.condition.matcher.OnNotLocalEnvCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnNotLocalEnvCondition.class)
public @interface ConditionOnNotLocalEnv {
}
