package com.xinyirun.scm.core.system.serviceimpl.business.rpd;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.rpd.RProductDailyBEntity;
import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyVo;
import com.xinyirun.scm.core.system.mapper.business.rpd.RProductDailyBMapper;
import com.xinyirun.scm.core.system.service.business.rpd.IRProductDailyBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 糙米 加工日报表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-16
 */
@Service
public class RProductDailyBServiceImpl extends ServiceImpl<RProductDailyBMapper, RProductDailyBEntity> implements IRProductDailyBService {

    @Autowired
    private RProductDailyBMapper mapper;

    /**
     * 新增糙米
     *
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertR_product_daily_b_30(BProductDailyVo vo) {
        // 全删
        mapper.deleteR_product_daily_b_31(vo);
        // 新增
        mapper.insertR_product_daily_b_32(vo);

    }

    /**
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertR_product_daily_b_300(BProductDailyVo vo) {
        // 全删
        mapper.deleteR_product_daily_b_301(vo);
        // 新增
        mapper.insertR_product_daily_b_302(vo);
    }
}
