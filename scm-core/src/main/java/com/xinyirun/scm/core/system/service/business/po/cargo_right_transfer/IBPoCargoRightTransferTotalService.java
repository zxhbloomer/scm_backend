package com.xinyirun.scm.core.system.service.business.po.cargo_right_transfer;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.po.cargo_right_transfer.BPoCargoRightTransferTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BPoCargoRightTransferTotalVo;

/**
 * 货权转移汇总表 服务类接口
 *
 * @author system
 * @since 2025-01-19
 */
public interface IBPoCargoRightTransferTotalService extends IService<BPoCargoRightTransferTotalEntity> {

    /**
     * 根据货权转移主表ID查询汇总数据
     *
     * @param cargoRightTransferId 货权转移主表ID
     * @return 汇总数据
     */
    BPoCargoRightTransferTotalVo selectByCargoRightTransferId(Integer cargoRightTransferId);

    /**
     * 刷新汇总数据
     *
     * @param cargoRightTransferId 货权转移主表ID
     * @return 刷新结果
     */
    boolean refreshTotal(Integer cargoRightTransferId);

    /**
     * 删除汇总数据
     *
     * @param cargoRightTransferId 货权转移主表ID
     * @return 删除结果
     */
    boolean deleteByCargoRightTransferId(Integer cargoRightTransferId);
}