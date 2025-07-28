package com.xinyirun.scm.core.system.service.business.wms.out;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.wms.out.BOutEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutVo;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCancelCommonCallBackService;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCommonCallBackService;

import java.util.List;

/**
 * <p>
 * 出库单 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-26
 */
public interface IBOutService extends IService<BOutEntity>,
        IBpmCommonCallBackService<BOutVo>,
        IBpmCancelCommonCallBackService<BOutVo> {

    /**
     * 新增出库单
     */
    InsertResultAo<BOutVo> insert(BOutVo bOutVo);

    /**
     * 启动审批流新增出库单
     */
    InsertResultAo<BOutVo> startInsert(BOutVo bOutVo);

    /**
     * 分页查询
     */
    IPage<BOutVo> selectPage(BOutVo searchCondition);

    /**
     * 根据ID查询
     */
    BOutVo selectById(Integer id);

    /**
     * 修改
     */
    UpdateResultAo<BOutVo> update(BOutVo bOutVo);

    /**
     * 启动审批流修改出库单
     */
    UpdateResultAo<BOutVo> startUpdate(BOutVo bOutVo);

    /**
     * 删除
     */
    DeleteResultAo<Integer> delete(BOutVo bOutVo);

    /**
     * 校验业务逻辑
     */
    CheckResultAo checkLogic(BOutVo bean, String checkType);

    /**
     * 导出列表
     */
    List<BOutVo> selectExportList(BOutVo param);

    /**
     * 合计查询
     */
    BOutVo querySum(BOutVo searchCondition);

    /**
     * 获取打印信息
     */
    BOutVo getPrintInfo(BOutVo searchCondition);

    /**
     * 删除多个
     */
    DeleteResultAo<Integer> delete(List<BOutVo> searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(BOutVo searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> finish(BOutVo searchCondition);

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
    BOutEntity setBillOutForUpdate(Integer id);
}