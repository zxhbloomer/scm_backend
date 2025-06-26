package com.xinyirun.scm.core.api.serviceimpl.business.v1.logistics;

import com.xinyirun.scm.bean.api.vo.business.logistics.LogisticsContractVo;
import com.xinyirun.scm.bean.entity.busniess.schedule.BScheduleEntity;
import com.xinyirun.scm.core.api.mapper.business.logistics.ApiBScheduleMapper;
import com.xinyirun.scm.core.api.service.business.v1.logistics.ApiIBLogisticsOrderService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 入库订单 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-11-02
 */
@Service
@Slf4j
public class ApiIBLogisticsOrderServiceImpl extends BaseServiceImpl<ApiBScheduleMapper, BScheduleEntity> implements ApiIBLogisticsOrderService {

    @Autowired
    private ApiBScheduleMapper mapper;

    @Override
    public LogisticsContractVo isContractRelevance(LogisticsContractVo vo) {
        Integer count = mapper.selectLogisticsOrderCount(vo);
        LogisticsContractVo result = new LogisticsContractVo();
        if (count == null || count == 0) {
            result.setIs_relevance(Boolean.FALSE);
            result.setCount(0);
        } else {
            result.setIs_relevance(Boolean.TRUE);
            result.setCount(count);
        }
        result.setContract_no(vo.getContract_no());
        return result;
    }
}
