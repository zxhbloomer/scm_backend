package com.xinyirun.scm.ai.controller.rag;

import com.xinyirun.scm.ai.bean.vo.rag.KbEmbeddingVo;
import com.xinyirun.scm.ai.bean.vo.rag.KbGraphVo;
import com.xinyirun.scm.ai.core.service.milvus.MilvusVectorRetrievalService;
import com.xinyirun.scm.ai.core.service.neo4j.Neo4jQueryService;
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
 * <p>功能说明：提供知识库文档的向量嵌入和图谱数据查询接口</p>
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
    private MilvusVectorRetrievalService milvusVectorRetrievalService;

    @Resource
    private Neo4jQueryService neo4jQueryService;

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

        List<KbEmbeddingVo> result = milvusVectorRetrievalService.listEmbeddings(itemUuid, currentPage, pageSize);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 查看文档的图谱数据
     *
     * @param itemUuid 知识项UUID
     * @param maxVertexId 最大顶点ID（用于分页）
     * @param maxEdgeId 最大边ID（用于分页）
     * @param limit 返回数量限制
     * @return 图谱数据VO
     */
    @GetMapping("/graph/data/{itemUuid}")
    @Operation(summary = "查看文档的图谱数据")
    @SysLogAnnotion("查看文档的图谱数据")
    public ResponseEntity<JsonResultAo<KbGraphVo>> getGraphData(
            @Parameter(description = "知识项UUID", required = true)
            @PathVariable @NotBlank String itemUuid,
            @Parameter(description = "最大顶点ID（用于分页）")
            @RequestParam(defaultValue = "9223372036854775807") Long maxVertexId,
            @Parameter(description = "最大边ID（用于分页）")
            @RequestParam(defaultValue = "9223372036854775807") Long maxEdgeId,
            @Parameter(description = "返回数量限制")
            @RequestParam(defaultValue = "100") @NotNull @Min(1) Integer limit) {

        KbGraphVo result = neo4jQueryService.getGraphData(itemUuid, maxVertexId, maxEdgeId, limit);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }
}
