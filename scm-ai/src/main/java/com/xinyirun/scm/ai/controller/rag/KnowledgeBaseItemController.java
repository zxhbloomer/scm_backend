package com.xinyirun.scm.ai.controller.rag;

import com.xinyirun.scm.ai.bean.vo.rag.KbEmbeddingVo;
import com.xinyirun.scm.ai.core.service.elasticsearch.ElasticsearchQueryService;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库文档项查询控制器
 *
 * <p>功能说明：提供知识库文档的向量嵌入查询接口</p>
 *
 * @author SCM-AI重构团队
 * @since 2025-10-09
 */
@Slf4j
@Tag(name = "知识库文档项查询")
@RestController
@RequestMapping("/api/v1/ai/kb-item")
@Validated
public class KnowledgeBaseItemController {

    /**
     * 默认最大ID值，用于分页查询的初始值
     */
    private static final Long DEFAULT_MAX_ID = Long.MAX_VALUE;

    @Resource
    private ElasticsearchQueryService elasticsearchQueryService;

    /**
     * 查看文档的向量嵌入列表
     *
     * @param itemUuid 知识项UUID
     * @param currentPage 当前页码
     * @param pageSize 每页大小
     * @return 向量嵌入VO列表
     */
    @GetMapping("/embedding/list/{itemUuid}")
    @Operation(summary = "查看文档的向量嵌入列表")
    @SysLogAnnotion("查看文档的向量嵌入列表")
    public ResponseEntity<JsonResultAo<List<KbEmbeddingVo>>> listEmbeddings(
            @Parameter(description = "知识项UUID", required = true)
            @PathVariable @NotBlank String itemUuid,
            @Parameter(description = "当前页码")
            @RequestParam(defaultValue = "1") @NotNull @Min(1) Integer currentPage,
            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "20") @NotNull @Min(1) Integer pageSize) {

        List<KbEmbeddingVo> result = elasticsearchQueryService.listEmbeddings(itemUuid, currentPage, pageSize);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }
}
