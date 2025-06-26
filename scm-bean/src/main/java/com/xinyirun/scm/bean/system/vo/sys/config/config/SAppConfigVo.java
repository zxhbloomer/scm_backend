package com.xinyirun.scm.bean.system.vo.sys.config.config;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * app配置
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "app配置", description = "app配置")
public class SAppConfigVo implements Serializable {

    private static final long serialVersionUID = 999965204415396152L;
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

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;


}
