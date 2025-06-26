package com.xinyirun.scm.core.system.service.master.enterprise;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseEntity;
import com.xinyirun.scm.bean.entity.master.org.MCompanyEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseHisVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseImportVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseVo;
import com.xinyirun.scm.bean.system.vo.excel.customer.MEnterpiseExcelVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 */
public interface IMEnterpriseService extends IService<MEnterpriseEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<MEnterpriseVo> selectPage(MEnterpriseVo searchCondition) ;

    /**
     * 插入企业信息、并更新审批流
     */
    InsertResultAo<Integer> insert(MEnterpriseVo bean);

    /**
     * 修改企业信息、并更新审批流
     */
    UpdateResultAo<Integer> update(MEnterpriseVo bean);


    /**
     * 获取企业类型
     */
    List<MEnterpriseVo> getType(MEnterpriseVo searchCondition);

    /**
     * 获取详情
     */
    MEnterpriseVo getDetail(MEnterpriseVo searchCondition);

    /**
     * 获取单条数据
     */
    MEnterpriseVo selectById(Integer id);

    /**
     * 删除企业信息
     */
    DeleteResultAo<Integer> delete(List<MEnterpriseVo> searchCondition);

    /**
     * 导出全部
     */
    List<MEnterpiseExcelVo> exportAll(MEnterpriseVo searchConditionList);

    /**
     * 获取所有调整信息
     */
    List<MEnterpriseHisVo> getAdjustList(MEnterpriseHisVo searchCondition);

    MEnterpriseVo getAdjustDetail(MEnterpriseVo searchCondition);

    /**
     * 导入数据
     */
    List<MEnterpriseImportVo> importData(List<MEnterpriseImportVo> beans);

    /**
     * 企业流程新增前校验
     */
    CheckResultAo checkLogic(MEnterpriseVo bean, String checkType);

    /**
     * 导出
     * @param searchConditionList 参数
     * @return List<MCustomerExcelVo>
     */
    List<MEnterpiseExcelVo> export(MEnterpriseVo searchConditionList);

    /**
     *
     *  企业管理审批流程回调
     *  审批流程创建时
     */
    UpdateResultAo<Integer> bpmCallBackCreateBpm(MEnterpriseVo searchCondition);

    /**
     *  审批流程通过 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCallBackApprove(MEnterpriseVo searchCondition);

    /**
     *  审批流程拒绝 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCallBackRefuse(MEnterpriseVo searchCondition);

    /**
     *  审批流程撤销 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCallBackCancel(MEnterpriseVo searchCondition);

    /**
     *
     *  企业管理审批流程回调
     *  审批流程撤销 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCallBackSave(MEnterpriseVo searchCondition);

    /**
     *  获取报表系统参数，并组装打印参数
     */
    MEnterpriseVo getPrintInfo(MEnterpriseVo searchCondition);

    /**
     * 获取企业下拉列表数据（交易对手、供应商）
     */
    IPage<MEnterpriseVo> selectCounterpartySupplierGridData(MEnterpriseVo searchCondition);

    /**
     * 获取企业下拉列表数据（交易对手、买方、经销商）
     */
    IPage<MEnterpriseVo> selectCounterpartyCustomerGridData(MEnterpriseVo searchCondition);

    /**
     * 获取企业下拉列表数据（主体企业、系统企业、供应商）
     */
    IPage<MEnterpriseVo> selectSystemEnterpriseSupplierGridData(MEnterpriseVo searchCondition);

    /**
     * 获取企业下拉列表数据（主体企业、系统企业、买方、经销商）
     */
    IPage<MEnterpriseVo> selectSystemEnterpriseCustomerGridData(MEnterpriseVo searchCondition);

    /**
     * 根据查询条件，获取企业列表（交易对手、供应商）
     */
    IPage<MEnterpriseVo> getCounterpartySupplierList(MEnterpriseVo searchCondition) ;
    /**
     * 根据查询条件，获取企业列表（主体企业、系统企业、买方、经销商）
     */
    IPage<MEnterpriseVo> getSystemEnterpriseCustomerList(MEnterpriseVo searchCondition) ;
    
    /**
     * 根据组织模块公司数据新增系统企业
     * @param companyEntity 组织模块公司实体
     * @return 插入结果
     */
    InsertResultAo<Integer> insertSystemEnterpriseByOrgCompany(MCompanyEntity companyEntity);
    
    /**
     * 根据组织模块公司数据更新系统企业
     * @param companyEntity 组织模块公司实体
     * @return 更新结果
     */
    UpdateResultAo<Integer> updateSystemEnterpriseByOrgCompany(MCompanyEntity companyEntity);
}
