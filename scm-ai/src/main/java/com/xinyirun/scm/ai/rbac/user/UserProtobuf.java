/**
 * 用户Protobuf传输对象，用于用户信息的序列化和传输
 */
package com.xinyirun.scm.ai.rbac.user;

import java.io.Serializable;

import com.alibaba.fastjson2.JSON;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 所有字段跟user.proto中字段一一对应
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserProtobuf implements Serializable {

    private static final long serialVersionUID = 1L;

	private String uid;

    private String nickname;

    private String avatar;

    @Builder.Default
    private String type = UserTypeEnum.USER.name();

    private String extra;

    public static UserProtobuf fromJson(String user) {
        if (user == null || user.trim().isEmpty()) {
            return null;
        }
        return JSON.parseObject(user, UserProtobuf.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public static UserProtobuf getSystemUser() {
        return UserProtobuf.builder()
                .uid("system")
                .nickname("系统通知")
                .avatar("/avatar/system.png")
                .type(UserTypeEnum.SYSTEM.name())
                .build();
    }

    public static UserProtobuf getFileAssistantUser() {
        return UserProtobuf.builder()
                .uid("file_assistant")
                .nickname("文件助手")
                .avatar("/avatar/file_assistant.png")
                .type(UserTypeEnum.SYSTEM.name())
                .build();
    }

    // 通过解析user字段中的type字段来判断 type=robot则为机器人，否则为访客
    public Boolean isRobot() {
        return UserTypeEnum.ROBOT.name().equalsIgnoreCase(getType());
    }

    // 通过解析user字段中的type字段来判断 type=visitor则为访客，否则为客服
    public Boolean isVisitor() {
        return UserTypeEnum.VISITOR.name().equalsIgnoreCase(getType());
    }

    public Boolean isUser() {
        return UserTypeEnum.USER.name().equalsIgnoreCase(getType());
    }

    public Boolean isMember() {
        return UserTypeEnum.MEMBER.name().equalsIgnoreCase(getType());
    }

    // 是否系统消息
    public Boolean isSystem() {
        return UserTypeEnum.SYSTEM.name().equalsIgnoreCase(getType());
    }

    // 是否客服消息
    public Boolean isAgent() {
        return UserTypeEnum.AGENT.name().equalsIgnoreCase(getType());
    }

}