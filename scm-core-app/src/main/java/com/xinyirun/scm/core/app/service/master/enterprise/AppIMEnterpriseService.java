package com.xinyirun.scm.core.app.service.master.enterprise;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.app.ao.result.AppCheckResultAo;
import com.xinyirun.scm.bean.app.vo.master.enterprise.AppMEnterpriseVo;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.excel.customer.MEnterpiseExcelVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseImportVo;
import com.xinyirun.scm.core.app.service.base.v1.AppIBaseService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 */
public interface AppIMEnterpriseService extends AppIBaseService<MEnterpriseEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<AppMEnterpriseVo> selectPage(AppMEnterpriseVo searchCondition) ;

    /**
     * 插入企业信息、并更新审批流
     */
    InsertResultAo<Integer> insert(AppMEnterpriseVo bean);

    /**
     * 修改企业信息、并更新审批流
     */
    UpdateResultAo<Integer> update(AppMEnterpriseVo bean);


    /**
     * 获取企业类型
     */
    List<AppMEnterpriseVo> getType(AppMEnterpriseVo searchCondition);

    /**
     * 获取详情
     */
    AppMEnterpriseVo getDetail(AppMEnterpriseVo searchCondition);

    AppMEnterpriseVo selectById(Integer id);

    /**
     * 删除企业信息
     */
    DeleteResultAo<Integer> delete(List<AppMEnterpriseVo> searchCondition);

    /**
     * 导出全部
     */
    List<MEnterpiseExcelVo> exportAll(AppMEnterpriseVo searchConditionList);

    /**
     * 获取所有调整信息
     */
    IPage<AppMEnterpriseVo> pagelistByAdjust(AppMEnterpriseVo searchCondition);

    AppMEnterpriseVo getAdjustDetail(AppMEnterpriseVo searchCondition);

    /**
     * 导入数据
     */
    List<MEnterpriseImportVo> importData(List<MEnterpriseImportVo> beans);

    /**
     *  审批流程通过 更新审核状态通过
     */
    UpdateResultAo<Integer> approveProcess(AppMEnterpriseVo searchCondition);

    /**
     *  审批流程拒绝 更新审核状态驳回
     */
    UpdateResultAo<Integer> rejectProcess(AppMEnterpriseVo searchCondition);

    /**
     *  审批流程撤销 更新审核状态驳回
     */
    UpdateResultAo<Integer> revokeProcss(AppMEnterpriseVo searchCondition);

    /**
     * 企业流程新增前校验
     */
    AppCheckResultAo checkLogic(AppMEnterpriseVo bean, String checkType);

    /**
     * 导出
     * @param searchConditionList 参数
     * @return List<MCustomerExcelVo>
     */
    List<MEnterpiseExcelVo> export(AppMEnterpriseVo searchConditionList);

    /**
     *  企业管理审批流程回调
     *  审批流程撤销 更新审核状态通过
     */
    UpdateResultAo<Integer> processCallBack(AppMEnterpriseVo searchCondition);
}
