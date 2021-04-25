package org.lyfy.beyond.common.constant;

import java.lang.reflect.Field;
import java.util.Objects;

public interface BaseEnum {

    String DEFAULT_CODE = "code";
    String DEFAULT_MESSAGE = "message";

    /**
     * get enum code
     *
     * @return enum code
     */
    default String getCode() {
        return getFieldValue(DEFAULT_CODE);
    }

    /**
     * get enum desc value
     *
     * @return enum desc value
     */
    default String getMessage() {
        return getFieldValue(DEFAULT_MESSAGE);
    }

    /**
     * parse enum code 2 enum
     *
     * @param enumClazz {param-0: enum class}
     * @param code {param-1: enum code}
     * @param <T> {param-2: enum type}
     * @return {return enum}
     */
    static <T extends Enum<T>> T valueOfEnum(Class<T> enumClazz, String code) {
        Objects.requireNonNull(enumClazz);
        if (code == null || code.trim().equals("")) {
            throw new IllegalArgumentException("enum code can not be null");
        }

        if (enumClazz.isAssignableFrom(BaseEnum.class) || !BaseEnum.class.isAssignableFrom(enumClazz)) {
            throw new IllegalArgumentException("enum must impl BaseEnum");
        }

        T[] enumConstantList = enumClazz.getEnumConstants();
        if (null == enumConstantList || enumConstantList.length == 0) {
            throw new IllegalArgumentException("parse enum err");
        }

        for (T element : enumConstantList) {
            BaseEnum baseEnum = (BaseEnum) element;
            if (code.equals(baseEnum.getCode())) {
                return element;
            }
        }

      throw new RuntimeException("value of enum err, class-" + enumClazz.getName());
    }

    default String getFieldValue(String defaultDescName) {
        try {
            Field field = this.getClass().getDeclaredField(defaultDescName);
            if (null == field) {
                return null;
            }

            field.setAccessible(true);
            return field.get(this).toString();

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }


}
