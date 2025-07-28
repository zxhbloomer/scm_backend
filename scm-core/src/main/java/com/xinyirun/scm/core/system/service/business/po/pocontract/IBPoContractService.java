package com.xinyirun.scm.core.system.service.business.po.pocontract;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.po.pocontract.BPoContractEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractImportVo;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractVo;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCancelCommonCallBackService;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCommonCallBackService;

import java.util.List;

/**
 * <p>
 * 采购合同表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
public interface IBPoContractService extends IService<BPoContractEntity> ,
        IBpmCommonCallBackService<BPoContractVo>,
        IBpmCancelCommonCallBackService<BPoContractVo> {

    /**
     * 采购合同  新增
     */
    InsertResultAo<BPoContractVo> startInsert(BPoContractVo BPoContractVo);

    /**
     * 分页查询
     */
    IPage<BPoContractVo> selectPage(BPoContractVo searchCondition);

    /**
     * 获取采购合同信息
     */
    BPoContractVo selectById(Integer id);

    /**
     * 更新采购合同信息
     */
    UpdateResultAo<Integer> startUpdate(BPoContractVo BPoContractVo);

    /**
     * 删除采购合同信息
     */
    DeleteResultAo<Integer> delete(List<BPoContractVo> searchCondition);

    /**
     * 按采购合同合计
     */
    BPoContractVo querySum(BPoContractVo searchCondition);

    /**
     * 采购合同校验
     */
    CheckResultAo checkLogic(BPoContractVo bean, String checkType);



    /**
     * 获取报表系统参数，并组装打印参数
     */
    BPoContractVo getPrintInfo(BPoContractVo searchCondition);

    /**
     * 导出查询
     */
    List<BPoContractVo> selectExportList(BPoContractVo param);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(BPoContractVo searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> finish(BPoContractVo searchCondition);

    /**
     * 导入数据
     */
    List<BPoContractImportVo> importData(List<BPoContractImportVo
            > beans);
}
