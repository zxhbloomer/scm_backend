package com.xinyirun.scm.bean.app.ao.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zxh
 * @date 2019/8/30
 */
@Data
@Builder
@AllArgsConstructor
public class AppCheckResultAo implements Serializable {

    private static final long serialVersionUID = 3505396357464124154L;

    /**
     * check的区分
     */
    public static final String INSERT_CHECK_TYPE = "INSERT_CHECK_TYPE";
    public static final String UPDATE_CHECK_TYPE = "UPDATE_CHECK_TYPE";
    public static final String DELETE_CHECK_TYPE = "DELETE_CHECK_TYPE";
    public static final String SUBMIT_CHECK_TYPE = "SUBMIT_CHECK_TYPE";
    public static final String AUDIT_CHECK_TYPE = "AUDIT_CHECK_TYPE";
    public static final String OPERATE_CHECK_TYPE = "OPERATE_CHECK_TYPE";
    public static final String OUT_OPERATE_CHECK_TYPE = "OUT_OPERATE_CHECK_TYPE";
    public static final String CANCEL_CHECK_TYPE = "CANCEL_CHECK_TYPE";
    public static final String FINISH_CHECK_TYPE = "FINISH_CHECK_TYPE";
    public static final String CANCEL_INVENTORY_CHECK_TYPE = "CANCEL_INVENTORY_CHECK_TYPE";
    public static final String REJECT_CHECK_TYPE = "REJECT_CHECK_TYPE";
    // 删除复原
    public static final String UNDELETE_CHECK_TYPE = "UNDELETE_CHECK_TYPE";
    public static final String SELECT_CHECK_TYPE = "SELECT_CHECK_TYPE";
    public static final String COPY_INSERT_CHECK_TYPE = "COPY_INSERT_CHECK_TYPE";
    public static final String OTHER_CHECK_TYPE = "OTHER_CHECK_TYPE";

    /** 返回消息：返回的消息 */
    private String message;

    /** 是否成功[true:成功;false:失败]，默认失败 */
    private boolean success;

    /** 返回数据 */
    private Object data ;
}
