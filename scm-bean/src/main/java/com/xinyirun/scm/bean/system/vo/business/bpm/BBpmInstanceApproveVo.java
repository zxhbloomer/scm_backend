package com.xinyirun.scm.bean.system.vo.business.bpm;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 审批流用户节点表
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BBpmInstanceApproveVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 7973323057052947318L;

    private Integer id;

    /**
     * 审批编号(根据process_instance_id搜索bpm_instance，获取process_code)
     */
    private String process_code;

    /**
     * 任务 ID，对应 Flowable 中的节点 ID
     */
    private String node_id;

    /**
     * 任务 ID，对应 Flowable 中的任务 ID
     */
    private String task_id;

    /**
     * 流程实例 ID
     */
    private String process_instance_id;

    /**
     * 流程定义 ID
     */
    private String process_definition_id;

    /**
     * 任务名称
     */
    private String task_name;

    /**
     * 节点类型 1-审批  2-抄送 3-参加评论
     */
    private String type;

    /**
     * 任务处理人 code
     */
    private String assignee_code;

    /**
     * 任务处理人姓名
     */
    private String assignee_name;

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
     * 任务到期时间，可选
     */
    private LocalDateTime due_date;

    /**
     * 0-代办 1-已办
     */
    private String status;

    /**
     * 审批时间
     */
    private LocalDateTime approve_time;

    /**
     * 审批类型  0=处理中 1=同意 2=委派 3=委派人完成 4=拒绝 5=转办 6=退回 7=加密 8=查到签上的人 9=减签 10=评论 11=取消
     */
    private String approve_type;

    /**
     * 记录完成状态 同意-agree 完成-complete 拒绝-refuse 取消-cancel
     */
    private String result;

    /**
     * 备注（审批意见）
     */
    private String remark;

    /**
     * 下一个执行节点id
     */
    private String is_next;


    /**
     * 更新时间
     */
    private LocalDateTime u_time;

    /**
     * 任务创建时间
     */
    private LocalDateTime c_time;

}
