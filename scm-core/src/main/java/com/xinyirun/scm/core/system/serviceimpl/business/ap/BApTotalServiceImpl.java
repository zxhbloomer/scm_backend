package com.xinyirun.scm.core.system.serviceimpl.business.ap;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.ap.BApEntity;
import com.xinyirun.scm.bean.entity.busniess.ap.BApTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.ap.BApTotalVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.ap.BApTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.ap.BApMapper;
import com.xinyirun.scm.core.system.service.business.ap.IBApTotalService;
import com.xinyirun.scm.core.system.service.business.pocontract.IBPoContractTotalService;
import com.xinyirun.scm.core.system.service.business.poorder.IBPoOrderTotalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * 应付账款管理表-财务数据汇总 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-11
 */
@Service
public class BApTotalServiceImpl extends ServiceImpl<BApTotalMapper, BApTotalEntity> implements IBApTotalService {

} 