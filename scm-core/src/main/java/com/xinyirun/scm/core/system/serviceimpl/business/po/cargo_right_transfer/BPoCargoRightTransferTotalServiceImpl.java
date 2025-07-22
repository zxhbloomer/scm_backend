package com.xinyirun.scm.core.system.serviceimpl.business.po.cargo_right_transfer;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.po.cargo_right_transfer.BPoCargoRightTransferTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BPoCargoRightTransferTotalVo;
import com.xinyirun.scm.core.system.mapper.business.po.cargo_right_transfer.BPoCargoRightTransferTotalMapper;
import com.xinyirun.scm.core.system.service.business.po.cargo_right_transfer.IBPoCargoRightTransferTotalService;
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
public class BPoCargoRightTransferTotalServiceImpl extends ServiceImpl<BPoCargoRightTransferTotalMapper, BPoCargoRightTransferTotalEntity>
        implements IBPoCargoRightTransferTotalService {

}