/**
 * 会话转接状态枚举
 */
package com.xinyirun.scm.ai.thread.enums;

public enum ThreadTransferStatusEnum {
    NONE, // 无转接
    TRANSFER_PENDING, // 转接待处理
    TRANSFER_ACCEPTED, // 接受转接
    TRANSFER_REJECTED, // 拒绝转接
    TRANSFER_TIMEOUT, // 转接超时
    TRANSFER_CANCELED, // 取消转接
}
