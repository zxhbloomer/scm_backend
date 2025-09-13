package com.xinyirun.scm.core.system.service.master.warehouse;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.warehouse.MLocationEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.master.warehouse.MLocationExportVo;
import com.xinyirun.scm.bean.system.vo.master.warehouse.MLocationVo;

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
     * 单条启用
     */
    MLocationVo enabledByIdsIn(MLocationVo locationVo);

    /**
     * 单条停用
     */
    MLocationVo disSabledByIdsIn(MLocationVo locationVo);




    /**
     * 查询by id，返回结果
     */
    MLocationVo selectById(int id);

    /**
     * 导出专用查询方法，支持动态排序
     * @param searchCondition 查询条件（可包含ids数组用于选中导出）
     */
    List<MLocationExportVo> selectExportList(MLocationVo searchCondition);

    /**
     * 删除库区
     * @param searchCondition 库区删除条件
     */
    void delete(MLocationVo searchCondition);

}
