package org.lyfy.beyond.annotation;

import org.lyfy.beyond.condition.matcher.OnNotPreEnvCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnNotPreEnvCondition.class)
public @interface ConditionOnNotPreEnv {
}
