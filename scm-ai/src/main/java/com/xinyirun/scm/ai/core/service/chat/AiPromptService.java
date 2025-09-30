package com.xinyirun.scm.ai.core.service.chat;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.model.AiPromptEntity;
import com.xinyirun.scm.ai.bean.vo.chat.AiPromptVo;
import com.xinyirun.scm.ai.core.mapper.chat.AiPromptMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI提示词服务
 *
 * 提供AI提示词模板管理功能，包括提示词的创建、查询、更新等操作
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Slf4j
public class AiPromptService {

    @Resource
    private AiPromptMapper aiPromptMapper;


    /**
     * 根据提示词编码查询
     *
     * @param code 提示词编码
     * @return 提示词VO
     */
    public AiPromptVo getByCode(String code) {
        try {
            AiPromptEntity entity = aiPromptMapper.selectByCode(code);
            if (entity != null) {
                AiPromptVo vo = new AiPromptVo();
                BeanUtils.copyProperties(entity, vo);
                return vo;
            }
            return null;
        } catch (Exception e) {
            log.error("根据提示词编码查询失败, code: {}", code, e);
            return null;
        }
    }

    /**
     * 根据提示词昵称查询
     *
     * @param nickname 提示词昵称
     * @return 提示词VO
     */
    public AiPromptVo getByNickname(String nickname) {
        try {
            AiPromptEntity entity = aiPromptMapper.selectByNickname(nickname);
            if (entity != null) {
                AiPromptVo vo = new AiPromptVo();
                BeanUtils.copyProperties(entity, vo);
                return vo;
            }
            return null;
        } catch (Exception e) {
            log.error("根据提示词昵称查询失败, nickname: {}", nickname, e);
            return null;
        }
    }

}