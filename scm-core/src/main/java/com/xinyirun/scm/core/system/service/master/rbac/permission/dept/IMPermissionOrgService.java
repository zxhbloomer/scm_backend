package com.xinyirun.scm.core.system.service.master.rbac.permission.dept;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionEntity;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.dept.MOrgDeptPermissionTreeVo;

import java.util.List;

/**
 * <p>
 * 权限类页面左侧的树 服务类
 * </p>
 *
 * @author zxh
 * @since 2020-07-27
 */
public interface IMPermissionOrgService extends IService<MPermissionEntity> {
    /**
     * 获取所有数据，左侧树数据
     */
    List<MOrgDeptPermissionTreeVo> getTreeList(MOrgDeptPermissionTreeVo searchCondition) ;

}
