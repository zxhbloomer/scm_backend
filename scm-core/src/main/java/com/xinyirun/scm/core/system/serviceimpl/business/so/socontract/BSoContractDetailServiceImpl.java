package com.xinyirun.scm.core.system.serviceimpl.business.so.socontract;

import com.xinyirun.scm.core.system.service.business.so.socontract.IBSoContractDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 销售合同明细Service业务层处理
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BSoContractDetailServiceImpl implements IBSoContractDetailService {

}