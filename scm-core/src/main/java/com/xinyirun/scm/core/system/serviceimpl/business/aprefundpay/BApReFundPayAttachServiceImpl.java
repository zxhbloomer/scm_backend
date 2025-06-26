package com.xinyirun.scm.core.system.serviceimpl.business.aprefundpay;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.appay.BApPayAttachEntity;
import com.xinyirun.scm.bean.entity.busniess.aprefundpay.BApReFundPayAttachEntity;
import com.xinyirun.scm.core.system.mapper.business.appay.BApPayAttachMapper;
import com.xinyirun.scm.core.system.mapper.business.aprefundpay.BApReFundPayAttachMapper;
import com.xinyirun.scm.core.system.service.business.appay.IBApPayAttachService;
import com.xinyirun.scm.core.system.service.business.aprefundpay.IBApReFundPayAttachService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 退款单附件表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Service
public class BApReFundPayAttachServiceImpl extends ServiceImpl<BApReFundPayAttachMapper, BApReFundPayAttachEntity> implements IBApReFundPayAttachService {

}
