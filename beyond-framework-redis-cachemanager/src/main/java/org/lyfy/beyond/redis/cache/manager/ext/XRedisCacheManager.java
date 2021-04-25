package org.lyfy.beyond.redis.cache.manager.ext;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.serializer.*;

import java.util.Collection;
import java.util.Map;

/**
 * @author: 谢星星
 * @Date: 2019/7/11 19:11
 * @Description:
 */
public class XRedisCacheManager extends RedisCacheManager {

    private final RedisCacheWriter cacheWriter;

    public XRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
        this.cacheWriter = cacheWriter;
        resetSerializer(defaultCacheConfiguration);
    }

    public XRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, String... initialCacheNames) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheNames);
        this.cacheWriter = cacheWriter;
        resetSerializer(defaultCacheConfiguration);
    }

    public XRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, boolean allowInFlightCacheCreation, String... initialCacheNames) {
        super(cacheWriter, defaultCacheConfiguration, allowInFlightCacheCreation, initialCacheNames);
        this.cacheWriter = cacheWriter;
        resetSerializer(defaultCacheConfiguration);
    }

    public XRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, Map<String, RedisCacheConfiguration> initialCacheConfigurations) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations);
        this.cacheWriter = cacheWriter;
        resetSerializer(defaultCacheConfiguration);
        resetSerializer(initialCacheConfigurations.values());
    }

    public XRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, Map<String, RedisCacheConfiguration> initialCacheConfigurations, boolean allowInFlightCacheCreation) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations, allowInFlightCacheCreation);
        this.cacheWriter = cacheWriter;
        resetSerializer(defaultCacheConfiguration);
        resetSerializer(initialCacheConfigurations.values());
    }

    private void resetSerializer(Collection<RedisCacheConfiguration> redisCacheConfigurations) {
        redisCacheConfigurations.forEach(this::resetSerializer);
    }

    private void resetSerializer(RedisCacheConfiguration redisCacheConfiguration) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        RedisSerializer<Object> redisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        redisCacheConfiguration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer));
        redisCacheConfiguration.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()));
    }

    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        return new XRedisCache(name, this.cacheWriter, cacheConfig);
    }
}
