package com.xinyirun.scm.core.app.serviceimpl.master.warehouse;

import com.xinyirun.scm.bean.entity.master.warehouse.MBinEntity;
import com.xinyirun.scm.core.app.mapper.master.warehouse.AppMBinMapper;
import com.xinyirun.scm.core.app.service.master.warehouse.AppIMBinService;
import com.xinyirun.scm.core.app.serviceimpl.base.v1.AppBaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 库位 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class AppMBinServiceImpl extends AppBaseServiceImpl<AppMBinMapper, MBinEntity> implements AppIMBinService {

}
