package com.xinyirun.scm.core.system.service.sys.rbac.role;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.rbac.role.SRoleEntity;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.SRoleExportVo;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.SRoleVo;

import java.util.List;

/**
 * <p>
 * 角色 服务类
 * </p>
 *
 * @author zxh
 * @since 2019-07-11
 */
public interface ISRoleService extends IService<SRoleEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<SRoleVo> selectPage(SRoleVo searchCondition) ;

    /**
     * 获取所有数据
     */
    List<SRoleExportVo> selectExportAll(SRoleVo searchCondition) ;

    /**
     * 获取所选id的数据
     */
    List<SRoleEntity> selectIdsIn(List<SRoleVo> searchCondition) ;

    /**
     * 批量导入
     */
    boolean saveBatches(List<SRoleEntity> entityList);

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    void deleteByIdsIn(List<SRoleVo> searchCondition);



    /**
     * 部分导出
     * @param searchConditionList 导出id
     * @return List<SRoleExportVo>
     */
    List<SRoleExportVo> selectExportList(List<SRoleVo> searchConditionList);

    /**
     * 获取角色选择弹窗列表（无分页，用于角色选择弹窗）
     * @param searchCondition 查询条件
     * @return List<SRoleVo>
     */
    List<SRoleVo> selectListForDialog(SRoleVo searchCondition);
}
