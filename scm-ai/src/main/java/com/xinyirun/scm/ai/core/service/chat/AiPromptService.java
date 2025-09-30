package com.xinyirun.scm.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.model.AiPromptEntity;
import com.xinyirun.scm.ai.bean.vo.chat.AiPromptVo;
import com.xinyirun.scm.ai.mapper.chat.AiPromptMapper;
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
     * 根据ID查询提示词
     *
     * @param id 提示词ID
     * @return 提示词VO
     */
    public AiPromptVo getById(Integer id) {
        try {
            AiPromptEntity entity = aiPromptMapper.selectById(id);
            if (entity != null) {
                return convertToVo(entity);
            }
            return null;
        } catch (Exception e) {
            log.error("根据ID查询提示词失败, id: {}", id, e);
            return null;
        }
    }

    /**
     * 根据提示词类型分页查询
     *
     * @param type 提示词类型
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 提示词分页列表
     */
    public IPage<AiPromptVo> getByType(Integer type, int pageNum, int pageSize) {
        try {
            Page<AiPromptEntity> page = new Page<>(pageNum, pageSize);
            IPage<AiPromptEntity> entityPage = aiPromptMapper.selectPageByType(page, type);

            // 转换为VO分页
            Page<AiPromptVo> voPage = new Page<>(pageNum, pageSize);
            voPage.setTotal(entityPage.getTotal());
            voPage.setRecords(entityPage.getRecords().stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList()));

            return voPage;
        } catch (Exception e) {
            log.error("根据提示词类型查询失败, type: {}", type, e);
            return new Page<>(pageNum, pageSize);
        }
    }

    /**
     * 查询所有有效提示词
     *
     * @return 提示词列表
     */
    public List<AiPromptVo> getAllActivePrompts() {
        try {
            List<AiPromptEntity> entities = aiPromptMapper.selectAllActivePrompts();
            return entities.stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询所有有效提示词失败", e);
            return List.of();
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
                return convertToVo(entity);
            }
            return null;
        } catch (Exception e) {
            log.error("根据提示词昵称查询失败, nickname: {}", nickname, e);
            return null;
        }
    }

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
                return convertToVo(entity);
            }
            return null;
        } catch (Exception e) {
            log.error("根据提示词编码查询失败, code: {}", code, e);
            return null;
        }
    }

    /**
     * 更新提示词信息
     *
     * @param promptVo 提示词VO
     * @return 更新结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePrompt(AiPromptVo promptVo) {
        try {
            AiPromptEntity entity = convertToEntity(promptVo);

            int result = aiPromptMapper.updateById(entity);
            if (result > 0) {
                log.info("更新提示词成功, id: {}", entity.getId());
                return true;
            }

            return false;
        } catch (Exception e) {
            log.error("更新提示词失败", e);
            throw new RuntimeException("更新提示词失败", e);
        }
    }

    /**
     * 创建新提示词
     *
     * @param promptVo 提示词VO
     * @return 创建结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean createPrompt(AiPromptVo promptVo) {
        try {
            AiPromptEntity entity = convertToEntity(promptVo);

            int result = aiPromptMapper.insert(entity);
            if (result > 0) {
                log.info("创建提示词成功, id: {}", entity.getId());
                return true;
            }

            return false;
        } catch (Exception e) {
            log.error("创建提示词失败", e);
            throw new RuntimeException("创建提示词失败", e);
        }
    }

    /**
     * 删除提示词（物理删除）
     *
     * @param promptId 提示词ID
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePrompt(Integer promptId) {
        try {
            int result = aiPromptMapper.deleteById(promptId);
            if (result > 0) {
                log.info("删除提示词成功, promptId: {}", promptId);
                return true;
            }

            return false;
        } catch (Exception e) {
            log.error("删除提示词失败, promptId: {}", promptId, e);
            throw new RuntimeException("删除提示词失败", e);
        }
    }

    /**
     * Entity转VO
     */
    private AiPromptVo convertToVo(AiPromptEntity entity) {
        AiPromptVo vo = new AiPromptVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * VO转Entity
     */
    private AiPromptEntity convertToEntity(AiPromptVo vo) {
        AiPromptEntity entity = new AiPromptEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }
}