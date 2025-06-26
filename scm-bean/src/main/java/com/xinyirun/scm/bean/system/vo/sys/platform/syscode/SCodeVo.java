package com.xinyirun.scm.bean.system.vo.sys.platform.syscode;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 编码控制
 * </p>
 *
 * @author zxh
 * @since 2019-12-12
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "编码控制表", description = "编码控制表")
@EqualsAndHashCode(callSuper=false)
public class SCodeVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 4617915261869763630L;

    private Long id;

    /**
     * 编码类型
     */
    private String type;

    /**
     * 名称
     */
    private String name;

    /**
     * 编码规则
     */
    private String rule;

    /**
     * 当前编码
     */
    private String code;

    /**
     * 代码增加序号
     */
    private Long auto_create;

    /**
     * 编码规则
     */
    private String code_rule_label;

    /**
     * 编码名称
     */
    private String code_type_label;


    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 前缀
     */
    private String prefex;

    private Long c_id;
    private String c_name;

    private LocalDateTime c_time;

    private Long u_id;
    private String u_name;

    private LocalDateTime u_time;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

}
