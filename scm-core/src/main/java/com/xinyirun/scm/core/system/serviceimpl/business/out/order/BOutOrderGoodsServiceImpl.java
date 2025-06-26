package com.xinyirun.scm.core.system.serviceimpl.business.out.order;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.out.BOutOrderGoodsEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.out.BOutOrderGoodsVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.out.order.BOutOrderGoodsMapper;
import com.xinyirun.scm.core.system.service.business.out.order.IBOutOrderGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
@Service
public class BOutOrderGoodsServiceImpl extends ServiceImpl<BOutOrderGoodsMapper, BOutOrderGoodsEntity> implements IBOutOrderGoodsService {
    @Autowired
    private BOutOrderGoodsMapper mapper;

    @Override
    @Transactional
    public InsertResultAo<Integer> insert(BOutOrderGoodsVo vo) {
        BOutOrderGoodsEntity entity = new BOutOrderGoodsEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        int rtn = mapper.insert(entity);

        return InsertResultUtil.OK(rtn);
    }

    @Override
    @Transactional
    public UpdateResultAo<Integer> update(BOutOrderGoodsVo vo) {
        BOutOrderGoodsEntity entity = new BOutOrderGoodsEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        int rtn = mapper.updateById(entity);

        return UpdateResultUtil.OK(rtn);
    }

    @Override
    public List<BOutOrderGoodsVo> list(BOutOrderGoodsVo searchCondition) {
        return mapper.selectList(searchCondition);
    }
}
