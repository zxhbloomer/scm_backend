package com.xinyirun.scm.core.api.serviceimpl.business.v1.sync;

import com.xinyirun.scm.bean.api.vo.business.sync.ApiBSyncStatusErrorVo;
import com.xinyirun.scm.bean.entity.busniess.sync.BSyncStatusErrorEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.api.mapper.sync.ApiBSyncStatusErrorMapper;
import com.xinyirun.scm.core.api.service.business.v1.sync.ApiIBSyncStatusErrorService;
import com.xinyirun.scm.core.api.serviceimpl.base.v1.ApiBaseServiceImpl;
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
public class ApiBSyncStatusErrorServiceImpl extends ApiBaseServiceImpl<ApiBSyncStatusErrorMapper, BSyncStatusErrorEntity> implements ApiIBSyncStatusErrorService {

    @Autowired
    private ApiBSyncStatusErrorMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> save(ApiBSyncStatusErrorVo vo) {

//        int rtn;

        BSyncStatusErrorEntity entity = mapper.selectDetail(vo.getSerial_id(), vo.getSerial_type());
//        BSyncStatusErrorEntity entity = mapper.selectDetail(vo.getSerial_detail_id(), vo.getSerial_type());
        if (entity != null) {
            Integer id = entity.getId();
            entity = (BSyncStatusErrorEntity) BeanUtilsSupport.copyProperties(vo, BSyncStatusErrorEntity.class);
            entity.setId(id);
            mapper.updateById(entity);
        } else {
            entity = new BSyncStatusErrorEntity();
            BeanUtilsSupport.copyProperties(vo, entity, new String[]{"id"});
            mapper.insert(entity);
        }


        return InsertResultUtil.OK(entity.getId());
    }
}
