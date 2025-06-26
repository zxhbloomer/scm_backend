package com.xinyirun.scm.core.system.service.master.menu;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.menu.MMenuEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertOrUpdateResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.master.menu.*;

import java.util.List;

/**
 * <p>
 * 菜单 服务类 接口
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
public interface IMMenuService extends IService<MMenuEntity> {

    /**
     * 获取所有数据
     */
    MMenuVo getTreeData(MMenuDataVo searchCondition) ;

    /**
     * 获取所有数据：级联
     */
    List<MMenuDataVo> getCascaderList(MMenuVo searchCondition) ;

    /**
     * 获取所有数据：级联
     */
    MMenuVo getCascaderGet(MMenuVo searchCondition) ;

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    MMenuDataVo selectByid(Long id);

    /**
     * 获取所选id的数据
     */
    List<MMenuEntity> selectIdsIn(List<MMenuVo> searchCondition) ;

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo
     * @return
     */
    InsertResultAo<MMenuDataVo> addMenuGroup(MMenuDataVo vo);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo
     * @return
     */
    InsertResultAo<MMenuDataVo> addTopNav(MMenuDataVo vo);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo
     * @return
     */
    InsertResultAo<MMenuDataVo> addSubNode(MMenuDataVo vo);


    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo
     * @return
     */
    InsertResultAo<MMenuDataVo> addSubMenu(MMenuDataVo vo);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    UpdateResultAo<MMenuDataVo> update(MMenuEntity entity);

    /**
     * 批量物理删除
     * @param searchCondition
     * @return
     */
    DeleteResultAo<String> realDeleteByCode(MMenuDataVo searchCondition);

    /**
     * 拖拽保存
     * @param bean
     * @return
     */
    Boolean dragsave(List<MMenuDataVo> bean);

    /**
     * 菜单重定向更新保存
     * @param bean
     * @return
     */
    InsertOrUpdateResultAo<MMenuRedirectVo> saveRedirect(MMenuRedirectVo bean);
}
