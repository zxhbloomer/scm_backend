package com.xinyirun.scm.core.system.service.business.po.cargo_right_transfer;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.po.cargo_right_transfer.BPoCargoRightTransferEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BPoCargoRightTransferImportVo;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BPoCargoRightTransferVo;
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
public interface IBPoCargoRightTransferService extends IService<BPoCargoRightTransferEntity> ,
        IBpmCommonCallBackService<BPoCargoRightTransferVo>,
        IBpmCancelCommonCallBackService<BPoCargoRightTransferVo> {

    /**
     * 货权转移新增
     */
    InsertResultAo<BPoCargoRightTransferVo> startInsert(BPoCargoRightTransferVo BPoCargoRightTransferVo);

    /**
     * 分页查询
     */
    IPage<BPoCargoRightTransferVo> selectPage(BPoCargoRightTransferVo searchCondition);

    /**
     * 获取货权转移信息
     */
    BPoCargoRightTransferVo selectById(Integer id);

    /**
     * 更新货权转移信息
     */
    UpdateResultAo<Integer> startUpdate(BPoCargoRightTransferVo BPoCargoRightTransferVo);

    /**
     * 删除货权转移信息
     */
    DeleteResultAo<Integer> delete(List<BPoCargoRightTransferVo> searchCondition);

    /**
     * 按货权转移合计
     */
    BPoCargoRightTransferVo querySum(BPoCargoRightTransferVo searchCondition);

    /**
     * 货权转移校验
     */
    CheckResultAo checkLogic(BPoCargoRightTransferVo bean, String checkType);

    /**
     * 获取报表系统参数，并组装打印参数
     */
    BPoCargoRightTransferVo getPrintInfo(BPoCargoRightTransferVo searchCondition);

    /**
     * 导出查询
     */
    List<BPoCargoRightTransferVo> selectExportList(BPoCargoRightTransferVo param);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(BPoCargoRightTransferVo searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> finish(BPoCargoRightTransferVo searchCondition);

}