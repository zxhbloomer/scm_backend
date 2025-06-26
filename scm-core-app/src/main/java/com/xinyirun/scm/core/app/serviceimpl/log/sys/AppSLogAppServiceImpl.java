package com.xinyirun.scm.core.app.serviceimpl.log.sys;

import com.xinyirun.scm.bean.entity.log.sys.SLogAppEntity;
import com.xinyirun.scm.core.app.mapper.log.sys.AppSLogAppMapper;
import com.xinyirun.scm.core.app.service.log.sys.AppISLogAppService;
import com.xinyirun.scm.core.app.serviceimpl.base.v1.AppBaseServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
@Service
public class AppSLogAppServiceImpl extends AppBaseServiceImpl<AppSLogAppMapper, SLogAppEntity> implements AppISLogAppService {
    /**
     * 异步保存
     * @param entity
     */
    @Async("logExecutor")
    @Override
    public void asyncSave(SLogAppEntity entity) {
        super.save(entity);
    }
}
