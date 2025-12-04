package com.xinyirun.scm.common.constant;

/**
 * @author zxh
 * @date 2019/9/23
 */
public class DictConstant {
    // 字典类型
    /**
     * 删除类型：0：未删除 1：已删除
     */
    public static final String DICT_SYS_DELETE_MAP = "sys_delete_type";
    public static final String DICT_SYS_DELETE_MAP_YES = "1";
    public static final String DICT_SYS_DELETE_MAP_NO = "0";
    public static final String DICT_SYS_DELETE_MAP_ALL = null;

    /**
     * 仓库类型 1 直属库、2 加工仓库 3. 铁路港口码头虚拟库 4.饲料厂库, 5中转港
     */
    public static final String DICT_M_WAREHOUSE_TYPE = "m_warehouse_type";
    public static final String DICT_M_WAREHOUSE_TYPE_ZX = "1";
    public static final String DICT_M_WAREHOUSE_TYPE_WD = "2";
    public static final String DICT_M_WAREHOUSE_TYPE_TL = "3";
    public static final String DICT_M_WAREHOUSE_TYPE_CL = "4";

    public static final String DICT_M_WAREHOUSE_TYPE_ZZ = "5";

    /**
     * 企业性质（1民营,2国企,3合资,4外资）
     */
    public static final String DICT_M_CUSTOMER_SCOPE = "m_customer_scope";
    public static final String DICT_M_CUSTOMER_SCOPE_MY = "1";
    public static final String DICT_M_CUSTOMER_SCOPE_GQ = "2";
    public static final String DICT_M_CUSTOMER_SCOPE_HZ = "3";
    public static final String DICT_M_CUSTOMER_SCOPE_WZ = "4";

    /**
     * 企业类型 1客户 2供应商 3仓储方 4承运商 5加工厂 6运营企业 7监管企业 0-主体企业
     */
    public static final String DICT_M_CUSTOMER_TYPE = "m_customer_type";
    public static final String DICT_M_CUSTOMER_TYPE_ZERO = "0";
    public static final String DICT_M_CUSTOMER_TYPE_ONE = "1";
    public static final String DICT_M_CUSTOMER_TYPE_TWO = "2";
    public static final String DICT_M_CUSTOMER_TYPE_THREE = "3";
    public static final String DICT_M_CUSTOMER_TYPE_FOUR = "4";
    public static final String DICT_M_CUSTOMER_TYPE_FIVE = "5";

    /**
     *审核状态 0-待审核 1-审核中 2-审核通过 3-驳回
     */
    public static final String DICT_M_CUSTOMER_STATUS = "m_customer_status";
    public static final String DICT_M_CUSTOMER_STATUS_ZERO = "0";
    public static final String DICT_M_CUSTOMER_STATUS_ONE = "1";
    public static final String DICT_M_CUSTOMER_STATUS_TWO = "2";
    public static final String DICT_M_CUSTOMER_STATUS_THREE = "3";

    /**
     * 企业类型 1客户 2供应商 3仓储方 4承运商 5加工厂 6运营企业 7监管企业
     */
    public static final String DICT_M_ENTERPRISE_TYPE = "m_enterprise_type";
    public static final String DICT_M_ENTERPRISE_TYPE_ONE = "1";
    public static final String DICT_M_ENTERPRISE_TYPE_TWO = "2";
    public static final String DICT_M_ENTERPRISE_TYPE_THREE = "3";
    public static final String DICT_M_ENTERPRISE_TYPE_FOUR = "4";
    public static final String DICT_M_ENTERPRISE_TYPE_FIVE = "5";
    public static final String DICT_M_ENTERPRISE_TYPE_SIX = "6";
    public static final String DICT_M_ENTERPRISE_TYPE_SEVEN = "7";

    /**
     * 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回 5-已作废
     */
    public static final String DICT_M_ENTERPRISE_STATUS = "m_enterprise_status";
    public static final String DICT_M_ENTERPRISE_STATUS_ZERO = "0";
    public static final String DICT_M_ENTERPRISE_STATUS_ONE = "1";
    public static final String DICT_M_ENTERPRISE_STATUS_TWO = "2";
    public static final String DICT_M_ENTERPRISE_STATUS_THREE = "3";
    // 已作废状态
    public static final String DICT_M_ENTERPRISE_STATUS_FIVE = "5";


    /**
     * 客户来源（1独立开发）
     */
    public static final String DICT_M_CUSTOMER_SOURCE = "m_customer_source";
    public static final String DICT_M_CUSTOMER_SOURCE_DL = "1";

    /**
     * 企业类型：1有限责任公司（自然人投资或控股）,2股份有限公司分公司（上市、国有股份）
     */
    public static final String DICT_M_CUSTOMER_MOLD = "m_customer_mold";
    public static final String DICT_M_CUSTOMER_MOLD_YX = "1";
    public static final String DICT_M_CUSTOMER_MOLD_GF = "2";

    /**
     * 状态：0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    public static final String DICT_B_IN_STATUS = "b_in_status";
    public static final String DICT_B_IN_STATUS_ZERO = "0";
    public static final String DICT_B_IN_STATUS_ONE = "1";
    public static final String DICT_B_IN_STATUS_TWO = "2";
    public static final String DICT_B_IN_STATUS_THREE = "3";
    public static final String DICT_B_IN_STATUS_FOUR = "4";
    public static final String DICT_B_IN_STATUS_FIVE = "5";
    public static final String DICT_B_IN_STATUS_SIX = "6";


    /**
     *  入库计划状态  0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    public static final String DICT_B_IN_PLAN_STATUS = "b_in_plan_status";
    public static final String DICT_B_IN_PLAN_STATUS_ZERO = "0";
    public static final String DICT_B_IN_PLAN_STATUS_ONE = "1";
    public static final String DICT_B_IN_PLAN_STATUS_TWO = "2";
    public static final String DICT_B_IN_PLAN_STATUS_THREE = "3";
    public static final String DICT_B_IN_PLAN_STATUS_FOUR = "4";
    public static final String DICT_B_IN_PLAN_STATUS_FIVE = "5";
    public static final String DICT_B_IN_PLAN_STATUS_SIX = "6";

    /**
     * 调整单状态：0制单,1已提交,2审核通过,3审核驳回
     */
    public static final String DICT_B_ADJUST_STATUS = "b_adjust_status";
    public static final String DICT_B_ADJUST_STATUS_SAVED = "0";
    public static final String DICT_B_ADJUST_STATUS_SUBMITTED = "1";
    public static final String DICT_B_ADJUST_STATUS_PASSED = "2";
    public static final String DICT_B_ADJUST_STATUS_RETURN = "3";

    /**
     * 调整单规则：1保持库存货值不变,2调整货值, 3调整调整量
     */
    public static final String DICT_B_ADJUST_RULE = "b_adjust_rule";
    public static final String DICT_B_ADJUST_RULE_ONE = "1";
    public static final String DICT_B_ADJUST_RULE_TWO = "2";
    public static final String DICT_B_ADJUST_RULE_THREE = "3";

    /**
     * 调整单类型 1、库存调整；2、盘盈调整；3、盘亏调整
     */
    public static final String DICT_B_ADJUST_TYPE = "b_adjust_type";
    public static final String DICT_B_ADJUST_TYPE_ONE = "1";
    public static final String DICT_B_ADJUST_TYPE_TWO = "2";
    public static final String DICT_B_ADJUST_TYPE_THREE = "3";

    /**
     * 调拨单状态：0制单,1已提交,2审核通过,3审核驳回
     */
    public static final String DICT_B_ALLOCATE_STATUS = "b_allocate_status";
    public static final String DICT_B_ALLOCATE_STATUS_SAVED = "0";
    public static final String DICT_B_ALLOCATE_STATUS_SUBMITTED = "1";
    public static final String DICT_B_ALLOCATE_STATUS_PASSED = "2";
    public static final String DICT_B_ALLOCATE_STATUS_RETURN = "3";

    /**
     * 货权转移状态：0制单,1已提交,2审核通过,3审核驳回
     */
    public static final String DICT_B_OWNER_CHANGE_STATUS = "b_owner_change_status";
    public static final String DICT_B_OWNER_CHANGE_STATUS_SAVED = "0";
    public static final String DICT_B_OWNER_CHANGE_STATUS_SUBMITTED = "1";
    public static final String DICT_B_OWNER_CHANGE_STATUS_PASSED = "2";
    public static final String DICT_B_OWNER_CHANGE_STATUS_RETURN = "3";

    /**
     * 物料转换状态：0制单,1已提交,2审核通过,3审核驳回,4作废,5执行中,6执行完成
     */
    public static final String DICT_B_MATERIAL_CONVERT_STATUS = "b_material_convert_status";
    public static final String DICT_B_MATERIAL_CONVERT_STATUS_ZERO = "0";
    public static final String DICT_B_MATERIAL_CONVERT_STATUS_ONE = "1";
    public static final String DICT_B_MATERIAL_CONVERT_STATUS_TWO = "2";
    public static final String DICT_B_MATERIAL_CONVERT_STATUS_THREE = "3";
    public static final String DICT_B_MATERIAL_CONVERT_STATUS_FOUR = "4";
    public static final String DICT_B_MATERIAL_CONVERT_STATUS_FIVE = "5";
    public static final String DICT_B_MATERIAL_CONVERT_STATUS_SIX = "6";

    /**
     * 物料转换执行类型：0单次任务 1定时任务
     */
    public static final String DICT_B_MATERIAL_CONVERT_TYPE = "b_material_convert_type";
    public static final String DICT_B_MATERIAL_CONVERT_TYPE_ZERO = "0";
    public static final String DICT_B_MATERIAL_CONVERT_TYPE_ONE = "1";

    /**
     * 物料转换频次：0每分钟 1每小时 2每天
     */
    public static final String DICT_B_MATERIAL_CONVERT_FREQUENCY = "b_material_convert_frequency";
    public static final String DICT_B_MATERIAL_CONVERT_FREQUENCY_ZERO = "0";
    public static final String DICT_B_MATERIAL_CONVERT_FREQUENCY_ONE = "1";
    public static final String DICT_B_MATERIAL_CONVERT_FREQUENCY_TWO = "2";

    /**
     * 盘点状态：0制单,2审核通过,4作废
     */
    public static final String DICT_B_CHECK_STATUS = "b_check_status";
    public static final String DICT_B_CHECK_STATUS_SAVED = "0";
    public static final String DICT_B_CHECK_STATUS_PASSED = "1";
    public static final String DICT_B_CHECK_STATUS_CANCEL = "2";

    /**
     * 盘点操作状态:0未盘点 1盘点中 2已盘点
     */
    public static final String DICT_B_CHECK_OPERATE_STATUS = "b_check_operate_status";
    public static final String DICT_B_CHECK_OPERATE_STATUS_CHECK = "0";
    public static final String DICT_B_CHECK_OPERATE_STATUS_CHECKING = "1";
    public static final String DICT_B_CHECK_OPERATE_STATUS_CHECKED = "2";

    /**
     * 盘盈盘亏单状态：0制单,1审核通过,2作废
     */
    public static final String DICT_B_CHECK_RESULT_STATUS = "b_check_status";
    public static final String DICT_B_CHECK_RESULT_STATUS_SAVED = "0";
    public static final String DICT_B_CHECK_RESULT_STATUS_PASSED = "1";
    public static final String DICT_B_CHECK_RESULT_STATUS_CANCEL = "2";

    /**
     * 出库计划状态：0-待审批，1-审批中，2-执行中，3-驳回，4-作废审批中，5-已作废，6-已完成
     */
    public static final String DICT_B_OUT_PLAN_STATUS = "b_out_plan_status";
    public static final String DICT_B_OUT_PLAN_STATUS_ZERO = "0";   // 待审批
    public static final String DICT_B_OUT_PLAN_STATUS_ONE = "1";    // 审批中
    public static final String DICT_B_OUT_PLAN_STATUS_TWO = "2";    // 执行中
    public static final String DICT_B_OUT_PLAN_STATUS_THREE = "3";  // 驳回
    public static final String DICT_B_OUT_PLAN_STATUS_FOUR = "4";   // 作废审批中
    public static final String DICT_B_OUT_PLAN_STATUS_FIVE = "5";   // 已作废
    public static final String DICT_B_OUT_PLAN_STATUS_SIX = "6";    // 已完成

    /**
     * 状态：0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    public static final String DICT_B_OUT_STATUS = "b_out_status";
    public static final String DICT_B_OUT_STATUS_ZERO = "0";
    public static final String DICT_B_OUT_STATUS_ONE = "1";
    public static final String DICT_B_OUT_STATUS_TWO = "2";
    public static final String DICT_B_OUT_STATUS_THREE = "3";
    public static final String DICT_B_OUT_STATUS_FOUR = "4";
    public static final String DICT_B_OUT_STATUS_FIVE = "5";
    public static final String DICT_B_OUT_STATUS_SIX = "6";

    /**
     * 入库单类型：0采购入库,1调拨入库,2退货入库,3监管入库,4普通入库,5生产入库,6提货入库 ,7监管退货
     */
    public static final String DICT_B_IN_TYPE = "b_in_type";
    public static final String DICT_B_IN_TYPE_ZERO = "0";
    public static final String DICT_B_IN_TYPE_ONE = "1";
    public static final String DICT_B_IN_TYPE_TWO = "2";
    public static final String DICT_B_IN_TYPE_THREE = "3";
    public static final String DICT_B_IN_TYPE_FOUR = "4";
    public static final String DICT_B_IN_TYPE_FIVE = "5";
    public static final String DICT_B_IN_TYPE_SIX = "6";
    public static final String DICT_B_IN_TYPE_SEVEN = "7";


    /**
     * 入库计划类型：0采购入库,1调拨入库,2退货入库,3监管入库,4普通入库,5生产入库,6提货入库 ,7监管退货
     */
    public static final String DICT_B_IN_PLAN_TYPE = "b_in_plan_type";
    public static final String DICT_B_IN_PLAN_TYPE_ZERO = "0";
    public static final String DICT_B_IN_PLAN_TYPE_ONE = "1";
    public static final String DICT_B_IN_PLAN_TYPE_TWO = "2";
    public static final String DICT_B_IN_PLAN_TYPE_THREE = "3";
    public static final String DICT_B_IN_PLAN_TYPE_FOUR = "4";
    public static final String DICT_B_IN_PLAN_TYPE_FIVE = "5";
    public static final String DICT_B_IN_PLAN_TYPE_SIX = "6";
    public static final String DICT_B_IN_PLAN_TYPE_SEVEN = "7";

    /**
     * 出库计划类型：0销售出库,1调拨出库,2退货出库,3监管出库,4普通出库,5生产出库,6提货出库 ,7监管退货
     */
    public static final String DICT_B_OUT_PLAN_TYPE = "b_out_plan_type";
    public static final String DICT_B_OUT_PLAN_TYPE_ZERO = "0";  // 销售出库
    public static final String DICT_B_OUT_PLAN_TYPE_ONE = "1";
    public static final String DICT_B_OUT_PLAN_TYPE_TWO = "2";
    public static final String DICT_B_OUT_PLAN_TYPE_THREE = "3";
    public static final String DICT_B_OUT_PLAN_TYPE_FOUR = "4";
    public static final String DICT_B_OUT_PLAN_TYPE_FIVE = "5";
    public static final String DICT_B_OUT_PLAN_TYPE_SIX = "6";
    public static final String DICT_B_OUT_PLAN_TYPE_SEVEN = "7";

    /**
     * 出库单类型：0销售出库,1调拨出库,2退货出库,3监管出库,4普通出库,5生产出库,6提货出库 ,7监管退货
     */
    public static final String DICT_B_OUT_TYPE = "b_out_type";
    public static final String DICT_B_OUT_TYPE_ZERO = "0";
    public static final String DICT_B_OUT_TYPE_ONE = "1";
    public static final String DICT_B_OUT_TYPE_TWO = "2";
    public static final String DICT_B_OUT_TYPE_THREE = "3";
    public static final String DICT_B_OUT_TYPE_FOUR = "4";
    public static final String DICT_B_OUT_TYPE_FIVE = "5";
    public static final String DICT_B_OUT_TYPE_SIX = "6";
    public static final String DICT_B_OUT_TYPE_SEVEN = "7";

    /**
     * 入库单单据类型:0供应链业务 1代理业务 2直销业务 3废钢业务
     */
    public static final String DICT_B_IN_BUSINESS_TYPE = "b_in_bill_type";
    public static final String DICT_B_IN_BUSINESS_TYPE_GYL = "0";
    public static final String DICT_B_IN_BUSINESS_TYPE_DL = "1";
    public static final String DICT_B_IN_BUSINESS_TYPE_ZX = "2";
    public static final String DICT_B_IN_BUSINESS_TYPE_FG = "3";

    /**
     * 入库计划单据类型:0供应链业务 1代理业务 2直销业务 3废钢业务 4直采业务
     */
    public static final String DICT_B_IN_PLAN_BUSINESS_TYPE = "b_in_plan_bill_type";
    public static final String DICT_B_IN_PLAN_BUSINESS_TYPE_GYL = "0";
    public static final String DICT_B_IN_PLAN_BUSINESS_TYPE_DL = "1";
    public static final String DICT_B_IN_PLAN_BUSINESS_TYPE_ZX = "2";
    public static final String DICT_B_IN_PLAN_BUSINESS_TYPE_FG = "3";
    public static final String DICT_B_IN_PLAN_BUSINESS_TYPE_ZCRK = "4";

    /**
     * 出库计划单据类型:0供应链业务 1代理业务 2直销业务 3废钢业务 4直销业务
     */
    public static final String DICT_B_OUT_PLAN_BUSINESS_TYPE = "b_out_plan_bill_type";
    public static final String DICT_B_OUT_PLAN_BUSINESS_TYPE_GYL = "0";
    public static final String DICT_B_OUT_PLAN_BUSINESS_TYPE_DL = "1";
    public static final String DICT_B_OUT_PLAN_BUSINESS_TYPE_ZX = "2";
    public static final String DICT_B_OUT_PLAN_BUSINESS_TYPE_FG = "3";
    public static final String DICT_B_OUT_PLAN_BUSINESS_TYPE_ZXCK = "4";

    /**
     * 调拨单单据类型:0供应链业务 1代理业务 2直销业务 3废钢业务
     */
    public static final String DICT_B_ALLOCATE_BUSINESS_TYPE = "b_allocate_bill_type";
    public static final String DICT_B_ALLOCATE_BUSINESS_TYPE_GYL = "0";
    public static final String DICT_B_ALLOCATE_BUSINESS_TYPE_DL = "1";
    public static final String DICT_B_ALLOCATE_BUSINESS_TYPE_ZX = "2";
    public static final String DICT_B_ALLOCATE_BUSINESS_TYPE_FG = "3";

    /**
     * 货权转移类型:0供应链业务 1代理业务 2直销业务 3废钢业务
     */
    public static final String DICT_B_OWNER_CHANGE_ORDER_BUSINESS_TYPE = "b_owner_change_bill_type";
    public static final String DICT_B_OWNER_CHANGE_ORDER_BUSINESS_TYPE_GYL = "0";
    public static final String DICT_B_OWNER_CHANGE_ORDER_BUSINESS_TYPE_DL = "1";
    public static final String DICT_B_OWNER_CHANGE_ORDER_BUSINESS_TYPE_ZX = "2";
    public static final String DICT_B_OWNER_CHANGE_ORDER_BUSINESS_TYPE_FG = "3";

    /**
     * 待办事项状态:0未完成 1已完成
     */
    public static final String DICT_B_TODO_STATUS= "b_todos_status";
    public static final String DICT_B_TODO_STATUS_TODO = "0";
    public static final String DICT_B_TODO_STATUS_ALREADY = "1";

    /**
     * 监管任务发货类型:0出库,1提货
     */
    public static final String DICT_B_MONITOR_TYPE_OUT= "b_monitor_type_out";
    public static final String DICT_B_MONITOR_TYPE_OUT_OUT = "0";
    public static final String DICT_B_MONITOR_TYPE_OUT_DELIVERY = "1";

    /**
     * 监管任务发货类型:0出库,1提货
     */
    public static final String DICT_B_MONITOR_TYPE_IN= "b_monitor_type_in";
    public static final String DICT_B_MONITOR_TYPE_IN_IN = "0";
    public static final String DICT_B_MONITOR_TYPE_IN_UNLOAD = "1";

    /**
     * 物流订单 出库计划生成方式：0 系统生成出库计划 1 手动选择出库计划
     */
    public static final String DICT_B_LOGISTICS_OUT_RULE = "b_logistics_out_rule";
    public static final String DICT_B_LOGISTICS_OUT_RULE_0 = "0";
    public static final String DICT_B_LOGISTICS_OUT_RULE_1 = "1";

    /**
     * 物流订单 入库计划生成方式：0 系统生成入库计划 1 手动选择入库计划
     */
    public static final String DICT_B_LOGISTICS_IN_RULE = "b_logistics_in_rule";
    public static final String DICT_B_LOGISTICS_IN_RULE_0 = "0";
    public static final String DICT_B_LOGISTICS_IN_RULE_1 = "1";

    /**
     * 物流订单运输方式：0公运、1铁运、2水运
     */
    public static final String DICT_B_SCHEDULE_TRANSPORT_TYPE = "b_schedule_transport_type";
    public static final String DICT_B_SCHEDULE_TRANSPORT_TYPE_0 = "0";
    public static final String DICT_B_SCHEDULE_TRANSPORT_TYPE_1 = "1";
    public static final String DICT_B_SCHEDULE_TRANSPORT_TYPE_2 = "2";

    /**
     * 审核意见类型：0拒绝,1同意
     */
    public static final String DICT_AUDIT_INFO_TYPE_FALSE = "0";
    public static final String DICT_AUDIT_INFO_TYPE_TRUE = "1";

    /**
     * 交货方式:0自提,1物流
     */
    public static final String DICT_B_ORDER_DELIVERY_TYPE = "delivery_type";
    public static final String DICT_B_ORDER_DELIVERY_TYPE_ONE = "1";
    public static final String DICT_B_ORDER_DELIVERY_TYPE_TWO = "2";

    /**
     * 出库计划是否需要调度：0否,1是
     */
    public static final String DICT_B_OUT_PLAN_SCHEDULE_STATUS = "b_out_plan_schedule_status";
    public static final String DICT_B_OUT_PLAN_SCHEDULE_STATUS_FALSE = "0";
    public static final String DICT_B_OUT_PLAN_SCHEDULE_STATUS_TRUE = "1";

    /**
     * 监管任务_状态：0空车过磅,1正在装货,2重车出库,3装货完成 4重车过磅,5正在卸货,6空车出库,7卸货完成（完成）,8作废
     *
     * 青盛状态 2,重车出库 6,空车出库，7卸货完成（完成）,8作废
     */
    public static final String DICT_B_MONITOR_STATUS = "b_monitor_status";
    public static final String DICT_B_MONITOR_STATUS_ZERO = "0";
    public static final String DICT_B_MONITOR_STATUS_ONE = "1";
    public static final String DICT_B_MONITOR_STATUS_TWO = "2";
    public static final String DICT_B_MONITOR_STATUS_THREE = "3";
    public static final String DICT_B_MONITOR_STATUS_FOUR = "4";
    public static final String DICT_B_MONITOR_STATUS_FIVE = "5";
    public static final String DICT_B_MONITOR_STATUS_SIX = "6";
    public static final String DICT_B_MONITOR_STATUS_SEVEN = "7";
    public static final String DICT_B_MONITOR_STATUS_EIGHT = "8";

    /**
     * 监管任务_结算状态：0未结算,1已结算
     */
    public static final String DICT_B_MONITOR_SETTLEMENT_STATUS = "b_monitor_settlement_status";
    public static final String DICT_B_MONITOR_SETTLEMENT_STATUS_ZERO = "0";
    public static final String DICT_B_MONITOR_SETTLEMENT_STATUS_ONE = "1";

    /**
     * 监管任务_审核状态: 0待审核 1审核驳回 2审核通过 3出库审核通过 4入库审核通过
     */
    public static final String DICT_B_MONITOR_AUDIT_STATUS = "b_monitor_audit_status";
    public static final String DICT_B_MONITOR_AUDIT_STATUS_ZERO = "0";
    public static final String DICT_B_MONITOR_AUDIT_STATUS_ONE = "1";
    public static final String DICT_B_MONITOR_AUDIT_STATUS_TWO = "2";
    public static final String DICT_B_MONITOR_AUDIT_STATUS_THREE = "3";
    public static final String DICT_B_MONITOR_AUDIT_STATUS_FOUR = "4";

    /**
     * 监管任务_出库状态：0空车过磅,1正在装货,2重车出库,3装货完成
     */
    public static final String DICT_B_MONITOR_OUT_STATUS = "b_monitor_out_status";
    public static final String DICT_B_MONITOR_OUT_STATUS_EMPTY = "0";
    public static final String DICT_B_MONITOR_OUT_STATUS_LOADING = "1";
    public static final String DICT_B_MONITOR_OUT_STATUS_OUT = "2";
    public static final String DICT_B_MONITOR_OUT_STATUS_FINISH = "3";

    /**
     * 监管任务_提货状态：0空车过磅,1正在装货,2重车出库,3装货完成
     */
    public static final String DICT_B_MONITOR_DELIVERY_STATUS = "b_monitor_delivery_status";
    public static final String DICT_B_MONITOR_DELIVERY_STATUS_EMPTY = "0";
    public static final String DICT_B_MONITOR_DELIVERY_STATUS_LOADING = "1";
    public static final String DICT_B_MONITOR_DELIVERY_STATUS_OUT = "2";
    public static final String DICT_B_MONITOR_DELIVERY_STATUS_FINISH = "3";

    /**
     * 监管任务_入库状态：4重车过磅,5正在卸货,6空车出库,7卸货完成
     */
    public static final String DICT_B_MONITOR_IN_STATUS = "b_monitor_in_status";
    public static final String DICT_B_MONITOR_IN_STATUS_HEAVY = "4";
    public static final String DICT_B_MONITOR_IN_STATUS_UNLOADING = "5";
    public static final String DICT_B_MONITOR_IN_STATUS_EMPTY = "6";
    public static final String DICT_B_MONITOR_IN_STATUS_FINISH = "7";

    /**
     * 监管任务_卸货状态：4重车过磅,5正在卸货,6空车出库,7卸货完成
     */
    public static final String DICT_B_MONITOR_UNLOAD_STATUS = "b_monitor_unload_status";
    public static final String DICT_B_MONITOR_UNLOAD_STATUS_HEAVY = "4";
    public static final String DICT_B_MONITOR_UNLOAD_STATUS_UNLOADING = "5";
    public static final String DICT_B_MONITOR_UNLOAD_STATUS_EMPTY = "6";
    public static final String DICT_B_MONITOR_UNLOAD_STATUS_FINISH = "7";

    /**
     * 订单类型 b_in_order:采购订单 b_out_order 销售订单
     */
    public static final String DICT_B_ORDER_TYPE = "b_order_type";
    public static final String DICT_B_ORDER_IN = "b_in_order";
    public static final String DICT_B_ORDER_OUT = "b_out_order";

    /**
     * 采购订单状态： 状态 0执行中 1已完成 -1作废
     */
    public static final String DICT_B_IN_ORDER_STATUS = "b_in_order_status";
    public static final String DICT_B_IN_ORDER_STATUS_ONE = "-1";
    public static final String DICT_B_IN_ORDER_STATUS_TWO = "0";
    public static final String DICT_B_IN_ORDER_STATUS_THREE = "1";

    /**
     * 销售订单状态： 状态 0执行中 1已完成 -1作废
     */
    public static final String DICT_B_OUT_ORDER_STATUS = "b_out_order_status";
    public static final String DICT_B_OUT_ORDER_STATUS_ONE = "-1";
    public static final String DICT_B_OUT_ORDER_STATUS_TWO = "0";
    public static final String DICT_B_OUT_ORDER_STATUS_THREE = "1";

    /**
     * 库存流水类型
     *     IN_CREATE("10", "入库单生成"),                    // 数量进入锁定库存
     *     IN_AGREE("11", "入库单审核同意"),                 // 锁定库存转入可用库存,锁定库存释放
     *     IN_NOT_CANCEL("12", "入库单审核驳回"),            // 锁定库存释放
     *     IN_CANCEL("13", "入库单作废"),                   // 制单时：锁定时库存释放,审核通过时可用库存释放
     *     OUT_CREATE("20", "出库单生成"),
     *     OUT_AGREE("21", "出库单生成审核同意"),
     *     OUT_NOT_AGREE("22", "出库单审核驳回"),
     *     OUT_CANCEL("23", "出库单作废"),
     *     ADJUST_CREATE("30", "调整单生成"),
     *     ADJUST_AGREE("31", "调整单审核同意"),
     *     ADJUST_NOT_AGREE("32", "调整单审核驳回"),
     *     ADJUST_CANCELLED("33", "调整单作废"),
     */
    public static final String DICT_B_INVENTORY_ACCOUNT_TYPE = "b_inventory_account_type";
    public static final String DICT_B_INVENTORY_ACCOUNT_TYPE_IN_CREATE = "10";
    public static final String DICT_B_INVENTORY_ACCOUNT_TYPE_IN_AGREE = "11";
    public static final String DICT_B_INVENTORY_ACCOUNT_TYPE_IN_NOT_AGREE = "12";
    public static final String DICT_B_INVENTORY_ACCOUNT_TYPE_IN_CANCEL = "13";
    public static final String DICT_B_INVENTORY_ACCOUNT_TYPE_IN_SUBMIT = "14";
    public static final String DICT_B_INVENTORY_ACCOUNT_TYPE_OUT_CREATE = "20";
    public static final String DICT_B_INVENTORY_ACCOUNT_TYPE_OUT_AGREE = "21";
    public static final String DICT_B_INVENTORY_ACCOUNT_TYPE_OUT_NOT_AGREE = "22";
    public static final String DICT_B_INVENTORY_ACCOUNT_TYPE_OUT_CANCEL = "23";
    public static final String DICT_B_INVENTORY_ACCOUNT_TYPE_OUT_SUBMIT = "24";
    public static final String DICT_B_INVENTORY_ACCOUNT_TYPE_OUT_EXPIRES = "26";
    public static final String DICT_B_INVENTORY_ACCOUNT_TYPE_ADJUST_CREATE = "30";
    public static final String DICT_B_INVENTORY_ACCOUNT_TYPE_ADJUST_AGREE = "31";
    public static final String DICT_B_INVENTORY_ACCOUNT_TYPE_ADJUST_NOT_AGREE = "32";
    public static final String DICT_B_INVENTORY_ACCOUNT_TYPE_ADJUST_CANCELLED = "33";

    /**
     * 操作日志服务
     */
    public static final String DICT_SYS_TABLE_TYPE = "sys_table_type";

    /**
     * 性别：
     */
    public static final String DICT_SYS_SEX_TYPE  = "sys_sex_type";

    /**
     * 在职情况：
     */
    public static final String DICT_USR_SERVICE_TYPE  = "usr_service_type";

    /**
     * 学历情况：
     */
    public static final String DICT_USR_DEGREE_TYPE  = "usr_degree_type";

    /**
     * 婚否：
     */
    public static final String DICT_USR_WED_TYPE  = "usr_wed_type";

    /**
     * 调度表状态:0待调度,1已完成,2制单,3已提交,4已驳回, 5已作废
     */
    public static final String DICT_B_SCHEDULE_STATUS = "b_schedule_status";
    public static final String DICT_B_SCHEDULE_STATUS_ZERO = "0";
    public static final String DICT_B_SCHEDULE_STATUS_ONE = "1";
    public static final String DICT_B_SCHEDULE_STATUS_TWO = "2";
    public static final String DICT_B_SCHEDULE_STATUS_THREE = "3";
    public static final String DICT_B_SCHEDULE_STATUS_FOUR = "4";
    public static final String DICT_B_SCHEDULE_STATUS_FIVE = "5";

    /**
     * 调度单是否删除, 0否, 1是
     */
    public static final String DICT_B_IS_DELETE = "b_schedule_is_delete";
    public static final String DICT_B_IS_DELETE_FALSE = "0";
    public static final String DICT_B_IS_DELETE_TRUE = "1";

    /**
     * 调度单入库类型 0-入库 1-卸货
     */
    public static final String DICT_B_SCHEDULE_IN_TYPE = "b_schedule_in_type";
    public static final String DICT_B_SCHEDULE_IN_TYPE_IN = "0";
    public static final String DICT_B_SCHEDULE_IN_TYPE_UNLOAD = "1";

    /**
     * 调度单出库类型 0-出库 1-提货
     */
    public static final String DICT_B_SCHEDULE_OUT_TYPE = "b_schedule_out_type";
    public static final String DICT_B_SCHEDULE_OUT_TYPE_OUT = "0";
    public static final String DICT_B_SCHEDULE_OUT_TYPE_DELIVERY = "1";


    /**
     * 隐藏显示类型： 0：显示 1：隐藏 null:全部
     */
    public static final String DICT_SYS_VISIBLE_TYPE = "sys_visible_type";
    public static final String DICT_SYS_VISIBLE_TYPE_SHOW = "0";
    public static final String DICT_SYS_VISIBLE_TYPE_HIDDEN = "1";
    public static final String DICT_SYS_VISIBLE_TYPE_ALL = null;

    /**
     * 按钮类型：
     */
    public static final String DICT_BTN_NAME_TYPE = "sys_btn_type";

    /**
     * 关联表类型：
     */
    public static final String DICT_SYS_SERIAL_TYPE = "sys_serial_type";

    /**
     * 地址簿_tag标签：
     */
    public static final String DICT_SYS_ADDRESS_TAG_TYPE  = "sys_address_tag_type";

    /**
     * 企业类型：
     */
    public static final String DICT_SYS_COMPANY_TYPE  = "sys_company_type";

    /**
     * 登录用户类型：
     */
    public static final String DICT_USR_LOGIN_TYPE  = "usr_login_type";
    /** 系统用户=10,职员=20,客户=30,供应商=40,其他=50,认证管理员=60,审计管理员=70 */
    public static final String DICT_USR_LOGIN_TYPE_ADMIN  = "60";

    /**
     * 登录用户类型：
     */
    public static final String DICT_SYS_LOGIN_TYPE  = "sys_login_type";
    /** 登录模式：（10：手机号码；20：邮箱） */
    public static final String DICT_SYS_LOGIN_TYPE_MOBILE  = "10";
    public static final String DICT_SYS_LOGIN_TYPE_EMAIL  = "20";

    /**
     * 组织架构类型：
     */
    public static final String DICT_ORG_SETTING_TYPE  = "org_setting_type";
//    public static final String DICT_ORG_SETTING_TYPE_TENANT  = "10";
//    public static final String DICT_ORG_SETTING_TYPE_TENANT_SERIAL_TYPE  = "s_tenant";
    public static final String DICT_ORG_SETTING_TYPE_GROUP  = "20";
    public static final String DICT_ORG_SETTING_TYPE_GROUP_SERIAL_TYPE  = "m_group";
    public static final String DICT_ORG_SETTING_TYPE_COMPANY  = "30";
    public static final String DICT_ORG_SETTING_TYPE_COMPANY_SERIAL_TYPE  = "m_company";
    public static final String DICT_ORG_SETTING_TYPE_DEPT  = "40";
    public static final String DICT_ORG_SETTING_TYPE_DEPT_SERIAL_TYPE  = "m_dept";
    public static final String DICT_ORG_SETTING_TYPE_POSITION  = "50";
    public static final String DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE  = "m_position";
    public static final String DICT_ORG_SETTING_TYPE_STAFF  = "60";
    public static final String DICT_ORG_SETTING_TYPE_STAFF_SERIAL_TYPE  = "m_staff";

    /**
     * 自动编号设置：
     */
    public static final String DICT_SYS_CODE_RULE_TYPE  = "sys_coderule_type";
    // YYYYMMDD??999
    public static final String DICT_SYS_CODE_RULE_TYPE_ONE  = "10";
    // P9999
    public static final String DICT_SYS_CODE_RULE_TYPE_TWO  = "11";
    // PC9999
    public static final String DICT_SYS_CODE_RULE_TYPE_THREE  = "12";
    // YYYYMMDD9999
    public static final String DICT_SYS_CODE_RULE_TYPE_FOUR  = "13";

    /**
     * 自动编号名称：
     */
    public static final String DICT_SYS_CODE_TYPE  = "sys_code_type";
    /** 租户编号 */
    public static final String DICT_SYS_CODE_TYPE_S_TENANT  = "s_tenant";
    /** 菜单组编号 */
    public static final String DICT_SYS_CODE_TYPE_S_MENU  = "s_menu";
    /** 角色编号 */
    public static final String DICT_SYS_CODE_TYPE_S_ROLE  = "s_role";
    /** 集团主表编号 */
    public static final String DICT_SYS_CODE_TYPE_M_GROUP  = "m_group";
    /** 部门主表编号 */
    public static final String DICT_SYS_CODE_TYPE_M_DEPT  = "m_dept";
    /** 岗位主表编号 */
    public static final String DICT_SYS_CODE_TYPE_M_POSITION  = "m_position";
    /** 公司编号 */
    public static final String DICT_SYS_CODE_TYPE_M_COMPANY  = "m_company";
    /** 员工编号 */
    public static final String DICT_SYS_CODE_TYPE_M_STAFF  = "m_staff";
    /** vue页面配置表 */
    public static final String DICT_SYS_CODE_TYPE_S_VUE_PAGE_SETTING  = "s_vue_page_setting";
    /** 菜单主表-路由编号 */
    public static final String DICT_SYS_CODE_TYPE_M_MENU  = "m_menu";
    /** 入库计划明细编号 */
    public static final String DICT_SYS_CODE_TYPE_B_IN_PLAN_DETAIL  = "b_in_plan_detail";

    /** 入库计划明细编号v2 */
    public static final String DICT_SYS_CODE_TYPE_B_IN_PLAN_DETAIL_V2  = "b_in_plan_detail_v2";
    /** 入库单编号 */
    public static final String DICT_SYS_CODE_TYPE_B_IN  = "b_in";
    /** 项目编号 */
    public static final String DICT_SYS_CODE_TYPE_B_PROJECT  = "b_project";
    /** 调整编号 */
    public static final String DICT_SYS_CODE_TYPE_B_ADJUST  = "b_adjust";
    /** 承运订单编号 */
    public static final String DICT_SYS_CODE_TYPE_B_CARRIAGE_ORDER  = "b_carriage_order";
    /** 调整编号 */
    public static final String DICT_SYS_CODE_TYPE_B_ADJUST_DETAIL  = "b_adjust_detail";
    /** 调度编号 */
    public static final String DICT_SYS_CODE_TYPE_B_SCHEDULE  = "b_schedule";
    /** 监管任务编号 */
    public static final String DICT_SYS_CODE_TYPE_B_MONITOR  = "b_monitor";
    /** 监管任务未审核 */
    public static final String DICT_SYS_CODE_TYPE_B_MONITOR_UNAUDITED  = "b_monitor_unaudited";
    /** 监管任务同步 */
    public static final String DICT_SYS_CODE_TYPE_B_MONITOR_SYNC  = "b_monitor_sync";
    /** 出库计划明细编号 */
    public static final String DICT_SYS_CODE_TYPE_B_OUT_PLAN_DETAIL  = "b_out_plan_detail";
    /** 出库单编号 */
    public static final String DICT_SYS_CODE_TYPE_B_OUT  = "b_out";
    /** 盘点单号 */
    public static final String DICT_SYS_CODE_TYPE_B_CHECK  = "b_check";
    public static final String DICT_SYS_CODE_TYPE_B_CHECK_OPERATE  = "b_check_operate";
    /** 库存编号 */
    public static final String DICT_SYS_CODE_TYPE_M_INVENTORY  = "m_inventory";

    /** 库存停滞港口*/
    public static final String DICT_SYS_CODE_TYPE_M_INVENTORY_STAGNATION  = "m_inventory_stag";
    /** 客户编号 */
    public static final String DICT_SYS_CODE_TYPE_M_CUSTOMER  = "m_customer";

    /** 客户编号 */
    public static final String DICT_SYS_CODE_TYPE_M_ENTERPRISE  = "m_enterprise";
    /** 调拨单编号 */
    public static final String DICT_SYS_CODE_TYPE_B_ALLOCATE  = "b_allocate";
    /** 货权转移编号 */
    public static final String DICT_SYS_CODE_TYPE_B_OWNER_CHANGE  = "b_owner_change";
    /** 物料转换 */
    public static final String DICT_SYS_CODE_TYPE_B_MATERIAL_CONVERT  = "b_material_convert";
    /** 监管任务_出库编号 */
    public static final String DICT_SYS_CODE_TYPE_B_MONITOR_OUT  = "b_monitor_out";
    /** 监管任务_入库编号 */
    public static final String DICT_SYS_CODE_TYPE_B_MONITOR_IN  = "b_monitor_in";

    /** 监管任务损耗预警TYPE值 */
    public static final String DICT_SYS_CODE_TYPE_M_MONITOR_LOSS  = "m_monitor_loss";

    /** 仓库编号 */
    public static final String DICT_SYS_CODE_TYPE_M_WAREHOUSE  = "m_warehouse";
    /** 库区编号 */
    public static final String DICT_SYS_CODE_TYPE_M_LOCATION  = "m_location";
    /** 库位编号 */
    public static final String DICT_SYS_CODE_TYPE_M_BIN  = "m_bin";
    /** 类别编号 */
    public static final String DICT_SYS_CODE_TYPE_M_CATEGORY  = "m_category";
    /** 物料编号 */
    public static final String DICT_SYS_CODE_TYPE_M_GOODS  = "m_goods";
    /** 入库计划编号 */
    public static final String DICT_SYS_CODE_TYPE_B_IN_PLAN = "b_in_plan";
    /** 出库计划编号 */
    public static final String DICT_SYS_CODE_TYPE_B_OUT_PLAN = "b_out_plan";
    /** 仓库组1编号 */
    public static final String DICT_SYS_CODE_WAREHOUSE_GROUP = "b_warehouse_group";
    /** 入库订单 */
    public static final String DICT_SYS_CODE_TYPE_B_IN_ORDER  = "b_in_order";
    /** 出库订单 */
    public static final String DICT_SYS_CODE_TYPE_B_OUT_ORDER  = "b_out_order";
    /** 车辆 */
    public static final String DICT_SYS_CODE_TYPE_M_VEHICLE  = "m_vehicle";
    /** 司机 */
    public static final String DICT_SYS_CODE_TYPE_M_DRIVER  = "m_driver";
    /** in_order_goods_id */
    public static final String DICT_SYS_CODE_TYPE_B_IN_ORDER_GOODS  = "b_in_order_goods";
    /** 通知编号 */
    public static final String DICT_SYS_CODE_TYPE_S_APP_NOTICE  = "s_app_notice";
    /** 每日库存 */
    public static final String DICT_SYS_CODE_TYPE_B_DAILY_INVENTORY  = "b_daily_inventory";
    /** 商品单价 */
    public static final String DICT_SYS_CODE_TYPE_B_MATERIAL_PRICE  = "b_material_price";
    /** 每日库存货值 */
    public static final String DICT_SYS_CODE_TYPE_B_DAILY_INVENTORY_PRICE  = "b_daily_inventory_price";
    /** 自定义表格设置 */
    public static final String DICT_SYS_CODE_TYPE_S_TABLE_CONFIG  = "s_table_config";

    /** 自定义表格设置 */
    public static final String DICT_SYS_CODE_TYPE_B_WO_PRODUCT  = "b_wo_product";

    /** 生产配方管理 */
    public static final String DICT_B_WO_ROUTER  = "b_wo_router";
    /** 生产配方产成品, 副产品 */
    public static final String DICT_B_WO_ROUTER_PRODUCT  = "b_wo_router_product";
    /** 生产配方 原材料 */
    public static final String DICT_B_WO_ROUTER_MATERIAL  = "b_wo_router_material";
    /** 生产管理单号 */
    public static final String DICT_B_WO  = "b_wo";
    /** 生产管理原材料 */
    public static final String DICT_B_WO_MATERiAL = "b_wo_material";
    /** 预警人员 */
    public static final String DICT_B_ALARM_STAFF  = "b_alarm_staff";
    /** 生产配方管理 */
    public static final String DICT_B_RT_WO_ROUTER  = "b_rt_wo_router";
    /** 生产配方产成品, 副产品 */
    public static final String DICT_B_RT_WO_ROUTER_PRODUCT  = "b_rt_wo_router_product";
    /** 生产配方 原材料 */
    public static final String DICT_B_RT_WO_ROUTER_MATERIAL  = "b_rt_wo_router_material";
    /** 生产管理单号 */
    public static final String DICT_B_RT_WO  = "b_rt_wo";
    /** 生产管理原材料 */
    public static final String DICT_B_RT_WO_MATERiAL = "b_rt_wo_material";

    /** 退货单信息 */
    public static final String DICT_B_RETURN_RELATION = "b_return_relation";

    /** 采购合同编号 */
    public static final String DICT_SYS_CODE_TYPE_B_PO_CONTRACT  = "b_po_contract";

    /** 货权转移编号 */
    public static final String DICT_SYS_CODE_TYPE_B_PO_CARGO_RIGHT_TRANSFER = "b_po_cargo_right_transfer";

    /** 销售货权转移编号 */
    public static final String DICT_SYS_CODE_TYPE_B_SO_CARGO_RIGHT_TRANSFER = "b_so_cargo_right_transfer";

    /** 采购结算 */
    public static final String DICT_SYS_CODE_TYPE_B_PO_SETTLEMENT  = "b_po_settlement";

    /** 销售结算 */
    public static final String DICT_SYS_CODE_TYPE_B_SO_SETTLEMENT  = "b_so_settlement";

    /** 销售合同编号 */
    public static final String DICT_SYS_CODE_TYPE_B_SO_CONTRACT  = "b_so_contract";


    /** 采购订单编号 */
    public static final String DICT_SYS_CODE_TYPE_B_PO_ORDER  = "b_po_order";

    /** 销售订单编号 */
    public static final String DICT_SYS_CODE_TYPE_B_SO_ORDER  = "b_so_order";

    /** 应付账款管理表 */
    public static final String DICT_SYS_CODE_TYPE_B_AP  = "b_ap";

    /** 付款单管理表 */
    public static final String DICT_SYS_CODE_TYPE_B_AP_PAY  = "b_ap_pay";

    /** 付款单明细表 */
    public static final String DICT_SYS_CODE_TYPE_B_AP_PAY_DETAIL  = "b_ap_pay_detail";

    /** 应付账款关联单据表-源单-预收款 */
    public static final String DICT_SYS_CODE_TYPE_B_AP_SOURCE_ADVANCE  = "b_ap_source_advance";

    /** 应付账款明细表 */
    public static final String DICT_SYS_CODE_TYPE_B_AP_DETAIL  = "b_ap_detail";

    /** 资金使用情况表 */
    public static final String DICT_SYS_CODE_TYPE_B_FUND_USAGE  = "b_fund_usage";

    /** 资金流水情况表 */
    public static final String DICT_SYS_CODE_TYPE_B_FUND_MONITOR = "b_fund_monitor";

    /** 应付退款管理表 */
    public static final String DICT_SYS_CODE_TYPE_B_AP_REFUND  = "b_ap_refund";

    /** 退款单管理表 */
    public static final String DICT_SYS_CODE_TYPE_B_AP_REFUND_PAY  = "b_ap_refund_pay";

    /** 退款账款关联单据表-源单-预收款 */
    public static final String DICT_SYS_CODE_TYPE_B_AP_REFUND_SOURCE_ADVANCE  = "b_ap_refund_source_advance";

    /** 应付退款明细表 */
    public static final String DICT_SYS_CODE_TYPE_B_AP_REFUND_DETAIL  = "b_ap_refund_detail";

    /** 应收账款管理表 */
    public static final String DICT_SYS_CODE_TYPE_B_AR  = "b_ar";

    /** 收款单管理表 */
    public static final String DICT_SYS_CODE_TYPE_B_AR_RECEIVE  = "b_ar_receive";

    /** 收款单明细表 */
    public static final String DICT_SYS_CODE_TYPE_B_AR_RECEIVE_DETAIL  = "b_ar_receive_detail";

    /** 应收账款关联单据表-源单-预收款 */
    public static final String DICT_SYS_CODE_TYPE_B_AR_SOURCE_ADVANCE  = "b_ar_source_advance";

    /** 应收账款明细表 */
    public static final String DICT_SYS_CODE_TYPE_B_AR_DETAIL  = "b_ar_detail";

    /** 应收退款管理表 */
    public static final String DICT_SYS_CODE_TYPE_B_AR_REFUND  = "b_ar_refund";

    /** 退款收款单管理表 */
    public static final String DICT_SYS_CODE_TYPE_B_AR_REFUND_PAY  = "b_ar_refund_pay";

    /** 应收退款收款单管理表 */
    public static final String DICT_SYS_CODE_TYPE_B_AR_REFUND_RECEIVE  = "b_ar_refund_receive";

    /** 退款应收账款关联单据表-源单-预收款 */
    public static final String DICT_SYS_CODE_TYPE_B_AR_REFUND_SOURCE_ADVANCE  = "b_ar_refund_source_advance";

    /** 应收退款明细表 */
    public static final String DICT_SYS_CODE_TYPE_B_AR_REFUND_DETAIL  = "b_ar_refund_detail";

    /** 退货单信息状态 1=审核通过 2=作废*/
    public static final String DICT_B_RETURN_RELATION_STATUS = "b_return_relation_status";
    public static final String DICT_B_RETURN_RELATION_STATUS_TG = "1";
    public static final String DICT_B_RETURN_RELATION_STATUS_ZF = "2";

    /**
     * 组织架构中已被使用
     */
    public static final String DICT_ORG_USED_TYPE  = "org_used_type";
    /** 显示组织机构中未被使用  */
    public static final String DICT_ORG_USED_TYPE_SHOW_UNUSED  = "10";
    /** 显示所有  */
    public static final String DICT_ORG_USED_TYPE_SHOW_ALL  = null;

    /**
     * 页面按钮类型
     */
    public static final String DICT_SYS_PAGES_FUN_TYPE = "sys_PAGES_FUN_type";
    public static final String DICT_SYS_PAGES_FUN_TYPE_ON_PAGE = "PAGE";
    public static final String DICT_SYS_PAGES_FUN_TYPE_ON_TABLE = "TABLE";
    public static final String DICT_SYS_PAGES_FUN_TYPE_ON_POPUP = "POPUP";

    /**
     * 菜单类型
     */
    public static final String DICT_SYS_MENU_TYPE = "sys_menu_type";
    /** ROOT：根节点 */
    public static final String DICT_SYS_MENU_TYPE_ROOT = "R";
    /** TOPNAV：顶部导航栏 */
    public static final String DICT_SYS_MENU_TYPE_TOPNAV = "T";
    /** NODE：结点 */
    public static final String DICT_SYS_MENU_TYPE_NODE = "N";
    /** PAGE：页面 */
    public static final String DICT_SYS_MENU_TYPE_PAGE = "P";

    /**
     * 权限类型
     */
    public static final String DICT_MSTR_PERMISSION_TYPE = "mstr_permission_type";
    public static final String DICT_MSTR_PERMISSION_TYPE_DEPT = "10";
    public static final String DICT_MSTR_PERMISSION_TYPE_POSITION = "20";
    public static final String DICT_MSTR_PERMISSION_TYPE_USER = "30";
    public static final String DICT_MSTR_PERMISSION_TYPE_OUT = "40";
    public static final String DICT_MSTR_PERMISSION_TYPE_ROLE = "50";

    /**
     * 定时任务组:00系统、01每日库存、02每日库存差量、03物料转换、04数据变更日志
     */
    public static final String DICT_SYS_JOB_GROUP_TYPE = "s_job_group_type";
    public static final String DICT_SYS_JOB_GROUP_TYPE_SYS = "00";
    public static final String DICT_SYS_JOB_GROUP_TYPE_DAILY_INVENTORY = "01";
    public static final String DICT_SYS_JOB_GROUP_TYPE_DAILY_INVENTORY_DIFF = "02";
    public static final String DICT_SYS_JOB_GROUP_TYPE_MATERIAL_CONVERT = "03";
    public static final String DICT_SYS_JOB_GROUP_TYPE_DATA_CHANGE = "08";
    public static final String DICT_SYS_JOB_GROUP_TYPE_AI_KB_STATISTICS = "10"; // AI知识库统计
    public static final String DICT_SYS_JOB_GROUP_TYPE_AI_TEMP_KB_CLEANUP = "11"; // AI临时知识库清理

    /**
     * 仓库分组定义：1一级；2二级；3三级
     */
    public static final String DICT_B_WAREHOUSE_GROUP = "b_warehouse_group";
    // 一级
    public static final String DICT_B_WAREHOUSE_GROUP_1 = "1";
    // 二级
    public static final String DICT_B_WAREHOUSE_GROUP_2 = "2";
    // 三级
    public static final String DICT_B_WAREHOUSE_GROUP_3 = "3";

    /**
     * app版本更新类型：0 非强制；1强制；
     */
    public static final String DICT_S_APP_NOTICE_TYPE = "s_app_notice_type";
    // 非强制
    public static final String DICT_S_APP_NOTICE_TYPE0 = "0";
    // 强制
    public static final String DICT_S_APP_NOTICE_TYPE1 = "1";

    /**
     * 仓库片区
     */
    public static final String DICT_M_WAREHOUSE_ZONE = "m_warehouse_zone";
    public static final String DICT_M_WAREHOUSE_ZONE1 = "1";
    public static final String DICT_M_WAREHOUSE_ZONE2 = "2";
    public static final String DICT_M_WAREHOUSE_ZONE3 = "3";
    public static final String DICT_M_WAREHOUSE_ZONE4 = "4";

    /**
     * 同步日志状态, 0 同步失败, 1同步成功
     */
    public static final String DICT_LOG_SYNC_STATUS_E = "0";
    public static final String DICT_LOG_SYNC_STATUS_S = "1";

    /**
     * 物流订单单据类型 1物流订单, 2物流调度, 3物流直送 4:直采入库 5:直销出库
     */
    public static final String DICT_B_SCHEDULE_TYPE = "b_schedule_type";
    public static final String DICT_B_SCHEDULE_TYPE_1 = "1";
    public static final String DICT_B_SCHEDULE_TYPE_2 = "2";
    public static final String DICT_B_SCHEDULE_TYPE_3 = "3";
    public static final String DICT_B_SCHEDULE_TYPE_4 = "4";
    public static final String DICT_B_SCHEDULE_TYPE_5 = "5";

    /**
     * 生产配方产成品,副产品属性, 1产成品, 2副产品
     */
    public static final String DICT_B_ROUTER_PRODUCT_TYPE = "b_router_pro_type";
    public static final String DICT_B_ROUTER_PRODUCT_TYPE_C = "1";
    public static final String DICT_B_ROUTER_PRODUCT_TYPE_F = "2";

    /**
     * 生产配方状态, 1启用, 0停用
     */
    public static final String DICT_B_ROUTER_ENABLE = "b_wo_router_enable";
    public static final String DICT_B_ROUTER_ENABLE_0 = "0";
    public static final String DICT_B_ROUTER_ENABLE_1 = "1";

    /**
     * 生产工单 状态, 1制单, 2已提交, 3审核通过, 4 审核驳回, 5已作废
     */
    public static final String DICT_B_WO_STATUS = "b_wo_status";
    public static final String DICT_B_WO_STATUS_1 = "1";
    public static final String DICT_B_WO_STATUS_2 = "2";
    public static final String DICT_B_WO_STATUS_3 = "3";
    public static final String DICT_B_WO_STATUS_4 = "4";
    public static final String DICT_B_WO_STATUS_5 = "5";

    /** 预警, 0 事件预警, 1阈值预警 */
    public static final String DICT_B_ALARM_RULES_TYPE = "b_alarm_rules_type";
    public static final String DICT_B_ALARM_RULES_TYPE_0 = "0";
    public static final String DICT_B_ALARM_RULES_TYPE_1 = "1";

    /** 预警规则, 1 实时预警, 2定时任务 */
    public static final String DICT_B_ALARM_SETTING_RULES = "b_alarm_setting_rules";
    public static final String DICT_B_ALARM_SETTING_RULES_1 = "1";
    public static final String DICT_B_ALARM_SETTING_RULES_2 = "2";

    /** 预警人员类型, 2 预警组, 1预警人员 */
    public static final String DICT_B_ALARM_RULES_STAFF_TYPE = "b_alarm_rules_staff_type";
    public static final String DICT_B_ALARM_RULES_STAFF_TYPE_1 = "1";
    public static final String DICT_B_ALARM_RULES_STAFF_TYPE_2 = "2";

    /** 预警是否启用, 1 启用, 0 不启用 */
    public static final String DICT_B_ALARM_RULES_IS_USING_TYPE = "b_alarm_rules_is_using_type";
    public static final String DICT_B_ALARM_RULES_IS_USING_TYPE_0 = "0";
    public static final String DICT_B_ALARM_RULES_IS_USING_TYPE_1= "1";

    /** 预警通知方式, 0 消息通知, 1 弹出显示 */
    public static final String DICT_B_ALARM_RULES_NOTICE_TYPE = "b_alarm_rules_notice_type";
    public static final String DICT_B_ALARM_RULES_NOTICE_TYPE_0 = "0";
    public static final String DICT_B_ALARM_RULES_NOTICE_TYPE_1= "1";

    /** 预警模型类型, 0通用 */
    public static final String DICT_B_BPM_TYPE = "b_bpm_type";
    public static final String DICT_DICT_B_BPM_TYPE_0 = "0";

    /** 通知消息类型, 0待办, 1预警, 2通知 */
    public static final String DICT_B_MESSAGE_TYPE = "b_message";
    public static final String DICT_B_MESSAGE_TYPE_0 = "0";
    public static final String DICT_B_MESSAGE_TYPE_1 = "1";
    public static final String DICT_B_MESSAGE_TYPE_2 = "2";

    /** 备份详情状态, 1已发送到mq, 2.已备份到mongo, 3.已完成 */
    public static final String DICT_B_MONITOR_BACKUP_DETAIL_STATUS = "b_monitor_backup_detail_status";
    public static final String DICT_B_MONITOR_BACKUP_DETAIL_STATUS_1 = "1";
    public static final String DICT_B_MONITOR_BACKUP_DETAIL_STATUS_2 = "2";
    public static final String DICT_B_MONITOR_BACKUP_DETAIL_STATUS_3 = "3";
    public static final String DICT_B_MONITOR_BACKUP_DETAIL_STATUS_4 = "4";


    /** 监管任务恢复, 1待开始, 2恢复中, 3恢复完成*/
    public static final String DICT_B_MONITOR_RESTORE_DETAIL_STATUS = "b_monitor_restore_status";
    public static final String DICT_B_MONITOR_RESTORE_DETAIL_STATUS_1 = "1";
    public static final String DICT_B_MONITOR_RESTORE_DETAIL_STATUS_2 = "2";
    public static final String DICT_B_MONITOR_RESTORE_DETAIL_STATUS_3 = "3";

    /** 备份类型, 1备份, 2恢复 */
    public static final String DICT_B_MONITOR_BACKUP_TYPE = "b_monitor_backup_type";
    public static final String DICT_B_MONITOR_BACKUP_TYPE_1 = "1";
    public static final String DICT_B_MONITOR_BACKUP_TYPE_2 = "2";

    /** mongodb 监管任务是否恢复 */
    public static final String DICT_B_MONITOR_MONGO_IS_RESTORE = "b_bk_monitor_is_restore";
    public static final String DICT_B_MONITOR_MONGO_IS_RESTORE_F = "0";
    public static final String DICT_B_MONITOR_MONGO_IS_RESTORE_T = "1";

    /** mongodb 监管任务列表是否可见, 当为 0 时不可见, 1或null可见 */
    public static final String DICT_B_MONITOR_MONGO_IS_SHOW = "b_bk_monitor_is_show";
    public static final String DICT_B_MONITOR_MONGO_IS_SHOW_F = "0";
    public static final String DICT_B_MONITOR_MONGO_IS_SHOW_T = "1";

    /**
     * 销售, 采购订单来源, 1wms, 2业务中台
     */
    public static final String DICT_B_ORDER_SOURCE_TYPE = "b_order_source_type";
    public static final String DICT_B_ORDER_SOURCE_TYPE_WMS = "1";
    public static final String DICT_B_ORDER_SOURCE_TYPE_ERP = "2";

    /**
     * 业务启动批次
     */
    public static final String DICT_B_REPORT_BUSINESS_START_DATE = "b_report_business_start_date";

    /**
     * 监管任务同步状态 0 未同步, -1不可同步, 1同步成功, 2同步失败
     */
    public static final String DICT_B_MONITOR_IS_SYNC = "b_monitor_sync_status";
    public static final String DICT_B_MONITOR_IS_SYNC_0 = "0";
    public static final String DICT_B_MONITOR_IS_SYNC_1 = "1";
    public static final String DICT_B_MONITOR_IS_SYNC_2 = "2";
    public static final String DICT_B_MONITOR_IS_SYNC_N = "-1";

    /**
     * 物流订单是否在执行中, 0执行中, 1未执行
     */
    public static final String DICT_B_SCHEDULE_IS_CONSUMER = "b_schedule_is_consumer";
    public static final String DICT_B_SCHEDULE_IS_CONSUMER_0 = "0";
    public static final String DICT_B_SCHEDULE_IS_CONSUMER_1 = "1";

    /**
     * 车辆颜色
     */
    public static final String DICT_M_VEHICLE_COLOR = "m_vehicle_no_color";
    public static final String DICT_M_VEHICLE_COLOR_BLUE = "1";
    public static final String DICT_M_VEHICLE_COLOR_YELLOW = "2";
    public static final String DICT_M_VEHICLE_COLOR_YELLOW_GREEN = "3";

    /**
     * 通知发布状态, 0未发布, 1已发布
     */
    public static final String DICT_B_NOTICE_STATUS = "b_notice_status";
    public static final String DICT_B_NOTICE_STATUS_0 = "0";
    public static final String DICT_B_NOTICE_STATUS_1 = "1";

    /**
     * 生产计划管理单号 生成
     */
    public static final String DICT_B_PP  = "b_pp";
    public static final String DICT_B_PP_STATUS  = "b_pp_status";

    public static final String DICT_B_PP_PRODUCT  = "b_pp_product";

    public static final String DICT_B_PP_MATERIAL  = "b_pp_material";

    /**
     * 生产计划管理 0制单，1已提交，2审核通过，3审核驳回，4作废，5作废审核中，6已完成
     */
    public static final String DICT_B_PP_STATUS_SAVED = "0";
    public static final String DICT_B_PP_STATUS_SUBMITTED = "1";
    public static final String DICT_B_PP_STATUS_PASSED = "2";
    public static final String DICT_B_PP_STATUS_RETURN = "3";
    public static final String DICT_B_PP_STATUS_CANCEL = "4";
    public static final String DICT_B_PP_STATUS_CANCEL_BEING_AUDITED = "5";
    public static final String DICT_B_PP_STATUS_FINISH = "6";

    /** 提货单 */
    public static final String DICT_SYS_CODE_TYPE_B_DELIVERY = "b_delivery";

    /** 提货单类型*/
    public static final String DICT_B_DELIVERY_TYPE = "b_delivery_type";

    /**
     * 提货单状态：0制单,1已提交,2审核通过,3审核驳回,4作废,5完成,6审核中
     */
    public static final String DICT_B_DELIVERY_STATUS = "b_delivery_status";
    public static final String DICT_B_DELIVERY_STATUS_SAVED = "0";
    public static final String DICT_B_DELIVERY_STATUS_SUBMITTED = "1";
    public static final String DICT_B_DELIVERY_STATUS_PASSED = "2";
    public static final String DICT_B_DELIVERY_STATUS_RETURN = "3";
    public static final String DICT_B_DELIVERY_STATUS_CANCEL = "4";
    public static final String DICT_B_DELIVERY_STATUS_FINISH = "5";
    public static final String DICT_B_DELIVERY_STATUS_CANCEL_BEING_AUDITED = "6";
    

    /**
     * 提货单单据类型:0供应链业务 1代理业务 2直销业务 3废钢业务
     */
    public static final String DICT_B_DELIVERY_BUSINESS_TYPE = "b_delivery_bill_type";
    public static final String DICT_B_DELIVERY_BUSINESS_TYPE_GYL = "0";
    public static final String DICT_B_DELIVERY_BUSINESS_TYPE_DL = "1";
    public static final String DICT_B_DELIVERY_BUSINESS_TYPE_ZX = "2";
    public static final String DICT_B_DELIVERY_BUSINESS_TYPE_FG = "3";

    /** 收货单编号 */
    public static final String DICT_SYS_CODE_TYPE_B_RECEIVE  = "b_receive";

    /** 收货单类型*/
    public static final String DICT_B_RECEIVE_TYPE = "b_receive_type";


    /**
     * 收货单单据类型:0供应链业务 1代理业务 2直销业务 3废钢业务
     */
    public static final String DICT_B_RECEIVE_BUSINESS_TYPE = "b_receive_bill_type";
    public static final String DICT_B_RECEIVE_BUSINESS_TYPE_GYL = "0";
    public static final String DICT_B_RECEIVE_BUSINESS_TYPE_DL = "1";
    public static final String DICT_B_RECEIVE_BUSINESS_TYPE_ZX = "2";
    public static final String DICT_B_RECEIVE_BUSINESS_TYPE_FG = "3";

    /**
     * 收货单状态：0制单,1已提交,2审核通过,3审核驳回,4作废,5完成,6过期,7审核中
     */
    public static final String DICT_B_RECEIVE_STATUS = "b_receive_status";
    public static final String DICT_B_RECEIVE_STATUS_SAVED = "0";
    public static final String DICT_B_RECEIVE_STATUS_SUBMITTED = "1";
    public static final String DICT_B_RECEIVE_STATUS_PASSED = "2";
    public static final String DICT_B_RECEIVE_STATUS_RETURN = "3";
    public static final String DICT_B_RECEIVE_STATUS_CANCEL = "4";
    public static final String DICT_B_RECEIVE_STATUS_FINISH = "5";
    public static final String DICT_B_RECEIVE_STATUS_EXPIRES = "6";
    public static final String DICT_B_RECEIVE_STATUS_CANCEL_BEING_AUDITED = "7";

    /**
     * 监管任务文件导出  0=监管任务 1=直销直采
     */
    public static final String DICT_B_MONITOR_FILE_EXPORT_SETTINGS = "b_monitor_file_export_settings";
    public static final String DICT_B_MONITOR_FILE_EXPORT_SETTINGS_TYPE_ZERO = "0";
    public static final String DICT_B_MONITOR_FILE_EXPORT_SETTINGS_TYPE_ONE = "1";

    /**
     * BPM流程类型
     */
    public static final String B_BPM_PROCESS_TYPE = "bpm_process_type";

    /**
     * 实例状态 执行状态 0-正在处理 1-已撤销 2-办结（已完成） 3-已驳回
     */
    public static final String DICT_SYS_CODE_BPM_INSTANCE_STATUS = "bpm_instance_status";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_STATUS_ZERO = "0";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_STATUS_ONE = "1";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_STATUS_TWO = "2";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_STATUS_THREE = "3";


    /** 实例状态 执行情况  RUNNING-进行中，COMPLETE-完成 ，PASS-审核通过，CANCEL-取消，REFUSE-拒绝 */
    public static final String DICT_SYS_CODE_BPM_INSTANCE_RESULT_RUNNING = "running";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_RESULT_COMPLETE = "complete";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_RESULT_CANCEL = "cancel";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_RESULT_REFUSE = "refuse";

    /**
     * 代办状态  0-代办 1-已办
     */
    public static final String B_BPM_TODO_STATUS = "bpm_process_type";
    public static final String B_BPM_TODO_STATUS_ZERO = "0";
    public static final String B_BPM_TODO_STATUS_ONE = "1";

    /** 流程实例化编号 */
    public static final String DICT_SYS_CODE_TYPE_BPM_INSTANCE  = "bpm_instance";

    /** 审批流程模板 */
    public static final String DICT_SYS_CODE_TYPE_BPM_PROCESS_TEMPLATES = "bpm_process_templates";

    /** 流程实例状态 审批类型  0=处理中 1=同意 2=委派 3=委派人完成 4=拒绝 5=转办 6=退回 7=加密 8=查到签上的人 9=减签 10=评论 11=已取消 */
    public static final String DICT_SYS_CODE_BPM_APPROVE_TYPE = "bpm_approve_type";
    public static final String DICT_SYS_CODE_BPM_APPROVE_ZERO = "0";
    public static final String DICT_SYS_CODE_BPM_APPROVE_ONE = "1";
    public static final String DICT_SYS_CODE_BPM_APPROVE_TWO = "2";
    public static final String DICT_SYS_CODE_BPM_APPROVE_THREE = "3";
    public static final String DICT_SYS_CODE_BPM_APPROVE_FOUR = "4";
    public static final String DICT_SYS_CODE_BPM_APPROVE_FIVE = "5";
    public static final String DICT_SYS_CODE_BPM_APPROVE_SIX = "6";
    public static final String DICT_SYS_CODE_BPM_APPROVE_SEVEN = "7";
    public static final String DICT_SYS_CODE_BPM_APPROVE_EIGHT = "8";
    public static final String DICT_SYS_CODE_BPM_APPROVE_NINE = "9";
    public static final String DICT_SYS_CODE_BPM_APPROVE_TEN = "10";
    public static final String DICT_SYS_CODE_BPM_APPROVE_ELEVEN = "11";

    /** 实例状态节点操作 点击的按钮 agree-同意,refuse-驳回,comment-评论,beforeAdd-后加签,afterAdd-前加签,transfer-转交,cancel-取消,recall-回退 */

    public static final String DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_AGREE = "agree";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_REFUSE = "refuse";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_COMMENT = "comment";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_BEFOREADD = "beforeAdd";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_AFTERADD = "afterAdd";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_TRANSFER = "transfer";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_CANCEL = "cancel";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_RECALL = "recall";

    /**任务类型 1-审批  2-抄送  3-参与评论**/
    public static final String DICT_SYS_CODE_BPM_INSTANCE_APPROVE_ONE = "1";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_APPROVE_TWO = "2";

    public static final String DICT_SYS_CODE_BPM_INSTANCE_APPROVE_THREE = "3";

    public static final String DICT_B_BPM_COMMENT  = "bpm_comment";


    /**
     * 节点审批类型 AND=会签  OR=或签  NEXT=顺序会签
     */
    public static final String DICT_SYS_CODE_BPM_INSTANCE_PROCESS_APPROVAL_MODE_AND = "AND";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_PROCESS_APPROVAL_MODE_OR = "OR";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_PROCESS_APPROVAL_MODE_NEXT = "NEXT";


    public static final String DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE = "审批通过";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL = "审批撤销";
    public static final String DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE = "审批驳回";

    /**
     * 项目类型  0 采购、销售 1 采购业务 2 销售业务
     */
    public static final String DICT_B_PROJECT_TYPE = "b_project_type";
    public static final String DICT_B_PROJECT_TYPE_ZERO = "0";
    public static final String DICT_B_PROJECT_TYPE_ONE = "1";
    public static final String DICT_B_PROJECT_TYPE_TWO = "2";

    /**
     * 状态：0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    public static final String DICT_B_PROJECT_STATUS = "b_project_status";
    public static final String DICT_B_PROJECT_STATUS_ZERO = "0";
    public static final String DICT_B_PROJECT_STATUS_ONE = "1";
    public static final String DICT_B_PROJECT_STATUS_TWO = "2";
    public static final String DICT_B_PROJECT_STATUS_THREE = "3";
    public static final String DICT_B_PROJECT_STATUS_FOUR = "4";
    public static final String DICT_B_PROJECT_STATUS_FIVE = "5";

    /**
     * 项目企业类型  0 供应商 1 客户
     */
    public static final String DICT_B_PROJECT_ENTERPRISE_TYPE = "b_project_enterprise_type";
    public static final String DICT_B_PROJECT_ENTERPRISE_TYPE_ZERO = "0";
    public static final String DICT_B_PROJECT_ENTERPRISE_TYPE_ONE = "1";

    /**
     * 项目运输方式  0 公路 1 铁路 2 多式联运
     */
    public static final String DICT_B_PROJECT_DELIVERY_TYPE = "b_project_delivery_type";
    public static final String DICT_B_PROJECT_DELIVERY_TYPE_ZERO = "0";
    public static final String DICT_B_PROJECT_DELIVERY_TYPE_ONE = "1";
    public static final String DICT_B_PROJECT_DELIVERY_TYPE_TWO = "2";


    /**
     * 付款方式 0依据合同 1预付款 2先款后货
     */
    public static final String DICT_B_PROJECT_PAYMENT_METHOD = "b_project_payment_method";
    public static final String DICT_B_PROJECT_PAYMENT_METHOD_ZERO = "0";
    public static final String DICT_B_PROJECT_PAYMENT_METHOD_ONE = "1";
    public static final String DICT_B_PROJECT_PAYMENT_METHOD_TWO = "2";

    /**
     * 采购合同 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    public static final String DICT_B_PO_CONTRACT_STATUS = "b_po_contract_status";
    public static final String DICT_B_PO_CONTRACT_STATUS_ZERO = "0";
    public static final String DICT_B_PO_CONTRACT_STATUS_ONE = "1";
    public static final String DICT_B_PO_CONTRACT_STATUS_TWO = "2";
    public static final String DICT_B_PO_CONTRACT_STATUS_THREE = "3";
    public static final String DICT_B_PO_CONTRACT_STATUS_FOUR = "4";
    public static final String DICT_B_PO_CONTRACT_STATUS_FIVE = "5";
    public static final String DICT_B_PO_CONTRACT_STATUS_SIX = "6";

    /**
     * 采购合同类型：0：标准合同；1：框架合同
     */
    public static final String DICT_B_PO_CONTRACT_TYPE = "b_po_contract_type";
    public static final String DICT_B_PO_CONTRACT_TYPE_ZERO = "0";
    public static final String DICT_B_PO_CONTRACT_TYPE_ONE = "1";

    /**
     * 采购合同 运输方式：1-公路；2-铁路；3-多式联运；
     */
    public static final String DICT_B_PO_CONTRACT_DELIVERY_TYPE = "b_po_contract_delivery_type";
    public static final String DICT_B_PO_CONTRACT_DELIVERY_TYPE_ONE = "1";
    public static final String DICT_B_PO_CONTRACT_DELIVERY_TYPE_TWO = "2";
    public static final String DICT_B_PO_CONTRACT_DELIVERY_TYPE_THREE = "3";

    /**
     *  采购合同结算方式：1-先款后货；2-先货后款；3-货到付款；
     */
    public static final String DICT_B_PO_CONTRACT_SETTLE_TYPE = "b_po_contract_settle_type";
    public static final String DICT_B_PO_CONTRACT_SETTLE_TYPE_ONE = "1";
    public static final String DICT_B_PO_CONTRACT_SETTLE_TYPE_TWO = "2";
    public static final String DICT_B_PO_CONTRACT_SETTLE_TYPE_THREE = "3";


    /**
     *  采购合同结算单据类型：1-实际到货结算；2-货转凭证结算
     */
    public static final String DICT_B_PO_CONTRACT_BILL_TYPE = "b_po_contract_bill_type";
    public static final String DICT_B_PO_CONTRACT_BILL_TYPE_ONE = "1";
    public static final String DICT_B_PO_CONTRACT_BILL_TYPE_TWO = "2";
    public static final String DICT_B_PO_CONTRACT_BILL_TYPE_THREE = "3";

    /**
     *  采购合同付款方式：1-银行转账
     */
    public static final String DICT_B_PO_CONTRACT_PAYMENT_TYPE = "b_po_contract_payment_type";
    public static final String DICT_B_PO_CONTRACT_PAYMENT_TYPE_ONE = "1";

    /**
     * 货权转移 审批状态 0-待审批 1-审批中 2-已审批 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    public static final String DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS = "b_po_cargo_right_transfer_status";
    public static final String DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_ZERO = "0";
    public static final String DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_ONE = "1";
    public static final String DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_TWO = "2";
    public static final String DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_THREE = "3";
    public static final String DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_FOUR = "4";
    public static final String DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_FIVE = "5";
    public static final String DICT_B_PO_CARGO_RIGHT_TRANSFER_STATUS_SIX = "6";

    /**
     * 销售货权转移 审批状态 0-待审批 1-审批中 2-已审批 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    public static final String DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS = "b_so_cargo_right_transfer_status";
    public static final String DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_ZERO = "0";
    public static final String DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_ONE = "1";
    public static final String DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_TWO = "2";
    public static final String DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_THREE = "3";
    public static final String DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_FOUR = "4";
    public static final String DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_FIVE = "5";
    public static final String DICT_B_SO_CARGO_RIGHT_TRANSFER_STATUS_SIX = "6";

    /**
     *  税率：6、17、13、9
     */
    public static final String DICT_S_TAX_TYPE = "s_tax_type";
    public static final String DICT_S_TAX_TYPE_6 = "6";
    public static final String DICT_S_TAX_TYPE_17 = "17";
    public static final String DICT_S_TAX_TYPE_13 = "13";
    public static final String DICT_S_TAX_TYPE_9 = "9";


    /**
     * 销售合同 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回  4-作废审批中 5-已作废 6-已完成
     */
    public static final String DICT_B_SO_CONTRACT_STATUS = "b_so_contract_status";
    public static final String DICT_B_SO_CONTRACT_STATUS_ZERO = "0";
    public static final String DICT_B_SO_CONTRACT_STATUS_ONE = "1";
    public static final String DICT_B_SO_CONTRACT_STATUS_TWO = "2";
    public static final String DICT_B_SO_CONTRACT_STATUS_THREE = "3";

    public static final String DICT_B_SO_CONTRACT_STATUS_FOUR = "4";
    public static final String DICT_B_SO_CONTRACT_STATUS_FIVE = "5";
    public static final String DICT_B_SO_CONTRACT_STATUS_SIX = "6";

    /**
     * 销售合同类型：0：标准合同；1：框架合同
     */
    public static final String DICT_B_SO_CONTRACT_TYPE = "b_so_contract_type";
    public static final String DICT_B_SO_CONTRACT_TYPE_ZERO = "0";
    public static final String DICT_B_SO_CONTRACT_TYPE_ONE = "1";

    /**
     * 销售合同 运输方式：1-公路；2-铁路；3-多式联运；
     */
    public static final String DICT_B_SO_CONTRACT_DELIVERY_TYPE = "b_so_contract_delivery_type";
    public static final String DICT_B_SO_CONTRACT_DELIVERY_TYPE_ONE = "1";
    public static final String DICT_B_SO_CONTRACT_DELIVERY_TYPE_TWO = "2";
    public static final String DICT_B_SO_CONTRACT_DELIVERY_TYPE_THREE = "3";

    /**
     *  销售合同结算方式：1-先款后货；2-先货后款；3-货到付款；
     */
    public static final String DICT_B_SO_CONTRACT_SETTLE_TYPE = "b_so_contract_settle_type";
    public static final String DICT_B_SO_CONTRACT_SETTLE_TYPE_ONE = "1";
    public static final String DICT_B_SO_CONTRACT_SETTLE_TYPE_TWO = "2";
    public static final String DICT_B_SO_CONTRACT_SETTLE_TYPE_THREE = "3";


    /**
     *  销售合同结算单据类型：1-实际到货结算；2-货转凭证结算
     */
    public static final String DICT_B_SO_CONTRACT_BILL_TYPE = "b_so_contract_bill_type";
    public static final String DICT_B_SO_CONTRACT_BILL_TYPE_ONE = "1";
    public static final String DICT_B_SO_CONTRACT_BILL_TYPE_TWO = "2";
    public static final String DICT_B_SO_CONTRACT_BILL_TYPE_THREE = "3";

    /**
     *  销售合同付款方式：1-银行转账
     */
    public static final String DICT_B_SO_CONTRACT_PAYMENT_TYPE = "b_so_contract_payment_type";
    public static final String DICT_B_SO_CONTRACT_PAYMENT_TYPE_ONE = "1";

    /**
     * 采购订单 审批状态 0-待审批 1-审批中 2-执行中 3-驳回  4-作废审批中 5-已作废 6-已完成
     */
    public static final String DICT_B_PO_ORDER_STATUS = "b_po_order_status";
    public static final String DICT_B_PO_ORDER_STATUS_ZERO = "0";
    public static final String DICT_B_PO_ORDER_STATUS_ONE = "1";
    public static final String DICT_B_PO_ORDER_STATUS_TWO = "2";
    public static final String DICT_B_PO_ORDER_STATUS_THREE = "3";
    public static final String DICT_B_PO_ORDER_STATUS_FOUR = "4";
    public static final String DICT_B_PO_ORDER_STATUS_FIVE = "5";
    public static final String DICT_B_PO_ORDER_STATUS_SIX = "6";

    /**
     * 采购订单类型：0：标准合同；1：框架合同
     */
    public static final String DICT_B_PO_ORDER_TYPE = "b_po_order_type";
    public static final String DICT_B_PO_ORDER_TYPE_ZERO = "0";
    public static final String DICT_B_PO_ORDER_TYPE_ONE = "1";

    /**
     * 采购订单 运输方式：1-公路；2-铁路；3-多式联运；
     */
    public static final String DICT_B_PO_ORDER_DELIVERY_TYPE = "b_po_order_delivery_type";
    public static final String DICT_B_PO_ORDER_DELIVERY_TYPE_ONE = "1";
    public static final String DICT_B_PO_ORDER_DELIVERY_TYPE_TWO = "2";
    public static final String DICT_B_PO_ORDER_DELIVERY_TYPE_THREE = "3";

    /**
     *  采购订单结算方式：1-先款后货；2-先货后款；3-货到付款；
     */
    public static final String DICT_B_PO_ORDER_SETTLE_TYPE = "b_po_order_settle_type";
    public static final String DICT_B_PO_ORDER_SETTLE_TYPE_ONE = "1";
    public static final String DICT_B_PO_ORDER_SETTLE_TYPE_TWO = "2";
    public static final String DICT_B_PO_ORDER_SETTLE_TYPE_THREE = "3";


    /**
     *  采购订单结算单据类型：1-实际到货结算；2-货转凭证结算
     */
    public static final String DICT_B_PO_ORDER_BILL_TYPE = "b_po_order_bill_type";
    public static final String DICT_B_PO_ORDER_BILL_TYPE_ONE = "1";
    public static final String DICT_B_PO_ORDER_BILL_TYPE_TWO = "2";
    public static final String DICT_B_PO_ORDER_BILL_TYPE_THREE = "3";

    /**
     *  采购订单付款方式：1-银行转账
     */
    public static final String DICT_B_PO_ORDER_PAYMENT_TYPE = "b_po_order_payment_type";
    public static final String DICT_B_PO_ORDER_PAYMENT_TYPE_ONE = "1";



    /**
     * 销售合同 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回 4-作废审批中 5-已作废
     */
    public static final String DICT_B_SO_ORDER_STATUS = "b_so_order_status";
    public static final String DICT_B_SO_ORDER_STATUS_ZERO = "0";
    public static final String DICT_B_SO_ORDER_STATUS_ONE = "1";
    public static final String DICT_B_SO_ORDER_STATUS_TWO = "2";
    public static final String DICT_B_SO_ORDER_STATUS_THREE = "3";
    public static final String DICT_B_SO_ORDER_STATUS_FOUR = "4";
    public static final String DICT_B_SO_ORDER_STATUS_FIVE = "5";
    public static final String DICT_B_SO_ORDER_STATUS_SIX = "6";
    /**
     * 销售合同类型：0：标准合同；1：框架合同
     */
    public static final String DICT_B_SO_ORDER_TYPE = "b_so_order_type";
    public static final String DICT_B_SO_ORDER_TYPE_ZERO = "0";
    public static final String DICT_B_SO_ORDER_TYPE_ONE = "1";

    /**
     * 销售合同 运输方式：1-公路；2-铁路；3-多式联运；
     */
    public static final String DICT_B_SO_ORDER_DELIVERY_TYPE = "b_so_order_delivery_type";
    public static final String DICT_B_SO_ORDER_DELIVERY_TYPE_ONE = "1";
    public static final String DICT_B_SO_ORDER_DELIVERY_TYPE_TWO = "2";
    public static final String DICT_B_SO_ORDER_DELIVERY_TYPE_THREE = "3";

    /**
     *  销售合同结算方式：1-先款后货；2-先货后款；3-货到付款；
     */
    public static final String DICT_B_SO_ORDER_SETTLE_TYPE = "b_so_order_settle_type";
    public static final String DICT_B_SO_ORDER_SETTLE_TYPE_ONE = "1";
    public static final String DICT_B_SO_ORDER_SETTLE_TYPE_TWO = "2";
    public static final String DICT_B_SO_ORDER_SETTLE_TYPE_THREE = "3";


    /**
     *  销售合同结算单据类型：1-实际到货结算；2-货转凭证结算
     */
    public static final String DICT_B_SO_ORDER_BILL_TYPE = "b_so_order_bill_type";
    public static final String DICT_B_SO_ORDER_BILL_TYPE_ONE = "1";
    public static final String DICT_B_SO_ORDER_BILL_TYPE_TWO = "2";
    public static final String DICT_B_SO_ORDER_BILL_TYPE_THREE = "3";

    /**
     *  销售合同付款方式：1-银行转账
     */
    public static final String DICT_B_SO_ORDER_PAYMENT_TYPE = "b_so_order_payment_type";
    public static final String DICT_B_SO_ORDER_PAYMENT_TYPE_ONE = "1";

    /**
     *  企业银行账户表 0-禁用 1-可用 -1-删除
     */
    public static final String DICT_M_BANK_STATUS = "m_bank_status";
    public static final String DICT_M_BANK_STATUS_ZERO = "0";
    public static final String DICT_M_BANK_STATUS_ONE = "1";
    public static final String DICT_M_BANK_STATUS_DEL = "-1";

    /**
     *  企业银行 是否默认(0-否 1-是)
     */
    public static final String DICT_M_BANK_IS_DEFAULT = "m_bank_is_default";
    public static final String DICT_M_BANK_IS_DEFAULT_ZERO = "0";
    public static final String DICT_M_BANK_IS_DEFAULT_ONE = "1";


    /**
     *  企业银行类型（1-预付款、2-预收款、3-应付款、4-应收款）
     */
    public static final String DICT_M_BANK_TYPE = "m_bank_type";
    public static final String DICT_M_BANK_TYPE_ONE = "1";
    public static final String DICT_M_BANK_TYPE_TWO = "2";
    public static final String DICT_M_BANK_TYPE_THREE = "3";
    public static final String DICT_M_BANK_TYPE_FOUR = "4";

    /**
     *  应付账款管理业务类型 1-应付、2-预付、3-其他支出
     */
    public static final String DICT_B_AP_TYPE = "b_ap_type";
    public static final String DICT_B_AP_TYPE_ONE = "1";
    public static final String DICT_B_AP_TYPE_TWO = "2";
    public static final String DICT_B_AP_TYPE_THREE = "3";

    /**
     *  应收账款管理业务类型 1-应收、2-预收、3-其他收入
     */
    public static final String DICT_B_AR_TYPE = "b_ar_type";
    public static final String DICT_B_AR_TYPE_ONE = "1";
    public static final String DICT_B_AR_TYPE_TWO = "2";
    public static final String DICT_B_AR_TYPE_THREE = "3";

    /**
     *  应付账款管理审批状态 0-待审批 1-审批中 2-审批通过 3-驳回 4-作废审批中 5-已作废
     */
    public static final String DICT_B_AP_STATUS = "b_ap_status";
    public static final String DICT_B_AP_STATUS_ZERO = "0";
    public static final String DICT_B_AP_STATUS_ONE = "1";
    public static final String DICT_B_AP_STATUS_TWO = "2";
    public static final String DICT_B_AP_STATUS_THREE = "3";
    public static final String DICT_B_AP_STATUS_FOUR = "4";
    public static final String DICT_B_AP_STATUS_FIVE = "5";

    /**
     *  应收账款管理审批状态 0-待审批 1-审批中 2-审批通过 3-驳回 4-作废审批中 5-已作废
     */
    public static final String DICT_B_AR_STATUS = "b_ar_status";
    public static final String DICT_B_AR_STATUS_ZERO = "0";
    public static final String DICT_B_AR_STATUS_ONE = "1";
    public static final String DICT_B_AR_STATUS_TWO = "2";
    public static final String DICT_B_AR_STATUS_THREE = "3";
    public static final String DICT_B_AR_STATUS_FOUR = "4";
    public static final String DICT_B_AR_STATUS_FIVE = "5";

    /**
     *  应付账款管理付款状态 付款状态：0-未付款、1-部分付款、2-已付款、-1-中止付款
     */
    public static final String DICT_B_AP_PAY_STATUS = "b_ap_pay_status";
    public static final String DICT_B_AP_PAY_STATUS_ZERO = "0";
    public static final String DICT_B_AP_PAY_STATUS_ONE = "1";
    public static final String DICT_B_AP_PAY_STATUS_TWO = "2";
    public static final String DICT_B_AP_PAY_STATUS_STOP = "-1";

    /**
     *  应收账款管理收款状态 收款状态：0-未收款、1-部分收款、2-已收款、-1-中止收款
     */
    public static final String DICT_B_AR_RECEIVE_STATUS = "b_ar_receive_status";
    public static final String DICT_B_AR_RECEIVE_STATUS_ZERO = "0";
    public static final String DICT_B_AR_RECEIVE_STATUS_ONE = "1";
    public static final String DICT_B_AR_RECEIVE_STATUS_TWO = "2";
    public static final String DICT_B_AR_RECEIVE_STATUS_STOP = "-1";

//    /**
//     *  付款单状态：状态（0-待付款、1已付款、2-作废、-1-中止付款）
//     */
//    public static final String DICT_B_AP_PAY_BILL_STATUS = "b_ap_pay_bill_status";
//    public static final String DICT_B_AP_PAY_BILL_STATUS_ZERO = "0";
//    public static final String DICT_B_AP_PAY_BILL_STATUS_ONE = "1";
//    public static final String DICT_B_AP_PAY_BILL_STATUS_TWO = "2";
//    public static final String DICT_B_AP_PAY_BILL_STATUS_STOP = "-1";

    public static final String DICT_B_FUND_MONITOR_BUSINESS_TYPE = "b_fund_monitor_business_type";

    /**
     * 应付账款管理-应付-登记     YF00-应付款付款指令    YF01-应付款付款凭证上传
     * 应付账款管理-应付-退款     YFT01-应付退款下推退款单 YFT02-应付退款凭证上传
     * 应付账款管理-应付-作废     YFZF01-应付款付款凭证作废
     * 应付账款管理-应付-中止付款  YFZZ01-应付款中止付款
     */
    public static final String AP_PAY_ORDER = "YF00";
    public static final String AP_PAY_VOUCHER = "YF01";
    public static final String AP_PAY_REFUND_ORDER = "YFT01";
    public static final String AP_PAY_REFUND_VOUCHER = "YFT02";
    public static final String AP_PAY_VOUCHER_CANCEL = "YFZF01";
    public static final String AP_STOP_PAY = "YFZZ01";

    /**
     * 应付账款管理-预付-登记     UF01-预付款付款指令 UF02-预付款付款凭证上传
     * 应付账款管理-预付-退款     UFT01-预付退款下推退款单 UFT02-预付退款凭证上传
     * 应付账款管理-预付-作废     UFZF01-预付款付款凭证作废
     * 应付账款管理-预付-中止付款  UFZZ01-应付款中止付款
     */
    public static final String AP_ADVANCE_ORDER = "UF01";
    public static final String AP_ADVANCE_VOUCHER = "UF02";
    public static final String AP_ADVANCE_PAY_REFUND_ORDER = "UFT01";
    public static final String AP_ADVANCE_PAY_REFUND_VOUCHER = "UFT02";
    public static final String AP_ADVANCE_PAY_CANCEL = "UFZF01";
    public static final String AP_ADVANCE_STOP_PAY = "UFZZ01";


    /**
     * 资金流水监控表 类型：0-冻结；1-生效
     */
    public static final String DICT_B_FUND_MONITOR_TYPE = "b_fund_monitor_type";
    public static final String DICT_B_FUND_MONITOR_TYPE_ONE = "0";
    public static final String DICT_B_FUND_MONITOR_TYPE_TWO = "1";


    /**
     *  应付退款管理业务类型 1-应付退款、2-预付退款、3-其他支出退款
     */
    public static final String DICT_B_AP_REFUND_TYPE = "b_ap_refund_type";
    public static final String DICT_B_AP_REFUND_TYPE_ONE = "1";
    public static final String DICT_B_AP_REFUND_TYPE_TWO = "2";
    public static final String DICT_B_AP_REFUND_TYPE_THREE = "3";

    /**
     *  应收退款管理业务类型 1-应收退款、2-预收退款、3-其他收入退款
     */
    public static final String DICT_B_AR_REFUND_TYPE = "b_ar_refund_type";
    public static final String DICT_B_AR_REFUND_TYPE_ONE = "1";
    public static final String DICT_B_AR_REFUND_TYPE_TWO = "2";
    public static final String DICT_B_AR_REFUND_TYPE_THREE = "3";

    /**
     *  应付退款管理审批状态 0-待审批 1-审批中 2-审批通过 3-驳回 4-作废审批中 5-已作废
     */
    public static final String DICT_B_AP_REFUND_STATUS = "b_ap_refund_status";
    public static final String DICT_B_AP_REFUND_STATUS_ZERO = "0";
    public static final String DICT_B_AP_REFUND_STATUS_ONE = "1";
    public static final String DICT_B_AP_REFUND_STATUS_TWO = "2";
    public static final String DICT_B_AP_REFUND_STATUS_THREE = "3";
    public static final String DICT_B_AP_REFUND_STATUS_FOUR = "4";
    public static final String DICT_B_AP_REFUND_STATUS_FIVE = "5";

    /**
     *  应收退款管理审批状态 0-待审批 1-审批中 2-审批通过 3-驳回 4-作废审批中 5-已作废
     */
    public static final String DICT_B_AR_REFUND_STATUS = "b_ar_refund_status";
    public static final String DICT_B_AR_REFUND_STATUS_ZERO = "0";
    public static final String DICT_B_AR_REFUND_STATUS_ONE = "1";
    public static final String DICT_B_AR_REFUND_STATUS_TWO = "2";
    public static final String DICT_B_AR_REFUND_STATUS_THREE = "3";
    public static final String DICT_B_AR_REFUND_STATUS_FOUR = "4";
    public static final String DICT_B_AR_REFUND_STATUS_FIVE = "5";

    /**
     *  应付退款管理付款状态 付款状态：0-未退款、1-部分退款、2-已退款
     */
    public static final String DICT_B_AP_REFUND_PAY_STATUS = "b_ap_refund_pay_status";
    public static final String DICT_B_AP_REFUND_PAY_STATUS_ZERO = "0";
    public static final String DICT_B_AP_REFUND_PAY_STATUS_ONE = "1";
    public static final String DICT_B_AP_REFUND_PAY_STATUS_TWO = "2";

    /**
     *  应收退款管理收款状态 收款状态：0-待收款、1-已收款、2-作废
     */
    public static final String DICT_B_AR_REFUND_RECEIVE_STATUS = "b_ar_refund_receive_status";
    public static final String DICT_B_AR_REFUND_RECEIVE_STATUS_ZERO = "0";
    public static final String DICT_B_AR_REFUND_RECEIVE_STATUS_ONE = "1";
    public static final String DICT_B_AR_REFUND_RECEIVE_STATUS_TWO = "2";

    /**
     * 采购结算类型：0-采购结算
     */
    public static final String DICT_B_PO_SETTLEMENT_TYPE = "b_po_settlement_type";
    public static final String DICT_B_PO_SETTLEMENT_TYPE_ZERO = "0";

    /**
     * 采购结算审批状态：0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    public static final String DICT_B_PO_SETTLEMENT_STATUS = "b_po_settlement_status";
    public static final String DICT_B_PO_SETTLEMENT_STATUS_ZERO = "0";
    public static final String DICT_B_PO_SETTLEMENT_STATUS_ONE = "1";
    public static final String DICT_B_PO_SETTLEMENT_STATUS_TWO = "2";
    public static final String DICT_B_PO_SETTLEMENT_STATUS_THREE = "3";
    public static final String DICT_B_PO_SETTLEMENT_STATUS_FOUR = "4";
    public static final String DICT_B_PO_SETTLEMENT_STATUS_FIVE = "5";
    public static final String DICT_B_PO_SETTLEMENT_STATUS_SIX = "6";

    /**
     * 采购结算结算方式：1-先款后货；2-先货后款；3-货到付款
     */
    public static final String DICT_B_PO_SETTLEMENT_SETTLE_TYPE = "b_po_settlement_settle_type";
    public static final String DICT_B_PO_SETTLEMENT_SETTLE_TYPE_ONE = "1";
    public static final String DICT_B_PO_SETTLEMENT_SETTLE_TYPE_TWO = "2";
    public static final String DICT_B_PO_SETTLEMENT_SETTLE_TYPE_THREE = "3";

    /**
     * 采购结算单据类型：1-实际到货结算；2-货转凭证结算
     */
    public static final String DICT_B_PO_SETTLEMENT_BILL_TYPE = "b_po_settlement_bill_type";
    public static final String DICT_B_PO_SETTLEMENT_BILL_TYPE_ONE = "1";
    public static final String DICT_B_PO_SETTLEMENT_BILL_TYPE_TWO = "2";

    /**
     * 销售结算类型：0-销售结算
     */
    public static final String DICT_B_SO_SETTLEMENT_TYPE = "b_so_settlement_type";
    public static final String DICT_B_SO_SETTLEMENT_TYPE_ZERO = "0";

    /**
     * 销售结算审批状态：0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    public static final String DICT_B_SO_SETTLEMENT_STATUS = "b_so_settlement_status";
    public static final String DICT_B_SO_SETTLEMENT_STATUS_ZERO = "0";
    public static final String DICT_B_SO_SETTLEMENT_STATUS_ONE = "1";
    public static final String DICT_B_SO_SETTLEMENT_STATUS_TWO = "2";
    public static final String DICT_B_SO_SETTLEMENT_STATUS_THREE = "3";
    public static final String DICT_B_SO_SETTLEMENT_STATUS_FOUR = "4";
    public static final String DICT_B_SO_SETTLEMENT_STATUS_FIVE = "5";
    public static final String DICT_B_SO_SETTLEMENT_STATUS_SIX = "6";

    /**
     * 销售结算结算方式：1-先款后货；2-先货后款；3-货到付款
     */
    public static final String DICT_B_SO_SETTLEMENT_SETTLE_TYPE = "b_so_settlement_settle_type";
    public static final String DICT_B_SO_SETTLEMENT_SETTLE_TYPE_ONE = "1";
    public static final String DICT_B_SO_SETTLEMENT_SETTLE_TYPE_TWO = "2";
    public static final String DICT_B_SO_SETTLEMENT_SETTLE_TYPE_THREE = "3";

    /**
     * 销售结算单据类型：1-实际发货结算；2-货转凭证结算
     */
    public static final String DICT_B_SO_SETTLEMENT_BILL_TYPE = "b_so_settlement_bill_type";
    public static final String DICT_B_SO_SETTLEMENT_BILL_TYPE_ONE = "1";
    public static final String DICT_B_SO_SETTLEMENT_BILL_TYPE_TWO = "2";
}
