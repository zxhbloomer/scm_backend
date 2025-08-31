package com.xinyirun.scm.core.system.service.master.org;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.master.org.MGroupEntity;
import com.xinyirun.scm.bean.system.vo.master.org.MGroupVo;
import com.xinyirun.scm.bean.system.vo.master.org.MGroupExportVo;

import java.util.List;

/**
 * <p>
 * 集团主表 服务类 接口
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
public interface IMGroupService extends IService<MGroupEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<MGroupVo> selectPage(MGroupVo searchCondition) ;

    /**
     * 导出专用查询方法，支持动态排序
     * @param searchCondition 查询条件（可包含ids数组用于选中导出）
     */
    List<MGroupExportVo> selectExportList(MGroupVo searchCondition);

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    void deleteByIdsIn(List<MGroupVo> searchCondition);

    /**
     * 从组织架构删除集团（同时删除集团实体和组织关联关系）
     * @param searchCondition
     * @return
     */
    void deleteByIdsFromOrg(List<MGroupVo> searchCondition);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(MGroupEntity entity);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    UpdateResultAo<Integer> update(MGroupEntity entity);

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    MGroupVo selectByid(Long id);
}
