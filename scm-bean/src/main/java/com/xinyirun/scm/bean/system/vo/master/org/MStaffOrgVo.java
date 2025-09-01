package com.xinyirun.scm.bean.system.vo.master.org;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 员工组织机构关系视图
 * </p>
 *
 * @author SCM System
 * @since 2025-01-01
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MStaffOrgVo extends BaseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关系表主键
     */
    private Long id;

    /**
     * 员工ID
     */
    private Long staff_id;

    /**
     * 员工编码
     */
    private String staff_code;

    /**
     * 员工姓名
     */
    private String staff_name;

    /**
     * 关联单号（组织单位ID）
     */
    private Long serial_id;

    /**
     * 关联单号类型（20=集团，30=企业，40=部门，50=岗位）
     */
    private String serial_type;

    // ================== 集团信息 ==================
    /**
     * 集团名称
     */
    private String group_name;

    // ================== 主体企业信息 ==================
    /**
     * 企业名称
     */
    private String company_name;

    // ================== 部门信息 ==================
    /**
     * 部门名称
     */
    private String dept_name;

    // ================== 岗位信息 ==================
    /**
     * 岗位ID
     */
    private Long position_id;

    /**
     * 岗位编码
     */
    private String position_code;

    /**
     * 岗位名称
     */
    private String position_name;

    // ================== 基础字段 ==================
    private Long c_id;
    private LocalDateTime c_time;
    private Long u_id;
    private LocalDateTime u_time;
    private Integer dbversion;
}