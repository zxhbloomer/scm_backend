package com.xinyirun.scm.ai.core.mapper;

import com.xinyirun.scm.ai.bean.domain.AiConversationContent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtAiConversationContentMapper {

    List<AiConversationContent> selectLastByConversationIdByLimit(@Param("conversationId") String conversationId,
                                                              @Param("limit")int limit);

}