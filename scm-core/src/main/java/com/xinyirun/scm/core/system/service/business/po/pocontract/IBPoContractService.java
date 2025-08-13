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
    InsertResultAo<BPoContractVo> startInsert(BPoContractVo bPoContractVo);

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
    UpdateResultAo<Integer> startUpdate(BPoContractVo bPoContractVo);

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
    UpdateResultAo<Integer> complete(BPoContractVo searchCondition);
    
    /**
     * 完成校验
     */
    CheckResultAo validateComplete(BPoContractVo contractVo);

    /**
     * 导入数据
     */
    List<BPoContractImportVo> importData(List<BPoContractImportVo
            > beans);

    /**
     * 全部导出 - 根据查询条件导出所有符合条件的采购合同数据
     * 用于Excel导出功能，返回完整的采购合同数据包含商品明细
     * 
     * @param param 查询条件参数
     * @return 符合条件的采购合同导出列表，包含完整的关联数据和商品明细展开
     */
    List<com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractExportVo> exportAll(BPoContractVo param);

    /**
     * 选中导出 - 根据ID列表导出指定的采购合同数据
     * 根据传入的采购合同参数查询指定的采购合同数据，用于选中记录导出
     * 包含导出状态管理、导出数量限制检查、数据转换等业务逻辑
     * 
     * @param param 查询参数，包含要导出的合同ID列表和其他查询条件
     * @return 指定ID的采购合同导出列表，包含完整的关联数据和商品明细展开
     * @throws IOException 当查询或数据转换失败时抛出
     */
    List<com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractExportVo> exportByIds(BPoContractVo param) throws java.io.IOException;
}
