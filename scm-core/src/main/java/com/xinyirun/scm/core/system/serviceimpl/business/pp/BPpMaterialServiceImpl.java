package com.xinyirun.scm.core.system.serviceimpl.business.pp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.pp.BPpMaterialEntity;
import com.xinyirun.scm.bean.entity.busniess.wo.BWoMaterialEntity;
import com.xinyirun.scm.bean.system.bo.inventory.warehouse.MBLWBo;
import com.xinyirun.scm.bean.system.vo.business.pp.BPpMaterialVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoMaterialVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.core.system.mapper.business.pp.BPpMaterialMapper;
import com.xinyirun.scm.core.system.service.business.pp.IBPpMaterialService;
import com.xinyirun.scm.core.system.service.master.warehouse.IMWarehouseService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BPpMaterialAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BWoMaterialAutoCodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 生产计划_原材料 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-18
 */
@Service
public class BPpMaterialServiceImpl extends ServiceImpl<BPpMaterialMapper, BPpMaterialEntity> implements IBPpMaterialService {

    @Autowired
    private IMWarehouseService warehouseService;


    @Autowired
    private BPpMaterialMapper bppMaterialMapper;


    @Autowired
    private BPpMaterialAutoCodeServiceImpl autoCodeService;

    /**
     * 新增原材料
     *
     * @param materialList
     * @param pId
     */
    @Override
    public void insertAll(List<BPpMaterialVo> materialList, Integer pId) {
        List<BPpMaterialEntity> list = new ArrayList<>();
        for (BPpMaterialVo bWoMaterialVo : materialList) {
            BPpMaterialEntity entity = new BPpMaterialEntity();
            List<MBLWBo> mblwBos = warehouseService.selectBLWByCode(bWoMaterialVo.getWarehouse_code());
            if (CollectionUtils.isEmpty(mblwBos)) {
                throw new BusinessException("仓库数据异常");
            }
            MBLWBo mblwBo = mblwBos.get(0);
            entity.setCode(autoCodeService.autoCode().getCode());
            entity.setPp_id(pId);
            entity.setSku_id(bWoMaterialVo.getSku_id());
            entity.setSku_code(bWoMaterialVo.getSku_code());
            entity.setPp_router(bWoMaterialVo.getPp_router());
            entity.setQty(bWoMaterialVo.getQty());
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
     * 查询生产计划_原材料
     *
     * @param id
     */
    @Override
    public List<BPpMaterialVo> selectByWoId(Integer id) {
        return bppMaterialMapper.selectByWoId(id);
    }

    /**
     * 删除生产计划表关联信息
     *
     * @param id
     */
    @Override
    public void deleteByPpId(Integer id) {
        bppMaterialMapper.deleteByPpId(id);
    }
}
