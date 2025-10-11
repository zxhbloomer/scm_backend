package com.xinyirun.scm.clickhouse.service.quartz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.quartz.SJobLogClickHouseVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.clickhouse.entity.quartz.SJobLogClickHouseEntity;
import com.xinyirun.scm.clickhouse.exception.ClickHouseException;
import com.xinyirun.scm.clickhouse.repository.quartz.SJobLogClickHouseRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 定时任务调度日志 ClickHouse 服务类
 * 专门处理 s_job_log 表的所有业务逻辑
 * 
 * @author SCM System
 * @since 1.0.39
 */
@Slf4j
@Service
public class SJobLogClickHouseService {

    private final SJobLogClickHouseRepository sJobLogRepository;

    public SJobLogClickHouseService(SJobLogClickHouseRepository sJobLogRepository) {
        this.sJobLogRepository = sJobLogRepository;
    }

    // ==================== 插入操作 ====================

    /**
     * 插入定时任务日志 - 接受 SJobLogClickHouseVo 参数
     * 在方法内部转换为 SJobLogClickHouseEntity
     */
    public void insert(SJobLogClickHouseVo jobLogVo) {
        try {
            // 转换为 ClickHouse 实体
            SJobLogClickHouseEntity jobLogEntity = convertVoToEntity(jobLogVo);
            
            // 执行插入
            sJobLogRepository.insert(jobLogEntity);
            
            log.info("插入定时任务日志成功，job_id: {}, 任务名称: {}, 类名: {}", 
                    jobLogEntity.getJob_id(), jobLogEntity.getJob_name(), jobLogEntity.getClass_name());
                       
        } catch (Exception e) {
            log.error("插入定时任务日志失败，job_id: {}", 
                     jobLogVo != null ? jobLogVo.getJob_id() : "null", e);
            throw new ClickHouseException("插入定时任务日志失败", e);
        }
    }

    /**
     * 异步插入定时任务日志 - 高性能场景
     */
    @Async("clickhouseTaskExecutor")
    public CompletableFuture<Void> insertAsync(SJobLogClickHouseVo jobLogVo) {
        try {
            insert(jobLogVo);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("异步插入定时任务日志失败", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 批量插入定时任务日志 - 最佳性能方案
     */
    public void batchInsert(List<SJobLogClickHouseVo> jobLogVos) {
        if (jobLogVos == null || jobLogVos.isEmpty()) {
            log.warn("批量插入定时任务日志数据为空，跳过操作");
            return;
        }

        try {
            // 转换为 ClickHouse 实体列表
            List<SJobLogClickHouseEntity> jobLogEntities = jobLogVos.stream()
                    .map(this::convertVoToEntity)
                    .toList();
            
            // 执行批量插入
            sJobLogRepository.batchInsert(jobLogEntities);
            
            log.info("批量插入定时任务日志成功，数量: {}", jobLogVos.size());
            
        } catch (Exception e) {
            log.error("批量插入定时任务日志失败，数量: {}", jobLogVos.size(), e);
            throw new ClickHouseException("批量插入定时任务日志失败", e);
        }
    }

    /**
     * 异步批量插入定时任务日志 - 高吞吐量场景
     */
    @Async("clickhouseTaskExecutor")
    public CompletableFuture<Void> batchInsertAsync(List<SJobLogClickHouseVo> jobLogVos) {
        try {
            batchInsert(jobLogVos);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("异步批量插入定时任务日志失败", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    // ==================== 查询操作 ====================

    /**
     * 分页查询定时任务日志 - 支持条件查询和排序
     * Controller层的核心方法，提供综合查询能力
     * 
     * @param searchCondition 查询条件（包含分页参数）
     * @return 分页结果包含VO列表
     */
    public IPage<SJobLogClickHouseVo> selectPage(SJobLogClickHouseVo searchCondition) {
        try {
            
            IPage<SJobLogClickHouseVo> result = sJobLogRepository.selectPageWithParams(searchCondition);
            
            log.info("分页查询定时任务日志成功，页号: {}, 页大小: {}, 总记录数: {}, 查询条件: [任务ID: {}, 任务名称: {}, 任务组类型: {}, 类名: {}, 方法名: {}, 是否删除: {}]", 
                    searchCondition.getPageCondition().getCurrent(),
                    searchCondition.getPageCondition().getSize(),
                    result.getTotal(),
                    searchCondition.getJob_id(),
                    searchCondition.getJob_name(),
                    searchCondition.getJob_group_type(),
                    searchCondition.getClass_name(),
                    searchCondition.getMethod_name(),
                    searchCondition.getIs_del());
            
            return result;
            
        } catch (Exception e) {
            log.error("分页查询定时任务日志失败，查询条件: [任务ID: {}, 任务名称: {}, 任务组类型: {}, 类名: {}, 方法名: {}, 是否删除: {}]", 
                    searchCondition.getJob_id(), 
                    searchCondition.getJob_name(), 
                    searchCondition.getJob_group_type(),
                    searchCondition.getClass_name(),
                    searchCondition.getMethod_name(),
                    searchCondition.getIs_del(), e);
            throw new ClickHouseException("分页查询定时任务日志失败", e);
        }
    }

    /**
     * 根据ID查询单条定时任务日志记录
     * Controller层的核心方法，提供根据ID的精确查询
     * 
     * @param searchCondition 查询条件VO（包含id字段）
     * @return 找到的VO对象，未找到返回null
     */
    public SJobLogClickHouseVo getById(SJobLogClickHouseVo searchCondition) {
        try {
            // 验证输入参数
            if (searchCondition == null || StringUtils.isBlank(searchCondition.getId())) {
                log.warn("根据ID查询定时任务日志失败：查询条件为空或ID为空");
                return null;
            }
            
            String id = searchCondition.getId().trim();
            
            // 传递完整的查询条件（包含tenant_code）到Repository层
            SJobLogClickHouseVo result = sJobLogRepository.getById(searchCondition);
            
            if (result != null) {
                log.info("根据ID查询定时任务日志成功，ID: {}, 任务名称: {}, 任务组类型: {}", 
                        id, result.getJob_name(), result.getJob_group_type());
            } else {
                log.info("根据ID查询定时任务日志未找到记录，ID: {}", id);
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("根据ID查询定时任务日志失败，ID: {}", 
                    searchCondition != null ? searchCondition.getId() : "null", e);
            throw new ClickHouseException("根据ID查询定时任务日志失败", e);
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 转换Vo对象到Entity对象，处理特殊字段类型转换
     */
    private SJobLogClickHouseEntity convertVoToEntity(SJobLogClickHouseVo vo) {
        // 基础属性拷贝
        SJobLogClickHouseEntity entity = (SJobLogClickHouseEntity) BeanUtilsSupport.copyProperties(vo, SJobLogClickHouseEntity.class);

        // 特殊字段处理（如果需要的话）
        // 这里可以添加特殊的转换逻辑

        return entity;
    }
}