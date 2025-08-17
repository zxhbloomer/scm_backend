package com.xinyirun.scm.bean.system.vo.master.org;

import lombok.Data;

/**
 * 组织机构子节点详细计数VO
 * 用于集团节点的分类显示：子集团数量、企业数量
 */
@Data
public class MOrgSubCountDetailVo {
    
    /**
     * 子集团数量
     */
    private Integer sub_group_count;
    
    /**
     * 企业数量
     */
    private Integer company_count;
    
}