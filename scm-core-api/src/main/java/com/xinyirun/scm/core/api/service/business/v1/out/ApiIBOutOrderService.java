package com.xinyirun.scm.core.api.service.business.v1.out;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.business.out.ApiBOutOrderVo;
import com.xinyirun.scm.bean.entity.busniess.out.BOutOrderEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;

import java.util.List;

public interface ApiIBOutOrderService extends IService<BOutOrderEntity> {


    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(ApiBOutOrderVo vo);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    UpdateResultAo<Integer> update(ApiBOutOrderVo vo);

    /**
     * 同步数据
     */
    void sync(List<ApiBOutOrderVo> list);


}
