package com.xinyirun.scm.core.system.service.log.operate;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.bo.log.operate.CustomOperateBo;
import com.xinyirun.scm.bean.entity.log.operate.SLogOperEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
public interface ISLogOperService extends IService<SLogOperEntity> {

    /**
     * 插入一条记录
     * @return
     */
    InsertResultAo<Boolean> save(CustomOperateBo cobo) ;
}
