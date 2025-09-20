package com.xinyirunscm.scm.clickhouse.service.datachange;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeMainClickHouseVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirunscm.scm.clickhouse.entity.datachange.SLogDataChangeMainClickHouseEntity;
import com.xinyirunscm.scm.clickhouse.exception.ClickHouseException;
import com.xinyirunscm.scm.clickhouse.repository.datachange.SLogDataChangeMainClickHouseRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 数据变更主日志 ClickHouse 服务类
 * 专门处理 s_log_data_change_main 表的所有业务逻辑
 * 
 * @author SCM System
 * @since 1.0.39
 * @updated 2025-09-19 - 数据变更日志架构实现
 */
@Slf4j
@Service
public class SLogDataChangeMainClickHouseService {

    private final SLogDataChangeMainClickHouseRepository sLogDataChangeMainRepository;

    public SLogDataChangeMainClickHouseService(SLogDataChangeMainClickHouseRepository sLogDataChangeMainRepository) {
        this.sLogDataChangeMainRepository = sLogDataChangeMainRepository;
    }

    // ==================== 插入操作 ====================

    /**
     * 插入数据变更主日志 - 接受 SLogDataChangeMainClickHouseVo 参数
     * 在方法内部转换为 SLogDataChangeMainClickHouseEntity
     */
    public void insert(SLogDataChangeMainClickHouseVo mainLogVo) {
        try {
            // 转换为 ClickHouse 实体
            SLogDataChangeMainClickHouseEntity mainLogEntity = convertVoToEntity(mainLogVo);
            
            // 执行插入
            sLogDataChangeMainRepository.insert(mainLogEntity);
            
            log.info("插入数据变更主日志成功，request_id: {}, 单号类型: {}, 单号: {}", 
                    mainLogEntity.getRequest_id(), mainLogEntity.getOrder_type(), mainLogEntity.getOrder_code());
                       
        } catch (Exception e) {
            log.error("插入数据变更主日志失败，request_id: {}", 
                     mainLogVo != null ? mainLogVo.getRequest_id() : "null", e);
            throw new ClickHouseException("插入数据变更主日志失败", e);
        }
    }

    /**
     * 异步插入数据变更主日志 - 高性能场景
     */
    @Async("clickhouseTaskExecutor")
    public CompletableFuture<Void> insertAsync(SLogDataChangeMainClickHouseVo mainLogVo) {
        try {
            insert(mainLogVo);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("异步插入数据变更主日志失败", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 批量插入数据变更主日志 - 最佳性能方案
     */
    public void batchInsert(List<SLogDataChangeMainClickHouseVo> mainLogVos) {
        if (mainLogVos == null || mainLogVos.isEmpty()) {
            log.warn("批量插入数据变更主日志数据为空，跳过操作");
            return;
        }

        try {
            // 转换为 ClickHouse 实体列表
            List<SLogDataChangeMainClickHouseEntity> mainLogEntities = mainLogVos.stream()
                    .map(this::convertVoToEntity)
                    .toList();
            
            // 执行批量插入
            sLogDataChangeMainRepository.batchInsert(mainLogEntities);
            
            log.info("批量插入数据变更主日志成功，数量: {}", mainLogVos.size());
            
        } catch (Exception e) {
            log.error("批量插入数据变更主日志失败，数量: {}", mainLogVos.size(), e);
            throw new ClickHouseException("批量插入数据变更主日志失败", e);
        }
    }

    /**
     * 异步批量插入数据变更主日志 - 高吞吐量场景
     */
    @Async("clickhouseTaskExecutor")
    public CompletableFuture<Void> batchInsertAsync(List<SLogDataChangeMainClickHouseVo> mainLogVos) {
        try {
            batchInsert(mainLogVos);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("异步批量插入数据变更主日志失败", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    // ==================== 查询操作 ====================

    /**
     * 分页查询数据变更主日志 - 支持条件查询和排序
     * Controller层的核心方法，提供综合查询能力
     * 
     * @param searchCondition 查询条件（包含分页参数）
     * @return 分页结果包含VO列表
     */
    public IPage<SLogDataChangeMainClickHouseVo> selectPage(SLogDataChangeMainClickHouseVo searchCondition) {
        try {
            
            IPage<SLogDataChangeMainClickHouseVo> result = sLogDataChangeMainRepository.selectPageWithParams(searchCondition);
            
            log.info("分页查询数据变更主日志成功，页号: {}, 页大小: {}, 总记录数: {}, 查询条件: [单号类型: {}, 单号: {}, 名称: {}, 更新人名称: {}, 更新人ID: {}]", 
                    searchCondition.getPageCondition().getCurrent(),
                    searchCondition.getPageCondition().getSize(),
                    result.getTotal(),
                    searchCondition.getOrder_type(),
                    searchCondition.getOrder_code(),
                    searchCondition.getName(),
                    searchCondition.getU_name(),
                    searchCondition.getU_id());
            
            return result;
            
        } catch (Exception e) {
            log.error("分页查询数据变更主日志失败，查询条件: [单号类型: {}, 单号: {}, 名称: {}, 更新人名称: {}, 更新人ID: {}]", 
                    searchCondition.getOrder_type(), 
                    searchCondition.getOrder_code(), 
                    searchCondition.getName(),
                    searchCondition.getU_name(),
                    searchCondition.getU_id(), e);
            throw new ClickHouseException("分页查询数据变更主日志失败", e);
        }
    }

    /**
     * 根据ID查询单条数据变更主日志记录
     * Controller层的核心方法，提供根据ID的精确查询
     * 
     * @param searchCondition 查询条件VO（包含id字段）
     * @return 找到的VO对象，未找到返回null
     */
    public SLogDataChangeMainClickHouseVo getById(SLogDataChangeMainClickHouseVo searchCondition) {
        try {
            // 验证输入参数
            if (searchCondition == null || StringUtils.isBlank(searchCondition.getId())) {
                log.warn("根据ID查询数据变更主日志失败：查询条件为空或ID为空");
                return null;
            }
            
            String id = searchCondition.getId().trim();
            
            // 传递完整的查询条件（包含tenant_code）到Repository层
            SLogDataChangeMainClickHouseVo result = sLogDataChangeMainRepository.getById(searchCondition);
            
            if (result != null) {
                log.info("根据ID查询数据变更主日志成功，ID: {}, 单号: {}, 单号类型: {}", 
                        id, result.getOrder_code(), result.getOrder_type());
            } else {
                log.info("根据ID查询数据变更主日志未找到记录，ID: {}", id);
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("根据ID查询数据变更主日志失败，ID: {}", 
                    searchCondition != null ? searchCondition.getId() : "null", e);
            throw new ClickHouseException("根据ID查询数据变更主日志失败", e);
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 转换Vo对象到Entity对象，处理特殊字段类型转换
     */
    private SLogDataChangeMainClickHouseEntity convertVoToEntity(SLogDataChangeMainClickHouseVo vo) {
        // 基础属性拷贝
        SLogDataChangeMainClickHouseEntity entity = (SLogDataChangeMainClickHouseEntity) BeanUtilsSupport.copyProperties(vo, SLogDataChangeMainClickHouseEntity.class);
        
        // 如果需要设置默认值或特殊处理，在这里添加
        if (entity.getC_time() == null) {
            entity.setC_time(LocalDateTime.now());
        }
        
        return entity;
    }
}