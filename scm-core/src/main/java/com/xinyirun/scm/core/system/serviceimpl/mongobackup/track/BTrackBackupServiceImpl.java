package com.xinyirun.scm.core.system.serviceimpl.mongobackup.track;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.track.BTrackEntity;
import com.xinyirun.scm.bean.system.vo.business.track.BTrackVo;
import com.xinyirun.scm.bean.system.vo.mongo.track.BMonitorTrackMongoDataVo;
import com.xinyirun.scm.core.system.mapper.mongobackup.track.BTrackBackupMapper;
import com.xinyirun.scm.core.system.service.mongobackup.track.IBTrackBackupService;
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
public class BTrackBackupServiceImpl extends ServiceImpl<BTrackBackupMapper, BTrackEntity> implements IBTrackBackupService {

    @Autowired
    private BTrackBackupMapper mapper;


    @Override
    public BMonitorTrackMongoDataVo get(BTrackVo vo) {
        return mapper.selectOne(vo);
    }

}
