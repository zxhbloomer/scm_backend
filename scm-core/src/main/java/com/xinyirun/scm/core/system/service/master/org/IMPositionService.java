package com.xinyirun.scm.core.system.service.master.org;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.master.org.MPositionEntity;
import com.xinyirun.scm.bean.system.vo.master.org.MPositionExportVo;
import com.xinyirun.scm.bean.system.vo.master.org.MPositionVo;
import com.xinyirun.scm.bean.system.vo.master.tree.TreeDataVo;

import java.util.List;

/**
 * <p>
 * 岗位主表 服务类 接口
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
public interface IMPositionService extends IService<MPositionEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<MPositionVo> selectPage(MPositionVo searchCondition);

    /**
     * 获取列表，页面查询
     *
     * @param page_code
     * @return
     */
    List<MPositionVo> selectPositionByPageCode(String page_code);

    /**
     * 查询明细
     * @param searchCondition
     * @return MPositionVo
     */
    MPositionVo getDetail(MPositionVo searchCondition);

    /**
     * 获取岗位仓库权限数据
     * @param searchCondition
     * @return
     */
    TreeDataVo getWarehouseTreeData(MPositionVo searchCondition);

    /**
     * 获取列表，页面查询
     *
     * @param perms
     * @return
     */
    List<MPositionVo> selectPositionByPerms(String perms);

    /**
     * 导出专用查询方法，支持动态排序
     * @param searchCondition 查询条件（可包含ids数组用于选中导出）
     */
    List<MPositionExportVo> selectExportList(MPositionVo searchCondition);

    /**
     * 获取所选id的数据
     */
    List<MPositionEntity> selectIdsIn(List<MPositionVo> searchCondition) ;

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    void deleteByIdsIn(List<MPositionVo> searchCondition);

    /**
     * 从组织架构删除岗位（同时删除岗位实体和组织关联关系）
     * @param searchCondition
     * @return
     */
    void deleteByIdsFromOrg(List<MPositionVo> searchCondition);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(MPositionEntity entity);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    UpdateResultAo<Integer> update(MPositionEntity entity);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param vo
     */
    void updateWarehousePermission(MPositionVo vo);

    /**
     * 获取数据byid
     * @param id
     * @return
     */
    MPositionVo selectByid(Long id);
}
