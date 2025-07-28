package com.xinyirun.scm.core.system.serviceimpl.sys.schedule.v6;

import com.xinyirun.scm.bean.entity.business.order.BOrderInvertedEntity;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderInvertedVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.ip.IpUtils;
import com.xinyirun.scm.core.system.mapper.sys.schedule.v6.SBOrderInvertedMapper;
import com.xinyirun.scm.core.system.service.sys.schedule.v6.ISBDOrderInvertedV5Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
public class SBDOrderInvertedV5ServiceImpl extends BaseServiceImpl<SBOrderInvertedMapper, BOrderInvertedEntity> implements ISBDOrderInvertedV5Service {

    @Autowired
    private SBOrderInvertedMapper mapper;


    /**
     * 稻谷出库计划倒排表（定时任务入参必须有,可以为空）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
//    @Scheduled(cron = "0/30 * * * * ?")
    public void queryOrderInverted(String parameterClass , String parameter) {

        log.error("稻谷出库计划倒排表:"+IpUtils.getHostIp());

        try {
            List<BOrderInvertedVo> bOrderInvertedVos = mapper.queryInvertedOrderOutPlan();

            // 查询是否已经存在快照 有则删除
            List<BOrderInvertedEntity> bOrderInvertedEntities = mapper.selectSnapshot();
            bOrderInvertedEntities.forEach(i -> {
                mapper.deleteById(i);
            });

            // 插入快照
            bOrderInvertedVos.forEach(i -> {
                BOrderInvertedEntity bOrderInvertedEntity = (BOrderInvertedEntity) BeanUtilsSupport.copyProperties(i, BOrderInvertedEntity.class);
                mapper.insert(bOrderInvertedEntity);
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
