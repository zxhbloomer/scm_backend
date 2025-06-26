package com.xinyirun.scm.core.system.service.master.goods;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.goods.MIndustryEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.master.goods.MIndustryExportVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MIndustryVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IMIndustryService extends IService<MIndustryEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<MIndustryVo> selectPage(MIndustryVo searchCondition) ;

    /**
     * 查询by id，返回结果
     */
    MIndustryVo selectById(int id);

    /**
     * 修改数据
     */
    UpdateResultAo<Integer> update(MIndustryVo vo);

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<Integer> insert(MIndustryVo vo);


    /**
     * 批量删除
     */
    DeleteResultAo<Integer> deleteByIdsIn(List<MIndustryVo> searchCondition);

    /**
     * 批量启用
     */
    void enabledByIdsIn(List<MIndustryVo> searchCondition);

    /**
     * 批量禁用
     */
    void disSabledByIdsIn(List<MIndustryVo> searchCondition);

    /**
     * 通过name查询
     *
     */
    List<MIndustryEntity> selectByName(String name);

    /**
     * 通过key查询
     *
     */
    List<MIndustryEntity> selectByBusiness(int businessTypeId);

    /**
     * 批量启用/停用
     */
    void enableByIdsIn(List<MIndustryVo> searchCondition);

    /**
     * 导出
     * @param searchConditionList 入参
     * @return List<MIndustryExportVo>
     */
    List<MIndustryExportVo> export(MIndustryVo searchConditionList);
}
