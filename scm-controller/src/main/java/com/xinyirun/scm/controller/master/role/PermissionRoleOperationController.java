package com.xinyirun.scm.controller.master.role;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.MPermissionMenuOperationVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.operation.OperationMenuDataVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.operation.OperationMenuVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.master.menu.IMMenuService;
import com.xinyirun.scm.core.system.service.master.rbac.permission.role.IMPermissionRoleOperationService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/permission/operation")
@Slf4j
// @Api(tags = "权限操作")
public class PermissionRoleOperationController extends SystemBaseController {

    @Autowired
    private IMPermissionRoleOperationService service;

    @Autowired
    private IMMenuService menuService;

    @SysLogAnnotion("根据查询条件，获取角色权限操作数据")
    // @ApiOperation(value = "根据查询条件，获取角色权限操作数据")
    @PostMapping("/role/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<OperationMenuVo>> list(@RequestBody(required = false) OperationMenuDataVo searchCondition) {
        OperationMenuVo entity = service.getTreeData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

//    @SysLogAnnotion("复制选中的菜单")
//    // @ApiOperation(value = "复制选中的菜单")
//    @PostMapping("/dept/set_permission_menu_data")
//    @ResponseBody
//    @RepeatSubmitAnnotion
//    public ResponseEntity<JsonResult<String>> setSystemMenuData2PermissionData(@RequestBody(required = false) OperationMenuDataVo searchCondition)
//    {
//        searchCondition.setTenant_id(super.getUserSessionTenantId());
//        searchCondition.setC_id(super.getUserSessionStaffId());
//        searchCondition.setU_id(super.getUserSessionStaffId());
//
//        service.setSystemMenuData2PermissionData(searchCondition);
//        return ResponseEntity.ok().body(ResultUtil.OK("复制成功","复制成功"));
//    }

    @SysLogAnnotion("保存权限操作数据")
    // @ApiOperation(value = "保存权限操作数据")
    @PostMapping("/role/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> savePermission(@RequestBody(required = false) MPermissionMenuOperationVo condition) {
        boolean rtn = service.savePermission(condition);
        if(rtn){
            return ResponseEntity.ok().body(ResultUtil.OK("保存成功","保存成功"));
        } else {
            throw new UpdateErrorException("保存失败，请查询后重新编辑保存。");
        }
    }
}
