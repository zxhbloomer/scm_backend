package com.xinyirun.scm.core.system.serviceimpl.releaseorder;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.releaseorder.BReleaseFilesEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.business.releaseorder.BReleaseFilesVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.releaseorder.BReleaseFilesMapper;
import com.xinyirun.scm.core.system.service.business.releaseorder.IBReleaseFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 放货指令/借货指令附件表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-17
 */
@Service
public class BReleaseFilesServiceImpl extends ServiceImpl<BReleaseFilesMapper, BReleaseFilesEntity> implements IBReleaseFilesService {

    @Autowired
    private BReleaseFilesMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(BReleaseFilesVo vo) {
        BReleaseFilesEntity entity = new BReleaseFilesEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        int rtn = mapper.insert(entity);
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 获取放货指令附件
     *
     * @param id
     */
    @Override
    public List<BReleaseFilesVo> selectByReleaseOrderId(Integer id) {
        QueryWrapper<BReleaseFilesEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("release_order_id", id);
        List<BReleaseFilesEntity> selectList = mapper.selectList(queryWrapper);

        List<BReleaseFilesVo> bReleaseFilesVos = BeanUtilsSupport.copyProperties(selectList,BReleaseFilesVo.class);
        return bReleaseFilesVos;
    }
}
