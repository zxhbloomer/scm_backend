package com.xinyirun.scm.core.system.service.business.po.aprefundpay;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.po.aprefundpay.BApReFundPayEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.po.aprefundpay.BApReFundPayVo;

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
     * 凭证上传、完成退款
     */
    UpdateResultAo<BApReFundPayVo> refundComplete(BApReFundPayVo searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<BApReFundPayVo> cancel(BApReFundPayVo searchCondition);

    /**
     * 校验
     */
    CheckResultAo checkLogic(BApReFundPayVo searchCondition, String checkType);

    /**
     * 付款单  新增
     */
    InsertResultAo<BApReFundPayVo> startInsert(BApReFundPayVo searchCondition);

    /**
     * 汇总查询
     */
    BApReFundPayVo querySum(BApReFundPayVo searchCondition);

    /**
     * 单条汇总查询
     */
    BApReFundPayVo queryViewSum(BApReFundPayVo searchCondition);

}
