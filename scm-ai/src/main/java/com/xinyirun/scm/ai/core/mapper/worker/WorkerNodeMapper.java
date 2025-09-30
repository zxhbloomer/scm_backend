package com.xinyirun.scm.ai.core.mapper.worker;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.worker.WorkerNodeEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 工作节点表 Mapper接口
 *
 * 提供工作节点的数据访问操作，使用注解SQL实现
 * 支持分布式工作节点的管理和查询
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Repository
@Mapper
public interface WorkerNodeMapper extends BaseMapper<WorkerNodeEntity> {

}