package com.xinyirun.scm.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseItemEntity;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseItemVo;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseItemMapper;
import com.xinyirun.scm.common.utils.UuidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文档处理 Service
 *
 * @author SCM AI Team
 * @since 2025-10-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentProcessingService {

    private final AiKnowledgeBaseItemMapper itemMapper;
    private final KnowledgeBaseService knowledgeBaseService;

    /**
     * 单文档上传（给Controller调用）
     */
    @Transactional(rollbackFor = Exception.class)
    public AiKnowledgeBaseItemVo uploadDoc(String kbUuid, Boolean indexAfterUpload, MultipartFile file, List<String> indexTypeList) {
        try {
            return knowledgeBaseService.uploadDoc(kbUuid, indexAfterUpload, file, indexTypeList);
        } catch (Exception e) {
            log.error("上传文档失败", e);
            throw new RuntimeException("上传文档失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从URL创建文档（给Controller调用）
     */
    @Transactional(rollbackFor = Exception.class)
    public AiKnowledgeBaseItemVo uploadDocFromUrl(String kbUuid, String fileUrl, String fileName, Long fileSize, Boolean indexAfterUpload, List<String> indexTypeList) {
        try {
            return knowledgeBaseService.uploadDocFromUrl(kbUuid, fileUrl, fileName, fileSize, indexAfterUpload, indexTypeList);
        } catch (Exception e) {
            log.error("从URL创建文档失败", e);
            throw new RuntimeException("从URL创建文档失败: " + e.getMessage(), e);
        }
    }

    /**
     * 保存或更新文档
     */
    @Transactional(rollbackFor = Exception.class)
    public AiKnowledgeBaseItemVo saveOrUpdate(AiKnowledgeBaseItemVo vo) {
        AiKnowledgeBaseItemEntity entity = new AiKnowledgeBaseItemEntity();
        BeanUtils.copyProperties(vo, entity);

        if (entity.getItemUuid() == null || entity.getItemUuid().isEmpty()) {
            // 新增
            entity.setItemUuid(UuidUtil.createShort());
            entity.setEmbeddingStatus(0);
            itemMapper.insert(entity);
        } else {
            // 更新
            itemMapper.updateById(entity);
        }

        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 搜索文档
     */
    public IPage<AiKnowledgeBaseItemVo> search(String kbUuid, String keyword, Integer currentPage, Integer pageSize) {
        Page<AiKnowledgeBaseItemEntity> page = new Page<>(currentPage, pageSize);

        LambdaQueryWrapper<AiKnowledgeBaseItemEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseItemEntity::getKbUuid, kbUuid);

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(AiKnowledgeBaseItemEntity::getTitle, keyword)
                    .or()
                    .like(AiKnowledgeBaseItemEntity::getSourceFileName, keyword));
        }

        wrapper.orderByDesc(AiKnowledgeBaseItemEntity::getCreateTime);

        IPage<AiKnowledgeBaseItemEntity> entityPage = itemMapper.selectPage(page, wrapper);

        // 转换为VO
        return entityPage.convert(entity -> {
            AiKnowledgeBaseItemVo voItem = new AiKnowledgeBaseItemVo();
            BeanUtils.copyProperties(entity, voItem);
            return voItem;
        });
    }

    /**
     * 根据UUID获取文档
     */
    public AiKnowledgeBaseItemVo getByUuid(String uuid) {
        LambdaQueryWrapper<AiKnowledgeBaseItemEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseItemEntity::getItemUuid, uuid);

        AiKnowledgeBaseItemEntity entity = itemMapper.selectOne(wrapper);

        if (entity == null) {
            return null;
        }

        AiKnowledgeBaseItemVo vo = new AiKnowledgeBaseItemVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 软删除文档
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean softDelete(String uuid) {
        LambdaQueryWrapper<AiKnowledgeBaseItemEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseItemEntity::getItemUuid, uuid);

        return itemMapper.delete(wrapper) > 0;
    }
}
