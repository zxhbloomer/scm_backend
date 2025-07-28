package com.xinyirun.scm.core.system.service.business.so.ar;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.so.ar.BArEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArVo;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCancelCommonCallBackService;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCommonCallBackService;

import java.util.List;

/**
 * <p>
 * 应收账款管理表（Accounts Receivable） 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
public interface IBArService extends IService<BArEntity> ,
        IBpmCommonCallBackService<BArVo>,
        IBpmCancelCommonCallBackService<BArVo> {

    /**
     * 获取业务类型
     */
    List<BArVo> getType();

    /**
     * 新增
     */
    InsertResultAo<BArVo> startInsert(BArVo searchCondition);

    /**
     * 更新
     */
    UpdateResultAo<BArVo> startUpdate(BArVo searchCondition);

    /**
     * 分页查询
     */
    IPage<BArVo> selectPage(BArVo searchCondition);


    /**
     * 根据id查询
     */
    BArVo selectById(Integer id);

    /**
     * 校验
     */
    CheckResultAo checkLogic(BArVo searchCondition, String checkType);

    /**
     * 导出查询
     */
    List<BArVo> selectExportList(BArVo param);

    /**
     * 获取报表系统参数，并组装打印参数
     */
    BArVo getPrintInfo(BArVo searchCondition);

    /**
     * 删除
     */
    DeleteResultAo<Integer> delete(List<BArVo> searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<BArVo> cancel(BArVo searchCondition);

    /**
     * 中止收款
     */
    UpdateResultAo<Integer> suspendReceive(BArVo searchCondition);

    /**
     * 汇总查询
     */
    BArVo querySum(BArVo searchCondition);
}