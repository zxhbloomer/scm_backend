package com.xinyirun.scm.bean.system.vo.business.bpm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author:
 * @Description:
 * @Date:Created in 2022/10/9 16:20
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BpmChildNodeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 6637691843042681412L;

    /**
     * 节点 ID
     */
    private String id;

    /**
     * 父节点 ID
     */
    private String parentId;

    /**
     * 节点类型
     */
    private String type;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 是否编辑
     */
    private Boolean isEdit;

    /**
     * 多用户
     */
    private Boolean multiple;

    /**
     * 节点名称
     */
    private String desc;

    /**
     * 审批类型 AND=会签  OR=或签  NEXT=顺序会签
     */
    private String approval_mode;

    /**
     * 用户
     */
    private List<OrgUserVo> users;
}

