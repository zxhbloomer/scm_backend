package com.xinyirun.scm.common.enums.api;

/**
 * 库存操作核心代码错误
 */
public enum ApiResultEnum {
    OK(0, "成功"),
    UNKNOWN_ERROR(-1, "未知错误！"),
    NEED_APP_KEY(101,"缺少参数app_key！"),
    NEED_SECRET_KEY(102,"缺少参数secret_key！"),
    NOT_NULL_APP_KEY(103,"缺少参数app_key！"),
    NOT_NULL_SECRET_KEY(104,"缺少参数secret_key！"),
    APP_KEY_DATA_IS_NULL(105,"没有找到对应app_key的数据，app_key在数据库中不存在！"),
    AUTH_DATA_IS_NULL(106,"secret_key不正确！"),

    CUSTOMER_NAME_NULL(301,"name为空！"),
    CUSTOMER_CODE_NULL(302,"code为空！"),
    CUSTOMER_SHORT_NAME_NULL(303,"short_name为空！"),
//    CUSTOMER_APP_CODE_NULL(304,"app_code为空！"),
    CUSTOMER_PARAM_CODE_REPEAT(416,"参数code重复！"),
    CUSTOMER_TYPE_NULL(417,"type为空！"),
    CUSTOMER_PARAM_NAME_REPEAT(418,"参数name重复！"),
    CUSTOMER_CREDIT_CODE_NULL(302,"credit_code为空！"),

    GOODS_SPEC_CODE_NULL(305,"code为空！"),
    GOODS_SPEC_GOODS_CODE_NULL(306,"goods_code为空！"),
    GOODS_SPEC_NAME_NULL(307,"name为空！"),
    GOODS_SPEC_SPEC_NULL(308,"spec为空！"),
    GOODS_SPEC_PM_NULL(309,"pm为空！"),
//    GOODS_SPEC_APP_CODE_NULL(310,"app_code为空！"),
    GOODS_SPEC_PARAM_SPEC_REPEAT(311,"参数spec重复！"),
    GOODS_SPEC_SPEC_REPEAT(312,"spec重复！"),
    GOODS_SPEC_PARAM_CODE_REPEAT(313,"参数code重复！"),

    IN_PLAN_PARAM_DETAIL_LIST_NULL(314,"参数detailList为空！"),
    IN_PLAN_PARAM_ORDER_VO_NULL(315,"参数orderVo为空！"),
    IN_PLAN_PARAM_PLAN_TIME_NULL(316,"参数plan_time为空！"),
    IN_PLAN_PARAM_TYPE_NULL(317,"参数type为空！"),
    IN_PLAN_PARAM_BILL_TYPE_NULL(318,"参数bill_type为空！"),
//    IN_PLAN_PARAM_APP_CODE_NULL(319,"app_code为空！"),
    IN_PLAN_PARAM_REMARK_NULL(320,"remark为空！"),
    IN_PLAN_PARAM_OWNER_CODE_NULL(321,"owner_code为空！"),
    IN_PLAN_PARAM_CONSIGNOR_CODE_NULL(322,"consignor_code为空！"),
    IN_PLAN_DETAIL_PARAM_PRICE_NULL(323,"price为空！"),
    IN_PLAN_DETAIL_PARAM_COUNT_NULL(324,"count为空！"),
    IN_PLAN_DETAIL_PARAM_PM_NULL(325,"pm为空！"),
    IN_PLAN_DETAIL_PARAM_UNIT_NULL(326,"unit为空！"),
    IN_PLAN_DETAIL_PARAM_WAREHOUSE_ID_NULL(327,"warehouse_id为空！"),
    IN_PLAN_DETAIL_PARAM_SPEC_CODE_NULL(328,"spec_code为空！"),
    IN_ORDER_PARAM_SHIP_NAME_NULL(329,"ship_name为空！"),
    IN_ORDER_PARAM_CONTRACT_NO_NULL(330,"contract_no为空！"),
    IN_ORDER_PARAM_CONTRACT_DT_NULL(331,"contract_dt为空！"),
    IN_ORDER_PARAM_CONTRACT_NUM_NULL(332,"contract_num为空！"),
    IN_ORDER_PARAM_ORDER_NO_NULL(333,"order_no为空！"),
    IN_ORDER_PARAM_SUPPLIER_CODE_NULL(334,"supplier_code为空！"),
    IN_PLAN_PARAM_CODE_NULL(335,"code为空！"),

    OUT_PLAN_PARAM_DETAIL_LIST_NULL(351,"参数detailList为空！"),
    OUT_PLAN_PARAM_ORDER_VO_NULL(352,"参数orderVo为空！"),
    OUT_PLAN_PARAM_PLAN_TIME_NULL(353,"参数plan_time为空！"),
    OUT_PLAN_PARAM_TYPE_NULL(354,"参数type为空！"),
    OUT_PLAN_PARAM_BILL_TYPE_NULL(355,"参数bill_type为空！"),
//    OUT_PLAN_PARAM_APP_CODE_NULL(356,"app_code为空！"),
    OUT_PLAN_PARAM_REMARK_NULL(357,"remark为空！"),
    OUT_PLAN_PARAM_OWNER_CODE_NULL(358,"owner_code为空！"),
    OUT_PLAN_PARAM_CONSIGNOR_CODE_NULL(359,"consignor_code为空！"),
    OUT_PLAN_DETAIL_PARAM_PRICE_NULL(360,"price为空！"),
    OUT_PLAN_DETAIL_PARAM_COUNT_NULL(361,"count为空！"),
    OUT_PLAN_DETAIL_PARAM_INVENTORY_ID_NULL(362,"inventory_id为空！"),
    OUT_PLAN_DETAIL_PARAM_UNIT_NULL(363,"unit为空！"),
    OUT_PLAN_DETAIL_PARAM_WAREHOUSE_ID_NULL(364,"warehouse_id为空！"),
    OUT_PLAN_DETAIL_PARAM_SPEC_CODE_NULL(365,"spec_code为空！"),
    OUT_ORDER_PARAM_SHIP_NAME_NULL(366,"ship_name为空！"),
    OUT_ORDER_PARAM_CONTRACT_NO_NULL(367,"contract_no为空！"),
    OUT_ORDER_PARAM_CONTRACT_DT_NULL(368,"contract_dt为空！"),
    OUT_ORDER_PARAM_CONTRACT_NUM_NULL(369,"contract_num为空！"),
    OUT_ORDER_PARAM_ORDER_NO_NULL(370,"order_no为空！"),
    OUT_ORDER_PARAM_CLIENT_CODE_NULL(371,"client_code为空！"),
    OUT_PLAN_PARAM_CODE_NULL(372,"code为空！"),
    OUT_PLAN_PARAM_CODE_ERROR(372,"code错误或不存在！"),
    OUT_PLAN_PARAM_NULL(353,"参数为空！"),

    BUSINESS_TYPE_CODE_NULL(380,"code为空！"),
    BUSINESS_TYPE_NAME_NULL(381,"name为空！"),
    BUSINESS_TYPE_PARAM_CODE_REPEAT(382,"参数code重复！"),
    BUSINESS_TYPE_PARAM_NAME_REPEAT(383,"参数name重复！"),
    BUSINESS_TYPE_NAME_REPEAT(384,"name重复！"),

    INDUSTRY_CODE_NULL(390,"code为空！"),
    INDUSTRY_NAME_NULL(391,"name为空！"),
    INDUSTRY_BUSINESS_TYPE_CODE_NULL(392,"business_type_code为空！"),
    INDUSTRY_PARAM_CODE_REPEAT(393,"参数code重复！"),
    INDUSTRY_PARAM_NAME_REPEAT(394,"参数name重复！"),
    INDUSTRY_NAME_REPEAT(395,"name重复！"),

    CATEGORY_CODE_NULL(400,"code为空！"),
    CATEGORY_NAME_NULL(401,"name为空！"),
    CATEGORY_INDUSTRY_CODE_NULL(402,"industry_code为空！"),
    CATEGORY_PARAM_CODE_REPEAT(403,"参数code重复！"),
    CATEGORY_PARAM_NAME_REPEAT(404,"参数name重复！"),
    CATEGORY_NAME_REPEAT(405,"name重复！"),

    GOODS_CODE_NULL(410,"code为空！"),
    GOODS_NAME_NULL(411,"name为空！"),
    GOODS_CATEGORY_CODE_NULL(412,"category_code为空！"),
    GOODS_PARAM_CODE_REPEAT(413,"参数code重复！"),
    GOODS_PARAM_NAME_REPEAT(414,"参数name重复！"),
    GOODS_NAME_REPEAT(415,"name重复！"),

    UNIT_CODE_NULL(416,"code为空！"),
    UNIT_NAME_NULL(417,"name为空！"),
    UNIT_PARAM_CODE_REPEAT(418,"参数code重复！"),
    CUSTOMER_NULL(420,"委托方code对应wms数据不存在！"),
    OWNER_NULL(421,"货主code对应wms数据不存在！"),
    GOODS_SPEC_NULL(422,"规格code对应wms数据不存在！"),
    UNIT_CONVERT_NULL(423,"单位unit对应wms数据不存在！"),
    SUPPLIER_NULL(424,"供应商code对应wms客户数据不存在！"),

    GOODS_PROP_CODE_NULL(430,"code为空！"),
    GOODS_PROP_NAME_NULL(431,"name为空！"),
    GOODS_PROP_PARAM_CODE_REPEAT(432,"参数code重复！"),
    GOODS_PROP_PARAM_NAME_REPEAT(433,"参数name重复！"),

    PRICE_PRICE_NULL(400,"price为空！"),
    PRICE_PROVINCE_NULL(401,"province为空！"),
    PRICE_CITY_NULL(402,"city为空！"),
    PRICE_DISTRICT_NULL(403,"district为空！"),
    PRICE_GOODS_CODE_NULL(404,"goods_code为空！"),
    PRICE_SKU_CODE_NULL(405,"sku_code为空！"),
    PRICE_START_DT_NULL(406,"start_dt为空！"),
    PRICE_END_DT_NULL(407,"end_dt为空！"),
    PRICE_PRICE_DT_NULL(408,"price_dt为空！"),

    ORDER_PARAM_CODE_REPEAT(418,"参数order_no重复！"),
    ORDER_PARAM_CODE_NULL(418,"参数order_no为空！"),
    ORDER_PARAM_CONTRACT_NO_NULL(418,"参数contract_no为空！"),
    ORDER_PARAM_BILL_TYPE_NULL(418,"参数bill_type为空！"),
    ORDER_PARAM_SUPPLIER_CODE_NULL(418,"supplier_code为空！"),
    ORDER_PARAM_OWNER_CODE_NULL(418,"owner_code为空！"),
    ORDER_PARAM_CLIENT_CODE_NULL(418,"client_code为空！"),
    IN_PLAN_DISCONTINUED_CODE_NULL(421, "code为空!"),
    IN_PLAN_DISCONTINUED_ENTITY_NULL(422, "对应code不存在!"),



    // 所有API check都需要在该枚举类中定义

    ;

    private Integer code;

    private String msg;

    ApiResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
