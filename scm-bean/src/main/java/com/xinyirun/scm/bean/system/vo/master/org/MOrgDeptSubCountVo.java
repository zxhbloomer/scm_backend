package com.xinyirun.scm.bean.system.vo.master.org;

import lombok.Data;

/**
 * 部门组织机构子节点详细计数VO
 * 用于部门节点的分类显示：子部门数量、岗位数量
 */
@Data
public class MOrgDeptSubCountVo {
    
    /**
     * 子部门数量
     */
    private Integer sub_dept_count;
    
    /**
     * 岗位数量
     */
    private Integer position_count;
    
}