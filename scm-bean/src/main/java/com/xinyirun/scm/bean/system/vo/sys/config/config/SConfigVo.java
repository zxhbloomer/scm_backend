package com.xinyirun.scm.bean.system.vo.sys.config.config;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zxh
 * @date 2019/9/26
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "参数数据信息", description = "参数数据信息")
@EqualsAndHashCode(callSuper=false)
public class SConfigVo implements Serializable {
    private static final long serialVersionUID = -21039960705170821L;

    private Long id;

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数键名
     */
    private String config_key;

    /**
     * 参数键值
     */
    private String value;

    /**
     * 额外配置1～4
     */
    private String extra1;
    private String extra2;
    private String extra3;
    private String extra4;

    /**
     * 是否启用(1:true-已启用,0:false-已禁用)
     */
    private Boolean is_enable;

    /**
     * 说明
     */
    private String descr;

    private Long c_id;
    private String c_name;

    private LocalDateTime c_time;

    private Long u_id;
    private String u_name;

    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;
}
