package com.xinyirun.scm.core.system.serviceimpl.sys.schedule.v2;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.bean.entity.busniess.wms.inventory.BDailyAveragePriceEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.BDailyAveragePriceVo;
import com.xinyirun.scm.core.system.mapper.sys.schedule.v2.SBDailyAveragePriceV2Mappper;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBDailyAveragePriceV2Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  每日平均单价
 * </p>
 * 废弃
 * @author wwl
 * @since 2022-03-21
 */
@Service
public class SBDailyAveragePriceV2ServiceImpl extends BaseServiceImpl<SBDailyAveragePriceV2Mappper, BDailyAveragePriceEntity> implements ISBDailyAveragePriceV2Service {

    @Autowired
    SBDailyAveragePriceV2Mappper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reCreateDailyAveragePriceAll() {
        createDailyAveragePrice(null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDailyAveragePrice(String parameterClass, String parameter) {
        log.debug("----------------每日平均单价start---------");

        BDailyAveragePriceVo condition = null;
        if (parameterClass == null || parameter == null ) {
            condition = new BDailyAveragePriceVo();
        } else {
            condition = JSON.parseObject(parameter ,BDailyAveragePriceVo.class);
        }
        // 删除当天数据
        mapper.deleteIntradayData();
        // 查询平均单价
        List<BDailyAveragePriceEntity> list = mapper.selectAveragePriceList(condition);
        // 保存数据
        this.saveBatch(list);
        log.debug("----------------每日平均单价start---------");
    }
}
