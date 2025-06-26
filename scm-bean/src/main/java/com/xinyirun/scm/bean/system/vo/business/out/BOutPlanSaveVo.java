package com.xinyirun.scm.bean.system.vo.business.out;


import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 出库计划新增修改
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "出库计划新增修改", description = "出库计划新增修改")
public class BOutPlanSaveVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -2399892729889880890L;
    /**
     * 出库计划明细id
     */
    private Integer id;

    /**
     * 出库计划id
     */
    private Integer plan_id;

    /**
     * 计划单号
     */
    private String plan_code;

    /**
     * 委托时间
     */
    private LocalDateTime plan_time;

    /**
     * 单据状态
     */
    private String status;

    /**
     * 单据状态值
     */
    private String status_name;

    /**
     * 单据类型:0供应链业务 1代理业务 2直销业务 3废钢业务
     */
    private String bill_type;

    /**
     * 单据类型值
     */
    private String bill_type_name;

    /**
     * 出库类型
     */
    private String type;

    /**
     * 锁库存开关
     */
    private Boolean inventory_lock;

    /**
     * 出库类型值
     */
    private String type_name;

    /**
     * 合同日期
     */
    private LocalDateTime contract_dt;

    /**
     * 合同量
     */
    private BigDecimal contract_num;

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
     * 订单Id
     */
    private Integer order_id;

    /**
     * 订单类型
     */
    private String order_type;


    /**
     * 订单明细编号
     */
    private String order_detail_no;

    /**
     * 合同编号
     */
    private String contract_no;

    /**
     * 客户id
     */
    private Integer client_id;

    /**
     * 客户编码
     */
    private String client_code;

    /**
     * 客户名
     */
    private String client_name;

    /**
     * 客户id
     */
    private Integer customer_id;

    /**
     * 客户编码
     */
    private String customer_code;

    /**
     * 客户名
     */
    private String customer_name;

    /**
     * 委托方id
     */
    private Integer consignor_id;

    /**
     * 委托方code
     */
    private String consignor_code;

    /**
     * 委托方名
     */
    private String consignor_name;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主code
     */
    private String owner_code;

    /**
     * 货主名
     */
    private String owner_name;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 仓库名
     */
    private String warehouse_name;

    /**
     * 库区id
     */
    private Integer location_id;

    /**
     * 库区名
     */
    private String location_name;

    /**
     * 库位id
     */
    private Integer bin_id;

    /**
     * 库位名
     */
    private String bin_name;

    /**
     * 可用库存
     */
    private BigDecimal qty_avaible;

    /**
     * 出库单详情list
     */
    private List<BOutPlanDetailVo> detailList;

    /**
     * 作废备注
     */
    private String cancel_remark;

    /**
     * 放货指令编号
     */
    private String release_order_code;

    /**
     * 放货指令编号
     */
    private String extra_code;

    /**
     * 放货指令
     */
    private Integer sync_id;

    /**
     * 上浮百分比
     */
    private BigDecimal over_inventory_upper;

    /**
     * 车牌号
     */
    private String vehicle_no;

    /**
     * 备注
     */
    private String remark;

    /**
     * 表单数据
     */
    private JSONObject form_data;

    /**
     * 初始化审批流程
     */
    private String initial_process;

    /**
     * 自选数据
     */
    private Map<String, List<OrgUserVo>> process_users;
}
