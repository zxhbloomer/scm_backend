package com.xinyirun.scm.core.system.serviceimpl.business.po.cargo_right_transfer;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.po.cargo_right_transfer.BCargoRightTransferAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BCargoRightTransferAttachVo;
import com.xinyirun.scm.core.system.mapper.business.po.cargo_right_transfer.BCargoRightTransferAttachMapper;
import com.xinyirun.scm.core.system.service.business.po.cargo_right_transfer.IBCargoRightTransferAttachService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 货权转移附件表 服务实现类
 *
 * @author system
 * @since 2025-01-19
 */
@Slf4j
@Service
public class BCargoRightTransferAttachServiceImpl extends ServiceImpl<BCargoRightTransferAttachMapper, BCargoRightTransferAttachEntity>  {


}