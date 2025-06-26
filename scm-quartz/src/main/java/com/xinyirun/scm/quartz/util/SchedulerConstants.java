package com.xinyirun.scm.quartz.util;

import com.xinyirun.scm.common.constant.DictConstant;

/**
 * 关于定时任务的常量类
 */
public class SchedulerConstants {

    /**
     * 创建定时任务：数据变更日志中，没找到order_code，发起定时任务开始找。
     */
    public static class DATA_CHANGE_FIND_ORDER_CODE {
        public static final String JOB_NAME = "数据变更日志中，没找到order_code，发起定时任务开始找";
        public static final String JOB_GROUP_TYPE = DictConstant.DICT_SYS_JOB_GROUP_TYPE_DATA_CHANGE;
        public static final String JOB_DESC = "创建定时任务：数据变更日志中，没找到order_code，发起定时任务开始找。";
        // 0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行
        public static final String MISFIRE_POLICY0 = "0";
        public static final String MISFIRE_POLICY1 = "1";
        public static final String MISFIRE_POLICY2 = "2";
        public static final String MISFIRE_POLICY3 = "3";
        public static final Boolean CONCURRENT = false;
        public static final Boolean IS_CRON = false;
        public static final Boolean IS_DEL = false;
        public static final Boolean IS_EFFECTED = true;
        public static final Integer NEXT_FIRE_SECONDS = 10;
        public static final String CLASS_NAME = "com.xinyirun.scm.mongodb.serviceimpl.log.datachange.LogChangeMongoServiceImpl";
        public static final String METHOD_NAME = "findOrderCode";
        public static final String PARAM_CLASS = "com.xinyirun.scm.bean.system.vo.sys.log.datachange.SDataChangeLogFindOrderCodeVo";
    }

    /**
     * 每日库存差量
     */
    public static class DAILY_INVENTORY_DIFF {
        public static final String JOB_NAME = "每日库存差量";
        public static final String JOB_GROUP_TYPE = DictConstant.DICT_SYS_JOB_GROUP_TYPE_DAILY_INVENTORY_DIFF;
        public static final String JOB_DESC = "每日库存差量计算，在入库、出库、调整业务后自定执行，计算每日库存";
        // 0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行
        public static final String MISFIRE_POLICY = "2";
        public static final Boolean CONCURRENT = false;
        public static final Boolean IS_CRON = false;
        public static final Boolean IS_DEL = false;
        public static final Boolean IS_EFFECTED = true;
        public static final Integer NEXT_FIRE_SECONDS = 10;
        public static final String CLASS_NAME = "com.xinyirun.scm.core.system.serviceimpl.sys.schedule.v2.SBDailyInventoryV2ServiceImpl";
        public static final String METHOD_NAME = "reCreateDailyInventoryAll";
        public static final String PARAM_CLASS = "com.xinyirun.scm.bean.system.vo.business.inventory.BDailyInventoryVo";
    }

    /**
     * 物料转换
     */
    public static class MATERIAL_CONVERT {
        public static final String JOB_NAME = "物料转换";
        public static final String JOB_GROUP_TYPE = DictConstant.DICT_SYS_JOB_GROUP_TYPE_MATERIAL_CONVERT;
        public static final String JOB_DESC = "物料转换";
        // 0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行
        public static final String MISFIRE_POLICY0 = "0";
        public static final String MISFIRE_POLICY1 = "1";
        public static final String MISFIRE_POLICY2 = "2";
        public static final String MISFIRE_POLICY3 = "3";
        public static final Boolean CONCURRENT = false;
        public static final Boolean IS_CRON_TRUE = true;
        public static final Boolean IS_CRON_FALSE = false;
        public static final Boolean IS_DEL = false;
        public static final Boolean IS_EFFECTED = true;
        public static final Integer NEXT_FIRE_SECONDS = 10;
        public static final String CLASS_NAME = "com.xinyirun.scm.core.system.serviceimpl.sys.schedule.v2.SBMaterialConvertV2ServiceImpl";
        public static final String METHOD_NAME = "materialConvert";
        public static final String PARAM_CLASS = "com.xinyirun.scm.bean.system.vo.business.materialconvert.BMaterialConvertVo";
    }
}
