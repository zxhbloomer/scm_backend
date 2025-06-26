package com.xinyirun.scm.core.system.service.log.sys;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.log.sys.SLogApiEntity;
import com.xinyirun.scm.bean.system.vo.sys.log.SLogApiVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
public interface ISLogApiService extends IService<SLogApiEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<SLogApiVo> selectPage(SLogApiVo searchCondition) ;

    /**
     * 异步保存
     * @param entity
     */
    void asyncSave(SLogApiEntity entity);
}
