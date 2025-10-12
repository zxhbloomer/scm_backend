package com.xinyirun.scm.ai.controller.rag;

import com.xinyirun.scm.ai.core.service.GraphRetrievalService;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI知识图谱查询控制器
 *
 * @author SCM AI Team
 * @since 2025-10-03
 */
@Slf4j
@Tag(name = "AI知识图谱查询")
@RestController
@RequestMapping("/api/v1/ai/knowledge-base-graph")
@Validated
public class GraphQueryController {

    @Resource
    private GraphRetrievalService graphRetrievalService;

    /**
     * 获取文档图谱
     */
    @GetMapping("/list/{kbItemUuid}")
    @Operation(summary = "获取文档图谱")
    @SysLogAnnotion("获取文档图谱")
    public Map<String, Object> list(
            @PathVariable String kbItemUuid,
            @RequestParam(defaultValue = Long.MAX_VALUE + "") Long maxVertexId,
            @RequestParam(defaultValue = Long.MAX_VALUE + "") Long maxEdgeId,
            @RequestParam(defaultValue = "-1") int limit) {

        Map<String, Object> result = graphRetrievalService.getGraphByKbItem(kbItemUuid, maxVertexId, maxEdgeId, limit);
        return result;
    }

    /**
     * 图谱顶点VO
     */
    @Data
    public static class GraphVertexVo {
        private Long id;
        private String name;
        private String type;
    }

    /**
     * 图谱边VO
     */
    @Data
    public static class GraphEdgeVo {
        private Long id;
        private Long sourceId;
        private Long targetId;
        private String relationship;
    }
}
