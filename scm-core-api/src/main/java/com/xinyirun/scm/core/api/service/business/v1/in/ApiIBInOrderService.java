package com.xinyirun.scm.core.api.service.business.v1.in;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.business.in.ApiBInOrderVo;
import com.xinyirun.scm.bean.entity.busniess.in.order.BInOrderEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;

import java.util.List;

public interface ApiIBInOrderService extends IService<BInOrderEntity> {


    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<Integer> insert(ApiBInOrderVo vo);

    /**
     * 修改一条记录（选择字段，策略插入）
     */
    UpdateResultAo<Integer> update(ApiBInOrderVo vo);

    /**
     * 同步数据
     */
    void sync(List<ApiBInOrderVo> list);


}
