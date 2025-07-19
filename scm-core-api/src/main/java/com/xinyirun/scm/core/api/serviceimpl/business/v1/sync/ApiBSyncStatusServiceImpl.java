package com.xinyirun.scm.core.api.serviceimpl.business.v1.sync;

import com.xinyirun.scm.bean.entity.busniess.sync.BSyncStatusEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.business.sync.BSyncStatusVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.api.mapper.sync.ApiBSyncStatusMapper;
import com.xinyirun.scm.core.api.service.business.v1.sync.ApiIBSyncStatusService;
import com.xinyirun.scm.core.api.serviceimpl.base.v1.ApiBaseServiceImpl;
import com.xinyirun.scm.core.system.mapper.business.wms.inplan.BInPlanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 业务数据同步状态服务类
 * </p>
 *
 * @author wwl
 * @since 2022-01-20
 */
@Service
public class ApiBSyncStatusServiceImpl extends ApiBaseServiceImpl<ApiBSyncStatusMapper, BSyncStatusEntity> implements ApiIBSyncStatusService {

    @Autowired
    private ApiBSyncStatusMapper mapper;

    @Autowired
    private BInPlanMapper bInPlanMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> save(BSyncStatusVo vo) {

        // 插入逻辑保存
        BSyncStatusEntity entity = (BSyncStatusEntity) BeanUtilsSupport.copyProperties(vo, BSyncStatusEntity.class);

//        Integer serial_id = mapper.isExists(vo.getSerial_id(), vo.getSerial_type());
        int rtn;
//        if(serial_id == null) {
        rtn = mapper.insert(entity);
        vo.setId(entity.getId());
//        } else {
//            rtn = mapper.updateById(entity);
//        }
        return InsertResultUtil.OK(rtn);
    }
}
