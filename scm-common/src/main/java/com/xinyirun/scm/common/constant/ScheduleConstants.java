package com.xinyirun.scm.common.constant;

/**
 * 任务调度通用常量
 * 
 * @author
 */
public interface ScheduleConstants
{
    public static final String TASK_CLASS_NAME = "TASK_CLASS_NAME";

    /** 执行目标key */
    public static final String TASK_PROPERTIES = "TASK_PROPERTIES_JOB_ENTITY";

    /** 默认 */
    public static final String MISFIRE_DEFAULT = "0";

    /** 立即触发执行 */
    public static final String MISFIRE_IGNORE_MISFIRES = "1";

    /** 触发一次执行 */
    public static final String MISFIRE_FIRE_AND_PROCEED = "2";

    /** 不触发立即执行 */
    public static final String MISFIRE_DO_NOTHING = "3";

    public enum Status
    {
        /**
         * 正常
         */
        NORMAL(true),
        /**
         * 停止
         */
        PAUSE(false);


        private boolean value;

        private Status(boolean value)
        {
            this.value = value;
        }

        public boolean getValue()
        {
            return value;
        }

    }
}
