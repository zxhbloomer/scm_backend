package com.xinyirun.scm.core.system.service.business.wms.inplan;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.wms.inplan.BInPlanEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.wms.inplan.BInPlanVo;
import com.xinyirun.scm.bean.system.vo.wms.inplan.BInPlanDetailVo;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCancelCommonCallBackService;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCommonCallBackService;

import java.util.List;

/**
 * <p>
 * 入库计划表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-07
 */
public interface IBInPlanService extends IService<BInPlanEntity>,
        IBpmCommonCallBackService<BInPlanVo>,
        IBpmCancelCommonCallBackService<BInPlanVo> {

    /**
     * 新增入库计划
     */
    InsertResultAo<BInPlanVo> insert(BInPlanVo bInPlanVo);

    /**
     * 启动审批流新增入库计划
     */
    InsertResultAo<BInPlanVo> startInsert(BInPlanVo bInPlanVo);

    /**
     * 分页查询
     */
    IPage<BInPlanVo> selectPage(BInPlanVo searchCondition);

    /**
     * 根据ID查询
     */
    BInPlanVo selectById(Integer id);

    /**
     * 修改
     */
    UpdateResultAo<BInPlanVo> update(BInPlanVo bInPlanVo);

    /**
     * 启动审批流修改入库计划
     */
    UpdateResultAo<BInPlanVo> startUpdate(BInPlanVo bInPlanVo);

    /**
     * 删除
     */
    DeleteResultAo<Integer> delete(BInPlanVo bInPlanVo);

    /**
     * 校验业务逻辑
     */
    CheckResultAo checkLogic(BInPlanVo bean, String checkType);

    /**
     * 导出列表
     */
    List<BInPlanVo> selectExportList(BInPlanVo param);

    /**
     * 合计查询
     */
    BInPlanVo querySum(BInPlanVo searchCondition);

    /**
     * 获取打印信息
     */
    BInPlanVo getPrintInfo(BInPlanVo searchCondition);

    /**
     * 删除多个
     */
    DeleteResultAo<Integer> delete(List<BInPlanVo> searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(BInPlanVo searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> finish(BInPlanVo searchCondition);

    /**
     * 初始化计划数据
     */
    List<BInPlanDetailVo> initPlanData(BInPlanDetailVo searchCondition);

}
