package org.lyfy.beyond.encrypt.plugin.util;

import java.lang.reflect.Field;

public class FieldUtil {

    public static Field getClassField(Class<?> clazz, String fieldName) {

        try {
            Field field = clazz.getDeclaredField(fieldName);
            return field;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

}
