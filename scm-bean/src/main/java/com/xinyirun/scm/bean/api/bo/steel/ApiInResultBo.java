package com.xinyirun.scm.bean.api.bo.steel;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 返回入库单
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "返回入库单", description = "返回入库单")
public class ApiInResultBo implements Serializable {

    private static final long serialVersionUID = -6380003697729157624L;

    /**
     * 入库单code
     */
    private String putDocCode;


    /**
     * 计划明细Code
     */
    private String planItemCode;

    /**
     * 实际入库时间
     */
    private String putTime;

    /**
     * 制单人
     */
    private String makeUser;

    /**
     * 制单时间
     */
    private String makeTime;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 状态Code(WMS对接)0 进行中 1 作废 2 已完成
     */
    private String statusCode;


    /**
     * 仓库id(WMS对接过来 wms.id)
     */
    private Integer houseId;

    /**
     * 仓库code
     */
    private String houseCode;


    /**
     * 仓库名称
     */
    private String houseName;

    /**
     * 实收车量
     */
    private Integer carCount;

    /**
     * 原发数量
     */
    private BigDecimal primaryQuantity;

    /**
     * 实际入库数量
     */
    private BigDecimal realPutNum;

    /**
     * 入库库存数量
     */
    private BigDecimal putStockNum;

    /**
     * 换算关系
     */
    private BigDecimal calc;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 磅单文件
     */
    private List<String> poundFile;

    /**
     * 物料照片
     */
    private List<String> photoFile;

    /**
     * 检验单附件
     */
    private List<String> inspectionFile;

    /**
     * 物料明细表
     */
    private List<String> goodsFile;
}
