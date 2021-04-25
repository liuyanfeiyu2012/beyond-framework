package org.lyfy.beyond.encrypt.plugin.core;

import org.lyfy.beyond.encrypt.plugin.util.AESUtil;

/**
 * @author: 谢星星
 * @Date: 2019/12/2 14:44
 * @Description:
 */
@Deprecated
public class AesFieldWrapperService implements FieldWrapperService {

    @Override
    public String filedWrap(String rawValue) throws Exception {
        return AESUtil.encryptAES(rawValue, AESUtil.getDBFieldEncrtptKey());
    }

    @Override
    public String filedUnwrap(String wrappedValue) throws Exception {
        return AESUtil.decryptAES(wrappedValue, AESUtil.getDBFieldEncrtptKey());
    }
}
