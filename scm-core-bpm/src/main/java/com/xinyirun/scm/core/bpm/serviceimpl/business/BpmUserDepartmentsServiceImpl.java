package com.xinyirun.scm.core.bpm.serviceimpl.business;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.BpmUserDepartmentsEntity;
import com.xinyirun.scm.core.bpm.mapper.business.BpmUserDepartmentsMapper;
import com.xinyirun.scm.core.bpm.service.business.IBpmUserDepartmentsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户部门关系表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-08
 */
@Service
public class BpmUserDepartmentsServiceImpl extends ServiceImpl<BpmUserDepartmentsMapper, BpmUserDepartmentsEntity> implements IBpmUserDepartmentsService {

}
