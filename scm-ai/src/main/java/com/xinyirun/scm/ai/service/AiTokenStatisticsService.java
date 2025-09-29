package com.xinyirun.scm.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.statistics.AiTokenStatisticsEntity;
import com.xinyirun.scm.ai.bean.vo.statistics.AiTokenStatisticsVo;
import com.xinyirun.scm.ai.mapper.statistics.AiTokenStatisticsMapper;
import com.xinyirun.scm.ai.mapper.statistics.ExtAiTokenStatisticsMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI Token统计服务
 *
 * 提供AI Token使用统计功能，包括统计数据的创建、查询、分析等操作
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Slf4j
public class AiTokenStatisticsService {

    @Resource
    private AiTokenStatisticsMapper aiTokenStatisticsMapper;

    @Resource
    private ExtAiTokenStatisticsMapper extAiTokenStatisticsMapper;

    /**
     * 根据ID查询统计记录
     *
     * @param id 统计记录ID
     * @return 统计记录VO
     */
    public AiTokenStatisticsVo getById(Integer id) {
        try {
            AiTokenStatisticsEntity entity = aiTokenStatisticsMapper.selectById(id);
            if (entity != null) {
                return convertToVo(entity);
            }
            return null;
        } catch (Exception e) {
            log.error("根据ID查询Token统计失败, id: {}", id, e);
            return null;
        }
    }

    /**
     * 根据用户和日期范围查询统计
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param tenant 租户标识
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 统计分页列表
     */
    public IPage<AiTokenStatisticsVo> getByUserAndDateRange(String userId, LocalDateTime startDate,
                                                            LocalDateTime endDate, String tenant,
                                                            int pageNum, int pageSize) {
        try {
            Page<AiTokenStatisticsEntity> page = new Page<>(pageNum, pageSize);
            QueryWrapper<AiTokenStatisticsEntity> wrapper = new QueryWrapper<>();

            wrapper.eq("user_id", userId);
            if (StringUtils.hasText(tenant)) {
                wrapper.eq("tenant", tenant);
            }
            if (startDate != null) {
                wrapper.ge("statistics_date", startDate);
            }
            if (endDate != null) {
                wrapper.le("statistics_date", endDate);
            }
            wrapper.orderByDesc("statistics_date");

            IPage<AiTokenStatisticsEntity> entityPage = aiTokenStatisticsMapper.selectPage(page, wrapper);

            // 转换为VO分页
            Page<AiTokenStatisticsVo> voPage = new Page<>(pageNum, pageSize);
            voPage.setTotal(entityPage.getTotal());
            voPage.setRecords(entityPage.getRecords().stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList()));

            return voPage;
        } catch (Exception e) {
            log.error("根据用户和日期范围查询Token统计失败, userId: {}, startDate: {}, endDate: {}, tenant: {}",
                    userId, startDate, endDate, tenant, e);
            return new Page<>(pageNum, pageSize);
        }
    }

    /**
     * 获取用户每日统计数据
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param tenant 租户标识
     * @return 每日统计数据
     */
    public List<Map<String, Object>> getUserDailyStatistics(String userId, LocalDateTime startDate,
                                                            LocalDateTime endDate, String tenant) {
        try {
            return extAiTokenStatisticsMapper.selectUserDailyStatistics(userId, startDate, endDate);
        } catch (Exception e) {
            log.error("获取用户每日统计数据失败, userId: {}, startDate: {}, endDate: {}, tenant: {}",
                    userId, startDate, endDate, tenant, e);
            return List.of();
        }
    }

    /**
     * 获取模型使用排行榜
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param tenant 租户标识
     * @param limit 返回数量限制
     * @return 模型排行数据
     */
    public List<Map<String, Object>> getModelRanking(LocalDateTime startDate, LocalDateTime endDate,
                                                     String tenant, Integer limit) {
        try {
            return extAiTokenStatisticsMapper.selectModelRanking(startDate, endDate, limit);
        } catch (Exception e) {
            log.error("获取模型使用排行榜失败, startDate: {}, endDate: {}, tenant: {}, limit: {}",
                    startDate, endDate, tenant, limit, e);
            return List.of();
        }
    }

    /**
     * 获取用户使用排行榜
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param tenant 租户标识
     * @param limit 返回数量限制
     * @return 用户排行数据
     */
    public List<Map<String, Object>> getUserRanking(LocalDateTime startDate, LocalDateTime endDate,
                                                    String tenant, Integer limit) {
        try {
            return extAiTokenStatisticsMapper.selectUserRanking(startDate, endDate, limit);
        } catch (Exception e) {
            log.error("获取用户使用排行榜失败, startDate: {}, endDate: {}, tenant: {}, limit: {}",
                    startDate, endDate, tenant, limit, e);
            return List.of();
        }
    }

    /**
     * 获取按小时统计的趋势数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param tenant 租户标识
     * @return 小时趋势数据
     */
    public List<Map<String, Object>> getHourlyTrend(LocalDateTime startDate, LocalDateTime endDate, String tenant) {
        try {
            return extAiTokenStatisticsMapper.selectHourlyTrend(startDate, endDate);
        } catch (Exception e) {
            log.error("获取按小时统计趋势数据失败, startDate: {}, endDate: {}, tenant: {}",
                    startDate, endDate, tenant, e);
            return List.of();
        }
    }

    /**
     * 获取指定日期的汇总统计
     *
     * @param statisticsDate 统计日期
     * @param tenant 租户标识
     * @return 汇总统计数据
     */
    public Map<String, Object> getDailySummary(LocalDateTime statisticsDate, String tenant) {
        try {
            return extAiTokenStatisticsMapper.selectDailySummary(statisticsDate);
        } catch (Exception e) {
            log.error("获取指定日期汇总统计失败, statisticsDate: {}, tenant: {}", statisticsDate, tenant, e);
            return Map.of();
        }
    }

    /**
     * 创建新统计记录
     *
     * @param statisticsVo 统计记录VO
     * @param operatorId 操作员ID
     * @return 创建的统计记录VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiTokenStatisticsVo createStatistics(AiTokenStatisticsVo statisticsVo, Long operatorId) {
        try {
            AiTokenStatisticsEntity entity = convertToEntity(statisticsVo);

            LocalDateTime now = LocalDateTime.now();
            entity.setC_time(now);
            entity.setC_id(operatorId);
            entity.setU_time(now);
            entity.setU_id(operatorId);
            entity.setDbversion(1);

            int result = aiTokenStatisticsMapper.insert(entity);
            if (result > 0) {
                log.info("创建Token统计记录成功, userId: {}, modelSourceId: {}",
                        entity.getUser_id(), entity.getModel_source_id());
                return convertToVo(entity);
            }

            return null;
        } catch (Exception e) {
            log.error("创建Token统计记录失败", e);
            throw new RuntimeException("创建Token统计记录失败", e);
        }
    }

    /**
     * 更新统计记录
     *
     * @param statisticsVo 统计记录VO
     * @param operatorId 操作员ID
     * @return 更新结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatistics(AiTokenStatisticsVo statisticsVo, Long operatorId) {
        try {
            AiTokenStatisticsEntity entity = convertToEntity(statisticsVo);

            LocalDateTime now = LocalDateTime.now();
            entity.setU_time(now);
            entity.setU_id(operatorId);

            int result = aiTokenStatisticsMapper.updateById(entity);
            if (result > 0) {
                log.info("更新Token统计记录成功, id: {}", entity.getId());
                return true;
            }

            return false;
        } catch (Exception e) {
            log.error("更新Token统计记录失败", e);
            throw new RuntimeException("更新Token统计记录失败", e);
        }
    }

    /**
     * 删除统计记录
     *
     * @param statisticsId 统计记录ID
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStatistics(Integer statisticsId) {
        try {
            int result = aiTokenStatisticsMapper.deleteById(statisticsId);
            if (result > 0) {
                log.info("删除Token统计记录成功, statisticsId: {}", statisticsId);
                return true;
            }

            return false;
        } catch (Exception e) {
            log.error("删除Token统计记录失败, statisticsId: {}", statisticsId, e);
            throw new RuntimeException("删除Token统计记录失败", e);
        }
    }

    /**
     * 批量删除过期统计数据
     *
     * @param beforeDate 删除此日期之前的数据
     * @param tenant 租户标识
     * @return 删除的记录数
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteStatisticsBeforeDate(LocalDateTime beforeDate, String tenant) {
        try {
            int deleteCount = extAiTokenStatisticsMapper.deleteStatisticsBeforeDate(beforeDate);
            log.info("批量删除过期Token统计数据完成, beforeDate: {}, tenant: {}, 删除数量: {}",
                    beforeDate, tenant, deleteCount);
            return deleteCount;
        } catch (Exception e) {
            log.error("批量删除过期Token统计数据失败, beforeDate: {}, tenant: {}", beforeDate, tenant, e);
            throw new RuntimeException("批量删除过期Token统计数据失败", e);
        }
    }

    /**
     * Entity转VO
     */
    private AiTokenStatisticsVo convertToVo(AiTokenStatisticsEntity entity) {
        AiTokenStatisticsVo vo = new AiTokenStatisticsVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * VO转Entity
     */
    private AiTokenStatisticsEntity convertToEntity(AiTokenStatisticsVo vo) {
        AiTokenStatisticsEntity entity = new AiTokenStatisticsEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }
}