package com.xinyirun.scm.ai.thread.request;

import java.util.ArrayList;
import java.util.List;
import com.xinyirun.scm.ai.base.BaseRequest;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ThreadRequest extends BaseRequest {
    
    private String topic;
    
    @Builder.Default
    private List<String> topicList = new ArrayList<>();

    private String status;

    @Builder.Default
    private String transferStatus = "NONE";

    @Builder.Default
    private String inviteStatus = "NONE";

    @Builder.Default
    private Boolean top = false;

    @Builder.Default
    private Boolean unread = false;

    @Builder.Default
    private Boolean mute = false;

    @Builder.Default
    private Boolean hide = false;

    @Builder.Default
    private Integer star = 0;

    @Builder.Default
    private Boolean fold = false;

    @Builder.Default
    private Boolean autoClose = false;

    private String note;

    @Builder.Default
    private Boolean offline = false;

    @Builder.Default
    private List<String> tagList = new ArrayList<>();

    private String transfer;

    @Builder.Default
    private List<String> inviteUids = new ArrayList<>();

    @Builder.Default
    private List<String> monitorUids = new ArrayList<>();

    @Builder.Default
    private List<String> assistantUids = new ArrayList<>();

    @Builder.Default
    private List<String> ticketorUids = new ArrayList<>();

    @Builder.Default
    private List<String> memberUids = new ArrayList<>();

    private String componentType;

    @Builder.Default
    private Boolean forceNew = false;

    @Builder.Default
    private Boolean mergeByTopic = false;

    private String userNickname;
    private String agentNickname;
    private String robotNickname;
    private String workgroupNickname;
    private String ownerNickname;
    private String ownerUid;
    private String agent;
    private String robot;
    private String workgroup;
    private String processInstanceId;
    private String processEntityUid;
    private Boolean unsubscribe;
    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}