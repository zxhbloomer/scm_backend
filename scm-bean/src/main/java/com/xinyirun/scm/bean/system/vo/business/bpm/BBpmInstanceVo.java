package com.xinyirun.scm.bean.system.vo.business.bpm;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 流程实例
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BBpmInstanceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -8521715224298998246L;

    private Integer id;

    /**
     * 审批编号
     */
    private String process_code;

    /**
     * 流程实例 ID，唯一标识
     */
    private String process_instance_id;

    /**
     * 流程定义 ID
     */
    private String process_definition_id;

    /**
     * 流程定义名称
     */
    private String process_definition_name;

    /**
     * 业务键，用于关联业务数据
     */
    private String business_key;

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

    /**
     * 流程结束时间，若流程未结束则为 null
     */
    private LocalDateTime end_time;

    /**
     * 0=审批中 1=审批通过 2=已撤销
     */
    private String status;
    private String status_name;

    /**
     * 摸板表单
     */
    private String form_items;

    /**
     * process
     */
    private String process;

    /**
     * 业务键，可用于关联业务数据
     */
    private Integer serial_id;

    /**
     * 表名
     */
    private String serial_type;

    /**
     * 当前任务 ID，可选
     */
    private String current_task_id;

    /**
     * 当前任务名称，可选
     */
    private String current_task_name;

    /**
     * 下一个审批人code（张三，李四）
     */
    private String next_approve_code;

    /**
     * 下一个审批人名称（张三，李四）
     */
    private String next_approve_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

    /**
     * 批准类型
     */
    private String approve_type_name;

    /**
     * 任务处理人姓名
     */
    private String assignee_name;

    /**
     * 表单数据
     */
    private JSONObject form_data;

    /**
     * 当前任务 ID
     */
    private String task_id;

    /**
     * 流程名，业务定义：如（新增企业审批）
     */
    private String process_definition_business_name;

    private PageCondition pageCondition;

}
