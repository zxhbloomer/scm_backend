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
}