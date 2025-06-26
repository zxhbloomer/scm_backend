package com.xinyirun.scm.core.system.service.master.vehicle;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.vehicle.MVehicleEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.master.vehicle.MVehicleVo;

import java.util.List;

public interface MVehicleService extends IService<MVehicleEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<MVehicleVo> selectPage(MVehicleVo searchCondition);

    /**
     * 获取承运商
     */
    MVehicleVo getDetail(MVehicleVo searchCondition);

    /**
     * 删除或恢复
     */
    void enable(List<MVehicleVo> searchCondition);

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<Integer> insert(MVehicleVo vo);

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    UpdateResultAo<Integer> update(MVehicleVo vo);

    /**
     * 查询by id，返回结果
     */
    MVehicleVo selectById(int id);

    /**
     * 删除
     */
    void delete(MVehicleVo searchCondition) ;

}
