package com.xinyirun.scm.ai.core.service;

import com.xinyirun.scm.ai.bean.domain.AiPrompt;
import com.xinyirun.scm.ai.bean.domain.AiPromptExample;
import com.xinyirun.scm.ai.core.mapper.AiPromptMapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AI提示词服务类
 * 提供提示词的查询功能
 * 暂时不包含增删改操作，只提供查询相关业务逻辑
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AiPromptService {

    @Resource
    private AiPromptMapper aiPromptMapper;

    /**
     * 根据编号查询提示词
     * @param code 编号
     * @return 提示词对象，如果不存在返回null
     */
    public AiPrompt getPromptByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return aiPromptMapper.selectByCode(code);
    }

    /**
     * 根据类型查询提示词列表
     * @param type 类型：1-客服提示词，2-知识库提示词
     * @return 提示词列表
     */
    public List<AiPrompt> getPromptsByType(Integer type) {
        if (type == null) {
            return List.of();
        }
        return aiPromptMapper.selectByType(type);
    }

    /**
     * 根据简称模糊查询提示词列表
     * @param nickname 简称（支持模糊查询）
     * @return 提示词列表
     */
    public List<AiPrompt> getPromptsByNickname(String nickname) {
        if (StringUtils.isBlank(nickname)) {
            return List.of();
        }
        // 为模糊查询添加通配符
        String likePattern = "%" + nickname + "%";
        return aiPromptMapper.selectByNicknameLike(likePattern);
    }

    /**
     * 获取所有提示词列表
     * @return 所有提示词列表
     */
    public List<AiPrompt> getAllPrompts() {
        AiPromptExample example = new AiPromptExample();
        example.setOrderByClause("type ASC, code ASC");
        return aiPromptMapper.selectByExample(example);
    }

    /**
     * 根据主键ID查询提示词
     * @param id 主键ID
     * @return 提示词对象，如果不存在返回null
     */
    public AiPrompt getPromptById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return aiPromptMapper.selectByPrimaryKey(id);
    }

    /**
     * 检查指定编号的提示词是否存在
     * @param code 编号
     * @return true-存在，false-不存在
     */
    public boolean existsByCode(String code) {
        return getPromptByCode(code) != null;
    }

    /**
     * 根据类型统计提示词数量
     * @param type 类型：1-客服提示词，2-知识库提示词
     * @return 提示词数量
     */
    public long countByType(Integer type) {
        if (type == null) {
            return 0L;
        }
        AiPromptExample example = new AiPromptExample();
        example.createCriteria().andTypeEqualTo(type);
        return aiPromptMapper.countByExample(example);
    }

    /**
     * 获取客服提示词列表
     * @return 客服提示词列表
     */
    public List<AiPrompt> getCustomerServicePrompts() {
        return getPromptsByType(1);
    }

    /**
     * 获取知识库提示词列表
     * @return 知识库提示词列表
     */
    public List<AiPrompt> getKnowledgeBasePrompts() {
        return getPromptsByType(2);
    }
}