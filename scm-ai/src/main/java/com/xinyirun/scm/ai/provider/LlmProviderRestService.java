/*
 * SCM AI Module - LLM Provider Rest Service
 * Adapted from ByteDesk AI Module for SCM System
 * 
 * Author: SCM Development Team
 * Description: AI提供商REST服务，处理AI提供商的业务逻辑
 */
package com.xinyirun.scm.ai.provider;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * AI提供商REST服务
 * 提供AI提供商的完整生命周期管理功能
 * 
 * @author SCM-AI Module
 * @version 1.0.0
 * @since 2025-01-12
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@Description("LLM Provider Service - Large Language Model provider management and configuration service")
public class LlmProviderRestService extends ServiceImpl<LlmProviderMapper, LlmProviderEntity> {

    @Autowired
    private LlmProviderMapper llmProviderMapper;

    /**
     * 分页查询AI提供商
     * 
     * @param request 查询条件
     * @return 分页结果
     */
    public IPage<LlmProviderResponse> queryPage(LlmProviderRequest request) {
        log.info("分页查询AI提供商，条件：{}", request);
        
        // 创建分页对象
        Page<LlmProviderEntity> page = new Page<>(request.getPageCondition().getCurrent(), request.getPageCondition().getSize());
        
        // 构建查询条件
        LambdaQueryWrapper<LlmProviderEntity> queryWrapper = createQueryWrapper(request);
        
        // 执行查询
        IPage<LlmProviderEntity> entityPage = llmProviderMapper.selectPage(page, queryWrapper);
        
        // 转换为Response分页结果
        return entityPage.convert(this::convertToResponse);
    }

    /**
     * 根据UID查询AI提供商
     * 
     * @param uid 提供商UID
     * @return 提供商Response
     */
    public Optional<LlmProviderResponse> findByUid(String uid) {
        log.info("根据UID查询AI提供商：{}", uid);
        
        LambdaQueryWrapper<LlmProviderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LlmProviderEntity::getUid, uid);
        
        LlmProviderEntity entity = llmProviderMapper.selectOne(queryWrapper);
        return entity != null ? Optional.of(convertToResponse(entity)) : Optional.empty();
    }
    
    /**
     * 根据提供商名称查询
     * 
     * @param name 提供商名称
     * @return 提供商Response
     */
    public Optional<LlmProviderResponse> findByName(String name) {
        log.info("根据名称查询AI提供商：{}", name);
        
        LambdaQueryWrapper<LlmProviderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LlmProviderEntity::getName, name);
        
        LlmProviderEntity entity = llmProviderMapper.selectOne(queryWrapper);
        return entity != null ? Optional.of(convertToResponse(entity)) : Optional.empty();
    }

    /**
     * 查询所有启用的提供商
     * 
     * @return 启用的提供商列表
     */
    public List<LlmProviderResponse> findByEnabled() {
        log.info("查询所有启用的AI提供商");
        
        LambdaQueryWrapper<LlmProviderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LlmProviderEntity::getEnabled, true);
        queryWrapper.orderByDesc(LlmProviderEntity::getId);
        
        List<LlmProviderEntity> entities = llmProviderMapper.selectList(queryWrapper);
        return entities.stream().map(this::convertToResponse).toList();
    }

    /**
     * 检查提供商名称是否已存在
     * 
     * @param name 提供商名称
     * @return 是否存在
     */
    public Boolean existsByName(String name) {
        log.info("检查提供商是否存在：{}", name);
        
        LambdaQueryWrapper<LlmProviderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LlmProviderEntity::getName, name);
        
        return llmProviderMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 创建AI提供商
     * 
     * @param request 提供商请求
     * @return 创建成功的提供商Response
     */
    public LlmProviderResponse create(LlmProviderRequest request) {
        log.info("创建AI提供商：{}", request);
        
        // 检查名称是否已存在
        if (existsByName(request.getName())) {
            // 如果已存在，返回现有记录
            log.warn("提供商已存在，返回现有记录：{}", request.getName());
            LambdaQueryWrapper<LlmProviderEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(LlmProviderEntity::getName, request.getName());
            queryWrapper.last("LIMIT 1");
            
            LlmProviderEntity existing = llmProviderMapper.selectOne(queryWrapper);
            if (existing != null) {
                return convertToResponse(existing);
            }
        }
        
        // 转换为实体
        LlmProviderEntity entity = convertToEntity(request);
        
        // 保存实体
        LlmProviderEntity savedProvider = saveEntity(entity);
        if (savedProvider == null) {
            throw new RuntimeException("Create LlmProvider failed");
        }
        
        return convertToResponse(savedProvider);
    }

    /**
     * 更新AI提供商
     * 
     * @param request 提供商请求
     * @return 更新后的提供商Response
     */
    public LlmProviderResponse update(LlmProviderRequest request) {
        log.info("更新AI提供商：{}", request);
        
        if (request.getUid() == null) {
            throw new RuntimeException("更新AI提供商时UID不能为空");
        }
        
        // 检查记录是否存在
        LambdaQueryWrapper<LlmProviderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LlmProviderEntity::getUid, request.getUid());
        LlmProviderEntity existing = llmProviderMapper.selectOne(queryWrapper);
        
        if (existing == null) {
            throw new RuntimeException("AI提供商不存在，UID：" + request.getUid());
        }
        
        // 转换为实体并保留原有的创建信息
        LlmProviderEntity entity = convertToEntity(request);
        entity.setId(existing.getId());
        
        // 执行更新
        LlmProviderEntity savedProvider = saveEntity(entity);
        if (savedProvider == null) {
            throw new RuntimeException("Update LlmProvider failed");
        }
        
        return convertToResponse(savedProvider);
    }

    /**
     * 根据UID删除AI提供商（逻辑删除）
     * 
     * @param uid 提供商UID
     */
    public void deleteByUid(String uid) {
        log.info("删除AI提供商：{}", uid);
        
        LambdaQueryWrapper<LlmProviderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LlmProviderEntity::getUid, uid);
        LlmProviderEntity entity = llmProviderMapper.selectOne(queryWrapper);
        
        if (entity == null) {
            throw new RuntimeException("AI提供商不存在，UID：" + uid);
        }
        
        // 逻辑删除
        entity.setDeleted(true);
        int result = llmProviderMapper.updateById(entity);
        if (result <= 0) {
            throw new RuntimeException("删除AI提供商失败");
        }
    }

    /**
     * 保存实体（使用MyBatis Plus提供的便利方法）
     * 
     * @param entity 实体对象
     * @return 保存后的实体
     */
    public LlmProviderEntity saveEntity(LlmProviderEntity entity) {
        try {
            // 使用MyBatis Plus的saveOrUpdate方法，自动判断是插入还是更新
            boolean success = this.saveOrUpdate(entity);
            if (!success) {
                throw new RuntimeException("保存操作失败");
            }
            return entity;
        } catch (Exception e) {
            log.error("保存LlmProvider失败: {}", e.getMessage(), e);
            throw new RuntimeException("保存LlmProvider失败", e);
        }
    }

    /**
     * 构建查询条件
     * 
     * @param request 查询请求
     * @return 查询包装器
     */
    private LambdaQueryWrapper<LlmProviderEntity> createQueryWrapper(LlmProviderRequest request) {
        LambdaQueryWrapper<LlmProviderEntity> queryWrapper = new LambdaQueryWrapper<>();
        
        if (request == null) {
            return queryWrapper;
        }
        
        // 启用状态
        if (request.getEnabled() != null) {
            queryWrapper.eq(LlmProviderEntity::getEnabled, request.getEnabled());
        }
        
        // 系统启用状态
        if (request.getSystemEnabled() != null) {
            queryWrapper.eq(LlmProviderEntity::getSystemEnabled, request.getSystemEnabled());
        }
        
        // 提供商名称
        if (StringUtils.hasText(request.getName())) {
            queryWrapper.like(LlmProviderEntity::getName, request.getName());
        }
        
        // 提供商状态
        if (StringUtils.hasText(request.getStatus())) {
            queryWrapper.eq(LlmProviderEntity::getStatus, request.getStatus());
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(LlmProviderEntity::getCreatedAt);
        
        return queryWrapper;
    }

    /**
     * 实体转换为Response
     * 
     * @param entity 实体对象
     * @return Response对象
     */
    public LlmProviderResponse convertToResponse(LlmProviderEntity entity) {
        if (entity == null) {
            return null;
        }
        
        LlmProviderResponse response = new LlmProviderResponse();
        BeanUtils.copyProperties(entity, response);
        
        return response;
    }

    /**
     * Request转换为实体
     * 
     * @param request Request对象
     * @return 实体对象
     */
    private LlmProviderEntity convertToEntity(LlmProviderRequest request) {
        if (request == null) {
            return null;
        }
        
        LlmProviderEntity entity = new LlmProviderEntity();
        BeanUtils.copyProperties(request, entity);
        
        return entity;
    }
}