package com.xinyirun.scm.core.system.serviceimpl.business.so.socontract;

import com.xinyirun.scm.core.system.service.business.so.socontract.IBSoContractTotalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 销售合同汇总Service业务层处理
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BSoContractTotalServiceImpl implements IBSoContractTotalService {

}