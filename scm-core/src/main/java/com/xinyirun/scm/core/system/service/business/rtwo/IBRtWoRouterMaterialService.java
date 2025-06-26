package com.xinyirun.scm.core.system.service.business.rtwo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.rtwo.BRtWoRouterMaterialEntity;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoRouterMaterialVo;

import java.util.List;

/**
 * <p>
 *  生产配方_原材料服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
public interface IBRtWoRouterMaterialService extends IService<BRtWoRouterMaterialEntity> {

    /**
     * 根据 router_id 查询
     * @param id router_id
     * @return List<BWoRouterMaterialVo>
     */
    List<BRtWoRouterMaterialVo> selectByRouterId(Integer id);

    /**
     * 新增原材料
     * @param material_list
     * @param id
     */
    void insertAll(List<BRtWoRouterMaterialVo> material_list, Integer id);
}
