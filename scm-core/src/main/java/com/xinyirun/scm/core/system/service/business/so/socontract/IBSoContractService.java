package com.xinyirun.scm.core.system.service.business.so.socontract;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.so.socontract.BSoContractEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractVo;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractImportVo;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCancelCommonCallBackService;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCommonCallBackService;

import java.util.List;

/**
 * <p>
 * 销售合同表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-22
 */
public interface IBSoContractService extends IService<BSoContractEntity> ,
        IBpmCommonCallBackService<BSoContractVo>,
        IBpmCancelCommonCallBackService<BSoContractVo> {

    /**
     * 销售合同  新增
     */
    InsertResultAo<BSoContractVo> startInsert(BSoContractVo soContractVo);

    /**
     * 分页查询
     */
    IPage<BSoContractVo> selectPage(BSoContractVo searchCondition);

    /**
     * 获取销售合同信息
     */
    BSoContractVo selectById(Integer id);

    /**
     * 更新销售合同信息
     */
    UpdateResultAo<Integer> startUpdate(BSoContractVo soContractVo);

    /**
     * 删除销售合同信息
     */
    DeleteResultAo<Integer> delete(List<BSoContractVo> searchCondition);

    /**
     * 按销售合同合计
     */
    BSoContractVo querySum(BSoContractVo searchCondition);

    /**
     * 销售合同校验
     */
    CheckResultAo checkLogic(BSoContractVo bean, String checkType);



    /**
     * 获取报表系统参数，并组装打印参数
     */
    BSoContractVo getPrintInfo(BSoContractVo searchCondition);

    /**
     * 导出查询
     */
    List<BSoContractVo> selectExportList(BSoContractVo param);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(BSoContractVo searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> finish(BSoContractVo searchCondition);

    /**
     * 导入数据
     */
    List<BSoContractImportVo> importData(List<BSoContractImportVo> beans);
}