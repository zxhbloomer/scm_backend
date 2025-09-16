package com.xinyirun.scm.ai.controller;

import com.xinyirun.scm.ai.entity.AiChatRecord;
import com.xinyirun.scm.ai.service.ScmChatService;
import com.xinyirun.scm.ai.service.ScmTokenUsageService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SCM AI聊天控制器
 * 
 * 提供AI聊天功能的REST API接口，包括：
 * 1. 聊天对话接口 - 发送消息给AI并获取回复
 * 2. 聊天历史查询 - 获取用户的聊天记录
 * 3. 会话管理 - 创建、查询、删除聊天会话
 * 4. 文件上传支持 - 聊天中上传文件
 * 5. Token使用统计 - 查询AI使用情况和成本
 * 
 * 安全特性：
 * - 基于用户身份的数据隔离
 * - 租户级别的数据权限控制
 * - 请求频率限制和异常处理
 * - 文件上传安全验证
 * 
 * @author SCM-AI模块
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/ai/chat")
@PreAuthorize("isAuthenticated()")
public class AiChatController {

    @Autowired
    private ScmChatService chatService;
    
    @Autowired
    private ScmTokenUsageService tokenUsageService;

    /**
     * 发送聊天消息
     * 
     * 用户向AI发送消息并获取回复，支持文本消息和文件附件
     * 
     * @param request 聊天请求参数
     * @param files 附件文件（可选）
     * @param httpRequest HTTP请求对象（用于获取用户信息）
     * @return AI回复结果
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestParam String tenantId,
            @RequestParam Long userId, 
            @RequestParam String pageCode,
            @RequestParam String message,
            @RequestParam(required = false) String sessionId,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            HttpServletRequest httpRequest) {
        
        log.info("接收聊天消息 - 租户: {}, 用户: {}, 页面: {}, 消息长度: {}", 
                tenantId, userId, pageCode, message != null ? message.length() : 0);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 参数验证
            if (tenantId == null || tenantId.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "租户ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用户ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (pageCode == null || pageCode.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "页面代码不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (message == null || message.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "消息内容不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 调用聊天服务
            String aiResponse = chatService.sendMessage(tenantId, userId, pageCode, sessionId, message, files);
            
            // 构建成功响应
            response.put("success", true);
            response.put("message", "消息发送成功");
            response.put("aiResponse", aiResponse);
            response.put("timestamp", System.currentTimeMillis());
            
            // 如果是新会话，返回会话ID
            if (sessionId == null || sessionId.trim().isEmpty()) {
                // 从AI服务中获取生成的会话ID
                sessionId = AiChatRecord.generateSessionId(tenantId, userId, pageCode);
                response.put("sessionId", sessionId);
            }
            
            log.info("聊天消息处理成功 - 租户: {}, 用户: {}", tenantId, userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("聊天消息处理异常 - 租户: {}, 用户: {}, 错误: {}", tenantId, userId, e.getMessage(), e);
            response.put("success", false);
            response.put("message", "消息处理失败: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取聊天历史记录
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param pageCode 页面代码
     * @param page 页号（从0开始）
     * @param size 每页大小
     * @return 聊天历史记录
     */
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getChatHistory(
            @RequestParam String tenantId,
            @RequestParam Long userId,
            @RequestParam String pageCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("查询聊天历史 - 租户: {}, 用户: {}, 页面: {}, 页号: {}, 大小: {}", 
                tenantId, userId, pageCode, page, size);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 参数验证
            if (size > 100) {
                size = 100; // 限制最大页面大小
            }
            
            Page<AiChatRecord> chatHistory = chatService.getChatHistory(tenantId, userId, pageCode, page, size);
            
            response.put("success", true);
            response.put("message", "聊天历史查询成功");
            response.put("content", chatHistory.getContent());
            response.put("totalElements", chatHistory.getTotalElements());
            response.put("totalPages", chatHistory.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);
            response.put("hasNext", chatHistory.hasNext());
            response.put("hasPrevious", chatHistory.hasPrevious());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("聊天历史查询异常 - 租户: {}, 用户: {}, 错误: {}", tenantId, userId, e.getMessage(), e);
            response.put("success", false);
            response.put("message", "聊天历史查询失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取会话聊天记录
     * 
     * @param sessionId 会话ID
     * @return 会话聊天记录
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<Map<String, Object>> getSessionHistory(@PathVariable String sessionId) {
        log.info("查询会话历史 - 会话ID: {}", sessionId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<AiChatRecord> sessionHistory = chatService.getSessionHistory(sessionId);
            
            response.put("success", true);
            response.put("message", "会话历史查询成功");
            response.put("sessionId", sessionId);
            response.put("messages", sessionHistory);
            response.put("messageCount", sessionHistory.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("会话历史查询异常 - 会话ID: {}, 错误: {}", sessionId, e.getMessage(), e);
            response.put("success", false);
            response.put("message", "会话历史查询失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除会话
     * 
     * @param sessionId 会话ID
     * @return 删除结果
     */
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Map<String, Object>> deleteSession(@PathVariable String sessionId) {
        log.info("删除会话 - 会话ID: {}", sessionId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            long deletedCount = chatService.deleteSession(sessionId);
            
            response.put("success", true);
            response.put("message", "会话删除成功");
            response.put("sessionId", sessionId);
            response.put("deletedMessageCount", deletedCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("会话删除异常 - 会话ID: {}, 错误: {}", sessionId, e.getMessage(), e);
            response.put("success", false);
            response.put("message", "会话删除失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取聊天统计信息
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param pageCode 页面代码
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getChatStatistics(
            @RequestParam String tenantId,
            @RequestParam Long userId,
            @RequestParam String pageCode) {
        
        log.info("查询聊天统计 - 租户: {}, 用户: {}, 页面: {}", tenantId, userId, pageCode);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            ScmChatService.ChatStatistics stats = chatService.getChatStatistics(tenantId, userId, pageCode);
            
            response.put("success", true);
            response.put("message", "统计信息查询成功");
            response.put("statistics", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("聊天统计查询异常 - 租户: {}, 用户: {}, 错误: {}", tenantId, userId, e.getMessage(), e);
            response.put("success", false);
            response.put("message", "统计信息查询失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取Token使用统计
     * 
     * @param tenantId 租户ID
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return Token使用统计
     */
    @GetMapping("/token-usage")
    public ResponseEntity<Map<String, Object>> getTokenUsage(
            @RequestParam String tenantId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        log.info("查询Token使用统计 - 租户: {}, 开始日期: {}, 结束日期: {}", tenantId, startDate, endDate);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 处理日期参数，默认查询最近30天
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(30);
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
            
            ScmTokenUsageService.TenantTokenUsageStats stats = 
                tokenUsageService.getTenantTokenUsage(tenantId, start, end);
            
            response.put("success", true);
            response.put("message", "Token使用统计查询成功");
            response.put("statistics", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Token使用统计查询异常 - 租户: {}, 错误: {}", tenantId, e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Token使用统计查询失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取用户Token使用统计
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 用户Token使用统计
     */
    @GetMapping("/user-token-usage")
    public ResponseEntity<Map<String, Object>> getUserTokenUsage(
            @RequestParam String tenantId,
            @RequestParam Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        log.info("查询用户Token使用统计 - 租户: {}, 用户: {}, 开始日期: {}, 结束日期: {}", 
                tenantId, userId, startDate, endDate);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(30);
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
            
            ScmTokenUsageService.UserTokenUsageStats stats = 
                tokenUsageService.getUserTokenUsage(tenantId, userId, start, end);
            
            response.put("success", true);
            response.put("message", "用户Token使用统计查询成功");
            response.put("statistics", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("用户Token使用统计查询异常 - 租户: {}, 用户: {}, 错误: {}", tenantId, userId, e.getMessage(), e);
            response.put("success", false);
            response.put("message", "用户Token使用统计查询失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 检查Token使用限制
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param pageCode 页面代码
     * @param dailyLimit 每日限制（可选）
     * @param monthlyLimit 每月限制（可选）
     * @return 限制检查结果
     */
    @GetMapping("/token-limit-check")
    public ResponseEntity<Map<String, Object>> checkTokenLimit(
            @RequestParam String tenantId,
            @RequestParam Long userId,
            @RequestParam String pageCode,
            @RequestParam(required = false, defaultValue = "0") long dailyLimit,
            @RequestParam(required = false, defaultValue = "0") long monthlyLimit) {
        
        log.info("检查Token使用限制 - 租户: {}, 用户: {}, 页面: {}, 每日限制: {}, 每月限制: {}", 
                tenantId, userId, pageCode, dailyLimit, monthlyLimit);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            ScmTokenUsageService.TokenLimitCheckResult result = 
                tokenUsageService.checkTokenLimit(tenantId, userId, pageCode, dailyLimit, monthlyLimit);
            
            response.put("success", true);
            response.put("message", "Token限制检查完成");
            response.put("limitCheck", result);
            response.put("canUse", !result.isLimitExceeded());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Token限制检查异常 - 租户: {}, 用户: {}, 错误: {}", tenantId, userId, e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Token限制检查失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取最近聊天上下文
     * 
     * 用于AI理解对话历史
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param pageCode 页面代码
     * @param limit 限制数量
     * @return 最近的聊天记录
     */
    @GetMapping("/recent-context")
    public ResponseEntity<Map<String, Object>> getRecentContext(
            @RequestParam String tenantId,
            @RequestParam Long userId,
            @RequestParam String pageCode,
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("查询最近聊天上下文 - 租户: {}, 用户: {}, 页面: {}, 限制: {}", 
                tenantId, userId, pageCode, limit);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<AiChatRecord> context = chatService.getRecentContext(tenantId, userId, pageCode, limit);
            
            response.put("success", true);
            response.put("message", "最近聊天上下文查询成功");
            response.put("context", context);
            response.put("contextSize", context.size());
            response.put("limit", limit);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("最近聊天上下文查询异常 - 租户: {}, 用户: {}, 错误: {}", tenantId, userId, e.getMessage(), e);
            response.put("success", false);
            response.put("message", "最近聊天上下文查询失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}