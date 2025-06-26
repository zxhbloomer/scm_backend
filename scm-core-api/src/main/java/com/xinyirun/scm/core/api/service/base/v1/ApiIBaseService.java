package com.xinyirun.scm.core.api.service.base.v1;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.bo.steel.ApiInPlanResultBo;
import com.xinyirun.scm.bean.api.bo.steel.ApiOutPlanResultBo;
import com.xinyirun.scm.bean.api.vo.sync.ApiDeliveryPlanIdCodeVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiInPlanIdCodeVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiOutPlanIdCodeVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiReceivePlanIdCodeVo;

import java.util.List;

/**
 * 扩展Mybatis-Plus接口
 *
 * @author
 */
public interface ApiIBaseService<T> extends IService<T> {

    /**
     * 返回中台数据
     * @param vo
     */
    List<ApiInPlanResultBo> getSyncInResultAppCode10(ApiInPlanIdCodeVo vo);

    /**
     * 返回中台数据
     * @param vo
     */
    List<ApiOutPlanResultBo> getSyncOutResultAppCode10(ApiOutPlanIdCodeVo vo);


    /**
     * 返回中台数据
     * @param vo
     */
    List<ApiInPlanResultBo> getSyncDeliveryResultAppCode10(ApiDeliveryPlanIdCodeVo vo);

    /**
     * 返回中台数据
     * @param vo
     */
    List<ApiOutPlanResultBo> getSyncReceiveResultAppCode10(ApiReceivePlanIdCodeVo vo);

}