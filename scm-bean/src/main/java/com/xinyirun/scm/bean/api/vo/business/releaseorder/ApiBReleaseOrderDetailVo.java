package com.xinyirun.scm.bean.api.vo.business.releaseorder;

import com.xinyirun.scm.bean.api.vo.business.file.ApiFileVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-11-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBReleaseOrderDetailVo implements Serializable {

    private static final long serialVersionUID = -3934074000059804051L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 放货指令编号
     */
    private String release_order_code;

    /**
     * 序号
     */
    private Integer no;

    /**
     * 放货指令id
     */
    private Integer release_order_id;

    /**
     * 商品编号
     */
    private String commodity_code;

    /**
     * 商品名称
     */
    private String commodity_name;

    /**
     * 商品规格
     */
    private String commodity_spec;

    /**
     * 规格code
     */
    private String commodity_spec_code;

    /**
     * 商品别称
     */
    private String commodity_nickname;

    /**
     * 型规
     */
    private String type_gauge;

    /**
     * 放货数量
     */
    private BigDecimal qty;

    /**
     * 单价(含税)(订单商品单价)
     */
    private BigDecimal price;

    /**
     * 实时单价(大宗商品实时单价)
     */
    private BigDecimal real_price;

    /**
     * 金额(含税)=单价*放货数量
     */
    private BigDecimal amount;

    /**
     * 收款日期
     */
    private LocalDateTime collection_date;

    /**
     * 单位名称
     */
    private String unit_name;

    /**
     * 仓库code
     */
    private String warehouse_code;

    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 更新人
     */
    private String u_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

}
