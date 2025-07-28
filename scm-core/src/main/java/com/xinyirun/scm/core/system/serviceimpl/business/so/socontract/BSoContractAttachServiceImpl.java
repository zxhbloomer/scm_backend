package com.xinyirun.scm.core.system.serviceimpl.business.so.socontract;

import com.xinyirun.scm.core.system.service.business.so.socontract.IBSoContractAttachService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 销售合同附件Service业务层处理
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BSoContractAttachServiceImpl implements IBSoContractAttachService {

}