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
     * 启用规格并返回更新后的数据
     * @param specVo 规格对象
     * @return 更新后的规格数据
     */
    MGoodsSpecVo enabledById(MGoodsSpecVo specVo);

    /**
     * 停用规格并返回更新后的数据
     * @param specVo 规格对象
     * @return 更新后的规格数据
     */
    MGoodsSpecVo disabledById(MGoodsSpecVo specVo);

    /**
     * 逻辑删除规格
     * @param searchCondition 规格对象
     */
    void delete(MGoodsSpecVo searchCondition);

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
     * 导出
     * @param searchConditionList 入参
     * @return List<MGoodsSpecExportVo>
     */
    List<MGoodsSpecExportVo> export(MGoodsSpecVo searchConditionList);

    /**
     * 查询导出列表数据（支持动态排序）- 完全按照仓库管理模式实现
     * @param searchCondition 搜索条件
     * @return 导出VO列表
     */
    List<MGoodsSpecExportVo> selectExportList(MGoodsSpecVo searchCondition);

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
