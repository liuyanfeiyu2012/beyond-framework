package org.lyfy.beyond.idworker.redis;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.*;

/**
 * @author: 谢星星
 * @Date: 2019/7/1 14:45
 * @Description:
 */
public class UsefulFunctions {

    public static final UnaryOperator<Predicate<Object>> not = predicate -> predicate.negate();
    public static final Predicate<Object> isNull = object -> null == object;
    public static final Predicate<String> isBlankString = string -> StringUtils.isBlank(string);
    public static final Predicate<Object[]> isEmptyArray = array -> ArrayUtils.isEmpty(array);
    public static final Predicate<Collection> isEmptyCollection = collection -> CollectionUtils.isEmpty(collection);
    public static final Predicate<Map> isEmptyMap = map -> MapUtils.isEmpty(map);
    public static final Predicate<Object> notNull = object -> null != object;
    public static final Predicate<String> notBlankString = string -> StringUtils.isNotBlank(string);
    public static final Predicate<Object[]> notEmptyArray = array -> !ArrayUtils.isEmpty(array);
    public static final Predicate<Collection> notEmptyCollection = collection -> !CollectionUtils.isEmpty(collection);
    public static final Predicate<Map> notEmptyMap = map -> !MapUtils.isEmpty(map);
    public static final Predicate<Object> whateverTrue = object -> true;
    public static final Predicate<Object> whateverFalse = object -> false;
    public static final Function<Optional, Object> orElseNull = opt -> opt.orElse(null);
    public static final Supplier<Boolean> getTrue = () -> true;
    public static final Supplier<Boolean> getFalse = () -> false;
    public static final Supplier<Object> getNull = () -> null;

    public static final <O> Optional<O> nullOptional() {
        return (Optional<O>) Optional.ofNullable(null);
    }

    public static final <O, P, R> Function<O, R> apply(BiFunction<O, P, R> classFunction, P param) {
        return obj -> classFunction.apply(obj, param);
    }

    public static final <O, P> Consumer<O> accept(BiConsumer<O, P> classConsumer, P param) {
        return obj -> classConsumer.accept(obj, param);
    }

    public static final <O, P> Predicate<O> test(BiPredicate<O, P> classPredicateFunction, P param) {
        return obj -> classPredicateFunction.test(obj, param);
    }

    public static final <T> Function<T, T> consumeAndReturn(Consumer<T> tConsumer) {
        return t -> {
            tConsumer.accept(t);
            return t;
        };
    }

    public static class MapValueMerger {
        public static final <T> BinaryOperator<T> replaceOldWithNew() {
            return (oldValue, newValue) -> newValue;
        }

        public static final <T> BinaryOperator<T> retainOldDiscardNew() {
            return (oldValue, newValue) -> oldValue;
        }
    }
}
