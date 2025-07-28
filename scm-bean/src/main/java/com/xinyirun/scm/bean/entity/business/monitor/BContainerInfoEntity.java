package com.xinyirun.scm.bean.entity.business.monitor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_container_info")
public class BContainerInfoEntity implements Serializable {

    private static final long serialVersionUID = -4208595885647203782L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 箱号
     */
    @TableField("code")
    private String code;

    /**
     * 铁运/海运运单号
     */
    @TableField("waybill_code")
    private String waybill_code;

    /**
     * 业务类型
     */
    @TableField("serial_type")
    private String serial_type;

    /**
     * 业务id
     */
    @TableField("serial_id")
    private Integer serial_id;

    /**
     * 毛重
     */
    @TableField("gross_weight")
    private BigDecimal gross_weight;

    /**
     * 皮重
     */
    @TableField("tare_weight")
    private BigDecimal tare_weight;

    /**
     * 净重
     */
    @TableField("net_weight")
    private BigDecimal net_weight;

    /**
     * 集装箱箱号照片
     */
    @TableField("file_one")
    private Integer file_one;

    /**
     * 集装箱内部空箱照片
     */
    @TableField("file_two")
    private Integer file_two;

    /**
     * 集装箱装货视频
     */
    @TableField("file_three")
    private Integer file_three;

    /**
     * 磅单
     */
    @TableField("file_four")
    private Integer file_four;

    /**
     * 表明此属性不是数据库表的字段
     */
    @TableField(exist = false)
    private Boolean skipAutoFill = false;
}
