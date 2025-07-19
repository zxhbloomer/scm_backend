package com.xinyirun.scm.bean.entity.busniess.wms.out.receive;

import java.io.Serial;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2024-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_receive_extra")
public class BReceiveExtraEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 8880966026005053153L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 收货单id
     */
    @TableField("receive_id")
    private Integer receive_id;

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
     * 磅单文件id
     */
    @TableField("pound_file")
    private Integer pound_file;

    /**
     * 出库照片文件id
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

    @TableField("seven_file")
    private Integer seven_file;

    @TableField("eight_file")
    private Integer eight_file;

    @TableField("nine_file")
    private Integer nine_file;

    /**
     * 合同日期 冗余
     */
    @TableField("contract_dt")
    private LocalDateTime contract_dt;

    /**
     * 合同量
     */
    @TableField("contract_num")
    private BigDecimal contract_num;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Integer client_id;

    /**
     * 客户编码
     */
    @TableField("client_code")
    private String client_code;

    /**
     * 单据类型:0供应链业务 1代理业务 2直销业务 3废钢业务
     */
    @TableField("bill_type")
    private String bill_type;


}
