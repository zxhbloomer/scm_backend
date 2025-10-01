package com.xinyirun.scm.ai.core.mapper.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * AI会话内容表 Mapper接口
 *
 * @author AI重构工具
 * @since 1.0.0
 */
@Repository
@Mapper
public interface AiConversationContentMapper extends BaseMapper<AiConversationContentEntity> {

}