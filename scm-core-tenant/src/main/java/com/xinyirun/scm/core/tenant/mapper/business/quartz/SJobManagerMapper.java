package com.xinyirun.scm.core.tenant.mapper.business.quartz;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.tenant.manager.quartz.SJobManagerEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 定时任务 Mapper 接口
 * </p>
 */
@Repository
public interface SJobManagerMapper extends BaseMapper<SJobManagerEntity> {

}
