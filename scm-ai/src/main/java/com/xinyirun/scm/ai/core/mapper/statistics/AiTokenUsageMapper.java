package com.xinyirun.scm.ai.core.mapper.statistics;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.statistics.AiTokenUsageEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * AI Token使用记录表 Mapper接口
 *
 * @author AI重构工具
 * @since 1.0.0
 */
@Repository
@Mapper
public interface AiTokenUsageMapper extends BaseMapper<AiTokenUsageEntity> {

}