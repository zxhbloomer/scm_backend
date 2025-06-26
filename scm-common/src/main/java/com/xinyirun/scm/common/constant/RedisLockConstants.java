package com.xinyirun.scm.common.constant;

/**
 * Author:      Wqf
 * Description:
 * CreateTime : 2023/3/16 17:42
 */


public class RedisLockConstants {

    /**
     * 入库计划信息
     */
    public class SyncInPlan {
        public static final String LOCK_KEY = "sync_in_plan_lock_key";
//        public static final String REQUEST_ID = "123456";
    }

    /**
     * 入库单信息
     */
    public class SyncIn {
        public static final String LOCK_KEY = "sync_in_lock_key";
        public static final String REQUEST_ID = "sync_in_request_id";
    }

    /**
     * 出库计划
     */
    public class SyncOutPlan {
        public static final String LOCK_KEY = "sync_out_plan_lock_key";
        public static final String REQUEST_ID = "sync_out_plan_request_id";
    }

    /**
     * 出库单
     */
    public class SyncOut {
        public static final String LOCK_KEY = "sync_out_lock_key";
        public static final String REQUEST_ID = "sync_out_request_id";
    }

    /**
     * 出库单
     */
    public class SyncMonitor{
        public static final String LOCK_KEY = "sync_monitor_lock_key";
        public static final String REQUEST_ID = "sync_monitor_request_id";
    }

    /**
     * 收货单
     */
    public class SyncReceive {
        public static final String LOCK_KEY = "sync_receive_lock_key";
        public static final String REQUEST_ID = "sync_out_request_id";
    }

    /**
     * 提货单
     */
    public class SyncDelivery {
        public static final String LOCK_KEY = "sync_delivery_lock_key";
        public static final String REQUEST_ID = "sync_out_request_id";
    }
}
