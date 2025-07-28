package com.xinyirun.scm.bean.system.vo.business.po.poorder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 采购订单附件表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BPoOrderAttachVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -7932359811295848753L;
    private Integer id;

    /**
     * 采购合同id
     */
    private Integer po_order_id;

    /**
     * 订单附件
     */
    private Integer one_file;

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
