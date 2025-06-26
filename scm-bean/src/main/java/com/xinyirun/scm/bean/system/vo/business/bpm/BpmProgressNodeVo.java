package com.xinyirun.scm.bean.system.vo.business.bpm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审批流程显示vo
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BpmProgressNodeVo {
    //节点ID
    private String node_id;
    private String task_id;
    //审批类型
    private String approval_mode;
    //节点类型
    private String node_type;
    //节点名称
    private String name;
    //节点相关人员
    private OrgUserVo user;
    //该节点动作操作类型
    private String action;
    // 处理意见
    private List<BBpmCommentVo> comment;
    //处理结果
    private String result;
    //开始结束时间
    private LocalDateTime start_time;
    private LocalDateTime finish_time;
}
