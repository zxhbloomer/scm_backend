package com.xinyirun.scm.ai.core.mapper;

import com.xinyirun.scm.ai.bean.domain.AiConversationContent;
import java.util.List;

import com.xinyirun.scm.ai.bean.domain.AiConversationContentExample;
import org.apache.ibatis.annotations.Param;

public interface AiConversationContentMapper {
    long countByExample(AiConversationContentExample example);

    int deleteByExample(AiConversationContentExample example);

    int deleteByPrimaryKey(String id);

    int insert(AiConversationContent record);

    int insertSelective(AiConversationContent record);

    List<AiConversationContent> selectByExampleWithBLOBs(AiConversationContentExample example);

    List<AiConversationContent> selectByExample(AiConversationContentExample example);

    AiConversationContent selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") AiConversationContent record, @Param("example") AiConversationContentExample example);

    int updateByExampleWithBLOBs(@Param("record") AiConversationContent record, @Param("example") AiConversationContentExample example);

    int updateByExample(@Param("record") AiConversationContent record, @Param("example") AiConversationContentExample example);

    int updateByPrimaryKeySelective(AiConversationContent record);

    int updateByPrimaryKeyWithBLOBs(AiConversationContent record);

    int updateByPrimaryKey(AiConversationContent record);

    int batchInsert(@Param("list") List<AiConversationContent> list);

    int batchInsertSelective(@Param("list") List<AiConversationContent> list, @Param("selective") AiConversationContent.Column ... selective);
}