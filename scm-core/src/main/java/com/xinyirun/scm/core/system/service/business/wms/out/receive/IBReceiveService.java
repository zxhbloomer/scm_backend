package com.xinyirun.scm.core.system.service.business.wms.out.receive;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.wms.out.receive.BReceiveEntity;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.receive.BReceiveSumVo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.receive.BReceiveVo;
import com.xinyirun.scm.bean.system.vo.excel.out.BReceiveExportVo;

import java.util.List;

/**
 * <p>
 * 收货单 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-07-01
 */
public interface IBReceiveService extends IService<BReceiveEntity> {

    /**
     * 查询收货单列表
     */
    List<BReceiveVo> selectPageListNotCount(BReceiveVo searchCondition);

    /**
     * 查询总条数
     */
    BReceiveVo selectListCount(BReceiveVo searchCondition);

    /**
     * 获取代办信息
     */
    Integer selectTodoCount(BReceiveVo searchCondition);

    /**
     * 页面统计信息
     */
    BReceiveSumVo selectSumData(BReceiveVo searchCondition);

    /**
     * 获取收货单详情
     */
    BReceiveVo selectById(Integer id);

    /**
     * 修改收获单
     */
    UpdateResultAo<Integer> update(BReceiveVo vo);

    /**
     * 批量提交
     */
    UpdateResultAo<Boolean> submit(List<BReceiveVo> searchConditionList);

    /**
     * 批量审核
     */
    UpdateResultAo<Boolean> audit(List<BReceiveVo> searchCondition);

    /**
     * 批量审核取消
     */
    UpdateResultAo<Boolean> cancelAudit(List<BReceiveVo> searchConditionList);

    /**
     * 批量驳回
     */
    UpdateResultAo<Boolean> reject(List<BReceiveVo> searchConditionList);

    /**
     * 批量取消驳回
     */
    UpdateResultAo<Boolean> cancelReject(List<BReceiveVo> searchConditionList);

    List<BReceiveVo> selectList(BReceiveVo searchCondition);

    /**
     * 批量作废
     */
    UpdateResultAo<Boolean> cancel(List<BReceiveVo> searchCondition);

    List<BReceiveExportVo> selectExportList(List<BReceiveVo> searchCondition);

    List<BReceiveExportVo> selectExportAllList(BReceiveVo searchCondition);
}
