package com.xinyirun.scm.bean.system.vo.master.warhouse;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <p>
 * 仓库
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "仓库", description = "仓库")
public class MWarehouseVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 1021499829475973079L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 仓库编码
     */
    private String code;

    private Integer type;

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
     * 片区
     */
    private String zone;
    private String zone_name;

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
     * 仓库面积
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
    private String warehouse_type_name;

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
     * 板块
     */
    private String business_type;

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
     * 创建人姓名
     */
    private String c_name;

    /**
     * 修改人姓名
     */
    private String u_name;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 省市区
     */
    private String cascader_areas;

    /**
     * 省市區名稱
     */
    private String cascader_areas_name;

    /**
     * 仓库组数量
     */
    private Integer warehouse_group_count;

    /**
     * 综合名称：全称，简称，拼音，简拼
     */
    private String combine_search_condition;

    /**
     * 仓库关系级别
     * 1：1级，2：二级；3：3级
     */
    private String b_warehouse_group_type;

    /**
     * 仓库组名称
     */
    private String warehouse_group_name;

    /**
     * id集合
     */
    private Long[] ids;

    private Long staff_id;
}
