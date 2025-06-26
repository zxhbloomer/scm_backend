package com.xinyirun.scm.core.system.service.log.sys;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.log.sys.SLogAppEntity;
import com.xinyirun.scm.bean.system.vo.sys.log.SLogAppVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
public interface ISLogAppService extends IService<SLogAppEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<SLogAppVo> selectPage(SLogAppVo searchCondition) ;

}
