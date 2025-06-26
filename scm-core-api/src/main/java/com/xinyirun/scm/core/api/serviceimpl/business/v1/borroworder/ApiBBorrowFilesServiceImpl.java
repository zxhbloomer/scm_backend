package com.xinyirun.scm.core.api.serviceimpl.business.v1.borroworder;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BReleaseFilesEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.business.releaseorder.BReleaseFilesVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.api.mapper.business.borroworder.ApiBBorrowFilesMapper;
import com.xinyirun.scm.core.api.mapper.business.releaseorder.ApiBReleaseFilesMapper;
import com.xinyirun.scm.core.api.service.business.v1.borroworder.ApiIBBorrowFilesService;
import com.xinyirun.scm.core.system.mapper.business.releaseorder.BReleaseFilesMapper;
import com.xinyirun.scm.core.system.service.business.releaseorder.IBReleaseFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 放货指令/借货指令附件表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-17
 */
@Service
public class ApiBBorrowFilesServiceImpl extends ServiceImpl<ApiBBorrowFilesMapper, BReleaseFilesEntity> implements ApiIBBorrowFilesService {

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
