package com.xinyirun.scm.core.system.service.business.wms.out;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutPlanDetailEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutPlanDetailVo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutPlanListVo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutPlanSaveVo;

import java.util.List;

/**
 * <p>
 * 出库计划详情 服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IBOutPlanDetailService extends IService<BOutPlanDetailEntity> {

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(BOutPlanSaveVo vo);

    /**
     * 修改数据
     * @param vo
     * @return
     */
    UpdateResultAo<Integer> update(BOutPlanSaveVo vo);

    /**
     * 插入一条记录（为调拨单服务）
     * @param bInPlanSaveVo
     * @return
     */
    InsertResultAo<Integer> insertForAllocate(BOutPlanSaveVo bInPlanSaveVo, Boolean auto);

    /**
     * 查询出库计划明细
     * @param id
     * @return
     */
    BOutPlanDetailVo selectById(Integer id);

    /**
     * 根据出库计划id查询明细
     * @param plan_id
     * @return
     */
    List<BOutPlanListVo> selectByPlanId(Integer plan_id);

    /**
     * 判断 当前 ID下的出库计划是否作废
     * @param id
     */
    void checkPalnStatus(Integer id);

    /**
     * 根据 计划 ID 查询 detail_id
     * @param plan_id
     * @return
     */
    List<BOutPlanListVo> selectByPlanIds(List<Integer> plan_id);

    /**
     * 查询 出库计划详情
     * @param orderId 销售订单id
     * @param orderType 订单类型
     * @return
     */
    List<BOutPlanListVo> selectOutPlanByOrderIdAndOrderType(Integer orderId, String orderType);

    /**
     * 查询 出库单详情
     * @param orderId 销售订单id
     * @param orderType 订单类型
     * @return
     */
    List<BOutPlanListVo> selectOutByOrderIdAndOrderType(Integer orderId, String orderType);

    /**
     * 出库计划数据新增保存 启动审批流
     */
    InsertResultAo<Integer> newInsert(BOutPlanSaveVo vo);
}
