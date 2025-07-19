package com.xinyirun.scm.bean.system.vo.business.so.socontract;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 采购合同附件表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SoContractAttachVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 2253304039622876959L;

    private Integer id;

    /**
     * 销售合同id
     */
    private Integer so_contract_id;

    /**
     * 营业执照
     */
    private Integer one_file;

    /**
     * 法人身份证正面
     */
    private Integer two_file;

    /**
     * 法人身份证反面
     */
    private Integer three_file;

    /**
     * 其他材料
     */
    private Integer four_file;


}
