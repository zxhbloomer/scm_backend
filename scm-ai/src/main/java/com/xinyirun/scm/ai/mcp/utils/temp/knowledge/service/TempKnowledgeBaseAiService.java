package com.xinyirun.scm.ai.mcp.utils.temp.knowledge.service;

import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseItemEntity;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseVo;
import com.xinyirun.scm.ai.core.service.DocumentIndexingService;
import com.xinyirun.scm.ai.core.service.DocumentProcessingService;
import com.xinyirun.scm.ai.core.service.KnowledgeBaseService;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.common.utils.UuidUtil;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 临时知识库AI服务
 *
 * 提供临时知识库创建和管理的业务逻辑封装,供MCP工具调用
 *
 * 核心功能：
 * - 创建临时知识库（is_temp=1, 2小时后自动清理）
 * - 支持文本和文件（数组）输入
 * - 同步执行向量索引（仅embedding，不做graphical）
 * - 返回统一的Map结构供MCP工具转换为JSON
 *
 * @author zzxxhh
 * @since 2025-12-03
 */
@Slf4j
@Service
public class TempKnowledgeBaseAiService {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @Autowired
    private DocumentProcessingService documentProcessingService;

    @Autowired
    private DocumentIndexingService documentIndexingService;

    /**
     * 创建临时知识库并同步执行向量索引
     *
     * @param text 文本内容（可选）
     * @param fileUrls 文件URL数组（可选）
     * @return 包含创建结果的Map
     */
    public Map<String, Object> createTempKnowledgeBase(String text, List<String> fileUrls) {
        try {
            // 1. 参数验证
            if ((text == null || text.trim().isEmpty()) &&
                (fileUrls == null || fileUrls.isEmpty())) {
                return Map.of(
                    "success", false,
                    "message", "文本和文件不能同时为空，至少提供一项"
                );
            }

            // 2. 创建临时知识库
            AiKnowledgeBaseVo kbVo = buildTempKnowledgeBase();
            AiKnowledgeBaseVo savedKb = knowledgeBaseService.saveOrUpdate(kbVo);
            String kbUuid = savedKb.getKbUuid();

            log.info("临时知识库创建成功: kbUuid={}, 过期时间={}",
                    kbUuid, savedKb.getExpireTime());

            // 3. 创建知识项
            List<String> itemUuids = new ArrayList<>();

            // 3.1 创建文本item
            if (text != null && !text.trim().isEmpty()) {
                String textItemUuid = createTextItem(kbUuid, savedKb.getId(), text);
                itemUuids.add(textItemUuid);
                log.info("文本item创建成功: itemUuid={}", textItemUuid);
            }

            // 3.2 创建文件items
            if (fileUrls != null && !fileUrls.isEmpty()) {
                List<String> fileItemUuids = createFileItems(kbUuid, fileUrls);
                itemUuids.addAll(fileItemUuids);
                log.info("文件items创建成功，数量: {}", fileItemUuids.size());
            }

            // 4. 同步执行向量索引
            log.info("开始同步索引，itemCount: {}", itemUuids.size());
            int indexedCount = syncIndexItems(kbUuid, itemUuids);
            log.info("索引完成，成功: {}/{}", indexedCount, itemUuids.size());

            // 5. 返回成功结果
            return Map.of(
                "success", true,
                "kbUuid", kbUuid,
                "message", "临时知识库创建成功，已完成向量索引",
                "itemCount", itemUuids.size(),
                "indexedCount", indexedCount,
                "expireTime", savedKb.getExpireTime().toString()
            );

        } catch (Exception e) {
            log.error("创建临时知识库失败: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "message", "创建临时知识库失败: " + e.getMessage(),
                "error", e.getClass().getSimpleName()
            );
        }
    }

    /**
     * 构建临时知识库VO
     */
    private AiKnowledgeBaseVo buildTempKnowledgeBase() {
        AiKnowledgeBaseVo kbVo = new AiKnowledgeBaseVo();
        String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        kbVo.setTitle("临时知识库-" + timestamp);
        kbVo.setRemark("由MCP工具自动创建的临时知识库，用于workflow");
        kbVo.setIsTemp(1);  // 标记为临时
        kbVo.setExpireTime(LocalDateTime.now().plusHours(2));
        kbVo.setIsPublic(0);  // 私有
        kbVo.setIsStrict(0);  // 非严格模式

        // 设置默认配置
        kbVo.setIngestMaxOverlap(2000);
        kbVo.setIngestTokenEstimator("cl100k_base");
        kbVo.setIngestEmbeddingModel("BAAI/bge-m3");
        kbVo.setRetrieveMaxResults(5);
        kbVo.setRetrieveMinScore(new java.math.BigDecimal("0.3"));
        kbVo.setQueryLlmTemperature(new java.math.BigDecimal("0.7"));

        return kbVo;
    }

    /**
     * 创建文本类型的知识项
     */
    private String createTextItem(String kbUuid, String kbId, String text) {
        AiKnowledgeBaseItemEntity item = new AiKnowledgeBaseItemEntity();

        String tenantCode = DataSourceHelper.getCurrentDataSourceName();
        String uuid = UuidUtil.createShort();
        String itemUuid = tenantCode + "::" + uuid;

        item.setItemUuid(itemUuid);
        item.setKbId(kbId);
        item.setKbUuid(kbUuid);
        item.setTitle("文本内容");
        item.setRemark(text);  // 文本内容保存在remark字段
        item.setBrief(text.length() > 200 ? text.substring(0, 200) : text);
        item.setEmbeddingStatus(0);  // 待索引

        documentProcessingService.getItemMapper().insert(item);

        return itemUuid;
    }

    /**
     * 创建文件类型的知识项
     */
    private List<String> createFileItems(String kbUuid, List<String> fileUrls) {
        List<SFileInfoVo> fileInfoList = new ArrayList<>();

        for (String fileUrl : fileUrls) {
            String fileName = extractFileNameFromUrl(fileUrl);

            SFileInfoVo fileInfo = new SFileInfoVo();
            fileInfo.setUrl(fileUrl);
            fileInfo.setFileName(fileName);
            fileInfo.setFile_size(new java.math.BigDecimal("0"));  // 文件大小未知
            fileInfoList.add(fileInfo);
        }

        return documentProcessingService
                .batchCreateItems(kbUuid, fileInfoList, false)
                .stream()
                .map(vo -> vo.getItemUuid())
                .toList();
    }

    /**
     * 从URL提取文件名
     */
    private String extractFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "未知文件";
        }

        int lastSlash = url.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < url.length() - 1) {
            return url.substring(lastSlash + 1);
        }

        return "未知文件";
    }

    /**
     * 同步执行索引
     *
     * 与现有的indexItems()不同：
     * - 不使用MQ异步，直接调用DocumentIndexingService
     * - 同步等待所有item索引完成
     * - 只做向量索引（indexTypes只包含"embedding"）
     *
     * @return 成功索引的item数量
     */
    private int syncIndexItems(String kbUuid, List<String> itemUuids) {
        int successCount = 0;
        List<String> indexTypes = Arrays.asList("embedding");  // 仅向量索引
        String tenantCode = DataSourceHelper.getCurrentDataSourceName();

        for (String itemUuid : itemUuids) {
            try {
                // 查询item信息（使用SQL查询方法，符合SCM规范17/24）
                AiKnowledgeBaseItemEntity item = documentProcessingService.getItemMapper()
                        .selectByItemUuid(itemUuid);

                if (item == null) {
                    log.warn("Item不存在，跳过索引: itemUuid={}", itemUuid);
                    continue;
                }

                // 查询文件URL（如果是文件类型）
                String fileUrl = "";
                String fileName = item.getTitle();

                if (item.getRemark() == null || item.getRemark().isEmpty()) {
                    // 没有remark说明是文件类型，需要查询URL
                    List<SFileInfoVo> files = documentProcessingService
                            .getSFileService()
                            .selectFileInfoBySerialTypeAndId("ai_knowledge_base_item",
                                    item.getId());

                    if (files != null && !files.isEmpty()) {
                        fileUrl = files.get(0).getUrl();
                        fileName = files.get(0).getFileName();
                    }
                }

                // 同步调用索引服务（关键：这里是同步的）
                documentIndexingService.processDocument(
                        tenantCode,
                        itemUuid,
                        kbUuid,
                        fileUrl,
                        fileName,
                        indexTypes  // 只包含"embedding"
                );

                successCount++;
                log.info("Item索引完成: itemUuid={}", itemUuid);

            } catch (Exception e) {
                log.error("Item索引失败: itemUuid={}, 错误={}", itemUuid, e.getMessage(), e);
            }
        }

        return successCount;
    }
}
