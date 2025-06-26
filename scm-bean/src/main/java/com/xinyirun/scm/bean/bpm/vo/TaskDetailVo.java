package com.xinyirun.scm.bean.bpm.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author LoveMyOrange
 * @create 2022-10-16 9:38
 */
@Data
public class TaskDetailVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -2504553368827597699L;
    private String taskId;
    private String activityId;
    private String name;
    private Date createTime;
    private Date endTime;
    private String signImage;
    private List<AttachmentVo> attachmentVoList;
    private List<OptionVo> optionVoList;
    private List<CommentVo> commentVoList;
    private String comment;
}
