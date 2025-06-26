package com.xinyirun.scm.core.bpm.service.business;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.bpm.BpmCcEntity;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmCcVo;
import org.flowable.engine.delegate.DelegateExecution;

/**
 * <p>
 * 抄送 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-08
 */
public interface IBpmCcService extends IService<BpmCcEntity> {

    IPage<BBpmCcVo> selectPageList(BBpmCcVo param);

    /**
     * 更新节点抄送信息 更新抄送表信息
     */
    void insertCc(DelegateExecution execution);
}
