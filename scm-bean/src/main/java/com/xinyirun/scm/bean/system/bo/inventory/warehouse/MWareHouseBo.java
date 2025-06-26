package com.xinyirun.scm.bean.system.bo.inventory.warehouse;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * <p>
 * 仓库bo
 * </p>
 *
 */
@Data
public class MWareHouseBo implements Serializable {

    private static final long serialVersionUID = 3177939059170863507L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 仓库编码
     */
    private String code;

    /**
     * 仓库名称
     */
    private String name;

    /**
     * 仓库简称
     */
    private String short_name;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    /**
     * 仓库地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contact_person;

    /**
     * 联系人手机
     */
    private String mobile_phone;

    /**
     * 是否启用
     */
    private Boolean enable;

    /**
     * 监管公司id
     */
    private Integer charge_company_id;

    /**
     * 运营公司id
     */
    private Integer operate_company_id;

    /**
     * 仓库容积
     */
    private BigDecimal warehouse_capacity;

    /**
     * 仓库面基
     */
    private BigDecimal area;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 启用日期
     */
    private LocalDateTime start_dt;

    /**
     * 到期日期
     */
    private LocalDateTime end_dt;

    /**
     * 每日收货开始时间
     */
    private LocalTime receive_start_time;

    /**
     * 每日收货结束时间
     */
    private LocalTime receive_end_time;

    /**
     * 每日发货开始时间
     */
    private LocalTime deliver_start_time;

    /**
     * 每日发货结束时间
     */
    private LocalTime deliver_end_time;

    /**
     * 仓库类型:0中心仓库,1网点仓库
     */
    private String warehouse_type;

    /**
     * 是否质检仓:0否,1是
     */
    private Boolean warehouse_check;

    /**
     * 启用库区 0-不启用 1-启用
     */
    private Boolean enable_location;

    /**
     * 启用库位 0-不启用 1-启用
     */
    private Boolean enable_bin;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 1仓库对多库区
     */
    private List<MLocationBo> locations;

    /**
     * 1仓库1库区，默认库区时使用
     */
    private MLocationBo location;
}

