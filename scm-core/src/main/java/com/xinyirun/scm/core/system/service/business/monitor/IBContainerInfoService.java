package com.xinyirun.scm.core.system.service.business.monitor;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.monitor.BContainerInfoEntity;
import com.xinyirun.scm.bean.system.vo.business.monitor.BContainerInfoVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-12
 */
public interface IBContainerInfoService extends IService<BContainerInfoEntity> {

    List<BContainerInfoVo> selectContainerInfos(int serial_id, String serial_type);

    BContainerInfoEntity selectById(Integer id);

}
