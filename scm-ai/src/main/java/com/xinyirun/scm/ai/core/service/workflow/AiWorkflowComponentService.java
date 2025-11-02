package com.xinyirun.scm.ai.core.service.workflow;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowComponentVo;
import com.xinyirun.scm.ai.core.mapper.workflow.AiWorkflowComponentMapper;
import com.xinyirun.scm.common.utils.UuidUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AI工作流组件Service
 *
 * <p>提供工作流组件库的管理功能,包括组件的查询、缓存等</p>
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Slf4j
@Service
public class AiWorkflowComponentService extends ServiceImpl<AiWorkflowComponentMapper, AiWorkflowComponentEntity> {

    private static final String CACHE_WORKFLOW_COMPONENTS = "workflow:components";
    private static final String CACHE_WORKFLOW_COMPONENT_START = "workflow:component:start";

    @Resource
    private AiWorkflowComponentMapper aiWorkflowComponentMapper;

    @Lazy
    @Resource
    private AiWorkflowComponentService self;

    /**
     * 新增或更新组件
     *
     * @param componentVo 组件VO
     * @return 组件实体
     */
    @CacheEvict(cacheNames = {CACHE_WORKFLOW_COMPONENTS, CACHE_WORKFLOW_COMPONENT_START}, allEntries = true)
    public AiWorkflowComponentEntity addOrUpdate(AiWorkflowComponentVo componentVo) {
        AiWorkflowComponentEntity entity = new AiWorkflowComponentEntity();

        if (StringUtils.isNotBlank(componentVo.getComponentUuid())) {
            // 更新
            AiWorkflowComponentEntity existing = aiWorkflowComponentMapper.selectOne(
                    new LambdaQueryWrapper<AiWorkflowComponentEntity>()
                            .eq(AiWorkflowComponentEntity::getComponentUuid, componentVo.getComponentUuid())
                            .eq(AiWorkflowComponentEntity::getIsDeleted, 0)
            );

            if (existing == null) {
                throw new RuntimeException("组件不存在: " + componentVo.getComponentUuid());
            }

            BeanUtils.copyProperties(componentVo, entity, "id", "componentUuid");
            entity.setId(existing.getId());
            aiWorkflowComponentMapper.updateById(entity);

            return entity;
        } else {
            // 新增
            BeanUtils.copyProperties(componentVo, entity, "id", "componentUuid");
            entity.setComponentUuid(UuidUtil.createShort());
            aiWorkflowComponentMapper.insert(entity);

            return entity;
        }
    }

    /**
     * 启用/禁用组件
     * 启用状态：false-禁用,true-启用
     * 符合SCM标准: 先selectOne查询完整实体,然后set修改字段,最后updateById更新
     *
     * @param componentUuid 组件UUID
     * @param isEnable 是否启用
     */
    @CacheEvict(cacheNames = {CACHE_WORKFLOW_COMPONENTS, CACHE_WORKFLOW_COMPONENT_START}, allEntries = true)
    public void enable(String componentUuid, Boolean isEnable) {
        // 1. 先查询出完整实体
        AiWorkflowComponentEntity component = aiWorkflowComponentMapper.selectOne(
                new LambdaQueryWrapper<AiWorkflowComponentEntity>()
                        .eq(AiWorkflowComponentEntity::getComponentUuid, componentUuid)
                        .eq(AiWorkflowComponentEntity::getIsDeleted, false)
        );

        if (component == null) {
            throw new RuntimeException("组件不存在: " + componentUuid);
        }

        // 2. 在查询出的对象上直接修改字段
        component.setIsEnable(isEnable);

        // 3. 使用完整对象更新(其他字段不受影响)
        aiWorkflowComponentMapper.updateById(component);
    }

    /**
     * 删除组件
     * 符合SCM标准: 先selectOne查询完整实体,然后set修改字段,最后updateById更新
     *
     * @param componentUuid 组件UUID
     */
    @CacheEvict(cacheNames = {CACHE_WORKFLOW_COMPONENTS, CACHE_WORKFLOW_COMPONENT_START}, allEntries = true)
    public void deleteByUuid(String componentUuid) {
        // TODO: 检查是否有节点引用此组件
        // Integer refNodeCount = aiWorkflowComponentMapper.countRefNodes(componentUuid);
        // if (refNodeCount > 0) {
        //     throw new RuntimeException("组件被节点引用,无法删除");
        // }

        // 1. 先查询出完整实体
        AiWorkflowComponentEntity component = aiWorkflowComponentMapper.selectOne(
                new LambdaQueryWrapper<AiWorkflowComponentEntity>()
                        .eq(AiWorkflowComponentEntity::getComponentUuid, componentUuid)
                        .eq(AiWorkflowComponentEntity::getIsDeleted, false)
        );

        if (component != null) {
            // 2. 在查询出的对象上直接修改字段
            component.setIsDeleted(true);

            // 3. 使用完整对象更新(其他字段不受影响)
            aiWorkflowComponentMapper.updateById(component);
        }
    }

    /**
     * 搜索组件
     *
     * @param title 组件标题关键词
     * @param isEnable 是否启用
     * @param currentPage 当前页
     * @param pageSize 每页数量
     * @return 分页结果
     */
    public Page<AiWorkflowComponentVo> search(String title, Integer isEnable,
                                               Integer currentPage, Integer pageSize) {
        LambdaQueryWrapper<AiWorkflowComponentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiWorkflowComponentEntity::getIsDeleted, 0);

        if (isEnable != null) {
            wrapper.eq(AiWorkflowComponentEntity::getIsEnable, isEnable);
        }

        if (StringUtils.isNotBlank(title)) {
            wrapper.like(AiWorkflowComponentEntity::getTitle, title);
        }

        wrapper.orderByAsc(AiWorkflowComponentEntity::getDisplayOrder, AiWorkflowComponentEntity::getId);

        Page<AiWorkflowComponentEntity> entityPage = aiWorkflowComponentMapper.selectPage(
                new Page<>(currentPage, pageSize), wrapper
        );

        Page<AiWorkflowComponentVo> voPage = new Page<>();
        voPage.setCurrent(entityPage.getCurrent());
        voPage.setSize(entityPage.getSize());
        voPage.setTotal(entityPage.getTotal());

        List<AiWorkflowComponentVo> voList = new ArrayList<>();
        for (AiWorkflowComponentEntity entity : entityPage.getRecords()) {
            voList.add(changeComponentToDTO(entity));
        }
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 获取所有启用的组件(带缓存)
     * 启用状态：0-禁用,1-启用
     *
     * @return 启用的组件列表
     */
    @Cacheable(cacheNames = CACHE_WORKFLOW_COMPONENTS)
    public List<AiWorkflowComponentEntity> getAllEnable() {
        return aiWorkflowComponentMapper.selectList(
                new LambdaQueryWrapper<AiWorkflowComponentEntity>()
                        .eq(AiWorkflowComponentEntity::getIsEnable, 1)
                        .eq(AiWorkflowComponentEntity::getIsDeleted, 0)
                        .orderByAsc(AiWorkflowComponentEntity::getDisplayOrder, AiWorkflowComponentEntity::getId)
        );
    }

    /**
     * 获取开始组件(带缓存)
     *
     * @return 开始组件
     */
    @Cacheable(cacheNames = CACHE_WORKFLOW_COMPONENT_START)
    public AiWorkflowComponentEntity getStartComponent() {
        List<AiWorkflowComponentEntity> components = self.getAllEnable();
        return components.stream()
                .filter(component -> "Start".equals(component.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到开始组件"));
    }

    /**
     * 按ID获取组件
     *
     * @param id 组件ID
     * @return 组件实体
     */
    public AiWorkflowComponentEntity getComponent(Long id) {
        List<AiWorkflowComponentEntity> components = self.getAllEnable();
        return components.stream()
                .filter(component -> component.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到组件: " + id));
    }

    /**
     * 将组件实体转换为VO
     *
     * @param entity 组件实体
     * @return 组件VO
     */
    private AiWorkflowComponentVo changeComponentToDTO(AiWorkflowComponentEntity entity) {
        AiWorkflowComponentVo vo = new AiWorkflowComponentVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
