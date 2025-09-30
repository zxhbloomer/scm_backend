package com.xinyirun.scm.ai.mapper.worker;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.worker.WorkerNodeEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 工作节点表 Mapper接口
 *
 * @author AI重构工具
 * @since 1.0.0
 */
@Repository
@Mapper
public interface BaseWorkerNodeMapper extends BaseMapper<WorkerNodeEntity> {

}