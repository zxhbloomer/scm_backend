package com.xinyirun.scm.ai.core.mapper.chat;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.xinyirun.scm.ai.bean.domain.AiConversationContent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租户，多数据源，在线程内，需要手工指定
 */
public interface ExtAiConversationContentMapper {

    List<AiConversationContent> selectLastByConversationIdByLimit(@Param("conversationId") String conversationId,
                                                              @Param("limit")int limit);

}