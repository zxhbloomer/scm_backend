package com.xinyirun.scm.core.system.service.business.appay;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.appay.BApPayEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.appay.BApPayVo;

/**
 * <p>
 * 付款单表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
public interface IBApPayService extends IService<BApPayEntity> {

    /**
     * 付款单  新增
     */
    InsertResultAo<BApPayVo> startInsert(BApPayVo searchCondition);

    /**
     * 获取付款单信息
     */
    BApPayVo selectById(Integer id);

    /**
     * 分页查询
     */
    IPage<BApPayVo> selectPage(BApPayVo searchCondition);

    /**
     * 下推付款单
     */
    InsertResultAo<BApPayVo> insert(BApPayVo searchCondition);

    /**
     * 凭证上传、完成付款
     */
    UpdateResultAo<BApPayVo> payComplete(BApPayVo searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<BApPayVo> cancel(BApPayVo searchCondition);

    /**
     * 校验
     */
    CheckResultAo checkLogic(BApPayVo searchCondition, String checkType);

    /**
     * 汇总查询
     */
    BApPayVo querySum(BApPayVo searchCondition);

    /**
     * 单条汇总查询
     */
    BApPayVo queryViewSum(BApPayVo searchCondition);

}
