package com.xinyirun.scm.core.system.serviceimpl.business.track.gsh56;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.track.BTrackApiSinoiovEntity;
import com.xinyirun.scm.bean.system.vo.business.track.BTrackApiSinoiovVo;
import com.xinyirun.scm.core.system.mapper.business.track.BTrackApiSinoiovMapper;
import com.xinyirun.scm.core.system.service.track.gsh56.IBTrackApiSinoiovService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-15
 */
@Service
public class BTrackApiSinoiovServiceImpl extends ServiceImpl<BTrackApiSinoiovMapper, BTrackApiSinoiovEntity> implements IBTrackApiSinoiovService {

    @Autowired
    BTrackApiSinoiovMapper mapper;

    @Override
    public BTrackApiSinoiovVo getDataByType(String type) {
        return mapper.selectByType(type);
    }
}
