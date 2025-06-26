package com.xinyirun.scm.core.system.service.log.sys;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.log.sys.SLogSysEntity;
import com.xinyirun.scm.bean.system.vo.sys.log.SLogSysVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
public interface ISLogService extends IService<SLogSysEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<SLogSysVo> selectPage(SLogSysVo searchCondition) ;

    /**
     * 异步保存
     * @param entity
     */
    void asyncSave(SLogSysEntity entity);
}
