package com.xinyirun.scm.core.system.serviceimpl.business.fund;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.fund.BFundMonitorEntity;
import com.xinyirun.scm.bean.system.vo.business.fund.BFundMonitorVo;
import com.xinyirun.scm.core.system.mapper.business.fund.BFundMonitorMapper;
import com.xinyirun.scm.core.system.service.business.fund.IBFundMonitorService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 资金流水监控表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-03-02
 */
@Service
public class BFundMonitorServiceImpl extends BaseServiceImpl<BFundMonitorMapper, BFundMonitorEntity> implements IBFundMonitorService {

    @Autowired
    private BFundMonitorMapper mapper;

    /**
     * 查询资金流水监控
     *
     * @param searchCondition
     */
    @Override
    public IPage<BFundMonitorVo> selectPage(BFundMonitorVo searchCondition) {
        Page<BFundMonitorVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
//        return mapper.selectPage(pageCondition, searchCondition);
        return null;
    }
}
