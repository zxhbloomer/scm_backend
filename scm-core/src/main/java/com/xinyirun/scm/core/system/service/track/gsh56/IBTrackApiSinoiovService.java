package com.xinyirun.scm.core.system.service.track.gsh56;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.track.BTrackApiSinoiovEntity;
import com.xinyirun.scm.bean.system.vo.business.track.BTrackApiSinoiovVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-15
 */
public interface IBTrackApiSinoiovService extends IService<BTrackApiSinoiovEntity> {

    /**
     * 获取url
     */
    public BTrackApiSinoiovVo getDataByType(String type);

}
