package com.xinyirun.scm.core.system.service.query.contract;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.vo.report.contract.PurchaseContractStatisticsExportVo;
import com.xinyirun.scm.bean.system.vo.report.contract.PurchaseContractStatisticsVo;
import com.xinyirun.scm.bean.system.vo.report.contract.SalesContractStatisticsExportVo;
import com.xinyirun.scm.bean.system.vo.report.contract.SalesContractStatisticsVo;

import java.util.List;

public interface ContractStatisticsService {

    /**
     * 采购合同统计表
     * @param param
     * @return
     */
    IPage<PurchaseContractStatisticsVo> queryPageList(PurchaseContractStatisticsVo param);

    /**
     * 采购合同统计表
     * @param param 参数
     * @return
     */
    PurchaseContractStatisticsVo getListSum(PurchaseContractStatisticsVo param);


    List<PurchaseContractStatisticsExportVo> getExportList(PurchaseContractStatisticsVo param);

    /**
     * 查询销售合同量
     * @param param
     * @return
     */
    IPage<SalesContractStatisticsVo> selectSalesPageList(SalesContractStatisticsVo param);

    /**
     * 查询销售合同量 合计
     * @param param
     * @return
     */
    SalesContractStatisticsVo selectSalesPageListSum(SalesContractStatisticsVo param);

    /**
     * 销售合同量 导出
     * @param param
     * @return
     */
    List<SalesContractStatisticsExportVo> selectSalesListExport(SalesContractStatisticsVo param);
}
