package com.xinyirun.scm.core.system.service.business.po.cargo_right_transfer;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.po.cargo_right_transfer.BPoCargoRightTransferDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BPoCargoRightTransferDetailVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 货权转移明细表 服务类接口
 *
 * @author system
 * @since 2025-01-19
 */
public interface IBPoCargoRightTransferDetailService extends IService<BPoCargoRightTransferDetailEntity> {

    /**
     * 根据货权转移主表ID查询明细列表
     *
     * @param cargoRightTransferId 货权转移主表ID
     * @return 明细列表
     */
    List<BPoCargoRightTransferDetailVo> selectByCargoRightTransferId(Integer cargoRightTransferId);

    /**
     * 保存明细数据
     *
     * @param cargoRightTransferId 货权转移主表ID
     * @param detailList           明细列表
     * @return 保存结果
     */
    boolean saveDetails(Integer cargoRightTransferId, List<BPoCargoRightTransferDetailVo> detailList);

    /**
     * 根据SKU ID统计转移数量
     *
     * @param skuId SKU ID
     * @return 转移数量
     */
    BigDecimal getTransferredQtyBySkuId(Integer skuId);

    /**
     * 根据商品ID查询货权转移明细
     *
     * @param goodsId 商品ID
     * @return 明细列表
     */
    List<BPoCargoRightTransferDetailVo> selectByGoodsId(Integer goodsId);

}