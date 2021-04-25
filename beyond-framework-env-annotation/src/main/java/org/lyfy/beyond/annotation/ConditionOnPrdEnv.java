package org.lyfy.beyond.annotation;

import org.lyfy.beyond.condition.matcher.OnPrdEnvCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnPrdEnvCondition.class)
public @interface ConditionOnPrdEnv {
}
