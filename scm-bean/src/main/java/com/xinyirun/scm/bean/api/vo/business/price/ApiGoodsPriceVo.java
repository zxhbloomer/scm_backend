package com.xinyirun.scm.bean.api.vo.business.price;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiGoodsPriceVo implements Serializable {

    private static final long serialVersionUID = -2693694473881198758L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 编码
     */
    private String code;

    /**
     * 物料id
     */
    private Integer goods_id;

    /**
     * 物料code
     */
    private String goods_code;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 规格id
     */
    private Integer sku_id;

    /**
     * 规格code
     */
    private String sku_code;

    /**
     * 规格名称
     */
    private String sku_name;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 产地
     */
    private String origin;

    /**
     * 厂家
     */
    private String company;

    /**
     * 片区
     */
    private String area;

    /**
     * 省
     */
    private String province;

    /**
     * 省
     */
    private String province_code;

    /**
     * 市
     */
    private String city;

    /**
     * 市
     */
    private String city_code;

    /**
     * 区/县
     */
    private String district;

    /**
     * 区/县
     */
    private String district_code;

    /**
     * 开始日期
     */
    private String startDt;

    /**
     * 结束日期
     */
    private String endDt;

    /**
     * 价格日期
     */
    private String priceDt;

    /**
     * 生成时间
     */
    private LocalDateTime cTime;


}
