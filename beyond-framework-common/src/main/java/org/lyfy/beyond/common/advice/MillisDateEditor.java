package org.lyfy.beyond.common.advice;

import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyEditorSupport;
import java.util.Date;

/**
 * [Description]
 * Created by zhanglong on 2019/7/8.
 */
public class MillisDateEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.isBlank(text)) {
            super.setValue(null);
        } else {
            super.setValue(new Date(Long.valueOf(StringUtils.stripToEmpty(text))));
        }
    }
}
