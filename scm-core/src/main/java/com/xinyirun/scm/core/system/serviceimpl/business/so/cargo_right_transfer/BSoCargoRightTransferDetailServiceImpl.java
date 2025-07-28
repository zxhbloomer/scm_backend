package com.xinyirun.scm.core.system.serviceimpl.business.so.cargo_right_transfer;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.so.cargo_right_transfer.BSoCargoRightTransferDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.so.cargo_right_transfer.BSoCargoRightTransferDetailVo;
import com.xinyirun.scm.core.system.mapper.business.so.cargo_right_transfer.BSoCargoRightTransferDetailMapper;
import com.xinyirun.scm.core.system.service.business.so.cargo_right_transfer.IBSoCargoRightTransferDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 销售货权转移明细表 服务实现类
 *
 * @author system
 * @since 2025-07-27
 */
@Slf4j
@Service
public class BSoCargoRightTransferDetailServiceImpl extends ServiceImpl<BSoCargoRightTransferDetailMapper, BSoCargoRightTransferDetailEntity>
        implements IBSoCargoRightTransferDetailService {

    @Autowired
    private BSoCargoRightTransferDetailMapper detailMapper;

    @Override
    public List<BSoCargoRightTransferDetailVo> selectByCargoRightTransferId(Integer cargoRightTransferId) {
        return detailMapper.selectByCargoRightTransferId(cargoRightTransferId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDetails(Integer cargoRightTransferId, List<BSoCargoRightTransferDetailVo> detailList) {
        try {
            // 删除原有明细
            detailMapper.deleteByCargoRightTransferId(cargoRightTransferId);

            if (detailList != null && !detailList.isEmpty()) {
                // 转换并批量插入
                List<BSoCargoRightTransferDetailEntity> entities = detailList.stream().map(vo -> {
                    BSoCargoRightTransferDetailEntity entity = new BSoCargoRightTransferDetailEntity();
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
    public BigDecimal getTransferredQtyBySkuId(Integer skuId) {
        return detailMapper.sumTransferredQtyBySkuId(skuId);
    }

    @Override
    public List<BSoCargoRightTransferDetailVo> selectByGoodsId(Integer goodsId) {
        return detailMapper.selectByGoodsId(goodsId);
    }
}