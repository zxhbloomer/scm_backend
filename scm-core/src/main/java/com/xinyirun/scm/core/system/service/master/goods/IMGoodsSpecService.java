package com.xinyirun.scm.core.system.service.master.goods;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecEntity;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecExportVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecLeftVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IMGoodsSpecService extends IService<MGoodsSpecEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<MGoodsSpecVo> selectPage(MGoodsSpecVo searchCondition) ;

    /**
     * 获取树状列表，页面查询
     */
    List<MGoodsSpecLeftVo> selectLeft(MGoodsSpecLeftVo searchCondition) ;

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<Integer> insert(MGoodsSpecVo vo);

    /**
     * 查询by id，返回结果
     */
    MGoodsSpecVo selectById(int id);

    /**
     * 修改数据
     */
    UpdateResultAo<Integer> update(MGoodsSpecVo vo);

    /**
     * 批量删除
     */
    DeleteResultAo<Integer> deleteByIdsIn(List<MGoodsSpecVo> searchCondition);

    /**
     * 批量启用
     */
    void enabledByIdsIn(List<MGoodsSpecVo> searchCondition);

    /**
     * 批量禁用
     */
    void disSabledByIdsIn(List<MGoodsSpecVo> searchCondition);

    /**
     * 通过name查询
     *
     */
    List<MGoodsSpecEntity> selectByName(String name);

    /**
     * 通过sku_code查询
     *
     */
    MGoodsSpecVo selectByCode(String code);


    /**
     * 批量启用/停用
     */
    void enableByIdsIn(List<MGoodsSpecVo> searchCondition);

    /**
     * 导出
     * @param searchConditionList 入参
     * @return List<MGoodsSpecExportVo>
     */
    List<MGoodsSpecExportVo> export(MGoodsSpecVo searchConditionList);

    /**
     * 查询物料转换 商品
     * @param searchCondition
     * @return
     */
    IPage<MGoodsSpecVo> getConvertGoodsList(MGoodsSpecVo searchCondition);

    /**
     * 根据 物料id查询规格
     * @param searchCondition
     * @return
     */
    List<MGoodsSpecVo> selectListByGoodsId(MGoodsSpecVo searchCondition);

}
