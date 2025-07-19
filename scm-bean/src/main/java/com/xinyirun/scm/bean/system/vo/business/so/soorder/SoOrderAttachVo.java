package com.xinyirun.scm.bean.system.vo.business.so.soorder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 销售订单附件表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SoOrderAttachVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 3327404831618472307L;
    private Integer id;

    /**
     * 销售合同id
     */
    private Integer so_order_id;

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
