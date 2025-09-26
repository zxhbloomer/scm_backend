package com.xinyirun.scm.ai.core.mapper.chat;

import com.xinyirun.scm.ai.bean.domain.AiPrompt;
import com.xinyirun.scm.ai.bean.domain.AiPromptExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AiPromptMapper {
    long countByExample(AiPromptExample example);

    int deleteByExample(AiPromptExample example);

    int deleteByPrimaryKey(String id);

    int insert(AiPrompt record);

    int insertSelective(AiPrompt record);

    List<AiPrompt> selectByExample(AiPromptExample example);

    AiPrompt selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") AiPrompt record, @Param("example") AiPromptExample example);

    int updateByExample(@Param("record") AiPrompt record, @Param("example") AiPromptExample example);

    int updateByPrimaryKeySelective(AiPrompt record);

    int updateByPrimaryKey(AiPrompt record);

    int batchInsert(@Param("list") List<AiPrompt> list);

    int batchInsertSelective(@Param("list") List<AiPrompt> list, @Param("selective") AiPrompt.Column ... selective);

    /**
     * 根据编号查询提示词
     * @param code 编号
     * @return 提示词对象
     */
    AiPrompt selectByCode(@Param("code") String code);

    /**
     * 根据类型查询提示词列表
     * @param type 类型：1-客服提示词，2-知识库提示词
     * @return 提示词列表
     */
    List<AiPrompt> selectByType(@Param("type") Integer type);

    /**
     * 根据简称模糊查询提示词列表
     * @param nickname 简称（支持模糊查询）
     * @return 提示词列表
     */
    List<AiPrompt> selectByNicknameLike(@Param("nickname") String nickname);
}