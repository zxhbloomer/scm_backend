package com.xinyirun.scm.ai.controller.search;

import com.xinyirun.scm.ai.bean.vo.search.AiSearchReqVo;
import com.xinyirun.scm.ai.bean.vo.search.AiSearchRespVo;
import com.xinyirun.scm.ai.core.service.search.AiSearchService;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI搜索Controller
 *
 * <p>对齐AIDeepin: SearchController和SearchRecordController</p>
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Slf4j
@Tag(name = "AI搜索管理")
@RestController
@RequestMapping("/api/v1/ai/search")
@Validated
public class SearchController {

    @Resource
    private AiSearchService searchService;

    /**
     * 执行AI搜索
     * 对齐AIDeepin: SearchController.sseAsk()
     *
     * @param req 搜索请求
     * @return SSE流式响应
     */
    @Operation(summary = "执行AI搜索", description = "支持简洁搜索(从摘要生成)和详细搜索(RAG检索)")
    @PostMapping(value = "/process", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @SysLogAnnotion("执行AI搜索")
    public SseEmitter process(@RequestBody @Validated AiSearchReqVo req) {
        log.info("AI搜索请求,searchText:{},engineName:{},modelName:{},briefSearch:{},userId:{}",
                req.getSearchText(), req.getEngineName(), req.getModelName(),
                req.isBriefSearch(), req.getUserId());
        return searchService.search(req.isBriefSearch(), req.getSearchText(),
                                   req.getEngineName(), req.getModelName(), req.getUserId());
    }

    /**
     * 查询搜索历史记录(基于maxId的增量查询)
     * 对齐AIDeepin: SearchRecordController.list()
     *
     * @param maxId 最大ID(用于增量查询,默认0表示查询全部)
     * @param keyword 关键词搜索
     * @param userId 用户ID
     * @return 搜索历史响应
     */
    @Operation(summary = "查询搜索历史记录")
    @GetMapping(value = "/list")
    @SysLogAnnotion("查询搜索历史记录")
    public ResponseEntity<JsonResultAo<AiSearchRespVo>> list(
            @RequestParam(defaultValue = "0") Long maxId,
            @RequestParam(required = false) String keyword,
            @RequestParam Long userId) {
        log.info("查询搜索历史,userId:{},maxId:{},keyword:{}", userId, maxId, keyword);
        AiSearchRespVo result = searchService.listByMaxId(userId, maxId, keyword);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 删除搜索记录
     * 对齐AIDeepin: SearchRecordController.recordDel()
     *
     * @param uuid 搜索记录UUID
     * @return 删除结果
     */
    @Operation(summary = "删除搜索记录")
    @PostMapping("/del/{uuid}")
    @SysLogAnnotion("删除搜索记录")
    public ResponseEntity<JsonResultAo<Boolean>> delete(@PathVariable String uuid) {
        log.info("删除搜索记录,uuid:{}", uuid);
        boolean result = searchService.softDelete(uuid);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }
}

