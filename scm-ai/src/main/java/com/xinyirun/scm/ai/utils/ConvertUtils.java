/**
 * 转换工具类，用于对象之间的转换
 */
package com.xinyirun.scm.ai.utils;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.ai.rbac.user.UserProtobuf;
import com.xinyirun.scm.ai.rbac.user.UserEntity;
import com.xinyirun.scm.ai.thread.entity.ThreadEntity;
import com.xinyirun.scm.ai.thread.entity.ThreadProtobuf;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConvertUtils {

    public static UserProtobuf convertToUserProtobuf(UserEntity user) {
        if (user == null) {
            return null;
        }
        return UserProtobuf.builder()
            .uid(user.getUid())
            .nickname(user.getNickname())
            .avatar(user.getAvatar())
            .build();
    }

    public static String convertToUserProtobufString(UserEntity user) {
        if (user == null) {
            return "{}";
        }
        UserProtobuf userProtobuf = convertToUserProtobuf(user);
        return JSON.toJSONString(userProtobuf);
    }

    public static ThreadProtobuf convertToThreadProtobuf(ThreadEntity thread) {
        if (thread == null) {
            return null;
        }
        return ThreadProtobuf.builder()
            .uid(thread.getUid())
            .topic(thread.getTopic())
            .content(thread.getContent())
            .type(thread.getType())
            .status(thread.getStatus())
            .user(thread.getUser())
            .agent(thread.getAgent())
            .robot(thread.getRobot())
            .workgroup(thread.getWorkgroup())
            .channel(thread.getChannel())
            .orgUid(null)  // ThreadEntity中没有orgUid字段
            .userUid(thread.getUserUid())
            .member(null)   // ThreadEntity中没有member字段
            .unread(thread.getUnread())
            .mute(thread.getMute())
            .hide(thread.getHide())
            .top(thread.getTop())
            .autoClose(thread.getAutoClose())
            .star(thread.getStar())
            .timestamp(null)  // ThreadEntity中没有timestamp字段
            .createdAt(thread.getCreatedAt() != null ? thread.getCreatedAt().toString() : null)
            .updatedAt(thread.getUpdatedAt() != null ? thread.getUpdatedAt().toString() : null)
            .build();
    }
}
