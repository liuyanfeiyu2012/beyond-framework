package org.lyfy.beyond.distributed.lock.advice;

import org.lyfy.beyond.distributed.lock.anno.DistributedLock;
import org.lyfy.beyond.distributed.lock.properties.LockProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Configuration
@ConditionalOnClass({Redisson.class})
@ConditionalOnBean(RedissonClient.class)
@EnableConfigurationProperties(LockProperties.class)
public class LockAspectAutoConfiguration {

    private static Logger LOGGER = LoggerFactory.getLogger(LockAspectAutoConfiguration.class);

    public static final String DEFAULT_EXPRESSION_PREFIX = "#";

    @Autowired
    @Qualifier("redissonLockClient")
    private RedissonClient redissonClient;

    @Autowired
    private LockProperties lockProperties;

    @Pointcut("@annotation(org.lyfy.beyond.distributed.lock.anno.DistributedLock)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        if (!lockProperties.isEnable() || redissonClient == null) {
            return point.proceed(point.getArgs());
        }
        Object[] arguments = point.getArgs();
        MethodSignature signature = (MethodSignature) point.getSignature();
        DistributedLock annotation = signature.getMethod().getAnnotation(DistributedLock.class);
        String key = annotation.key();
        String lockKey;
        if (!key.startsWith(DEFAULT_EXPRESSION_PREFIX)) {
            if (!key.contains(DEFAULT_EXPRESSION_PREFIX)) {
                lockKey = key;
            } else {
                int index = key.indexOf(DEFAULT_EXPRESSION_PREFIX);
                String prefix = key.substring(0, index);
                String keySuffix = key.substring(index);
                lockKey = prefix + parseElKey(signature, arguments, keySuffix);
            }
        } else {
            lockKey = parseElKey(signature, arguments, key);
        }

        int waitTime = annotation.waitTime();
        int leaseTime = annotation.leaseTime();

        try {
            tryLock(lockKey, waitTime, leaseTime);
            return point.proceed(arguments);
        } finally {
            unlock(lockKey);
        }
    }

    private void unlock(String lockKey) {
        try {
            RLock lock = redissonClient.getLock(lockKey);
            lock.unlock();
            LOGGER.debug("UnLock distributed lock successfully, lockKey:{}", lockKey);
        } catch (Exception e) {
            LOGGER.error("Can not unlock, lockKey:{}, e:{}", lockKey, e);
        }
    }

    private void tryLock(String lockKey, int waitTime, int leaseTime) {
        try {
            boolean lock = redissonClient.getLock(lockKey).tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            LOGGER.debug("Fetch distributed lock state:{}, lockKey:{}", lock, lockKey);
            if (!lock) {
                throw new RuntimeException("Fetch lock error, lockKey:" + lockKey);
            }
        } catch (Exception e) {
            LOGGER.error("Lock err,lockKey:{}, e:{}", lockKey, e);
            throw new RuntimeException(e);
        }
    }


    private String[] getParamNames(Method method) {
        LocalVariableTableParameterNameDiscoverer paramTable = new LocalVariableTableParameterNameDiscoverer();
        return paramTable.getParameterNames(method);
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
}
