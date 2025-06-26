package com.xinyirun.scm.core.system.service.master.rbac.permission.role;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionEntity;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.MPermissionMenuOperationVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.operation.OperationMenuDataVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.operation.OperationMenuVo;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author zxh
 * @since 2020-07-27
 */
public interface IMPermissionRoleOperationService extends IService<MPermissionEntity> {

    /**
     * 获取所有数据
     */
    OperationMenuVo getTreeData(OperationMenuDataVo searchCondition) ;

    /**
     * 复制选中的菜单
     */
    void setSystemMenuData2PermissionData(OperationMenuDataVo searchCondition) ;

    /**
     * 保存权限操作数据和菜单权限
     */
    boolean savePermission(MPermissionMenuOperationVo condition) ;

}
