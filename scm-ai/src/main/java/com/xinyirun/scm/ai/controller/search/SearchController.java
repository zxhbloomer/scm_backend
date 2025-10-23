package com.xinyirun.scm.ai.controller.search;

import com.xinyirun.scm.ai.core.service.search.AiSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI搜索Controller
 *
 * <p>基于AIDeepin SearchController实现</p>
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Slf4j
@Tag(name = "AI搜索管理")
@RestController
@RequestMapping("/ai/search")
@Validated
public class SearchController {

    @Resource
    private AiSearchService searchService;

    /**
     * 执行AI搜索
     *
     * @param searchText 搜索文本
     * @param engineName 搜索引擎名称(google/bing/baidu)
     * @param modelName 模型名称
     * @param briefSearch 是否简洁搜索(true-简洁搜索,false-详细搜索)
     * @param userId 用户ID
     * @return SSE流式响应
     */
    @Operation(summary = "执行AI搜索", description = "支持简洁搜索(从摘要生成)和详细搜索(RAG检索)")
    @PostMapping(value = "/process", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter process(
            @RequestParam @NotBlank String searchText,
            @RequestParam(defaultValue = "google") String engineName,
            @RequestParam @NotBlank String modelName,
            @RequestParam(defaultValue = "true") Boolean briefSearch,
            @RequestParam @NotNull Long userId) {
        log.info("AI搜索请求,searchText:{},engineName:{},modelName:{},briefSearch:{},userId:{}",
                searchText, engineName, modelName, briefSearch, userId);
        return searchService.search(briefSearch, searchText, engineName, modelName, userId);
    }
}
