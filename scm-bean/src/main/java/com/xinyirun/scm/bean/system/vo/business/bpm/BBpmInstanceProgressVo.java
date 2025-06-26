package com.xinyirun.scm.bean.system.vo.business.bpm;

import com.alibaba.fastjson2.JSONObject;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BBpmInstanceProgressVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -8521715224298998246L;


    /**
     * 审批编号(根据process_instance_id搜索bpm_instance，获取process_code)
     */
    private String process_code;

    /**
     * 任务 ID，对应 Flowable 中的任务 ID (返回当前登录用户的任务id)
     */
    private String current_task_id;

    /**
     * 流程实例 ID
     */
    private String process_instance_id;

    /**
     * 流程实例 name
     */
    private String process_instance_name;

    /**
     * 流程定义 ID
     */
    private String process_definition_id;

    /**
     * 任务名称
     */
    private String task_name;

    /**
     * bpm_process_templates.form_items
     */
    private String form_items;

    //流程进度步骤
    private List<BpmProgressNodeVo> progress;

    /**
     * 业务键，可用于关联业务数据
     */
    private Integer serial_id;

    /**
     * 表名
     */
    private String serial_type;

    /**
     * 流程状态
     */
    private String status_name;

    /**
     * 流程状态参数
     */
    private String result;

    /**
     * 开始时间
     */
    private LocalDateTime start_time;

    /**
     * 审批时间
     */
    private LocalDateTime finish_time;

    /**
     * 流程定义名称
     */
    private String process_definition_name;

    /**
     * 表单数据
     */
    private JSONObject form_data;

    /**
     * 发起人信息
     */
    private OrgUserVo owner_user;

    /**
     * 当前登录用户是否是发起人
     */
    private boolean if_owner_user;

    /**
     * 当前用户是否是审批人
     */
    private boolean if_approve_user;

    //流程按钮权限配置
    private BpmOperationPermVo operationPerm;


}
