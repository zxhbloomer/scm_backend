package com.xinyirun.scm.common.utils;

import com.xinyirun.scm.common.properies.SystemConfigProperies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: Wqf
 * @Description: 判断当前运行环境， 中林。。。
 * @CreateTime : 2023/5/10 16:29
 */

@Component
public class RuntimeEnvUtil {

    private static SystemConfigProperies systemConfigProperies;

    @Autowired
    public void setProperties(SystemConfigProperies systemConfigProperies) {
        RuntimeEnvUtil.systemConfigProperies = systemConfigProperies;
    }

    public class ENV {
        public static final String DEV = "dev";
        public static final String DAILI = "daili";
        public static final String HAIDA = "haida";
        public static final String ZHONGLIN = "zhonglin";
        public static final String QINGRUN = "qingrun";
        public static final String YS = "ys";
    }

    public static String getEnv() {
        return systemConfigProperies.getEnv();
    }
}
