package com.xinyirun.scm.core.system.service.mongobackup.monitorrestore.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.mongo2mysql.monitor.v2.BReturnRelationRestoreV2Entity;

/**
 * <p>
 * 备份后保存入库, 出库数量, 用于计算监管任务 已出库, 已入库数量 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2023-07-05
 */
public interface IBReturnRelationRestoreV2Service extends IService<BReturnRelationRestoreV2Entity> {



}
