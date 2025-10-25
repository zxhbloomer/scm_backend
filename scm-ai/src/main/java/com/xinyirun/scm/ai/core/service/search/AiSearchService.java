package com.xinyirun.scm.ai.core.service.search;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.xinyirun.scm.ai.bean.entity.search.AiSearchRecordEntity;
import com.xinyirun.scm.ai.bean.vo.search.AiSearchRecordVo;
import com.xinyirun.scm.ai.bean.vo.search.AiSearchRespVo;
import com.xinyirun.scm.ai.core.mapper.search.AiSearchRecordMapper;
import com.xinyirun.scm.common.utils.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.ai.document.Document;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI搜索服务
 *
 * <p>基于AIDeepin SearchService实现,提供两种搜索模式:</p>
 * <ul>
 *   <li>简洁搜索(briefSearch): 从搜索引擎摘要生成回答</li>
 *   <li>详细搜索(detailSearch): 抓取完整网页,向量化后RAG检索生成回答</li>
 * </ul>
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Slf4j
@Service
public class AiSearchService extends ServiceImpl<AiSearchRecordMapper, AiSearchRecordEntity> {

    private static final int MAX_SEARCH_RESULTS = 5;
    private static final int TIMEOUT_MS = 10000;

    /**
     * 搜索入口方法
     *
     * @param isBriefSearch 是否简洁搜索
     * @param searchText 搜索文本
     * @param engineName 搜索引擎名称
     * @param modelName 模型名称
     * @param userId 用户ID
     * @return SSE Emitter
     */
    public SseEmitter search(boolean isBriefSearch, String searchText, String engineName,
                             String modelName, Long userId) {
        SseEmitter sseEmitter = new SseEmitter(60 * 60 * 1000L);

        // TODO: 检查用户并发限制和配额
        // if (!checkUserQuota(userId)) {
        //     sendErrorAndComplete(sseEmitter, "超出搜索配额限制");
        //     return sseEmitter;
        // }

        // 异步执行搜索
        asyncSearch(userId, sseEmitter, isBriefSearch, searchText, engineName, modelName);
        return sseEmitter;
    }

    /**
     * 异步搜索
     *
     * @param userId 用户ID
     * @param sseEmitter SSE Emitter
     * @param isBriefSearch 是否简洁搜索
     * @param searchText 搜索文本
     * @param engineName 搜索引擎名称
     * @param modelName 模型名称
     */
    @Async
    public void asyncSearch(Long userId, SseEmitter sseEmitter, boolean isBriefSearch,
                            String searchText, String engineName, String modelName) {
        log.info("AI搜索开始,userId:{},searchText:{},engineName:{},isBrief:{}",
                userId, searchText, engineName, isBriefSearch);

        try {
            // TODO: 调用搜索引擎服务获取结果
            // SearchReturn searchResult = SearchEngineServiceContext.getService(engineName).search(searchText, "", "", MAX_SEARCH_RESULTS);
            // List<SearchReturnWebPage> resultItems = searchResult.getItems();

            // 临时模拟搜索结果
            List<SearchReturnWebPage> resultItems = mockSearchResults(searchText);

            if (resultItems == null || resultItems.isEmpty()) {
                sendErrorAndComplete(sseEmitter, "搜索引擎未返回结果");
                return;
            }

            // 根据搜索模式选择不同的处理方式
            if (isBriefSearch) {
                briefSearch(userId, searchText, modelName, resultItems, sseEmitter);
            } else {
                detailSearch(userId, searchText, engineName, modelName, resultItems, sseEmitter);
            }

        } catch (Exception e) {
            log.error("AI搜索失败", e);
            sendErrorAndComplete(sseEmitter, "搜索失败: " + e.getMessage());
        }
    }

    /**
     * 简洁搜索 - 从搜索引擎摘要生成回答
     *
     * @param userId 用户ID
     * @param searchText 搜索文本
     * @param modelName 模型名称
     * @param resultItems 搜索结果
     * @param sseEmitter SSE Emitter
     */
    public void briefSearch(Long userId, String searchText, String modelName,
                           List<SearchReturnWebPage> resultItems, SseEmitter sseEmitter) {
        log.info("执行简洁搜索,userId:{},searchText:{}", userId, searchText);

        // 创建搜索记录
        String searchUuid = UuidUtil.createShort();
        AiSearchRecordEntity searchRecord = new AiSearchRecordEntity();
        searchRecord.setSearchUuid(searchUuid);
        searchRecord.setUserId(userId);
        searchRecord.setQuestion(searchText);
        baseMapper.insert(searchRecord);

        // 从搜索结果中提取摘要文本
        StringBuilder contextBuilder = new StringBuilder();
        Map<String, Object> searchEngineResponse = new HashMap<>();
        List<Map<String, String>> items = new ArrayList<>();

        for (SearchReturnWebPage item : resultItems) {
            contextBuilder.append(item.getSnippet()).append("\n\n");
            Map<String, String> itemMap = new HashMap<>();
            itemMap.put("title", item.getTitle());
            itemMap.put("link", item.getLink());
            itemMap.put("snippet", item.getSnippet());
            items.add(itemMap);
        }
        searchEngineResponse.put("items", items);

        // 构建提示词
        String prompt = createSearchPrompt(searchText, contextBuilder.toString(), "");

        // TODO: 调用LLM流式生成回答
        // ChatLanguageModel chatModel = getChatModel(modelName);
        // streamingChat(chatModel, prompt, sseEmitter, searchRecord);

        // 临时模拟流式响应
        mockStreamingResponse(sseEmitter, searchRecord, searchText, searchEngineResponse, prompt);
    }

    /**
     * 详细搜索 - 抓取完整网页,向量化后RAG检索生成回答
     *
     * @param userId 用户ID
     * @param searchText 搜索文本
     * @param engineName 搜索引擎名称
     * @param modelName 模型名称
     * @param resultItems 搜索结果
     * @param sseEmitter SSE Emitter
     */
    public void detailSearch(Long userId, String searchText, String engineName, String modelName,
                            List<SearchReturnWebPage> resultItems, SseEmitter sseEmitter) {
        log.info("执行详细搜索,userId:{},searchText:{}", userId, searchText);

        // 创建搜索记录
        String searchUuid = UuidUtil.createShort();
        AiSearchRecordEntity searchRecord = new AiSearchRecordEntity();
        searchRecord.setSearchUuid(searchUuid);
        searchRecord.setUserId(userId);
        searchRecord.setQuestion(searchText);
        baseMapper.insert(searchRecord);

        List<Document> documents = new ArrayList<>();
        Map<String, Object> searchEngineResponse = new HashMap<>();
        List<Map<String, String>> items = new ArrayList<>();

        // 抓取每个搜索结果的完整网页内容
        for (int i = 0; i < Math.min(resultItems.size(), MAX_SEARCH_RESULTS); i++) {
            SearchReturnWebPage item = resultItems.get(i);
            try {
                log.info("抓取网页内容: {}", item.getLink());
                String content = getContentFromRemote(item);

                if (content != null && !content.isBlank()) {
                    // 使用Spring AI的Document替代LangChain4j
                    Map<String, Object> metadata = Map.of(
                            "title", item.getTitle(),
                            "url", item.getLink(),
                            "index", String.valueOf(i)
                    );
                    documents.add(new Document(content, metadata));

                    Map<String, String> itemMap = new HashMap<>();
                    itemMap.put("title", item.getTitle());
                    itemMap.put("link", item.getLink());
                    itemMap.put("snippet", item.getSnippet());
                    items.add(itemMap);
                }
            } catch (Exception e) {
                log.error("抓取网页失败: {}", item.getLink(), e);
            }
        }

        searchEngineResponse.put("items", items);

        if (documents.isEmpty()) {
            sendErrorAndComplete(sseEmitter, "未能抓取到有效网页内容");
            return;
        }

        // TODO: 将文档向量化并存储到Elasticsearch
        // for (Document document : documents) {
        //     searchRagService.ingest(document, 0, "", null);
        // }

        // TODO: 创建ContentRetriever并执行RAG检索
        // ContentRetriever contentRetriever = searchRagService.createRetriever(...);
        // compositeRAG.ragChat(List.of(contentRetriever), sseAskParams, callback);

        // 临时模拟流式响应
        String prompt = createSearchPrompt(searchText, "详细搜索内容摘要", "");
        mockStreamingResponse(sseEmitter, searchRecord, searchText, searchEngineResponse, prompt);
    }

    /**
     * 从远程获取网页内容
     *
     * @param webPage 搜索结果网页
     * @return 网页文本内容
     */
    private String getContentFromRemote(SearchReturnWebPage webPage) {
        try {
            org.jsoup.nodes.Document doc = Jsoup.connect(webPage.getLink())
                    .timeout(TIMEOUT_MS)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get();

            // 移除脚本和样式标签
            doc.select("script, style").remove();

            // 提取主要内容
            Element body = doc.body();
            if (body == null) {
                return "";
            }

            // 优先提取article或main标签内容
            Elements articles = body.select("article, main");
            if (!articles.isEmpty()) {
                return articles.text();
            }

            // 否则提取body文本
            return body.text();

        } catch (IOException e) {
            log.error("抓取网页内容失败: {}", webPage.getLink(), e);
            return "";
        }
    }

    /**
     * 创建搜索提示词
     *
     * @param question 搜索问题
     * @param context 上下文内容
     * @param retrievalContext RAG检索上下文
     * @return 提示词
     */
    private String createSearchPrompt(String question, String context, String retrievalContext) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("请基于以下搜索结果回答用户的问题。\n\n");
        promptBuilder.append("用户问题: ").append(question).append("\n\n");
        promptBuilder.append("搜索结果:\n").append(context).append("\n\n");

        if (retrievalContext != null && !retrievalContext.isBlank()) {
            promptBuilder.append("检索上下文:\n").append(retrievalContext).append("\n\n");
        }

        promptBuilder.append("请用简洁、准确的语言回答,如果搜索结果无法回答问题,请说明。");
        return promptBuilder.toString();
    }

    /**
     * 模拟搜索结果
     */
    private List<SearchReturnWebPage> mockSearchResults(String searchText) {
        List<SearchReturnWebPage> results = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            SearchReturnWebPage page = new SearchReturnWebPage();
            page.setTitle("搜索结果 " + i + " - " + searchText);
            page.setLink("https://example.com/result" + i);
            page.setSnippet("这是关于 " + searchText + " 的搜索结果摘要 " + i);
            results.add(page);
        }
        return results;
    }

    /**
     * 模拟流式响应
     */
    private void mockStreamingResponse(SseEmitter sseEmitter, AiSearchRecordEntity searchRecord,
                                       String searchText, Map<String, Object> searchEngineResponse,
                                       String prompt) {
        try {
            String answer = "根据搜索结果,关于\"" + searchText + "\"的回答如下:\n\n这是一个模拟的搜索回答。实际实现需要调用LLM模型生成真实回答。";

            // 发送流式响应
            sseEmitter.send(SseEmitter.event()
                    .name("message")
                    .data(answer));

            // 更新搜索记录
            searchRecord.setSearchEngineResponse(searchEngineResponse);
            searchRecord.setPrompt(prompt);
            searchRecord.setAnswer(answer);
            searchRecord.setPromptTokens(prompt.length() / 4);
            searchRecord.setAnswerTokens(answer.length() / 4);
            searchRecord.setTotalTokens(searchRecord.getPromptTokens() + searchRecord.getAnswerTokens());
            baseMapper.updateById(searchRecord);

            sseEmitter.complete();
        } catch (IOException e) {
            log.error("发送SSE响应失败", e);
            sseEmitter.completeWithError(e);
        }
    }

    /**
     * 发送错误并完成SSE
     */
    private void sendErrorAndComplete(SseEmitter sseEmitter, String errorMsg) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("error")
                    .data(errorMsg));
            sseEmitter.complete();
        } catch (IOException e) {
            log.error("发送SSE错误失败", e);
            sseEmitter.completeWithError(e);
        }
    }

    /**
     * 基于maxId的增量查询搜索历史记录
     * 对齐AIDeepin: com.moyz.adi.common.service.AiSearchRecordService.listByMaxId(Long maxId, String keyword)
     *
     * <p>使用maxId作为锚点的增量查询方式，按ID降序返回记录</p>
     * <p>DEFAULT_PAGE_SIZE = 20</p>
     *
     * @param userId 用户ID (scm-ai特有,aideepin从ThreadContext获取)
     * @param maxId 最大ID锚点,0表示查询全部
     * @param keyword 关键词搜索(可选)
     * @return 搜索历史响应
     */
    public AiSearchRespVo listByMaxId(Long userId, Long maxId, String keyword) {
        log.info("增量查询搜索历史,userId:{},maxId:{},keyword:{}", userId, maxId, keyword);

        // 构建查询条件
        LambdaQueryWrapper<AiSearchRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiSearchRecordEntity::getUserId, userId);
        wrapper.eq(AiSearchRecordEntity::getIsDeleted, false);

        // 关键词搜索
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(AiSearchRecordEntity::getQuestion, keyword);
        }

        // maxId锚点查询(对应aideepin的BizPager.listByMaxId逻辑)
        if (maxId > 0) {
            wrapper.lt(AiSearchRecordEntity::getId, maxId);
        }
        wrapper.orderByDesc(AiSearchRecordEntity::getId);
        wrapper.last("LIMIT 20"); // DEFAULT_PAGE_SIZE = 20

        // 执行查询
        List<AiSearchRecordEntity> records = baseMapper.selectList(wrapper);

        // 转换为VO
        List<AiSearchRecordVo> recordVos = records.stream().map(entity -> {
            AiSearchRecordVo vo = new AiSearchRecordVo();
            BeanUtils.copyProperties(entity, vo);

            // 确保searchEngineResponse不为null
            if (vo.getSearchEngineResponse() == null) {
                Map<String, Object> emptyResp = new HashMap<>();
                emptyResp.put("items", new ArrayList<>());
                vo.setSearchEngineResponse(emptyResp);
            }

            // TODO: 设置aiModelPlatform(需要从模型配置获取)
            // AiModel aiModel = MODEL_ID_TO_OBJ.get(entity.getAiModelId());
            // vo.setAiModelPlatform(aiModel != null ? aiModel.getPlatform() : "");

            return vo;
        }).collect(Collectors.toList());

        // 计算minId
        long minId = 0;
        if (!records.isEmpty()) {
            minId = records.stream()
                    .map(AiSearchRecordEntity::getId)
                    .min(Long::compareTo)
                    .orElse(0L);
        }

        // 构建响应
        AiSearchRespVo result = new AiSearchRespVo();
        result.setMinId(minId);
        result.setRecords(recordVos);

        return result;
    }

    /**
     * 软删除搜索记录
     * 对齐AIDeepin: com.moyz.adi.common.service.AiSearchRecordService.softDelete(String uuid)
     *
     * <p>通过uuid软删除搜索记录,设置is_deleted=true</p>
     *
     * @param uuid 搜索记录UUID
     * @return 是否删除成功
     */
    public boolean softDelete(String uuid) {
        log.info("软删除搜索记录,uuid:{}", uuid);

        // 通过uuid查询记录是否存在
        AiSearchRecordEntity exist = ChainWrappers.lambdaQueryChain(baseMapper)
                .eq(AiSearchRecordEntity::getSearchUuid, uuid)
                .eq(AiSearchRecordEntity::getIsDeleted, false)
                .one();

        if (exist == null) {
            log.warn("搜索记录不存在或已删除,uuid:{}", uuid);
            return false;
        }

        // 执行软删除
        return ChainWrappers.lambdaUpdateChain(baseMapper)
                .eq(AiSearchRecordEntity::getId, exist.getId())
                .set(AiSearchRecordEntity::getIsDeleted, true)
                .update();
    }

    /**
     * 搜索结果网页内部类
     */
    @lombok.Data
    public static class SearchReturnWebPage {
        private String title;
        private String link;
        private String snippet;
    }
}
