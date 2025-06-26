package com.xinyirun.scm.core.system.service.business.ap;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.ap.BApEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.ap.BApVo;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCancelCommonCallBackService;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCommonCallBackService;

import java.util.List;

/**
 * <p>
 * 应付账款管理表（Accounts Payable） 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
public interface IBApService extends IService<BApEntity> ,
        IBpmCommonCallBackService<BApVo>,
        IBpmCancelCommonCallBackService<BApVo> {

    /**
     * 获取业务类型
     */
    List<BApVo> getType();

    /**
     * 新增
     */
    InsertResultAo<BApVo> startInsert(BApVo searchCondition);

    /**
     * 更新
     */
    UpdateResultAo<BApVo> startUpdate(BApVo searchCondition);

    /**
     * 分页查询
     */
    IPage<BApVo> selectPage(BApVo searchCondition);

    /**
     * 根据id查询
     */
    BApVo selectById(Integer id);

    /**
     * 校验
     */
    CheckResultAo checkLogic(BApVo searchCondition, String checkType);

    /**
     * 导出查询
     */
    List<BApVo> selectExportList(BApVo param);

    /**
     * 获取报表系统参数，并组装打印参数
     */
    BApVo getPrintInfo(BApVo searchCondition);

    /**
     * 删除
     */
    DeleteResultAo<Integer> delete(List<BApVo> searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<BApVo> cancel(BApVo searchCondition);

    /**
     * 中止付款
     */
    UpdateResultAo<Integer> suspendPayment(BApVo searchCondition);

    /**
     * 获取下推预付退款款数据
     */
    BApVo getApRefund(BApVo searchCondition);

    /**
     * 汇总查询
     */
    BApVo querySum(BApVo searchCondition);
}
