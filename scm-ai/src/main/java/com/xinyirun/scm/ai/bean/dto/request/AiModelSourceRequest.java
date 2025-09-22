package com.xinyirun.scm.ai.bean.dto.request;

import com.xinyirun.scm.ai.bean.dto.sdk.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AiModelSourceRequest extends BasePageRequest {

    @Schema(description =  "组织id/个人id")
    private String owner;

    @Schema(description =  "供应商名称")
    private String providerName;

}