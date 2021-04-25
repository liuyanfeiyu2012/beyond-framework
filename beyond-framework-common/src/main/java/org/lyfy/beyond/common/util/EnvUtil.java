package org.lyfy.beyond.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author: 谢星星
 * @Date: 2019/8/12 21:09
 * @Description:
 */
public class EnvUtil {

    private static final String ENV_CONF_NAME = "funmall.env";

    private static final String LOCAL_ENV_NAME = "local";
    private static final String PROD_ENV_NAME = "prd";

    public static boolean isLocalEnv() {
        if (StringUtils.isEmpty(System.getProperty(ENV_CONF_NAME)) || LOCAL_ENV_NAME.equalsIgnoreCase(System.getProperty(ENV_CONF_NAME))) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean isProdEnv() {
        if (StringUtils.isNotEmpty(System.getProperty(ENV_CONF_NAME)) && PROD_ENV_NAME.equalsIgnoreCase(System.getProperty(ENV_CONF_NAME))) {
            return true;
        } else {
            return false;
        }
    }
}
