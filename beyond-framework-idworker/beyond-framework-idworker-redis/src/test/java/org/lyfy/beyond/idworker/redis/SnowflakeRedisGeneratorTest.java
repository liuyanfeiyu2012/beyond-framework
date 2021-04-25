package org.lyfy.beyond.idworker.redis;

import org.apache.commons.lang3.time.DateUtils;
import org.lyfy.beyond.idworker.service.IdWorker;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.util.Pool;

/**
 * @Auther: 谢星星
 * @Date: 2019/7/1 15:07
 * @Description:
 */
public class SnowflakeRedisGeneratorTest {

    private static final String IP = "localhost";
    private static final Integer PORT = 6379;

    private static JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMinIdle(2);
        jedisPoolConfig.setMaxWaitMillis(DateUtils.MILLIS_PER_SECOND * 10);
        jedisPoolConfig.setNumTestsPerEvictionRun(10);
        jedisPoolConfig.setBlockWhenExhausted(true);
        return jedisPoolConfig;
    }

    private static Pool<Jedis> jedisPool4() {
        return new JedisPool(jedisPoolConfig(), IP, PORT);
    }

    public static void main(String[] args) throws Exception {
        IdWorker generator = new SnowflakeIdRedisGenerator(jedisPool4());
        Long start = System.currentTimeMillis();
        System.out.println("Start timestamp: " + start);
        for (int i = 0; i < 50000; i++) {
            System.out.println(generator.nextId());
        }

        Long end = System.currentTimeMillis();
        System.out.println("End timestamp: " + end);
        System.out.println("Total cost: " + (end - start));
    }
}
