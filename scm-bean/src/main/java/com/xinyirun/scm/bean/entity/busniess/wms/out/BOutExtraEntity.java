package com.xinyirun.scm.bean.entity.busniess.wms.out;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 出库单其他信息
 * </p>
 *
 * @author wwl
 * @since 2021-10-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_out_extra")
public class BOutExtraEntity implements Serializable {

    private static final long serialVersionUID = 999513270368549250L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 出库单id
     */
    @TableField("out_id")
    private Integer out_id;

    /**
     * 是否异常 0否 1是
     */
    @TableField("is_exception")
    private Boolean is_exception;

    /**
     * 合同编号
     */
    @TableField("contract_no")
    private String contract_no;

    /**
     * 异常描述
     */
    @TableField("exceptionexplain")
    private String exceptionexplain;

    /**
     * 单价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 磅单文件
     */
    @TableField("pound_file")
    private Integer pound_file;

    /**
     * 出库照片文件
     */
    @TableField("out_photo_file")
    private Integer out_photo_file;

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
     * 空车过磅附件id
     */
    @TableField("one_file")
    private Integer one_file;

    /**
     * 车头车尾带司机附件id
     */
    @TableField("two_file")
    private Integer two_file;

    /**
     * 司机承诺书附件id
     */
    @TableField("three_file")
    private Integer three_file;

    /**
     * 司机驾驶证附件id
     */
    @TableField("four_file")
    private Integer four_file;

    /**
     * 装货照片附件id
     */
    @TableField("five_file")
    private Integer five_file;

    /**
     * 装货视频附件id
     */
    @TableField("six_file")
    private Integer six_file;

    /**
     * 重车过磅附件id
     */
    @TableField("seven_file")
    private Integer seven_file;

    /**
     * 车头车尾带司机附件id(重车)
     */
    @TableField("eight_file")
    private Integer eight_file;

    /**
     * 磅单附件id
     */
    @TableField("nine_file")
    private Integer nine_file;

    /**
     * 合同日期
     */
    @TableField("contract_dt")
    private LocalDateTime contract_dt;

    /**
     * 合同量
     */
    @TableField("contract_num")
    private BigDecimal contract_num;

    /**
     * 客户ID
     */
    @TableField("client_id")
    private Integer client_id;

    /**
     * 客户编码
     */
    @TableField("client_code")
    private String client_code;

    @TableField("bill_type")
    private String bill_type;

    @TableField("order_id")
    private Integer order_id;

    @TableField("order_type")
    private String order_type;
}
