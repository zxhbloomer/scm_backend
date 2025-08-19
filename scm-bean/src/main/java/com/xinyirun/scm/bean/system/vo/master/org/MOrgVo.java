package com.xinyirun.scm.bean.system.vo.master.org;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 组织主表
 * </p>
 *
 * @author zxh
 * @since 2019-11-12
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "组织主表", description = "组织主表")
@EqualsAndHashCode(callSuper=false)
public class MOrgVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 6709627756368776224L;
    
    private Long id;

    /**
     * 上级组织，null为根结点
     */
    private Long parent_id;

    /**
     * 租户id，根结点
     */
//    private Long tenant_id;

    /**
     * 关联单号
     */
    private Long serial_id;

    /**
     * 关联单号类型
     */
    private String serial_type;

    /**
     * 编号，00010001..
     */
    private String code;

    /**
     * 筛选
     */
    private String [] filter_para;

    /**
     * 类型：10（租户）、20（集团）、30（公司）、40（部门）、50（岗位）、60（人员）
     */
    private String type;
    private String type_text;

    private Long c_id;

    private LocalDateTime c_time;

    private Long u_id;

    private LocalDateTime u_time;

    /**
     * 子节点数量（仅集团类型显示）
     */
    private Integer sub_count;

    /**
     * 员工数量（岗位节点统计）
     */
    private Long staff_count;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;
}
