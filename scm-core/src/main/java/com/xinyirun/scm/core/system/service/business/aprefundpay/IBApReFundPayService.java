package com.xinyirun.scm.core.system.service.business.aprefundpay;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.aprefundpay.BApReFundPayEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundPayVo;

/**
 * <p>
 * 退款单表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
public interface IBApReFundPayService extends IService<BApReFundPayEntity> {

    /**
     * 下推付款单
     */
    InsertResultAo<BApReFundPayVo> insert(BApReFundPayVo searchCondition);

    /**
     * 列表查询
     */
    IPage<BApReFundPayVo> selectPage(BApReFundPayVo searchCondition);

    /**
     * 获取单条数据
     */
    BApReFundPayVo selectById(Integer id);

    /**
     * 付款复核
     */
    UpdateResultAo<BApReFundPayVo> paymentReview(BApReFundPayVo searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<BApReFundPayVo> cancel(BApReFundPayVo searchCondition);

}
