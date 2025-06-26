package com.xinyirun.scm.core.system.serviceimpl.business.rtwo;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.rtwo.BRtWoRouterMaterialEntity;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoRouterMaterialVo;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.rtwo.BRtWoRouterMaterialMapper;
import com.xinyirun.scm.core.system.service.business.rtwo.IBRtWoRouterMaterialService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BRtWoRouterMaterialAutoCodeServiceImpl;
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
public class BRtWoRouterMaterialServiceImpl extends ServiceImpl<BRtWoRouterMaterialMapper, BRtWoRouterMaterialEntity> implements IBRtWoRouterMaterialService {

    @Autowired
    private BRtWoRouterMaterialAutoCodeServiceImpl autoCodeService;
    /**
     * 根据 router_id 查询
     *
     * @param id router_id
     * @return List<BWoRouterProductVo>
     */
    @Override
    public List<BRtWoRouterMaterialVo> selectByRouterId(Integer id) {
        return baseMapper.selectByRouterId(id);
    }

    /**
     * 新增原材料
     *
     * @param material_list 原材料
     * @param router_id router_id
     */
    @Override
    public void insertAll(List<BRtWoRouterMaterialVo> material_list, Integer router_id) {
        List<BRtWoRouterMaterialEntity> materialEntities = BeanUtilsSupport.copyProperties(material_list, BRtWoRouterMaterialEntity.class);
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
