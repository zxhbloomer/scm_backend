package com.xinyirun.scm.ai.base;

import java.io.Serializable;
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
public abstract class BaseRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String uid;
    protected int pageNumber;

    @Builder.Default
    protected int pageSize = 10;

    protected String type;
    protected String content;
    protected String channel;
    protected String userUid;
    protected String orgUid;

    @Builder.Default
    private String level = LevelEnum.ORGANIZATION.name();

    @Builder.Default
    private String platform = PlatformEnum.BYTEDESK.name();

    @Builder.Default
    private Boolean superUser = false;

    @Builder.Default
    private Boolean exportAll = false;

    @Builder.Default
    private String sortBy = "updatedAt";

    @Builder.Default
    private String sortDirection = "desc";

    private String searchText;

    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Object> getPage() {
        return new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNumber, pageSize);
    }
}