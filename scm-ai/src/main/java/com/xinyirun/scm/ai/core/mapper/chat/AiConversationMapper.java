package com.xinyirun.scm.ai.mapper.chat;

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


}