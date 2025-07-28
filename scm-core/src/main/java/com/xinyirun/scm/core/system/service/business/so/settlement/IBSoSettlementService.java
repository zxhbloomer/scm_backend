package com.xinyirun.scm.core.system.service.business.so.settlement;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.so.settlement.BSoSettlementEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.so.settlement.BSoSettlementVo;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCancelCommonCallBackService;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCommonCallBackService;

import java.util.List;

/**
 * 销售结算表 服务类
 */
public interface IBSoSettlementService extends IService<BSoSettlementEntity>,
        IBpmCommonCallBackService<BSoSettlementVo>,
        IBpmCancelCommonCallBackService<BSoSettlementVo> {

    /**
     * 获取业务类型
     */
    List<BSoSettlementVo> getType();

    /**
     * 新增
     */
    InsertResultAo<BSoSettlementVo> startInsert(BSoSettlementVo searchCondition);

    /**
     * 更新
     */
    UpdateResultAo<BSoSettlementVo> startUpdate(BSoSettlementVo searchCondition);

    /**
     * 分页查询
     */
    IPage<BSoSettlementVo> selectPage(BSoSettlementVo searchCondition);

    /**
     * 根据id查询
     */
    BSoSettlementVo selectById(Integer id);

    /**
     * 校验
     */
    CheckResultAo checkLogic(BSoSettlementVo searchCondition, String checkType);

    /**
     * 导出查询
     */
    List<BSoSettlementVo> selectExportList(BSoSettlementVo param);

    /**
     * 获取报表系统参数，并组装打印参数
     */
    BSoSettlementVo getPrintInfo(BSoSettlementVo searchCondition);

    /**
     * 删除
     */
    DeleteResultAo<Integer> delete(List<BSoSettlementVo> searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(BSoSettlementVo searchCondition);

    /**
     * 汇总查询
     */
    BSoSettlementVo querySum(BSoSettlementVo searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> finish(BSoSettlementVo searchCondition);


}