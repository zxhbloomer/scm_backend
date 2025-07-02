package com.xinyirun.scm.core.system.service.business.settlement;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.settlement.BPoSettlementEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.settlement.BPoSettlementVo;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCancelCommonCallBackService;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCommonCallBackService;

import java.util.List;

/**
 * 采购结算表 服务类
 */
public interface IBPoSettlementService extends IService<BPoSettlementEntity>,
        IBpmCommonCallBackService<BPoSettlementVo>,
        IBpmCancelCommonCallBackService<BPoSettlementVo> {

    /**
     * 获取业务类型
     */
    List<BPoSettlementVo> getType();

    /**
     * 新增
     */
    InsertResultAo<BPoSettlementVo> startInsert(BPoSettlementVo searchCondition);

    /**
     * 更新
     */
    UpdateResultAo<BPoSettlementVo> startUpdate(BPoSettlementVo searchCondition);

    /**
     * 分页查询
     */
    IPage<BPoSettlementVo> selectPage(BPoSettlementVo searchCondition);

    /**
     * 根据id查询
     */
    BPoSettlementVo selectById(Integer id);

    /**
     * 校验
     */
    CheckResultAo checkLogic(BPoSettlementVo searchCondition, String checkType);

    /**
     * 导出查询
     */
    List<BPoSettlementVo> selectExportList(BPoSettlementVo param);

    /**
     * 获取报表系统参数，并组装打印参数
     */
    BPoSettlementVo getPrintInfo(BPoSettlementVo searchCondition);

    /**
     * 删除
     */
    DeleteResultAo<Integer> delete(List<BPoSettlementVo> searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(BPoSettlementVo searchCondition);

    /**
     * 汇总查询
     */
    BPoSettlementVo querySum(BPoSettlementVo searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> finish(BPoSettlementVo searchCondition);


} 