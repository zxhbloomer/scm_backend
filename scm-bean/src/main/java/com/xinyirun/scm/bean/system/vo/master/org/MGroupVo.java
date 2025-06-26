package com.xinyirun.scm.bean.system.vo.master.org;

import com.baomidou.mybatisplus.annotation.FieldFill;
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
 * 集团主表
 * </p>
 *
 * @author zxh
 * @since 2019-10-30
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "集团主表", description = "集团主表")
@EqualsAndHashCode(callSuper=false)
public class MGroupVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -7466167220853981131L;

    private Long id;

    /**
     * 集团编码
     */
    private String code;

    /**
     * 集团名称
     */
    private String name;

    /**
     * 简称
     */
    private String simple_name;

    /**
     * 说明
     */
    private String descr;

    /**
     * 是否删除
     */
    private Boolean is_del;
    private String is_del_name;

    /**
     * 租户id
     */
//    private Long tenant_id;

    private Long c_id;
    private String c_name;

    private LocalDateTime c_time;

    private Long u_id;
    private String u_name;

    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 关联单号
     */
    private Long parent_serial_id;

    /**
     * 关联单号类型
     */
    private String parent_serial_type;
    private String parent_group_name;
    private String parent_group_simple_name;
    private String parent_type_text;

    /**
     * 弹出框模式：空：普通模式；10：组织使用，需要排除已经选择的数据；
     */
    private String dataModel;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;
}
