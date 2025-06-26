package com.xinyirun.scm.core.system.serviceimpl.log.sys;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.sync.BSyncStatusEntity;
import com.xinyirun.scm.bean.entity.log.sys.SLogSysEntity;
import com.xinyirun.scm.bean.system.vo.business.sync.BSyncStatusVo;
import com.xinyirun.scm.core.system.mapper.log.sys.SLogSyncStatusMapper;
import com.xinyirun.scm.core.system.service.log.sys.ISLogSyncStatusService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @date 2022/10/21 11:01
 */
@Service
public class ISLogSyncStatusServiceImpl extends BaseServiceImpl<SLogSyncStatusMapper, BSyncStatusEntity> implements ISLogSyncStatusService {

    @Autowired
    private SLogSyncStatusMapper mapper;

    /**
     * 根据条件查询同步日志
     *
     * @param searchCondition 查询条件
     * @return Page<BSyncStatusVo>
     */
    @Override
    public IPage<BSyncStatusVo> selectPage(BSyncStatusVo searchCondition) {
        // 分页条件
        Page<SLogSysEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        IPage<BSyncStatusVo> syncStatusVoIPage = mapper.selectPage(searchCondition, pageCondition);
        // 动态计算最大的limit
       /* searchCondition.getPageCondition().setLimit_count((int) (searchCondition.getPageCondition().getSize() * 10));
        // 根据动态计算的最大limit，计算count
        Integer count = mapper.getLimitCount(searchCondition) ;
        // 计算pages，加上之当前页前的pages
        if(count > searchCondition.getPageCondition().getSize()) {
            syncStatusVoIPage.setTotal(count + searchCondition.getPageCondition().getSize()*searchCondition.getPageCondition().getCurrent());
        } else {
            syncStatusVoIPage.setTotal( searchCondition.getPageCondition().getSize()*searchCondition.getPageCondition().getCurrent());
        }*/
        return syncStatusVoIPage;
    }
}
