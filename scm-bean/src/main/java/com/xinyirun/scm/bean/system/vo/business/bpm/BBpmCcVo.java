package com.xinyirun.scm.bean.system.vo.business.bpm;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 抄送
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BBpmCcVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -8521715224298998246L;

    /**
     * 主键
     */
    private String id;


    /**
     * 节点id
     */
    private String node_id;

    /**
     * 节点名称
     */
    private String node_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

    /**
     * 审批编号(根据process_instance_id搜索bpm_instance，获取process_code)
     */
    private String process_code;

    /**
     * 发起人
     */
    private String owner_name;

    /**
     * 流程名称
     */
    private String process_name;

    /**
     * 流程实例 ID
     */
    private String process_instance_id;

    /**
     * 业务键，可用于关联业务数据
     */
    private Integer serial_id;

    /**
     * 表名
     */
    private String serial_type;

    /**
     * 用户code
     */
    private String user_code;
    private String tenant_code;

    /**
     * 流程状态
     */
    private String status_name;

    private PageCondition pageCondition;
}
