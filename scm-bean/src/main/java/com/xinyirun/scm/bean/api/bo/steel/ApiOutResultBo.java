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
 * 返回出库单
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "返回出库单", description = "返回出库单")
public class ApiOutResultBo implements Serializable {

    private static final long serialVersionUID = -8447612904702235362L;
    /**
     * 出库单code
     */
    private String outDocCode;

    /**
     * 出库单价
     */
    private BigDecimal outPrice;

    /**
     * 计划明细Code
     */
    private String planItemCode;

    /**
     * 实际出库时间
     */
    private String outTime;

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
     * 状态Code(WMS对接) 0 进行中 1 作废 2 已完成
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
     * 实际出库数量
     */
    private BigDecimal realOutNum;

    /**
     * 出库库存数量
     */
    private BigDecimal outStockNum;

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
     * 出库照片文件
     */
    private List<String> outPhotoFile;

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
