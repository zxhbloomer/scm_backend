package com.xinyirun.scm.bean.api.vo.business.in;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 入库单返回结果
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "入库单返回结果", description = "入库单返回结果")
public class ApiInBillResultVo implements Serializable {

    private static final long serialVersionUID = -8071731123894381563L;

    /**
     * 计划单号
     */
    private String plan_code;

    /**
     * 序号
     */
    private String code;

    /**
     * 入库单号
     */
    private String in_code;

    /**
     * 入库类型：0采购入库，1调拨入库，2退货入库，9监管入库，10普通入库
     */
    private String type;

    /**
     * 入库状态：0制单，1已提交，2审核通过，3审核驳回，4作废
     */
    private String status;

    /**
     * 是否已结算 0否 1是
     */
    private Boolean is_settled;

    /**
     * 结算单号
     */
    private String settle_code;

    /**
     * 委托方编码
     */
    private String consignor_code;

    /**
     * 货主编码
     */
    private String owner_code;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 规格code
     */
    private String sku_code;

    /**
     * 数量
     */
    private BigDecimal count;

    /**
     * 单据类型：0供应链业务 1代理业务 2直销业务 3废钢业务
     */
    private String bill_type;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 磅单文件
     */
    private String pound_file;

    /**
     * 物料照片
     */
    private String photo_file;

    /**
     * 检验单附件
     */
    private String inspection_file;

    /**
     * 物料明细表
     */
    private String goods_file;

    /**
     * 原发数量
     */
    private BigDecimal primary_quantity;

    /**
     * 实收车数
     */
    private Integer car_count;

    /**
     * 船名
     */
    private String ship_name;

    /**
     * 订单编号
     */
    private String order_no;

    /**
     * 合同编号
     */
    private String contract_no;

    /**
     * 合同日期
     */
    private LocalDateTime contract_dt;

    /**
     * 合同量
     */
    private BigDecimal contract_num;

    /**
     * 供应商编码
     */
    private String supplier_code;

}
