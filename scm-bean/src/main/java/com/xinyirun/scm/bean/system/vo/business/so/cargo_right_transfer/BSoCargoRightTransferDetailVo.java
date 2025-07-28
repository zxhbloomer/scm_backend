package com.xinyirun.scm.bean.system.vo.business.so.cargo_right_transfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售货权转移明细表VO类
 * 
 * @author system
 * @since 2025-07-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BSoCargoRightTransferDetailVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 7410719455343119320L;

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 货权转移主表ID
     */
    private Integer cargo_right_transfer_id;

    /**
     * 销售订单明细ID
     */
    private Integer so_order_detail_id;

    /**
     * 销售订单ID
     */
    private Integer so_order_id;

    /**
     * 销售订单号
     */
    private String so_order_code;

    /**
     * 商品ID
     */
    private Integer goods_id;

    /**
     * 商品编码
     */
    private String goods_code;

    /**
     * 商品名称
     */
    private String goods_name;

    /**
     * SKU ID
     */
    private Integer sku_id;

    /**
     * SKU编码
     */
    private String sku_code;

    /**
     * SKU名称
     */
    private String sku_name;

    /**
     * 单位ID
     */
    private Integer unit_id;

    /**
     * 产地
     */
    private String origin;

    /**
     * 订单数量
     */
    private BigDecimal order_qty;

    /**
     * 订单单价
     */
    private BigDecimal order_price;

    /**
     * 订单金额
     */
    private BigDecimal order_amount;

    /**
     * 本次转移数量
     */
    private BigDecimal transfer_qty;

    /**
     * 转移单价
     */
    private BigDecimal transfer_price;

    /**
     * 转移金额
     */
    private BigDecimal transfer_amount;

    /**
     * 质量状态(1-合格,2-不合格,3-待检)
     */
    private String quality_status;

    /**
     * 质量状态名称
     */
    private String qualityStatusName;

    /**
     * 批次号
     */
    private String batch_no;

    /**
     * 生产日期
     */
    private LocalDate production_date;

    /**
     * 有效期
     */
    private Integer expiry_date;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人ID
     */
    private Long c_id;

    /**
     * 更新人ID
     */
    private Long u_id;

    /**
     * 数据版本号
     */
    private Integer dbversion;
}