package com.xinyirun.scm.core.system.service.sys.schedule.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;

/**
 * <p>
 *  每日库存变化表的service
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
public interface ISFileBackupV2Service extends IService<SFileInfoEntity> {

    /**
     * 重新生成每日库存表，所有仓库
     *
     */
    void backup(String parameterClass , String parameter);

    /**
     * 重新生成每日库存表，所有仓库
     *
     */
    void delete(String parameterClass , String parameter);

}
