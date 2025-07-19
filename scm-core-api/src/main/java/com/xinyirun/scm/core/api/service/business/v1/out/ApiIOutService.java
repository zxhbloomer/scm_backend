package com.xinyirun.scm.core.api.service.business.v1.out;

import com.xinyirun.scm.bean.api.vo.business.out.ApiOutPlanVo;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutPlanEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutPlanListVo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutPlanVo;
import com.xinyirun.scm.core.api.service.base.v1.ApiIBaseService;

import java.util.List;

/**
 * <p>
 * 出库计划 服务类
 * </p>
 *
 * @author htt
 * @since 2021-11-12
 */
public interface ApiIOutService extends ApiIBaseService<BOutPlanEntity> {

    /**
     * 返回中台数据
     * @param vo
    Out
    List<ApiOutPlanResultBo> getSyncOutResultAppCode10(ApiOutPlanIdCodeVo vo);

    /**
     * 同步新增出库计划
     */
    InsertResultAo<BOutPlanVo> save(ApiOutPlanVo vo);

    /**
     * 通知出库计划过期
     */
    void expires(String code);


    /**
     * 中止出库计划
     */
    List<BOutPlanListVo> discontinue(String code);

    /**
     * 完成出库计划
     */
    void finish(String code);

    /**
     * 判断放货指令是否能作废
     */
    void cancelable(String code);

    /**
     * 作废出库计划
     */
    List<BOutPlanListVo> cancel(String code);
}
