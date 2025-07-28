package com.xinyirun.scm.core.system.service.business.cancel;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.cancel.BCancelEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.vo.business.cancel.BCancelVo;

/**
 * <p>
 * 作废单 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-01-23
 */
public interface IBCancelService extends IService<BCancelEntity> {

    /**
     * 插入数据
     */
    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(BCancelVo vo);


}
