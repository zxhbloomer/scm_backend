package com.xinyirun.scm.ai.bean.vo.request;

import com.xinyirun.scm.ai.bean.vo.model.AiModelSourceVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * AI模型源创建名称业务视图对象
 *
 * 用于AI模型源创建时包含创建人名称的传输对象
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Accessors(chain = true)
public class AiModelSourceCreateNameVo extends AiModelSourceVo {

    /**
     * 创建人名称
     */
    @Schema(description = "创建人名称")
    private String createUserName;
}