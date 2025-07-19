package com.xinyirun.scm.bean.system.vo.business.po.pocontract;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 采购合同附件表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PoContractAttachVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 2253304039622876959L;

    private Integer id;

    /**
     * 采购合同id
     */
    private Integer po_contract_id;

    /**
     * 合同附件
     */
    private Integer one_file;

    /**
     * 其他材料
     */
    private Integer two_file;

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
