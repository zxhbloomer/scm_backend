package com.xinyirun.scm.core.system.service.business.so.cargo_right_transfer;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.so.cargo_right_transfer.BSoCargoRightTransferDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.so.cargo_right_transfer.BSoCargoRightTransferDetailVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 销售货权转移明细表 服务类接口
 *
 * @author system
 * @since 2025-07-27
 */
public interface IBSoCargoRightTransferDetailService extends IService<BSoCargoRightTransferDetailEntity> {

    /**
     * 根据货权转移主表ID查询明细列表
     *
     * @param cargoRightTransferId 货权转移主表ID
     * @return 明细列表
     */
    List<BSoCargoRightTransferDetailVo> selectByCargoRightTransferId(Integer cargoRightTransferId);

    /**
     * 保存明细数据
     *
     * @param cargoRightTransferId 货权转移主表ID
     * @param detailList           明细列表
     * @return 保存结果
     */
    boolean saveDetails(Integer cargoRightTransferId, List<BSoCargoRightTransferDetailVo> detailList);

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
    List<BSoCargoRightTransferDetailVo> selectByGoodsId(Integer goodsId);

}