package com.xinyirun.scm.ai.kbase;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.kbase.entity.KbaseEntity;
import com.xinyirun.scm.ai.kbase.KbaseMapper;
import com.xinyirun.scm.ai.kbase.request.KbaseRequest;
import com.xinyirun.scm.ai.kbase.response.KbaseResponse;
import com.xinyirun.scm.ai.util.UidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识库配置服务类
 */
@Slf4j
@Service
public class KbaseRestService extends ServiceImpl<KbaseMapper, KbaseEntity> {

    @Autowired
    private KbaseMapper kbaseMapper;

    /**
     * 创建知识库
     */
    @Transactional
    public KbaseResponse create(KbaseRequest kbaseRequest) {
        log.info("Creating knowledge base: {}", kbaseRequest.getName());
        
        if (!StringUtils.hasText(kbaseRequest.getName())) {
            throw new IllegalArgumentException("Knowledge base name is required");
        }

        KbaseEntity kbaseEntity = new KbaseEntity();
        BeanUtils.copyProperties(kbaseRequest, kbaseEntity);
        
        kbaseEntity.setUid(UidUtils.uuid());
        
        // 设置默认值
        if (kbaseEntity.getType() == null) {
            kbaseEntity.setType("HELPCENTER");
        }
        if (kbaseEntity.getTheme() == null) {
            kbaseEntity.setTheme("DEFAULT");
        }
        if (kbaseEntity.getMemberCount() == null) {
            kbaseEntity.setMemberCount(0);
        }
        if (kbaseEntity.getArticleCount() == null) {
            kbaseEntity.setArticleCount(0);
        }

        save(kbaseEntity);
        
        log.info("Created knowledge base with uid: {}", kbaseEntity.getUid());
        return convertToResponse(kbaseEntity);
    }

    /**
     * 更新知识库
     */
    @Transactional
    public KbaseResponse update(KbaseRequest kbaseRequest) {
        log.info("Updating knowledge base: {}", kbaseRequest.getUid());
        
        if (!StringUtils.hasText(kbaseRequest.getUid())) {
            throw new IllegalArgumentException("UID is required for update");
        }

        KbaseEntity existingEntity = getById(kbaseRequest.getUid());
        if (existingEntity == null) {
            throw new IllegalArgumentException("Knowledge base not found: " + kbaseRequest.getUid());
        }

        BeanUtils.copyProperties(kbaseRequest, existingEntity, "id", "uid", "createdAt", "createdBy");

        updateById(existingEntity);
        
        log.info("Updated knowledge base: {}", kbaseRequest.getUid());
        return convertToResponse(existingEntity);
    }

    /**
     * 根据UID查询知识库
     */
    public KbaseResponse findByUid(String uid) {
        log.debug("Finding knowledge base by uid: {}", uid);
        
        KbaseEntity kbaseEntity = getById(uid);
        if (kbaseEntity == null) {
            return null;
        }

        return convertToResponse(kbaseEntity);
    }

    /**
     * 分页查询知识库
     */
    public IPage<KbaseResponse> findByPage(KbaseRequest kbaseRequest) {
        log.debug("Finding knowledge bases by page: {}", kbaseRequest.getPageNumber());
        
        // 创建分页对象
        Page<KbaseEntity> pageParam = new Page<>(kbaseRequest.getPageNumber() + 1, kbaseRequest.getPageSize());

        QueryWrapper<KbaseEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(kbaseRequest.getName())) {
            queryWrapper.like("name", kbaseRequest.getName());
        }
        if (StringUtils.hasText(kbaseRequest.getType())) {
            queryWrapper.eq("type", kbaseRequest.getType());
        }
        
        IPage<KbaseEntity> result = page(pageParam, queryWrapper);
        
        return result.convert(this::convertToResponse);
    }

    /**
     * 根据类型查询知识库
     */
    public List<KbaseResponse> findByType(String type) {
        log.debug("Finding knowledge bases by type: {}", type);
        
        QueryWrapper<KbaseEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        List<KbaseEntity> entities = list(queryWrapper);
        
        return entities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 删除知识库
     */
    @Transactional
    public void deleteByUid(String uid) {
        log.info("Deleting knowledge base: {}", uid);
        
        QueryWrapper<KbaseEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        boolean result = remove(queryWrapper);
        if (!result) {
            throw new IllegalArgumentException("Knowledge base not found or already deleted: " + uid);
        }
        
        log.info("Deleted knowledge base: {}", uid);
    }

    /**
     * 更新文章数量
     */
    @Transactional
    public void updateArticleCount(String uid, Integer count) {
        log.debug("Updating article count for knowledge base: {} to {}", uid, count);
        
        KbaseEntity entity = getById(uid);
        if (entity != null) {
            entity.setArticleCount(count);
            updateById(entity);
        }
    }

    /**
     * 增加文章数量
     */
    @Transactional
    public void incrementArticleCount(String uid) {
        log.debug("Incrementing article count for knowledge base: {}", uid);
        
        KbaseEntity entity = getById(uid);
        if (entity != null) {
            entity.setArticleCount(entity.getArticleCount() + 1);
            updateById(entity);
        }
    }

    /**
     * 减少文章数量
     */
    @Transactional
    public void decrementArticleCount(String uid) {
        log.debug("Decrementing article count for knowledge base: {}", uid);
        
        KbaseEntity entity = getById(uid);
        if (entity != null && entity.getArticleCount() > 0) {
            entity.setArticleCount(entity.getArticleCount() - 1);
            updateById(entity);
        }
    }

    /**
     * 统计信息
     */
    public KbaseResponse getStatistics() {
        Long totalCount = count();
        
        return KbaseResponse.builder()
                .articleCount(totalCount.intValue())
                .build();
    }

    /**
     * 转换为响应对象
     */
    private KbaseResponse convertToResponse(KbaseEntity entity) {
        KbaseResponse response = new KbaseResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}