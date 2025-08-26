package com.xinyirun.scm.bean.system.vo.master.org;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.BWarehouseGroupVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.master.tree.TreeDataVo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MWarehouseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 岗位主表
 * </p>
 *
 * @author zxh
 * @since 2019-11-12
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "岗位主表", description = "岗位主表")
@EqualsAndHashCode(callSuper=false)
public class MPositionVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 3554363110752008984L;

    private Long id;

    /**
     * 编码
     */
    private String code;

    /**
     * 全称
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
     * 所属数据
     */
    private String group_full_name;
    private String group_simple_name;
    private String company_name;
    private String company_simple_name;
    private String dept_full_name;
    private String parent_dept_simple_name;

    /**
     * 岗位下员工id
     */
    private Long staff_id;

    /**
     * 是否已经设置该岗位
     */
    private Boolean settled;

    /**
     * 该岗位向下，员工数量
     */
    private int staff_count;

    /**
     * 该岗位向下，角色数量
     */
    private int role_count;

    /**
     * 该岗位向下，仓库数量
     */
    private int warehouse_count;

    /**
     * 该岗位向下，仓库数量
     */
    private int warehouse_count1;

    /**
     * 角色列表（支持角色点击功能）
     */
    private List<RoleItem> roleList;
    
    /**
     * 该岗位权限数量
     */
    private int permission_count;
    
    /**
     * 权限列表（支持权限点击功能）
     */
    private List<PermissionItem> permissionList;
    
    /**
     * 角色项数据结构
     */
    @Data
    @NoArgsConstructor
    public static class RoleItem implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Long id;
        private String code;
        private String name;
        private String key;
        private String label;
    }
    
    /**
     * 权限项数据结构
     */
    @Data
    @NoArgsConstructor
    public static class PermissionItem implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Long id;
        private String key;
        private String label;
    }


    /**
     * 弹出框模式：空：普通模式；10：组织使用，需要排除已经选择的数据；
     */
    private String dataModel;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    private List<Integer> warehouseIds;

    private List<Integer> warehouseGroupIds;

    // 按岗位设置的仓库列表
    private List<MWarehouseVo> warehousePositionList;

    // 按岗位设置的仓库组列表
    private List<BWarehouseGroupVo> warehouseGroupList;
    // 按岗位设置的仓库列表
    private List<MWarehouseVo> warehouseGroupPositionList;

//    private TreeDataVo permissionTreeData;

    // 仓库权限树信息
    private TreeDataVo warehouseTreeData;
}
