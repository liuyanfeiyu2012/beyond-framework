package org.lyfy.beyond.encrypt.plugin.core;

/**
 * @author: 谢星星
 * @Date: 2019/12/2 14:44
 * @Description:
 */
public class DefalutFieldWrapperService implements FieldWrapperService {

    @Override
    public String filedWrap(String rawValue) {
        return rawValue;
    }

    @Override
    public String filedUnwrap(String wrappedValue) {
        return wrappedValue;
    }
}
