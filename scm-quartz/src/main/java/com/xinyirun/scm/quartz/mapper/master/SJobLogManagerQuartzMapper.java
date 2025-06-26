package com.xinyirun.scm.quartz.mapper.master;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.tenant.manager.quartz.SJobLogManagerEntity;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 定时任务日志 Mapper 接口
 * </p>
 */
@Repository
public interface SJobLogManagerQuartzMapper extends BaseMapper<SJobLogManagerEntity> {

}
