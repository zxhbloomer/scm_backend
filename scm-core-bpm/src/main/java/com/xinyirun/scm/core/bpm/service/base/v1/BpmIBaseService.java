package com.xinyirun.scm.core.bpm.service.base.v1;

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
public interface BpmIBaseService<T> extends IService<T> {

}