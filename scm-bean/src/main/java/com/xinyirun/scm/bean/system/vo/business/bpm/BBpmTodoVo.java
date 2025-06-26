package com.xinyirun.scm.bean.system.vo.business.bpm;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
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
 * 代办 已办
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BBpmTodoVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -8521715224298998246L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 审批编号(根据process_instance_id搜索bpm_instance，获取process_code)
     */
    private String process_code;

    /**
     * 流程实例code
     */
    private String bpm_instance_code;

    /**
     * 流程定义版本号
     */
    private Integer process_definition_version;

    /**
     * 流程名，业务定义：如（新增企业审批）
     */
    private String process_definition_business_name;

    /**
     * 头像
     */
    String avatar;

    /**
     * 任务 ID，对应 Flowable 中的任务 ID
     */
    private String task_id;
    private String node_id;
    private String type;

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
     * 任务处理人 code
     */
    private String assignee_code;

    /**
     * 任务处理人姓名
     */
    private String assignee_name;

    /**
     * bpm_process_templates.form_items
     */
    private String form_items;

    /**
     * bpm_process_templates.process
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
     * 审批类型 1=同意 2=委派 3=委派人完成 4=拒绝 5=转办 6=退回 7=加密 8=查到签上的人 9=减签 10=评论
     */
    private String approve_type;

    /**
     * 备注（审批意见）
     */
    private String remark;

    /**
     * 上一节点id
     */
    private Integer last_todo_id;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

    /**
     * 任务创建时间
     */
    private LocalDateTime c_time;

    /**
     * 用户code
     */
    private String user_code;

    /**
     * 流程定义名称
     */
    private String process_definition_name;

    /**
     * 发起人姓名
     */
    private String owner_name;

    /**
     * 批准类型
     */
    private String approve_type_name;

    /**
     * 表单数据
     */
    private JSONObject form_data;


    private PageCondition pageCondition;

    /**
     * 意见
     */
    private String comments;

    /**
     * 文件
     */
    private List<SFileInfoVo> annex_files;

    /**
     * 审批摘要
     */
    private String json_summary;

}
