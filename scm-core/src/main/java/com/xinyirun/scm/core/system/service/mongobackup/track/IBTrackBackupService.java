package com.xinyirun.scm.core.system.service.mongobackup.track;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.track.BTrackEntity;
import com.xinyirun.scm.bean.system.vo.business.track.BTrackVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.track.BMonitorTrackMongoDataVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-15
 */
public interface IBTrackBackupService extends IService<BTrackEntity> {


    /**
     * 查询明细
     */
    BMonitorTrackMongoDataVo get(BTrackVo vo);


}
