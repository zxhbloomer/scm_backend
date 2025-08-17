package com.xinyirun.scm.bean.system.vo.master.org;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 组织子节点数量查询请求VO
 *
 * @author Claude
 * @date 2025-01-16
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class MOrgSubCountRequestVo {

    /**
     * 组织ID
     */
    private Long org_id;
    
    /**
     * 组织类型 (可选)
     * 用于区分查询详细信息还是简单计数
     * 集团类型返回详细分类统计，其他类型返回简单计数
     */
    private String org_type;
}