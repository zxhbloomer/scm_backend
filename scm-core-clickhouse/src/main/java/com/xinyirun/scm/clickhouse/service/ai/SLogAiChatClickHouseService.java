package com.xinyirun.scm.clickhouse.service.ai;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.clickhouse.vo.ai.SLogAiChatVo;
import com.xinyirun.scm.clickhouse.entity.ai.SLogAiChatClickHouseEntity;
import com.xinyirun.scm.clickhouse.exception.ClickHouseException;
import com.xinyirun.scm.clickhouse.repository.ai.SLogAiChatClickHouseRepository;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * AI聊天日志 ClickHouse 服务类
 * 专门处理 s_log_ai_chat 表的所有业务逻辑
 *
 * <p>职责：
 * - 插入操作：单条插入、批量插入、异步插入
 * - 查询操作：分页查询、按ID查询
 * - 数据转换：VO → Entity（用于插入）
 *
 * <p>业务特点：
 * - 简化Consumer逻辑：Consumer只需调用insert方法
 * - 数据转换集中管理：统一的convertVoToEntity方法
 * - 异常处理统一：所有操作统一异常处理策略
 *
 * <p>使用场景：
 * - Consumer消费MQ消息后调用insert方法
 * - Controller层调用selectPage/getById进行查询
 * - 批量同步场景调用batchInsert方法
 *
 * @author AI Chat Logging System
 * @since 2025-09-30
 * @see com.xinyirun.scm.clickhouse.repository.ai.SLogAiChatClickHouseRepository
 * @see com.xinyirun.scm.mq.consumer.business.log.ai.LogAiChatConsumer
 */
@Slf4j
@Service
public class SLogAiChatClickHouseService {

    private final SLogAiChatClickHouseRepository sLogAiChatRepository;

    public SLogAiChatClickHouseService(SLogAiChatClickHouseRepository sLogAiChatRepository) {
        this.sLogAiChatRepository = sLogAiChatRepository;
    }

    // ==================== 插入操作 ====================

    /**
     * 插入AI聊天日志 - 接受 SLogAiChatVo 参数
     * 在方法内部转换为 SLogAiChatClickHouseEntity
     *
     * <p>Consumer端核心方法，消费MQ消息后直接调用此方法
     *
     * <p>处理流程：
     * 1. VO → Entity转换（使用BeanUtilsSupport自动映射）
     * 2. 调用Repository执行ClickHouse插入
     * 3. 记录插入日志和异常
     *
     * @param aiChatLogVo AI聊天日志VO对象
     * @throws ClickHouseException 插入失败时抛出
     */
    public void insert(SLogAiChatVo aiChatLogVo) {
        try {
            // 转换为 ClickHouse 实体
            SLogAiChatClickHouseEntity aiChatLogEntity = convertVoToEntity(aiChatLogVo);

            // 执行插入
            sLogAiChatRepository.insert(aiChatLogEntity);

            log.info("插入AI聊天日志成功，conversation_id: {}, type: {}, tenant_code: {}",
                    aiChatLogEntity.getConversation_id(),
                    aiChatLogEntity.getType(),
                    aiChatLogEntity.getTenant_code());

        } catch (Exception e) {
            log.error("插入AI聊天日志失败，conversation_id: {}, type: {}",
                     aiChatLogVo != null ? aiChatLogVo.getConversation_id() : "null",
                     aiChatLogVo != null ? aiChatLogVo.getType() : "null", e);
            throw new ClickHouseException("插入AI聊天日志失败", e);
        }
    }

    /**
     * 批量插入AI聊天日志 - 最佳性能方案
     *
     * <p>性能优势：
     * - ClickHouse批量插入性能远优于单条插入
     * - 适合历史数据批量同步
     * - 适合Consumer累积批量处理
     *
     * <p>使用场景：
     * - 历史数据迁移
     * - Consumer批量消费优化
     *
     * @param aiChatLogVos AI聊天日志VO对象列表
     * @throws ClickHouseException 批量插入失败时抛出
     */
    public void batchInsert(List<SLogAiChatVo> aiChatLogVos) {
        if (aiChatLogVos == null || aiChatLogVos.isEmpty()) {
            log.warn("批量插入AI聊天日志数据为空，跳过操作");
            return;
        }

        try {
            // 转换为 ClickHouse 实体列表
            List<SLogAiChatClickHouseEntity> aiChatLogEntities = aiChatLogVos.stream()
                    .map(this::convertVoToEntity)
                    .toList();

            // 执行批量插入
            sLogAiChatRepository.batchInsert(aiChatLogEntities);

            log.info("批量插入AI聊天日志成功，数量: {}", aiChatLogVos.size());

        } catch (Exception e) {
            log.error("批量插入AI聊天日志失败，数量: {}", aiChatLogVos.size(), e);
            throw new ClickHouseException("批量插入AI聊天日志失败", e);
        }
    }

    // ==================== 查询操作 ====================

    /**
     * 分页查询AI聊天日志 - 支持条件查询和排序
     * Controller层的核心方法，提供综合查询能力
     *
     * <p>支持查询条件：
     * - conversation_id（对话ID）
     * - type（记录类型：USER/ASSISTANT）
     * - base_name（模型名称）
     * - c_id（创建人ID）
     * - tenant_code（租户编码，必填）
     * - 时间范围（startTime、endTime）
     *
     * @param searchCondition 查询条件（包含分页参数）
     * @return 分页结果包含VO列表
     * @throws ClickHouseException 查询失败时抛出
     */
    public IPage<SLogAiChatVo> selectPage(SLogAiChatVo searchCondition) {
        try {
            IPage<SLogAiChatVo> result = sLogAiChatRepository.selectPageWithParams(searchCondition);

            log.info("分页查询AI聊天日志成功，页号: {}, 页大小: {}, 总记录数: {}, 查询条件: [对话ID: {}, 类型: {}, 模型: {}, 租户: {}]",
                    searchCondition.getPageCondition().getCurrent(),
                    searchCondition.getPageCondition().getSize(),
                    result.getTotal(),
                    searchCondition.getConversation_id(),
                    searchCondition.getType(),
                    searchCondition.getBase_name(),
                    searchCondition.getTenant_code());

            return result;

        } catch (Exception e) {
            log.error("分页查询AI聊天日志失败，查询条件: [对话ID: {}, 类型: {}, 模型: {}, 租户: {}]",
                    searchCondition.getConversation_id(),
                    searchCondition.getType(),
                    searchCondition.getBase_name(),
                    searchCondition.getTenant_code(), e);
            throw new ClickHouseException("分页查询AI聊天日志失败", e);
        }
    }

    /**
     * 根据ID查询单条AI聊天日志记录
     * Controller层的核心方法，提供根据ID的精确查询
     *
     * <p>多租户隔离：
     * - 查询条件必须包含tenant_code
     * - Repository层强制添加tenant_code过滤
     *
     * @param searchCondition 查询条件VO（包含id字段和tenant_code）
     * @return 找到的VO对象，未找到返回null
     * @throws ClickHouseException 查询失败时抛出
     */
    public SLogAiChatVo getById(SLogAiChatVo searchCondition) {
        try {
            // 验证输入参数
            if (searchCondition == null || StringUtils.isBlank(searchCondition.getId())) {
                log.warn("根据ID查询AI聊天日志失败：查询条件为空或ID为空");
                return null;
            }

            String id = searchCondition.getId().trim();

            // 传递完整的查询条件（包含tenant_code）到Repository层
            SLogAiChatVo result = sLogAiChatRepository.getById(searchCondition);

            if (result != null) {
                log.info("根据ID查询AI聊天日志成功，ID: {}, 对话ID: {}, 类型: {}",
                        id, result.getConversation_id(), result.getType());
            } else {
                log.info("根据ID查询AI聊天日志未找到记录，ID: {}", id);
            }

            return result;

        } catch (Exception e) {
            log.error("根据ID查询AI聊天日志失败，ID: {}",
                    searchCondition != null ? searchCondition.getId() : "null", e);
            throw new ClickHouseException("根据ID查询AI聊天日志失败", e);
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 转换Vo对象到Entity对象
     *
     * <p>转换说明：
     * - 使用BeanUtilsSupport.copyProperties自动映射同名字段
     * - VO和Entity字段名完全一致，无需手动映射
     * - VO中已包含所有必要数据（包括type字段），无需额外构建
     *
     * <p>参考设计：
     * - 符合research.md中"VO中已包含所有数据（包括type字段）"的设计原则
     * - 避免重复的业务逻辑，保持日志记录的纯粹性
     *
     * @param vo AI聊天日志VO对象
     * @return AI聊天日志Entity对象
     */
    private SLogAiChatClickHouseEntity convertVoToEntity(SLogAiChatVo vo) {
        // 基础属性拷贝
        SLogAiChatClickHouseEntity entity = (SLogAiChatClickHouseEntity)
                BeanUtilsSupport.copyProperties(vo, SLogAiChatClickHouseEntity.class);

        // 特殊字段处理（如果需要）
        // 注意：VO中已包含所有数据（包括type字段），无需额外构建

        return entity;
    }
}