package com.xinyirun.scm.ai.controller.rag;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseItemVo;
import com.xinyirun.scm.ai.core.service.DocumentProcessingService;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * AI文档管理控制器
 *
 * @author SCM AI Team
 * @since 2025-10-03
 */
@Slf4j
@Tag(name = "AI文档管理")
@RestController
@RequestMapping("/api/v1/ai/knowledge-base-item")
@Validated
public class DocumentController {

    @Resource
    private DocumentProcessingService documentProcessingService;

    /**
     * 创建或更新文档
     */
    @PostMapping("/saveOrUpdate")
    @Operation(summary = "创建或更新文档")
    @SysLogAnnotion("创建或更新文档")
    public ResponseEntity<JsonResultAo<AiKnowledgeBaseItemVo>> saveOrUpdate(
            @Valid @RequestBody AiKnowledgeBaseItemVo vo,
            @RequestParam(defaultValue = "false") Boolean indexAfterCreate,
            @RequestParam(defaultValue = "false") Boolean indexAfterEdit) {
        AiKnowledgeBaseItemVo result = documentProcessingService.saveOrUpdate(vo, indexAfterCreate, indexAfterEdit);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 搜索文档
     */
    @GetMapping("/search")
    @Operation(summary = "搜索文档")
    @SysLogAnnotion("搜索文档")
    public ResponseEntity<JsonResultAo<IPage<AiKnowledgeBaseItemVo>>> search(
            @RequestParam String kbUuid,
            @RequestParam(defaultValue = "") String keyword,
            @NotNull @Min(1) @RequestParam Integer currentPage,
            @NotNull @Min(10) @RequestParam Integer pageSize) {

        IPage<AiKnowledgeBaseItemVo> result = documentProcessingService.search(kbUuid, keyword, currentPage, pageSize);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 文档详情
     */
    @GetMapping("/info/{uuid}")
    @Operation(summary = "文档详情")
    @SysLogAnnotion("获取文档详情")
    public ResponseEntity<JsonResultAo<AiKnowledgeBaseItemVo>> info(@PathVariable String uuid) {
        AiKnowledgeBaseItemVo result = documentProcessingService.getByUuid(uuid);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 删除文档
     */
    @PostMapping("/del/{uuid}")
    @Operation(summary = "删除文档")
    @SysLogAnnotion("删除文档")
    public ResponseEntity<JsonResultAo<Boolean>> delete(@PathVariable String uuid) {
        boolean result = documentProcessingService.delete(uuid);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }
}
