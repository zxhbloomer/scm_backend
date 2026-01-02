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
     * @param brief 简介（用于填充知识项的title和brief字段）
     * @param staffId 员工ID（MCP工具传入，避免依赖SecurityUtil）
     * @return 包含创建结果的Map
     */
    public Map<String, Object> createTempKnowledgeBase(String text, List<String> fileUrls, String brief, Long staffId) {
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
            // 重要：传入staffId，避免在异步线程中依赖SecurityUtil获取用户信息
            AiKnowledgeBaseVo kbVo = buildTempKnowledgeBase(staffId);
            AiKnowledgeBaseVo savedKb = knowledgeBaseService.saveOrUpdate(kbVo);
            String kbUuid = savedKb.getKbUuid();

            log.info("临时知识库创建成功: kbUuid={}, 过期时间={}",
                    kbUuid, savedKb.getExpireTime());

            // 3. 创建知识项
            List<String> itemUuids = new ArrayList<>();

            // 3.1 创建文本item
            if (text != null && !text.trim().isEmpty()) {
                String textItemUuid = createTextItem(kbUuid, savedKb.getId(), text, brief);
                itemUuids.add(textItemUuid);
                log.info("文本item创建成功: itemUuid={}", textItemUuid);
            }

            // 3.2 创建文件items
            if (fileUrls != null && !fileUrls.isEmpty()) {
                List<String> fileItemUuids = createFileItems(kbUuid, savedKb.getId(), fileUrls, brief);
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
     *
     * @param staffId 员工ID（用于设置ownerId，避免依赖SecurityUtil）
     */
    private AiKnowledgeBaseVo buildTempKnowledgeBase(Long staffId) {
        AiKnowledgeBaseVo kbVo = new AiKnowledgeBaseVo();
        String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        kbVo.setTitle("临时知识库-" + timestamp);
        kbVo.setRemark("由MCP工具自动创建的临时知识库，用于workflow");
        kbVo.setIsTemp(true);  // 标记为临时知识库
        kbVo.setExpireTime(LocalDateTime.now().plusHours(2));
        kbVo.setIsPublic(1);  // 公开（临时知识库需要公开才能被工作流访问）
        kbVo.setIsStrict(1);  // 严格模式（临时知识库使用严格匹配）

        // 重要：直接设置ownerId，避免KnowledgeBaseService.saveOrUpdate依赖SecurityUtil
        // 在MCP异步线程中SecurityUtil上下文不可用，会导致selectByid查询错误
        if (staffId != null) {
            kbVo.setOwnerId(String.valueOf(staffId));
            kbVo.setOwnerName("MCP工具用户");  // 临时知识库不需要精确的用户名
        }

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
     *
     * @param kbUuid 知识库UUID
     * @param kbId 知识库ID
     * @param text 文本内容
     * @param brief 简介（用于填充title和brief字段）
     * @return 知识项UUID
     */
    private String createTextItem(String kbUuid, String kbId, String text, String brief) {
        AiKnowledgeBaseItemEntity item = new AiKnowledgeBaseItemEntity();

        String tenantCode = DataSourceHelper.getCurrentDataSourceName();
        String uuid = UuidUtil.createShort();
        String itemUuid = tenantCode + "::" + uuid;

        // 向后兼容: brief为空时使用默认值
        String actualBrief = (brief == null || brief.trim().isEmpty()) ? "文本内容" : brief;

        item.setItemUuid(itemUuid);
        item.setKbId(kbId);
        item.setKbUuid(kbUuid);
        item.setTitle(actualBrief);  // 使用brief填充title
        item.setRemark(text);  // 文本内容保存在remark字段
        item.setBrief(actualBrief);  // 使用brief填充brief字段
        item.setEmbeddingStatus(0);  // 待索引

        documentProcessingService.getItemMapper().insert(item);

        return itemUuid;
    }

    /**
     * 创建文件类型的知识项
     *
     * @param kbUuid 知识库UUID
     * @param kbId 知识库ID
     * @param fileUrls 文件URL列表
     * @param brief 简介（用于填充title和brief字段）
     * @return 知识项UUID列表
     */
    private List<String> createFileItems(String kbUuid, String kbId, List<String> fileUrls, String brief) {
        // 向后兼容: brief为空时使用默认值
        String actualBrief = (brief == null || brief.trim().isEmpty()) ? "文件内容" : brief;

        // 1. 构建文件信息列表
        List<SFileInfoVo> fileInfoList = new ArrayList<>();
        for (String fileUrl : fileUrls) {
            String fileName = extractFileNameFromUrl(fileUrl);

            SFileInfoVo fileInfo = new SFileInfoVo();
            fileInfo.setUrl(fileUrl);
            fileInfo.setFileName(fileName);
            fileInfo.setFile_size(new java.math.BigDecimal("0"));
            fileInfoList.add(fileInfo);
        }

        // 2. 调用批量创建方法（会使用文件名作为title）
        List<String> itemUuids = documentProcessingService
                .batchCreateItems(kbUuid, fileInfoList, false)
                .stream()
                .map(vo -> vo.getItemUuid())
                .toList();

        // 3. 手动更新每个item的title和brief为用户输入的简介
        for (String itemUuid : itemUuids) {
            AiKnowledgeBaseItemEntity item = documentProcessingService.getItemMapper()
                    .selectByItemUuid(itemUuid);

            if (item != null) {
                item.setTitle(actualBrief);
                item.setBrief(actualBrief);
                documentProcessingService.getItemMapper().updateById(item);
            }
        }

        return itemUuids;
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
