package com.xinyirun.scm.core.system.serviceimpl.business.wo;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.wo.BWoRouterProductEntity;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoRouterProductVo;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.wo.BWoRouterProductMapper;
import com.xinyirun.scm.core.system.service.business.wo.IBWoRouterProductService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BWoRouterProductAutoCodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  生产配方_产成品、副产品服务类 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
@Service
public class BWoRouterProductServiceImpl extends ServiceImpl<BWoRouterProductMapper, BWoRouterProductEntity> implements IBWoRouterProductService {

    @Autowired
    private BWoRouterProductAutoCodeServiceImpl autoCodeService;

    /**
     * 根据 router_id 查询
     *
     * @param router_id router_id
     * @return List<BWoRouterProductVo>
     */
    @Override
    public List<BWoRouterProductVo> selectByRouterId(Integer router_id) {
        return baseMapper.selectByRouterId(router_id);
    }

    /**
     * 新增 产成品,副产品
     *
     * @param product_list 产品
     */
    @Override
    public void insertAll(List<BWoRouterProductVo> product_list, Integer router_id) {
        List<BWoRouterProductEntity> productList = BeanUtilsSupport.copyProperties(product_list, BWoRouterProductEntity.class);
        productList.forEach(item -> {
            item.setRouter_id(router_id);
            item.setCode(autoCodeService.autoCode().getCode());
        });
        boolean b = this.saveBatch(productList);
        if (!b) {
            throw new InsertErrorException("保存失败");
        }
    }
}
