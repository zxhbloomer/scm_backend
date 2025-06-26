package com.xinyirun.scm.core.system.serviceimpl.master.goods;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecPropEntity;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecPropVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.master.goods.MGoodsSpecPropMapper;
import com.xinyirun.scm.core.system.service.master.goods.IMGoodsSpecPropService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class MGoodsSpecPropServiceImpl extends BaseServiceImpl<MGoodsSpecPropMapper, MGoodsSpecPropEntity> implements IMGoodsSpecPropService {

    @Autowired
    private MGoodsSpecPropMapper mapper;

    /**
     * 启用的属性列表下拉框
     *
     * @param searchCondition 参数
     * @return List<MGoodsSpecPropVo>
     */
    @Override
    public List<MGoodsSpecPropVo> selectList(MGoodsSpecPropVo searchCondition) {
        List<MGoodsSpecPropEntity> entityList = mapper.selectList(new LambdaQueryWrapper<MGoodsSpecPropEntity>()
                .eq(MGoodsSpecPropEntity::getEnable, true));
        return (List<MGoodsSpecPropVo>) BeanUtilsSupport.copyProperties(entityList, MGoodsSpecPropVo.class);
    }
}
