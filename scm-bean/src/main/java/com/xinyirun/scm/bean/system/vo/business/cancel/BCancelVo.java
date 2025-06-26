package com.xinyirun.scm.bean.system.vo.business.cancel;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 作废单
 * </p>
 *
 * @author xinyirun
 * @since 2024-01-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "作废单", description = "作废单")
public class BCancelVo implements Serializable {

    private static final long serialVersionUID = 650991906296181762L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 业务id（入库单id、出库单id）
     */
    private Integer business_id;

    /**
     * 业务类型
     */
    private String business_type;

    /**
     * 业务单号（入库单code、出库单code）
     */
    private String business_code;

    /**
     * 状态 1-影响库存的数据 0-不影响库存
     */
    private String status;

    /**
     * 数量（入库单入库数量、出库单出库数量）
     */
    private BigDecimal qty;

    /**
     * 时间（入库单入库时间、出库单出库时间）
     */
    private LocalDateTime time;

    /**
     * 作废时间
     */
    private LocalDateTime cancel_time;

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
     * 创建人
     */
    private String c_name;

    /**
     * 修改人
     */
    private String u_name;

    /**
     * 入库库位id
     */
    private Integer warehouse_id;

    /**
     * 入库库位id
     */
    private Integer location_id;

    /**
     * 入库库位id
     */
    private Integer bin_id;

    /**
     * 物料id
     */
    private Integer sku_id;

    /**
     * 规格编码
     */
    private String sku_code;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主code
     */
    private String owner_code;

    /**
     * 备注
     */
    private String remark;
}
