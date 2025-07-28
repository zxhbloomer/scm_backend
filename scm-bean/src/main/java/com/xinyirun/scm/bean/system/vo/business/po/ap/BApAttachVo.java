package com.xinyirun.scm.bean.system.vo.business.po.ap;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 应付账款附件表（Accounts Payable）
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ap_attach")
public class BApAttachVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 2803985246533917449L;

    private Integer id;

    /**
     * b_ap id
     */
    private Integer ap_id;

    /**
     * 付款附件
     */
    private Integer one_file;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;


    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

}
