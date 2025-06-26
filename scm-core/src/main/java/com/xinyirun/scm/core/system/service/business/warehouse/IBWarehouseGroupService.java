package com.xinyirun.scm.core.system.service.business.warehouse;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.warehouse.BWarehouseGroupEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.warehouse.BWarehouseGroupVo;

import java.util.List;

/**
 * <p>
 * 仓库组一级分类 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-01-30
 */
public interface IBWarehouseGroupService extends IService<BWarehouseGroupEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<BWarehouseGroupVo> selectPage(BWarehouseGroupVo searchCondition);

    /**
     * 获取列表，页面查询
     */
    List<BWarehouseGroupVo> selectList(BWarehouseGroupVo searchCondition);

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<Integer> insert(BWarehouseGroupVo vo);

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    UpdateResultAo<Integer> update(BWarehouseGroupVo vo);

    /**
     * 删除一条记录（选择字段，策略更新）
     */
    void delete(BWarehouseGroupVo vo);

    /**
     * 查询by id，返回结果
     */
    BWarehouseGroupVo selectById(int id);

}
