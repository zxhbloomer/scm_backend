package com.xinyirun.scm.core.system.service.log.sys;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.sync.BSyncStatusEntity;
import com.xinyirun.scm.bean.system.vo.business.sync.BSyncStatusVo;

/**
 * @author Wang Qianfeng
 * @date 2022/10/21 11:00
 */
public interface ISLogSyncStatusService extends IService<BSyncStatusEntity> {

    /**
     * 根据条件查询同步日志
     * @param searchCondition 查询条件
     * @return Page<BSyncStatusVo>
     */
    IPage<BSyncStatusVo> selectPage(BSyncStatusVo searchCondition);
}
