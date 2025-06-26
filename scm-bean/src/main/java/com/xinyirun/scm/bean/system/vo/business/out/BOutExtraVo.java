package com.xinyirun.scm.bean.system.vo.business.out;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 出库单其他信息
 * </p>
 *
 * @author wwl
 * @since 2021-10-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BOutExtraVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 8735029232028167446L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 出库单id
     */
    private Integer out_id;

    /**
     * 是否异常 0否 1是
     */
    private Boolean is_exception;

    /**
     * 合同编号
     */
    private String contract_no;

    /**
     * 异常描述
     */
    private String exceptionexplain;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 磅单文件
     */
    private Integer pound_file;

    /**
     * 出库照片文件
     */
    private Integer out_photo_file;


}
