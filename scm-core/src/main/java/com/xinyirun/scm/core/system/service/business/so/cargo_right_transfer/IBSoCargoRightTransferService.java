package com.xinyirun.scm.core.system.service.business.so.cargo_right_transfer;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.so.cargo_right_transfer.BSoCargoRightTransferEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.so.cargo_right_transfer.BSoCargoRightTransferVo;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCancelCommonCallBackService;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCommonCallBackService;

import java.util.List;

/**
 * <p>
 * 销售货权转移表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-27
 */
public interface IBSoCargoRightTransferService extends IService<BSoCargoRightTransferEntity> ,
        IBpmCommonCallBackService<BSoCargoRightTransferVo>,
        IBpmCancelCommonCallBackService<BSoCargoRightTransferVo> {

    /**
     * 销售货权转移新增
     */
    InsertResultAo<BSoCargoRightTransferVo> startInsert(BSoCargoRightTransferVo BSoCargoRightTransferVo);

    /**
     * 分页查询
     */
    IPage<BSoCargoRightTransferVo> selectPage(BSoCargoRightTransferVo searchCondition);

    /**
     * 获取货权转移信息
     */
    BSoCargoRightTransferVo selectById(Integer id);

    /**
     * 更新货权转移信息
     */
    UpdateResultAo<Integer> startUpdate(BSoCargoRightTransferVo BSoCargoRightTransferVo);

    /**
     * 删除货权转移信息
     */
    DeleteResultAo<Integer> delete(List<BSoCargoRightTransferVo> searchCondition);

    /**
     * 按货权转移合计
     */
    BSoCargoRightTransferVo querySum(BSoCargoRightTransferVo searchCondition);

    /**
     * 货权转移校验
     */
    CheckResultAo checkLogic(BSoCargoRightTransferVo bean, String checkType);

    /**
     * 获取报表系统参数，并组装打印参数
     */
    BSoCargoRightTransferVo getPrintInfo(BSoCargoRightTransferVo searchCondition);

    /**
     * 导出查询
     */
    List<BSoCargoRightTransferVo> selectExportList(BSoCargoRightTransferVo param);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(BSoCargoRightTransferVo searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> finish(BSoCargoRightTransferVo searchCondition);

}