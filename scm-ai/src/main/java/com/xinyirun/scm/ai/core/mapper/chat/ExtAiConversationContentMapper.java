package com.xinyirun.scm.ai.core.mapper.chat;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentEntity;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationContentVo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * AI会话内容扩展Mapper接口
 * 租户，多数据源，在线程内，需要手工指定
 *
 * @author AI重构工具
 * @since 1.0.0
 */
@Repository
@Mapper
public interface ExtAiConversationContentMapper {

    /**
     * 根据会话ID查询最后N条记录
     */
    @Select("""
        SELECT
            id,
            conversation_id,
            type,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion,
            content,
            model_source_id
        FROM ai_conversation_content
        WHERE conversation_id = #{conversationId}
        ORDER BY c_time DESC
        LIMIT 1, #{limit}
        """)
    List<AiConversationContentVo> selectLastByConversationIdByLimit(@Param("conversationId") String conversationId,
                                                                    @Param("limit") int limit);
}