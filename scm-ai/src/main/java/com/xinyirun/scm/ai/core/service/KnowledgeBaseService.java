package com.xinyirun.scm.ai.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseEntity;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseItemEntity;
import com.xinyirun.scm.ai.bean.vo.config.AiModelConfigVo;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseItemVo;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseVo;
import com.xinyirun.scm.ai.common.constant.AiConstant;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseItemMapper;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseMapper;
import com.xinyirun.scm.ai.core.service.milvus.MilvusVectorIndexingService;
import com.xinyirun.scm.ai.core.service.config.AiModelConfigService;
import com.xinyirun.scm.bean.system.ao.mqsender.MqMessageAo;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.utils.UuidUtil;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.core.system.service.master.user.IMStaffService;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mq.rabbitmq.producer.ScmMqProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 知识库管理 Service
 *
 * <p>负责知识库的全生命周期管理，包括创建、更新、删除、文档管理和索引</p>
 * <p>技术架构：MySQL(元数据) + Milvus(向量索引) + Neo4j(知识图谱)</p>
 *
 * @author SCM AI Team
 * @since 2025-10-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseService {

    private final AiKnowledgeBaseMapper knowledgeBaseMapper;
    private final AiKnowledgeBaseItemMapper itemMapper;
    private final ScmMqProducer scmMqProducer;
    private final DocumentParsingService documentParsingService;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final IMStaffService staffService;
    private final com.xinyirun.scm.core.system.service.sys.file.ISFileService sFileService;
    private final MilvusVectorIndexingService milvusVectorIndexingService;
    private final Neo4jGraphIndexingService neo4jGraphIndexingService;
    private final com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseGraphSegmentMapper graphSegmentMapper;
    private final AiModelConfigService aiModelConfigService;
    private final com.xinyirun.scm.ai.core.mapper.config.AiModelConfigMapper aiModelConfigMapper;
    private final com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseQaMapper qaMapper;
    private final com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseQaRefEmbeddingMapper qaRefEmbeddingMapper;
    private final com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseQaRefGraphMapper qaRefGraphMapper;

    /**
     * Redis key: 用户索引进行中标识
     * <p>格式：ai:kb:user:{userId}:indexing</p>
     * <p>用途：防止用户重复提交索引任务</p>
     */
    private static final String USER_INDEXING_KEY_PATTERN = "ai:kb:user:%s:indexing";

    /**
     * 保存或更新知识库
     *
     * <p>简单的CRUD操作,直接保存前端传入的数据</p>
     *
     * @param vo 知识库VO对象
     * @return 保存后的知识库VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiKnowledgeBaseVo saveOrUpdate(AiKnowledgeBaseVo vo) {
        AiKnowledgeBaseEntity entity = new AiKnowledgeBaseEntity();

        // 判断是新增还是更新
        boolean isNew = (vo.getKbUuid() == null || vo.getKbUuid().isEmpty());

        if (isNew) {
            // 新增：排除id字段(由数据库自动生成)
            BeanUtils.copyProperties(vo, entity, "id");

            // kb_uuid 格式：{tenantCode}::{uuid}，方便定时任务识别租户
            String tenantCode = DataSourceHelper.getCurrentDataSourceName();
            String uuid = UuidUtil.createShort();
            entity.setKbUuid(tenantCode + "::" + uuid);

            Long currentUserId = SecurityUtil.getStaff_id();
            entity.setOwnerId(String.valueOf(currentUserId));

            // 查询员工信息获取name
            MStaffVo staffVo = staffService.selectByid(currentUserId);
            if (staffVo != null && staffVo.getName() != null) {
                entity.setOwnerName(staffVo.getName());
            } else {
                log.warn("无法获取员工姓名，userId: {}", currentUserId);
                entity.setOwnerName("");
            }

            knowledgeBaseMapper.insert(entity);

            log.info("新增知识库成功，kbUuid: {}, title: {}",
                     entity.getKbUuid(), entity.getTitle());

            // 返回插入后的实体
            BeanUtils.copyProperties(entity, vo);
        } else {
            // 更新：先查询完整实体（SCM标准模式）
            AiKnowledgeBaseEntity existEntity = knowledgeBaseMapper.selectById(vo.getId());
            if (existEntity == null) {
                throw new RuntimeException("知识库不存在，无法更新");
            }

            // 更新需要修改的字段
            existEntity.setTitle(vo.getTitle());
            existEntity.setRemark(vo.getRemark());
            existEntity.setIsPublic(vo.getIsPublic());
            existEntity.setIsStrict(vo.getIsStrict());
            existEntity.setIngestMaxOverlap(vo.getIngestMaxOverlap());
            existEntity.setIngestTokenEstimator(vo.getIngestTokenEstimator());

            // 模型配置字段：只有非空时才更新
            if (StringUtils.isNotBlank(vo.getIngestModelId())) {
                existEntity.setIngestModelId(vo.getIngestModelId());
                // 同步更新模型名称
                if (StringUtils.isNotBlank(vo.getIngestModelName())) {
                    existEntity.setIngestModelName(vo.getIngestModelName());
                } else {
                    // 如果VO没有传模型名称，从AI模型配置中查询
                    try {
                        Long modelId = Long.parseLong(vo.getIngestModelId());
                        com.xinyirun.scm.ai.bean.entity.config.AiModelConfigEntity modelConfig =
                            aiModelConfigMapper.selectById(modelId);
                        if (modelConfig != null) {
                            existEntity.setIngestModelName(modelConfig.getModelName());
                        }
                    } catch (NumberFormatException e) {
                        log.warn("模型ID格式错误: {}", vo.getIngestModelId());
                    }
                }
            }

            existEntity.setRetrieveMaxResults(vo.getRetrieveMaxResults());
            existEntity.setRetrieveMinScore(vo.getRetrieveMinScore());
            existEntity.setQuerySystemMessage(vo.getQuerySystemMessage());
            existEntity.setQueryLlmTemperature(vo.getQueryLlmTemperature());

            knowledgeBaseMapper.updateById(existEntity);

            log.info("更新知识库成功，kbUuid: {}, title: {}",
                     existEntity.getKbUuid(), existEntity.getTitle());

            // 返回更新后的实体
            BeanUtils.copyProperties(existEntity, vo);
        }

        return vo;
    }

    /**
     * 单文档上传
     *
     * <p>创建知识库文档项并可选择立即索引</p>
     *
     * @param kbUuid 知识库UUID
     * @param indexAfterUpload 是否立即索引
     * @param file 上传的文件
     * @param indexTypeList 索引类型列表（embedding、graphical）
     * @return 创建的文档VO
     * @throws IOException 文件处理异常
     */
    @Transactional(rollbackFor = Exception.class)
    public AiKnowledgeBaseItemVo uploadDoc(String kbUuid, Boolean indexAfterUpload, MultipartFile file, List<String> indexTypeList) throws IOException {
        // 1. 查询知识库
        LambdaQueryWrapper<AiKnowledgeBaseEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseEntity::getKbUuid, kbUuid);
        AiKnowledgeBaseEntity kb = knowledgeBaseMapper.selectOne(wrapper);

        if (kb == null) {
            throw new RuntimeException("知识库不存在：" + kbUuid);
        }

        String fileName = file.getOriginalFilename();
        String tenantCode = DataSourceHelper.getCurrentDataSourceName();
        String uuid = UuidUtil.createShort();
        String itemUuid = tenantCode + "::" + uuid;

        // 3. 创建知识库文档项（MySQL）
        AiKnowledgeBaseItemEntity item = new AiKnowledgeBaseItemEntity();
        item.setItemUuid(itemUuid);
        item.setKbId(kb.getId());
        item.setKbUuid(kbUuid);
        item.setTitle(fileName);
        item.setSourceFileName(fileName);
        item.setEmbeddingStatus(0); // 未索引
        item.setSourceFileUploadTime(System.currentTimeMillis());

        itemMapper.insert(item);

        // 4. 如果需要立即索引，发送RabbitMQ消息
        if (Boolean.TRUE.equals(indexAfterUpload)) {
            indexItems(List.of(itemUuid), indexTypeList);
        }

        // 5. 返回VO
        AiKnowledgeBaseItemVo vo = new AiKnowledgeBaseItemVo();
        BeanUtils.copyProperties(item, vo);
        return vo;
    }

    /**
     * 从URL创建文档
     */
    @Transactional(rollbackFor = Exception.class)
    public AiKnowledgeBaseItemVo uploadDocFromUrl(String kbUuid, String fileUrl, String fileName, Long fileSize, Boolean indexAfterUpload, List<String> indexTypeList) {
        log.info("从URL创建文档，kbUuid: {}, fileUrl: {}, fileName: {}", kbUuid, fileUrl, fileName);

        // 1. 查询知识库
        LambdaQueryWrapper<AiKnowledgeBaseEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseEntity::getKbUuid, kbUuid);
        AiKnowledgeBaseEntity kb = knowledgeBaseMapper.selectOne(wrapper);

        if (kb == null) {
            throw new RuntimeException("知识库不存在：" + kbUuid);
        }

        // 2. 解析文档内容
        String content = null;
        String brief = null;

        try {
            content = documentParsingService.parseDocumentFromUrl(fileUrl, fileName);

            if (content != null) {
                brief = content.length() > 200 ? content.substring(0, 200) : content;
                log.info("文档解析成功，内容长度: {} 字符", content.length());
            } else {
                log.warn("文档解析返回null，将在索引时重试");
            }
        } catch (Exception e) {
            log.warn("文档解析失败，将在索引时重试，错误: {}", e.getMessage());
        }

        // 3. 创建知识库文档项
        String itemUuid = UuidUtil.createShort();
        AiKnowledgeBaseItemEntity item = new AiKnowledgeBaseItemEntity();
        item.setItemUuid(itemUuid);
        item.setKbUuid(kbUuid);
        item.setTitle(fileName);
        item.setRemark(content);
        item.setBrief(brief);
        item.setSourceFileName(fileName);
        item.setSourceFileUploadTime(System.currentTimeMillis());
        item.setEmbeddingStatus(0);

        itemMapper.insert(item);

        log.info("文档项创建成功，itemUuid: {}", itemUuid);

        // 4. 如果需要立即索引，发送MQ消息
        if (Boolean.TRUE.equals(indexAfterUpload)) {
            indexItems(List.of(itemUuid), indexTypeList);
            log.info("已发送索引消息，itemUuid: {}, indexTypes: {}", itemUuid, indexTypeList);
        }

        // 5. 转换为VO返回
        AiKnowledgeBaseItemVo vo = new AiKnowledgeBaseItemVo();
        BeanUtils.copyProperties(item, vo);

        return vo;
    }

    /**
     * 索引知识库所有未索引文档
     *
     * @param kbUuid 知识库UUID
     * @param indexTypes 索引类型列表（embedding、graphical）
     * @return 是否成功发送索引消息
     */
    public boolean indexing(String kbUuid, List<String> indexTypes) {
        // 查询该知识库下所有未索引的文档
        LambdaQueryWrapper<AiKnowledgeBaseItemEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseItemEntity::getKbUuid, kbUuid);
        wrapper.eq(AiKnowledgeBaseItemEntity::getEmbeddingStatus, 0);

        List<AiKnowledgeBaseItemEntity> items = itemMapper.selectList(wrapper);

        if (items.isEmpty()) {
            return false;
        }

        // 获取所有itemUuid
        List<String> itemUuids = items.stream().map(AiKnowledgeBaseItemEntity::getItemUuid).toList();

        // 批量索引
        return indexItems(itemUuids, indexTypes);
    }

    /**
     * 索引指定文档列表
     *
     * <p>通过RabbitMQ异步处理索引任务，支持向量索引和图谱索引</p>
     *
     * <p>实现方式：</p>
     * <ul>
     *   <li>设置Redis索引标识，防止重复提交</li>
     *   <li>查询文档项和文件URL</li>
     *   <li>发送MQ消息到索引队列</li>
     *   <li>异步消费者执行实际索引任务</li>
     * </ul>
     *
     * @param itemUuids 文档UUID列表
     * @param indexTypes 索引类型列表（embedding、graphical）
     * @return 是否成功发送消息
     */
    public boolean indexItems(List<String> itemUuids, List<String> indexTypes) {
        if (itemUuids == null || itemUuids.isEmpty()) {
            return false;
        }

        try {
            // 设置用户索引进行中标识
            Long currentUserId = SecurityUtil.getStaff_id();
            String userIndexKey = String.format(AiConstant.USER_INDEXING_KEY, currentUserId);
            stringRedisTemplate.opsForValue().set(userIndexKey, "", 10, TimeUnit.MINUTES);

            log.info("设置用户索引标识，userId: {}, key: {}", currentUserId, userIndexKey);

            // 发送RabbitMQ消息进行异步索引
            for (String itemUuid : itemUuids) {
                // 查询文档项
                LambdaQueryWrapper<AiKnowledgeBaseItemEntity> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(AiKnowledgeBaseItemEntity::getItemUuid, itemUuid);
                AiKnowledgeBaseItemEntity item = itemMapper.selectOne(wrapper);

                if (item == null) {
                    log.warn("文档不存在，跳过索引，itemUuid: {}", itemUuid);
                    continue;
                }

                // 查询文件URL（从s_file_info表）
                String fileUrl = "";
                List<com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo> files =
                    sFileService.selectFileInfoBySerialTypeAndId("ai_knowledge_base_item", item.getId());

                if (files != null && !files.isEmpty()) {
                    fileUrl = files.get(0).getUrl();  // 取第一个文件URL
                    log.info("查询到文件URL，itemUuid: {}, url: {}", itemUuid, fileUrl);
                } else {
                    log.warn("未查询到文件URL，itemUuid: {}, itemId: {}", itemUuid, item.getId());
                }

                // 构建消息上下文
                Map<String, Object> messageContext = new HashMap<>();
                messageContext.put("item_uuid", itemUuid);
                messageContext.put("kb_uuid", item.getKbUuid());
                messageContext.put("file_url", fileUrl);
                messageContext.put("file_name", item.getSourceFileName());
                messageContext.put("index_types", indexTypes);

                // 构建MqMessageAo（消息体，使用Builder模式）
                MqMessageAo mqMessageAo = MqMessageAo.builder()
                        .messageBeanClass("java.util.HashMap")  // 设置消息类型为HashMap
                        .parameterJson(objectMapper.writeValueAsString(messageContext))
                        .build();

                // 构建MqSenderAo（SCM-MQ标准格式）
                MqSenderAo mqSenderAo = new MqSenderAo();
                mqSenderAo.setKey(UuidUtil.createShort()); // 消息ID
                mqSenderAo.setType(MQEnum.MQ_AI_DOCUMENT_INDEXING_QUEUE.getQueueCode());
                mqSenderAo.setName("AI文档索引");
                mqSenderAo.setTenant_code(DataSourceHelper.getCurrentDataSourceName());
                mqSenderAo.setMqMessageAo(mqMessageAo);

                // 发送消息
                scmMqProducer.send(mqSenderAo, MQEnum.MQ_AI_DOCUMENT_INDEXING_QUEUE);

                log.info("发送文档索引消息成功，item_uuid: {}, kb_uuid: {}, index_types: {}",
                        itemUuid, item.getKbUuid(), indexTypes);
            }

            return true;

        } catch (Exception e) {
            log.error("发送文档索引消息失败，error: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查索引是否完成
     *
     * <p>通过Redis标识判断当前用户是否有索引任务正在执行</p>
     *
     * @return true表示索引已完成，false表示索引进行中
     */
    public boolean checkIndexIsFinish() {
        Long currentUserId = SecurityUtil.getStaff_id();
        String userIndexKey = String.format(USER_INDEXING_KEY_PATTERN, currentUserId);
        Boolean hasKey = stringRedisTemplate.hasKey(userIndexKey);
        return Boolean.FALSE.equals(hasKey);
    }

    /**
     * 搜索我的知识库
     *
     * @param keyword 搜索关键词
     * @param includeOthersPublic 是否包含其他人公开的知识库
     * @param currentPage 当前页码
     * @param pageSize 每页大小
     * @return 分页查询结果
     */
    public IPage<AiKnowledgeBaseVo> searchMine(String keyword, Boolean includeOthersPublic, Integer currentPage, Integer pageSize) {
        Page<AiKnowledgeBaseEntity> page = new Page<>(currentPage, pageSize);

        LambdaQueryWrapper<AiKnowledgeBaseEntity> wrapper = new LambdaQueryWrapper<>();

        Long currentUserId = SecurityUtil.getStaff_id();
        String ownerIdStr = String.valueOf(currentUserId);

        log.info("搜索知识库 - currentUserId: {}, ownerIdStr: {}, keyword: {}, page: {}, size: {}",
                 currentUserId, ownerIdStr, keyword, currentPage, pageSize);

        wrapper.eq(AiKnowledgeBaseEntity::getOwnerId, ownerIdStr);

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(AiKnowledgeBaseEntity::getTitle, keyword);
        }

        // TODO: 如果includeOthersPublic=true，还需要查询其他人公开的知识库

        wrapper.orderByDesc(AiKnowledgeBaseEntity::getC_time);

        IPage<AiKnowledgeBaseEntity> entityPage = knowledgeBaseMapper.selectPage(page, wrapper);

        log.info("搜索知识库结果 - 查询到 {} 条记录，总数: {}", entityPage.getRecords().size(), entityPage.getTotal());

        IPage<AiKnowledgeBaseVo> voPage = entityPage.convert(entity -> {
            AiKnowledgeBaseVo vo = new AiKnowledgeBaseVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        });

        return voPage;
    }

    /**
     * 搜索公开的知识库
     */
    public IPage<AiKnowledgeBaseVo> searchPublic(String keyword, Integer currentPage, Integer pageSize) {
        Page<AiKnowledgeBaseEntity> page = new Page<>(currentPage, pageSize);

        LambdaQueryWrapper<AiKnowledgeBaseEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseEntity::getIsPublic, 1);

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(AiKnowledgeBaseEntity::getTitle, keyword);
        }

        wrapper.orderByDesc(AiKnowledgeBaseEntity::getStarCount, AiKnowledgeBaseEntity::getC_time);

        IPage<AiKnowledgeBaseEntity> entityPage = knowledgeBaseMapper.selectPage(page, wrapper);

        return entityPage.convert(entity -> {
            AiKnowledgeBaseVo vo = new AiKnowledgeBaseVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        });
    }

    /**
     * 根据UUID获取知识库
     */
    public AiKnowledgeBaseVo getByUuid(String uuid) {
        LambdaQueryWrapper<AiKnowledgeBaseEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseEntity::getKbUuid, uuid);

        AiKnowledgeBaseEntity entity = knowledgeBaseMapper.selectOne(wrapper);

        if (entity == null) {
            return null;
        }

        AiKnowledgeBaseVo vo = new AiKnowledgeBaseVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 物理删除知识库（级联删除MySQL、Milvus、Neo4j三处数据）
     * <p>SCM系统统一使用物理删除</p>
     *
     * <p>删除顺序（按依赖关系反向删除）：</p>
     * <ol>
     *   <li>查询知识库是否存在</li>
     *   <li>删除Neo4j图谱数据（WHERE kb_uuid = ?）</li>
     *   <li>删除Milvus向量数据（WHERE kb_uuid = ?）</li>
     *   <li>删除MySQL数据（按依赖关系）：
     *     <ul>
     *       <li>qa_ref_embedding表（问答向量引用）</li>
     *       <li>qa_ref_graph表（问答图谱引用）</li>
     *       <li>qa表（问答记录）</li>
     *       <li>graph_segment表</li>
     *       <li>item表</li>
     *       <li>knowledge_base主表</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @param uuid 知识库UUID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String uuid) {
        log.info("删除知识库（物理删除），kb_uuid: {}", uuid);

        // 1. 查询知识库是否存在
        AiKnowledgeBaseEntity kbEntity = knowledgeBaseMapper.selectByKbUuid(uuid);
        if (kbEntity == null) {
            log.warn("知识库不存在, kb_uuid: {}", uuid);
            return false;
        }

        // 2. 删除Neo4j图谱数据
        try {
            String deleteResult = neo4jGraphIndexingService.deleteKnowledgeBaseGraph(uuid);
            log.info("删除Neo4j图谱数据, kb_uuid: {}, 删除结果: {}", uuid, deleteResult);
        } catch (Exception e) {
            log.error("删除Neo4j图谱数据失败, kb_uuid: {}", uuid, e);
            throw new RuntimeException("删除图谱数据失败: " + e.getMessage(), e);
        }

        // 3. 删除Milvus向量数据
        try {
            int deletedVectors = milvusVectorIndexingService.deleteKnowledgeBaseEmbeddings(uuid);
            log.info("删除Milvus向量数据, kb_uuid: {}, 删除数量: {}", uuid, deletedVectors);
        } catch (Exception e) {
            log.error("删除Milvus向量数据失败, kb_uuid: {}", uuid, e);
            throw new RuntimeException("删除向量数据失败: " + e.getMessage(), e);
        }

        // 4. 删除MySQL数据（按依赖关系：qa_ref_* → qa → graph_segment → item → knowledge_base）
        try {
            // 4.1 删除问答向量引用数据（依赖qa表）
            Integer deletedQaRefEmbedding = qaRefEmbeddingMapper.deleteByKbUuid(uuid);
            int deletedQaRefEmbeddingCount = (deletedQaRefEmbedding != null) ? deletedQaRefEmbedding : 0;
            log.info("删除问答向量引用数据, kb_uuid: {}, 删除数量: {}", uuid, deletedQaRefEmbeddingCount);

            // 4.2 删除问答图谱引用数据（依赖qa表）
            Integer deletedQaRefGraph = qaRefGraphMapper.deleteByKbUuid(uuid);
            int deletedQaRefGraphCount = (deletedQaRefGraph != null) ? deletedQaRefGraph : 0;
            log.info("删除问答图谱引用数据, kb_uuid: {}, 删除数量: {}", uuid, deletedQaRefGraphCount);

            // 4.3 删除问答记录
            Integer deletedQa = qaMapper.deleteByKbUuid(uuid);
            int deletedQaCount = (deletedQa != null) ? deletedQa : 0;
            log.info("删除问答记录, kb_uuid: {}, 删除数量: {}", uuid, deletedQaCount);

            // 4.4 删除graph segment数据
            int deletedSegments = graphSegmentMapper.deleteByKbUuid(uuid);
            log.info("删除graph segment数据, kb_uuid: {}, 删除数量: {}", uuid, deletedSegments);

            // 4.5 删除item数据
            int deletedItems = itemMapper.deleteByKbUuid(uuid);
            log.info("删除item数据, kb_uuid: {}, 删除数量: {}", uuid, deletedItems);

            // 4.6 删除主表
            int result = knowledgeBaseMapper.deleteByKbUuid(uuid);
            boolean success = result > 0;

            if (success) {
                log.info("知识库删除成功（物理删除），kb_uuid: {}", uuid);
            } else {
                log.warn("知识库主表删除失败, kb_uuid: {}", uuid);
            }

            return success;

        } catch (Exception e) {
            log.error("删除MySQL数据失败, kb_uuid: {}", uuid, e);
            throw new RuntimeException("删除MySQL数据失败: " + e.getMessage(), e);
        }
    }

}
