package com.xinyirun.scm.bean.bpm.vo;

import com.xinyirun.scm.bean.bpm.dto.json.UserInfo;
import com.xinyirun.scm.bean.entity.bpm.BpmUsersEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 抄送
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BpmCcVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 4701633378390487963L;

    /**
     * 主键
     */
    private String id;

    /**
     * 用户编号，与租户编号一起唯一
     */
    private String user_code;

    /**
     * 租户编号
     */
    private byte[] tenant_code;

    /**
     * 流程实例id
     */
    private String process_instance_id;

    /**
     * 审批类型
     */
    private String processDefinitionName;

    /**
     * 发起人
     */
    private UserInfo startUser;

    /**
     * 发起人
     */
    private BpmUsersEntity users;

    /**
     * 提交时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 当前节点
     */
    private String currentActivityName;

    /**
     * 审批状态
     */
    private String businessStatus;

    /**
     * 耗时
     */
    private String duration;

}
