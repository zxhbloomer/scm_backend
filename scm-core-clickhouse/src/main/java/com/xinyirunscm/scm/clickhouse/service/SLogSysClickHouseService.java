package com.xinyirunscm.scm.clickhouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.SLogSysClickHouseVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirunscm.scm.clickhouse.entity.SLogSysClickHouseEntity;
import com.xinyirunscm.scm.clickhouse.exception.ClickHouseException;
import com.xinyirunscm.scm.clickhouse.repository.SLogSysClickHouseRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 系统日志 ClickHouse 服务类
 * 专门处理 s_log_sys 表的所有业务逻辑
 * 
 * @author SCM System
 * @since 1.0.39
 */
@Slf4j
@Service
public class SLogSysClickHouseService {

    private final SLogSysClickHouseRepository sLogSysRepository;

    public SLogSysClickHouseService(SLogSysClickHouseRepository sLogSysRepository) {
        this.sLogSysRepository = sLogSysRepository;
    }

    // ==================== 插入操作 ====================

    /**
     * 插入系统日志 - 接受 SLogSysClickHouseVo 参数
     * 在方法内部转换为 SLogSysClickHouseEntity
     */
    public void insert(SLogSysClickHouseVo sysLogVo) {
        try {
            // 转换为 ClickHouse 实体
            SLogSysClickHouseEntity sysLogEntity = convertVoToEntity(sysLogVo);
            
            // 执行插入
            sLogSysRepository.insert(sysLogEntity);
            
            log.info("插入系统日志成功，request_id: {}, 类型: {}, 用户: {}", 
                    sysLogEntity.getRequest_id(), sysLogEntity.getType(), sysLogEntity.getUser_name());
                       
        } catch (Exception e) {
            log.error("插入系统日志失败，request_id: {}", 
                     sysLogVo != null ? sysLogVo.getRequest_id() : "null", e);
            throw new ClickHouseException("插入系统日志失败", e);
        }
    }


    /**
     * 异步插入系统日志 - 高性能场景
     */
    @Async("clickhouseTaskExecutor")
    public CompletableFuture<Void> insertAsync(SLogSysClickHouseVo sysLogVo) {
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
    public void batchInsert(List<SLogSysClickHouseVo> sysLogVos) {
        if (sysLogVos == null || sysLogVos.isEmpty()) {
            log.warn("批量插入系统日志数据为空，跳过操作");
            return;
        }

        try {
            // 转换为 ClickHouse 实体列表
            List<SLogSysClickHouseEntity> sysLogEntities = sysLogVos.stream()
                    .map(this::convertVoToEntity)
                    .toList();
            
            // 执行批量插入
            sLogSysRepository.batchInsert(sysLogEntities);
            
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
    public CompletableFuture<Void> batchInsertAsync(List<SLogSysClickHouseVo> sysLogVos) {
        try {
            batchInsert(sysLogVos);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("异步批量插入系统日志失败", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    // ==================== 查询操作 ====================

    /**
     * 分页查询系统日志 - 支持条件查询和排序
     * Controller层的核心方法，提供综合查询能力
     * 
     * @param searchCondition 查询条件（包含分页参数）
     * @return 分页结果包含VO列表
     */
    public IPage<SLogSysClickHouseVo> selectPage(SLogSysClickHouseVo searchCondition) {
        try {
            
            IPage<SLogSysClickHouseVo> result = sLogSysRepository.selectPageWithParams(searchCondition);
            
            log.info("分页查询系统日志成功，页号: {}, 页大小: {}, 总记录数: {}, 查询条件: [用户名: {}, 用户名称: {}, 类型: {}, 类名: {}, 方法名: {}, URL: {}, 操作: {}]", 
                    searchCondition.getPageCondition().getCurrent(),
                    searchCondition.getPageCondition().getSize(),
                    result.getTotal(),
                    searchCondition.getUser_name(),
                    searchCondition.getStaff_name(),
                    searchCondition.getType(),
                    searchCondition.getClass_name(),
                    searchCondition.getClass_method(),
                    searchCondition.getUrl(),
                    searchCondition.getOperation());
            
            return result;
            
        } catch (Exception e) {
            log.error("分页查询系统日志失败，查询条件: [用户名: {}, 用户名称: {}, 类型: {}, 类名: {}, 方法名: {}, URL: {}, 操作: {}]", 
                    searchCondition.getUser_name(), 
                    searchCondition.getStaff_name(), 
                    searchCondition.getType(),
                    searchCondition.getClass_name(),
                    searchCondition.getClass_method(),
                    searchCondition.getUrl(),
                    searchCondition.getOperation(), e);
            throw new ClickHouseException("分页查询系统日志失败", e);
        }
    }

    /**
     * 根据ID查询单条系统日志记录
     * Controller层的核心方法，提供根据ID的精确查询
     * 
     * @param searchCondition 查询条件VO（包含id字段）
     * @return 找到的VO对象，未找到返回null
     */
    public SLogSysClickHouseVo getById(SLogSysClickHouseVo searchCondition) {
        try {
            // 验证输入参数
            if (searchCondition == null || StringUtils.isBlank(searchCondition.getId())) {
                log.warn("根据ID查询系统日志失败：查询条件为空或ID为空");
                return null;
            }
            
            String id = searchCondition.getId().trim();
            
            // 传递完整的查询条件（包含tenant_code）到Repository层
            SLogSysClickHouseVo result = sLogSysRepository.getById(searchCondition);
            
            if (result != null) {
                log.info("根据ID查询系统日志成功，ID: {}, 用户: {}, 类型: {}", 
                        id, result.getUser_name(), result.getType());
            } else {
                log.info("根据ID查询系统日志未找到记录，ID: {}", id);
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("根据ID查询系统日志失败，ID: {}", 
                    searchCondition != null ? searchCondition.getId() : "null", e);
            throw new ClickHouseException("根据ID查询系统日志失败", e);
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 转换Vo对象到Entity对象，处理特殊字段类型转换
     */
    private SLogSysClickHouseEntity convertVoToEntity(SLogSysClickHouseVo vo) {
        // 基础属性拷贝
        SLogSysClickHouseEntity entity = (SLogSysClickHouseEntity) BeanUtilsSupport.copyProperties(vo, SLogSysClickHouseEntity.class);
        
        // IP地址直接赋值（现在都是String类型）
        entity.setIp(vo.getIp());
        
        return entity;
    }

}