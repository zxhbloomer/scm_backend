package com.xinyirun.scm.core.system.service.business.wo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.wo.BWoRouterProductEntity;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoRouterProductVo;

import java.util.List;

/**
 * <p>
 *  生产配方_产成品、副产品服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
public interface IBWoRouterProductService extends IService<BWoRouterProductEntity> {

    /**
     * 根据 router_id 查询
     * @param router_id  router_id
     * @return List<BWoRouterProductVo>
     */
    List<BWoRouterProductVo> selectByRouterId(Integer router_id);

    /**
     * 新增 产成品,副产品
     * @param product_list 产品
     * @param router_id router_id
     */
    void insertAll(List<BWoRouterProductVo> product_list, Integer router_id);
}
