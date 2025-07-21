package com.xinyirun.scm.core.system.serviceimpl.business.po.cargo_right_transfer;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.po.cargo_right_transfer.BPoCargoRightTransferDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BPoCargoRightTransferDetailVo;
import com.xinyirun.scm.core.system.mapper.business.po.cargo_right_transfer.BPoCargoRightTransferDetailMapper;
import com.xinyirun.scm.core.system.service.business.po.cargo_right_transfer.IBPoCargoRightTransferDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 货权转移明细表 服务实现类
 *
 * @author system
 * @since 2025-01-19
 */
@Slf4j
@Service
public class BPoCargoRightTransferDetailServiceImpl extends ServiceImpl<BPoCargoRightTransferDetailMapper, BPoCargoRightTransferDetailEntity>
        implements IBPoCargoRightTransferDetailService {

    @Autowired
    private BPoCargoRightTransferDetailMapper detailMapper;

    @Override
    public List<BPoCargoRightTransferDetailVo> selectByCargoRightTransferId(Integer cargoRightTransferId) {
        return detailMapper.selectByCargoRightTransferId(cargoRightTransferId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDetails(Integer cargoRightTransferId, List<BPoCargoRightTransferDetailVo> detailList) {
        try {
            // 删除原有明细
            detailMapper.deleteByCargoRightTransferId(cargoRightTransferId);

            if (detailList != null && !detailList.isEmpty()) {
                // 转换并批量插入
                List<BPoCargoRightTransferDetailEntity> entities = detailList.stream().map(vo -> {
                    BPoCargoRightTransferDetailEntity entity = new BPoCargoRightTransferDetailEntity();
                    BeanUtils.copyProperties(vo, entity);
                    entity.setCargo_right_transfer_id(cargoRightTransferId);
                    return entity;
                }).collect(Collectors.toList());

                detailMapper.batchInsert(entities);
            }

            return true;
        } catch (Exception e) {
            log.error("保存明细数据失败", e);
            return false;
        }
    }

    @Override
    public BigDecimal getTransferredQtyByPoOrderDetailId(Integer poOrderDetailId) {
        return detailMapper.sumTransferredQtyByPoOrderDetailId(poOrderDetailId);
    }

    @Override
    public BigDecimal getTransferredQtyBySkuId(Integer skuId) {
        return detailMapper.sumTransferredQtyBySkuId(skuId);
    }

    @Override
    public List<BPoCargoRightTransferDetailVo> selectByGoodsId(Integer goodsId) {
        return detailMapper.selectByGoodsId(goodsId);
    }
}