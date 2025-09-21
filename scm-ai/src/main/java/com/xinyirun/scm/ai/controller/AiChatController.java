package com.xinyirun.scm.ai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.ai.entity.AiConversationContent;
import com.xinyirun.scm.ai.entity.AiConversation;
import com.xinyirun.scm.ai.service.IAiChatBusinessService;
import com.xinyirun.scm.ai.service.IAiConversationContentService;
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
 * AI聊天控制器
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
@Slf4j
@RestController
@RequestMapping("/scm/ai/chat")
@Tag(name = "AI聊天", description = "AI聊天对话相关接口")
@Validated
public class AiChatController {

    @Autowired
    private IAiChatBusinessService chatBusinessService;

    @Autowired
    private IAiConversationContentService contentService;

    @Operation(summary = "发送消息", description = "发送消息并获取AI回复")
    @PostMapping("/send")
    public ResponseEntity<JsonResultAo<AiConversationContent>> sendMessage(
            @Parameter(description = "会话ID（可为空，自动创建新会话）") @RequestParam(required = false) String conversationId,
            @Parameter(description = "消息内容", required = true) @RequestParam @NotNull String message,
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId,
            @Parameter(description = "模型提供商") @RequestParam(required = false) String modelProvider,
            @Parameter(description = "模型名称") @RequestParam(required = false) String modelName) {

        try {
            AiConversationContent response = chatBusinessService.sendMessage(
                    conversationId, message, userId, modelProvider, modelName);
            log.info("发送消息成功, conversationId: {}, messageId: {}",
                    response.getConversation_id(), response.getId());
            return ResponseEntity.ok().body(ResultUtil.OK(response, "消息发送成功"));
        } catch (Exception e) {
            log.error("发送消息失败, conversationId: {}, userId: {}, error: {}",
                    conversationId, userId, e.getMessage(), e);
            throw new RuntimeException("消息发送失败: " + e.getMessage());
        }
    }

    @Operation(summary = "开始新对话", description = "创建新会话并发送第一条消息")
    @PostMapping("/start")
    public ResponseEntity<JsonResultAo<AiConversationContent>>startNewConversation(
            @Parameter(description = "会话标题") @RequestParam(required = false) String title,
            @Parameter(description = "消息内容", required = true) @RequestParam @NotNull String message,
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId,
            @Parameter(description = "模型提供商") @RequestParam(required = false) String modelProvider,
            @Parameter(description = "模型名称") @RequestParam(required = false) String modelName) {

        try {
            AiConversationContent response = chatBusinessService.startNewConversation(
                    title, message, userId, modelProvider, modelName);
            log.info("开始新对话成功, conversationId: {}, messageId: {}",
                    response.getConversation_id(), response.getId());
            return ResponseEntity.ok().body(ResultUtil.OK(response, "新对话创建成功"));
        } catch (Exception e) {
            log.error("开始新对话失败, userId: {}, error: {}", userId, e.getMessage(), e);
            throw new RuntimeException("新对话创建失败: " + e.getMessage());
        }
    }

    @Operation(summary = "继续对话", description = "在现有会话中发送消息")
    @PostMapping("/continue")
    public ResponseEntity<JsonResultAo<AiConversationContent>>continueConversation(
            @Parameter(description = "会话ID", required = true) @RequestParam @NotNull String conversationId,
            @Parameter(description = "消息内容", required = true) @RequestParam @NotNull String message,
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId) {

        try {
            AiConversationContent response = chatBusinessService.continueConversation(
                    conversationId, message, userId);
            log.info("继续对话成功, conversationId: {}, messageId: {}",
                    conversationId, response.getId());
            return ResponseEntity.ok().body(ResultUtil.OK(response, "消息发送成功"));
        } catch (Exception e) {
            log.error("继续对话失败, conversationId: {}, userId: {}, error: {}",
                    conversationId, userId, e.getMessage(), e);
            throw new RuntimeException("消息发送失败: " + e.getMessage());
        }
    }

    @Operation(summary = "重新生成回复", description = "重新生成AI回复")
    @PostMapping("/regenerate")
    public ResponseEntity<JsonResultAo<AiConversationContent>>regenerateResponse(
            @Parameter(description = "会话ID", required = true) @RequestParam @NotNull String conversationId,
            @Parameter(description = "消息ID", required = true) @RequestParam @NotNull Long messageId,
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId) {

        try {
            AiConversationContent response = chatBusinessService.regenerateResponse(
                    conversationId, messageId, userId);
            log.info("重新生成回复成功, conversationId: {}, originalMessageId: {}, newMessageId: {}",
                    conversationId, messageId, response.getId());
            return ResponseEntity.ok().body(ResultUtil.OK(response, "重新生成成功"));
        } catch (Exception e) {
            log.error("重新生成回复失败, conversationId: {}, messageId: {}, error: {}",
                    conversationId, messageId, e.getMessage(), e);
            throw new RuntimeException("重新生成失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取对话历史", description = "获取完整的对话历史")
    @GetMapping("/history/{conversationId}")
    public ResponseEntity<JsonResultAo<AiConversation>>getConversationHistory(
            @Parameter(description = "会话ID", required = true) @PathVariable @NotNull String conversationId,
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId) {

        try {
            AiConversation conversation = chatBusinessService.getConversationHistory(conversationId, userId);
            log.info("获取对话历史成功, conversationId: {}", conversationId);
            return ResponseEntity.ok().body(ResultUtil.OK(conversation, "获取成功"));
        } catch (Exception e) {
            log.error("获取对话历史失败, conversationId: {}, userId: {}, error: {}",
                    conversationId, userId, e.getMessage(), e);
            throw new RuntimeException("获取失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取会话消息列表", description = "分页获取会话的消息列表")
    @GetMapping("/messages/{conversationId}")
    public ResponseEntity<JsonResultAo<IPage<AiConversationContent>>>getConversationMessages(
            @Parameter(description = "会话ID", required = true) @PathVariable @NotNull String conversationId,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Long size) {

        try {
            IPage<AiConversationContent> page = contentService.getContentsByConversationId(
                    conversationId, current, size);
            log.info("获取会话消息列表成功, conversationId: {}, total: {}", conversationId, page.getTotal());
            return ResponseEntity.ok().body(ResultUtil.OK(page));
        } catch (Exception e) {
            log.error("获取会话消息列表失败, conversationId: {}, error: {}",
                    conversationId, e.getMessage(), e);
            throw new RuntimeException("获取失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取最近消息", description = "获取会话的最近N条消息")
    @GetMapping("/recent/{conversationId}")
    public ResponseEntity<JsonResultAo<List<AiConversationContent>>>getRecentMessages(
            @Parameter(description = "会话ID", required = true) @PathVariable @NotNull String conversationId,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "20") Integer limit) {

        try {
            List<AiConversationContent> messages = contentService.getRecentMessages(conversationId, limit);
            log.info("获取最近消息成功, conversationId: {}, count: {}", conversationId, messages.size());
            return ResponseEntity.ok().body(ResultUtil.OK(messages, "获取成功"));
        } catch (Exception e) {
            log.error("获取最近消息失败, conversationId: {}, error: {}",
                    conversationId, e.getMessage(), e);
            throw new RuntimeException("获取失败: " + e.getMessage());
        }
    }

    @Operation(summary = "搜索消息", description = "在会话中搜索消息")
    @GetMapping("/search/{conversationId}")
    public ResponseEntity<JsonResultAo<List<AiConversationContent>>>searchMessages(
            @Parameter(description = "会话ID", required = true) @PathVariable @NotNull String conversationId,
            @Parameter(description = "搜索关键字", required = true) @RequestParam @NotNull String keyword) {

        try {
            List<AiConversationContent> messages = contentService.searchMessages(conversationId, keyword);
            log.info("搜索消息成功, conversationId: {}, keyword: {}, count: {}",
                    conversationId, keyword, messages.size());
            return ResponseEntity.ok().body(ResultUtil.OK(messages, "搜索成功"));
        } catch (Exception e) {
            log.error("搜索消息失败, conversationId: {}, keyword: {}, error: {}",
                    conversationId, keyword, e.getMessage(), e);
            throw new RuntimeException("搜索失败: " + e.getMessage());
        }
    }

    @Operation(summary = "清空对话", description = "清空会话的所有消息")
    @DeleteMapping("/clear/{conversationId}")
    public ResponseEntity<JsonResultAo<Boolean>>clearConversation(
            @Parameter(description = "会话ID", required = true) @PathVariable @NotNull String conversationId,
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId) {

        try {
            Boolean result = chatBusinessService.clearConversation(conversationId, userId);
            if (result) {
                log.info("清空对话成功, conversationId: {}", conversationId);
                return ResponseEntity.ok().body(ResultUtil.OK(result, "清空成功"));
            } else {
                throw new RuntimeException("清空失败");
            }
        } catch (Exception e) {
            log.error("清空对话失败, conversationId: {}, error: {}", conversationId, e.getMessage(), e);
            throw new RuntimeException("清空失败: " + e.getMessage());
        }
    }

    @Operation(summary = "导出对话", description = "导出会话内容")
    @GetMapping("/export/{conversationId}")
    public ResponseEntity<JsonResultAo<String>>exportConversation(
            @Parameter(description = "会话ID", required = true) @PathVariable @NotNull String conversationId,
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId,
            @Parameter(description = "导出格式") @RequestParam(defaultValue = "txt") String format) {

        try {
            String exportContent = chatBusinessService.exportConversation(conversationId, userId, format);
            log.info("导出对话成功, conversationId: {}, format: {}", conversationId, format);
            return ResponseEntity.ok().body(ResultUtil.OK(exportContent, "导出成功"));
        } catch (Exception e) {
            log.error("导出对话失败, conversationId: {}, format: {}, error: {}",
                    conversationId, format, e.getMessage(), e);
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取对话统计", description = "获取会话的统计信息")
    @GetMapping("/statistics/{conversationId}")
    public ResponseEntity<JsonResultAo<String>>getConversationStatistics(
            @Parameter(description = "会话ID", required = true) @PathVariable @NotNull String conversationId,
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId) {

        try {
            String statistics = chatBusinessService.getConversationStatistics(conversationId, userId);
            log.info("获取对话统计成功, conversationId: {}", conversationId);
            return ResponseEntity.ok().body(ResultUtil.OK(statistics, "获取成功"));
        } catch (Exception e) {
            log.error("获取对话统计失败, conversationId: {}, error: {}",
                    conversationId, e.getMessage(), e);
            throw new RuntimeException("获取失败: " + e.getMessage());
        }
    }

    @Operation(summary = "自动生成标题", description = "根据对话内容自动生成会话标题")
    @PostMapping("/generate-title/{conversationId}")
    public ResponseEntity<JsonResultAo<String>>generateConversationTitle(
            @Parameter(description = "会话ID", required = true) @PathVariable @NotNull String conversationId,
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId) {

        try {
            String title = chatBusinessService.generateConversationTitle(conversationId, userId);
            log.info("自动生成标题成功, conversationId: {}, title: {}", conversationId, title);
            return ResponseEntity.ok().body(ResultUtil.OK(title, "生成成功"));
        } catch (Exception e) {
            log.error("自动生成标题失败, conversationId: {}, error: {}",
                    conversationId, e.getMessage(), e);
            throw new RuntimeException("生成失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取推荐问题", description = "获取推荐的后续问题")
    @GetMapping("/suggestions/{conversationId}")
    public ResponseEntity<JsonResultAo<String>>getSuggestedQuestions(
            @Parameter(description = "会话ID", required = true) @PathVariable @NotNull String conversationId,
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId) {

        try {
            String suggestions = chatBusinessService.getSuggestedQuestions(conversationId, userId);
            log.info("获取推荐问题成功, conversationId: {}", conversationId);
            return ResponseEntity.ok().body(ResultUtil.OK(suggestions, "获取成功"));
        } catch (Exception e) {
            log.error("获取推荐问题失败, conversationId: {}, error: {}",
                    conversationId, e.getMessage(), e);
            throw new RuntimeException("获取失败: " + e.getMessage());
        }
    }

    @Operation(summary = "验证访问权限", description = "验证用户是否有权限访问会话")
    @GetMapping("/validate-access/{conversationId}")
    public ResponseEntity<JsonResultAo<Boolean>>validateConversationAccess(
            @Parameter(description = "会话ID", required = true) @PathVariable @NotNull String conversationId,
            @Parameter(description = "用户ID", required = true) @RequestParam @NotNull Long userId) {

        try {
            Boolean hasAccess = chatBusinessService.validateConversationAccess(conversationId, userId);
            log.info("验证访问权限, conversationId: {}, userId: {}, hasAccess: {}",
                    conversationId, userId, hasAccess);
            return ResponseEntity.ok().body(ResultUtil.OK(hasAccess, "验证完成"));
        } catch (Exception e) {
            log.error("验证访问权限失败, conversationId: {}, userId: {}, error: {}",
                    conversationId, userId, e.getMessage(), e);
            throw new RuntimeException("验证失败: " + e.getMessage());
        }
    }
}