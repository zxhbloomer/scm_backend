package com.xinyirun.scm.ai.controller;

import com.xinyirun.scm.ai.bean.dto.request.AiChatRequest;
import com.xinyirun.scm.ai.bean.dto.request.AiConversationUpdateRequest;
import com.xinyirun.scm.ai.bean.entity.AiConversation;
import com.xinyirun.scm.ai.bean.entity.AiConversationContent;
import com.xinyirun.scm.ai.core.service.AiConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * AI对话控制器
 *
 * 提供AI对话的REST API接口，包括对话管理、聊天功能等
 * 从MeterSphere的AiConversationController迁移而来，适配scm-ai架构
 *
 * @Author: jianxing (原作者)
 * @CreateTime: 2025-05-28 13:44 (原创建时间)
 * @Migration: 2025-09-21 (迁移到scm-ai)
 */
@Slf4j
@Tag(name = "AI对话管理", description = "AI对话相关接口")
@RestController
@RequestMapping("/ai/conversation")
@Validated
public class AiConversationController {

    @Autowired
    private AiConversationService aiConversationService;

    /**
     * 获取用户的对话列表
     *
     * @param userId 用户ID
     * @return 对话列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取对话列表", description = "获取指定用户的所有AI对话列表")
    public List<AiConversation> list(
            @Parameter(description = "用户ID", required = true)
            @RequestParam @NotNull(message = "用户ID不能为空") String userId) {
        log.debug("获取用户对话列表 - userId: {}", userId);
        return aiConversationService.list(userId);
    }

    /**
     * 获取对话的聊天内容列表
     *
     * @param conversationId 对话ID
     * @param userId 用户ID（用于权限验证）
     * @return 聊天内容列表
     */
    @GetMapping("/chat/list/{conversationId}")
    @Operation(summary = "获取对话内容列表", description = "获取指定对话的所有聊天内容")
    public List<AiConversationContent> chatList(
            @Parameter(description = "对话ID", required = true)
            @PathVariable @NotNull(message = "对话ID不能为空") String conversationId,
            @Parameter(description = "用户ID", required = true)
            @RequestParam @NotNull(message = "用户ID不能为空") String userId) {
        log.debug("获取对话内容列表 - conversationId: {}, userId: {}", conversationId, userId);
        return aiConversationService.chatList(conversationId, userId);
    }

    /**
     * 创建新对话
     *
     * @param request 聊天请求参数
     * @return 创建的对话信息
     */
    @PostMapping("/add")
    @Operation(summary = "创建对话", description = "创建新的AI对话")
    public AiConversation add(@Valid @RequestBody AiChatRequest request) {
        log.debug("创建对话 - request: {}", request);
        return aiConversationService.add(request, request.getUser_id().toString());
    }

    /**
     * 更新对话信息
     *
     * @param request 更新请求参数
     * @return 更新后的对话信息
     */
    @PostMapping("/update")
    @Operation(summary = "更新对话", description = "更新对话标题和其他属性")
    public AiConversation update(@Valid @RequestBody AiConversationUpdateRequest request) {
        log.debug("更新对话 - request: {}", request);
        return aiConversationService.update(request, request.getUser_id().toString());
    }

    /**
     * AI聊天接口
     *
     * @param request 聊天请求参数
     * @return AI回复内容
     */
    @PostMapping("/chat")
    @Operation(summary = "AI聊天", description = "与AI进行对话聊天")
    public String chat(@Valid @RequestBody AiChatRequest request) {
        log.debug("AI聊天 - conversationId: {}, userId: {}", request.getConversation_id(), request.getUser_id());
        return aiConversationService.chat(request, request.getUser_id().toString());
    }

    /**
     * 删除对话
     *
     * @param conversationId 对话ID
     * @param userId 用户ID（用于权限验证）
     */
    @DeleteMapping("/{conversationId}")
    @Operation(summary = "删除对话", description = "删除指定的AI对话及其所有内容")
    public void delete(
            @Parameter(description = "对话ID", required = true)
            @PathVariable @NotNull(message = "对话ID不能为空") String conversationId,
            @Parameter(description = "用户ID", required = true)
            @RequestParam @NotNull(message = "用户ID不能为空") String userId) {
        log.debug("删除对话 - conversationId: {}, userId: {}", conversationId, userId);
        aiConversationService.delete(conversationId, userId);
    }

    /**
     * 获取对话详情
     *
     * @param conversationId 对话ID
     * @param userId 用户ID（用于权限验证）
     * @return 对话详情
     */
    @GetMapping("/{conversationId}")
    @Operation(summary = "获取对话详情", description = "获取指定对话的详细信息")
    public AiConversation get(
            @Parameter(description = "对话ID", required = true)
            @PathVariable @NotNull(message = "对话ID不能为空") String conversationId,
            @Parameter(description = "用户ID", required = true)
            @RequestParam @NotNull(message = "用户ID不能为空") String userId) {
        log.debug("获取对话详情 - conversationId: {}, userId: {}", conversationId, userId);
        return aiConversationService.get(conversationId, userId);
    }

    /**
     * 获取用户最近的对话列表
     *
     * @param userId 用户ID
     * @param limit 限制数量，默认10条
     * @return 最近的对话列表
     */
    @GetMapping("/recent")
    @Operation(summary = "获取最近对话列表", description = "获取用户最近的对话列表")
    public List<AiConversation> getRecentConversations(
            @Parameter(description = "用户ID", required = true)
            @RequestParam @NotNull(message = "用户ID不能为空") String userId,
            @Parameter(description = "限制数量", required = false)
            @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("获取最近对话列表 - userId: {}, limit: {}", userId, limit);
        return aiConversationService.getRecentConversations(userId, limit);
    }

    /**
     * 搜索对话
     *
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return 匹配的对话列表
     */
    @GetMapping("/search")
    @Operation(summary = "搜索对话", description = "根据关键词搜索用户的对话")
    public List<AiConversation> searchConversations(
            @Parameter(description = "用户ID", required = true)
            @RequestParam @NotNull(message = "用户ID不能为空") String userId,
            @Parameter(description = "搜索关键词", required = true)
            @RequestParam @NotNull(message = "搜索关键词不能为空") String keyword) {
        log.debug("搜索对话 - userId: {}, keyword: {}", userId, keyword);
        return aiConversationService.searchConversations(userId, keyword);
    }

    /**
     * 批量删除对话
     *
     * @param conversationIds 对话ID列表
     * @param userId 用户ID（用于权限验证）
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除对话", description = "批量删除多个AI对话")
    public void batchDelete(
            @Parameter(description = "对话ID列表", required = true)
            @RequestParam @NotNull(message = "对话ID列表不能为空") List<String> conversationIds,
            @Parameter(description = "用户ID", required = true)
            @RequestParam @NotNull(message = "用户ID不能为空") String userId) {
        log.debug("批量删除对话 - conversationIds: {}, userId: {}", conversationIds, userId);
        aiConversationService.batchDelete(conversationIds, userId);
    }
}