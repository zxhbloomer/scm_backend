package com.xinyirun.scm.bean.system.vo.master.bankaccounts;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 款项类型
 * </p>
 *
 * @author xinyirun
 * @since 2025-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MBankAccountsTypeVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 7707800057110751180L;

    private Integer id;

    /**
     * 单号
     */
    private String code;

    /**
     * 名称：预付款、预收款、应付款、应收款
     */
    private String name;

    /**
     * 状态  0停用 1启用
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;


    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

}
