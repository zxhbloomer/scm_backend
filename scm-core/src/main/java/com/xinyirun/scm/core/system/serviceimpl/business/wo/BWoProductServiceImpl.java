package com.xinyirun.scm.core.system.serviceimpl.business.wo;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.wo.BWoProductEntity;
import com.xinyirun.scm.bean.system.bo.inventory.warehouse.MBLWBo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoProductVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.core.system.mapper.business.wo.BWoProductMapper;
import com.xinyirun.scm.core.system.service.business.wo.IBWoProductService;
import com.xinyirun.scm.core.system.service.master.warehouse.IMWarehouseService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BWoRouterProductAutoCodeServiceImpl;
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
 *  生产管理_产成品、副产品服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
@Service
public class BWoProductServiceImpl extends ServiceImpl<BWoProductMapper, BWoProductEntity> implements IBWoProductService {

    @Autowired
    private IMWarehouseService warehouseService;

    @Autowired
    private BWoRouterProductAutoCodeServiceImpl autoCodeService;

    /**
     * 校验 产成品, 副产品配比是否满足 100 %
     *
     * @param product_list 产成品, 副产品 集合
     * @param result       错误信息集合, map键值 error_msg
     */
    @Override
    public void checkProductRouter(List<BWoProductVo> product_list, List<Map<String, String>> result) {
        BigDecimal router = product_list.stream().map(BWoProductVo::getWo_router).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (router.compareTo(new BigDecimal(100)) != 0) {
            Map<String, String> msg = new HashMap<>();
            msg.put("error_msg", "产成品、副产品配比之和不是100%");
            result.add(msg);
        }
    }

    /**
     * 新增产成品, 副产品
     *
     * @param product_list 产成品, 副产品集合
     * @param wo_id 主表id
     */
    @Override
    public void insertAll(List<BWoProductVo> product_list, Integer wo_id) {
        List<BWoProductEntity> list = new ArrayList<>();
        for (BWoProductVo bWoProductVo : product_list) {
            BWoProductEntity entity = new BWoProductEntity();
            List<MBLWBo> mblwBos = warehouseService.selectBLWByCode(bWoProductVo.getWarehouse_code());
            if (CollectionUtils.isEmpty(mblwBos)) {
                throw new BusinessException("仓库数据异常");
            }
            MBLWBo mblwBo = mblwBos.get(0);
            entity.setCode(autoCodeService.autoCode().getCode());
            entity.setType(bWoProductVo.getType());
            entity.setWo_id(wo_id);
            entity.setSku_id(bWoProductVo.getSku_id());
            entity.setSku_code(bWoProductVo.getSku_code());
            entity.setWo_router(bWoProductVo.getWo_router());
            entity.setWo_qty(bWoProductVo.getWo_qty());
            entity.setWarehouse_id(mblwBo.getWarehouse_id());
            entity.setWarehouse_code(mblwBo.getWarehouse_code());
            entity.setLocation_id(mblwBo.getLocation_id());
            entity.setLocation_code(mblwBo.getLocation_code());
            entity.setBin_id(mblwBo.getBin_id());
            entity.setBin_code(mblwBo.getBin_code());
            entity.setUnit_id(bWoProductVo.getUnit_id());
            entity.setUnit_name(bWoProductVo.getUnit_name());
//            entity.setGoods_id(bWoProductVo.getGoods_id());
//            entity.setGoods_code(bWoProductVo.getGoods_code());
            list.add(entity);
        }
        boolean b = saveBatch(list);
        if (!b) {
            throw new InsertErrorException("新增处理失败");
        }

    }

    /**
     * 查询产成品, 副产品
     *
     * @param wo_id wo_id

     * @return List<BWoProductVo>
     */
    @Override
    public List<BWoProductVo> selectByWoId(Integer wo_id) {
        return baseMapper.selectByWoId(wo_id);
    }

    /**
     * 查询 当前 wo_id 的产成品已生产数量
     *
     * @param wo_id
     * @param delivery_order_detail_id 订单详情 ID
     * @return
     */
    @Override
    public BigDecimal selectHasProductNum(Integer wo_id, Integer delivery_order_detail_id) {
//        if (wo_id == null) {
//            return BigDecimal.ZERO;
//        }
        return baseMapper.selectHasProductNum(wo_id, delivery_order_detail_id);
    }
}
