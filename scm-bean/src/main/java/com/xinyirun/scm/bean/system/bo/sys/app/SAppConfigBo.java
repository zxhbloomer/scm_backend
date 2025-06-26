package com.xinyirun.scm.bean.system.bo.sys.app;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * app配置表
 * </p>
 *
 * @author
 * @since 2021-09-23
 */
@Data
public class SAppConfigBo implements Serializable {

    private static final long serialVersionUID = -6059645518221885680L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * app_key
     */
    private String app_key;

    /**
     * 秘钥
     */
    private String secret_key;

    /**
     * app名称
     */
    private String name;

    /**
     * app简称
     */
    private String short_name;

    /**
     * 0:内网 1:外网
     */
    private String fs_config;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

}
