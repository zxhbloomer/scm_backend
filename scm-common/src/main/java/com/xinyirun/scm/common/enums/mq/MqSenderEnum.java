package com.xinyirun.scm.common.enums.mq;

/**
 * @ClassName: mq发送枚举类
 * @Author: zxh
 * @date: 2019/10/16
 * @Version: 1.0
 */
public enum MqSenderEnum {



    NORMAL_MQ("NORMAL_MQ", "通用队列"),
    TEST_MQ("TEST_MQ", "测试队列"),
    SYNC_ERROR_MSG_QUEUE_IN_PLAN_MQ("SYNC_ERROR_MSG_QUEUE_IN_PLAN_MQ", "入库计划数据同步错误通知队列"),
    SYNC_ERROR_MSG_QUEUE_IN_MQ("SYNC_ERROR_MSG_QUEUE_IN_MQ", "入库单数据同步错误通知队列"),
    SYNC_ERROR_MSG_QUEUE_OUT_PLAN_MQ("SYNC_ERROR_MSG_QUEUE_OUT_PLAN_MQ", "出库计划数据同步错误通知队列"),
    SYNC_ERROR_MSG_QUEUE_OUT_MQ("SYNC_ERROR_MSG_QUEUE_OUT_MQ", "出库单数据同步错误通知队列"),
    SYNC_ERROR_MSG_QUEUE_MONITOR_MQ("SYNC_ERROR_MSG_QUEUE_MONITOR_MQ", "监管任务数据同步错误通知队列"),
    SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_IN(MqSenderConstants.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_IN, "业务中台同步队列(所有的)：入库单"),
    SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_OUT(MqSenderConstants.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_OUT, "业务中台同步队列(所有的)：出库单"),
    SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_IN_PLAN(MqSenderConstants.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_IN_PLAN, "业务中台同步队列(所有的)：入库计划"),
    SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_OUT_PLAN(MqSenderConstants.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_OUT_PLAN, "业务中台同步队列(所有的)：出库计划"),
    SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_MONITOR(MqSenderConstants.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_MONITOR, "业务中台同步队列(所有的)：监管任务"),
    MQ_LOG_PC_SYSTEM_QUEUE(MqSenderConstants.MQ_LOG_PC_SYSTEM_QUEUE, "系统日志"),
    MQ_LOG_IMPORT_QUEUE(MqSenderConstants.MQ_LOG_IMPORT_QUEUE, "导入日志"),
    MQ_LOG_API_QUEUE(MqSenderConstants.MQ_LOG_API_QUEUE, "api日志"),
    MQ_LOG_DATA_CHANGE_QUEUE(MqSenderConstants.MQ_LOG_DATA_CHANGE_QUEUE, "操作日志队列-数据更新前更新后"),
    MQ_LOG_APP_QUEUE(MqSenderConstants.MQ_LOG_APP_QUEUE, "app日志"),
    MQ_LOG_QUARTZ_QUEUE(MqSenderConstants.MQ_LOG_QUARTZ_QUEUE, "调度日志"),
    MQ_MONITOR_BACKUP_QUEUE(MqSenderConstants.MQ_MONITOR_BACKUP_QUEUE, "监管任务备份"),
    MQ_MONITOR_RENEW_QUEUE(MqSenderConstants.MQ_MONITOR_RENEW_QUEUE, "监管任务恢复"),

    MQ_MONITOR_BACKUP_QUEUE_V2(MqSenderConstants.MQ_MONITOR_BACKUP_QUEUE_V2, "监管任务备份v2"),
    MQ_MONITOR_RENEW_QUEUE_V2(MqSenderConstants.MQ_MONITOR_RENEW_QUEUE_V2, "监管任务恢复v2"),
    MQ_FILE_BACKUP_QUEUE(MqSenderConstants.MQ_FILE_BACKUP_QUEUE, "文件备份"),
    MQ_RECREATE_DAILY_INVENTORY_QUEUE(MqSenderConstants.MQ_RECREATE_DAILY_INVENTORY_QUEUE, "入出库单作废后重新生成每日库存"),
    MQ_SCHEDULE_CALC_QTY_QUEUE(MqSenderConstants.MQ_SCHEDULE_CALC_QTY_QUEUE, "计算物流订单物流数量"),

    SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_RECEIVE(MqSenderConstants.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_RECEIVE, "业务中台同步队列(所有的)：收货单"),
    SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_DELIVERY(MqSenderConstants.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_DELIVERY, "业务中台同步队列(所有的)：提货单"),

    ;

    private String code;

    private String name;

    MqSenderEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName(){
        return name;
    }

    public String getContent() {
        return "mqCode:" + code +";mqName:" +name;
    }


    public class MqSenderConstants {
        // 业务中台同步队列(所有的)：入库单
        public static final String SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_IN = "SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_IN";
        // 业务中台同步队列(所有的)：出库单
        public static final String SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_OUT = "SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_OUT";
        // 业务中台同步队列(所有的): 入库计划
        public static final String SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_IN_PLAN = "SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_IN_PLAN";
        // 业务中台同步队列(所有的): 入库计划
        public static final String SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_OUT_PLAN = "SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_OUT_PLAN";
        // 业务中台同步队列(所有的): 监管任务
        public static final String SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_MONITOR = "SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_MONITOR";
        // 系统日志
        public static final String MQ_LOG_PC_SYSTEM_QUEUE = "scm_pc_system_log";
        // 导入日志
        public static final String MQ_LOG_IMPORT_QUEUE = "scm_import_log";
        // api 日志
        public static final String MQ_LOG_API_QUEUE = "scm_api_log";
        // api 日志
        public static final String MQ_LOG_APP_QUEUE = "scm_app_log";
        // 操作日志队列-数据更新前更新后
        public static final String MQ_LOG_DATA_CHANGE_QUEUE = "scm_data_change_log";
        // 调度 日志
        public static final String MQ_LOG_QUARTZ_QUEUE = "scm_quartz_log";
        // 监管任务备份
        public static final String MQ_MONITOR_BACKUP_QUEUE = "scm_monitor_backup1";
        // 监管任务恢复
        public static final String MQ_MONITOR_RENEW_QUEUE = "scm_monitor_renew";
        // 监管任务备份v2
        public static final String MQ_MONITOR_BACKUP_QUEUE_V2 = "scm_monitor_backup_v2";
        // 监管任务恢复v2
        public static final String MQ_MONITOR_RENEW_QUEUE_V2 = "scm_monitor_renew_v2";
        public static final String MQ_FILE_BACKUP_QUEUE = "scm_file_backup";
        // 入出库单作废后重新生成每日库存
        public static final String MQ_RECREATE_DAILY_INVENTORY_QUEUE = "MQ_RECREATE_DAILY_INVENTORY_QUEUE";
        // 物流订单计算 数量
        public static final String MQ_SCHEDULE_CALC_QTY_QUEUE = "MQ_SCHEDULE_CALC_QTY_QUEUE";

        // 业务中台同步队列(所有的)：提货单
        public static final String SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_DELIVERY = "SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_DELIVERY";
        // 业务中台同步队列(所有的)：收货单
        public static final String SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_RECEIVE = "SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_RECEIVE";
    }
}
