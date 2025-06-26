package com.xinyirun.scm.core.system.service.business.wo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.wo.BWoRouterMaterialEntity;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoRouterMaterialVo;

import java.util.List;

/**
 * <p>
 *  生产配方_原材料服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
public interface IBWoRouterMaterialService extends IService<BWoRouterMaterialEntity> {

    /**
     * 根据 router_id 查询
     * @param id router_id
     * @return List<BWoRouterMaterialVo>
     */
    List<BWoRouterMaterialVo> selectByRouterId(Integer id);

    /**
     * 新增原材料
     * @param material_list
     * @param id
     */
    void insertAll(List<BWoRouterMaterialVo> material_list, Integer id);
}
