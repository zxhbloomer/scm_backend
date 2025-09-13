/**
 * 会话线程响应对象
 */
package com.xinyirun.scm.ai.thread.response;

import java.time.LocalDateTime;
import com.xinyirun.scm.ai.rbac.user.UserProtobuf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadResponse {

    private String uid;
    private String type;
    private String topic;
    private String status;
    private String content;
    private Boolean top;
    private Boolean unread;
    private Boolean mute;
    private Boolean hide;
    private Integer star;
    private Boolean fold;
    private String note;
    private UserProtobuf user;
    private String agent;
    private String robot;
    private String workgroup;
    private String channel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}