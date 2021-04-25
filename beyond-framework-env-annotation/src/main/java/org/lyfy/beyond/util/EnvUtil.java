package org.lyfy.beyond.util;

import org.apache.commons.lang3.StringUtils;

public class EnvUtil {

    private static final String ENV_NAME = "env";

    private static final String LOCAL_ENV_NAME = "local";
    private static final String UAT_ENV_NAME = "uat";
    private static final String PRE_ENV_NAME = "pre";
    private static final String PRD_ENV_NAME = "prd";

    public static boolean isLocalEnv() {
        String envTag = System.getProperty(ENV_NAME);
        if (StringUtils.isEmpty(envTag)) {
            return true;
        }
        return LOCAL_ENV_NAME.equalsIgnoreCase(envTag);
    }

    public static boolean isUatEnv() {
        String envTag = System.getProperty(ENV_NAME);
        if (StringUtils.isEmpty(envTag)) {
            return false;
        }
        return UAT_ENV_NAME.equalsIgnoreCase(envTag);
    }

    public static boolean isPreEnv() {
        String envTag = System.getProperty(ENV_NAME);
        if (StringUtils.isEmpty(envTag)) {
            return false;
        }
        return PRE_ENV_NAME.equalsIgnoreCase(envTag);
    }


    public static boolean isPrdEnv() {
        String envTag = System.getProperty(ENV_NAME);
        if (StringUtils.isEmpty(envTag)) {
            return false;
        }
        return PRD_ENV_NAME.equalsIgnoreCase(envTag);
    }

}
