package com.xinyirun.scm.core.system.service.business.po.aprefund;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.po.aprefund.BApReFundEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundVo;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCancelCommonCallBackService;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCommonCallBackService;

import java.util.List;

/**
 * <p>
 * 应付退款管理表（Accounts Payable） 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
public interface IBApReFundService extends IService<BApReFundEntity>,
        IBpmCommonCallBackService<BApReFundVo>,
        IBpmCancelCommonCallBackService<BApReFundVo> {

    /**
     * 获取业务类型
     */
    List<BApReFundVo> getType();

    /**
     * 新增
     */
    InsertResultAo<BApReFundVo> startInsert(BApReFundVo vo);

    /**
     * 更新
     */
    UpdateResultAo<BApReFundVo> startUpdate(BApReFundVo vo);

    /**
     * 分页查询
     */
    IPage<BApReFundVo> selectPage(BApReFundVo vo);

    /**
     * 根据id查询
     */
    BApReFundVo selectById(Integer id);

    /**
     * 校验
     */
    CheckResultAo checkLogic(BApReFundVo vo, String checkType);


    /**
     * 导出查询
     */
    List<BApReFundVo> selectExportList(BApReFundVo param);

    /**
     * 获取报表系统参数，并组装打印参数
     */
    BApReFundVo getPrintInfo(BApReFundVo vo);

    /**
     * 删除
     */
    DeleteResultAo<Integer> delete(List<BApReFundVo> vo);

    /**
     * 作废
     */
    UpdateResultAo<BApReFundVo> cancel(BApReFundVo searchCondition);

    /**
     * 获取下推预付退款款数据
     */
    BApReFundVo getApRefund(BApReFundVo searchCondition);

    /**
     * 汇总查询
     */
    BApReFundVo querySum(BApReFundVo searchCondition);
}
