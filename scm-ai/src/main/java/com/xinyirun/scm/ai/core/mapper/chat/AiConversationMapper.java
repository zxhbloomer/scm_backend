package com.xinyirun.scm.ai.core.mapper.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI会话表 Mapper接口
 *
 * @author AI重构工具
 * @since 1.0.0
 */
@Repository
@Mapper
public interface AiConversationMapper extends BaseMapper<AiConversationEntity> {

    /**
     * 根据创建人ID查询对话列表
     *
     * @param userId 创建人ID
     * @return 对话列表
     */
    @Select("""
        SELECT
            id,
            title,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_conversation
        WHERE c_id = #{userId}
        ORDER BY c_time DESC
        """)
    List<AiConversationEntity> selectByUserId(@Param("userId") Long userId);

}