package com.xinyirun.scm.core.system.serviceimpl.business.wo;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.wo.BWoMaterialEntity;
import com.xinyirun.scm.bean.system.bo.inventory.warehouse.MBLWBo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoMaterialVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.core.system.mapper.business.wo.BWoMaterialMapper;
import com.xinyirun.scm.core.system.service.business.wo.IBWoMaterialService;
import com.xinyirun.scm.core.system.service.master.warehouse.IMWarehouseService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BWoMaterialAutoCodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  生产管理_原材料服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
@Service
public class BWoMaterialServiceImpl extends ServiceImpl<BWoMaterialMapper, BWoMaterialEntity> implements IBWoMaterialService {

    @Autowired
    private IMWarehouseService warehouseService;

    @Autowired
    private BWoMaterialAutoCodeServiceImpl autoCodeService;
    /**
     * 检验 原材料配比是不是 100%
     *
     * @param material_list 原材料列表
     * @param result        返回错误信息, map键值 error_msg
     */
    @Override
    public void checkMaterialRouter(List<BWoMaterialVo> material_list, List<Map<String, String>> result) {
        BigDecimal router = material_list.stream().map(BWoMaterialVo::getWo_router).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (router.compareTo(new BigDecimal(100)) != 0) {
            Map<String, String> msg = new HashMap<>();
            msg.put("error_msg", "原材料配比之和不是 100%");
            result.add(msg);
        }
    }

    /**
     * 新增原材料
     *
     * @param material_list 原材料列表
     * @param wo_id         wo_id
     */
    @Override
    public void insertAll(List<BWoMaterialVo> material_list, Integer wo_id) {
        List<BWoMaterialEntity> list = new ArrayList<>();
        for (BWoMaterialVo bWoMaterialVo : material_list) {
            BWoMaterialEntity entity = new BWoMaterialEntity();
            List<MBLWBo> mblwBos = warehouseService.selectBLWByCode(bWoMaterialVo.getWarehouse_code());
            if (CollectionUtils.isEmpty(mblwBos)) {
                throw new BusinessException("仓库数据异常");
            }
            MBLWBo mblwBo = mblwBos.get(0);
            entity.setCode(autoCodeService.autoCode().getCode());
            entity.setWo_id(wo_id);
            entity.setSku_id(bWoMaterialVo.getSku_id());
            entity.setSku_code(bWoMaterialVo.getSku_code());
            entity.setWo_router(bWoMaterialVo.getWo_router());
            entity.setWo_qty(bWoMaterialVo.getWo_qty());
            entity.setWarehouse_id(mblwBo.getWarehouse_id());
            entity.setWarehouse_code(mblwBo.getWarehouse_code());
            entity.setLocation_id(mblwBo.getLocation_id());
            entity.setLocation_code(mblwBo.getLocation_code());
            entity.setBin_id(mblwBo.getBin_id());
            entity.setBin_code(mblwBo.getBin_code());
            entity.setUnit_id(bWoMaterialVo.getUnit_id());
            entity.setUnit_name(bWoMaterialVo.getUnit_name());
//            entity.setGoods_id(bWoMaterialVo.getGoods_id());
//            entity.setGoods_code(bWoMaterialVo.getGoods_code());
            list.add(entity);
        }
        boolean b = saveBatch(list);
        if (!b) {
            throw new InsertErrorException("新增处理失败");
        }
    }

    /**
     * 根据 wo_id 查询详情
     *
     * @param wo_id wo_id
     * @return
     */
    @Override
    public List<BWoMaterialVo> selectByWoId(Integer wo_id) {
        return baseMapper.selectByWoId(wo_id);
    }
}
