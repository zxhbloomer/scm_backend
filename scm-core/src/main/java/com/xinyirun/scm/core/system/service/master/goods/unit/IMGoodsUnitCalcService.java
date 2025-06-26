package com.xinyirun.scm.core.system.service.master.goods.unit;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.goods.unit.MGoodsUnitCalcEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitVo;
import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitCalcVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-01-16
 */
public interface IMGoodsUnitCalcService extends IService<MGoodsUnitCalcEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<MGoodsUnitCalcVo> selectPage(MGoodsUnitCalcVo searchCondition) ;

    /**
     * 获取列表，页面查询
     */
    List<MGoodsUnitCalcVo> selectList(MGoodsUnitCalcVo searchCondition) ;

    /**
     * 获取数据条数
     */
    Integer getCount(MGoodsUnitCalcVo searchCondition) ;

    /**
     * 查询一条数据
     * @param searchCondition
     * @return
     */
    MGoodsUnitCalcVo selectOne(MGoodsUnitCalcVo searchCondition) ;

    /**
     * 获取未被使用的单位列表
     */
    List<MUnitVo> selectUnusedUnitsList(MGoodsUnitCalcVo searchCondition) ;

    /**
     * 查询by id，返回结果
     */
    MGoodsUnitCalcVo selectById(int id);

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<MGoodsUnitCalcVo> insert(MGoodsUnitCalcVo vo);

    /**
     * 修改单位换算数据
     */
    UpdateResultAo<MGoodsUnitCalcVo> update(MGoodsUnitCalcVo vo);

    /**
     * 删除单位换算数据
     */
    DeleteResultAo<Integer> delete(MGoodsUnitCalcVo vo);

    /**
     * 根据id获取详情单条数据
     */
    MGoodsUnitCalcVo detail(MGoodsUnitCalcVo searchCondition) ;
}
