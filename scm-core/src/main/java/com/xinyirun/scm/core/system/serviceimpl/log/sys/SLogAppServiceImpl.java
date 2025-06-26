package com.xinyirun.scm.core.system.serviceimpl.log.sys;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.log.sys.SLogAppEntity;
import com.xinyirun.scm.bean.system.vo.sys.log.SLogAppVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.mapper.log.sys.SLogAppMapper;
import com.xinyirun.scm.core.system.service.log.sys.ISLogAppService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
@Service
public class SLogAppServiceImpl extends BaseServiceImpl<SLogAppMapper, SLogAppEntity> implements ISLogAppService {

    @Autowired
    private SLogAppMapper mapper;

    @Override
    public IPage<SLogAppVo> selectPage(SLogAppVo searchCondition) {
        // 分页条件
        Page<SLogAppEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize(), false);
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        IPage<SLogAppVo> logAppVoIPage = mapper.selectPage(pageCondition, searchCondition);

        // 动态计算最大的limit
        searchCondition.getPageCondition().setLimit_count((int) (searchCondition.getPageCondition().getSize() * 10));
        // 根据动态计算的最大limit，计算count
        Integer count = mapper.getLimitCount(searchCondition) ;
        // 计算pages，加上之当前页前的pages
        if(count > searchCondition.getPageCondition().getSize()) {
            logAppVoIPage.setTotal(count + searchCondition.getPageCondition().getSize()*searchCondition.getPageCondition().getCurrent());
        } else {
            logAppVoIPage.setTotal( searchCondition.getPageCondition().getSize()*searchCondition.getPageCondition().getCurrent());
        }
        return logAppVoIPage;
    }
}
