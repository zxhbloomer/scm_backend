package com.xinyirun.scm.bean.system.vo.master.permission;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 权限员工关系表VO
 * </p>
 *
 * @author system
 * @since 2025-01-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MPermissionStaffVo extends BaseVo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 权限ID，关联m_permission.id
     */
    private Long permission_id;
    
    /**
     * 员工ID，关联m_staff.id
     */
    private Long staff_id;
    
    /**
     * 权限名称
     */
    private String permission_name;
    
    /**
     * 权限编码
     */
    private String permission_code;
    
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