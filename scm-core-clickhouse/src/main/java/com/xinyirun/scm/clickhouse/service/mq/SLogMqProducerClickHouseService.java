package com.xinyirun.scm.clickhouse.service.mq;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.mq.SLogMqProducerClickHouseVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import org.apache.commons.lang3.StringUtils;
import com.xinyirun.scm.clickhouse.entity.mq.SLogMqProducerClickHouseEntity;
import com.xinyirun.scm.clickhouse.exception.ClickHouseException;
import com.xinyirun.scm.clickhouse.repository.mq.SLogMqProducerClickHouseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 系统日志 ClickHouse 服务类
 * 专门处理 s_log_mq_producer 表的所有业务逻辑
 * 
 * @author SCM System
 * @since 1.0.39
 */
@Slf4j
@Service
public class SLogMqProducerClickHouseService {

    private final SLogMqProducerClickHouseRepository sLogMqProducerRepository;

    public SLogMqProducerClickHouseService(SLogMqProducerClickHouseRepository sLogMqProducerRepository) {
        this.sLogMqProducerRepository = sLogMqProducerRepository;
    }

    // ==================== 插入操作 ====================

    /**
     * 插入系统日志 - 接受 SLogMqProducerClickHouseVo 参数
     * 在方法内部转换为 SLogMqProducerClickHouseEntity
     */
    public void insert(SLogMqProducerClickHouseVo sysLogVo) {
        try {
            // 转换为 ClickHouse 实体
            SLogMqProducerClickHouseEntity sysLogEntity = convertVoToEntity(sysLogVo);
            
            // 执行插入
            sLogMqProducerRepository.insert(sysLogEntity);
            
            log.info("插入系统日志成功，request_id: {}, 类型: {}, 用户: {}", 
                    sysLogEntity.getMessage_id(), sysLogEntity.getType(), sysLogEntity.getName());
                       
        } catch (Exception e) {
            log.error("插入系统日志失败，request_id: {}", 
                     sysLogVo != null ? sysLogVo.getMessage_id() : "null", e);
            throw new ClickHouseException("插入系统日志失败", e);
        }
    }


    /**
     * 异步插入系统日志 - 高性能场景
     */
    @Async("clickhouseTaskExecutor")
    public CompletableFuture<Void> insertAsync(SLogMqProducerClickHouseVo sysLogVo) {
        try {
            insert(sysLogVo);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("异步插入系统日志失败", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 批量插入系统日志 - 最佳性能方案
     */
    public void batchInsert(List<SLogMqProducerClickHouseVo> sysLogVos) {
        if (sysLogVos == null || sysLogVos.isEmpty()) {
            log.warn("批量插入系统日志数据为空，跳过操作");
            return;
        }

        try {
            // 转换为 ClickHouse 实体列表
            List<SLogMqProducerClickHouseEntity> sysLogEntities = sysLogVos.stream()
                    .map(this::convertVoToEntity)
                    .toList();
            
            // 执行批量插入
            sLogMqProducerRepository.batchInsert(sysLogEntities);
            
            log.info("批量插入系统日志成功，数量: {}", sysLogVos.size());
            
        } catch (Exception e) {
            log.error("批量插入系统日志失败，数量: {}", sysLogVos.size(), e);
            throw new ClickHouseException("批量插入系统日志失败", e);
        }
    }

    /**
     * 异步批量插入系统日志 - 高吞吐量场景
     */
    @Async("clickhouseTaskExecutor")
    public CompletableFuture<Void> batchInsertAsync(List<SLogMqProducerClickHouseVo> sysLogVos) {
        try {
            batchInsert(sysLogVos);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("异步批量插入系统日志失败", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    // ==================== 分页查询操作 ====================

    /**
     * 分页查询MQ生产者日志 - 多条件查询支持
     * 支持的查询条件：类型、队列名称、队列编码、message_id、消息体、时间范围、租户代码
     */
    public IPage<SLogMqProducerClickHouseVo> selectPageList(SLogMqProducerClickHouseVo searchCondition) {
        try {
            
            IPage<SLogMqProducerClickHouseVo> result = sLogMqProducerRepository.selectPageWithParams(searchCondition);
            
            log.info("分页查询MQ生产者日志成功，页号: {}, 页大小: {}, 总记录数: {}, 查询条件: [类型: {}, 队列名称: {}, 队列编码: {}, message_id: {}, 租户: {}]", 
                    searchCondition.getPageCondition().getCurrent(),
                    searchCondition.getPageCondition().getSize(),
                    result.getTotal(),
                    searchCondition.getType(),
                    searchCondition.getName(),
                    searchCondition.getCode(),
                    searchCondition.getMessage_id(),
                    searchCondition.getTenant_code());
            
            return result;
            
        } catch (Exception e) {
            log.error("分页查询MQ生产者日志失败，查询条件: [类型: {}, 队列名称: {}, 队列编码: {}, message_id: {}, 租户: {}]", 
                    searchCondition.getType(),
                    searchCondition.getName(),
                    searchCondition.getCode(),
                    searchCondition.getMessage_id(),
                    searchCondition.getTenant_code(), e);
            throw new ClickHouseException("分页查询MQ生产者日志失败", e);
        }
    }

    /**
     * 根据ID查询单条MQ生产者日志记录
     * 支持租户代码过滤，确保数据隔离
     */
    public SLogMqProducerClickHouseVo getById(SLogMqProducerClickHouseVo searchCondition) {
        try {
            // 验证输入参数
            if (searchCondition == null || StringUtils.isBlank(searchCondition.getId())) {
                log.warn("根据ID查询MQ生产者日志失败：查询条件为空或ID为空");
                return null;
            }
            
            String id = searchCondition.getId().trim();
            
            // 传递完整的查询条件（包含tenant_code）到Repository层
            SLogMqProducerClickHouseVo result = sLogMqProducerRepository.getById(searchCondition);
            
            if (result != null) {
                log.info("根据ID查询MQ生产者日志成功，ID: {}, 租户: {}, 队列: {}", 
                        id, result.getTenant_code(), result.getName());
            } else {
                log.info("根据ID查询MQ生产者日志未找到记录，ID: {}, 租户: {}", id, searchCondition.getTenant_code());
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("根据ID查询MQ生产者日志失败，ID: {}, 租户: {}", 
                    searchCondition.getId(), searchCondition.getTenant_code(), e);
            throw new ClickHouseException("根据ID查询MQ生产者日志失败", e);
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 转换Vo对象到Entity对象，处理特殊字段类型转换
     */
    private SLogMqProducerClickHouseEntity convertVoToEntity(SLogMqProducerClickHouseVo vo) {
        // 基础属性拷贝
        SLogMqProducerClickHouseEntity entity = (SLogMqProducerClickHouseEntity) BeanUtilsSupport.copyProperties(vo, SLogMqProducerClickHouseEntity.class);
        
        return entity;
    }

}