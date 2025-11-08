package com.xinyirun.scm.ai.controller.chat;

import com.xinyirun.scm.ai.bean.entity.chat.AiConversationPresetEntity;
import com.xinyirun.scm.ai.common.constant.AiMessageTypeConstant;
import com.xinyirun.scm.ai.core.service.chat.AiConversationPresetService;
import com.xinyirun.scm.ai.core.service.chat.AiConversationPresetRelService;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI对话预设控制器
 *
 * 提供AI角色预设管理功能的REST API接口，包括系统预设和用户自定义预设的CRUD操作
 *
 * @author SCM-AI团队
 * @since 2025-11-08
 */
@Slf4j
@Tag(name = "AI对话预设")
@RestController
@RequestMapping(value = "/api/v1/ai/conversation/preset")
public class AiConversationPresetController {

    @Resource
    private AiConversationPresetService presetService;

    @Resource
    private AiConversationPresetRelService presetRelService;

    /**
     * 获取公开预设列表
     */
    @GetMapping(value = "/list/public")
    @Operation(summary = "公开预设列表")
    @SysLogAnnotion("获取公开预设列表")
    public ResponseEntity<List<AiConversationPresetEntity>> listPublic() {
        List<AiConversationPresetEntity> presets = presetService.listPublicPresets();
        return ResponseEntity.ok(presets);
    }

    /**
     * 获取用户自定义预设列表
     */
    @GetMapping(value = "/list/user")
    @Operation(summary = "用户自定义预设列表")
    @SysLogAnnotion("获取用户自定义预设列表")
    public ResponseEntity<List<AiConversationPresetEntity>> listUser() {
        Long userId = SecurityUtil.getStaff_id();
        List<AiConversationPresetEntity> presets = presetService.listUserPresets(userId);
        return ResponseEntity.ok(presets);
    }

    /**
     * 获取预设详情
     */
    @GetMapping(value = "/detail/{uuid}")
    @Operation(summary = "预设详情")
    @SysLogAnnotion("获取预设详情")
    public ResponseEntity<AiConversationPresetEntity> detail(@PathVariable("uuid") String uuid) {
        AiConversationPresetEntity preset = presetService.getByUuid(uuid);
        return ResponseEntity.ok(preset);
    }

    /**
     * 创建用户自定义预设
     */
    @PostMapping(value = "/create")
    @Operation(summary = "创建预设")
    @SysLogAnnotion("创建用户自定义预设")
    public ResponseEntity<AiConversationPresetEntity> create(@RequestBody AiConversationPresetEntity bean) {
        Long userId = SecurityUtil.getStaff_id();

        AiConversationPresetEntity created = presetService.createPreset(
                bean.getTitle(),
                bean.getRemark(),
                bean.getAiSystemMessage(),
                bean.getIsPublic() != null ? bean.getIsPublic() : 0,
                bean.getCategory(),
                AiMessageTypeConstant.MESSAGE_TYPE_USER,
                userId
        );

        if (created != null) {
            log.info("用户创建预设成功，userId: {}, uuid: {}", userId, created.getUuid());
            return ResponseEntity.ok(created);
        } else {
            log.error("用户创建预设失败，userId: {}", userId);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新用户自定义预设
     */
    @PostMapping(value = "/update")
    @Operation(summary = "更新预设")
    @SysLogAnnotion("更新用户自定义预设")
    public ResponseEntity<AiConversationPresetEntity> update(@RequestBody AiConversationPresetEntity bean) {
        if (presetService.updateById(bean)) {
            AiConversationPresetEntity updated = presetService.getById(bean.getId());
            log.info("更新预设成功，id: {}", bean.getId());
            return ResponseEntity.ok(updated);
        } else {
            log.error("更新预设失败，id: {}", bean.getId());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除用户自定义预设
     */
    @DeleteMapping(value = "/delete/{id}")
    @Operation(summary = "删除预设")
    @SysLogAnnotion("删除用户自定义预设")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        // 先删除关联关系
        presetRelService.deleteByPresetId(id);

        // 再删除预设
        boolean success = presetService.removeById(id);

        if (success) {
            log.info("删除预设成功，id: {}", id);
            return ResponseEntity.ok().build();
        } else {
            log.error("删除预设失败，id: {}", id);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 增加预设使用次数
     */
    @PostMapping(value = "/increment-use/{id}")
    @Operation(summary = "增加使用次数")
    @SysLogAnnotion("增加预设使用次数")
    public ResponseEntity<Void> incrementUse(@PathVariable("id") String id) {
        presetService.incrementUseCount(id);
        log.debug("增加预设使用次数，id: {}", id);
        return ResponseEntity.ok().build();
    }
}
