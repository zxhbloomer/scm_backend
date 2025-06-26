package com.xinyirun.scm.core.app.serviceimpl.master.warehouse;

import com.xinyirun.scm.bean.entity.master.warehouse.MLocationEntity;
import com.xinyirun.scm.core.app.mapper.master.warehouse.AppMLocationMapper;
import com.xinyirun.scm.core.app.service.master.warehouse.AppIMLocationService;
import com.xinyirun.scm.core.app.serviceimpl.base.v1.AppBaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class AppMLocationServiceImpl extends AppBaseServiceImpl<AppMLocationMapper, MLocationEntity> implements AppIMLocationService {

}
