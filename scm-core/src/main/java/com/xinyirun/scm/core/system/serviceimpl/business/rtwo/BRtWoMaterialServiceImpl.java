package com.xinyirun.scm.core.system.serviceimpl.business.rtwo;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.rtwo.BRtWoMaterialEntity;
import com.xinyirun.scm.bean.system.bo.inventory.warehouse.MBLWBo;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoMaterialVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.core.system.mapper.business.rtwo.BRtWoMaterialMapper;
import com.xinyirun.scm.core.system.service.business.rtwo.IBRtWoMaterialService;
import com.xinyirun.scm.core.system.service.master.warehouse.IMWarehouseService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BRtWoMaterialAutoCodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  生产管理_原材料服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
@Service
public class BRtWoMaterialServiceImpl extends ServiceImpl<BRtWoMaterialMapper, BRtWoMaterialEntity> implements IBRtWoMaterialService {

    @Autowired
    private IMWarehouseService warehouseService;

    @Autowired
    private BRtWoMaterialAutoCodeServiceImpl autoCodeService;
    /**
     * 检验 原材料配比是不是 100%
     *
     * @param material_list 原材料列表
     * @param result        返回错误信息, map键值 error_msg
     */
/*    @Override
    public void checkMaterialRouter(List<BRtWoMaterialVo> material_list, List<Map<String, String>> result) {
        BigDecimal router = material_list.stream().map(BRtWoMaterialVo::getWo_router).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (router.compareTo(new BigDecimal(100)) != 0) {
            Map<String, String> msg = new HashMap<>();
            msg.put("error_msg", "原材料配比之和不是 100%");
            result.add(msg);
        }
    }*/

    /**
     * 新增原材料
     *
     * @param material_list 原材料列表
     * @param wo_id         wo_id
     */
    @Override
    public void insertAll(List<BRtWoMaterialVo> material_list, Integer wo_id) {
        List<BRtWoMaterialEntity> list = new ArrayList<>();
        for (BRtWoMaterialVo bWoMaterialVo : material_list) {
            BRtWoMaterialEntity entity = new BRtWoMaterialEntity();
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
    public List<BRtWoMaterialVo> selectByWoId(Integer wo_id) {
        return baseMapper.selectByWoId(wo_id);
    }
}
