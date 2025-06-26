package com.xinyirun.scm.core.system.service.master.goods;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.goods.MBusinessTypeEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.master.goods.MBusinessTypeExportVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MBusinessTypeVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-27
 */
public interface IMBusinessTypeService extends IService<MBusinessTypeEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<MBusinessTypeVo> selectPage(MBusinessTypeVo searchCondition) ;

    /**
     * 获取列表，页面查询
     */
    List<MBusinessTypeVo> selectList(MBusinessTypeVo searchCondition) ;

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(MBusinessTypeVo vo);

    /**
     * 修改数据
     * @param vo
     * @return
     */
    UpdateResultAo<Integer> update(MBusinessTypeVo vo);

    /**
     * 批量删除
     * @return
     */
    DeleteResultAo<Integer> deleteByIdsIn(List<MBusinessTypeVo> searchCondition);

    /**
     * 批量启用
     * @return
     */
    void enabledByIdsIn(List<MBusinessTypeVo> searchCondition);

    /**
     * 批量禁用
     * @return
     */
    void disSabledByIdsIn(List<MBusinessTypeVo> searchCondition);

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    MBusinessTypeVo selectById(int id);

    /**
     * 通过name查询
     *
     */
    List<MBusinessTypeEntity> selectByName(String name);

    /**
     * 通过key查询
     *
     */
    List<MBusinessTypeEntity> selectByCode(String code);

    /**
     * 批量启用/停用
     */
    void enableByIdsIn(List<MBusinessTypeVo> searchCondition);

    /**
     * 商品板块
     * @param searchCondition 入参
     * @return List<MBusinessTypeExportVo>
     */
    List<MBusinessTypeExportVo> export(MBusinessTypeVo searchCondition);
}
