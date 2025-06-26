package com.xinyirun.scm.bean.api.vo.business.logistics;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class LogisticsContractVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -5491556012930736142L;

    /**
     * 合同单号
     */
    private String contract_no;

    /**
     * 关联物流订单数量
     */
    private Integer count;

    /**
     * 是否关联
     */
    private Boolean is_relevance;
}
