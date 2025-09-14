package com.xinyirun.scm.ai.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import jakarta.validation.constraints.NotBlank;

import com.xinyirun.scm.ai.enums.LevelEnum;
import com.xinyirun.scm.ai.enums.PlatformEnum;
import com.xinyirun.scm.ai.utils.BdDateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BytedeskBaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -2236065320544063926L;

    @TableId(type = IdType.AUTO)
    private Long id;

	@NotBlank(message = "uid is required")
	@TableField("uuid")
	private String uid;
    
    @Version
    private int version;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private ZonedDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private ZonedDateTime updatedAt;

	@Builder.Default
	@TableField("is_deleted")
	private boolean deleted = false;

    private String userUid;

    @Builder.Default
    private String level = LevelEnum.ORGANIZATION.name();

    @Builder.Default
    private String platform = PlatformEnum.BYTEDESK.name();

    public String getCreatedAtString() {
        return BdDateUtils.formatDatetimeToString(createdAt);
    }

    public String getUpdatedAtString() {
        return BdDateUtils.formatDatetimeToString(updatedAt);
    }
}
