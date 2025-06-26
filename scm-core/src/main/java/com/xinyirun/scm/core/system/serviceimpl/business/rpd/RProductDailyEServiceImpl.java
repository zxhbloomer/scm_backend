package com.xinyirun.scm.core.system.serviceimpl.business.rpd;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.rpd.RProductDailyEEntity;
import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyVo;
import com.xinyirun.scm.core.system.mapper.business.rpd.RProductDailyEMapper;
import com.xinyirun.scm.core.system.service.business.rpd.IRProductDailyEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 稻壳 加工日报表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-16
 */
@Service
public class RProductDailyEServiceImpl extends ServiceImpl<RProductDailyEMapper, RProductDailyEEntity> implements IRProductDailyEService {

    @Autowired
    private RProductDailyEMapper mapper;


    /**
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertR_product_daily_e_60(BProductDailyVo vo) {
        // 全删
        mapper.deleteR_product_daily_e_61(vo);
        // 全增
        mapper.insertR_product_daily_e_62(vo);
    }

    /**
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertR_product_daily_e_600(BProductDailyVo vo) {
        // 全删
        mapper.deleteR_product_daily_e_601(vo);
        // 全增
        mapper.insertR_product_daily_e_602(vo);
    }
}
