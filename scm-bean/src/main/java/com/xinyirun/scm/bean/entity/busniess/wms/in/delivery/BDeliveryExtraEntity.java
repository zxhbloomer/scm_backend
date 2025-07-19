package com.xinyirun.scm.bean.entity.busniess.wms.in.delivery;

import java.io.Serial;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 提货单副表
 * </p>
 *
 * @author xinyirun
 * @since 2024-06-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_delivery_extra")
public class BDeliveryExtraEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -4260930737832352947L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 提货单id
     */
    @TableField("delivery_id")
    private Integer delivery_id;

    /**
     * 订单id
     */
    @TableField("order_id")
    private Integer order_id;

    /**
     * 订单类型
     */
    @TableField("order_type")
    private String order_type;

    /**
     * 单据类型:0供应链业务 1代理业务 2直销业务 3废钢业务
     */
    @TableField("bill_type")
    private String bill_type;

    /**
     * 单价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 商品总价
     */
    @TableField("total_price")
    private BigDecimal total_price;

    /**
     * 磅单文件
     */
    @TableField("pound_file")
    private Integer pound_file;

    /**
     * 货物图片
     */
    @TableField("photo_file")
    private Integer photo_file;

    /**
     * 检验单
     */
    @TableField("inspection_file")
    private Integer inspection_file;

    /**
     * 物料明细表
     */
    @TableField("goods_file")
    private Integer goods_file;

    /**
     * 物料信息说明
     */
    @TableField("info_detail")
    private String info_detail;

    /**
     * 物料是否合格:0合格,1不合格
     */
    @TableField("stock_status")
    private Boolean stock_status;

    /**
     * 是否异常:0否;1:是
     */
    @TableField("is_exception")
    private Boolean is_exception;

    /**
     * 异常描述
     */
    @TableField("exception_explain")
    private String exception_explain;

    /**
     * 原发数量
     */
    @TableField("primary_quantity")
    private BigDecimal primary_quantity;

    /**
     * 实收车数
     */
    @TableField("car_count")
    private Integer car_count;

    /**
     * 车头车尾带司机附件id
     */
    @TableField("one_file")
    private Integer one_file;

    /**
     * 重车过磅附件id
     */
    @TableField("two_file")
    private Integer two_file;

    /**
     * 卸货照片附件id
     */
    @TableField("three_file")
    private Integer three_file;

    /**
     * 卸货视频附件id
     */
    @TableField("four_file")
    private Integer four_file;

    /**
     * 车头车尾带司机id
     */
    @TableField("five_file")
    private Integer five_file;

    /**
     * 磅单(司机签字)附件id
     */
    @TableField("six_file")
    private Integer six_file;


}
