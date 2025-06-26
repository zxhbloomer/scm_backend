package com.xinyirun.scm.bean.api.vo.business.out;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 出库单
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "出库单", description = "出库单")
public class ApiOutBillResultVo implements Serializable {

    private static final long serialVersionUID = 8325384769305851758L;


    /**
     * 计划单号
     */
    private String plan_code;

    /**
     * 序号
     */
    private String code;

    /**
     * 出库单号
     */
    private String out_code;

    /**
     * 出库类型：0采购出库，1调拨出库，2退货出库，3监管出库，4普通出库
     */
    private String type;

    /**
     * 出库状态：0制单，1已提交，2审核通过，3审核驳回，4作废
     */
    private String status;

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
     * 客户编号
     */
    private String client_code;
}
