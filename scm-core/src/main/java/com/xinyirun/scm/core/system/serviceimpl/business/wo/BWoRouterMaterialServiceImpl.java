package com.xinyirun.scm.core.system.serviceimpl.business.wo;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.wo.BWoRouterMaterialEntity;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoRouterMaterialVo;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.wo.BWoRouterMaterialMapper;
import com.xinyirun.scm.core.system.service.business.wo.IBWoRouterMaterialService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BWoRouterMaterialAutoCodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  生产配方_原材料 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
@Service
public class BWoRouterMaterialServiceImpl extends ServiceImpl<BWoRouterMaterialMapper, BWoRouterMaterialEntity> implements IBWoRouterMaterialService {

    @Autowired
    private BWoRouterMaterialAutoCodeServiceImpl autoCodeService;
    /**
     * 根据 router_id 查询
     *
     * @param id router_id
     * @return List<BWoRouterProductVo>
     */
    @Override
    public List<BWoRouterMaterialVo> selectByRouterId(Integer id) {
        return baseMapper.selectByRouterId(id);
    }

    /**
     * 新增原材料
     *
     * @param material_list 原材料
     * @param router_id router_id
     */
    @Override
    public void insertAll(List<BWoRouterMaterialVo> material_list, Integer router_id) {
        List<BWoRouterMaterialEntity> materialEntities = BeanUtilsSupport.copyProperties(material_list, BWoRouterMaterialEntity.class);
        materialEntities.forEach(item -> {
            item.setRouter_id(router_id);
            item.setCode(autoCodeService.autoCode().getCode());
        });
        boolean b = this.saveBatch(materialEntities);
        if (!b) {
            throw new InsertErrorException("保存失败");
        }
    }
}
