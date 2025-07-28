package com.xinyirun.scm.core.system.service.business.materialconvert;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.materialconvert.BMaterialConvertEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.*;
import com.xinyirun.scm.bean.system.vo.excel.materialconvert.BMaterialConvertExportVo;

import java.util.List;

/**
 * <p>
 * 库存调整 服务类
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
public interface IBMaterialConvertService extends IService<BMaterialConvertEntity> {

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<Integer> insert(BMaterialConvertVo vo);

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<Integer> newInsert(BMaterialConvertNewVo vo);

    /**
     * 修改数据
     * @param vo
     * @return
     */
    UpdateResultAo<Integer> update(BMaterialConvertVo vo);

    /**
     * 修改数据
     * @param vo
     * @return
     */
    UpdateResultAo<Integer> newUpdate(BMaterialConvertVo vo);

    /**
     * 获取列表，页面查询
     */
    IPage<BMaterialConvertVo> selectPage(BMaterialConvertVo searchCondition) ;

    /**
     * 获取列表
     */
    List<BMaterialConvertVo> selectList(BMaterialConvertVo searchCondition) ;

    /**
     * 获取列表
     */
    List<BMaterialConvertVo> selectList1(BMaterialConvertVo searchCondition) ;

    /**
     * 查询单条数据
     */
    BMaterialConvertVo get(BMaterialConvertVo vo) ;

    /**
     * 查询单条数据
     */
    BMaterialConvertVo getByConvertId(BMaterialConvertVo vo) ;

    /**
     * 查询by id，返回结果
     */
    BMaterialConvertVo selectById(int id);

    /**
     * 启用停用
     * @param list
     * @return
     */
    void enabled(List<BMaterialConvertVo> list);

    /**
     * 批量提交
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> submit(List<BMaterialConvertDetailVo> searchCondition);

    /**
     * 批量审核
     * @param searchCondition
     * @return
     */
   Boolean audit(List<BMaterialConvertDetailVo> searchCondition);

    /**
     * 批量作废
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> cancel(List<BMaterialConvertDetailVo> searchCondition);

    /**
     * 批量驳回
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> reject(List<BMaterialConvertDetailVo> searchCondition);

    /**
     * 物料转换商品价格查询
     * @param searchCondition 查询参数
     * @return IPage<BMaterialConvertPriceVo>
     */
    IPage<BMaterialConvertPriceVo> selectConvertPricePage(BMaterialConvertPriceVo searchCondition);

    /**
     * 物料转换商品价格 导出
     * @param searchCondition 查询参数
     * @return List<BMaterialConvertPriceExportVo>
     */
    List<BMaterialConvertPriceExportVo> exportList(BMaterialConvertPriceVo searchCondition);

    /**
     * 物料转换商品价格 求和
     * @param searchCondition 查询参数
     * @return BMaterialConvertPriceVo
     */
    BMaterialConvertPriceVo selectConvertPriceSum(BMaterialConvertPriceVo searchCondition);

    /**
     * 获取列表，页面查询, 主表有更改
     */
    IPage<BMaterialConvert1Vo> selectPage1(BMaterialConvertVo searchCondition);

    /**
     * 获取详情
     * @param vo 入参, 主表ID
     * @return BMaterialConvertVo
     */
    BMaterialConvertVo getDetail(BMaterialConvertVo vo);

    BMaterialConvertVo get1(BMaterialConvertVo vo);

    void enabled1(List<BMaterialConvertVo> list);

    /**
     * 以 主表作为 ID， 一对一
     */
    IPage<BMaterialConvertNewVo> selectPageNew(BMaterialConvertNewVo searchCondition);

    /**
     * 以 主表作为 ID， 一对一
     */
    IPage<BMaterialConvert1Vo> selectPage2(BMaterialConvertVo searchCondition);

    /**
     * 物料转换, 部分导出
     * @param searchCondition
     * @return
     */
    List<BMaterialConvertExportVo> selectExportList(List<BMaterialConvertNewVo> searchCondition);

    /**
     * 物料转换, 全部导出
     * @param searchCondition
     * @return
     */
    List<BMaterialConvertExportVo> selectExportAll(BMaterialConvertNewVo searchCondition);
}
