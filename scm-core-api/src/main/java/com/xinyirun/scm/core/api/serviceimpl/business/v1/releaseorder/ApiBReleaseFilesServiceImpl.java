package com.xinyirun.scm.core.api.serviceimpl.business.v1.releaseorder;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BReleaseFilesEntity;
import com.xinyirun.scm.core.api.mapper.business.borroworder.ApiBBorrowFilesMapper;
import com.xinyirun.scm.core.api.mapper.business.releaseorder.ApiBReleaseFilesMapper;
import com.xinyirun.scm.core.api.service.business.v1.borroworder.ApiIBBorrowFilesService;
import com.xinyirun.scm.core.api.service.business.v1.releaseorder.ApiIBReleaseFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 放货指令/借货指令附件表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-17
 */
@Service
public class ApiBReleaseFilesServiceImpl extends ServiceImpl<ApiBReleaseFilesMapper, BReleaseFilesEntity> implements ApiIBReleaseFilesService {

    @Autowired
    private ApiBReleaseFilesMapper mapper;

    /**
     * 按release_order_code删除数据
     */
    @Override
    public void deleteByReleaseOrderCode(String releaseOrderCode) {
        mapper.delete(new QueryWrapper<BReleaseFilesEntity>().eq("release_order_code", releaseOrderCode));
    }
}
