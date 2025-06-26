package com.xinyirun.scm.core.api.service.business.v1.carriage;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.business.monitor.ApiCarriageOrderVo;
import com.xinyirun.scm.bean.entity.busniess.monitor.BCarriageOrderEntity;

import java.util.List;

public interface ApiICarriageOrderService extends IService<BCarriageOrderEntity> {


//    /**
//     * 插入一条记录（选择字段，策略插入）
//     */
//    InsertResultAo<Integer> insert(ApiCarriageOrderVo vo);
//
//    /**
//     * 修改一条记录（选择字段，策略插入）
//     */
//    UpdateResultAo<Integer> update(ApiCarriageOrderVo vo);

    /**
     * 同步数据
     */
    void sync(List<ApiCarriageOrderVo> list);

    /**
     * 校验承运订单是否能够作废
     */
    void check(ApiCarriageOrderVo vo);


}
