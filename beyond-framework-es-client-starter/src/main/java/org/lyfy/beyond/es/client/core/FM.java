package org.lyfy.beyond.es.client.core;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author: 谢星星
 * @date: 2019/12/18 10:57
 * @description:
 */
@FunctionalInterface
public interface FM<T, R> extends Function<T, R>, Serializable {
}