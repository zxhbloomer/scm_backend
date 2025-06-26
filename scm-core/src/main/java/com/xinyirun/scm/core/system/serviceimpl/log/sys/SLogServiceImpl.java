package com.xinyirun.scm.core.system.serviceimpl.log.sys;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.log.sys.SLogSysEntity;
import com.xinyirun.scm.bean.system.vo.sys.log.SLogSysVo;
import com.xinyirun.scm.core.system.mapper.log.sys.SLogMapper;
import com.xinyirun.scm.core.system.service.log.sys.ISLogService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
public class SLogServiceImpl extends BaseServiceImpl<SLogMapper, SLogSysEntity> implements ISLogService {

    @Autowired
    private SLogMapper mapper;

    @Override
    public IPage<SLogSysVo> selectPage(SLogSysVo searchCondition) {
        /**
         * 分页条件
         * 最后的参数false：不自动调用count
         */
        Page<SLogSysEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize(), false);
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询入库计划page
        IPage<SLogSysVo> list = mapper.selectPage(pageCondition, searchCondition);

        // 动态计算最大的limit
        searchCondition.getPageCondition().setLimit_count((int) (searchCondition.getPageCondition().getSize() * 10));
        // 根据动态计算的最大limit，计算count
        Integer count = mapper.getLimitCount(searchCondition) ;
        // 计算pages，加上之当前页前的pages
        if(count > searchCondition.getPageCondition().getSize()) {
            list.setTotal(count + searchCondition.getPageCondition().getSize()*searchCondition.getPageCondition().getCurrent());
        } else {
            list.setTotal( searchCondition.getPageCondition().getSize()*searchCondition.getPageCondition().getCurrent());
        }
        return list;
    }


    /**
     * 异步保存
     * @param entity
     */
    @Async("logExecutor")
    @Override
    public void asyncSave(SLogSysEntity entity) {
        super.save(entity);
    }
}
