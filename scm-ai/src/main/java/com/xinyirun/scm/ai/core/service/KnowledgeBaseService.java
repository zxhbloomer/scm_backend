package com.xinyirun.scm.ai.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.model.AiModelSourceEntity;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseEntity;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseItemEntity;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseItemVo;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseVo;
import com.xinyirun.scm.ai.common.constant.AiConstant;
import com.xinyirun.scm.ai.core.mapper.model.AiModelSourceMapper;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseItemMapper;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseMapper;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseGraphSegmentMapper;
import com.xinyirun.scm.ai.core.repository.elasticsearch.AiKnowledgeBaseEmbeddingRepository;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseGraphSegmentEntity;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.common.utils.UuidUtil;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.core.system.service.master.user.IMStaffService;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseStarEntity;
import com.xinyirun.scm.ai.core.service.rag.AiKnowledgeBaseStarService;
import com.xinyirun.scm.mq.rabbitmq.producer.ScmMqProducer;
import com.xinyirun.scm.bean.system.ao.mqsender.MqMessageAo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 知识库管理 Service
 *
 * 参考aideepin实现，适配SCM的MySQL+Elasticsearch+Neo4j架构
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
    private final AiModelSourceMapper aiModelSourceMapper;
    private final RabbitTemplate rabbitTemplate;
    private final ScmMqProducer scmMqProducer;
    private final DocumentParsingService documentParsingService;
    private final AiKnowledgeBaseStarService starService;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final IMStaffService staffService;
    private final com.xinyirun.scm.core.system.service.sys.file.ISFileService sFileService;
    private final AiKnowledgeBaseEmbeddingRepository embeddingRepository;
    private final Neo4jGraphIndexingService neo4jGraphIndexingService;
    private final com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseGraphSegmentMapper graphSegmentMapper;

    /**
     * Redis key: 用户索引进行中标识
     * 对应 aideepin 的 RedisKeyConstant.USER_INDEXING
     */
    private static final String USER_INDEXING_KEY_PATTERN = "ai:kb:user:%s:indexing";

    /**
     * 保存或更新知识库
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
            // 新增：排除id、模型ID和名称字段
            // id由MyBatis Plus自动生成，模型信息后续根据ID自动填充
            BeanUtils.copyProperties(vo, entity, "id", "ingestModelId", "ingestModelName");
        } else {
            // 更新：排除模型ID和名称字段，但保留id
            BeanUtils.copyProperties(vo, entity, "ingestModelId", "ingestModelName");
        }

        // 如果前端传入了模型ID，查询模型信息并自动填充模型名称
        if (StringUtils.isNotBlank(vo.getIngestModelId())) {
            LambdaQueryWrapper<AiModelSourceEntity> modelWrapper = new LambdaQueryWrapper<>();
            modelWrapper.eq(AiModelSourceEntity::getId, vo.getIngestModelId());
            AiModelSourceEntity modelEntity = aiModelSourceMapper.selectOne(modelWrapper);

            if (modelEntity != null) {
                entity.setIngestModelId(vo.getIngestModelId());
                String normalizedModelName = normalizeModelNameForKb(modelEntity);
                entity.setIngestModelName(normalizedModelName);

                log.info("知识库保存：自动填充模型名称，modelId: {}, modelName: {}",
                         vo.getIngestModelId(), normalizedModelName);
            } else {
                throw new RuntimeException("模型不存在，ID: " + vo.getIngestModelId());
            }
        } else {
            log.warn("知识库保存：未指定索引模型，将使用系统默认模型");
        }

        // 处理Token估计器
        if (StringUtils.isNotBlank(vo.getIngestTokenEstimator())) {
            entity.setIngestTokenEstimator(vo.getIngestTokenEstimator());
        }

        if (isNew) {
            // 新增
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

            log.info("新增知识库成功，kbUuid: {}, title: {}, ingestModelId: {}, ingestModelName: {}",
                     entity.getKbUuid(), entity.getTitle(), entity.getIngestModelId(), entity.getIngestModelName());
        } else {
            // 更新
            knowledgeBaseMapper.updateById(entity);

            log.info("更新知识库成功，kbUuid: {}, title: {}, ingestModelId: {}, ingestModelName: {}",
                     entity.getKbUuid(), entity.getTitle(), entity.getIngestModelId(), entity.getIngestModelName());
        }

        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 规范化模型名称
     *
     * <p>确保返回技术标识格式的模型名称</p>
     *
     * @param modelEntity 模型实体
     * @return 规范化的模型名称
     */
    private String normalizeModelNameForKb(AiModelSourceEntity modelEntity) {
        String name = modelEntity.getName();

        if (StringUtils.isBlank(name) ||
            (!name.contains("-") && !isKnownModelNameForKb(name))) {

            String providerName = modelEntity.getProviderName();
            String provider = StringUtils.isNotBlank(providerName)
                ? providerName.toLowerCase()
                : "unknown";

            if (StringUtils.isNotBlank(modelEntity.getBaseName())) {
                return provider + "-" +
                       modelEntity.getBaseName().toLowerCase().replace(" ", "-");
            } else {
                return provider + "-model-" + modelEntity.getId();
            }
        }

        return name;
    }

    /**
     * 判断是否为知名模型名称
     *
     * @param name 模型名称
     * @return 是否为知名模型
     */
    private boolean isKnownModelNameForKb(String name) {
        if (StringUtils.isBlank(name)) {
            return false;
        }

        List<String> knownModels = List.of(
            "gpt-4-turbo", "gpt-4", "gpt-3.5-turbo",
            "claude-3-opus", "claude-3-sonnet", "claude-3-haiku",
            "gemini-pro", "gemini-1.5-pro",
            "llama-3-70b", "llama-3-8b",
            "qwen-turbo", "qwen-plus", "qwen-max"
        );

        return knownModels.contains(name.toLowerCase());
    }

    /**
     * 批量上传文档
     * 参考aideepin的uploadDocs方法
     */
    public void uploadDocs(String uuid, Boolean indexAfterUpload, MultipartFile[] files, List<String> indexTypeList) {
        if (files == null || files.length == 0) {
            return;
        }

        // 查询知识库
        LambdaQueryWrapper<AiKnowledgeBaseEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseEntity::getKbUuid, uuid);
        AiKnowledgeBaseEntity kb = knowledgeBaseMapper.selectOne(wrapper);

        if (kb == null) {
            throw new RuntimeException("知识库不存在：" + uuid);
        }

        for (MultipartFile file : files) {
            try {
                uploadDoc(uuid, indexAfterUpload, file, indexTypeList);
            } catch (Exception e) {
                log.warn("上传文档失败，fileName: {}", file.getOriginalFilename(), e);
            }
        }
    }

    /**
     * 单文档上传
     * 参考aideepin的uploadDoc方法，适配SCM架构
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

        // 2. TODO: 保存文件到外部文件服务（暂时模拟）
        String fileName = file.getOriginalFilename();
        String tenantCode = DataSourceHelper.getCurrentDataSourceName();
        String uuid = UuidUtil.createShort();
        String itemUuid = tenantCode + "::" + uuid;
        String fileUrl = "http://file.xinyirunscm.com/kb/" + kbUuid + "/" + itemUuid + "/" + fileName;

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
     * 索引知识库所有文档
     * 参考aideepin的indexing方法
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
     * 对应aideepin的indexItems方法，通过RabbitMQ异步处理
     *
     * <p>aideepin代码：</p>
     * <pre>
     * public boolean indexItems(String kbUuid, List<String> itemUuids, List<String> indexTypes) {
     *     for (String itemUuid : itemUuids) {
     *         knowledgeBaseItemService.asyncIndex(currentUser, kb, item, indexTypes);
     *     }
     * }
     * </pre>
     *
     * <p>scm-ai实现：</p>
     * 使用RabbitMQ消息队列代替@Async注解
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
            // 设置用户索引进行中标识（对应 aideepin: KnowledgeBaseItemService.asyncIndex() 第137行）
            Long currentUserId = SecurityUtil.getStaff_id();
            String userIndexKey = String.format(AiConstant.USER_INDEXING_KEY, currentUserId);
            stringRedisTemplate.opsForValue().set(userIndexKey, "", 10, TimeUnit.MINUTES);

            log.info("设置用户索引标识，userId: {}, key: {}", currentUserId, userIndexKey);

            // 发送RabbitMQ消息进行异步索引（对应aideepin的asyncIndex）
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

                // 构建消息上下文（对应aideepin的asyncIndex参数）
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

                // 发送消息（对应aideepin的knowledgeBaseItemService.asyncIndex）
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
     * <p>对应 aideepin 方法：KnowledgeBaseService.checkIndexIsFinish()</p>
     *
     * <p>aideepin实现：</p>
     * <pre>
     * public boolean checkIndexIsFinish() {
     *     String userIndexKey = MessageFormat.format(USER_INDEXING, ThreadContext.getCurrentUserId());
     *     return Boolean.FALSE.equals(stringRedisTemplate.hasKey(userIndexKey));
     * }
     * </pre>
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
     * 参考aideepin的searchMine方法
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
     * 物理删除知识库（级联删除MySQL、Elasticsearch、Neo4j三处数据）
     * <p>SCM系统统一使用物理删除</p>
     *
     * <p>删除顺序（按依赖关系反向删除）：</p>
     * <ol>
     *   <li>查询知识库是否存在</li>
     *   <li>删除Neo4j图谱数据（WHERE kb_uuid = ?）</li>
     *   <li>删除Elasticsearch向量数据（WHERE kb_uuid = ?）</li>
     *   <li>删除MySQL数据（按依赖关系）：
     *     <ul>
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

        // 3. 删除Elasticsearch向量数据
        try {
            long deletedVectors = embeddingRepository.deleteByKbUuid(uuid);
            log.info("删除Elasticsearch向量数据, kb_uuid: {}, 删除数量: {}", uuid, deletedVectors);
        } catch (Exception e) {
            log.error("删除Elasticsearch向量数据失败, kb_uuid: {}", uuid, e);
            throw new RuntimeException("删除向量数据失败: " + e.getMessage(), e);
        }

        // 4. 删除MySQL数据（按依赖关系：graph_segment → item → knowledge_base）
        try {
            // 4.1 删除graph segment数据
            int deletedSegments = graphSegmentMapper.deleteByKbUuid(uuid);
            log.info("删除graph segment数据, kb_uuid: {}, 删除数量: {}", uuid, deletedSegments);

            // 4.2 删除item数据
            int deletedItems = itemMapper.deleteByKbUuid(uuid);
            log.info("删除item数据, kb_uuid: {}, 删除数量: {}", uuid, deletedItems);

            // 4.3 删除主表
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

    /**
     * 收藏/取消收藏
     *
     * <p>对应 aideepin 方法：KnowledgeBaseService.toggleStar()</p>
     *
     * <p>aideepin实现：</p>
     * <pre>
     * public boolean toggleStar(User user, String kbUuid) {
     *     KnowledgeBase knowledgeBase = self.getOrThrow(kbUuid);
     *     boolean star;
     *     KnowledgeBaseStar oldRecord = knowledgeBaseStarRecordService.getRecord(user.getId(), kbUuid);
     *     if (null == oldRecord) {
     *         KnowledgeBaseStar starRecord = new KnowledgeBaseStar();
     *         starRecord.setUserId(user.getId());
     *         starRecord.setKbId(knowledgeBase.getId());
     *         knowledgeBaseStarRecordService.save(starRecord);
     *         star = true;
     *     } else {
     *         knowledgeBaseStarRecordService.lambdaUpdate()
     *             .eq(KnowledgeBaseStar::getId, oldRecord.getId())
     *             .set(KnowledgeBaseStar::getIsDeleted, !oldRecord.getIsDeleted())
     *             .update();
     *         star = oldRecord.getIsDeleted();
     *     }
     *     int starCount = star ? knowledgeBase.getStarCount() + 1 : knowledgeBase.getStarCount() - 1;
     *     ChainWrappers.lambdaUpdateChain(baseMapper)
     *         .eq(KnowledgeBase::getId, knowledgeBase.getId())
     *         .set(KnowledgeBase::getStarCount, starCount)
     *         .update();
     *     return star;
     * }
     * </pre>
     *
     * @param kbId 知识库ID
     * @return true表示已收藏，false表示已取消收藏
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleStar(String kbId) {
        // 1. 查询知识库（对应aideepin的getOrThrow）
        LambdaQueryWrapper<AiKnowledgeBaseEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseEntity::getId, kbId);
        AiKnowledgeBaseEntity knowledgeBase = knowledgeBaseMapper.selectOne(wrapper);

        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在，id: " + kbId);
        }

        // 2. 获取当前用户ID
        String currentUserId = SecurityUtil.getStaff_id().toString();

        boolean star;
        // 3. 查询是否已收藏（对应aideepin的getRecord）
        AiKnowledgeBaseStarEntity oldRecord = starService.getRecord(currentUserId, kbId);

        if (oldRecord == null) {
            // 4. 未收藏，创建收藏记录（对应aideepin的save）
            AiKnowledgeBaseStarEntity starRecord = new AiKnowledgeBaseStarEntity();
            starRecord.setId(UuidUtil.createShort());
            starRecord.setKbId(kbId);
            starRecord.setUserId(currentUserId);
            starRecord.setCreateTime(System.currentTimeMillis());
            starRecord.setCreateUser(SecurityUtil.getAppJwtBaseBo().getUsername());
            starService.save(starRecord);

            star = true;
            log.info("用户收藏知识库，userId: {}, kbId: {}", currentUserId, kbId);
        } else {
            // 5. 已收藏，删除收藏记录（对应aideepin的delete by id）
            starService.removeById(oldRecord.getId());
            star = false;
            log.info("用户取消收藏知识库，userId: {}, kbId: {}", currentUserId, kbId);
        }

        // 6. 更新知识库的star_count（使用 selectById + updateById 模式）
        Integer currentStarCount = knowledgeBase.getStarCount();
        if (currentStarCount == null) {
            currentStarCount = 0;
        }
        int newStarCount = star ? currentStarCount + 1 : Math.max(0, currentStarCount - 1);

        // 先查询完整实体
        AiKnowledgeBaseEntity updateEntity = knowledgeBaseMapper.selectById(knowledgeBase.getId());
        // 修改字段
        updateEntity.setStarCount(newStarCount);
        // 更新
        knowledgeBaseMapper.updateById(updateEntity);

        log.info("更新知识库收藏数，kbId: {}, oldCount: {}, newCount: {}", kbId, currentStarCount, newStarCount);

        return star;
    }

    // ==================== 辅助方法 ====================

    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }
}
