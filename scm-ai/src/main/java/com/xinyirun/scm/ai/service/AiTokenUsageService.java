package com.xinyirun.scm.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.statistics.AiTokenUsageEntity;
import com.xinyirun.scm.ai.bean.vo.statistics.AiTokenUsageVo;
import com.xinyirun.scm.ai.mapper.statistics.AiTokenUsageMapper;
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
 * AI Token使用记录服务
 *
 * 提供AI Token实时使用记录管理功能，包括记录的创建、查询、统计等操作
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Slf4j
public class AiTokenUsageService {

    @Resource
    private AiTokenUsageMapper aiTokenUsageMapper;

    /**
     * 根据ID查询使用记录
     *
     * @param id 使用记录ID
     * @return 使用记录VO
     */
    public AiTokenUsageVo getById(Integer id) {
        try {
            AiTokenUsageEntity entity = aiTokenUsageMapper.selectById(id);
            if (entity != null) {
                return convertToVo(entity);
            }
            return null;
        } catch (Exception e) {
            log.error("根据ID查询Token使用记录失败, id: {}", id, e);
            return null;
        }
    }

    /**
     * 根据用户和日期范围查询使用记录
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param tenant 租户标识
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 使用记录分页列表
     */
    public IPage<AiTokenUsageVo> getByUserAndTimeRange(String userId, LocalDateTime startTime,
                                                       LocalDateTime endTime, String tenant,
                                                       int pageNum, int pageSize) {
        try {
            Page<AiTokenUsageEntity> page = new Page<>(pageNum, pageSize);
            QueryWrapper<AiTokenUsageEntity> wrapper = new QueryWrapper<>();

            wrapper.eq("user_id", userId);
            if (StringUtils.hasText(tenant)) {
                wrapper.eq("tenant", tenant);
            }
            if (startTime != null) {
                wrapper.ge("usage_time", startTime);
            }
            if (endTime != null) {
                wrapper.le("usage_time", endTime);
            }
            wrapper.orderByDesc("usage_time");

            IPage<AiTokenUsageEntity> entityPage = aiTokenUsageMapper.selectPage(page, wrapper);

            // 转换为VO分页
            Page<AiTokenUsageVo> voPage = new Page<>(pageNum, pageSize);
            voPage.setTotal(entityPage.getTotal());
            voPage.setRecords(entityPage.getRecords().stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList()));

            return voPage;
        } catch (Exception e) {
            log.error("根据用户和时间范围查询Token使用记录失败, userId: {}, startTime: {}, endTime: {}, tenant: {}",
                    userId, startTime, endTime, tenant, e);
            return new Page<>(pageNum, pageSize);
        }
    }

    /**
     * 根据对话ID查询使用记录
     *
     * @param conversationId 对话ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 使用记录分页列表
     */
    public IPage<AiTokenUsageVo> getByConversationId(Integer conversationId, int pageNum, int pageSize) {
        try {
            Page<AiTokenUsageEntity> page = new Page<>(pageNum, pageSize);
            QueryWrapper<AiTokenUsageEntity> wrapper = new QueryWrapper<>();

            wrapper.eq("conversation_id", conversationId);
            wrapper.orderByDesc("usage_time");

            IPage<AiTokenUsageEntity> entityPage = aiTokenUsageMapper.selectPage(page, wrapper);

            // 转换为VO分页
            Page<AiTokenUsageVo> voPage = new Page<>(pageNum, pageSize);
            voPage.setTotal(entityPage.getTotal());
            voPage.setRecords(entityPage.getRecords().stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList()));

            return voPage;
        } catch (Exception e) {
            log.error("根据对话ID查询Token使用记录失败, conversationId: {}", conversationId, e);
            return new Page<>(pageNum, pageSize);
        }
    }

    /**
     * 根据模型源查询使用记录
     *
     * @param modelSourceId 模型源ID
     * @param tenant 租户标识
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 使用记录分页列表
     */
    public IPage<AiTokenUsageVo> getByModelSource(Integer modelSourceId, String tenant,
                                                  int pageNum, int pageSize) {
        try {
            Page<AiTokenUsageEntity> page = new Page<>(pageNum, pageSize);
            QueryWrapper<AiTokenUsageEntity> wrapper = new QueryWrapper<>();

            wrapper.eq("model_source_id", modelSourceId);
            if (StringUtils.hasText(tenant)) {
                wrapper.eq("tenant", tenant);
            }
            wrapper.orderByDesc("usage_time");

            IPage<AiTokenUsageEntity> entityPage = aiTokenUsageMapper.selectPage(page, wrapper);

            // 转换为VO分页
            Page<AiTokenUsageVo> voPage = new Page<>(pageNum, pageSize);
            voPage.setTotal(entityPage.getTotal());
            voPage.setRecords(entityPage.getRecords().stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList()));

            return voPage;
        } catch (Exception e) {
            log.error("根据模型源查询Token使用记录失败, modelSourceId: {}, tenant: {}", modelSourceId, tenant, e);
            return new Page<>(pageNum, pageSize);
        }
    }

    /**
     * 创建新使用记录
     *
     * @param usageVo 使用记录VO
     * @param operatorId 操作员ID
     * @return 创建的使用记录VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiTokenUsageVo createUsage(AiTokenUsageVo usageVo, Long operatorId) {
        try {
            AiTokenUsageEntity entity = convertToEntity(usageVo);

            LocalDateTime now = LocalDateTime.now();
            entity.setC_time(now);
            entity.setC_id(operatorId);
            entity.setU_time(now);
            entity.setU_id(operatorId);
            entity.setDbversion(1);

            // 设置使用时间（如果未设置）
            if (entity.getUsage_time() == null) {
                entity.setUsage_time(now);
            }

            int result = aiTokenUsageMapper.insert(entity);
            if (result > 0) {
                log.info("创建Token使用记录成功, userId: {}, modelSourceId: {}, totalTokens: {}",
                        entity.getUser_id(), entity.getModel_source_id(), entity.getTotal_tokens());
                return convertToVo(entity);
            }

            return null;
        } catch (Exception e) {
            log.error("创建Token使用记录失败", e);
            throw new RuntimeException("创建Token使用记录失败", e);
        }
    }

    /**
     * 批量创建使用记录
     *
     * @param usageVoList 使用记录VO列表
     * @param operatorId 操作员ID
     * @return 创建成功的记录数
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchCreateUsage(List<AiTokenUsageVo> usageVoList, Long operatorId) {
        try {
            if (usageVoList == null || usageVoList.isEmpty()) {
                return 0;
            }

            LocalDateTime now = LocalDateTime.now();
            List<AiTokenUsageEntity> entities = usageVoList.stream()
                    .map(vo -> {
                        AiTokenUsageEntity entity = convertToEntity(vo);
                        entity.setC_time(now);
                        entity.setC_id(operatorId);
                        entity.setU_time(now);
                        entity.setU_id(operatorId);
                        entity.setDbversion(1);
                        if (entity.getUsage_time() == null) {
                            entity.setUsage_time(now);
                        }
                        return entity;
                    })
                    .collect(Collectors.toList());

            int successCount = 0;
            for (AiTokenUsageEntity entity : entities) {
                try {
                    int result = aiTokenUsageMapper.insert(entity);
                    if (result > 0) {
                        successCount++;
                    }
                } catch (Exception e) {
                    log.error("批量创建Token使用记录时单条记录失败: {}", entity, e);
                }
            }

            log.info("批量创建Token使用记录完成, 总数: {}, 成功: {}", entities.size(), successCount);
            return successCount;
        } catch (Exception e) {
            log.error("批量创建Token使用记录失败", e);
            throw new RuntimeException("批量创建Token使用记录失败", e);
        }
    }

    /**
     * 更新使用记录
     *
     * @param usageVo 使用记录VO
     * @param operatorId 操作员ID
     * @return 更新结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUsage(AiTokenUsageVo usageVo, Long operatorId) {
        try {
            AiTokenUsageEntity entity = convertToEntity(usageVo);

            LocalDateTime now = LocalDateTime.now();
            entity.setU_time(now);
            entity.setU_id(operatorId);

            int result = aiTokenUsageMapper.updateById(entity);
            if (result > 0) {
                log.info("更新Token使用记录成功, id: {}", entity.getId());
                return true;
            }

            return false;
        } catch (Exception e) {
            log.error("更新Token使用记录失败", e);
            throw new RuntimeException("更新Token使用记录失败", e);
        }
    }

    /**
     * 删除使用记录
     *
     * @param usageId 使用记录ID
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUsage(Integer usageId) {
        try {
            int result = aiTokenUsageMapper.deleteById(usageId);
            if (result > 0) {
                log.info("删除Token使用记录成功, usageId: {}", usageId);
                return true;
            }

            return false;
        } catch (Exception e) {
            log.error("删除Token使用记录失败, usageId: {}", usageId, e);
            throw new RuntimeException("删除Token使用记录失败", e);
        }
    }

    /**
     * 根据时间范围删除使用记录
     *
     * @param beforeTime 删除此时间之前的记录
     * @param tenant 租户标识
     * @return 删除的记录数
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteUsageBeforeTime(LocalDateTime beforeTime, String tenant) {
        try {
            QueryWrapper<AiTokenUsageEntity> wrapper = new QueryWrapper<>();
            wrapper.lt("usage_time", beforeTime);
            if (StringUtils.hasText(tenant)) {
                wrapper.eq("tenant", tenant);
            }

            int deleteCount = aiTokenUsageMapper.delete(wrapper);
            log.info("删除过期Token使用记录完成, beforeTime: {}, tenant: {}, 删除数量: {}",
                    beforeTime, tenant, deleteCount);
            return deleteCount;
        } catch (Exception e) {
            log.error("删除过期Token使用记录失败, beforeTime: {}, tenant: {}", beforeTime, tenant, e);
            throw new RuntimeException("删除过期Token使用记录失败", e);
        }
    }

    /**
     * 计算用户在指定时间范围内的总Token使用量
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param tenant 租户标识
     * @return Token使用总量
     */
    public Long getTotalTokensByUser(String userId, LocalDateTime startTime, LocalDateTime endTime, String tenant) {
        try {
            QueryWrapper<AiTokenUsageEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            if (StringUtils.hasText(tenant)) {
                wrapper.eq("tenant", tenant);
            }
            if (startTime != null) {
                wrapper.ge("usage_time", startTime);
            }
            if (endTime != null) {
                wrapper.le("usage_time", endTime);
            }

            List<AiTokenUsageEntity> entities = aiTokenUsageMapper.selectList(wrapper);
            return entities.stream()
                    .mapToLong(entity -> entity.getTotal_tokens() != null ? entity.getTotal_tokens() : 0L)
                    .sum();
        } catch (Exception e) {
            log.error("计算用户Token使用总量失败, userId: {}, startTime: {}, endTime: {}, tenant: {}",
                    userId, startTime, endTime, tenant, e);
            return 0L;
        }
    }

    /**
     * 异步记录Token使用情况
     *
     * @param conversationId 对话ID
     * @param modelSourceId 模型源ID
     * @param userId 用户ID
     * @param aiProvider AI提供商
     * @param aiModelType AI模型类型
     * @param promptTokens 输入Token数
     * @param completionTokens 输出Token数
     * @param success 是否成功
     * @param responseTime 响应时间
     */
    @Transactional(rollbackFor = Exception.class)
    public void recordTokenUsageAsync(String conversationId, String modelSourceId, String userId,
                                      String aiProvider, String aiModelType,
                                     Long promptTokens, Long completionTokens, Boolean success,
                                     Long responseTime) {
        try {
            // 创建Token使用记录
            AiTokenUsageEntity entity = new AiTokenUsageEntity();

            // 处理ID字段的类型转换（String转Integer）
            if (conversationId != null) {
                try {
                    entity.setConversation_id(Integer.valueOf(conversationId));
                } catch (NumberFormatException e) {
                    log.warn("对话ID转换失败，使用默认值: {}", conversationId);
                    entity.setConversation_id(null);
                }
            }

            if (modelSourceId != null) {
                try {
                    entity.setModel_source_id(Integer.valueOf(modelSourceId));
                } catch (NumberFormatException e) {
                    log.warn("模型源ID转换失败，使用默认值: {}", modelSourceId);
                    entity.setModel_source_id(null);
                }
            }

            entity.setUser_id(userId);

            // 映射token字段
            entity.setPrompt_tokens(promptTokens != null ? promptTokens.intValue() : 0);
            entity.setCompletion_tokens(completionTokens != null ? completionTokens.intValue() : 0);
            // total_tokens是数据库生成列，自动计算，不需要手动设置
            entity.setUsage_time(LocalDateTime.now());

            LocalDateTime now = LocalDateTime.now();
            entity.setC_time(now);
            entity.setU_time(now);
            entity.setDbversion(1);

            int result = aiTokenUsageMapper.insert(entity);
            if (result > 0) {
                log.debug("记录Token使用情况成功, conversationId: {}, userId: {}, totalTokens: {}",
                        conversationId, userId, entity.getTotal_tokens());
            }
        } catch (Exception e) {
            log.error("记录Token使用情况失败, conversationId: {}, userId: {}, tenant: {}",
                    conversationId, userId, e);
        }
    }

    /**
     * Entity转VO
     */
    private AiTokenUsageVo convertToVo(AiTokenUsageEntity entity) {
        AiTokenUsageVo vo = new AiTokenUsageVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * VO转Entity
     */
    private AiTokenUsageEntity convertToEntity(AiTokenUsageVo vo) {
        AiTokenUsageEntity entity = new AiTokenUsageEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }
}