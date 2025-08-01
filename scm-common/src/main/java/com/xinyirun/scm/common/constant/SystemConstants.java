package com.xinyirun.scm.common.constant;

/**
 * @author zxh
 */
public class SystemConstants  {

    private SystemConstants() {
    }

    /**
     *   关于仓库模块的定义：根据传入的仓库id，获取到相应的库区/库位" / "的分割符号
     */
    public static final String WAREHOUSE_LOCSTION_BIN_DELIMITER = "/";
    // excel
    public static final String XLSX_SUFFIX = ".xlsx";
    public static final String XLS_SUFFIX = ".xls";
    public static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static final short HEAD_BG_COLOR = 12;

    public static final String LOG_DATA_CHANGE = "log_data_change";

    public static final String DAILY_INVENTORY_GENERATION_METHOD = "daily_inventory_generation_method";
    public static final String LOG_DATA_CHANGE_OPEN = "1";

    public static final String EXPORT_URL = "export_url";

    // apk下载地址
    public static final String APK_URL = "apk_url";

    // 加权单价天数
    public static final String PRICE_DAYS = "price_days";

    // 超发 key
    public static final String OVER_RELEASE = "over_release";

    // 超发 key
    public static final String APP_OVER_RELEASE = "app_over_release";

    // 完成装货最大重量 key
    public static final String APP_MONITOR_COMPLETE_SUBMIT_PARA = "app_monitor_complete_submit_para";

    // 订单超发 key
    public static final String ORDER_OVER_RELEASE = "order_over_release";

    // 监管任务最大出库比率
    public static final String OVER_RELEASE_RATE = "over_release_rate";

    // 超收 key
    public static final String OVER_RECEIVE = "over_receive";

    // 皮重, 毛重开关 key
    public static final String TARE_GROSS_WEIGHT_CONTROL = "tare_gross_weight_control";

    // 日加工报表是否启用
    public static final String QRTZ_PRODUCT_DAILY = "qrtz_product_daily";

    // 监管任务已完成状态查询参数
    public static final String APP_MONITOR_TASK_SEARCH_PARA = "app_monitor_task_search_para";

    // 物流订单已完成状态查询参数
    public static final String APP_LOGISTICS_ORDER_SEARCH_PARA = "app_logistics_order_search_para";

    // 物流订单已完成状态查询参数
    public static final String VEHICLE_VALIDATE_MINUNTES = "vehicle_validate_minuntes";

    // 轨迹接口配置 1-腾颢 2-好伙伴
    public static final String TRACK_CONFIG = "track_config";

    // 文件备份 0-关闭 1-开启 key
    public static final String BACKUP = "backup";

    // 轨迹时间 key
    public static final String TRACK_MINUTES = "track_minutes";

    // 文件备份url key
    public static final String BACKUP_URL = "backup_url";

    // 文件备份uri key
    public static final String BACKUP_URI = "backup_uri";

    // 删除文件备份uri key
    public static final String DELETE_BACKUP_URI = "delete_backup_uri";

    // 文件备份天数 key
    public static final String BACKUP_DAYS = "backup_days";

    // 默认单位换算关系
    public static final String DEFAULT_GOODS_UNIT_CALC = "default_goods_unit_calc";

    // 每日货值计算天数
    public static final String DAILY_PRICE_DAYS = "daily_price_days";

    /** 图形验证码 session key */
    public static final String SESSION_KEY_IMAGE_CODE = "SESSION_KEY_IMAGE_CODE";
    /** 手机验证码 session key */
    public static final String SESSION_KEY_SMS_CODE = "SESSION_KEY_SMS_CODE";
    /** 用户注册 所保存的密码 */
    public static final String SESSION_KEY_USER_PASSWORD = "SESSION_KEY_USER_PASSWORD";

    // 返回报文头 json格式，编码 utf-8
    public static final String JSON_UTF8 = "application/json;charset=utf-8";
    // 返回 html
    public static final String HTML_UTF8 = "text/html;charset=utf-8";
    // 用户注册 URL
    public static final String FEBS_REGIST_URL = "/user/regist";
    // 权限不足 URL
    public static final String FEBS_ACCESS_DENY_URL = "/access/deny/403";

    // 默认库区名称
    public static final String DEFAULT_LOCATION = "默认库区";
    // 默认库位名称
    public static final String DEFAULT_BIN = "默认库位";

    /** API  0：进行中 1：作废 2：已完成 3中止 */
    public static final String API_STATUS_PROGRESS = "0";
    public static final String API_STATUS_CANCEL = "1";
    public static final String API_STATUS_OVER = "2";
    public static final String API_STATUS_DISCONTINUE = "3";

    /** API  0：内部企业，同步客户、货主数据，1：外部企业，同步客户数据  */
    public static final String API_INTERIOR_ENTERPRISE_TYPE = "0";
    public static final String API_EXTERNAL_ENTERPRISE_TYPE = "1";

    /** enable 1:启用 0禁用*/
    public static final int ENABLE_FALSE = 0;
    public static final int ENABLE_TRUE = 1;

    /** enable 1:外网 0内网*/
    public static final String FS_CONFIG_FALSE = "0";
    public static final String FS_CONFIG_TRUE = "1";

    public static final String APP_FILE = "app_file";

    public static final int CAR_COUNT = 0;

    public static final String DEV_MODEL_PROD = "prod";

    /**
     * 库存单位
     */
    public static final String INVENTORY_UNIT = "吨";

    public static final String HMAC_SHA1 = "HmacSHA1";
    public static final char[] DIGITAL = "0123456789ABCDEF".toCharArray();

    // 导出临时目录
    public static final String KEY_EXPORT_TEMP_DIR = "export_temp_dir";

    // 物流订单最大发货执行情况 自动完成
    public static final String KEY_LOGISTICS_MAX_OUT = "logistics_max_out";

    // 监管任务收货最大可超发货数量百分比
    public static final String KEY_MONITOR_MAX_IN = "key_monitor_max_in";

    // 中台入库类型 0 :  采购入库,   1:销售退货入库  2提货入库
    public static final String STEEL_IN_TYPE_ZERO = "0";
    public static final String STEEL_IN_TYPE_ONE = "1";
    public static final String STEEL_IN_TYPE_TWO = "2";

    // 中台出库类型 0 销售出库 1采购退货出库 2直采出库
    public static final String STEEL_OUT_TYPE_ZERO = "0";
    public static final String STEEL_OUT_TYPE_ONE = "1";
    public static final String STEEL_OUT_TYPE_TWO = "2";

    // 默认审核人code
    public static final String AUDIT_STAFF_CODE = "SYSTEMADMIN";

    // 最大导出数量限制
    public static final String EXPORT_LIMIT_KEY = "export_limit";

    // 阿里云OSS图片前缀
    public static final String IMG_URL = "img_url";

    // 强制修改开关
    public static final String PWD_SWITCH = "pwd_switch";

    // 报表系统参数
    public static final String PRINT_SYSTEM_CONFIG = "print_system_config";


    public static final String WORK_BENCH_LAYOUT_DEFAULT = "work_bench_layout_default";


    // 获取scm域名
    public static final String SCM_SYSTEM_DOMAIN = "scm_system_domain";

    /**
     * sql的类型常量：UPDATE、INSERT、DELETE、SELECT
     */
    public class SQLCOMMANDTYPE {
        public static final String UPDATE = "UPDATE";
        public static final String INSERT = "INSERT";
        public static final String DELETE = "DELETE";
        public static final String SELECT = "SELECT";
    }

    /**
     * 监管任务类型
     */
    public class MONITOR {
        // 入库
        public static final String B_MONITOR_IN = "b_monitor_in";
        // 卸货
        public static final String B_MONITOR_UNLOAD = "b_monitor_unload";
        // 出库
        public static final String B_MONITOR_OUT = "b_monitor_out";
        // 提货
        public static final String B_MONITOR_DELIVERY = "b_monitor_delivery";
    }

    public class FILE_TYPE {
        // 空车过磅 司机车头
        public static final String ALI_OSS = "ALI_OSS";
        // 空车过磅 司机车尾
        public static final String LOCAL = "LOCAL";
    }

    /**
     * 监管任务类型
     */
    public class MONITOR_FILE_TYPE {
        // 空车过磅 司机车头
        public static final String OUT_ONE = "out_one";
        // 空车过磅 司机车尾
        public static final String OUT_TWO = "out_two";
        // 空车过磅 司机承诺书
        public static final String OUT_THREE = "out_three";
        // 空车过磅 司机身份证
        public static final String OUT_FOUR = "out_four";
        // 空车过磅 司机驾驶证
        public static final String OUT_TWELVE = "out_twelve";
        // 空车过磅 车辆行驶证
        public static final String OUT_THIRTEEN = "out_thirteen";

        // 车厢情况照片
        public static final String OUT_FOURTEEN = "out_fourteen";

        // 正在装货 车头照片
        public static final String OUT_FIVE = "out_five";
        // 正在装货 车尾照片
        public static final String OUT_SIX = "out_six";
        // 正在装货 车侧身附件
        public static final String OUT_SEVEN = "out_seven";
        // 正在装货 装货视频
        public static final String OUT_EIGHT = "out_eight";

        // 重车出库 车头照片
        public static final String OUT_NINE = "out_nine";
        // 重车出库 车尾照片
        public static final String OUT_TEN = "out_ten";
        // 重车出库 磅单
        public static final String OUT_ELEVEN = "out_eleven";

        // 正在装货 集装箱箱号照片1
        public static final String OUT_CONTAINER_ONE = "out_container_one";
        // 正在装货 集装箱内部空箱照片1
        public static final String OUT_CONTAINER_TWO = "out_container_two";
        // 正在装货 集装箱装货视频1
        public static final String OUT_CONTAINER_THREE = "out_container_three";
        // 正在装货 磅单1
        public static final String OUT_CONTAINER_FOUR = "out_container_four";
        // 正在装货 箱号照片2
        public static final String OUT_CONTAINER_FIVE = "out_container_five";
        // 正在装货 集装箱内部空箱照片2
        public static final String OUT_CONTAINER_SIX = "out_container_six";
        // 正在装货 集装箱装货视频2
        public static final String OUT_CONTAINER_SEVEN = "out_container_seven";
        // 正在装货 磅单2
        public static final String OUT_CONTAINER_EIGHT = "out_container_eight";

        // 重车过磅 司机车头照片
        public static final String IN_ONE = "in_one";
        // 重车过磅 司机车尾附件
        public static final String IN_TWO = "in_two";
        // 重车过磅 行车轨迹
        public static final String IN_TEN = "in_ten";

        // 重车卸货 车头照片
        public static final String IN_THREE = "in_three";
        // 重车卸货 车尾照片
        public static final String IN_FOUR = "in_four";
        // 重车卸货 车侧身照片
        public static final String IN_FIVE = "in_five";
        // 重车卸货 卸货视频附
        public static final String IN_SIX = "in_six";

        // 空车过磅 司机车头附件
        public static final String IN_SEVEN = "in_seven";
        // 空车过磅 司机车尾附件
        public static final String IN_EIGHT = "in_eight";
        // 空车过磅 磅单
        public static final String IN_NINE = "in_nine";

        // 正在卸货 集装箱箱号照片1
        public static final String IN_CONTAINER_ONE = "in_container_one";
        // 正在卸货 集装箱内部空箱照片1
        public static final String IN_CONTAINER_TWO = "in_container_two";
        // 正在卸货 集装箱装货视频1
        public static final String IN_CONTAINER_THREE = "in_container_three";
        // 正在卸货 磅单1
        public static final String IN_CONTAINER_FOUR = "in_container_four";
        // 正在卸货 箱号照片2
        public static final String IN_CONTAINER_FIVE = "in_container_five";
        // 正在卸货 集装箱内部空箱照片2
        public static final String IN_CONTAINER_SIX = "in_container_six";
        // 正在卸货 集装箱装货视频2
        public static final String IN_CONTAINER_SEVEN = "in_container_seven";
        // 正在卸货 磅单2
        public static final String IN_CONTAINER_EIGHT = "in_container_eight";
    }

    /**
     * 订单类型
     */
    public class ORDER {
        // 入库
        public static final String B_IN_ORDER = "b_in_order";
        // 卸货
        public static final String B_OUT_ORDER = "b_out_order";
    }

    /**
     * redis前缀
     */
    public class SESSION_PREFIX {
        public static final String SESSION_USER_PREFIX_PREFIX = "XINYIRUN_SCM_USER_SESSION";
    }

    /**
     * redis前缀
     */
    public class REDIS_PREFIX {
        public static final String MQ_SEND_PREFIX = "SCM_MQ";// mq发送消息暂存到redis的prefix
        public static final String MQ_CONSUME_FAILT_PREFIX = "SCM_MQ_CONSUME_FAILT_PREFIX";
        public static final String MQ_CONSUME_RETURN_PREFIX = "SCM_MQ_CONSUME_RETURN_PREFIX";
    }

    /**
     * 加工商品
     */
    public class PRODUCT_COMM_CODE {
        /** 稻谷 */
        public static final String COMM_RICE_CODE = "zlsd-0100507-3";
        /** 玉米 */
        public static final String COMM_MAIZE_CODE = "zlsd-0100506";
        /** 小麦 */
        public static final String COMM_WHEAT_CODE = "zlsd-0100505";
        /** 稻壳 */
        public static final String COMM_RICE_HULL_CODE = "zlsd-0100511";
        /** 糙米 */
        public static final String COMM_GRAIN_CODE = "CM-001";
        /** 杂质 */
        public static final String COMM_IMPURITIES = "1";
        /** 混合物 */
//        public final List<String> COMM_COMBO_CODE = Lists.newArrayList("zlsd-0100509", "zlsd-0100510", "zlsd-0100508", "19");
    }

    /**
     * 缓存使用常量
     */
    public class CACHE_PC {
        /** 地区级联 */
        public static final String CACHE_AREAS_CASCADER = "CACHE_AREAS_CASCADER";
        /** 字典表 */
        public static final String CACHE_DICT_TYPE = "CACHE_DICT_TYPE";
        /** 列宽 */
        public static final String CACHE_COLUMNS_TYPE = "CACHE_COLUMNS_TYPE";
        /** 系统icon */
        public static final String CACHE_SYSTEM_ICON_TYPE = "CACHE_SYSTEM_ICON_TYPE";

        /**菜单查询权限*/
        public static final String CACHE_SYSTEM_MENU_SEARCH_TYPE = "CACHE_SYSTEM_MENU_SEARCH_TYPE";
        /** 菜单查询 历史记录 */
        public static final String CACHE_SYSTEM_MENU_SEARCH_HISTORY = "CACHE_SYSTEM_MENU_SEARCH_HISTORY";

        /** 系统参数 */
        public static final String CACHE_CONFIG = "CACHE_CONFIG";
    }

    /**
     * 短信验证码类型
     */
    public class SMS_CODE_TYPE {
        /** 未知 */
        public static final String SMS_CODE_TYPE_NO_TYPE = "0";
        /** 注册 */
        public static final String SMS_CODE_TYPE_REGIST = "1";
        /** 忘记密码 */
        public static final String SMS_CODE_TYPE_FORGET = "2";
        /** 修改绑定手机 */
        public static final String SMS_CODE_TYPE_CHANGE_MOBILE = "3";
    }

    /**
     * 注册时
     */
    public class SIGN_UP {
//        public static final String TENANT_SERIAL_NO = "0001";
    }

    /**
     * app_config
     */
    public class APP_CODE {
        /** 云仓 */
        public static final String WMS = "00";
        /** 中台 */
        public static final String ZT = "10";
    }

    /**
     * app_config
     */
    public class SINOIOV {
        /** 登录 */
        public static final String API_TYPE_LOGIN = "01";
        /** 物流轨迹 */
        public static final String API_TYPE_TRACK = "02";
        /** 物流轨迹-gsh56 */
        public static final String API_TYPE_TRACK_GSH56 = "21";
        /** 车辆入网验证-gsh56 */
        public static final String API_TYPE_CHECK_TRUCK_GSH56 = "22";
        /** 车辆确认验证-gsh56 */
        public static final String API_TYPE_CHECK_VEHICLE_GSH56 = "23";
        /** 物流轨迹-好伙伴-获取token */
        public static final String API_TYPE_TRACK_BEST_FRIEND_TOKEN = "31";
        /** 物流轨迹-好伙伴 */
        public static final String API_TYPE_TRACK_BEST_FRIEND = "32";
        /** 车辆入网验证-好伙伴 */
        public static final String API_TYPE_CHECK_VEHICLE_BEST_FRIEND = "33";

        /** 车排颜色(1 蓝色、2 黄色、3 黄绿色 */
        public static final String COLOR_BLUE = "1";
        public static final String COLOR_YELLOW = "2";
        public static final String COLOR_GREEN = "3";

        public static final String SUCCESS_CODE = "1001";
    }

    /**
     * app_config_detail
     */
    public class APP_URI_TYPE {
        /** 入库计划提交 */
        public static final String IN_PLAN_SUBMIT = "00";
        /** 入库计划审核 */
        public static final String IN_PLAN_AUDIT = "01";
        /** 入库计划驳回 */
        public static final String IN_PLAN_RETURN = "02";
        /** 入库计划作废 */
        public static final String IN_PLAN_CANCEL = "03";
        /** 入库计划完成 */
        public static final String IN_PLAN_FINISH = "04";
        /** 入库操作 */
        public static final String IN_PLAN_OPERATE = "05";

        /** 入库单提交 */
        public static final String IN_SUBMIT = "10";
        /** 入库单审核 */
        public static final String IN_AUDIT = "11";
        /** 入库单驳回 */
        public static final String IN_RETURN = "12";
        /** 入库单作废 */
        public static final String IN_CANCEL = "13";
        /** 入库单完成 */
        public static final String IN_FINISH = "14";

        /** 出库计划提交 */
        public static final String OUT_PLAN_SUBMIT = "20";
        /** 出库计划审核 */
        public static final String OUT_PLAN_AUDIT = "21";
        /** 出库计划驳回 */
        public static final String OUT_PLAN_RETURN = "22";
        /** 出库计划作废 */
        public static final String OUT_PLAN_CANCEL = "23";
        /** 出库计划完成 */
        public static final String OUT_PLAN_FINISH = "24";

        /** 出库单提交 */
        public static final String OUT_SUBMIT = "30";
        /** 出库单审核 */
        public static final String OUT_AUDIT = "31";
        /** 出库单驳回 */
        public static final String OUT_RETURN = "32";
        /** 出库单作废 */
        public static final String OUT_CANCEL = "33";
        /** 出库单完成 */
        public static final String OUT_FINISH = "34";


        /** 入库是否已结算 */
        public static final String IN_IS_SETTLED = "40";

        /** 出库是否已结算 */
        public static final String OUT_IS_SETTLED = "50";

        /** 入库是否可作废 */
        public static final String IN_CANCELED = "60";

        /** 出库是否可作废 */
        public static final String OUT_CANCELED = "70";

        /** 收货确认单 */
        public static final String IN_DELIVERY_DOCUMENT = "80";

        /** 监管任务 全部同步*/
        public static final String MONITOR_SYNC = "100";

        /** 出库超发校验接口 */
        public static final String OUT_CHECK_EXCESS = "110";

        /** 监管任务是否可作废 */
        public static final String MONITOR_CANCELED = "120";

        /** 出库超发校验接口-借货 */
        public static final String OUT_CHECK_EXCESS_BORROW = "130";

        /** 提货单提交 */
        public static final String DELIVERY_SUBMIT = "140";

        /** 提货单审核 */
        public static final String DELIVERY_AUDIT = "141";

        /** 提货单驳回 */
        public static final String DELIVERY_RETURN = "142";

        /** 提货单作废 */
        public static final String DELIVERY_CANCEL = "143";

        /** 提货单完成 */
        public static final String  DELIVERY_FINISH = "144";

        /** 收货单提交 */
        public static final String RECEIVE_SUBMIT = "150";

        /** 收货单审核 */
        public static final String RECEIVE_AUDIT = "151";

        /** 收货单驳回 */
        public static final String RECEIVE_RETURN = "152";

        /** 收货单作废 */
        public static final String RECEIVE_CANCEL = "153";

        /** 收货单完成 */
        public static final String RECEIVE__FINISH = "154";

        /** 提货单是否可作废 */
        public static final String DELIVERY_CANCELED = "160";

        /** 收货单是否可作废 */
        public static final String RECEIVE_CANCELED = "170";


        /** 收货单超发校验接口 */
        public static final String RECEIVE_CHECK_EXCESS = "200";

        /** 收货单超发校验接口-借货 */
        public static final String RECEIVE_CHECK_EXCESS_BORROW = "210";

    }

    /**
     * 日志分类
     */
    public class LOG_FLG {
        public static final String OK = "OK";
        public static final String NG = "NG";
    }

    /**
     * 刷新session时使用loginuserid 还是 staffid
     */
    public class LOGINUSER_OR_STAFF_ID {
        public static final String LOGIN_USER_ID = "LOGIN_USER_ID";
        public static final String STAFF_ID = "STAFF_ID";
    }

    /**
     *  平台类型
     */
    public class PLATFORM {
        public static final int OTHER = 0;
        public static final int PC = 1;
        public static final int APP = 2;
    }

    /**
     *  关联单号类型
     */
    public class SERIAL_TYPE {
        /** 入库计划 */
        public static final String B_IN_PLAN = "b_in_plan";
        /** 入库单 */
        public static final String B_IN = "b_in";
        /** 出库计划 */
        public static final String B_OUT_PLAN = "b_out_plan";
        /** 出库计划详情 */
        public static final String B_OUT_PLAN_DETAIL = "b_out_plan_detail";
        /** 出库单 */
        public static final String B_OUT = "b_out";
        /** 库存调整单 */
        public static final String B_ADJUST_DETAIL = "b_adjust_detail";
        /** 库存调拨单 */
        public static final String B_ALLOCATE_DETAIL = "b_allocate_detail";
        /** 货权转移 */
        public static final String B_OWNERCHANGE_DETAIL = "b_owner_change_detail";
        /** 库存盘点 */
        public static final String B_CHECK = "b_check";
        /** 盘点操作 */
        public static final String B_CHECK_OPERATE = "b_check_operate";
        /** 盘盈盘亏 */
        public static final String B_CHECK_RESULT = "b_check_result";
        /** 集装箱信息 */
        public static final String B_CONTAINER_INFO = "b_container_info";
        /** 监管任务 */
        public static final String B_MONITOR = "b_monitor";
        /** 入库监管 */
        public static final String B_MONITOR_IN = "b_monitor_in";
        /** 卸货监管 */
        public static final String B_MONITOR_UNLOAD = "b_monitor_unload";
        /** 出库监管 */
        public static final String B_MONITOR_OUT = "b_monitor_out";
        /** 提货监管 */
        public static final String B_MONITOR_DELIVERY = "b_monitor_delivery";
        /** 生产工单 */
        public static final String B_WO = "b_wo";

        /** 待办 */
        public static final String TO_DO_STATUS = "0";

        /** 已办 */
        public static final String ALREADY_DO_STATUS = "1";

        /** 生产工单 */
        public static final String B_RT_WO = "b_rt_wo";

        /** 生产计划工单 */
        public static final String B_PP = "b_pp";

        /** 提货单 */
        public static final String B_DELIVERY = "b_delivery";

        /** 收货单 */
        public static final String B_RECEIVE = "b_receive";

        /** 退货单信息 */
        public static final String B_RETURN_RELATION = "b_return_relation";

        /** 采购合同编号 */
        public static final String B_PO_CONTRACT = "b_po_contract";

        /** 货权转移编号 */
        public static final String B_PO_CARGO_RIGHT_TRANSFER = "b_po_cargo_right_transfer";

        /** 采购订单编号 */
        public static final String B_PO_ORDER = "b_po_order";

        /** 销售货权转移编号 */
        public static final String B_SO_CARGO_RIGHT_TRANSFER = "b_so_cargo_right_transfer";

        /** 销售合同编号 */
        public static final String B_SO_CONTRACT = "b_so_contract";

        /** 销售订单编号 */
        public static final String B_SO_ORDER  = "b_so_order";

        /** 应付账款管理表 */
        public static final String B_AP  = "b_ap";

        /** 付款单 */
        public static final String B_AP_PAY  = "b_ap_pay";

        /** 应付退款管理表 */
        public static final String B_AP_REFUND  = "b_ap_refund";

        /** 应收账款管理表 */
        public static final String B_AR = "b_ar";

        /** 应收收款单 */
        public static final String B_AR_RECEIVE = "b_ar_receive";

        /** 应收退款管理表 */
        public static final String B_AR_REFUND  = "b_ar_refund";

        /** 项目管理 */
        public static final String B_PROJECT = "b_project";

        /** 采购结算 */
        public static final String B_PO_SETTLEMENT = "b_po_settlement";

        /** 销售结算 */
        public static final String B_SO_SETTLEMENT = "b_so_settlement";
    }

    /**
     *  权限标识
     */
    public class PERMS {
        /** 入库计划提交 */
        public static final String B_IN_PLAN_DETAIL_SUBMIT = "P_INPLAN:SUBMIT";
        /** 入库计划审核 */
        public static final String B_IN_PLAN_DETAIL_AUDIT = "P_INPLAN:AUDIT";
        /** 入库计划操作 */
        public static final String B_IN_PLAN_DETAIL_OPERATE = "P_INPLAN:IN_OPERATE";
        /** 入库计划审核拒绝 */
        public static final String B_IN_PLAN_DETAIL_REJECT = "P_INPLAN:REJECT";
        /** 入库计划完成 */
        public static final String B_IN_PLAN_DETAIL_FINISH = "P_INPLAN:FINISH";
        /** 入库计划作废 */
        public static final String B_IN_PLAN_DETAIL_CANCEL = "P_INPLAN:CANCEL";


        /** 入库单提交 */
        public static final String B_IN_SUBMIT = "P_IN:SUBMIT";
        /** 入库单提交 */
        public static final String B_IN_AUDIT = "P_IN:AUDIT";
        /** 入库单审核拒绝 */
        public static final String B_IN_REJECT = "P_IN:REJECT";
        /** 入库计划完成 */
        public static final String B_IN_FINISH = "P_IN:FINISH";
        /** 入库计划作废 */
        public static final String B_IN_CANCEL = "P_IN:CANCEL";

        /** 出库计划提交 */
        public static final String B_OUT_PLAN_DETAIL_SUBMIT = "P_OUT_PLAN:SUBMIT";
        /** 出库计划审核 */
        public static final String B_OUT_PLAN_DETAIL_AUDIT = "P_OUT_PLAN:AUDIT";
        /** 出库计划审核 */
        public static final String B_OUT_PLAN_DETAIL_OPERATE = "P_OUT_PLAN:OUT_OPERATE";
        /** 出库计划审核拒绝 */
        public static final String B_OUT_PLAN_DETAIL_REJECT = "P_OUT_PLAN:REJECT";
        /** 出库计划完成 */
        public static final String B_OUT_PLAN_DETAIL_FINISH = "P_OUT_PLAN:FINISH";
        /** 出库计划作废 */
        public static final String B_OUT_PLAN_DETAIL_CANCEL = "P_OUT_PLAN:CANCEL";


        /** 出库单提交 */
        public static final String B_OUT_SUBMIT = "P_OUT:SUBMIT";
        /** 出库单提交 */
        public static final String B_OUT_AUDIT = "P_OUT:AUDIT";
        /** 出库单审核拒绝 */
        public static final String B_OUT_REJECT = "P_OUT:REJECT";
        /** 出库计划完成 */
        public static final String B_OUT_FINISH = "P_OUT:FINISH";
        /** 出库计划作废 */
        public static final String B_OUT_CANCEL = "P_OUT:CANCEL";

        /** 库存调整单提交 */
        public static final String B_ADJUST_SUBMIT = "P_INVENTORY_ADJUST:SUBMIT";
        /** 库存调整单审核 */
        public static final String B_ADJUST_AUDIT = "P_INVENTORY_ADJUST:AUDIT";

        /** 库存调拨单审核 */
        public static final String B_ALLOCATE_AUDIT = "P_INVENTORY_ALLOCATE:AUDIT";

        /** 货权转移审核 */
        public static final String B_OWNER_CHANGE_AUDIT = "P_OWNER_CHANGE:AUDIT";
        /** 生产订单(配方) 审核 */
        public static final String B_WO_AUDIT = "P_WO:AUDIT";
        /** 生产订单(配方) 驳回 */
        public static final String B_WO_REJECT = "P_WO:REJECT";
        /** 生产订单(配方) 作废 */
        public static final String B_WO_CANCEL = "P_WO:CANCEL";
        /** 生产订单(配方) 提交 */
        public static final String B_WO_SUBMIT = "P_WO:SUBMIT";
        /** 生产订单(配比) 审核 */
        public static final String B_RT_WO_AUDIT = "P_RT_WO:AUDIT";
        /** 生产订单(配比) 驳回 */

        public static final String B_RT_WO_REJECT = "P_RT_WO:REJECT";
        /** 生产订单(配比) 作废 */
        public static final String B_RT_WO_CANCEL = "P_RT_WO:CANCEL";
        /** 生产订单(配比) 提交 */
        public static final String B_RT_WO_SUBMIT = "P_RT_WO:SUBMIT";

        /** 生产计划 审核 */
        public static final String B_PP_AUDIT = "P_BPP:AUDIT";

        /** 生产计划 驳回 */
        public static final String B_PP_REJECT = "P_BPP:REJECT";
        /** 生产计划 作废 */
        public static final String B_PP_CANCEL = "P_BPP:CANCEL";

        /** 生产计划 提交 */
        public static final String B_PP_SUBMIT = "P_BPP:SUBMIT";

        /** 生产计划 已完成 */
        public static final String B_PP_FINISH = "P_BPP:FINISH";


        /** 提货单提交 */
        public static final String B_DELIVERY_SUBMIT = "P_DELIVERY:SUBMIT";

        /** 提货单审核 */
        public static final String B_DELIVERY_AUDIT = "P_DELIVERY:AUDIT";


        /** 提货单驳回 */
        public static final String B_DELIVERY_REJECT = "P_DELIVERY:REJECT";

        /**提货单作废*/
        public static final String B_DELIVERY_CANCEL = "P_DELIVERY:CANCEL";

        /** 收货单提交 */
        public static final String B_RECEIVE_SUBMIT = "P_RECEIVE:SUBMIT";
        /** 收货单提交 */
        public static final String B_RECEIVE_AUDIT = "P_RECEIVE:AUDIT";
        /** 收货单审核拒绝 */
        public static final String B_RECEIVE_REJECT = "P_RECEIVE:REJECT";
        /** 收货计划完成 */
        public static final String B_RECEIVE_FINISH = "P_RECEIVE:FINISH";
        /** 收货计划作废 */
        public static final String B_RECEIVE_CANCEL = "P_RECEIVE:CANCEL";

    }
    /**
     * 页面编号常量类
     */
    public class PAGE {
        public static final String P00000038 = "P00000038"; // 系统编码管理
        public static final String P00000035 = "P00000035"; // 字典类型维护
        public static final String P00000036 = "P00000036"; // 字典数据维护
        public static final String P00000037 = "P00000037"; // 系统参数
        public static final String P00000019 = "P00000019"; // 集团信息维护
        public static final String P00000020 = "P00000020"; // 企业信息维护
        public static final String P00000023 = "P00000023"; // 员工管理维护
        public static final String P00000021 = "P00000021"; // 部门信息维护
        public static final String P00000022 = "P00000022"; // 岗位信息维护
        public static final String P00000018 = "P00000018"; // 组织机构维护
        public static final String P00000001 = "P00000001"; // 工作台
        public static final String P00000040 = "P00000040"; // 按钮维护
        public static final String P00000041 = "P00000041"; // 页面维护
        public static final String P00000042 = "P00000042"; // 页面按钮维护
        public static final String P00000043 = "P00000043"; // 菜单维护
        public static final String P00000025 = "P00000025"; // 仓库管理
        public static final String P00000026 = "P00000026"; // 库区管理
        public static final String P00000027 = "P00000027"; // 库位管理
        public static final String P00000028 = "P00000028"; // 板块管理
        public static final String P00000029 = "P00000029"; // 行业管理
        public static final String P00000030 = "P00000030"; // 类别管理
        public static final String P00000031 = "P00000031"; // 物料管理
        public static final String P00000032 = "P00000032"; // 规格管理
        public static final String P00000033 = "P00000033"; // 客户管理
        public static final String P00000010 = "P00000010"; // 入库计划
        public static final String P00000011 = "P00000011"; // 入库单
        public static final String P00000013 = "P00000013"; // 出库计划
        public static final String P00000014 = "P00000014"; // 出库单
        public static final String P00000012 = "P00000012"; // 入库订单管理
        public static final String P00000015 = "P00000015"; // 出库订单管理
        public static final String P00000016 = "P00000016"; // 库存明细
        public static final String P00000044 = "P00000044"; // 权限设置
        public static final String P00000045 = "P00000045"; // 角色管理
        public static final String P00000046 = "P00000046"; // 授权管理
        public static final String P00000053 = "P00000053"; // 货主管理
        public static final String P00000254 = "P00000254"; // 岗位角色
        public static final String P00000017 = "P00000017"; // 库存调整
        public static final String P00000060 = "P00000060"; // 库存调拨
        public static final String P00000061 = "P00000061"; // 盘点任务管理
        public static final String P00000062 = "P00000062"; // 盘点操作管理
        public static final String P00000063 = "P00000063"; // 盘盈盘亏操作
        public static final String P00000064 = "P00000064"; // 货主库存
        public static final String P00000065 = "P00000065"; // 货主规格
        public static final String P00000024 = "P00000024"; // 库存流水
        public static final String P00000054 = "P00000054"; // 物流订单
        public static final String P00000055 = "P00000055"; // 仓库组
        public static final String P00000058 = "P00000058"; // 定时任务
        public static final String P00000059 = "P00000059"; // 调拨订单
        public static final String P00000066 = "P00000066"; // 司机管理
        public static final String P00000067 = "P00000067"; // 车辆管理
        public static final String P00000068 = "P00000068"; // 监管任务
        public static final String P00000069 = "P00000069"; // 仓库分组
        public static final String P00000070 = "P00000070"; // 系统日志
        public static final String P00000071 = "P00000071"; // API日志
        public static final String P00000072 = "P00000072"; // APP日志
        public static final String P00000073 = "P00000073"; // APP通知
        public static final String P00000074 = "P00000074"; // 每日库存查询
        public static final String P00000075 = "P00000075"; // 数据导入日志
        public static final String P00000076 = "P00000076"; // 货权转移
        public static final String P00000077 = "P00000077"; // 货权转移订单
        public static final String P00000078 = "P00000078"; // 物料转换明细
        public static final String P00000096 = "P00000096"; // 按仓库类型商品
        public static final String P00000080 = "P00000080"; // 按仓库类型仓库商品-入库
        public static final String P00000081 = "P00000081"; // 按仓库类型仓库商品-出库
        public static final String P00000082 = "P00000082"; // 按仓库类型仓库商品-调整
        public static final String P00000083 = "P00000083"; // 按仓库类型商品仓库-存货
        public static final String P00000084 = "P00000084"; // 按仓库类型仓库商品
        public static final String P00000085 = "P00000085"; // 采购合同汇总
        public static final String P00000086 = "P00000086"; // 销售合同汇总
        public static final String P00000087 = "P00000087"; // 损耗报表明细
        public static final String P00000088 = "P00000088"; // 物流订单损耗明细
        public static final String P00000089 = "P00000089"; // 监管任务损耗明细
        public static final String P00000090 = "P00000090"; // 在途报表明细
        public static final String P00000091 = "P00000091"; // 物流订单在途明细
        public static final String P00000092 = "P00000092"; // 监管任务在途明细
        public static final String P00000093 = "P00000093"; // 在途报表汇总
        public static final String P00000094 = "P00000094"; // 在途损耗报表汇总
        public static final String P00000095 = "P00000095"; // 数据看板
        public static final String P00000097 = "P00000097"; // 个人中心
        public static final String P00000098 = "P00000098"; // 直属库仓库统计表
        public static final String P00000099 = "P00000099"; // 服务监控
        public static final String P00000100 = "P00000100"; // 缓存监控
        public static final String P00000101 = "P00000101"; // 物料转换商品价格
        public static final String P00000102 = "P00000102"; // 同步日志
        public static final String P00000103 = "P00000103"; // 系统通知
        public static final String P00000105 = "P00000105"; // 库存预警
        public static final String P00000106 = "P00000106"; // 物料转换记录
        public static final String P00000107 = "P00000107"; // 放货指令管理
        public static final String P00000108 = "P00000108"; // 物料转换
        public static final String P00000109 = "P00000109"; // 页面列配置
        public static final String P00000110 = "P00000110"; // 商品价格
        public static final String P00000111 = "P00000111"; // 预警组
        public static final String P00000112 = "P00000112"; // 生产配方管理
        public static final String P00000113 = "P00000113"; // 生产管理
        public static final String P00000114 = "P00000114"; // 预警人员
        public static final String P00000115 = "P00000115"; // 配方生产管理
        public static final String P00000116 = "P00000116"; // 配方生产配方管理
        public static final String P00000117 = "P00000117"; // 监管任务历史记录
        public static final String P00000118 = "P00000118"; // 系统日志Mongo
        public static final String P00000119 = "P00000119"; // API日志Mongo
        public static final String P00000120 = "P00000120"; // APP日志Mongo
        public static final String P00000121 = "P00000121"; // 同步日志Mongo
        public static final String P00000122 = "P00000122"; // 数据导入日志new
        public static final String P00000123 = "P00000123"; // 业务流程模型
        public static final String P00000124 = "P00000124"; // 预警设置
        public static final String P00000125 = "P00000125"; // 监管任务备份日志
        public static final String P00000126 = "P00000126"; // mq生产者日志
        public static final String P00000127 = "P00000127"; // mq消费者日志
        public static final String P00000128 = "P00000128"; // 加工日报表
    }


    /**
     * 操作日志内容
     */
    public class OPERATION {

        /**
         * 集团
         */
        public class M_GROUP {
            public static final String TABLE_NAME = "m_group";

            public static final String OPER_INSERT = "集团主表新增";
            public static final String OPER_UPDATE = "集团主表更新";
            public static final String OPER_LOGIC_DELETE = "集团主表逻辑删除";
        }

        /**
         * 组织机构
         */
        public class M_ORG {
            public static final String TABLE_NAME = "m_org";

            public static final String OPER_INSERT = "组织主表新增";
            public static final String OPER_UPDATE = "组织主表更新";
            public static final String OPER_DELETE = "组织主表物理删除";
            public static final String OPER_DRAG_DROP = "组织主表拖拽操作";
            public static final String OPER_POSITION_STAFF = "用户组织机构关系表，成员维护";
        }

        /**
         * 用户组织机构关系表
         */
        public class M_STAFF_ORG {
            public static final String TABLE_NAME = "m_staff_org";

            public static final String OPER_INSERT = "用户组织机构关系表新增";
            public static final String OPER_UPDATE = "用户组织机构关系表更新";
            public static final String OPER_DELETE = "用户组织机构关系表物理删除";
            public static final String OPER_POSITION_STAFF = "用户组织机构关系表，成员维护";
        }

        /**
         * 权限角色关系表
         */
        public class M_PERMISSION_ROLE {
            public static final String TABLE_NAME = "m_permission_role";

            public static final String OPER_INSERT = "权限角色关系表新增";
            public static final String OPER_UPDATE = "权限角色关系表更新";
            public static final String OPER_DELETE = "权限角色关系表物理删除";
            public static final String OPER_ROLE_STAFF = "权限角色关系表，成员维护";
        }

        /**
         * 角色岗位关系表
         */
        public class M_ROLE_POSITION {
            public static final String TABLE_NAME = "m_role_position";

            public static final String OPER_INSERT = "角色岗位关系表新增";
            public static final String OPER_UPDATE = "角色岗位关系表更新";
            public static final String OPER_DELETE = "角色岗位关系表物理删除";
            public static final String OPER_POSITION_ROLE = "角色岗位关系表，角色维护";
        }

    }

    /**
     * 顶部导航栏类型
     */
    public class TOP_NAV {
        /**
         * 按路径查询
         */
        public static final String TOP_NAV_FIND_BY_PATH = "find_by_path";
        /**
         * 按排序查询
         */
        public static final String TOP_NAV_FIND_BY_INDEX = "find_by_index";
    }

    /**
     * 默认单位：吨，常量
     */
    public class DEFAULT_UNIT {
        public static final String CODE = "DUN";
        public static final String NAME = "吨";
    }

    /**
     * 数据同步状态
     */
    public class B_SYNC_STATUS {
        public static final String FAILED = "0";
        public static final String SUCCESS= "1";
    }

    /**
     * 关于每日库存工作表中的类型
     */
    public class DAILY_INVENTORY_SYNC_TYPES {
        public static final String IN = "01";
        public static final String OUT= "02";
        public static final String ADJUST = "03";
    }

    /**
     * 关于数据的来源, erp推过来的, wms自己新增的
     */
    public class DATA_SOURCE_TYPE {
        public static final String WMS = "wms";
    }
    /** API调用出错信息 */
    public static final String API_ERROR = "API调用出错：";

    /** 物流订单计算类型, out是出库, in是入库 */
    public class SCHEDULE_CALC_TYPE {
        public static final String OUT = "out";
        public static final String IN = "in";
    }

    /** 承运商社会统一信用代码 */
    public class CUSTOMER_CREDIT_NO {
        // 上海青润盛禾农业有限公司
        public static final String SHHRSHYYXGS = "91310120MA7EYB0AXE";
    }

    /** 全局的requestid类型常量 */
    public static final String REQUEST_ID = "MY_REQUEST_ID";

    /** 预警使用系统参数 */
    public class WARNING_TYPE{

        /** 监管任务损耗预警百分比 */
        public static final String M_MONITOR_LOSS_PERCENTAGE  = "m_monitor_loss_percentage";


        /** 港口允许停滞时间 */
        public static final String M_INVENTORY_STAGNATION_TIME  = "m_inventory_stag_time";

    }

    public class WECHAT {
        public static final String SCOPE = "snsapi_userinfo";
    }

    public class BPM_FORM {
        public static final String FIELD_TYPE_TEXTINPUT = "TextInput";
        public static final String FIELD_TYPE_NUMBERINPUT = "NumberInput";
        public static final String FIELD_TYPE_SELECTINPUT = "SelectInput";
        public static final String VALUE_TYPE_STRING = "String";
        public static final String VALUE_TYPE_NUMBER = "Number";
    }

    /**
     * 业务流程模型code
     */
    public class BPM_PROCESS_CODE {

//        // 出库计划
//        public static final String BPM_PROCESS_B_OUT_PLAN = "BPM20240102001";
//
//        // 企业管理
//        public static final String BPM_PROCESS_M_ENTERPRISE = "BPM20240102002";
//
//        // 项目管理
//        public static final String BPM_PROCESS_B_PROJECT = "BPM20240102003";
//
//        // 采购合同
//        public static final String BPM_PROCESS_B_PO_CONTRACT = "BPM20240102004";
//
//        // 销售合同
//        public static final String BPM_PROCESS_B_SO_CONTRACT = "BPM20240102005";
    }

    /**
     * 审批流实例化关联业务类型
     */
    public class BPM_INSTANCE_TYPE {

        // 采购结算
        public static final String BPM_INSTANCE_B_PO_SETTLEMENT = "b_po_settlement";

        // 作废 采购结算
        public static final String BPM_INSTANCE_B_PO_SETTLEMENT_CANCEL = "b_po_settlement_cancel";

        // 销售结算
        public static final String BPM_INSTANCE_B_SO_SETTLEMENT = "b_so_settlement";

        // 作废 销售结算
        public static final String BPM_INSTANCE_B_SO_SETTLEMENT_CANCEL = "b_so_settlement_cancel";

        // 出库计划
        public static final String BPM_INSTANCE_B_OUT_PLAN = "b_out_plan";

        // 作废 出库计划
        public static final String BPM_INSTANCE_B_OUT_PLAN_CANCEL = "b_out_plan_cancel";

        // 出库单
        public static final String BPM_INSTANCE_B_OUT = "b_out";

        // 作废 出库单
        public static final String BPM_INSTANCE_B_OUT_CANCEL = "b_out_cancel";

        // 企业管理
        public static final String BPM_INSTANCE_M_ENTERPRISE = "m_enterprise";

        // 采购项目管理
        public static final String BPM_INSTANCE_B_PO_PROJECT = "b_po_project";

        // 销售项目管理
        public static final String BPM_INSTANCE_B_SO_PROJECT = "b_so_project";

        // 作废 采购项目管理
        public static final String BPM_INSTANCE_B_PO_PROJECT_CANCEL = "b_po_project_cancel";

        // 作废 销售项目管理
        public static final String BPM_INSTANCE_B_SO_PROJECT_CANCEL = "b_so_project_cancel";

        // 采购合同
        public static final String BPM_INSTANCE_B_PO_CONTRACT = "b_po_contract";

        // 作废 采购合同
        public static final String BPM_INSTANCE_B_PO_CONTRACT_CANCEL = "b_po_contract_cancel";

        // 货权转移
        public static final String BPM_INSTANCE_B_PO_CARGO_RIGHT_TRANSFER = "b_po_cargo_right_transfer";

        // 作废 货权转移
        public static final String BPM_INSTANCE_B_PO_CARGO_RIGHT_TRANSFER_CANCEL = "b_po_cargo_right_transfer_cancel";

        // 销售货权转移
        public static final String BPM_INSTANCE_B_SO_CARGO_RIGHT_TRANSFER = "b_so_cargo_right_transfer";

        // 作废 销售货权转移
        public static final String BPM_INSTANCE_B_SO_CARGO_RIGHT_TRANSFER_CANCEL = "b_so_cargo_right_transfer_cancel";

        // 销售合同
        public static final String BPM_INSTANCE_B_SO_CONTRACT = "b_so_contract";

        // 作废 销售合同
        public static final String BPM_INSTANCE_B_SO_CONTRACT_CANCEL = "b_so_contract_cancel";

        // 采购订单
        public static final String BPM_INSTANCE_B_PO_ORDER = "b_po_order";

        // 作废 采购订单
        public static final String BPM_INSTANCE_B_PO_ORDER_CANCEL = "b_po_order_cancel";

        // 销售订单
        public static final String BPM_INSTANCE_B_SO_ORDER = "b_so_order";

        // 作废 销售订单
        public static final String BPM_INSTANCE_B_SO_ORDER_CANCEL = "b_so_order_cancel";

        // 付款管理
        public static final String BPM_INSTANCE_B_AP = "b_ap";

        // 付款管理作废流程
        public static final String BPM_INSTANCE_B_AP_CANCEL = "b_ap_cancel";

        // 退款管理
        public static final String BPM_INSTANCE_B_AP_REFUND = "b_ap_refund";

        // 退款管理作废流程
        public static final String BPM_INSTANCE_B_AP_REFUND_CANCEL = "b_ap_refund_cancel";

        // 应收账款管理
        public static final String BPM_INSTANCE_B_AR = "b_ar";

        // 应收账款管理作废流程
        public static final String BPM_INSTANCE_B_AR_CANCEL = "b_ar_cancel";

        // 应收退款管理
        public static final String BPM_INSTANCE_B_AR_REFUND = "b_ar_refund";

        // 应收退款管理作废流程
        public static final String BPM_INSTANCE_B_AR_REFUND_CANCEL = "b_ar_refund_cancel";

        // 入库计划
        public static final String BPM_INSTANCE_B_IN_PLAN = "b_in_plan";

        // 入库计划作废流程
        public static final String BPM_INSTANCE_B_IN_PLAN_CANCEL = "b_in_plan_cancel";

        // 入库单
        public static final String BPM_INSTANCE_B_IN = "b_in";

        // 入库单作废流程
        public static final String BPM_INSTANCE_B_IN_CANCEL = "b_in_cancel";
    }

    /**
     * 款项类型
     */
    public class M_BANK_ACCOUNTS_TYPE {
            public static final String M_BANK_ACCOUNTS_TYPE_YUFUKUAN = "预付款";
            public static final String M_BANK_ACCOUNTS_TYPE_YUSHOUKUAN = "预收款";
            public static final String M_BANK_ACCOUNTS_TYPE_YINFUKUAN = "应付款";
            public static final String M_BANK_ACCOUNTS_TYPE_YINSHOUKUAN = "应收款";
    }


    /**
     * 默认值
     */
    public class DEFAULT_VALUE {
        /** 默认的单位ID：吨 */
        public static final Integer UNIT = 151;
    }

}
