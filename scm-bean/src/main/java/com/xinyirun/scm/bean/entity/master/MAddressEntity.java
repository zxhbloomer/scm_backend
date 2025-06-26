package com.xinyirun.scm.bean.entity.master;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 地址簿
 * </p>
 *
 * @author zxh
 * @since 2019-10-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_address")
public class MAddressEntity implements Serializable {

    private static final long serialVersionUID = 4921853425830909222L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 邮编
     */
    @TableField("postal_code")
    private String postal_code;

    /**
     * 联系人
     */
    @TableField("link_man")
    private String link_man;

    /**
     * 电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 默认
     */
    @TableField("is_default")
    private Boolean is_default;

    /**
     * 标签
     */
    @TableField("tag")
    private String tag;

    /**
     * 省
     */
    @TableField("province_code")
    private Integer province_code;

    /**
     * 市
     */
    @TableField("city_code")
    private Integer city_code;

    /**
     * 区
     */
    @TableField("area_code")
    private Integer area_code;

    /**
     * 详细地址
     */
    @TableField("detail_address")
    private String detail_address;

    /**
     * 关联单号
     */
    @TableField("serial_id")
    private Long serial_id;

    /**
     * 关联单号类型
     */
    @TableField("serial_type")
    private String serial_type;

    /**
     * 是否删除
     */
    @TableField("is_del")
    private Boolean is_del;

    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;


}
