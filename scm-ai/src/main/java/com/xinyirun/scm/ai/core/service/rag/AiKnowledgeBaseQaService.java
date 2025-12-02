package com.xinyirun.scm.ai.core.service.rag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseQaEntity;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseQaVo;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseVo;
import com.xinyirun.scm.ai.bean.vo.request.QARecordRequestVo;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseMapper;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseQaMapper;
import com.xinyirun.scm.ai.core.service.KnowledgeBaseService;
import com.xinyirun.scm.common.utils.UuidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 知识库问答记录服务类
 *
 * 
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiKnowledgeBaseQaService extends ServiceImpl<AiKnowledgeBaseQaMapper, AiKnowledgeBaseQaEntity> {

    private final KnowledgeBaseService knowledgeBaseService;

    /**
     * 创建知识库问答记录（基础方法）
     *
     * @param entity 问答记录实体
     * @return 是否成功
     */
    public boolean add(AiKnowledgeBaseQaEntity entity) {
        return this.save(entity);
    }

    /**
     * 创建知识库问答记录（业务方法）
     *
     * @param kbUuid 知识库UUID
     * @param req 问答请求
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 问答记录VO
     */
    public AiKnowledgeBaseQaVo add(String kbUuid, QARecordRequestVo req, Long userId, Long tenantId) {
        // 1. 查询知识库（验证是否存在）
        AiKnowledgeBaseVo kb = knowledgeBaseService.getByUuid(kbUuid);
        if (kb == null) {
            log.error("知识库不存在，kbUuid: {}", kbUuid);
            throw new RuntimeException("知识库不存在");
        }

        // 2. 创建问答记录实体
        AiKnowledgeBaseQaEntity entity = new AiKnowledgeBaseQaEntity();
        entity.setUuid(UuidUtil.createShort());
        entity.setKbId(kb.getId());
        entity.setKbUuid(kbUuid);
        entity.setQuestion(req.getQuestion());
        entity.setUserId(userId);

        // 初始化必填字段（在SSE流式问答时会填充）
        entity.setPrompt("");
        entity.setAnswer("");
        entity.setPromptTokens(0);
        entity.setAnswerTokens(0);
        entity.setSourceFileIds("");

        // 设置AI模型ID：直接使用知识库配置的默认模型（对齐AI Chat机制）
        if (StringUtils.isBlank(kb.getIngestModelId())) {
            log.error("知识库未配置默认AI模型，kbUuid: {}", kbUuid);
            throw new RuntimeException("知识库未配置AI模型，请先配置知识库的AI模型");
        }
        entity.setAiModelId(kb.getIngestModelId());
        log.debug("使用知识库默认模型: {}", kb.getIngestModelId());

        // 时间戳
        long currentTime = System.currentTimeMillis();
        entity.setCreateTime(currentTime);
        entity.setUpdateTime(currentTime);
        entity.setIsDeleted(0);
        entity.setEnableStatus(1);

        // 3. 保存到数据库
        boolean success = this.save(entity);

        if (!success) {
            log.error("创建问答记录失败，kbUuid: {}, question: {}", kbUuid, req.getQuestion());
            throw new RuntimeException("创建问答记录失败");
        }

        log.info("创建问答记录成功，qaUuid: {}, kbUuid: {}, userId: {}",
                entity.getUuid(), kbUuid, userId);

        // 4. 转换为VO返回
        AiKnowledgeBaseQaVo vo = new AiKnowledgeBaseQaVo();
        vo.setId(entity.getId());
        vo.setUuid(entity.getUuid());
        vo.setKbId(entity.getKbId());
        vo.setKbUuid(entity.getKbUuid());
        vo.setQuestion(entity.getQuestion());
        vo.setUserId(entity.getUserId());
        vo.setAiModelId(entity.getAiModelId());
        vo.setCreateTime(entity.getCreateTime());

        return vo;
    }

    /**
     * 搜索问答记录
     *
     * @param kbUuid 知识库UUID
     * @param keyword 搜索关键词（模糊匹配question字段）
     * @param userId 用户ID（只查询该用户的问答记录）
     * @param currentPage 当前页码
     * @param pageSize 每页数量
     * @return 分页的问答记录列表
     */
    public IPage<AiKnowledgeBaseQaVo> search(String kbUuid, String keyword, Long userId,
                                              Integer currentPage, Integer pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<AiKnowledgeBaseQaEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseQaEntity::getKbUuid, kbUuid);
        wrapper.eq(AiKnowledgeBaseQaEntity::getUserId, userId);
        wrapper.eq(AiKnowledgeBaseQaEntity::getIsDeleted, 0);

        // 关键词模糊搜索
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(AiKnowledgeBaseQaEntity::getQuestion, keyword);
        }

        // 按更新时间降序
        wrapper.orderByDesc(AiKnowledgeBaseQaEntity::getUpdateTime);

        // 分页查询
        Page<AiKnowledgeBaseQaEntity> page = this.page(
                new Page<>(currentPage, pageSize), wrapper
        );

        // 转换为VO
        IPage<AiKnowledgeBaseQaVo> result = page.convert(entity -> {
            AiKnowledgeBaseQaVo vo = new AiKnowledgeBaseQaVo();
            vo.setId(entity.getId());
            vo.setUuid(entity.getUuid());
            vo.setKbId(entity.getKbId());
            vo.setKbUuid(entity.getKbUuid());
            vo.setQuestion(entity.getQuestion());
            vo.setPrompt(entity.getPrompt());
            vo.setPromptTokens(entity.getPromptTokens());
            vo.setAnswer(entity.getAnswer());
            vo.setAnswerTokens(entity.getAnswerTokens());
            vo.setUserId(entity.getUserId());
            vo.setAiModelId(entity.getAiModelId());
            vo.setCreateTime(entity.getCreateTime());

            return vo;
        });

        return result;
    }

    /**
     * 软删除问答记录
     *
     * @param qaUuid 问答记录UUID
     * @param userId 用户ID（权限校验）
     * @return 是否成功
     */
    public boolean softDelete(String qaUuid, Long userId) {
        // 查询问答记录（校验权限）
        LambdaQueryWrapper<AiKnowledgeBaseQaEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiKnowledgeBaseQaEntity::getUuid, qaUuid);
        queryWrapper.eq(AiKnowledgeBaseQaEntity::getIsDeleted, 0);
        AiKnowledgeBaseQaEntity exist = this.getOne(queryWrapper);

        if (exist == null) {
            log.error("问答记录不存在，qaUuid: {}", qaUuid);
            throw new RuntimeException("问答记录不存在");
        }

        // 权限校验：只能删除自己的记录
        if (!exist.getUserId().equals(userId)) {
            log.error("无权删除他人的问答记录，qaUuid: {}, userId: {}, ownerId: {}",
                    qaUuid, userId, exist.getUserId());
            throw new RuntimeException("无权删除他人的问答记录");
        }

        // 软删除
        exist.setIsDeleted(1);
        exist.setUpdateTime(System.currentTimeMillis());
        boolean success = this.updateById(exist);

        if (success) {
            log.info("删除问答记录成功，qaUuid: {}, userId: {}", qaUuid, userId);
        }

        return success;
    }

    /**
     * 清空当前用户的所有问答记录
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    public boolean clearByCurrentUser(Long userId) {
        // 查询所有未删除的记录
        LambdaQueryWrapper<AiKnowledgeBaseQaEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiKnowledgeBaseQaEntity::getUserId, userId);
        queryWrapper.eq(AiKnowledgeBaseQaEntity::getIsDeleted, 0);
        java.util.List<AiKnowledgeBaseQaEntity> records = this.list(queryWrapper);

        if (records.isEmpty()) {
            log.info("用户没有问答记录需要清空，userId: {}", userId);
            return true;
        }

        // 逐个更新为已删除状态
        long currentTime = System.currentTimeMillis();
        for (AiKnowledgeBaseQaEntity record : records) {
            record.setIsDeleted(1);
            record.setUpdateTime(currentTime);
        }

        boolean success = this.updateBatchById(records);

        if (success) {
            log.info("清空用户问答记录成功，userId: {}, 清空数量: {}", userId, records.size());
        }

        return success;
    }

    /**
     * 根据UUID查询问答记录
     *
     * @param qaUuid 问答记录UUID
     * @return 问答记录实体，不存在则返回null
     */
    public AiKnowledgeBaseQaEntity getByQaUuid(String qaUuid) {
        LambdaQueryWrapper<AiKnowledgeBaseQaEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseQaEntity::getUuid, qaUuid);
        wrapper.eq(AiKnowledgeBaseQaEntity::getIsDeleted, 0);
        return this.getOne(wrapper);
    }
}
