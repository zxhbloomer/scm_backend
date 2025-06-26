package com.xinyirun.scm.core.system.service.master.driver;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.driver.MDriverEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.master.driver.MDriverExportVo;
import com.xinyirun.scm.bean.system.vo.master.driver.MDriverVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IMDriverService extends IService<MDriverEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<MDriverVo> selectPage(MDriverVo searchCondition) ;

    /**
     * 获取司机
     */
    MDriverVo getDetail(MDriverVo searchCondition) ;

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<Integer> insert(MDriverVo vo);

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    UpdateResultAo<Integer> update(MDriverVo vo);

    /**
     * 查询by id，返回结果
     */
    MDriverVo selectById(int id);

    /**
     * 删除司机
     */
    void delete(MDriverVo searchCondition) ;

    /**
     * 批量启用
     */
    void enabledByIdsIn(List<MDriverVo> searchCondition);

    /**
     * 批量停用
     */
    void disSabledByIdsIn(List<MDriverVo> searchCondition);

    /**
     * 批量启用/停用
     */
    void enableByIdsIn(List<MDriverVo> searchCondition);

    /**
     * 司机 列表导出
     * @param param
     * @return
     */
    List<MDriverExportVo> selectExportList(MDriverVo param);
}
