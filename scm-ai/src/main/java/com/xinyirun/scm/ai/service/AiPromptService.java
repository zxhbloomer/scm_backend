package com.xinyirun.scm.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.chat.AiPromptEntity;
import com.xinyirun.scm.ai.bean.vo.chat.AiPromptVo;
import com.xinyirun.scm.ai.mapper.chat.AiPromptMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
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
     * 根据提示词类型查询
     *
     * @param promptType 提示词类型
     * @param tenant 租户标识
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 提示词分页列表
     */
    public IPage<AiPromptVo> getByPromptType(String promptType, String tenant, int pageNum, int pageSize) {
        try {
            Page<AiPromptEntity> page = new Page<>(pageNum, pageSize);
            QueryWrapper<AiPromptEntity> wrapper = new QueryWrapper<>();

            wrapper.eq("prompt_type", promptType);
            if (StringUtils.hasText(tenant)) {
                wrapper.eq("tenant", tenant);
            }
            wrapper.eq("is_active", 1);
            wrapper.orderByDesc("c_time");

            IPage<AiPromptEntity> entityPage = aiPromptMapper.selectPage(page, wrapper);

            // 转换为VO分页
            Page<AiPromptVo> voPage = new Page<>(pageNum, pageSize);
            voPage.setTotal(entityPage.getTotal());
            voPage.setRecords(entityPage.getRecords().stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList()));

            return voPage;
        } catch (Exception e) {
            log.error("根据提示词类型查询失败, promptType: {}, tenant: {}", promptType, tenant, e);
            return new Page<>(pageNum, pageSize);
        }
    }

    /**
     * 查询所有有效提示词
     *
     * @param tenant 租户标识
     * @return 提示词列表
     */
    public List<AiPromptVo> getAllActivePrompts(String tenant) {
        try {
            QueryWrapper<AiPromptEntity> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(tenant)) {
                wrapper.eq("tenant", tenant);
            }
            wrapper.eq("is_active", 1);
            wrapper.orderByAsc("prompt_type", "sort_order");

            List<AiPromptEntity> entities = aiPromptMapper.selectList(wrapper);
            return entities.stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询所有有效提示词失败, tenant: {}", tenant, e);
            return List.of();
        }
    }

    /**
     * 根据提示词名称查询
     *
     * @param promptName 提示词名称
     * @param tenant 租户标识
     * @return 提示词VO
     */
    public AiPromptVo getByPromptName(String promptName, String tenant) {
        try {
            QueryWrapper<AiPromptEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("prompt_name", promptName);
            if (StringUtils.hasText(tenant)) {
                wrapper.eq("tenant", tenant);
            }
            wrapper.eq("is_active", 1);

            AiPromptEntity entity = aiPromptMapper.selectOne(wrapper);
            if (entity != null) {
                return convertToVo(entity);
            }
            return null;
        } catch (Exception e) {
            log.error("根据提示词名称查询失败, promptName: {}, tenant: {}", promptName, tenant, e);
            return null;
        }
    }

    /**
     * 创建新提示词
     *
     * @param promptVo 提示词VO
     * @param operatorId 操作员ID
     * @return 创建的提示词VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiPromptVo createPrompt(AiPromptVo promptVo, Long operatorId) {
        try {
            AiPromptEntity entity = convertToEntity(promptVo);

            LocalDateTime now = LocalDateTime.now();
            entity.setC_time(now);
            entity.setC_id(operatorId);
            entity.setU_time(now);
            entity.setU_id(operatorId);
            entity.setDbversion(1);

            int result = aiPromptMapper.insert(entity);
            if (result > 0) {
                log.info("创建提示词成功, promptName: {}", entity.getPrompt_name());
                return convertToVo(entity);
            }

            return null;
        } catch (Exception e) {
            log.error("创建提示词失败", e);
            throw new RuntimeException("创建提示词失败", e);
        }
    }

    /**
     * 更新提示词信息
     *
     * @param promptVo 提示词VO
     * @param operatorId 操作员ID
     * @return 更新结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePrompt(AiPromptVo promptVo, Long operatorId) {
        try {
            AiPromptEntity entity = convertToEntity(promptVo);

            LocalDateTime now = LocalDateTime.now();
            entity.setU_time(now);
            entity.setU_id(operatorId);

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
     * 停用提示词
     *
     * @param promptId 提示词ID
     * @param operatorId 操作员ID
     * @return 停用结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deactivatePrompt(Integer promptId, Long operatorId) {
        try {
            AiPromptEntity entity = new AiPromptEntity();
            entity.setId(promptId);
            entity.setU_time(LocalDateTime.now());
            entity.setU_id(operatorId);

            int result = aiPromptMapper.updateById(entity);
            if (result > 0) {
                log.info("停用提示词成功, promptId: {}", promptId);
                return true;
            }

            return false;
        } catch (Exception e) {
            log.error("停用提示词失败, promptId: {}", promptId, e);
            throw new RuntimeException("停用提示词失败", e);
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