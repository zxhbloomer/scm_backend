package com.xinyirun.scm.core.system.service.sys.schedule.v2;

import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyVo;

public interface ISBDailyProductV2Service {

    /**
     * 生成每日报表
     * @param vo
     */
    void create(String parameterClass , String parameter);

    /**
     * 生成日加工报表
     * @param vo
     */
    void recreate(BProductDailyVo vo);

    /**
     * 生成日加工报表
     * @param vo
     */
    void recreate2Cancel(BProductDailyVo vo);
}
