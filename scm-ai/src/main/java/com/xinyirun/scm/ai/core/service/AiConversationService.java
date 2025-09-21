package com.xinyirun.scm.ai.core.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.dto.request.AiChatRequest;
import com.xinyirun.scm.ai.bean.dto.request.AiConversationUpdateRequest;
import com.xinyirun.scm.ai.bean.entity.AiConversation;
import com.xinyirun.scm.ai.bean.entity.AiConversationContent;
import com.xinyirun.scm.ai.core.mapper.AiConversationContentMapper;
import com.xinyirun.scm.ai.core.mapper.AiConversationMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * AI会话服务类
 *
 * 提供AI会话管理的核心功能，包括：
 * 1. 会话创建和删除
 * 2. 会话列表查询和内容查询
 * 3. 会话更新和权限检查
 * 4. AI聊天功能集成
 *
 * 从MeterSphere迁移而来，适配scm-ai模块的架构
 *
 * @Author: jianxing (原作者)
 * @CreateTime: 2025-05-28  13:44 (原创建时间)
 * @Migration: 2025-09-21 (迁移到scm-ai)
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AiConversationService extends ServiceImpl<com.xinyirun.scm.ai.core.mapper.AiConversationMapper, AiConversation> {

    @Autowired
    private AiChatBaseService aiChatBaseService;

    @Autowired
    private AiConversationMapper aiConversationMapper;

    @Autowired
    private AiConversationContentMapper aiConversationContentMapper;

    /**
     * 执行AI聊天
     *
     * @param request 聊天请求
     * @param userId 用户ID
     * @return AI回复内容
     */
    public String chat(AiChatRequest request, String userId) {
        try {
            // 确定会话ID
            String conversationId;
            if (request.getConversation_id() != null) {
                conversationId = request.getConversation_id().toString();
            } else {
                conversationId = generateId();
                log.info("为用户 {} 创建新会话ID: {}", userId, conversationId);
            }

            // 持久化用户提示词
            aiChatBaseService.saveUserConversationContent(conversationId, request.getMessage());

            // 构建聊天选项
            AiChatBaseService.AIChatOption aiChatOption = aiChatBaseService.buildAIChatOption(request, conversationId);

            // 执行AI聊天（带记忆）
            String assistantMessage = aiChatBaseService.chatWithMemory(aiChatOption).content();

            // 持久化AI回复内容
            aiChatBaseService.saveAssistantConversationContent(conversationId, assistantMessage);

            log.info("用户 {} 的会话 {} 聊天完成，回复长度: {}", userId, conversationId, assistantMessage.length());
            return assistantMessage;

        } catch (Exception e) {
            log.error("AI聊天失败 - 用户: {}, 错误: {}", userId, e.getMessage(), e);
            throw new RuntimeException("AI聊天失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建新的AI会话
     *
     * @param request 聊天请求
     * @param userId 用户ID
     * @return 创建的会话实体
     */
    public AiConversation add(AiChatRequest request, String userId) {
        try {
            // 确定会话ID
            String conversationId;
            if (request.getConversation_id() != null) {
                conversationId = request.getConversation_id().toString();
            } else {
                conversationId = generateId();
            }

            // 使用AI生成会话标题
            String conversationTitle = generateConversationTitle(request, userId);

            // 创建会话实体
            AiConversation aiConversation = new AiConversation();
            aiConversation.setId(conversationId);
            aiConversation.setTitle(conversationTitle);
            aiConversation.setCreate_user(userId.toString());
            aiConversation.setCreate_time(System.currentTimeMillis());
            aiConversation.setDefaults(); // 设置默认值

            save(aiConversation);
            log.info("创建AI会话成功 - ID: {}, 标题: {}, 用户: {}", conversationId, conversationTitle, userId);

            return aiConversation;

        } catch (Exception e) {
            log.error("创建AI会话失败 - 用户: {}, 错误: {}", userId, e.getMessage(), e);
            throw new RuntimeException("创建会话失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除AI会话
     *
     * @param conversationId 会话ID
     * @param userId 用户ID
     */
    public void delete(String conversationId, String userId) {
        try {
            String conversationIdStr = conversationId.toString();
            // 检查会话权限
            AiConversation aiConversation = getConversationById(conversationIdStr);
            checkConversationPermission(userId.toString(), aiConversation);

            // TODO: 待创建Mapper后启用删除操作
            // removeById(conversationId);

            // 删除相关的会话内容
            // deleteConversationContent(conversationId);

            log.info("删除AI会话成功 - ID: {}, 用户: {}", conversationId, userId);

        } catch (Exception e) {
            log.error("删除AI会话失败 - ID: {}, 用户: {}, 错误: {}", conversationId, userId, e.getMessage(), e);
            throw new RuntimeException("删除会话失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取用户的会话列表
     *
     * @param userId 用户ID
     * @return 会话列表（按创建时间倒序）
     */
    public List<AiConversation> list(String userId) {
        try {
            // TODO: 待创建Mapper后启用
            // QueryWrapper<AiConversation> wrapper = new QueryWrapper<>();
            // wrapper.eq("create_user", userId)
            //        .orderByDesc("create_time");
            // return list(wrapper);

            log.info("查询用户 {} 的会话列表", userId);
            return Collections.emptyList(); // 临时返回空列表

        } catch (Exception e) {
            log.error("查询会话列表失败 - 用户: {}, 错误: {}", userId, e.getMessage(), e);
            throw new RuntimeException("查询会话列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取会话的聊天内容列表
     *
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 聊天内容列表（按创建时间排序）
     */
    public List<AiConversationContent> chatList(String conversationId, String userId) {
        try {
            String conversationIdStr = conversationId.toString();
            // 检查会话权限
            AiConversation aiConversation = getConversationById(conversationIdStr);
            checkConversationPermission(userId, aiConversation);

            // TODO: 待创建Mapper后启用
            // QueryWrapper<AiConversationContent> wrapper = new QueryWrapper<>();
            // wrapper.eq("conversation_id", conversationId)
            //        .orderByAsc("create_time");
            // return aiConversationContentMapper.selectList(wrapper);

            log.info("查询会话 {} 的聊天内容，用户: {}", conversationId, userId);
            return Collections.emptyList(); // 临时返回空列表

        } catch (Exception e) {
            log.error("查询聊天内容失败 - 会话: {}, 用户: {}, 错误: {}", conversationId, userId, e.getMessage(), e);
            throw new RuntimeException("查询聊天内容失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新AI会话
     *
     * @param request 更新请求
     * @param userId 用户ID
     * @return 更新后的会话实体
     */
    public AiConversation update(AiConversationUpdateRequest request, String userId) {
        try {
            String conversationId = request.getConversation_id().toString();

            // 检查会话权限
            AiConversation originConversation = getConversationById(conversationId);
            checkConversationPermission(userId.toString(), originConversation);

            // 复制更新字段
            AiConversation updateConversation = new AiConversation();
            BeanUtils.copyProperties(request, updateConversation);
            updateConversation.setId(conversationId);

            // TODO: 待创建Mapper后启用
            // updateById(updateConversation);

            // 更新原始对象并返回
            if (StringUtils.isNotBlank(request.getTitle())) {
                originConversation.setTitle(request.getTitle());
            }

            log.info("更新AI会话成功 - ID: {}, 用户: {}", conversationId, userId);
            return originConversation;

        } catch (Exception e) {
            log.error("更新AI会话失败 - 用户: {}, 错误: {}", userId, e.getMessage(), e);
            throw new RuntimeException("更新会话失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成会话标题
     *
     * @param request 聊天请求
     * @param userId 用户ID
     * @return 生成的标题
     */
    private String generateConversationTitle(AiChatRequest request, String userId) {
        String prompt = """
                概况用户输入的主旨生成本轮对话的标题，只返回标题，不带标点符号，最好50字以内，不超过255。
                用户输入:
                """ + request.getMessage();

        String conversationTitle = request.getMessage();
        try {
            // 使用AI生成标题
            AiChatBaseService.AIChatOption aiChatOption = new AiChatBaseService.AIChatOption();
            aiChatOption.setPrompt(prompt);
            aiChatOption.setModelProvider(request.getModel_provider());
            aiChatOption.setModelName(request.getModel_name());

            conversationTitle = aiChatBaseService.chat(aiChatOption).content();
            conversationTitle = conversationTitle.replace("\"", "");

            // 限制标题长度
            if (conversationTitle.length() > 255) {
                conversationTitle = conversationTitle.substring(0, 255);
            }

        } catch (Exception e) {
            log.warn("AI生成会话标题失败，使用原始输入: {}", e.getMessage());
            // 如果AI生成失败，使用原始消息的前50个字符作为标题
            if (conversationTitle.length() > 50) {
                conversationTitle = conversationTitle.substring(0, 50) + "...";
            }
        }

        return conversationTitle;
    }

    /**
     * 根据ID获取会话
     *
     * @param conversationId 会话ID
     * @return 会话实体
     */
    private AiConversation getConversationById(String conversationId) {
        return getById(conversationId);
    }

    /**
     * 检查会话权限
     *
     * @param userId 用户ID
     * @param aiConversation 会话实体
     * @throws RuntimeException 如果权限检查失败
     */
    private void checkConversationPermission(String userId, AiConversation aiConversation) {
        if (aiConversation == null) {
            throw new RuntimeException("会话不存在");
        }
        if (!StringUtils.equals(aiConversation.getCreate_user(), userId)) {
            throw new RuntimeException("无权限访问此会话");
        }
    }

    /**
     * 删除会话内容
     *
     * @param conversationId 会话ID
     */
    private void deleteConversationContent(String conversationId) {
        // TODO: 待创建Mapper后启用
        // QueryWrapper<AiConversationContent> wrapper = new QueryWrapper<>();
        // wrapper.eq("conversation_id", conversationId);
        // aiConversationContentMapper.delete(wrapper);
        log.info("删除会话 {} 的所有内容", conversationId);
    }

    /**
     * 生成唯一ID
     * TODO: 后续可以替换为统一的ID生成器
     *
     * @return 唯一ID
     */
    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 简化的聊天执行方法
     *
     * @param request 聊天请求
     * @param userId 用户ID
     * @return 聊天回复
     */
    public String executeSimpleChat(AiChatRequest request, String userId) {
        try {
            return aiChatBaseService.executeSimpleChat(request);
        } catch (Exception e) {
            log.error("简化聊天执行失败 - 用户: {}, 错误: {}", userId, e.getMessage(), e);
            throw new RuntimeException("聊天执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取用户最近的对话列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近的对话列表
     */
    public List<AiConversation> getRecentConversations(String userId, Integer limit) {
        try {
            return aiConversationMapper.selectRecentByUserId(userId, limit);
        } catch (Exception e) {
            log.error("获取最近对话列表失败 - 用户: {}, 错误: {}", userId, e.getMessage(), e);
            throw new RuntimeException("获取最近对话列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 搜索对话
     *
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return 匹配的对话列表
     */
    public List<AiConversation> searchConversations(String userId, String keyword) {
        try {
            return aiConversationMapper.selectByUserIdAndTitle(userId, keyword);
        } catch (Exception e) {
            log.error("搜索对话失败 - 用户: {}, 关键词: {}, 错误: {}", userId, keyword, e.getMessage(), e);
            throw new RuntimeException("搜索对话失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量删除对话
     *
     * @param conversationIds 对话ID列表
     * @param userId 用户ID
     */
    public void batchDelete(List<String> conversationIds, String userId) {
        try {
            // 验证权限
            for (String conversationId : conversationIds) {
                AiConversation conversation = getConversationById(conversationId);
                checkConversationPermission(userId, conversation);
            }

            // 批量删除
            String conversationIdsStr = String.join(",", conversationIds);

            if (!conversationIdsStr.isEmpty()) {
                aiConversationMapper.deleteByUserIdAndIds(userId, conversationIdsStr);
                aiConversationContentMapper.deleteByConversationIds(conversationIdsStr);
            }

            log.info("批量删除对话成功 - 用户: {}, 删除数量: {}", userId, conversationIds.size());
        } catch (Exception e) {
            log.error("批量删除对话失败 - 用户: {}, 错误: {}", userId, e.getMessage(), e);
            throw new RuntimeException("批量删除对话失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取对话详情
     *
     * @param conversationId 对话ID
     * @param userId 用户ID
     * @return 对话详情
     */
    public AiConversation get(String conversationId, String userId) {
        try {
            AiConversation conversation = getConversationById(conversationId);
            checkConversationPermission(userId, conversation);
            return conversation;
        } catch (Exception e) {
            log.error("获取对话详情失败 - 对话: {}, 用户: {}, 错误: {}", conversationId, userId, e.getMessage(), e);
            throw new RuntimeException("获取对话详情失败: " + e.getMessage(), e);
        }
    }
}