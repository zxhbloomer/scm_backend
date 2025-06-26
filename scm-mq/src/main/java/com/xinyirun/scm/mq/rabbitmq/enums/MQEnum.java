package com.xinyirun.scm.mq.rabbitmq.enums;

/**
 * mq重要配置
 */
public enum MQEnum {
	MQ_TASK_Tenant_ENABLE(MqInfo.TenantEnableTask.queueCode,
		MqInfo.TenantEnableTask.name,
		MqInfo.TenantEnableTask.exchange,
		MqInfo.TenantEnableTask.routing_key),
	MQ_TASK_Tenant_Disable(MqInfo.TenantDisableTask.queueCode,
		MqInfo.TenantDisableTask.name,
		MqInfo.TenantDisableTask.exchange,
		MqInfo.TenantDisableTask.routing_key),
	MQ_TEST(MqInfo.TEST.queueCode,
			MqInfo.TEST.name,
			MqInfo.TEST.exchange,
			MqInfo.TEST.routing_key),
	// 入库计划
	MQ_SYNC_ERROR_MSG_QUEUE_IN_PLAN(MqInfo.SYNC_ERROR_MSG_QUEUE_IN_PLAN.queueCode,
			MqInfo.SYNC_ERROR_MSG_QUEUE_IN_PLAN.name,
			MqInfo.SYNC_ERROR_MSG_QUEUE_IN_PLAN.exchange,
			MqInfo.SYNC_ERROR_MSG_QUEUE_IN_PLAN.routing_key),
	// 入库单
	MQ_SYNC_ERROR_MSG_QUEUE_IN(MqInfo.SYNC_ERROR_MSG_QUEUE_IN.queueCode,
			MqInfo.SYNC_ERROR_MSG_QUEUE_IN.name,
			MqInfo.SYNC_ERROR_MSG_QUEUE_IN.exchange,
			MqInfo.SYNC_ERROR_MSG_QUEUE_IN.routing_key),
	// 出库计划
	MQ_SYNC_ERROR_MSG_QUEUE_OUT_PLAN(MqInfo.SYNC_ERROR_MSG_QUEUE_OUT_PLAN.queueCode,
			MqInfo.SYNC_ERROR_MSG_QUEUE_OUT_PLAN.name,
			MqInfo.SYNC_ERROR_MSG_QUEUE_OUT_PLAN.exchange,
			MqInfo.SYNC_ERROR_MSG_QUEUE_OUT_PLAN.routing_key),
	// 出库单
	MQ_SYNC_ERROR_MSG_QUEUE_OUT(MqInfo.SYNC_ERROR_MSG_QUEUE_OUT.queueCode,
			MqInfo.SYNC_ERROR_MSG_QUEUE_OUT.name,
			MqInfo.SYNC_ERROR_MSG_QUEUE_OUT.exchange,
			MqInfo.SYNC_ERROR_MSG_QUEUE_OUT.routing_key),
	// 监管任务
	MQ_SYNC_ERROR_MSG_QUEUE_MONITOR(MqInfo.SYNC_ERROR_MSG_QUEUE_MONITOR.queueCode,
			MqInfo.SYNC_ERROR_MSG_QUEUE_MONITOR.name,
			MqInfo.SYNC_ERROR_MSG_QUEUE_MONITOR.exchange,
			MqInfo.SYNC_ERROR_MSG_QUEUE_MONITOR.routing_key),
	// 业务中台同步队列(所有的)SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MSG_QUEUE
	MQ_SYNC_BUSINESS_PLATFORM_ALL_IN_ONE(MqInfo.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MSG_QUEUE.queueCode,
			MqInfo.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MSG_QUEUE.name,
			MqInfo.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MSG_QUEUE.exchange,
			MqInfo.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MSG_QUEUE.routing_key),

	/** test */
	MQ_SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_TEST(MqInfo.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MSG_QUEUE_TEST.queueCode,
			MqInfo.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MSG_QUEUE_TEST.name,
			MqInfo.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MSG_QUEUE_TEST.exchange,
			MqInfo.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MSG_QUEUE_TEST.routing_key),

    // 接口日志队列
	MQ_LOG_PC_SYSTEM_QUEUE(MqInfo.LOG_PC_SYSTEM_QUEUE.queueCode,
			MqInfo.LOG_PC_SYSTEM_QUEUE.name,
			MqInfo.LOG_PC_SYSTEM_QUEUE.exchange,
			MqInfo.LOG_PC_SYSTEM_QUEUE.routing_key),

	// 操作日志队列-数据更新前更新后
	MQ_LOG_DATA_CHANGE_QUEUE(MqInfo.LOG_DATA_CHANGE_QUEUE.queueCode,
			MqInfo.LOG_DATA_CHANGE_QUEUE.name,
			MqInfo.LOG_DATA_CHANGE_QUEUE.exchange,
			MqInfo.LOG_DATA_CHANGE_QUEUE.routing_key),

	// 导入日志队列
	MQ_LOG_IMPORT_QUEUE(MqInfo.LOG_IMPORT_QUEUE.queueCode,
			MqInfo.LOG_IMPORT_QUEUE.name,
			MqInfo.LOG_IMPORT_QUEUE.exchange,
			MqInfo.LOG_IMPORT_QUEUE.routing_key),

	// api日志队列
	MQ_LOG_API_QUEUE(MqInfo.LOG_API_QUEUE.queueCode,
			MqInfo.LOG_API_QUEUE.name,
			MqInfo.LOG_API_QUEUE.exchange,
			MqInfo.LOG_API_QUEUE.routing_key),

	// app日志队列
	MQ_LOG_APP_QUEUE(MqInfo.LOG_APP_QUEUE.queueCode,
			MqInfo.LOG_APP_QUEUE.name,
			MqInfo.LOG_APP_QUEUE.exchange,
			MqInfo.LOG_APP_QUEUE.routing_key),

	// 调度日志队列
	MQ_LOG_QUARTZ_QUEUE(MqInfo.LOG_QUARTZ_QUEUE.queueCode,
			MqInfo.LOG_QUARTZ_QUEUE.name,
			MqInfo.LOG_QUARTZ_QUEUE.exchange,
			MqInfo.LOG_QUARTZ_QUEUE.routing_key),

	// 监管任务备份队列
	MQ_MONITOR_BACKUP_QUEUE(MqInfo.MONITOR_BACKUP_QUEUE.queueCode,
			MqInfo.MONITOR_BACKUP_QUEUE.name,
			MqInfo.MONITOR_BACKUP_QUEUE.exchange,
			MqInfo.MONITOR_BACKUP_QUEUE.routing_key),
	// 监管任务恢复队列
	MQ_MONITOR_RENEW_QUEUE(MqInfo.MONITOR_RENEW_QUEUE.queueCode,
			MqInfo.MONITOR_RENEW_QUEUE.name,
			MqInfo.MONITOR_RENEW_QUEUE.exchange,
			MqInfo.MONITOR_RENEW_QUEUE.routing_key),
	// 监管任务备份队列
	MQ_MONITOR_BACKUP_QUEUE_V2(MqInfo.MONITOR_BACKUP_QUEUE_V2.queueCode,
			MqInfo.MONITOR_BACKUP_QUEUE_V2.name,
			MqInfo.MONITOR_BACKUP_QUEUE_V2.exchange,
			MqInfo.MONITOR_BACKUP_QUEUE_V2.routing_key),
	// 监管任务恢复队列
	MQ_MONITOR_RENEW_QUEUE_V2(MqInfo.MONITOR_RENEW_QUEUE_V2.queueCode,
			MqInfo.MONITOR_RENEW_QUEUE_V2.name,
			MqInfo.MONITOR_RENEW_QUEUE_V2.exchange,
			MqInfo.MONITOR_RENEW_QUEUE_V2.routing_key),

	// 文件备份队列
	MQ_FILE_BACKUP_QUEUE(MqInfo.FILE_BACKUP_QUEUE.queueCode,
			MqInfo.FILE_BACKUP_QUEUE.name,
			MqInfo.FILE_BACKUP_QUEUE.exchange,
			MqInfo.FILE_BACKUP_QUEUE.routing_key),

	// 入出库单作废后重新生成每日库存队列
	MQ_RECREATE_DAILY_INVENTORY_QUEUE(MqInfo.MQ_RECREATE_DAILY_INVENTORY_QUEUE.queueCode,
			MqInfo.MQ_RECREATE_DAILY_INVENTORY_QUEUE.name,
			MqInfo.MQ_RECREATE_DAILY_INVENTORY_QUEUE.exchange,
			MqInfo.MQ_RECREATE_DAILY_INVENTORY_QUEUE.routing_key),

	// 入出库单作废后重新生成每日库存队列
	MQ_SCHEDULE_CALC_QTY_QUEUE(MqInfo.MQ_SCHEDULE_CALC_QTY_QUEUE.queueCode,
			MqInfo.MQ_SCHEDULE_CALC_QTY_QUEUE.name,
			MqInfo.MQ_SCHEDULE_CALC_QTY_QUEUE.exchange,
			MqInfo.MQ_SCHEDULE_CALC_QTY_QUEUE.routing_key),


	;

	private String queueCode;
	private String name;
	private String exchange;
	private String routing_key;

	private MQEnum(String queueCode, String name, String exchange, String routing_key) {
		this.queueCode = queueCode;
		this.name = name;
		this.exchange = exchange;
		this.routing_key = routing_key;
	}

	public String getQueueCode() {
		return queueCode;
	}

	public void setQueueCode(String queueCode) {
		this.queueCode = queueCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getRouting_key() {
		return routing_key;
	}

	public void setRouting_key(String routing_key) {
		this.routing_key = routing_key;
	}

	public static class MqInfo {

		/**
		 * 平台任务类，需要在quartz中实现
		 *
		 * @author zxh
		 * @date 2019年 10月12日 23:34:54
		 */
		public class Task {
			public static final String queueCode = "wms-task";
			public static final String name = "平台任务类";
			public static final String exchange = "wms-task-exchange";
			public static final String routing_key = "wms-task.#";
		}

		/**
		 * 租户任务消息队列,需要在quartz中实现：启用
		 *
		 * @author zxh
		 * @date 2019年 10月12日 23:34:54
		 */
		public class TenantEnableTask {
			public static final String queueCode = "wms-task-tenant-enable";
			public static final String name = "租户任务消息队列：启用";
			public static final String exchange = "wms-task-tenant-enable-exchange";
			public static final String routing_key = "wms-task-tenant-enable.#";
		}

		/**
		 * 租户任务消息队列,需要在quartz中实现：禁用
		 *
		 * @author zxh
		 * @date 2019年 10月12日 23:34:54
		 */
		public class TenantDisableTask {
			public static final String queueCode = "wms-task-tenant-disable";
			public static final String name = "租户任务消息队列：关闭";
			public static final String exchange = "wms-task-tenant-disable-exchange";
			public static final String routing_key = "wms-task-tenant-disable.#";
		}

		/**
		 * test
		 *
		 * @author zxh
		 * @date 2019年 10月12日 23:34:54
		 */
		public class TEST {
			public static final String queueCode = "wms-test-que_www";
			public static final String name = "测试队列-www";
			public static final String exchange = "wms-test-que-exchange_www";
			public static final String routing_key = "wms-test-que-www.#";
		}


		/**
		 * 入库计划数据同步错误通知
		 *
		 * @author zxh
		 * @date 2019年 10月12日 23:34:54
		 */
		public class SYNC_ERROR_MSG_QUEUE_IN_PLAN {
			public static final String queueCode = "wms-error-msg-que-in-plan";
			public static final String name = "入库计划数据同步错误通知";
			public static final String exchange = "wms-error-msg-que-in-plan-exchange";
			public static final String routing_key = "wms-error-msg-que-in-plan.#";
		}

		/**
		 * 入库单数据同步错误通知
		 *
		 * @author zxh
		 * @date 2019年 10月12日 23:34:54
		 */
		public class SYNC_ERROR_MSG_QUEUE_IN {
			public static final String queueCode = "wms-error-msg-que-in";
			public static final String name = "入库计划数据同步错误通知";
			public static final String exchange = "wms-error-msg-que-in-exchange";
			public static final String routing_key = "wms-error-msg-que-in.#";
		}

		/**
		 * 出库计划数据同步错误通知
		 *
		 * @author zxh
		 * @date 2019年 10月12日 23:34:54
		 */
		public class SYNC_ERROR_MSG_QUEUE_OUT_PLAN {
			public static final String queueCode = "wms-error-msg-que-out-plan";
			public static final String name = "入库计划数据同步错误通知";
			public static final String exchange = "wms-error-msg-que-out-plan-exchange";
			public static final String routing_key = "wms-error-msg-que-out-plan.#";
		}

		/**
		 * 出库单数据同步错误通知
		 *
		 * @author zxh
		 * @date 2019年 10月12日 23:34:54
		 */
		public class SYNC_ERROR_MSG_QUEUE_OUT {
			public static final String queueCode = "wms-error-msg-que-out";
			public static final String name = "入库计划数据同步错误通知";
			public static final String exchange = "wms-error-msg-que-out-exchange";
			public static final String routing_key = "wms-error-msg-que-out.#";
		}

		/**
		 * 监管任务数据同步错误通知
		 *
		 * @author zxh
		 * @date 2019年 10月12日 23:34:54
		 */
		public class SYNC_ERROR_MSG_QUEUE_MONITOR {
			public static final String queueCode = "wms-error-msg-que-monitor";
			public static final String name = "监管任务数据同步错误通知";
			public static final String exchange = "wms-error-msg-que-monitor-exchange";
			public static final String routing_key = "wms-error-msg-que-monitor.#";
		}

		/**
		 * 业务中台同步队列(所有的)
		 *
		 * @author zxh
		 * @date 2019年 10月12日 23:34:54
		 */
		public class SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MSG_QUEUE {
			public static final String queueCode = "scm_sync_business_platform_all_in_one";
			public static final String name = "业务中台同步队列(所有的)";
			public static final String exchange = "scm_sync_business_platform_all_in_one_exchange";
			public static final String routing_key = "scm_sync_business_platform_all_in_one.#";
		}

		/**
		 * test
		 *
		 * @author zxh
		 * @date 2019年 10月12日 23:34:54
		 */
		public class SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MSG_QUEUE_TEST {
			public static final String queueCode = "scm_sync_business_platform_all_in_one_test";
			public static final String name = "业务中台同步队列(所有的)";
			public static final String exchange = "scm_sync_business_platform_all_in_one_exchange_test";
			public static final String routing_key = "scm_sync_business_platform_all_in_one_test.#";
		}

		/** 操作日志队列-数据更新前更新后 */
		public class LOG_DATA_CHANGE_QUEUE {
			public static final String queueCode = "scm_data_change_log";
			public static final String name = "操作日志队列-数据更新前更新后";
			public static final String exchange = "scm_data_change_log";
			public static final String routing_key = "scm_data_change_log.#";
//			public static final String routing_key_data = "scm_data_change_log.#";
		}

		/** 系统日志 */
		public class LOG_PC_SYSTEM_QUEUE {
			public static final String queueCode = "scm_pc_system_log";
			public static final String name = "pc端日志同步";
			public static final String exchange = "scm_pc_system_log";
			public static final String routing_key = "scm_pc_system_log.#";
		}

		/** 导入日志 */
		public class LOG_IMPORT_QUEUE {
			public static final String queueCode = "scm_import_log";
			public static final String name = "pc端导入同步";
			public static final String exchange = "scm_import_log";
			public static final String routing_key = "scm_import_log.#";
		}

		/** api日志 */
		public class LOG_API_QUEUE {
			public static final String queueCode = "scm_api_log";
			public static final String name = "api日志同步";
			public static final String exchange = "scm_api_log";
			public static final String routing_key = "scm_api_log.#";
		}

		/** app日志 */
		public class LOG_APP_QUEUE {
			public static final String queueCode = "scm_app_log";
			public static final String name = "app日志同步";
			public static final String exchange = "scm_app_log";
			public static final String routing_key = "scm_app_log.#";
		}

		/** quartz日志 */
		public class LOG_QUARTZ_QUEUE {
			public static final String queueCode = "scm_quartz_log";
			public static final String name = "调度日志同步";
			public static final String exchange = "scm_quartz_log";
			public static final String routing_key = "scm_quartz_log.#";
		}

		/** 监管任务队列 */
		public class MONITOR_BACKUP_QUEUE {
			public static final String queueCode = "scm_monitor_backup1";
			public static final String name = "监管任务备份";
			public static final String exchange = "scm_monitor_backup1";
			public static final String routing_key = "scm_monitor_backup1.#";
		}

		/** 监管任务队列 */
		public class MONITOR_RENEW_QUEUE {
			public static final String queueCode = "scm_monitor_renew";
			public static final String name = "监管任务备份";
			public static final String exchange = "scm_monitor_renew";
			public static final String routing_key = "scm_monitor_renew.#";
		}

		/** 监管任务队列V2 */
		public class MONITOR_BACKUP_QUEUE_V2 {
			public static final String queueCode = "scm_monitor_backup_v2";
			public static final String name = "监管任务备份v2";
			public static final String exchange = "scm_monitor_backup_v2";
			public static final String routing_key = "scm_monitor_backup_v2.#";
		}

		/** 监管任务队列V2 */
		public class MONITOR_RENEW_QUEUE_V2 {
			public static final String queueCode = "scm_monitor_renew_v2";
			public static final String name = "监管任务备份v2";
			public static final String exchange = "scm_monitor_renew_v2";
			public static final String routing_key = "scm_monitor_renew_v2.#";
		}

		/** 文件备份队列 */
		public class FILE_BACKUP_QUEUE {
			public static final String queueCode = "scm_file_backup";
			public static final String name = "文件备份";
			public static final String exchange = "scm_file_backup";
			public static final String routing_key = "scm_file_backup.#";
		}

		/** 文件备份队列 */
		public class MQ_RECREATE_DAILY_INVENTORY_QUEUE {
			public static final String queueCode = "scm_recreate_daily_inventory";
			public static final String name = "重新生成每日库存";
			public static final String exchange = "scm_recreate_daily_inventory";
			public static final String routing_key = "scm_recreate_daily_inventory.#";
		}

		/** api日志 */
		public class MQ_SCHEDULE_CALC_QTY_QUEUE {
			public static final String queueCode = "scm_schedule_calc_qty";
			public static final String name = "重新计算物流订单数量";
			public static final String exchange = "scm_schedule_calc_qty";
			public static final String routing_key = "scm_schedule_calc_qty.#";
		}

	}
}
