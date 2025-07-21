package com.xinyirun.scm.core.system.service.business.po.cargo_right_transfer;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.po.cargo_right_transfer.BCargoRightTransferEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BCargoRightTransferImportVo;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BCargoRightTransferVo;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCancelCommonCallBackService;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCommonCallBackService;

import java.util.List;

/**
 * <p>
 * 货权转移表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-20
 */
public interface IBCargoRightTransferService extends IService<BCargoRightTransferEntity> ,
        IBpmCommonCallBackService<BCargoRightTransferVo>,
        IBpmCancelCommonCallBackService<BCargoRightTransferVo> {

    /**
     * 货权转移新增
     */
    InsertResultAo<BCargoRightTransferVo> startInsert(BCargoRightTransferVo BCargoRightTransferVo);

    /**
     * 分页查询
     */
    IPage<BCargoRightTransferVo> selectPage(BCargoRightTransferVo searchCondition);

    /**
     * 获取货权转移信息
     */
    BCargoRightTransferVo selectById(Integer id);

    /**
     * 更新货权转移信息
     */
    UpdateResultAo<Integer> startUpdate(BCargoRightTransferVo BCargoRightTransferVo);

    /**
     * 删除货权转移信息
     */
    DeleteResultAo<Integer> delete(List<BCargoRightTransferVo> searchCondition);

    /**
     * 按货权转移合计
     */
    BCargoRightTransferVo querySum(BCargoRightTransferVo searchCondition);

    /**
     * 货权转移校验
     */
    CheckResultAo checkLogic(BCargoRightTransferVo bean, String checkType);

    /**
     * 获取报表系统参数，并组装打印参数
     */
    BCargoRightTransferVo getPrintInfo(BCargoRightTransferVo searchCondition);

    /**
     * 导出查询
     */
    List<BCargoRightTransferVo> selectExportList(BCargoRightTransferVo param);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(BCargoRightTransferVo searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> finish(BCargoRightTransferVo searchCondition);

}