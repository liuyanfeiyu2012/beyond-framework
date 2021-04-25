package org.lyfy.beyond.es.client.util;

import org.lyfy.beyond.es.client.annotation.EsField;
import org.lyfy.beyond.es.client.core.FM;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author: 谢星星
 * @date: 2019/12/18 11:22
 * @description:
 */
public class Anno {
    private static final Pattern GETTER_PATTERN = Pattern.compile("^get[A-Z].*");

    private static final Pattern IS_PATTERN = Pattern.compile("^is[A-Z].*");

    public static <T, R> String of(FM<T, R> fm) {
        String fullPath;// = "";
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

            String parentPath = "";
            String path = "";

            Annotation clzEsAnno = clazz.getAnnotation(EsField.class);
            if (clzEsAnno != null) {
                EsField field = (EsField) clzEsAnno;
                parentPath = field.parentPath();
            }

            String filedName = toCamelCase(getter);
            fullPath = filedName;
            path = filedName;

            Field field = clazz.getDeclaredField(filedName);
            EsField esField = field.getAnnotation(EsField.class);
            if (esField != null) {
                if (!StringUtils.isEmpty(esField.parentPath())) {
                    parentPath = esField.parentPath();
                }
                if (!StringUtils.isEmpty(esField.path())) {
                    path = esField.path();
                }
            }

            if (!StringUtils.isEmpty(parentPath)) {
                fullPath = parentPath + "." + path;
            }

            return fullPath;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String toCamelCase(String getter) {
        String first = getter.substring(0, 1);
        String left = getter.substring(1);
        return first.toLowerCase() + left;
    }
}
