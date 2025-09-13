/*
 * SCM AI Module - LLM Model Rest Service
 * Adapted from ByteDesk AI Module for SCM System
 * 
 * Author: SCM Development Team
 * Description: LLM模型REST服务，处理AI模型的业务逻辑
 */
package com.xinyirun.scm.ai.model;

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
 * LLM模型REST服务
 * 提供AI模型的完整生命周期管理功能
 * 
 * @author SCM-AI Module
 * @version 1.0.0
 * @since 2025-01-12
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@Description("LLM Model Service - Large Language Model management and configuration service")
public class LlmModelRestService extends ServiceImpl<LlmModelMapper, LlmModelEntity> {

    @Autowired
    private LlmModelMapper llmModelMapper;

    /**
     * 分页查询AI模型
     * 
     * @param request 查询条件
     * @return 分页结果
     */
    public IPage<LlmModelResponse> queryPage(LlmModelRequest request) {
        log.info("分页查询AI模型，条件：{}", request);
        
        // 创建分页对象
        Page<LlmModelEntity> page = new Page<>(request.getPageCondition().getCurrent(), request.getPageCondition().getSize());
        
        // 构建查询条件
        LambdaQueryWrapper<LlmModelEntity> queryWrapper = createQueryWrapper(request);
        
        // 执行查询
        IPage<LlmModelEntity> entityPage = llmModelMapper.selectPage(page, queryWrapper);
        
        // 转换为Response分页结果
        return entityPage.convert(this::convertToResponse);
    }

    /**
     * 根据UID查询AI模型
     * 
     * @param uid 模型UID
     * @return 模型Response
     */
    public Optional<LlmModelResponse> findByUid(String uid) {
        log.info("根据UID查询AI模型：{}", uid);
        
        LambdaQueryWrapper<LlmModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LlmModelEntity::getUid, uid);
        
        LlmModelEntity entity = llmModelMapper.selectOne(queryWrapper);
        return entity != null ? Optional.of(convertToResponse(entity)) : Optional.empty();
    }
    
    /**
     * 根据提供商UID查询模型列表
     * 
     * @param providerUid 提供商UID
     * @return 模型列表
     */
    public List<LlmModelResponse> findByProviderUid(String providerUid) {
        log.info("根据提供商UID查询模型列表：{}", providerUid);
        
        LambdaQueryWrapper<LlmModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LlmModelEntity::getProviderUid, providerUid);
        queryWrapper.orderByDesc(LlmModelEntity::getId);
        
        List<LlmModelEntity> entities = llmModelMapper.selectList(queryWrapper);
        return entities.stream().map(this::convertToResponse).toList();
    }

    /**
     * 根据提供商名称和组织UID查询模型列表
     * 
     * @param providerName 提供商名称
     * @param orgUid 组织UID
     * @return 模型列表
     */
    public List<LlmModelResponse> findByProviderNameAndOrgUid(String providerName, String orgUid) {
        log.info("根据提供商名称和组织UID查询模型列表：{}, {}", providerName, orgUid);
        
        LambdaQueryWrapper<LlmModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LlmModelEntity::getProviderName, providerName);
        queryWrapper.orderByDesc(LlmModelEntity::getId);
        
        List<LlmModelEntity> entities = llmModelMapper.selectList(queryWrapper);
        return entities.stream().map(this::convertToResponse).toList();
    }

    /**
     * 检查模型名称和提供商UID是否已存在
     * 
     * @param name 模型名称
     * @param providerUid 提供商UID
     * @return 是否存在
     */
    public Boolean existsByNameAndProviderUid(String name, String providerUid) {
        log.info("检查模型是否存在：{}, {}", name, providerUid);
        
        LambdaQueryWrapper<LlmModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LlmModelEntity::getName, name);
        queryWrapper.eq(LlmModelEntity::getProviderUid, providerUid);
        
        return llmModelMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 创建AI模型
     * 
     * @param request 模型请求
     * @return 创建成功的模型Response
     */
    public LlmModelResponse create(LlmModelRequest request) {
        log.info("创建AI模型：{}", request);
        
        // 检查名称和提供商是否已存在
        if (existsByNameAndProviderUid(request.getName(), request.getProviderUid())) {
            // 如果已存在，返回现有记录
            log.warn("模型已存在，返回现有记录：{}, {}", request.getName(), request.getProviderUid());
            LambdaQueryWrapper<LlmModelEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(LlmModelEntity::getName, request.getName());
            queryWrapper.eq(LlmModelEntity::getProviderUid, request.getProviderUid());
            queryWrapper.last("LIMIT 1");
            
            LlmModelEntity existing = llmModelMapper.selectOne(queryWrapper);
            if (existing != null) {
                return convertToResponse(existing);
            }
        }
        
        // 转换为实体
        LlmModelEntity entity = convertToEntity(request);
        
        // 保存实体
        LlmModelEntity savedModel = saveEntity(entity);
        if (savedModel == null) {
            throw new RuntimeException("Create LlmModel failed");
        }
        
        return convertToResponse(savedModel);
    }

    /**
     * 更新AI模型
     * 
     * @param request 模型请求
     * @return 更新后的模型Response
     */
    public LlmModelResponse update(LlmModelRequest request) {
        log.info("更新AI模型：{}", request);
        
        if (request.getUid() == null) {
            throw new RuntimeException("更新AI模型时UID不能为空");
        }
        
        // 检查记录是否存在
        LambdaQueryWrapper<LlmModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LlmModelEntity::getUid, request.getUid());
        LlmModelEntity existing = llmModelMapper.selectOne(queryWrapper);
        
        if (existing == null) {
            throw new RuntimeException("AI模型不存在，UID：" + request.getUid());
        }
        
        // 转换为实体并保留原有的创建信息
        LlmModelEntity entity = convertToEntity(request);
        entity.setId(existing.getId());
        
        // 执行更新
        LlmModelEntity savedModel = saveEntity(entity);
        if (savedModel == null) {
            throw new RuntimeException("Update LlmModel failed");
        }
        
        return convertToResponse(savedModel);
    }

    /**
     * 根据UID删除AI模型（逻辑删除）
     * 
     * @param uid 模型UID
     */
    public void deleteByUid(String uid) {
        log.info("删除AI模型：{}", uid);
        
        LambdaQueryWrapper<LlmModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LlmModelEntity::getUid, uid);
        LlmModelEntity entity = llmModelMapper.selectOne(queryWrapper);
        
        if (entity == null) {
            throw new RuntimeException("AI模型不存在，UID：" + uid);
        }
        
        // 逻辑删除
        entity.setDeleted(true);
        int result = llmModelMapper.updateById(entity);
        if (result <= 0) {
            throw new RuntimeException("删除AI模型失败");
        }
    }

    /**
     * 保存实体（使用MyBatis Plus提供的便利方法）
     * 
     * @param entity 实体对象
     * @return 保存后的实体
     */
    public LlmModelEntity saveEntity(LlmModelEntity entity) {
        try {
            // 使用MyBatis Plus的saveOrUpdate方法，自动判断是插入还是更新
            boolean success = this.saveOrUpdate(entity);
            if (!success) {
                throw new RuntimeException("保存操作失败");
            }
            return entity;
        } catch (Exception e) {
            log.error("保存LlmModel失败: {}", e.getMessage(), e);
            throw new RuntimeException("保存LlmModel失败", e);
        }
    }

    /**
     * 构建查询条件
     * 
     * @param request 查询请求
     * @return 查询包装器
     */
    private LambdaQueryWrapper<LlmModelEntity> createQueryWrapper(LlmModelRequest request) {
        LambdaQueryWrapper<LlmModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        
        if (request == null) {
            return queryWrapper;
        }
        
        // 启用状态
        if (request.getEnabled() != null) {
            queryWrapper.eq(LlmModelEntity::getEnabled, request.getEnabled());
        }
        
        // 系统启用状态
        if (request.getSystemEnabled() != null) {
            queryWrapper.eq(LlmModelEntity::getSystemEnabled, request.getSystemEnabled());
        }
        
        // 模型类型
        if (StringUtils.hasText(request.getType())) {
            queryWrapper.eq(LlmModelEntity::getType, request.getType());
        }
        
        // 提供商UID
        if (StringUtils.hasText(request.getProviderUid())) {
            queryWrapper.eq(LlmModelEntity::getProviderUid, request.getProviderUid());
        }
        
        // 提供商名称
        if (StringUtils.hasText(request.getProviderName())) {
            queryWrapper.eq(LlmModelEntity::getProviderName, request.getProviderName());
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(LlmModelEntity::getCreatedAt);
        
        return queryWrapper;
    }

    /**
     * 实体转换为Response
     * 
     * @param entity 实体对象
     * @return Response对象
     */
    public LlmModelResponse convertToResponse(LlmModelEntity entity) {
        if (entity == null) {
            return null;
        }
        
        LlmModelResponse response = new LlmModelResponse();
        BeanUtils.copyProperties(entity, response);
        
        return response;
    }

    /**
     * Request转换为实体
     * 
     * @param request Request对象
     * @return 实体对象
     */
    private LlmModelEntity convertToEntity(LlmModelRequest request) {
        if (request == null) {
            return null;
        }
        
        LlmModelEntity entity = new LlmModelEntity();
        BeanUtils.copyProperties(request, entity);
        
        return entity;
    }
}