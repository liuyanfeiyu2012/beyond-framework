package org.lyfy.beyond.idworker.redis.util;

import org.lyfy.beyond.idworker.redis.UsefulFunctions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.util.Pool;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: 谢星星
 * @Date: 2019/7/1 14:30
 * @Description:
 */
public class SJedis2Util {

    private static final LinkedHashMap EMPTY_MAP = new LinkedHashMap();

    public static final Long SETNX_FAIL = 0L;

    @SuppressWarnings("unchecked")
    public static <T, P> LinkedHashMap<String, T> pipelinedBatchFunc(BiFunction<Pipeline, Map.Entry<String, P>, Response<T>> singleFunc,
                                                                     Pool<Jedis> pool,
                                                                     Map<String, P> keyParams) {
        if (null == singleFunc || null == pool || keyParams == null || MapUtils.isEmpty(keyParams)) {
            return EMPTY_MAP;
        }

        try (Jedis jedis = pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            Map<String, Response<T>> keyPinplineResponse = keyParams.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> singleFunc.apply(pipeline, entry)));
            pipeline.sync();
            return keyPinplineResponse.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(), UsefulFunctions.MapValueMerger.replaceOldWithNew(), LinkedHashMap::new));
        }
    }

    public static <T> T multiAndExec(Pool<Jedis> pool, Function<Transaction, T> transFunc) {
        if (null == pool || null == transFunc) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            Transaction transaction = jedis.multi();
            T result = transFunc.apply(transaction);
            transaction.exec();
            return result;
        }
    }

    public static <T> T watchAndMultiAndExec(Pool<Jedis> pool, Function<Transaction, T> transFunc,
                                             String... watchKeys) {
        if (null == pool || null == transFunc || null == watchKeys) {
            return null;
        }

        List<String> distinctWatchKeyList = Stream.of(watchKeys)
                .filter(UsefulFunctions.notBlankString)
                .map(StringUtils::stripToEmpty).distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(distinctWatchKeyList)) {
            return null;
        }

        String[] distinctKeys = distinctWatchKeyList.toArray(new String[0]);
        try (Jedis jedis = pool.getResource()) {
            jedis.watch(distinctKeys);
            Transaction transaction = jedis.multi();
            T result = transFunc.apply(transaction);
            transaction.exec();
            return result;
        }
    }

    public static boolean exists(Pool<Jedis> pool, String key) {
        if (null == pool || StringUtils.isBlank(key)) {
            return false;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.exists(StringUtils.stripToEmpty(key));
        }
    }

    public static Long expire(Pool<Jedis> pool, String key, int seconds) {
        if (null == pool || StringUtils.isBlank(key) || 0 > seconds) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.expire(StringUtils.stripToEmpty(key), seconds);
        }
    }

    public static Long pexpire(Pool<Jedis> pool, String key, long milliSeconds) {
        if (null == pool || StringUtils.isBlank(key) || 0 > milliSeconds) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.pexpire(StringUtils.stripToEmpty(key), milliSeconds);
        }
    }

    public static Long ttl(Pool<Jedis> pool, String key) {
        if (null == pool || StringUtils.isBlank(key)) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.ttl(StringUtils.stripToEmpty(key));
        }
    }

    public static Long incr(Pool<Jedis> pool, String key) {
        if (null == pool || StringUtils.isBlank(key)) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.incr(StringUtils.stripToEmpty(key));
        }
    }

    public static String get(Pool<Jedis> pool, String key) {
        if (null == pool || StringUtils.isBlank(key)) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.get(StringUtils.stripToEmpty(key));
        }
    }

    @SuppressWarnings("unchecked")
    public static LinkedHashMap<String, String> mget(Pool<Jedis> pool, String... keys) {
        if (null == pool || null == keys) {
            return EMPTY_MAP;
        }

        List<String> distinctKeyList = Stream.of(keys)
                .filter(UsefulFunctions.notBlankString)
                .map(StringUtils::stripToEmpty).distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(distinctKeyList)) {
            return EMPTY_MAP;
        }

        String[] distinctKeys = distinctKeyList.toArray(new String[0]);
        try (Jedis jedis = pool.getResource()) {
            List<String> valueList = jedis.mget(distinctKeys);
            return distinctKeyList.stream()
                    .map(key -> Pair.of(key, valueList.get(distinctKeyList.indexOf(key))))
                    .filter(pair -> null != pair.getRight())
                    .collect(Collectors.toMap(Pair::getLeft, Pair::getRight, UsefulFunctions.MapValueMerger.replaceOldWithNew(), LinkedHashMap::new));
        }
    }

    public static String set(Pool<Jedis> pool, String key, String value) {
        if (null == pool || StringUtils.isBlank(key) || null == value) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.set(StringUtils.stripToEmpty(key), value);
        }
    }

    public static Long setnx(Pool<Jedis> pool, String key, String value) {
        if (null == pool || StringUtils.isBlank(key) || null == value) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.setnx(StringUtils.stripToEmpty(key), value);
        }
    }

    public static String setnxex(Pool<Jedis> pool, String key, String value, int expireSeconds) {
        if (null == pool || StringUtils.isBlank(key) || null == value) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.set(StringUtils.stripToEmpty(key), value, SetParams.setParams().nx().ex(expireSeconds));
        }
    }

    public static String setnxpx(Pool<Jedis> pool, String key, String value, long expireMilliSeconds) {
        if (null == pool || StringUtils.isBlank(key) || null == value) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.set(StringUtils.stripToEmpty(key), value, SetParams.setParams().nx().px(expireMilliSeconds));
        }
    }

    public static String setex(Pool<Jedis> pool, String key, int expireSeconds, String value) {
        if (null == pool || StringUtils.isBlank(key) || 0 > expireSeconds || null == value) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.setex(StringUtils.stripToEmpty(key), expireSeconds, value);
        }
    }

    public static String psetex(Pool<Jedis> pool, String key, long expireMilliSeconds, String value) {
        if (null == pool || StringUtils.isBlank(key) || 0 > expireMilliSeconds || null == value) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.psetex(StringUtils.stripToEmpty(key), expireMilliSeconds, value);
        }
    }

    public static String mset(Pool<Jedis> pool, Map<String, String> key_value) {
        if (null == pool || MapUtils.isEmpty(key_value)) {
            return null;
        }

        String[] arr = convertKVMap2Array4Mset(key_value);
        try (Jedis jedis = pool.getResource()) {
            return jedis.mset(arr);
        }
    }

    private static String[] convertKVMap2Array4Mset(Map<String, String> keyValue) {
        return keyValue.entrySet().stream()
                .filter(entry -> StringUtils.isNotBlank(entry.getKey()) && null != entry.getValue())
                .flatMap(entry -> Stream.of(StringUtils.stripToEmpty(entry.getKey()), entry.getValue()))
                .toArray(String[]::new);
    }

    public static Long del(Pool<Jedis> pool, String... keys) {
        if (null == pool || null == keys) {
            return null;
        }

        Set<String> distinctKeySet = Stream.of(keys).filter(UsefulFunctions.notBlankString).map(StringUtils::stripToEmpty)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(distinctKeySet)) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.del(distinctKeySet.toArray(new String[0]));
        }
    }

    public static String hget(Pool<Jedis> pool, String key, String field) {
        if (null == pool || StringUtils.isBlank(key) || StringUtils.isBlank(field)) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.hget(StringUtils.stripToEmpty(key), StringUtils.stripToEmpty(field));
        }
    }

    public static Map<String, String> hgetAll(Pool<Jedis> pool, String key) {
        if (null == pool || StringUtils.isBlank(key)) {
            return Collections.emptyMap();
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.hgetAll(key);
        }
    }

    @SuppressWarnings("unchecked")
    public static LinkedHashMap<String, String> hmget(Pool<Jedis> pool, String key, Set<String> fieldSet) {
        if (null == pool || StringUtils.isBlank(key) || CollectionUtils.isEmpty(fieldSet)) {
            return EMPTY_MAP;
        }

        List<String> distinctFieldList = fieldSet.stream()
                .filter(StringUtils::isNotBlank).map(StringUtils::stripToEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(distinctFieldList)) {
            return EMPTY_MAP;
        }

        String[] distinctFields = distinctFieldList.toArray(new String[0]);
        try (Jedis jedis = pool.getResource()) {
            List<String> valueList = jedis.hmget(key, distinctFields);
            return distinctFieldList.stream().map(field -> Pair.of(field, valueList.get(distinctFieldList.indexOf(field))))
                    .filter(pair -> null != pair.getRight()).collect(Collectors.toMap(Pair::getLeft, Pair::getRight,
                            UsefulFunctions.MapValueMerger.replaceOldWithNew(), LinkedHashMap::new));
        }
    }

    public static Long hset(Pool<Jedis> pool, String key, String field, String value) {
        if (null == pool || StringUtils.isBlank(key) || StringUtils.isBlank(field) || null == value) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.hset(StringUtils.stripToEmpty(key), StringUtils.stripToEmpty(field), value);
        }
    }

    public static String hmset(Pool<Jedis> pool, String key, Map<String, String> field_value) {
        if (null == pool || StringUtils.isBlank(key)) {
            return null;
        }

        Map<String, String> cleanedMap = field_value.entrySet().stream().filter(entry -> StringUtils.isNotBlank(entry.getKey()) && null != entry.getValue())
                .collect(Collectors.toMap(entry -> StringUtils.stripToEmpty(entry.getKey()), Map.Entry::getValue, (oldValue, newValue) -> newValue));
        try (Jedis jedis = pool.getResource()) {
            return jedis.hmset(key, cleanedMap);
        }
    }

    public static Long hdel(Pool<Jedis> pool, String key, Set<String> fieldSet) {
        if (null == pool || StringUtils.isBlank(key) || CollectionUtils.isEmpty(fieldSet)) {
            return null;
        }

        List<String> distinctFieldList = fieldSet.stream().filter(StringUtils::isNotBlank).map(StringUtils::stripToEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(distinctFieldList)) {
            return null;
        }

        String[] distinctFields = distinctFieldList.toArray(new String[distinctFieldList.size()]);
        try (Jedis jedis = pool.getResource()) {
            return jedis.hdel(key, distinctFields);
        }
    }

    private static final String STAR = "*";

    public static Set<String> keys(Pool<Jedis> pool, String pattern) {
        if (null == pool || StringUtils.isBlank(pattern)) {
            return Collections.emptySet();
        }

        pattern = StringUtils.stripToEmpty(pattern);
        try (Jedis jedis = pool.getResource()) {
            return jedis.keys(pattern.endsWith(STAR) ? pattern : pattern + STAR);
        }
    }

    public static Set<String> hkeys(Pool<Jedis> pool, String key) {
        if (null == pool || StringUtils.isBlank(key)) {
            return Collections.emptySet();
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.hkeys(StringUtils.stripToEmpty(key));
        }
    }

    public static Long hincrBy(Pool<Jedis> pool, String key, String field, long value) {
        if (null == pool || StringUtils.isBlank(key)) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.hincrBy(key, field, value);
        }
    }

    public static String getSet(Pool<Jedis> pool, String key, String value) {
        if (null == pool || StringUtils.isBlank(key)) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.getSet(key, value);
        }
    }

    public static Long zadd(Pool<Jedis> pool, String key, double score, String member) {
        if (null == pool || StringUtils.isBlank(key) || StringUtils.isBlank(member)) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.zadd(StringUtils.stripToEmpty(key), score, StringUtils.stripToEmpty(member));
        }
    }

    public static Long zrank(Pool<Jedis> pool, String key, String member) {
        if (null == pool || StringUtils.isBlank(key) || StringUtils.isBlank(member)) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.zrank(StringUtils.stripToEmpty(key), StringUtils.stripToEmpty(member));
        }
    }

    public static Long rpush(Pool<Jedis> pool, String key, String... values) {
        if (null == pool || StringUtils.isBlank(key)) {
            return null;
        }

        LinkedHashSet<String> distinctValueSet = Stream.of(values).filter(StringUtils::isNotBlank).map(StringUtils::stripToEmpty).collect(Collectors.toCollection(LinkedHashSet::new));
        if (CollectionUtils.isEmpty(distinctValueSet)) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.rpush(StringUtils.stripToEmpty(key), distinctValueSet.toArray(new String[0]));
        }
    }

    public static List<String> lrange(Pool<Jedis> pool, String key, long start, long stop) {
        if (null == pool || StringUtils.isBlank(key)) {
            return Collections.emptyList();
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.lrange(StringUtils.stripToEmpty(key), start, stop);
        }
    }

    public static Long sadd(Pool<Jedis> pool, String key, String... members) {
        if (null == pool || StringUtils.isBlank(key)) {
            return null;
        }

        Set<String> distinctMemberSet = Stream.of(members).filter(StringUtils::isNotBlank).map(StringUtils::stripToEmpty).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(distinctMemberSet)) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.sadd(StringUtils.stripToEmpty(key), distinctMemberSet.toArray(new String[0]));
        }
    }

    public static Set<String> smembers(Pool<Jedis> pool, String key) {
        if (null == pool || StringUtils.isBlank(key)) {
            return Collections.emptySet();
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.smembers(StringUtils.stripToEmpty(key));
        }
    }

    public static boolean sismember(Pool<Jedis> pool, String key, String member) {
        if (null == pool || StringUtils.isBlank(key) || null == member) {
            return false;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.sismember(StringUtils.stripToEmpty(key), member);
        }
    }

    public static Long srem(Pool<Jedis> pool, String key, String... members) {
        if (null == pool || StringUtils.isBlank(key)) {
            return null;
        }

        Set<String> distinctMemberSet = Stream.of(members).filter(StringUtils::isNotBlank).map(StringUtils::stripToEmpty).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(distinctMemberSet)) {
            return null;
        }

        try (Jedis jedis = pool.getResource()) {
            return jedis.srem(StringUtils.stripToEmpty(key), distinctMemberSet.toArray(new String[0]));
        }
    }
}

