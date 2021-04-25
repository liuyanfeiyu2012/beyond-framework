package org.lyfy.beyond.ratelimiter.aop;

import org.lyfy.beyond.ratelimiter.annotation.RateLimit;
import org.lyfy.beyond.ratelimiter.config.condition.AopLimitCondition;
import org.lyfy.beyond.ratelimiter.entity.RateLimitEntity;
import org.lyfy.beyond.ratelimiter.exception.RateLimitException;
import org.lyfy.beyond.ratelimiter.limit.RateLimiterExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
@Slf4j
@Configuration
@Conditional(AopLimitCondition.class)
public class Aop {
    private static final String DEFAULT_EXPRESSION_PREFIX = "#";

    @Autowired
    private RateLimiterExecutor rateLimiterExecutor;

    @Pointcut("@annotation(org.lyfy.beyond.ratelimiter.annotation.RateLimit)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void around(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Annotation[] methodAnnotations = method.getDeclaredAnnotations();
        for (Annotation annotation : methodAnnotations) {
            if (annotation instanceof RateLimit) {
                RateLimit rateLimit = (RateLimit) annotation;
                RateLimitEntity entity = new RateLimitEntity();
                entity.setLimitNum(rateLimit.limitNum());
                entity.setKey(getKey(rateLimit, signature, joinPoint.getArgs()));
                entity.setSeconds(rateLimit.seconds());
                if (!rateLimiterExecutor.tryAccess(entity)) {
                    throw new RateLimitException("Frequency Limited, Please Try Later!");
                }
            }
        }
    }

    private String getKey(RateLimit rateLimit, MethodSignature signature, Object[] arguments) {
        String annoKey = rateLimit.key();
        if (StringUtils.isNotEmpty(annoKey)) {
            String targetKey;
            if (!annoKey.startsWith(DEFAULT_EXPRESSION_PREFIX)) {
                if (!annoKey.contains(DEFAULT_EXPRESSION_PREFIX)) {
                    targetKey = annoKey;
                } else {
                    int index = annoKey.indexOf(DEFAULT_EXPRESSION_PREFIX);
                    String prefix = annoKey.substring(0, index);
                    String keySuffix = annoKey.substring(index);
                    targetKey = prefix + parseElKey(signature, arguments, keySuffix);
                }
            } else {
                targetKey = parseElKey(signature, arguments, annoKey);
            }
            return signature.getMethod().getDeclaringClass().getName() + "." + signature.getMethod().getName() + "." + targetKey;
        } else {
            return signature.getMethod().getDeclaringClass().getName() + "." + signature.getMethod().getName();
        }
    }


    private String parseElKey(MethodSignature signature, Object[] arguments, String sourceKey) {
        String[] paramNames = getParamNames(signature.getMethod());
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(sourceKey, null);
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < arguments.length; i++) {
            context.setVariable(paramNames[i], arguments[i]);
        }
        return expression.getValue(context, String.class);
    }

    private String[] getParamNames(Method method) {
        LocalVariableTableParameterNameDiscoverer paramTable = new LocalVariableTableParameterNameDiscoverer();
        return paramTable.getParameterNames(method);
    }

}
