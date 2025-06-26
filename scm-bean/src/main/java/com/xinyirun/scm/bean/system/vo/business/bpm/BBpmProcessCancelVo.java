package com.xinyirun.scm.bean.system.vo.business.bpm;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 取消任务信息
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BBpmProcessCancelVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -8521715224298998246L;

    /**
     * 任务id
     */
    private String task_id;

    /** 驳回，撤销都会调用这个监听器。状态一定填写
     *  1-已撤销 3-驳回
     */
    private String status;

    /**
     * 原因
     */
    private String reason;

}
