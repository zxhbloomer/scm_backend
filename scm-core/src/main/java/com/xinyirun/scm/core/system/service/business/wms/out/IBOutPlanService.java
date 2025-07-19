package com.xinyirun.scm.core.system.service.business.wms.out;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutCheckVo;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutPlanEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.*;
import com.xinyirun.scm.bean.system.vo.business.wms.out.receive.BReceiveVo;
import com.xinyirun.scm.bean.system.vo.excel.out.BOutPlanExportVo;

import java.util.List;

/**
 * <p>
 * 出库计划 服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IBOutPlanService extends IService<BOutPlanEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<BOutPlanListVo> selectPage(BOutPlanListVo searchCondition) ;

    /**
     * 获取待办数量
     */
    Integer selectTodoCount(BOutPlanListVo searchCondition);

    /**
     * 获取合计信息，页面查询
     */
    BOutPlanSumVo selectSumData(BOutPlanListVo searchCondition) ;

    /**
     * 获取列表，页面查询
     */
    List<BOutPlanListVo> selectList(BOutPlanListVo searchCondition) ;

    /**
     * 查询出库计划
     */
    BOutPlanSaveVo get(BOutPlanSaveVo vo) ;

    /**
     * 查询出库操作页面对象
     * @param vo
     * @return
     */
    BOutPlanDetailVo getPlanDetail(BOutPlanDetailVo vo);

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    BOutPlanVo selectById(Integer id);

    /**
     * 查询by id，返回更新对象结果
     *
     * @param id
     * @return
     */
    List<BOutPlanListVo> selectBySaveId(int id);

    /**
     * 查询by id，返回出库操作对象
     *
     * @param id
     * @return
     */
    BOutPlanOperateVo selectByOperateId(int id);

    /**
     * 出库操作
     * @param vo
     * @return
     */
    InsertResultAo<Integer> operate(BOutPlanOperateVo vo);

    /**
     * 批量提交
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> submit(List<BOutPlanListVo> searchCondition);

    /**
     * 批量审核
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> audit(List<BOutPlanListVo> searchCondition);

    /**
     * 批量作废审核
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> cancelAudit(List<BOutPlanListVo> searchCondition);

    /**
     * 批量作废
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> cancel(List<BOutPlanListVo> searchCondition);

    /**
     * 直接作废, 用于生产订单
     * @param searchCondition
     */
    void cancelDirect(List<BOutPlanListVo> searchCondition);

    /**
     * 批量驳回
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> reject(List<BOutPlanListVo> searchCondition);

    /**
     * 批量作废驳回
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> cancelReject(List<BOutPlanListVo> searchCondition);

    /**
     * 批量完成
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> finish(List<BOutPlanListVo> searchCondition);

    List<BOutPlanExportVo> selectExportList(List<BOutPlanListVo> searchCondition);

    List<BOutPlanExportVo> selectExportAllList(BOutPlanListVo searchCondition);

    /**
     * 根据 plan_id 查询有几条明细
     * @param searchCondition
     * @return
     */
    Integer getDetailCount(List<BOutPlanListVo> searchCondition);

    ApiOutCheckVo selectOutCheckVo(Integer id);

    List<ApiOutCheckVo> selectOutCheckVoByOutBill(List<BOutVo> beans);

    /**
     * 查询出库单计划列表, 不查询总数量
     * @param searchCondition
     * @return
     */
    List<BOutPlanListVo> selectPageListNotCount(BOutPlanListVo searchCondition);

    /**
     * 查询出库计划单 总条数
     * @param searchCondition
     * @return
     */
    BOutPlanListVo selectPageListCount(BOutPlanListVo searchCondition);

    /**
     * 收货操作
     */
    InsertResultAo<Integer> operateDelivery(BOutPlanOperateVo vo);

    List<ApiOutCheckVo> selectReceiveCheckVoByOutBill(List<BReceiveVo> beans);

    /**
     * 根据出库计划id获取出库计划详情
     */
    BOutPlanSaveVo newGet(BOutPlanSaveVo vo);
}
