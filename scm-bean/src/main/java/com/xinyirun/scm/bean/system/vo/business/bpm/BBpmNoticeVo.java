package com.xinyirun.scm.bean.system.vo.business.bpm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 审批流 通知
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BBpmNoticeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -4387340082399200171L;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知方式：
     * 0消息通知, 1弹窗显示
     */
    private String type;

    /**
     * 流程定义名称
     */
    private String process_definition_name;

    /**
     * 发起人 code
     */
    private String owner_code;

    /**
     * 发起人 名称
     */
    private String owner_name;

    /**
     * 流程开始时间
     */
    private LocalDateTime start_time;

//    /**
//     * 上一个审批人的岗位名称
//     */
//    private String pre_staff_position_names;
//    /**
//     * 上一个审批人的岗位名称
//     */
//    private String pre_staff_code;
//    /**
//     * 上一个审批人的岗位名称
//     */
//    private String pre_staff_name;

    /**
     * 下一个审批人的岗位名称
     */
    private String next_staff_name;
    private String next_staff_code;
    private Long next_staff_id;

    /**
     * 获取摘要
     */
    private String summary;

    /**
     * 获取审批内容：comment
     */
    private List<BBpmCommentVo> comment;

    /**
     * 业务键，可用于关联业务数据
     */
    private Integer serial_id;

    /**
     * 表名
     */
    private String serial_type;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 结束时间
     */
    private LocalDateTime deadLine;

    /**
     * 审批链接
     */
    private String approvalUrl;

    private String markDown;

    private String html;
}
