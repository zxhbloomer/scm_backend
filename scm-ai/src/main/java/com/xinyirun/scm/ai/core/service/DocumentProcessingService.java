package com.xinyirun.scm.ai.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseItemEntity;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseItemVo;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseVo;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseItemMapper;
import com.xinyirun.scm.ai.core.repository.elasticsearch.AiKnowledgeBaseEmbeddingRepository;
import com.xinyirun.scm.ai.core.repository.neo4j.KnowledgeBaseSegmentRepository;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.utils.UuidUtil;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
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
    private final SFileMapper sFileMapper;
    private final SFileInfoMapper sFileInfoMapper;
    private final ISFileService sFileService;
    private final AiKnowledgeBaseEmbeddingRepository embeddingRepository;
    private final KnowledgeBaseSegmentRepository segmentRepository;

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
            String tenantCode = DataSourceHelper.getCurrentDataSourceName();
            String uuid = UuidUtil.createShort();
            String itemUuid = tenantCode + "::" + uuid;
            entity.setItemUuid(itemUuid);
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

            // 查询并填充附件信息
            List<SFileInfoVo> files = sFileService.selectFileInfoBySerialTypeAndId(
                "ai_knowledge_base_item",
                entity.getId()
            );
            voItem.setDoc_att_files(files);

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

        // 查询并填充附件信息
        List<SFileInfoVo> files = sFileService.selectFileInfoBySerialTypeAndId(
            "ai_knowledge_base_item",
            entity.getId()
        );
        vo.setDoc_att_files(files);

        return vo;
    }

    /**
     * 物理删除知识项（包含MySQL、Elasticsearch、Neo4j三处数据）
     * <p>SCM系统统一使用物理删除</p>
     *
     * @param uuid 知识项UUID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean softDelete(String uuid) {
        log.info("删除知识项, uuid: {}", uuid);

        // 1. 查询实体（select-then-delete模式）
        LambdaQueryWrapper<AiKnowledgeBaseItemEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiKnowledgeBaseItemEntity::getItemUuid, uuid);
        AiKnowledgeBaseItemEntity entity = itemMapper.selectOne(queryWrapper);

        if (entity == null) {
            log.warn("知识项不存在, uuid: {}", uuid);
            return false;
        }

        // 2. 权限检查
        Long currentUserId = SecurityUtil.getStaff_id();
        if (currentUserId != null && entity.getCreateUser() != null) {
            if (!entity.getCreateUser().equals(String.valueOf(currentUserId))) {
                log.warn("无权删除知识项, uuid: {}, currentUser: {}, createUser: {}",
                    uuid, currentUserId, entity.getCreateUser());
                throw new RuntimeException("无权删除该知识项");
            }
        }

        // 3. 删除Elasticsearch向量数据
        try {
            long deletedCount = embeddingRepository.deleteByKbItemUuid(uuid);
            log.info("删除Elasticsearch向量数据, uuid: {}, 删除数量: {}", uuid, deletedCount);
        } catch (Exception e) {
            log.error("删除Elasticsearch向量数据失败, uuid: {}", uuid, e);
            throw new RuntimeException("删除向量数据失败: " + e.getMessage(), e);
        }

        // 4. 删除Neo4j图谱数据
        try {
            String tenantId = DataSourceHelper.getCurrentDataSourceName();
            Integer deletedCount = segmentRepository.deleteByItemUuidAndTenantId(uuid, tenantId);
            log.info("删除Neo4j图谱数据, uuid: {}, tenantId: {}, 删除数量: {}", uuid, tenantId, deletedCount);
        } catch (Exception e) {
            log.error("删除Neo4j图谱数据失败, uuid: {}", uuid, e);
            throw new RuntimeException("删除图谱数据失败: " + e.getMessage(), e);
        }

        // 5. 删除MySQL记录（物理删除）
        int result = itemMapper.deleteById(entity.getId());
        boolean success = result > 0;

        if (success) {
            log.info("知识项删除成功, uuid: {}", uuid);
        } else {
            log.warn("知识项删除失败, uuid: {}", uuid);
        }

        return success;
    }

    /**
     * 批量创建知识项（从前端上传的文件数组）
     *
     * @param kbUuid 知识库UUID
     * @param docAttFiles 文件信息数组（来自前端上传）
     * @param indexAfterUpload 是否立即索引
     * @return 创建的知识项列表
     */
    @Transactional(rollbackFor = Exception.class)
    public List<AiKnowledgeBaseItemVo> batchCreateItems(
            String kbUuid,
            List<SFileInfoVo> docAttFiles,
            Boolean indexAfterUpload) {

        List<AiKnowledgeBaseItemVo> results = new ArrayList<>();

        // 0. 查询知识库获取 kbId
        AiKnowledgeBaseVo kb = knowledgeBaseService.getByUuid(kbUuid);
        if (kb == null) {
            throw new RuntimeException("知识库不存在: " + kbUuid);
        }

        // 循环处理每个文件
        for (SFileInfoVo fileInfo : docAttFiles) {
            // 1. 创建知识项记录
            AiKnowledgeBaseItemEntity entity = new AiKnowledgeBaseItemEntity();
            String tenantCode = DataSourceHelper.getCurrentDataSourceName();
            String uuid = UuidUtil.createShort();
            String itemUuid = tenantCode + "::" + uuid;
            entity.setItemUuid(itemUuid);
            entity.setKbId(kb.getId());  // ✅ 设置知识库ID
            entity.setKbUuid(kbUuid);
            entity.setTitle(fileInfo.getFileName());  // 使用文件名作为标题
            entity.setSourceFileName(fileInfo.getFileName());
            entity.setCreateTime(System.currentTimeMillis());
            entity.setEmbeddingStatus(0);  // 待处理状态

            // 保存知识项
            itemMapper.insert(entity);

            // 2. 保存文件信息到s_file和s_file_info表
            saveItemFiles(entity.getId(), Arrays.asList(fileInfo));

            // 3. 如果需要立即索引
            if (Boolean.TRUE.equals(indexAfterUpload)) {
                // TODO: 触发索引任务
                log.info("知识项 {} 将被索引", entity.getItemUuid());
            }

            // 转换为VO并返回
            AiKnowledgeBaseItemVo vo = new AiKnowledgeBaseItemVo();
            BeanUtils.copyProperties(entity, vo);
            results.add(vo);
        }

        return results;
    }

    /**
     * 保存知识项的附件信息
     * 参考采购合同的 BPoContractServiceImpl.insertFile 方法
     *
     * @param itemId 知识项ID
     * @param docAttFiles 文件信息列表
     */
    private void saveItemFiles(Integer itemId, List<SFileInfoVo> docAttFiles) {
        if (docAttFiles == null || docAttFiles.isEmpty()) {
            return;
        }

        // 1. 创建s_file记录（逻辑文件层）
        SFileEntity sFileEntity = new SFileEntity();
        sFileEntity.setSerial_type("ai_knowledge_base_item");  // 业务表完整表名
        sFileEntity.setSerial_id(itemId);           // 关联知识项ID
        sFileMapper.insert(sFileEntity);

        // 2. 循环创建s_file_info记录（物理文件层）
        for (SFileInfoVo fileInfo : docAttFiles) {
            SFileInfoEntity sFileInfoEntity = new SFileInfoEntity();
            sFileInfoEntity.setF_id(sFileEntity.getId());          // 关联s_file.id
            sFileInfoEntity.setUrl(fileInfo.getUrl());             // 文件URL
            sFileInfoEntity.setFile_name(fileInfo.getFileName()); // 文件名
            sFileInfoEntity.setFile_size(fileInfo.getFile_size()); // 文件大小
            sFileInfoEntity.setTimestamp(fileInfo.getTimestamp());// 上传时间（支持版本）
            sFileInfoMapper.insert(sFileInfoEntity);
        }
    }
}
