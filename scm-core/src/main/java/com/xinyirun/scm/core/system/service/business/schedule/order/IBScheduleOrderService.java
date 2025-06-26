package com.xinyirun.scm.core.system.service.business.schedule.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.schedule.BScheduleOrderEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.schedule.BScheduleOrderVo;

/**
 * <p>
 *  调度服务类
 * </p>
 *
 * @author wwl
 * @since 2022-01-10
 */
public interface IBScheduleOrderService extends IService<BScheduleOrderEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<BScheduleOrderVo> selectPage(BScheduleOrderVo searchCondition) ;

    /**
     * 查询by id，返回结果
     *
     */
    BScheduleOrderVo selectById(int id);

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<Integer> insert(BScheduleOrderVo vo);

    /**
     * 修改数据
     */
    UpdateResultAo<Integer> update(BScheduleOrderVo vo);

    /**
     * 删除数据
     */
    DeleteResultAo<Integer> delete(BScheduleOrderVo vo);
}
