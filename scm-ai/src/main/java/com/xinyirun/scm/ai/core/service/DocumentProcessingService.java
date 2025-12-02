package com.xinyirun.scm.ai.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseEntity;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseItemEntity;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseItemVo;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseVo;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseItemMapper;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseMapper;
import com.xinyirun.scm.ai.core.repository.elasticsearch.AiKnowledgeBaseEmbeddingRepository;
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
    private final AiKnowledgeBaseMapper kbMapper;
    private final KnowledgeBaseService knowledgeBaseService;
    private final SFileMapper sFileMapper;
    private final SFileInfoMapper sFileInfoMapper;
    private final ISFileService sFileService;
    private final AiKnowledgeBaseEmbeddingRepository embeddingRepository;
    private final Neo4jGraphIndexingService neo4jGraphIndexingService;

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
     *
     * @param vo 知识项VO
     * @param indexAfterCreate 新增后是否立即索引
     * @return 保存后的知识项VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiKnowledgeBaseItemVo saveOrUpdate(AiKnowledgeBaseItemVo vo, Boolean indexAfterCreate) {
        // 根据 kbUuid 查询知识库，获取 kbId
        if (vo.getKbUuid() == null || vo.getKbUuid().isEmpty()) {
            log.error("kbUuid不能为空");
            throw new RuntimeException("kbUuid不能为空");
        }

        AiKnowledgeBaseEntity kbEntity = kbMapper.selectByKbUuid(vo.getKbUuid());
        if (kbEntity == null) {
            log.error("知识库不存在, kbUuid: {}", vo.getKbUuid());
            throw new RuntimeException("知识库不存在: " + vo.getKbUuid());
        }

        boolean isNewItem = false;
        String itemUuid = null;

        if (vo.getItemUuid() == null || vo.getItemUuid().isEmpty()) {
            // 新增：使用VO数据创建新实体（SCM标准模式）
            AiKnowledgeBaseItemEntity entity = new AiKnowledgeBaseItemEntity();
            BeanUtils.copyProperties(vo, entity);

            String tenantCode = DataSourceHelper.getCurrentDataSourceName();
            String uuid = UuidUtil.createShort();
            itemUuid = tenantCode + "::" + uuid;
            entity.setItemUuid(itemUuid);
            entity.setKbId(kbEntity.getId().toString());
            entity.setEmbeddingStatus(0);

            itemMapper.insert(entity);
            isNewItem = true;

            // 返回插入后的实体
            BeanUtils.copyProperties(entity, vo);
        } else {
            // 更新：先查询完整实体（SCM标准模式）
            AiKnowledgeBaseItemEntity existEntity = itemMapper.selectByItemUuid(vo.getItemUuid());
            if (existEntity == null) {
                log.error("文档不存在, itemUuid: {}", vo.getItemUuid());
                throw new RuntimeException("文档不存在，无法更新");
            }

            // 更新需要修改的字段
            existEntity.setKbId(kbEntity.getId().toString());
            existEntity.setTitle(vo.getTitle());
            existEntity.setBrief(vo.getBrief());
            existEntity.setRemark(vo.getRemark());
            if (vo.getSourceFileName() != null) {
                existEntity.setSourceFileName(vo.getSourceFileName());
            }

            itemMapper.updateById(existEntity);
            itemUuid = existEntity.getItemUuid();

            // 返回更新后的实体
            BeanUtils.copyProperties(existEntity, vo);
        }

        // 如果是新增且需要立即索引，触发索引任务
        if (isNewItem && Boolean.TRUE.equals(indexAfterCreate)) {
            log.info("新增知识点完成，开始触发索引任务，itemUuid: {}", itemUuid);
            List<String> indexTypes = Arrays.asList("embedding", "graphical");
            knowledgeBaseService.indexItems(Arrays.asList(itemUuid), indexTypes);
        }

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

        wrapper.orderByDesc(AiKnowledgeBaseItemEntity::getC_time);

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
    public boolean delete(String uuid) {
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
        if (currentUserId != null && entity.getC_id() != null) {
            if (!entity.getC_id().equals(currentUserId)) {
                log.warn("无权删除知识项, uuid: {}, currentUser: {}, createUser: {}",
                    uuid, currentUserId, entity.getC_id());
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
            String deleteResult = neo4jGraphIndexingService.deleteDocumentGraph(uuid);
            log.info("删除Neo4j图谱数据, uuid: {}, 删除结果: {}", uuid, deleteResult);
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
        List<String> itemUuids = new ArrayList<>();

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
            entity.setKbId(kb.getId());  // 设置知识库ID
            entity.setKbUuid(kbUuid);
            entity.setTitle(fileInfo.getFileName());  // 使用文件名作为标题
            entity.setSourceFileName(fileInfo.getFileName());
            entity.setEmbeddingStatus(0);  // 待处理状态
            // c_time, c_id 由 MyBatis Plus 自动填充，不需要手动设置

            // 保存知识项
            itemMapper.insert(entity);

            // 2. 保存文件信息到s_file和s_file_info表
            saveItemFiles(entity.getId(), Arrays.asList(fileInfo));

            // 3. 收集itemUuid用于批量索引
            itemUuids.add(itemUuid);

            // 转换为VO并返回
            AiKnowledgeBaseItemVo vo = new AiKnowledgeBaseItemVo();
            BeanUtils.copyProperties(entity, vo);
            results.add(vo);
        }

        // 4. 如果需要立即索引，批量调用索引方法
        if (Boolean.TRUE.equals(indexAfterUpload) && !itemUuids.isEmpty()) {
            log.info("批量创建完成，开始触发索引任务，数量: {}", itemUuids.size());
            // 默认索引类型：embedding（向量化）和 graphical（图谱化）
            List<String> indexTypes = Arrays.asList("embedding", "graphical");
            knowledgeBaseService.indexItems(itemUuids, indexTypes);
        }

        return results;
    }

    /**
     * 保存知识项的附件信息
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
