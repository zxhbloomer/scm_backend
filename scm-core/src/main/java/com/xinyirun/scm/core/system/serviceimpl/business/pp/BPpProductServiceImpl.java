package com.xinyirun.scm.core.system.serviceimpl.business.pp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.pp.BPpProductEntity;
import com.xinyirun.scm.bean.entity.busniess.wo.BWoProductEntity;
import com.xinyirun.scm.bean.system.bo.inventory.warehouse.MBLWBo;
import com.xinyirun.scm.bean.system.vo.business.pp.BPpProductVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoProductVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.core.system.mapper.business.pp.BPpProductMapper;
import com.xinyirun.scm.core.system.service.business.pp.IBPpProductService;
import com.xinyirun.scm.core.system.service.master.warehouse.IMWarehouseService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BPpProductAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BWoAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BWoRouterProductAutoCodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 生产计划_产成品、副产品 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-18
 */
@Service
public class BPpProductServiceImpl extends ServiceImpl<BPpProductMapper, BPpProductEntity> implements IBPpProductService {

    @Autowired
    private IMWarehouseService warehouseService;

    @Autowired
    private BPpProductAutoCodeServiceImpl autoCodeService;

    @Autowired
    private  BPpProductMapper bPpProductMapper;

    /**
     *   新增生产计划管理产成品, 副产品
     */
    @Override
    public void insertAll(List<BPpProductVo> productList, Integer pId) {
        List<BPpProductEntity> list = new ArrayList<>();
        for (BPpProductVo bWoProductVo : productList) {
            BPpProductEntity bPpProductEntity = new BPpProductEntity();
            List<MBLWBo> mblwBos = warehouseService.selectBLWByCode(bWoProductVo.getWarehouse_code());
            if (CollectionUtils.isEmpty(mblwBos)) {
                throw new BusinessException("仓库数据异常");
            }
            MBLWBo mblwBo = mblwBos.get(0);
            bPpProductEntity.setCode(autoCodeService.autoCode().getCode());
            bPpProductEntity.setType(bWoProductVo.getType());
            bPpProductEntity.setPp_id(pId);
            bPpProductEntity.setSku_id(bWoProductVo.getSku_id());
            bPpProductEntity.setSku_code(bWoProductVo.getSku_code());
            bPpProductEntity.setPp_router(bWoProductVo.getPp_router());
            bPpProductEntity.setQty(bWoProductVo.getQty());
            bPpProductEntity.setWarehouse_id(mblwBo.getWarehouse_id());
            bPpProductEntity.setWarehouse_code(mblwBo.getWarehouse_code());
            bPpProductEntity.setLocation_id(mblwBo.getLocation_id());
            bPpProductEntity.setLocation_code(mblwBo.getLocation_code());
            bPpProductEntity.setBin_id(mblwBo.getBin_id());
            bPpProductEntity.setBin_code(mblwBo.getBin_code());
            bPpProductEntity.setUnit_id(bWoProductVo.getUnit_id());
            bPpProductEntity.setUnit_name(bWoProductVo.getUnit_name());

            list.add(bPpProductEntity);
        }
        boolean b = saveBatch(list);
        if (!b) {
            throw new InsertErrorException("新增处理失败");
        }
    }

    @Override
    public List<BPpProductVo> selectByWoId(Integer id) {
        return bPpProductMapper.selectByWoId(id);
    }

    /**
     * 删除生产计划表关联信息
     *
     * @param id
     */
    @Override
    public void deleteByPpId(Integer id) {
        bPpProductMapper.deleteByPpId(id);
    }
}
