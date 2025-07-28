package com.xinyirun.scm.core.system.service.business.so.arrefund;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.so.arrefund.BArReFundEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.so.arrefund.BArReFundVo;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCancelCommonCallBackService;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCommonCallBackService;

import java.util.List;

/**
 * <p>
 * 应收退款管理表（Accounts Receivable） 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
public interface IBArReFundService extends IService<BArReFundEntity>,
        IBpmCommonCallBackService<BArReFundVo>,
        IBpmCancelCommonCallBackService<BArReFundVo> {

    /**
     * 获取业务类型
     */
    List<BArReFundVo> getType();

    /**
     * 新增
     */
    InsertResultAo<BArReFundVo> startInsert(BArReFundVo vo);

    /**
     * 更新
     */
    UpdateResultAo<BArReFundVo> startUpdate(BArReFundVo vo);

    /**
     * 分页查询
     */
    IPage<BArReFundVo> selectPage(BArReFundVo vo);

    /**
     * 根据id查询
     */
    BArReFundVo selectById(Integer id);

    /**
     * 校验
     */
    CheckResultAo checkLogic(BArReFundVo vo, String checkType);


    /**
     * 导出查询
     */
    List<BArReFundVo> selectExportList(BArReFundVo param);

    /**
     * 获取报表系统参数，并组装打印参数
     */
    BArReFundVo getPrintInfo(BArReFundVo vo);

    /**
     * 删除
     */
    DeleteResultAo<Integer> delete(List<BArReFundVo> vo);

    /**
     * 作废
     */
    UpdateResultAo<BArReFundVo> cancel(BArReFundVo searchCondition);

    /**
     * 获取下推预收退款款数据
     */
    BArReFundVo getArRefund(BArReFundVo searchCondition);

    /**
     * 汇总查询
     */
    BArReFundVo querySum(BArReFundVo searchCondition);
}