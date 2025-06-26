package com.xinyirun.scm.core.system.service.sys.pages;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.pages.SPagesEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesExportVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;

import java.util.List;

/**
 * <p>
 * 页面表 服务类
 * </p>
 *
 * @author zxh
 * @since 2020-06-05
 */
public interface ISPagesService extends IService<SPagesEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<SPagesVo> selectPage(SPagesVo searchCondition) ;

    /**
     * 根据code获取明细
     */
    SPagesVo get(SPagesVo searchCondition);

    /**
     * 获取所有数据
     */
    List<SPagesVo> select(SPagesVo searchCondition) ;


    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    SPagesVo selectByid(Long id);


    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(SPagesEntity entity);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    UpdateResultAo<Integer> update(SPagesEntity entity);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    UpdateResultAo<Integer> update(SPagesVo entity);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param vo 实体对象
     * @return
     */
    void updateImportProcessingFalse(SPagesVo vo);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param vo 实体对象
     * @return
     */
    void updateImportProcessingTrue(SPagesVo vo);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param vo 实体对象
     * @return
     */
    void updateExportProcessingFalse(SPagesVo vo);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param vo 实体对象
     * @return
     */
    void updateExportProcessingTrue(SPagesVo vo);


    /**
     * 批量物理删除
     * @param searchCondition
     * @return
     */
    DeleteResultAo<Integer> realDeleteByIdsIn(List<SPagesVo> searchCondition);

    /**
     * 更新 日生产报表执行状态
     * @param pagesVo
     * @param status 0执行结束, 1进行中
     */
    void updateProductDailyProcessing(SPagesVo pagesVo, String status);

    /**
     * 导出查询
     * @param searchCondition
     * @return
     */
    List<SPagesExportVo> selectExportList(SPagesVo searchCondition);
}
