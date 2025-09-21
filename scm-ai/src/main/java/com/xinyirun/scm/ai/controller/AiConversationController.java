package com.xinyirun.scm.ai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.ai.entity.AiConversation;
import com.xinyirun.scm.ai.service.IAiConversationService;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * AI对话会话控制器
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
@Slf4j
@RestController
@RequestMapping("/scm/ai/conversation")
@Tag(name = "AI会话管理", description = "AI对话会话相关接口")
@Validated
public class AiConversationController {

    @Autowired
    private IAiConversationService conversationService;

    @Operation(summary = "创建会话", description = "创建新的AI对话会话")
    @PostMapping("/create")
    public ResponseEntity<JsonResultAo<AiConversation>> createConversation(
            @Parameter(description = "会话标题") @RequestParam(required = false) String title,
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId,
            @Parameter(description = "模型提供商") @RequestParam(required = false) String modelProvider,
            @Parameter(description = "模型名称") @RequestParam(required = false) String modelName) {

        try {
            AiConversation conversation = conversationService.createConversation(title, userId, modelProvider, modelName);
            log.info("创建会话成功, conversationId: {}, userId: {}", conversation.getId(), userId);
            return ResponseEntity.ok().body(ResultUtil.OK(conversation, "创建会话成功"));
        } catch (Exception e) {
            log.error("创建会话失败, userId: {}, error: {}", userId, e.getMessage(), e);
            throw new RuntimeException("创建会话失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新会话标题", description = "修改会话标题")
    @PutMapping("/{conversationId}/title")
    public ResponseEntity<JsonResultAo<Boolean>> updateConversationTitle(
            @Parameter(description = "会话ID", required = true) @PathVariable @NotNull String conversationId,
            @Parameter(description = "新标题", required = true) @RequestParam @NotNull String title,
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId) {

        try {
            Boolean result = conversationService.updateConversationTitle(conversationId, title, userId);
            if (result) {
                log.info("更新会话标题成功, conversationId: {}, title: {}", conversationId, title);
                return ResponseEntity.ok().body(ResultUtil.OK(result, "更新会话标题成功"));
            } else {
                throw new RuntimeException("更新会话标题失败");
            }
        } catch (Exception e) {
            log.error("更新会话标题失败, conversationId: {}, error: {}", conversationId, e.getMessage(), e);
            throw new RuntimeException("更新会话标题失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新会话状态", description = "修改会话状态")
    @PutMapping("/{conversationId}/status")
    public ResponseEntity<JsonResultAo<Boolean>> updateConversationStatus(
            @Parameter(description = "会话ID", required = true) @PathVariable @NotNull String conversationId,
            @Parameter(description = "新状态", required = true) @RequestParam @NotNull Integer status,
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId) {

        try {
            Boolean result = conversationService.updateConversationStatus(conversationId, status, userId);
            if (result) {
                log.info("更新会话状态成功, conversationId: {}, status: {}", conversationId, status);
                return ResponseEntity.ok().body(ResultUtil.OK(result, "更新会话状态成功"));
            } else {
                throw new RuntimeException("更新会话状态失败");
            }
        } catch (Exception e) {
            log.error("更新会话状态失败, conversationId: {}, error: {}", conversationId, e.getMessage(), e);
            throw new RuntimeException("更新会话状态失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除会话", description = "软删除会话")
    @DeleteMapping("/{conversationId}")
    public ResponseEntity<JsonResultAo<Boolean>> deleteConversation(
            @Parameter(description = "会话ID", required = true) @PathVariable @NotNull String conversationId,
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId) {

        try {
            Boolean result = conversationService.deleteConversation(conversationId, userId);
            if (result) {
                log.info("删除会话成功, conversationId: {}", conversationId);
                return ResponseEntity.ok().body(ResultUtil.OK(result, "删除会话成功"));
            } else {
                throw new RuntimeException("删除会话失败");
            }
        } catch (Exception e) {
            log.error("删除会话失败, conversationId: {}, error: {}", conversationId, e.getMessage(), e);
            throw new RuntimeException("删除会话失败: " + e.getMessage());
        }
    }

    @Operation(summary = "分页查询用户会话", description = "分页获取用户的会话列表")
    @GetMapping("/page")
    public ResponseEntity<JsonResultAo<IPage<AiConversation>>> getConversationsPage(
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size) {

        try {
            IPage<AiConversation> page = conversationService.getConversationsByUserId(userId, current, size);
            log.info("分页查询用户会话成功, userId: {}, total: {}", userId, page.getTotal());
            return ResponseEntity.ok().body(ResultUtil.OK(page));
        } catch (Exception e) {
            log.error("分页查询用户会话失败, userId: {}, error: {}", userId, e.getMessage(), e);
            throw new RuntimeException("查询失败: " + e.getMessage());
        }
    }

    @Operation(summary = "查询用户会话列表", description = "获取用户的所有会话")
    @GetMapping("/list")
    public ResponseEntity<JsonResultAo<List<AiConversation>>> getConversationsList(
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId,
            @Parameter(description = "状态筛选") @RequestParam(required = false) Integer status) {

        try {
            List<AiConversation> conversations;
            if (status != null) {
                conversations = conversationService.getConversationsByUserIdAndStatus(userId, status);
            } else {
                conversations = conversationService.getConversationsByUserId(userId);
            }
            log.info("查询用户会话列表成功, userId: {}, count: {}", userId, conversations.size());
            return ResponseEntity.ok().body(ResultUtil.OK(conversations, "查询成功"));
        } catch (Exception e) {
            log.error("查询用户会话列表失败, userId: {}, error: {}", userId, e.getMessage(), e);
            throw new RuntimeException("查询失败: " + e.getMessage());
        }
    }

    @Operation(summary = "查询最近会话", description = "获取用户最近的会话")
    @GetMapping("/recent")
    public ResponseEntity<JsonResultAo<List<AiConversation>>> getRecentConversations(
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") Integer limit) {

        try {
            List<AiConversation> conversations = conversationService.getRecentConversations(userId, limit);
            log.info("查询最近会话成功, userId: {}, count: {}", userId, conversations.size());
            return ResponseEntity.ok().body(ResultUtil.OK(conversations, "查询成功"));
        } catch (Exception e) {
            log.error("查询最近会话失败, userId: {}, error: {}", userId, e.getMessage(), e);
            throw new RuntimeException("查询失败: " + e.getMessage());
        }
    }

    @Operation(summary = "搜索会话", description = "根据关键字搜索会话")
    @GetMapping("/search")
    public ResponseEntity<JsonResultAo<List<AiConversation>>> searchConversations(
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId,
            @Parameter(description = "关键字", required = true) @RequestParam @NotNull String keyword) {

        try {
            List<AiConversation> conversations = conversationService.searchConversations(userId, keyword);
            log.info("搜索会话成功, userId: {}, keyword: {}, count: {}", userId, keyword, conversations.size());
            return ResponseEntity.ok().body(ResultUtil.OK(conversations, "搜索成功"));
        } catch (Exception e) {
            log.error("搜索会话失败, userId: {}, keyword: {}, error: {}", userId, keyword, e.getMessage(), e);
            throw new RuntimeException("搜索失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取会话详情", description = "获取会话详细信息")
    @GetMapping("/{conversationId}")
    public ResponseEntity<JsonResultAo<AiConversation>> getConversationDetail(
            @Parameter(description = "会话ID", required = true) @PathVariable @NotNull String conversationId,
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId) {

        try {
            AiConversation conversation = conversationService.getConversationDetail(conversationId, userId);
            log.info("获取会话详情成功, conversationId: {}", conversationId);
            return ResponseEntity.ok().body(ResultUtil.OK(conversation, "查询成功"));
        } catch (Exception e) {
            log.error("获取会话详情失败, conversationId: {}, error: {}", conversationId, e.getMessage(), e);
            throw new RuntimeException("查询失败: " + e.getMessage());
        }
    }

    @Operation(summary = "统计用户会话数量", description = "统计用户的会话总数")
    @GetMapping("/count")
    public ResponseEntity<JsonResultAo<Long>> countConversations(
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId) {

        try {
            Long count = conversationService.countConversationsByUserId(userId);
            log.info("统计用户会话数量成功, userId: {}, count: {}", userId, count);
            return ResponseEntity.ok().body(ResultUtil.OK(count, "统计成功"));
        } catch (Exception e) {
            log.error("统计用户会话数量失败, userId: {}, error: {}", userId, e.getMessage(), e);
            throw new RuntimeException("统计失败: " + e.getMessage());
        }
    }

    @Operation(summary = "检查会话权限", description = "检查用户是否有权限访问会话")
    @GetMapping("/{conversationId}/check-access")
    public ResponseEntity<JsonResultAo<Boolean>> checkConversationAccess(
            @Parameter(description = "会话ID", required = true) @PathVariable @NotNull String conversationId,
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId) {

        try {
            Boolean hasAccess = conversationService.checkConversationOwnership(conversationId, userId);
            log.info("检查会话权限, conversationId: {}, userId: {}, hasAccess: {}", conversationId, userId, hasAccess);
            return ResponseEntity.ok().body(ResultUtil.OK(hasAccess, "检查完成"));
        } catch (Exception e) {
            log.error("检查会话权限失败, conversationId: {}, userId: {}, error: {}", conversationId, userId, e.getMessage(), e);
            throw new RuntimeException("检查失败: " + e.getMessage());
        }
    }
}