package com.xinyirun.scm.core.system.serviceimpl.business.rpd;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.rpd.RProductDailyDEntity;
import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyVo;
import com.xinyirun.scm.core.system.mapper.business.rpd.RProductDailyDMapper;
import com.xinyirun.scm.core.system.service.business.rpd.IRProductDailyDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 混合物 加工日报表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-16
 */
@Service
public class RProductDailyDServiceImpl extends ServiceImpl<RProductDailyDMapper, RProductDailyDEntity> implements IRProductDailyDService {

    @Autowired
    private RProductDailyDMapper mapper;
    /**
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertR_product_daily_d_50(BProductDailyVo vo) {
        // 全删
        mapper.deleteR_product_daily_d_51(vo);
        // 新增
        mapper.insertR_product_daily_d_52(vo);
    }

    /**
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertR_product_daily_d_500(BProductDailyVo vo) {
        // 全删
        mapper.deleteR_product_daily_d_501(vo);
        // 新增
        mapper.insertR_product_daily_d_502(vo);
    }
}
