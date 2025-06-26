package com.xinyirun.scm.bean.entity.master.warehouse;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_warehouse")
public class MWarehouseEntity implements Serializable {

    private static final long serialVersionUID = 2956020115627816256L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 仓库编码
     */
    @TableField("code")
    private String code;

    /**
     * 仓库名称
     */
    @TableField("name")
    private String name;

    /**
     * 仓库简称
     */
    @TableField("short_name")
    private String short_name;

    /**
     * 名称拼音
     */
    @TableField("name_pinyin")
    private String name_pinyin;

    /**
     * 简称拼音
     */
    @TableField("short_name_pinyin")
    private String short_name_pinyin;

    /**
     * 名称拼音首字母
     */
    @TableField("name_pinyin_initial")
    private String name_pinyin_initial;

    /**
     * 简称拼音首字母
     */
    @TableField("short_name_pinyin_initial")
    private String short_name_pinyin_initial;

    /**
     * 省
     */
    @TableField("province")
    private String province;

    /**
     * 市
     */
    @TableField("city")
    private String city;

    /**
     * 区
     */
    @TableField("district")
    private String district;

    /**
     * 仓库地址
     */
    @TableField("address")
    private String address;

    /**
     * 片区
     */
    @TableField("zone")
    private String zone;

    /**
     * 联系人
     */
    @TableField("contact_person")
    private String contact_person;

    /**
     * 联系人手机
     */
    @TableField("mobile_phone")
    private String mobile_phone;

    /**
     * 是否启用
     */
    @TableField("enable")
    private Boolean enable;

    /**
     * 监管公司id
     */
    @TableField("charge_company_id")
    private Integer charge_company_id;

    /**
     * 运营公司id
     */
    @TableField("operate_company_id")
    private Integer operate_company_id;

    /**
     * 仓库容积
     */
    @TableField("warehouse_capacity")
    private BigDecimal warehouse_capacity;

    /**
     * 仓库面基
     */
    @TableField("area")
    private BigDecimal area;

    /**
     * 经度
     */
    @TableField("longitude")
    private String longitude;

    /**
     * 纬度
     */
    @TableField("latitude")
    private String latitude;

    /**
     * 启用日期
     */
    @TableField("start_dt")
    private LocalDateTime start_dt;

    /**
     * 到期日期
     */
    @TableField("end_dt")
    private LocalDateTime end_dt;

    /**
     * 每日收货开始时间
     */
    @TableField("receive_start_time")
    private LocalTime receive_start_time;

    /**
     * 每日收货结束时间
     */
    @TableField("receive_end_time")
    private LocalTime receive_end_time;

    /**
     * 每日发货开始时间
     */
    @TableField("deliver_start_time")
    private LocalTime deliver_start_time;

    /**
     * 每日发货结束时间
     */
    @TableField("deliver_end_time")
    private LocalTime deliver_end_time;

    /**
     * 仓库类型:0中心仓库,1网点仓库
     */
    @TableField("warehouse_type")
    private String warehouse_type;

    /**
     * 是否质检仓:0否,1是
     */
    @TableField("warehouse_check")
    private Boolean warehouse_check;

    /**
     * 启用库区 0-不启用 1-启用
     */
    @TableField("enable_location")
    private Boolean enable_location;

    /**
     * 启用库位 0-不启用 1-启用
     */
    @TableField("enable_bin")
    private Boolean enable_bin;

    /**
     * 板块
     */
    @TableField("business_type")
    private String business_type;

    /**
     * 创建时间 
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;
}
