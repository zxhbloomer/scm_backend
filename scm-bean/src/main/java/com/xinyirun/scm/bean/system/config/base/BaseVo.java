package com.xinyirun.scm.bean.system.config.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: BaseVo
 * @Author: zxh
 * @date: 2019/12/31
 * @Version: 1.0
 */
public class BaseVo implements Serializable {
    private static final long serialVersionUID = 8985689562789467734L;

    /** 请求参数 */
    private Map<String, Object> params;

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParams(Map<String, Object> params)
    {
        this.params = params;
    }
}
