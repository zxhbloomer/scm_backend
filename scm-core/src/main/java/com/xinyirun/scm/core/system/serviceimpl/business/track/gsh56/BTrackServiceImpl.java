package com.xinyirun.scm.core.system.serviceimpl.business.track.gsh56;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.track.BTrackEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.business.track.BTrackVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.track.BTrackMapper;
import com.xinyirun.scm.core.system.service.track.gsh56.IBTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-15
 */
@Service
public class BTrackServiceImpl extends ServiceImpl<BTrackMapper, BTrackEntity> implements IBTrackService {

    @Autowired
    BTrackMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(BTrackVo vo) {
        BTrackEntity entity = new BTrackEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        int rtn = mapper.insert(entity);
        return InsertResultUtil.OK(rtn);
    }

    @Override
    public BTrackVo get(BTrackVo vo) {
        return mapper.selectOne(vo);
    }

    @Override
    @Transactional
    public void delete(BTrackVo vo) {
        mapper.deleteTrack(vo);
    }

}
