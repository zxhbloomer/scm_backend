package com.xinyirun.scm.core.whapp.service.bpm;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.bpm.BpmProcessTemplatesEntity;
import com.xinyirun.scm.bean.whapp.vo.business.bpm.WhAppBBpmProcessVo;
import com.xinyirun.scm.bean.whapp.vo.business.bpm.WhAppBpmProcessJson;

/**
 * <p>
 * process_templates 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-11
 */
public interface WhAppIBpmProcessTemplatesService extends IService<BpmProcessTemplatesEntity> {

    /**
     * 获取审批流程数据
     */
    WhAppBBpmProcessVo generateEngineFlow(WhAppBBpmProcessVo param);

    /**
     * 获取审批流程模型数据
     * @param param
     * @return
     */
    WhAppBpmProcessJson getProcessModel(WhAppBBpmProcessVo param);
}
