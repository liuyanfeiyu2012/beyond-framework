package org.lyfy.beyond.encrypt.plugin.core;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author: 谢星星
 * @Date: 2019/7/1 10:32
 * @Description:
 */
public class SimpleDBSchemaEncryptRepo implements DBSchemaEncryptRepo {

    private static final Pattern GETTER_PATTERN = Pattern.compile("^get[A-Z].*");

    private static final Pattern IS_PATTERN = Pattern.compile("^is[A-Z].*");

    private Set<String> encryptionFieldStringSet = new HashSet<>();

    private FieldWrapperService fieldWrapperService = new DefalutFieldWrapperService();

    public SimpleDBSchemaEncryptRepo() {
    }

    public SimpleDBSchemaEncryptRepo(FieldWrapperService fieldWrapperService) {
        this.fieldWrapperService = fieldWrapperService;
    }

    @Override
    public <T, R> DBSchemaEncryptRepo addField(FM<T, R> fm) {
        try {
            Method method = fm.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(fm);
            String clazzName = serializedLambda.getImplClass();

            String getter = serializedLambda.getImplMethodName();
            if (GETTER_PATTERN.matcher(getter).matches()) {
                getter = getter.substring(3);
            } else if (IS_PATTERN.matcher(getter).matches()) {
                getter = getter.substring(2);
            }
            Class clazz = Class.forName(clazzName.replaceAll("\\/", "."));
            String simpleClassName = clazz.getSimpleName();
            encryptionFieldStringSet.add((simpleClassName + "." + getter).toUpperCase());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public void setFieldWrapperService(FieldWrapperService service) {
        this.fieldWrapperService = service;
    }

    @Override
    public FieldWrapperService getFieldWrapperService() {
        return fieldWrapperService;
    }

    @Override
    public Set<String> getEncryptionFieldStringSet() {
        return encryptionFieldStringSet;
    }
}
