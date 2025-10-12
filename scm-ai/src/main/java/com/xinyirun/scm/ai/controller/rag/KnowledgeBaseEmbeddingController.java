package com.xinyirun.scm.ai.controller.rag;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.vo.rag.KbItemEmbeddingVo;
import com.xinyirun.scm.ai.core.service.rag.KnowledgeBaseEmbeddingService;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * AI知识库嵌入向量控制器
 *
 * @author SCM AI Team
 * @since 2025-10-12
 */
@Slf4j
@Tag(name = "AI知识库嵌入向量")
@RestController
@RequestMapping("/api/v1/ai/knowledge-base-embedding")
@Validated
@RequiredArgsConstructor
public class KnowledgeBaseEmbeddingController {

    private final KnowledgeBaseEmbeddingService embeddingService;

    /**
     * 查询知识项的嵌入向量列表（分页）
     *
     * @param kbItemUuid 知识项UUID
     * @param currentPage 当前页码（从1开始）
     * @param pageSize 每页大小
     * @return 嵌入向量分页数据
     */
    @GetMapping("/list/{kbItemUuid}")
    @Operation(summary = "查询嵌入向量列表", description = "根据知识项UUID分页查询该文档的所有嵌入向量切片")
    @SysLogAnnotion("查询嵌入向量列表")
    public ResponseEntity<JsonResultAo<Page<KbItemEmbeddingVo>>> list(
            @Parameter(description = "知识项UUID", required = true)
            @PathVariable @NotBlank String kbItemUuid,

            @Parameter(description = "当前页码（从1开始）", required = true)
            @RequestParam @NotNull @Min(1) Integer currentPage,

            @Parameter(description = "每页大小", required = true)
            @RequestParam @NotNull @Min(1) Integer pageSize) {

        log.info("接收查询嵌入向量列表请求: kbItemUuid={}, currentPage={}, pageSize={}",
                 kbItemUuid, currentPage, pageSize);

        Page<KbItemEmbeddingVo> result = embeddingService.listByItemUuid(kbItemUuid, currentPage, pageSize);

        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }
}
