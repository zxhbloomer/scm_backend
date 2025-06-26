package com.xinyirun.scm.core.system.service.wms.in;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.in.BInEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.wms.in.BInVo;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCancelCommonCallBackService;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCommonCallBackService;

import java.util.List;

/**
 * <p>
 * 入库单 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-26
 */
public interface IBInService extends IService<BInEntity>,
        IBpmCommonCallBackService<BInVo>,
        IBpmCancelCommonCallBackService<BInVo> {

    /**
     * 新增入库单
     */
    InsertResultAo<BInVo> insert(BInVo bInVo);

    /**
     * 启动审批流新增入库单
     */
    InsertResultAo<BInVo> startInsert(BInVo bInVo);

    /**
     * 分页查询
     */
    IPage<BInVo> selectPage(BInVo searchCondition);

    /**
     * 根据ID查询
     */
    BInVo selectById(Integer id);

    /**
     * 修改
     */
    UpdateResultAo<BInVo> update(BInVo bInVo);

    /**
     * 启动审批流修改入库单
     */
    UpdateResultAo<BInVo> startUpdate(BInVo bInVo);

    /**
     * 删除
     */
    DeleteResultAo<Integer> delete(BInVo bInVo);

    /**
     * 校验业务逻辑
     */
    CheckResultAo checkLogic(BInVo bean, String checkType);

    /**
     * 导出列表
     */
    List<BInVo> selectExportList(BInVo param);

    /**
     * 合计查询
     */
    BInVo querySum(BInVo searchCondition);

    /**
     * 获取打印信息
     */
    BInVo getPrintInfo(BInVo searchCondition);

    /**
     * 删除多个
     */
    DeleteResultAo<Integer> delete(List<BInVo> searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(BInVo searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> finish(BInVo searchCondition);

    /**
     * check批次是否重复
     * @param lot
     * @return
     */
    boolean isDuplicate(String lot);

    /**
     * 悲观锁
     * @param id
     * @return
     */
    BInEntity setBillInForUpdate(Integer id);
}
