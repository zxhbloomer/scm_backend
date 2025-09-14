package com.xinyirun.scm.ai.base;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.xinyirun.scm.ai.enums.LevelEnum;
import com.xinyirun.scm.ai.enums.PlatformEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@SuperBuilder
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String uid;
    protected String type;
    protected String content;
    protected String channel;
    protected String userUid;
    protected String orgUid;

    @Builder.Default
    private String level = LevelEnum.ORGANIZATION.name();

    @Builder.Default
    private String platform = PlatformEnum.BYTEDESK.name();

    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    protected String createdBy;
    protected String updatedBy;

    @Builder.Default
    protected Boolean deleted = false;

    protected Integer version;

    /**
     * 是否已删除
     */
    public Boolean isDeleted() {
        return this.deleted;
    }

    /**
     * 获取创建时间
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 获取更新时间
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 获取创建者
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * 获取更新者
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * 获取版本号
     */
    public Integer getVersion() {
        return version;
    }
}