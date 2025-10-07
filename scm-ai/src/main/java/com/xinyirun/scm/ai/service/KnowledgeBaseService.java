package com.xinyirun.scm.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseEntity;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseItemEntity;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseItemVo;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseVo;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseItemMapper;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseMapper;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.utils.UuidUtil;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseStarEntity;
import com.xinyirun.scm.ai.service.rag.AiKnowledgeBaseStarService;
import com.xinyirun.scm.mq.rabbitmq.producer.ScmMqProducer;
import com.xinyirun.scm.bean.system.ao.mqsender.MqMessageAo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final RabbitTemplate rabbitTemplate;
    private final ScmMqProducer scmMqProducer;
    private final DocumentParsingService documentParsingService;
    private final AiKnowledgeBaseStarService starService;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Redis key: 用户索引进行中标识
     * 对应 aideepin 的 RedisKeyConstant.USER_INDEXING
     */
    private static final String USER_INDEXING_KEY_PATTERN = "ai:kb:user:%s:indexing";

    /**
     * 保存或更新知识库
     */
    @Transactional(rollbackFor = Exception.class)
    public AiKnowledgeBaseVo saveOrUpdate(AiKnowledgeBaseVo vo) {
        AiKnowledgeBaseEntity entity = new AiKnowledgeBaseEntity();
        BeanUtils.copyProperties(vo, entity);

        if (entity.getKbUuid() == null || entity.getKbUuid().isEmpty()) {
            // 新增
            entity.setKbUuid(UuidUtil.createShort());
            Long currentUserId = SecurityUtil.getAppJwtBaseBo().getUser_Id();
            entity.setOwnerId(String.valueOf(currentUserId));
            knowledgeBaseMapper.insert(entity);
        } else {
            // 更新
            knowledgeBaseMapper.updateById(entity);
        }

        BeanUtils.copyProperties(entity, vo);
        return vo;
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
        String itemUuid = UuidUtil.createShort();
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

                // 构建消息上下文（对应aideepin的asyncIndex参数）
                Map<String, Object> messageContext = new HashMap<>();
                messageContext.put("item_uuid", itemUuid);
                messageContext.put("kb_uuid", item.getKbUuid());
                messageContext.put("file_url", "");
                messageContext.put("file_name", item.getSourceFileName());
                messageContext.put("index_types", indexTypes);

                // 构建MqMessageAo（消息体，使用Builder模式）
                MqMessageAo mqMessageAo = MqMessageAo.builder()
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
        Long currentUserId = SecurityUtil.getAppJwtBaseBo().getUser_Id();
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

        Long currentUserId = SecurityUtil.getAppJwtBaseBo().getUser_Id();
        wrapper.eq(AiKnowledgeBaseEntity::getOwnerId, currentUserId);

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(AiKnowledgeBaseEntity::getTitle, keyword);
        }

        // TODO: 如果includeOthersPublic=true，还需要查询其他人公开的知识库

        wrapper.orderByDesc(AiKnowledgeBaseEntity::getCreateTime);

        IPage<AiKnowledgeBaseEntity> entityPage = knowledgeBaseMapper.selectPage(page, wrapper);

        return entityPage.convert(entity -> {
            AiKnowledgeBaseVo vo = new AiKnowledgeBaseVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        });
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

        wrapper.orderByDesc(AiKnowledgeBaseEntity::getStarCount, AiKnowledgeBaseEntity::getCreateTime);

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
     * 软删除知识库
     * 参考aideepin的softDelete方法
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean softDelete(String uuid) {
        LambdaQueryWrapper<AiKnowledgeBaseEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseEntity::getKbUuid, uuid);

        return knowledgeBaseMapper.delete(wrapper) > 0;
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
        String currentUserId = SecurityUtil.getAppJwtBaseBo().getUser_Id().toString();

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

        // 6. 更新知识库的star_count（对应aideepin的update）
        Integer currentStarCount = knowledgeBase.getStarCount();
        if (currentStarCount == null) {
            currentStarCount = 0;
        }
        int newStarCount = star ? currentStarCount + 1 : Math.max(0, currentStarCount - 1);

        LambdaQueryWrapper<AiKnowledgeBaseEntity> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(AiKnowledgeBaseEntity::getId, knowledgeBase.getId());

        AiKnowledgeBaseEntity updateEntity = new AiKnowledgeBaseEntity();
        updateEntity.setStarCount(newStarCount);
        knowledgeBaseMapper.update(updateEntity, updateWrapper);

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
