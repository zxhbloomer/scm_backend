package com.xinyirun.scm.ai.core.service.chat;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationPresetEntity;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationPresetMapper;
import com.xinyirun.scm.common.utils.UuidUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI对话预设服务类
 *
 * @author SCM-AI团队
 * @since 2025-11-08
 */
@Slf4j
@Service
public class AiConversationPresetService extends ServiceImpl<AiConversationPresetMapper, AiConversationPresetEntity> {

    @Resource
    private AiConversationPresetMapper presetMapper;

    /**
     * 创建预设
     *
     * @param title 预设标题
     * @param remark 预设描述
     * @param aiSystemMessage AI系统提示词
     * @param isPublic 是否公开（1-公开，0-私有）
     * @param category 分类
     * @param creatorType 创建者类型（SYSTEM/USER）
     * @param userId 用户ID
     * @return 预设实体
     */
    public AiConversationPresetEntity createPreset(String title, String remark, String aiSystemMessage,
                                                    Integer isPublic, String category, String creatorType, Long userId) {
        AiConversationPresetEntity entity = new AiConversationPresetEntity();
        entity.setUuid(UuidUtil.createShort());
        entity.setTitle(title);
        entity.setRemark(remark);
        entity.setAiSystemMessage(aiSystemMessage);
        entity.setIsPublic(isPublic);
        entity.setCategory(category);
        entity.setSortOrder(0);
        entity.setUseCount(0);
        entity.setCreatorType(creatorType);

        boolean success = this.save(entity);
        log.info("创建预设完成，uuid: {}, title: {}, creatorType: {}", entity.getUuid(), title, creatorType);

        return success ? entity : null;
    }

    /**
     * 查询公开的预设列表
     *
     * @return 公开预设列表
     */
    public List<AiConversationPresetEntity> listPublicPresets() {
        return presetMapper.selectPublicPresets();
    }

    /**
     * 查询用户创建的预设列表
     *
     * @param userId 用户ID
     * @return 用户预设列表
     */
    public List<AiConversationPresetEntity> listUserPresets(Long userId) {
        return presetMapper.selectUserPresets(userId);
    }

    /**
     * 增加预设使用次数
     *
     * @param id 预设ID
     */
    public void incrementUseCount(String id) {
        int count = presetMapper.incrementUseCount(id);
        log.debug("增加预设使用次数，id: {}, 更新行数: {}", id, count);
    }

    /**
     * 根据UUID查询预设
     *
     * @param uuid 预设UUID
     * @return 预设实体
     */
    public AiConversationPresetEntity getByUuid(String uuid) {
        return presetMapper.selectByUuid(uuid);
    }
}
