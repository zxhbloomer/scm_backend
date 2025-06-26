package com.xinyirun.scm.core.system.service.sys.schedule.v5;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;

/**
 * <p>
 *  港口停滞预警service
 * </p>
 */
public interface ISInventoryStagnationWarningV5Service extends IService<MInventoryEntity> {

     void stagnationWarning(String parameterClass , String parameter);
}
