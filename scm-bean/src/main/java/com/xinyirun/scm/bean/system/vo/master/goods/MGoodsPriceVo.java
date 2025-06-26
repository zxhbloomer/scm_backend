package com.xinyirun.scm.bean.system.vo.master.goods;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 物料实时单价
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "物料实时单价", description = "物料实时单价")
public class MGoodsPriceVo implements Serializable {

    private static final long serialVersionUID = 8529017835251499151L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 单据号
     */
    private String code;

    /**
     * 物料id
     */
    private Integer sku_id;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 物料编号
     */
    private String goods_code;

    /**
     * 规格型号
     */
    private String spec;

    /**
     * 产地
     */
    private String origin;

    /**
     * 生产厂家
     */
    private String manufacturer;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 所属片区
     */
    private String region;

    /**
     * 所属省份
     */
    private String province;

    /**
     * 所属城市
     */
    private String city;

    /**
     * 所属区县
     */
    private String district;

    /**
     * 起始日期
     */
    private LocalDateTime beginDt;

    /**
     * 终止日期
     */
    private LocalDateTime endDt;

    /**
     * 采集日期
     */
    private LocalDateTime collectionDt;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人ID
     */
    private Long c_id;

    /**
     * 修改人ID
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;


}
