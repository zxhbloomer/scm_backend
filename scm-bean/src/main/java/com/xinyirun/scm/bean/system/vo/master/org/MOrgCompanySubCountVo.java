package com.xinyirun.scm.bean.system.vo.master.org;

import lombok.Data;

/**
 * 企业组织机构子节点详细计数VO
 * 用于企业节点的部门数量显示：（部门数：8）
 */
@Data
public class MOrgCompanySubCountVo {
    
    /**
     * 部门数量
     */
    private Integer dept_count;
    
}