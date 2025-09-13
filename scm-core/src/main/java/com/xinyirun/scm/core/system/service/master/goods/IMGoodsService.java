package com.xinyirun.scm.core.system.service.master.goods;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsExportVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IMGoodsService extends IService<MGoodsEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<MGoodsVo> selectPage(MGoodsVo searchCondition) ;

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(MGoodsVo vo);

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    MGoodsVo selectById(int id);

    /**
     * 修改数据
     * @param vo
     * @return
     */
    UpdateResultAo<Integer> update(MGoodsVo vo);

    /**
     * 批量删除
     * @param searchCondition
     * @return
     */
    DeleteResultAo<Integer> deleteByIdsIn(List<MGoodsVo> searchCondition);

    /**
     * 删除物料（逻辑删除复原）
     * @param searchCondition 删除条件
     */
    void delete(MGoodsVo searchCondition);

    /**
     * 批量启用
     * @param searchCondition
     * @return
     */
    void enabledByIdsIn(List<MGoodsVo> searchCondition);

    /**
     * 批量禁用
     * @param searchCondition
     * @return
     */
    void disSabledByIdsIn(List<MGoodsVo> searchCondition);

    /**
     * 通过name查询
     *
     */
    List<MGoodsEntity> selectByName(String name);


    /**
     * 批量启用/停用
     */
    void enableByIdsIn(List<MGoodsVo> searchCondition);

    /**
     * 导出
     * @param searchConditionList 入参
     * @return List<MGoodsExportVo>
     */
    List<MGoodsExportVo> export(MGoodsVo searchConditionList);

}
