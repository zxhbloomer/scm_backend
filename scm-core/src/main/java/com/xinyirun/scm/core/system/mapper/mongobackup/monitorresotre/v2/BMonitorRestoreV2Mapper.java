package com.xinyirun.scm.core.system.mapper.mongobackup.monitorresotre.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.mongo2mysql.monitor.v2.BMonitorRestoreV2Entity;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口, 旨在从mongo恢复数据到mysql， 不更新 c_time, u_time, dbversion 等字段
 * </p>
 *
 * @author wqf
 * @since 2021-09-23
 */
@Repository
public interface BMonitorRestoreV2Mapper extends BaseMapper<BMonitorRestoreV2Entity> {

}
