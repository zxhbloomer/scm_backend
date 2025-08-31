package com.xinyirun.scm.core.system.service.master.org;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.master.org.MDeptEntity;
import com.xinyirun.scm.bean.system.vo.master.org.MDeptVo;
import com.xinyirun.scm.bean.system.vo.master.org.MDeptExportVo;

import java.util.List;

/**
 * <p>
 * 部门主表 服务类 接口
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
public interface IMDeptService extends IService<MDeptEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<MDeptVo> selectPage(MDeptVo searchCondition) ;

    /**
     * 获取所有数据
     */
    List<MDeptVo> select(MDeptVo searchCondition) ;

    /**
     * 获取所选id的数据
     */
    List<MDeptEntity> selectIdsIn(List<MDeptVo> searchCondition) ;

    /**
     * 获取所选id的数据，导出用
     */
    List<MDeptVo> selectIdsInForExport(List<MDeptVo> searchCondition) ;

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    void deleteByIdsIn(List<MDeptVo> searchCondition);

    /**
     * 从组织架构删除部门（同时删除部门实体和组织关联关系）
     * @param searchCondition
     * @return
     */
    void deleteByIdsFromOrg(List<MDeptVo> searchCondition);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(MDeptEntity entity);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    UpdateResultAo<Integer> update(MDeptEntity entity);

    /**
     * 获取数据byid
     * @param id
     * @return
     */
    MDeptVo selectByid(Long id);

    /**
     * 导出专用查询方法，支持动态排序
     * @param searchCondition 查询条件（可包含ids数组用于选中导出）
     * @return 导出数据列表
     */
    List<MDeptExportVo> selectExportList(MDeptVo searchCondition);
}
