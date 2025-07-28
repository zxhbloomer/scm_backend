package com.xinyirun.scm.core.system.service.business.so.arreceive;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.so.arreceive.BArReceiveEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.so.arreceive.BArReceiveVo;

/**
 * <p>
 * 收款单表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
public interface IBArReceiveService extends IService<BArReceiveEntity> {

    /**
     * 收款单  新增
     */
    InsertResultAo<BArReceiveVo> startInsert(BArReceiveVo searchCondition);

    /**
     * 获取收款单信息
     */
    BArReceiveVo selectById(Integer id);

    /**
     * 分页查询
     */
    IPage<BArReceiveVo> selectPage(BArReceiveVo searchCondition);

    /**
     * 下推收款单
     */
    InsertResultAo<BArReceiveVo> insert(BArReceiveVo searchCondition);

    /**
     * 凭证上传、完成收款
     */
    UpdateResultAo<BArReceiveVo> receiveComplete(BArReceiveVo searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<BArReceiveVo> cancel(BArReceiveVo searchCondition);

    /**
     * 校验
     */
    CheckResultAo checkLogic(BArReceiveVo searchCondition, String checkType);

    /**
     * 汇总查询
     */
    BArReceiveVo querySum(BArReceiveVo searchCondition);

    /**
     * 单条汇总查询
     */
    BArReceiveVo queryViewSum(BArReceiveVo searchCondition);

}
