package com.xinyirun.scm.core.system.service.master.goods;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.goods.MCategoryEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.master.category.MCategoryExportVo;
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
    UpdateResultAo<MCategoryVo> update(MCategoryVo vo);

    /**
     * 批量删除
     * @return
     */
    DeleteResultAo<Integer> deleteByIdsIn(List<MCategoryVo> searchCondition);

    /**
     * 启用类别并返回更新后的数据
     * @param categoryVo 类别对象
     * @return 更新后的类别数据
     */
    MCategoryVo enabledByIdsIn(MCategoryVo categoryVo);

    /**
     * 停用类别并返回更新后的数据
     * @param categoryVo 类别对象
     * @return 更新后的类别数据
     */
    MCategoryVo disSabledByIdsIn(MCategoryVo categoryVo);

    /**
     * 通过name查询
     *
     */
    List<MCategoryEntity> selectByName(String name);



    /**
     * 导出
     * @param searchConditionList 入参
     * @return List<MCategoryExportVo>
     */
    List<MCategoryExportVo> export(MCategoryVo searchConditionList);

    /**
     * 删除（完全参考仓库管理）
     * @param searchCondition 删除条件
     */
    void delete(MCategoryVo searchCondition);

}
