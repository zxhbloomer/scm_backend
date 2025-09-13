/**
 * 会话线程Protobuf对象
 */
package com.xinyirun.scm.ai.thread.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadProtobuf {

    private String uid;
    private String topic;
    private String content;
    private String type;
    private String status;
    private String user;
    private String agent;
    private String robot;
    private String workgroup;
    private String channel;
    private String orgUid;
    private String userUid;
    private String member;
    private Boolean unread;
    private Boolean mute;
    private Boolean hide;
    private Boolean top;
    private Boolean autoClose;
    private Integer star;
    private String timestamp;
    private String createdAt;
    private String updatedAt;
}