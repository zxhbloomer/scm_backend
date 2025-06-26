package com.xinyirun.scm.core.system.service.master.warehouse;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.warehouse.MLocationEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MLocationExportVo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MLocationVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IMLocationService extends IService<MLocationEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<MLocationVo> selectPage(MLocationVo searchCondition);

    /**
     * 获取列表，页面查询
     */
    List<MLocationVo> selectList(MLocationVo searchCondition);

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<Integer> insert(MLocationVo vo);

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    UpdateResultAo<Integer> update(MLocationVo vo);


    /**
     * 通过name查询
     *
     */
    List<MLocationEntity> selectByName(String name,int warehouse_id);

    /**
     * 通过code查询
     *
     */
    List<MLocationEntity> selectByCode(String code,int warehouse_id);

    /**
     * 通过shortName查询
     *
     */
    List<MLocationEntity> selectByShortName(String shortName,int warehouse_id);

    /**
     * 批量启用
     */
    void enabledByIdsIn(List<MLocationVo> searchCondition);

    /**
     * 批量停用
     */
    void disSabledByIdsIn(List<MLocationVo> searchCondition);

    /**
     * 批量启用/停用
     */
    void enableByIdsIn(List<MLocationVo> searchCondition);



    /**
     * 查询by id，返回结果
     */
    MLocationVo selectById(int id);

    /**
     * 导出
     * @param searchCondition 入参
     * @return List<MLocationExportVo>
     */
    List<MLocationExportVo> export(MLocationVo searchCondition);

}
