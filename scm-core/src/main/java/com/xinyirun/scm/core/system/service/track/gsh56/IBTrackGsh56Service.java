package com.xinyirun.scm.core.system.service.track.gsh56;

import com.baomidou.mybatisplus.extension.service.IService;
//import com.xinyirun.scm.bean.app.vo.master.vehicle.AppMVehicleVo;
import com.xinyirun.scm.bean.entity.business.track.BTrackGsh56Entity;
import com.xinyirun.scm.bean.system.vo.business.track.BTrackVo;
import com.xinyirun.scm.bean.system.vo.sys.log.STrackValidateTestVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-15
 */
public interface IBTrackGsh56Service extends IService<BTrackGsh56Entity> {

    /**
     * 刷新轨迹数据_gsh56
     */
    public void refreshGsh56Track(BTrackVo vo);

    /**
     * 车辆入网验证
     */
    public Boolean checkTruckExist(String vehicle_no);

    /**
     * 车辆确认验证
     */
//    public BVehicleValidateVo checkVehicleExist(AppMVehicleVo vo);

    /**
     * 测试 车辆轨迹
     * @param param
     * @return
     */
    String getTrackMsg(STrackValidateTestVo param);
}
