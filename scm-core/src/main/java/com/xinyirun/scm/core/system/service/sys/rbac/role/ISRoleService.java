package com.xinyirun.scm.core.system.service.sys.rbac.role;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.rbac.role.SRoleEntity;
import com.xinyirun.scm.bean.system.vo.master.org.MRolePositionTransferVo;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.MRoleTransferVo;
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
     * 获取角色清单，为穿梭框服务
     * @return
     */
    MRolePositionTransferVo getRoleTransferList(MRoleTransferVo condition);

    /**
     * 保存角色框数据，权限角色设置
     * @return
     */
    MRolePositionTransferVo setRoleTransfer(MRoleTransferVo bean);

    /**
     * 部分导出
     * @param searchConditionList 导出id
     * @return List<SRoleExportVo>
     */
    List<SRoleExportVo> selectExportList(List<SRoleVo> searchConditionList);
}
