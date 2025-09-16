package com.xinyirun.scm.ai.controller;

import com.xinyirun.scm.ai.config.properties.ScmAiProperties;
import com.xinyirun.scm.ai.service.ScmChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/ai")
@PreAuthorize("hasRole('ADMIN')")
public class AiAdminController {

    @Autowired
    private ScmAiProperties aiProperties;

    @Autowired
    private ScmChatService chatService;

    /**
     * 获取当前AI配置
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getCurrentConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("primaryProvider", aiProperties.getPrimaryProvider());
        config.put("fallbackProvider", aiProperties.getFallbackProvider());
        config.put("mcpEnabled", aiProperties.getMcp().isEnabled());
        return ResponseEntity.ok(config);
    }

    /**
     * 切换主要厂商
     */
    @PostMapping("/switch-provider")
    public ResponseEntity<String> switchProvider(@RequestParam String provider) {
        log.info("管理员切换AI厂商: {} -> {}", aiProperties.getPrimaryProvider(), provider);
        aiProperties.setPrimaryProvider(provider);
        return ResponseEntity.ok("AI厂商已切换到: " + provider);
    }

    /**
     * 测试AI服务可用性
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> testAiService() {
        Map<String, Object> result = new HashMap<>();

        try {
            String testMessage = "请回复'测试成功'";
            String response = chatService.chatWithFallback(testMessage);

            result.put("status", "success");
            result.put("response", response);
            result.put("provider", aiProperties.getPrimaryProvider());

        } catch (Exception e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 检查各厂商状态
     */
    @GetMapping("/providers/status")
    public ResponseEntity<Map<String, String>> checkProvidersStatus() {
        Map<String, String> status = new HashMap<>();

        // 这里可以实现各厂商的健康检查逻辑
        status.put("openai", "unknown");
        status.put("anthropic", "unknown");
        status.put("zhipuai", "unknown");

        // TODO: 实现实际的健康检查

        return ResponseEntity.ok(status);
    }
}