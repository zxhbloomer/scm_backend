package com.xinyirun.scm.core.system.service.master.goods;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.goods.MCategoryEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.master.goods.MCategoryExportVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MCategoryVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IMCategoryService extends IService<MCategoryEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<MCategoryVo> selectPage(MCategoryVo searchCondition) ;

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(MCategoryVo vo);

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    MCategoryVo selectById(int id);

    /**
     * 修改数据
     * @param vo
     * @return
     */
    UpdateResultAo<Integer> update(MCategoryVo vo);

    /**
     * 批量删除
     * @return
     */
    DeleteResultAo<Integer> deleteByIdsIn(List<MCategoryVo> searchCondition);

    /**
     * 批量启用
     * @return
     */
    void enabledByIdsIn(List<MCategoryVo> searchCondition);

    /**
     * 批量禁用
     * @return
     */
    void disSabledByIdsIn(List<MCategoryVo> searchCondition);

    /**
     * 通过name查询
     *
     */
    List<MCategoryEntity> selectByName(String name);


    /**
     * 批量启用/停用
     */
    void enableByIdsIn(List<MCategoryVo> searchCondition);

    /**
     * 导出
     * @param searchConditionList 入参
     * @return List<MCategoryExportVo>
     */
    List<MCategoryExportVo> export(MCategoryVo searchConditionList);

}
