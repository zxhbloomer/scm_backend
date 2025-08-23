package com.xinyirun.scm.bean.system.vo.sys.rbac.role;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 角色员工关系表VO
 * </p>
 *
 * @author system
 * @since 2025-01-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MRoleStaffVo extends BaseVo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 角色ID，关联s_role.id
     */
    private Long role_id;
    
    /**
     * 员工ID，关联m_staff.id
     */
    private Long staff_id;
    
    /**
     * 角色名称
     */
    private String role_name;
    
    /**
     * 角色编码
     */
    private String role_code;
    
    /**
     * 角色简称
     */
    private String role_simple_name;
    
    /**
     * 员工姓名
     */
    private String staff_name;
    
    /**
     * 员工工号
     */
    private String staff_code;
    
    /**
     * 创建人ID
     */
    private Long c_id;
    
    /**
     * 创建时间
     */
    private LocalDateTime c_time;
    
    /**
     * 创建人姓名
     */
    private String c_name;
    
    /**
     * 修改人ID
     */
    private Long u_id;
    
    /**
     * 修改时间
     */
    private LocalDateTime u_time;
    
    /**
     * 修改人姓名
     */
    private String u_name;
    
    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;
}