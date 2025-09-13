/**
 * 会话邀请状态枚举
 */
package com.xinyirun.scm.ai.thread.enums;

public enum ThreadInviteStatusEnum {
    NONE, // 未邀请
    INVITE_PENDING, // 邀请处理
    INVITE_ACCEPTED, // 接受邀请
    INVITE_REJECTED, // 拒绝邀请
    INVITE_TIMEOUT, // 邀请超时
    INVITE_CANCELED, // 取消邀请
}