package com.xinyirun.scm.core.system.service.business.so.arrefundreceive;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.so.arrefundreceive.BArReFundReceiveEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.so.arrefundreceive.BArReFundReceiveVo;

/**
 * <p>
 * 退款单表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
public interface IBArReFundReceiveService extends IService<BArReFundReceiveEntity> {

    /**
     * 下推退款单
     */
    InsertResultAo<BArReFundReceiveVo> insert(BArReFundReceiveVo searchCondition);

    /**
     * 列表查询
     */
    IPage<BArReFundReceiveVo> selectPage(BArReFundReceiveVo searchCondition);

    /**
     * 获取单条数据
     */
    BArReFundReceiveVo selectById(Integer id);

    /**
     * 凭证上传、完成退款
     */
    UpdateResultAo<BArReFundReceiveVo> refundComplete(BArReFundReceiveVo searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<BArReFundReceiveVo> cancel(BArReFundReceiveVo searchCondition);

    /**
     * 校验
     */
    CheckResultAo checkLogic(BArReFundReceiveVo searchCondition, String checkType);

    /**
     * 退款单  新增
     */
    InsertResultAo<BArReFundReceiveVo> startInsert(BArReFundReceiveVo searchCondition);

    /**
     * 汇总查询
     */
    BArReFundReceiveVo querySum(BArReFundReceiveVo searchCondition);

    /**
     * 单条汇总查询
     */
    BArReFundReceiveVo queryViewSum(BArReFundReceiveVo searchCondition);

}