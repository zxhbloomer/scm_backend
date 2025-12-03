package com.xinyirun.scm.ai.controller.rag;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseQaVo;
import com.xinyirun.scm.ai.bean.vo.rag.QaRefEmbeddingVo;
import com.xinyirun.scm.ai.bean.vo.rag.RefGraphVo;
import com.xinyirun.scm.ai.bean.vo.request.QARecordRequestVo;
import com.xinyirun.scm.ai.bean.vo.response.ChatResponseVo;
import com.xinyirun.scm.ai.core.service.RagService;
import com.xinyirun.scm.ai.core.service.rag.AiKnowledgeBaseQaRefEmbeddingService;
import com.xinyirun.scm.ai.core.service.rag.AiKnowledgeBaseQaRefGraphService;
import com.xinyirun.scm.ai.core.service.rag.AiKnowledgeBaseQaService;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI知识库问答控制器
 *
 * @author SCM AI Team
 * @since 2025-10-06
 */
@Slf4j
@Tag(name = "AI知识库问答")
@RestController
@RequestMapping("/api/v1/ai/knowledge-base/qa")
@Validated
@RequiredArgsConstructor
public class KnowledgeBaseQAController {

    private final RagService ragService;
    private final AiKnowledgeBaseQaService aiKnowledgeBaseQaService;
    private final AiKnowledgeBaseQaRefEmbeddingService qaRefEmbeddingService;
    private final AiKnowledgeBaseQaRefGraphService aiKnowledgeBaseQaRefGraphService;

    /**
     * 创建知识库问答记录
     *
     * @param kbUuid 知识库UUID
     * @param req 问答请求
     * @return 问答记录VO（包含qa_uuid，用于后续调用/process接口）
     */
    @PostMapping("/add/{kbUuid}")
    @Operation(summary = "创建知识库问答记录")
    @SysLogAnnotion("创建知识库问答记录")
    public ResponseEntity<JsonResultAo<AiKnowledgeBaseQaVo>> add(
            @PathVariable String kbUuid,
            @RequestBody @Validated QARecordRequestVo req) {

        Long userId = SecurityUtil.getStaff_id();
        // 数据库级别多租户，不需要从JWT获取tenantId

        AiKnowledgeBaseQaVo result = aiKnowledgeBaseQaService.add(kbUuid, req, userId, null);

        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * SSE流式问答
     *
     * @param qaUuid 问答记录UUID（由/add接口创建）
     * @param maxResults 最大检索结果数（可选，默认3）
     * @param minScore 最小相似度分数（可选，默认0.3）
     * @return SSE流式响应（ChatResponseVo流）
     */
    @PostMapping(value = "/process/{qaUuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "知识库RAG问答（SSE流式）")
    @SysLogAnnotion("知识库RAG问答")
    @DS("#header.X-Tenant-ID")
    public Flux<ChatResponseVo> sseAsk(
            @PathVariable String qaUuid,
            @RequestParam(required = false) Integer maxResults,
            @RequestParam(required = false) Double minScore,
            HttpServletRequest request) {

        Long userId = SecurityUtil.getStaff_id();

        // 【多租户支持】从请求头中获取租户编码
        // 由于使用了异步响应式流，必须在主线程获取租户编码后传递给Service层
        String tenantCode = request.getHeader("X-Tenant-ID");

        // 验证问答记录是否存在
        var qaRecord = aiKnowledgeBaseQaService.getByQaUuid(qaUuid);
        if (qaRecord == null) {
            return Flux.error(new RuntimeException("问答记录不存在: " + qaUuid));
        }

        return ragService.sseAsk(qaUuid, userId, tenantCode, maxResults, minScore);
    }

    /**
     * 搜索问答记录
     *
     * @param kbUuid 知识库UUID
     * @param keyword 搜索关键词（模糊匹配question字段，可选）
     * @param currentPage 当前页码（从1开始）
     * @param pageSize 每页数量（最小10）
     * @return 分页的问答记录列表
     */
    @GetMapping("/search")
    @Operation(summary = "搜索问答记录")
    @SysLogAnnotion("搜索问答记录")
    public ResponseEntity<JsonResultAo<IPage<AiKnowledgeBaseQaVo>>> search(
            @RequestParam String kbUuid,
            @RequestParam(required = false) String keyword,
            @NotNull @Min(1) @RequestParam Integer currentPage,
            @NotNull @Min(10) @RequestParam Integer pageSize) {

        Long userId = SecurityUtil.getStaff_id();

        IPage<AiKnowledgeBaseQaVo> result = aiKnowledgeBaseQaService.search(
                kbUuid, keyword, userId, currentPage, pageSize
        );

        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 删除问答记录
     *
     * @param uuid 问答记录UUID
     * @return 是否成功
     */
    @PostMapping("/del/{uuid}")
    @Operation(summary = "删除问答记录")
    @SysLogAnnotion("删除问答记录")
    public ResponseEntity<JsonResultAo<Boolean>> del(@PathVariable String uuid) {
        Long userId = SecurityUtil.getStaff_id();

        boolean result = aiKnowledgeBaseQaService.delete(uuid, userId);

        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 查询问答的向量引用
     *
     * @param uuid 问答记录UUID
     * @return 向量引用列表（包含embedding_id、score、文本内容，直接从MySQL返回）
     */
    @GetMapping("/embedding-ref/{uuid}")
    @Operation(summary = "查询问答的向量引用")
    @SysLogAnnotion("查询问答向量引用")
    public ResponseEntity<JsonResultAo<List<QaRefEmbeddingVo>>> embeddingRef(
            @PathVariable String uuid) {

        List<QaRefEmbeddingVo> result = qaRefEmbeddingService.listRefEmbeddingsForDisplay(uuid);

        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 查询问答的图谱引用
     *
     * @param uuid 问答记录UUID
     * @return 图谱引用（包含vertices实体列表、edges关系列表、entitiesFromQuestion从问题提取的实体）
     */
    @GetMapping("/graph-ref/{uuid}")
    @Operation(summary = "查询问答的图谱引用")
    @SysLogAnnotion("查询问答图谱引用")
    public ResponseEntity<JsonResultAo<RefGraphVo>> graphRef(@PathVariable String uuid) {
        RefGraphVo result = aiKnowledgeBaseQaRefGraphService.getByQaUuid(uuid);

        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 清空当前用户的所有问答记录
     *
     * @return 是否成功
     */
    @PostMapping("/clear")
    @Operation(summary = "清空当前用户的所有问答记录")
    @SysLogAnnotion("清空问答记录")
    public ResponseEntity<JsonResultAo<Boolean>> clear() {
        Long userId = SecurityUtil.getStaff_id();

        boolean result = aiKnowledgeBaseQaService.clearByCurrentUser(userId);

        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }
}
