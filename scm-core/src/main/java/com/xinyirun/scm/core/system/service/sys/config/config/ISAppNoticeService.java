package com.xinyirun.scm.core.system.service.sys.config.config;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppNoticeEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppNoticeVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-24
 */
public interface ISAppNoticeService extends IService<SAppNoticeEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<SAppNoticeVo> selectPage(SAppNoticeVo searchCondition) ;

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(SAppNoticeVo vo);

    /**
     * 修改一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    UpdateResultAo<Integer> update(SAppNoticeVo vo);

}
