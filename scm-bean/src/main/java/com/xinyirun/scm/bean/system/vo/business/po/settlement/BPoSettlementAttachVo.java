package com.xinyirun.scm.bean.system.vo.business.po.settlement;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 采购结算-附件表
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BPoSettlementAttachVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 906274726215658129L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 采购结算主表id
     */
    private Integer po_settlement_id;

    /**
     * 采购结算附件
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