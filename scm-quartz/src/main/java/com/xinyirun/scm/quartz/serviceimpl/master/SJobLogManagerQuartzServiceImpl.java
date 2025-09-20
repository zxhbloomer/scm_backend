package com.xinyirun.scm.quartz.serviceimpl.master;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.tenant.manager.quartz.SJobLogManagerEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.bo.tenant.manager.quartz.SJobLogManagerBo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.quartz.mapper.master.SJobLogManagerQuartzMapper;
import com.xinyirun.scm.quartz.service.master.ISJobLogManagerQuartzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 定时任务日志 服务实现类
 * @DS 手动指定使用什么数据库
 * </p>
 */
@Slf4j
@Service
@DS("master")
public class SJobLogManagerQuartzServiceImpl extends ServiceImpl<SJobLogManagerQuartzMapper, SJobLogManagerEntity> implements ISJobLogManagerQuartzService {

    @Autowired
    private SJobLogManagerQuartzMapper mapper;

    /**
     * 保存定时任务日志
     *
     * @param bo 定时任务日志实体
     * @return 插入结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<SJobLogManagerBo> insert(SJobLogManagerBo bo) {
        SJobLogManagerEntity entity = new SJobLogManagerEntity();
        BeanUtils.copyProperties(bo, entity);
        mapper.insert(entity);
        bo.setId(entity.getId());
        return InsertResultUtil.OK(bo);
    }
}
