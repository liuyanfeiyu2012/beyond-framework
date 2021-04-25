package org.lyfy.beyond.encrypt.plugin.core;

/**
 * @author: 谢星星
 * @Date: 2019/12/2 14:35
 * @Description:
 */
public interface FieldWrapperService {

    String filedWrap(String rawValue) throws Exception;

    String filedUnwrap(String wrappedValue) throws Exception;
}
