package com.xinyirun.scm.core.system.serviceimpl.business.rtwo;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.rtwo.BRtWoRouterProductEntity;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoRouterProductVo;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.rtwo.BRtWoRouterProductMapper;
import com.xinyirun.scm.core.system.service.business.rtwo.IBRtWoRouterProductService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BRtWoRouterProductAutoCodeServiceImpl;
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
public class BRtWoRouterProductServiceImpl extends ServiceImpl<BRtWoRouterProductMapper, BRtWoRouterProductEntity> implements IBRtWoRouterProductService {

    @Autowired
    private BRtWoRouterProductAutoCodeServiceImpl autoCodeService;

    /**
     * 根据 router_id 查询
     *
     * @param router_id router_id
     * @return List<BWoRouterProductVo>
     */
    @Override
    public List<BRtWoRouterProductVo> selectByRouterId(Integer router_id) {
        return baseMapper.selectByRouterId(router_id);
    }

    /**
     * 新增 产成品,副产品
     *
     * @param product_list 产品
     */
    @Override
    public void insertAll(List<BRtWoRouterProductVo> product_list, Integer router_id, String routerType) {
        List<BRtWoRouterProductEntity> productList = BeanUtilsSupport.copyProperties(product_list, BRtWoRouterProductEntity.class);
        productList.forEach(item -> {
            item.setRouter_id(router_id);
            item.setCode(autoCodeService.autoCode().getCode());
            item.setType(routerType);
        });
        boolean b = this.saveBatch(productList);
        if (!b) {
            throw new InsertErrorException("保存失败");
        }
    }
}
