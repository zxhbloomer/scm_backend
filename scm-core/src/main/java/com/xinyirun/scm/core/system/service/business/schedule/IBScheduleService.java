package com.xinyirun.scm.core.system.service.business.schedule;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.schedule.BScheduleEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.schedule.BScheduleSumVo;
import com.xinyirun.scm.bean.system.vo.business.schedule.BScheduleVo;
import com.xinyirun.scm.bean.system.vo.excel.schedule.BScheduleExcelVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  调度服务类
 * </p>
 *
 * @author wwl
 * @since 2022-01-10
 */
public interface IBScheduleService extends IService<BScheduleEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<BScheduleVo> selectPage(BScheduleVo searchCondition);

    /**
     * 获取明细，页面查询
     */
    BScheduleVo get(BScheduleVo searchCondition);

    /**
     * 获取列表，页面查询
     */
    BScheduleVo selectByScheduleId(BScheduleVo searchCondition);

    /**
     * 插入一条记录
     */
    InsertResultAo<BScheduleVo> insert1(BScheduleVo vo);

    /**
     * 插入一条记录
     */
    InsertResultAo<BScheduleVo>  insert2(BScheduleVo vo);

    /**
     * 插入一条记录
     */
    UpdateResultAo<Integer> save(BScheduleVo vo);

    /**
     * 提交
     */
    UpdateResultAo<Integer> submit(List<BScheduleVo> searchCondition);

    /**
     * 审核
     */
    UpdateResultAo<Integer> audit(List<BScheduleVo> searchCondition);

    /**
     * 驳回
     */
    UpdateResultAo<Integer> reject(List<BScheduleVo> searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> finish(List<BScheduleVo> searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> enable(List<BScheduleVo> searchCondition);

    /**
     * 导出
     * @param searchCondition
     * @return
     */
    List<BScheduleExcelVo> selectList(List<BScheduleVo> searchCondition);

    /**
     * 导出全部
     * @param searchCondition
     * @return
     */
    List<BScheduleExcelVo> selectListExportAll(BScheduleVo searchCondition);

    /**
     * 物流订单增加合计
     * @param searchCondition
     * @return
     */
    BScheduleSumVo sumData(BScheduleVo searchCondition);

    /**
     * 根据 sku_id, out_owner_id, out_warehouse_id 查询可调度库存
     * @param searchCondition
     * @return
     */
    BigDecimal getScheduleQty(BScheduleVo searchCondition);

    /**
     * 更新物流订单
     * @param bean
     */
    UpdateResultAo<BScheduleVo> update1(BScheduleVo bean);

    /**
     * 更新物流调度
     * @param bean
     */
    UpdateResultAo<BScheduleVo> update2(BScheduleVo bean);

    /**
     * 作废
     * @param bean
     */
    void cancel(BScheduleVo bean);

    /**
     * 物流订单删除
     * @param bean
     */
    void delete(List<BScheduleVo> bean);

    List<BScheduleVo> selectScheduleByOrderId(Integer orderId, String orderType);

    /**
     * 查询 监管任务 是否有 备份
     * @param bean
     * @return
     */
    BScheduleVo selectMonitorIsBackup(BScheduleVo bean);

    /**
     * 完成 物流订单Id
     * @param scheduleIds
     */
    void completeSchedule(List<Integer> scheduleIds);

    /**
     * 物流直达单数据新增
     */
    InsertResultAo<BScheduleVo> insert3(BScheduleVo bean);

    /**
     * 物流直达单数据修改
     */
    UpdateResultAo<BScheduleVo> update3(BScheduleVo bean);

    /**
     * 物流直采单数据新增
     */
    InsertResultAo<BScheduleVo> insert4(BScheduleVo bean);

    /**
     * 物流直采单数据修改
     */
    UpdateResultAo<BScheduleVo> update4(BScheduleVo bean);

    /**
     * 物流直销单数据新增
     */
    InsertResultAo<BScheduleVo> insert5(BScheduleVo bean);

    /**
     * 物流直销单数据修改
     */
    UpdateResultAo<BScheduleVo> update5(BScheduleVo bean);
}
