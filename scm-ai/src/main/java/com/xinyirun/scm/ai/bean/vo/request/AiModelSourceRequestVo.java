package com.xinyirun.scm.ai.bean.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI模型源请求业务视图对象
 *
 * 用于AI模型源查询请求的参数传输，包含分页和过滤条件
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AiModelSourceRequestVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 组织id/个人id
     */
    @Schema(description = "组织id/个人id")
    private String owner;

    /**
     * 供应商名称
     */
    @Schema(description = "供应商名称")
    private String providerName;

    /**
     * 当前页码
     */
    @Min(value = 1, message = "当前页码必须大于0")
    @Schema(description = "当前页码")
    private int current = 1;

    /**
     * 每页显示条数
     */
    @Min(value = 5, message = "每页显示条数必须不小于5")
    @Max(value = 500, message = "每页显示条数不能大于500")
    @Schema(description = "每页显示条数")
    private int pageSize = 10;

    /**
     * 关键字搜索
     */
    @Schema(description = "关键字搜索")
    private String keyword;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段")
    private String sortField;

    /**
     * 排序方向（ASC/DESC）
     */
    @Schema(description = "排序方向（ASC/DESC）")
    private String sortOrder = "ASC";
}