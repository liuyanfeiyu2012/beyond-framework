package org.lyfy.beyond.idworker.redis;

import org.lyfy.beyond.idworker.redis.util.SIpUtil;
import org.lyfy.beyond.idworker.redis.util.SJedis2Util;
import org.apache.commons.lang3.StringUtils;
import org.lyfy.beyond.idworker.service.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.util.Pool;

import java.net.InetAddress;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author: 谢星星
 * @Date: 2019/7/1 14:23
 * @Description:
 */
@Deprecated
public final class SnowflakeIdRedisGenerator implements IdWorker {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnowflakeIdRedisGenerator.class);

    private static long sequence = 0L;
    private static long lastTimestamp = -1L;
    private static final ReentrantLock LOCK = new ReentrantLock();
    private final short workId;

    public SnowflakeIdRedisGenerator(Pool<Jedis> jedisPool) {
        String privateIPv4 = SIpUtil.getAllLocalPrivateInet4AddressList().stream().map(InetAddress::getHostAddress).filter(UsefulFunctions.notBlankString).findAny().orElse((null));
        if (StringUtils.isBlank(privateIPv4)) {
            System.exit(-1);
        }

        String workIdUK = privateIPv4 + "_" + SnowflakeIdRedisGenerator.class.getResource("").getPath();
        String hashKey = SnowflakeIdRedisGenerator.class.getSimpleName();
        String lockKey = SnowflakeIdRedisGenerator.class.getSimpleName() + "Lock";
        Set<Long> legalWordIdSet = LongStream.range(0L, 1023L).boxed().collect(Collectors.toSet());
        Long workId = Optional.ofNullable(SJedis2Util.hget(jedisPool, hashKey, workIdUK)).map(Long::valueOf).orElse(null);
        if (null == workId || !legalWordIdSet.contains(workId)) {
            while (SJedis2Util.SETNX_FAIL.equals(SJedis2Util.setnx(jedisPool, lockKey, workIdUK))) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    LOGGER.warn("Error occur.", e);
                }
            }
            SJedis2Util.expire(jedisPool, lockKey, 30);
            Set<Long> usedWordIdSet = SJedis2Util.hgetAll(jedisPool, hashKey).values().stream().mapToLong(Long::valueOf).boxed().collect(Collectors.toSet());
            workId = legalWordIdSet.stream().filter((i) -> !usedWordIdSet.contains(i)).mapToLong(Long::longValue).min().orElse(9223372036854775807L);
        }

        if (!legalWordIdSet.contains(workId)) {
            SJedis2Util.del(jedisPool, lockKey);
            System.exit(-555555555);
        } else {
            SJedis2Util.hset(jedisPool, hashKey, workIdUK, String.valueOf(workId));
            SJedis2Util.del(jedisPool, lockKey);
        }
        this.workId = workId.shortValue();
    }

    @Override
    public long nextId() {
        long nextId;
        LOCK.lock();
        try {
            long timeStamp = timeGen();
            if (lastTimestamp == timeStamp) {
                sequence = sequence + 1L & 4095L;
                if (sequence == 0L) {
                    timeStamp = tillNextMills(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }
            if (timeStamp < lastTimestamp) {
                throw new RuntimeException("system clock fall back");
            }
            lastTimestamp = timeStamp;
            nextId = timeStamp - 1429113600000L << 22 | (long) (this.workId << 12) | sequence;
        } finally {
            LOCK.unlock();
        }
        return nextId;
    }

    private static long timeGen() {
        return System.currentTimeMillis();
    }

    private static long tillNextMills(long lastTimestamp) {
        long timeStamp;
        //noinspection StatementWithEmptyBody
        for (timeStamp = timeGen(); timeStamp <= lastTimestamp; timeStamp = timeGen()) {
        }
        return timeStamp;
    }
}
