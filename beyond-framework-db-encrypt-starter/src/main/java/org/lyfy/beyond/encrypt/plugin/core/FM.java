package org.lyfy.beyond.encrypt.plugin.core;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author: 谢星星
 * @Date: 2019/7/1 10:56
 * @Description:
 */
public interface FM<T, R> extends Function<T, R>, Serializable {
}
