package com.xinyirun.scm.bean.system.vo.business.bpm;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 评论vo
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BBpmCommentVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -8521715224298998246L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 评论内容
     */
    private String text;

    /**
     * 附件数组
     */
    private Integer files_id;

    /**
     * 图片数组
     */
    private String images;

    /**
     * 任务id
     */
    private String task_id;

    /**
     * 节点id
     */
    private String node_id;

    /**
     * 审批编号
     */
    private String process_code;

    /**
     * 任务处理人 code
     */
    private String assignee_code;

    /**
     * 任务处理人姓名
     */
    private String assignee_name;

    /**
     * 留言方式
     * OPINION_COMMENT	意见评论的标识，可能用于在任务或流程中记录用户的评论意见。
     * OPTION_COMMENT	选项评论的标识，可能用于记录用户选择的选项。
     * COMMENTS_COMMENT	评论的标识，可能用于收集用户在流程中的各种评论信息。
     */
    private String type;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 评论人员
     */
    private OrgUserVo user;

    /**
     * 附件
     */
    private List<SFileInfoVo> annex_files;
}
