package com.xinyirun.scm.core.api.serviceimpl.master.v1.warehouse;

import com.xinyirun.scm.bean.entity.master.warehouse.MWarehouseEntity;
import com.xinyirun.scm.bean.api.vo.master.warehouse.ApiWarehouseVo;
import com.xinyirun.scm.core.api.mapper.master.warehouse.ApiWarehouseMapper;
import com.xinyirun.scm.core.api.service.master.v1.warehouse.ApiWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;

import java.util.List;

@Service
public class ApiWarehouseServiceImpl extends BaseServiceImpl<ApiWarehouseMapper, MWarehouseEntity> implements ApiWarehouseService {

    @Autowired
    private ApiWarehouseMapper mapper;

    /**
     * 仓库下拉
     */
    @Override
    public List<ApiWarehouseVo> getWarehouse(ApiWarehouseVo vo) {
        return mapper.getWarehouse(vo);
    }
}
