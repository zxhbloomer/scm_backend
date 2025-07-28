package com.xinyirun.scm.core.system.service.business.wms.outplan;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.wms.outplan.BOutPlanEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.wms.outplan.BOutPlanVo;
import com.xinyirun.scm.bean.system.vo.business.wms.outplan.BOutPlanDetailVo;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCancelCommonCallBackService;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCommonCallBackService;

import java.util.List;

/**
 * <p>
 * 出库计划表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-07
 */
public interface IBOutPlanService extends IService<BOutPlanEntity>,
        IBpmCommonCallBackService<BOutPlanVo>,
        IBpmCancelCommonCallBackService<BOutPlanVo> {

    /**
     * 新增出库计划
     */
    InsertResultAo<BOutPlanVo> insert(BOutPlanVo bOutPlanVo);

    /**
     * 启动审批流新增出库计划
     */
    InsertResultAo<BOutPlanVo> startInsert(BOutPlanVo bOutPlanVo);

    /**
     * 分页查询
     */
    IPage<BOutPlanVo> selectPage(BOutPlanVo searchCondition);

    /**
     * 根据ID查询
     */
    BOutPlanVo selectById(Integer id);

    /**
     * 修改
     */
    UpdateResultAo<BOutPlanVo> update(BOutPlanVo bOutPlanVo);

    /**
     * 启动审批流修改出库计划
     */
    UpdateResultAo<BOutPlanVo> startUpdate(BOutPlanVo bOutPlanVo);

    /**
     * 删除
     */
    DeleteResultAo<Integer> delete(BOutPlanVo bOutPlanVo);

    /**
     * 校验业务逻辑
     */
    CheckResultAo checkLogic(BOutPlanVo bean, String checkType);

    /**
     * 导出列表
     */
    List<BOutPlanVo> selectExportList(BOutPlanVo param);

    /**
     * 合计查询
     */
    BOutPlanVo querySum(BOutPlanVo searchCondition);

    /**
     * 获取打印信息
     */
    BOutPlanVo getPrintInfo(BOutPlanVo searchCondition);

    /**
     * 删除多个
     */
    DeleteResultAo<Integer> delete(List<BOutPlanVo> searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(BOutPlanVo searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> finish(BOutPlanVo searchCondition);

    /**
     * 初始化计划数据
     */
    List<BOutPlanDetailVo> initPlanData(BOutPlanDetailVo searchCondition);

}