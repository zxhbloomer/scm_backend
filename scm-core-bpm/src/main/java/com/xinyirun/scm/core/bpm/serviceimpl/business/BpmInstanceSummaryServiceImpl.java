package com.xinyirun.scm.core.bpm.serviceimpl.business;

import com.xinyirun.scm.bean.bpm.vo.BpmInstanceSummaryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xinyirun.scm.core.bpm.service.business.IBpmInstanceSummaryService;
import com.xinyirun.scm.core.bpm.mapper.business.BpmInstanceSummaryMapper;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
/**
 * <p>
 * 审批流实例-摘要 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-20
 */
@Service
public class BpmInstanceSummaryServiceImpl extends ServiceImpl<BpmInstanceSummaryMapper, BpmInstanceSummaryEntity> implements IBpmInstanceSummaryService {

    @Autowired
    BpmInstanceSummaryMapper mapper;

    /**
     * 根据编号获取数据
     * @param instanceCode
     * @return
     */
    @Override
    public BpmInstanceSummaryVo getDataByInstanceCode(String instanceCode){
        return mapper.getDataByInstanceCode(instanceCode);
    }
}
