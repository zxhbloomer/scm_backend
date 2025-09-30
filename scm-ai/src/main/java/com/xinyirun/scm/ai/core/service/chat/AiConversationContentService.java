package com.xinyirun.scm.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentEntity;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationContentVo;
import com.xinyirun.scm.ai.mapper.chat.AiConversationContentMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI对话内容服务
 *
 * 提供AI对话内容管理功能，包括消息的创建、查询等操作
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Slf4j
public class AiConversationContentService {

    @Resource
    private AiConversationContentMapper aiConversationContentMapper;

    /**
     * 根据对话ID查询内容列表
     *
     * @param conversationId 对话ID
     * @return 对话内容列表
     */
    public List<AiConversationContentVo> getByConversationId(String conversationId) {
        try {
            QueryWrapper<AiConversationContentEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("conversation_id", conversationId);
            wrapper.orderByAsc("c_time");

            List<AiConversationContentEntity> entities = aiConversationContentMapper.selectList(wrapper);
            return entities.stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据对话ID查询内容列表失败, conversationId: {}", conversationId, e);
            return List.of();
        }
    }

    /**
     * 添加对话内容
     *
     * @param contentVo 对话内容VO
     * @param operatorId 操作员ID
     * @return 创建的对话内容VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiConversationContentVo addContent(AiConversationContentVo contentVo, Long operatorId) {
        try {
            AiConversationContentEntity entity = convertToEntity(contentVo);

            LocalDateTime now = LocalDateTime.now();

            int result = aiConversationContentMapper.insert(entity);
            if (result > 0) {
                log.info("添加对话内容成功, conversationId: {}, messageType: {}",
                        entity.getConversation_id(), entity.getType());
                return convertToVo(entity);
            }

            return null;
        } catch (Exception e) {
            log.error("添加对话内容失败", e);
            throw new RuntimeException("添加对话内容失败", e);
        }
    }

    /**
     * 保存对话内容（包含模型信息）
     *
     * @param conversationId 对话ID
     * @param type 内容类型
     * @param content 内容
     * @param modelSourceId 模型源ID
     * @param providerName AI提供商名称
     * @param baseName 基础模型名称
     * @param operatorId 操作员ID
     * @return 保存的对话内容VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiConversationContentVo saveConversationContent(String conversationId, String type, String content,
                                                          String modelSourceId, String providerName, String baseName, Long operatorId) {
        try {
            AiConversationContentEntity entity = new AiConversationContentEntity();
            entity.setConversation_id(conversationId);
            entity.setType(type);
            entity.setContent(content);
            entity.setModel_source_id(modelSourceId);
            entity.setProvider_name(providerName);
            entity.setBase_name(baseName);

            int result = aiConversationContentMapper.insert(entity);
            if (result > 0) {
                log.info("保存对话内容成功, conversationId: {}, type: {}, provider: {}, model: {}",
                        conversationId, type, providerName, baseName);
                return convertToVo(entity);
            }

            return null;
        } catch (Exception e) {
            log.error("保存对话内容失败, conversationId: {}, provider: {}, model: {}",
                    conversationId, providerName, baseName, e);
            throw new RuntimeException("保存对话内容失败", e);
        }
    }

    /**
     * 根据ID查询对话内容
     *
     * @param id 内容ID
     * @return 对话内容VO
     */
    public AiConversationContentVo getById(Integer id) {
        try {
            AiConversationContentEntity entity = aiConversationContentMapper.selectById(id);
            if (entity != null) {
                return convertToVo(entity);
            }
            return null;
        } catch (Exception e) {
            log.error("根据ID查询对话内容失败, id: {}", id, e);
            return null;
        }
    }

    /**
     * 根据消息类型查询对话内容
     *
     * @param conversationId 对话ID
     * @param messageType 消息类型
     * @return 对话内容列表
     */
    public List<AiConversationContentVo> getByMessageType(String conversationId, String messageType) {
        try {
            QueryWrapper<AiConversationContentEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("conversation_id", conversationId);
            wrapper.eq("message_type", messageType);
            wrapper.orderByAsc("c_time");

            List<AiConversationContentEntity> entities = aiConversationContentMapper.selectList(wrapper);
            return entities.stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据消息类型查询对话内容失败, conversationId: {}, messageType: {}",
                    conversationId, messageType, e);
            return List.of();
        }
    }

    /**
     * 删除对话内容
     *
     * @param id 内容ID
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteContent(Integer id) {
        try {
            int result = aiConversationContentMapper.deleteById(id);
            if (result > 0) {
                log.info("删除对话内容成功, id: {}", id);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除对话内容失败, id: {}", id, e);
            throw new RuntimeException("删除对话内容失败", e);
        }
    }

    /**
     * 根据对话ID删除所有内容
     *
     * @param conversationId 对话ID
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByConversationId(String conversationId) {
        try {
            QueryWrapper<AiConversationContentEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("conversation_id", conversationId);

            int result = aiConversationContentMapper.delete(wrapper);
            log.info("删除对话所有内容成功, conversationId: {}, 删除数量: {}", conversationId, result);
            return true;
        } catch (Exception e) {
            log.error("删除对话所有内容失败, conversationId: {}", conversationId, e);
            throw new RuntimeException("删除对话所有内容失败", e);
        }
    }

    /**
     * Entity转VO
     */
    private AiConversationContentVo convertToVo(AiConversationContentEntity entity) {
        AiConversationContentVo vo = new AiConversationContentVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * VO转Entity
     */
    private AiConversationContentEntity convertToEntity(AiConversationContentVo vo) {
        AiConversationContentEntity entity = new AiConversationContentEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }
}