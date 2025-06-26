package com.xinyirun.scm.core.api.serviceimpl.business.v1.in;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.api.vo.business.in.ApiBInOrderGoodsVo;
import com.xinyirun.scm.bean.entity.busniess.in.order.BInOrderGoodsEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.api.service.business.v1.in.ApiIBInOrderGoodsService;
import com.xinyirun.scm.core.system.mapper.wms.in.order.BInOrderGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
@Service
public class ApiBInOrderGoodsServiceImpl extends ServiceImpl<BInOrderGoodsMapper, BInOrderGoodsEntity> implements ApiIBInOrderGoodsService {

    @Autowired
    private BInOrderGoodsMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(ApiBInOrderGoodsVo vo) {
        BInOrderGoodsEntity entity = new BInOrderGoodsEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        int rtn = mapper.insert(entity);

        return InsertResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(ApiBInOrderGoodsVo vo) {
        BInOrderGoodsEntity entity = new BInOrderGoodsEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        int rtn = mapper.updateById(entity);

        return UpdateResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer order_id) {
        mapper.deleteByOrderId(order_id);
    }

}
