package com.xinyirun.scm.bean.api.vo.master.goods;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 物料规格
 * </p>
 *
 * @author htt
 * @since 2021-10-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "商品规格", description = "商品规格")
public class ApiGoodsSpecVo implements Serializable {

    private static final long serialVersionUID = 6010763526984183656L;

    /**
     * 物料编码
     */
    private String code;

    /**
     * 商品编码
     */
    private String goods_code;

    /**
     * 商品名称
     */
    private String name;


    /**
     * 物料属性编号
     */
    private String goods_attr_id;

    /**
     * 规格名称
     */
    private String spec;

    /**
     * 品名
     */
    private String pm;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 创建人ID
     */
    private Long c_id;

    /**
     * 单位：1吨,2湿吨，3卷，4扎，5根
     */
    private String unit;

    public String getCodeAppCode() {
        return code;
    }

    public String getSpecNameGoodsCodeAppCode() {
        return code+goods_code;
    }

}
