package org.lyfy.beyond.common.advice;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.propertyeditors.CustomNumberEditor;

/**
 * [Description]
 *
 * @author zhanglong
 * @date 2019/7/8
 */
public class StripNumberEditor extends CustomNumberEditor {

    public StripNumberEditor(Class<? extends Number> numberClass) {
        super(numberClass, true);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        super.setAsText(StringUtils.stripToEmpty(text));
    }
}
