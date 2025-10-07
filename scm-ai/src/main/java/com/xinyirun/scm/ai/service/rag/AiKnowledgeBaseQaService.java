package com.xinyirun.scm.ai.service.rag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseEntity;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseQaEntity;
import com.xinyirun.scm.ai.bean.vo.request.QARecordRequestVo;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseQaVo;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseVo;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseMapper;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseQaMapper;
import com.xinyirun.scm.common.utils.UuidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 知识库问答记录服务类
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiKnowledgeBaseQaService extends ServiceImpl<AiKnowledgeBaseQaMapper, AiKnowledgeBaseQaEntity> {

    private final AiKnowledgeBaseMapper knowledgeBaseMapper;
    private final com.xinyirun.scm.ai.service.KnowledgeBaseService knowledgeBaseService;

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

        // 设置AI模型ID（如果提供）
        if (StringUtils.isNotBlank(req.getAiModelId())) {
            entity.setAiModelId(req.getAiModelId());
        }

        // 设置租户ID
        entity.setTenantId(tenantId);

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
        LambdaUpdateWrapper<AiKnowledgeBaseQaEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AiKnowledgeBaseQaEntity::getUuid, qaUuid);
        updateWrapper.eq(AiKnowledgeBaseQaEntity::getUserId, userId);
        updateWrapper.set(AiKnowledgeBaseQaEntity::getIsDeleted, 1);
        updateWrapper.set(AiKnowledgeBaseQaEntity::getUpdateTime, System.currentTimeMillis());

        boolean success = this.update(updateWrapper);

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
        LambdaUpdateWrapper<AiKnowledgeBaseQaEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AiKnowledgeBaseQaEntity::getUserId, userId);
        wrapper.eq(AiKnowledgeBaseQaEntity::getIsDeleted, 0);
        wrapper.set(AiKnowledgeBaseQaEntity::getIsDeleted, 1);
        wrapper.set(AiKnowledgeBaseQaEntity::getUpdateTime, System.currentTimeMillis());

        boolean success = this.update(wrapper);

        if (success) {
            log.info("清空用户问答记录成功，userId: {}", userId);
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

    /**
     * 根据UUID查询问答记录（不存在则抛异常）
     *
     * @param qaUuid 问答记录UUID
     * @return 问答记录实体
     */
    public AiKnowledgeBaseQaEntity getOrThrow(String qaUuid) {
        AiKnowledgeBaseQaEntity exist = getByQaUuid(qaUuid);
        if (exist == null) {
            log.error("问答记录不存在，qaUuid: {}", qaUuid);
            throw new RuntimeException("问答记录不存在");
        }
        return exist;
    }
}
