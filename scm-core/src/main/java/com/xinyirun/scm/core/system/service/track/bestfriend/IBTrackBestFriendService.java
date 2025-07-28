package com.xinyirun.scm.core.system.service.track.bestfriend;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.track.BTrackGsh56Entity;
import com.xinyirun.scm.bean.system.vo.business.track.BTrackVo;
import com.xinyirun.scm.bean.system.vo.sys.log.STrackValidateTestVo;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-15
 */
public interface IBTrackBestFriendService extends IService<BTrackGsh56Entity> {

    /**
     * 车辆确认验证
     */
    Map<String, String> CheckVehicleExsit(String vehicle_no);

    /**
     * 刷新轨迹
     */
    void refreshGsh56Track(BTrackVo vo);

    String getValidateResult(String vehicleNo);

    /**
     * 轨迹测试
     * @param param
     * @return
     */
    String getTrackMsg(STrackValidateTestVo param);
}
