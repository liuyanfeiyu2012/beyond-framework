package org.lyfy.beyond.redis.cache.manager.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

/**
 * @author: 谢星星
 * @Date: 2019/7/18 17:19
 * @Description:
 */
public class XRedisCache extends RedisCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(XRedisCache.class);

    private static final ThreadLocal<Boolean> THREAD_LOCAL = new ThreadLocal<>();

    public XRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
        super(name, cacheWriter, cacheConfig);
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper vw = null;
        try {
            vw = super.get(key);
            THREAD_LOCAL.remove();
        } catch (Exception e) {
            THREAD_LOCAL.set(Boolean.FALSE);
            //ignore, Maybe Alarm Later
            LOGGER.error("Can Not Get Cache From Redis, MayBe Lost Redis Connection.", e);
        }
        return vw;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        T t = null;
        try {
            t = super.get(key, type);
            THREAD_LOCAL.remove();
        } catch (Exception e) {
            THREAD_LOCAL.set(Boolean.FALSE);
            //ignore, Maybe Alarm Later
            LOGGER.error("Can Not Get Cache From Redis, MayBe Lost Redis Connection.", e);
        }

        return t;
    }

    @Override
    public void put(Object key, Object value) {
        Boolean connectionOk = THREAD_LOCAL.get();
        if (connectionOk == null || connectionOk) {
            try {
                super.put(key, value);
            } catch (Exception e) {
                LOGGER.error("Can Not Put Cache From Redis, MayBe Lost Redis Connection.", e);
            }
        }
    }
}
