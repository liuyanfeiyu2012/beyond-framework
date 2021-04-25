package org.lyfy.beyond.encrypt.plugin.core;

import java.util.Set;

public interface DBSchemaEncryptRepo {

    Set<String> getEncryptionFieldStringSet();

    <T, R> DBSchemaEncryptRepo addField(FM<T, R> fm);

    void setFieldWrapperService(FieldWrapperService service);

    FieldWrapperService getFieldWrapperService();

}
