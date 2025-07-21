package com.xinyirun.scm.core.system.serviceimpl.business.po.cargo_right_transfer;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.po.cargo_right_transfer.BCargoRightTransferTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BCargoRightTransferTotalVo;
import com.xinyirun.scm.core.system.mapper.business.po.cargo_right_transfer.BCargoRightTransferTotalMapper;
import com.xinyirun.scm.core.system.service.business.po.cargo_right_transfer.IBCargoRightTransferTotalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 货权转移汇总表 服务实现类
 *
 * @author system
 * @since 2025-01-19
 */
@Slf4j
@Service
public class BCargoRightTransferTotalServiceImpl extends ServiceImpl<BCargoRightTransferTotalMapper, BCargoRightTransferTotalEntity>
        implements IBCargoRightTransferTotalService {

    @Autowired
    private BCargoRightTransferTotalMapper totalMapper;

    @Override
    public BCargoRightTransferTotalVo selectByCargoRightTransferId(Integer cargoRightTransferId) {
        return totalMapper.selectByCargoRightTransferId(cargoRightTransferId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refreshTotal(Integer cargoRightTransferId) {
        try {
            totalMapper.refreshTotal(cargoRightTransferId);
            return true;
        } catch (Exception e) {
            log.error("刷新汇总数据失败", e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByCargoRightTransferId(Integer cargoRightTransferId) {
        try {
            totalMapper.deleteByCargoRightTransferId(cargoRightTransferId);
            return true;
        } catch (Exception e) {
            log.error("删除汇总数据失败", e);
            return false;
        }
    }
}