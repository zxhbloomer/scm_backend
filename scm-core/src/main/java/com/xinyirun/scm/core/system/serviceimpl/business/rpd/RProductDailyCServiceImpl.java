package com.xinyirun.scm.core.system.serviceimpl.business.rpd;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.rpd.RProductDailyCEntity;
import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyVo;
import com.xinyirun.scm.core.system.mapper.business.rpd.RProductDailyCMapper;
import com.xinyirun.scm.core.system.service.business.rpd.IRProductDailyCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 玉米 加工日报表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-16
 */
@Service
public class RProductDailyCServiceImpl extends ServiceImpl<RProductDailyCMapper, RProductDailyCEntity> implements IRProductDailyCService {

    @Autowired
    private RProductDailyCMapper mapper;

    /**
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertR_product_daily_c_40(BProductDailyVo vo) {
        // 全删
        mapper.deleteR_product_daily_c_41(vo);
        // 新增
        mapper.insertR_product_daily_c_42(vo);

    }

    /**
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertR_product_daily_c_400(BProductDailyVo vo) {
        // 全删
        mapper.deleteR_product_daily_c_401(vo);
        // 新增
        mapper.insertR_product_daily_c_402(vo);
    }
}
