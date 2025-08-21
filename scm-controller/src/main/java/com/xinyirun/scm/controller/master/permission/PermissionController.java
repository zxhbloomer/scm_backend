package com.xinyirun.scm.controller.master.permission;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.MMenuRootNodeListVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.MPermissionVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.operation.OperationMenuDataVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.master.rbac.permission.IMPermissionService;
import com.xinyirun.scm.core.system.service.master.rbac.permission.IMPermissionRoleService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/permission")
@Slf4j
// @Api(tags = "角色权限相关")
public class PermissionController extends SystemBaseController {

    @Autowired
    private IMPermissionService service;

    @Autowired
    private IMPermissionRoleService permissionRoleService;

    @SysLogAnnotion("根据查询条件，获取权限权限表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MPermissionVo>>> list(@RequestBody(required = false)
        MPermissionVo searchCondition)  {
        IPage<MPermissionVo> entity = service.selectRolePermissionPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("根据查询条件，不分页获取权限权限表信息")
    @PostMapping("/cascaderlist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MPermissionVo>>> cascaderList(@RequestBody(required = false)
                                                                           MPermissionVo searchCondition)  {
        List<MPermissionVo> entity = service.selectCascaderList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }




    @SysLogAnnotion("权限权限表数据更新保存")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MPermissionVo>> save(@RequestBody(required = false) MPermissionVo bean) {
        UpdateResultAo<MPermissionVo> rtn = service.update(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("权限权限表数据新增保存")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MPermissionVo>> insert(@RequestBody(required = false) MPermissionVo bean) {
        OperationMenuDataVo operationMenuDataVo = new OperationMenuDataVo();
        operationMenuDataVo.setC_id(super.getUserSessionStaffId());
        operationMenuDataVo.setU_id(super.getUserSessionStaffId());

        InsertResultAo<MPermissionVo> rtn = service.insert(bean, operationMenuDataVo);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("权限权限表数据刷新")
    @PostMapping("/refresh")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> refresh(@RequestBody(required = false) MPermissionVo bean) {
        service.refresh(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("权限权限表数据逻辑删除复原")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<MPermissionVo> searchConditionList) {
        service.deleteByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }


    @SysLogAnnotion("权限权限表数据设置为管理员")
    @PostMapping("/admin")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> admin(@RequestBody(required = false) MPermissionVo searchConditionList) {
        service.adminById(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("获取所有权限列表，为权限选择弹窗服务")
    @PostMapping("/all")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MPermissionVo>>> getAllPermissions(@RequestBody(required = false) MPermissionVo searchCondition) {
        if (searchCondition == null) {
            searchCondition = new MPermissionVo();
        }
        searchCondition.setIs_del(false); // 只查询未删除的权限
        List<MPermissionVo> permissions = service.selectCascaderList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(permissions));
    }

    @SysLogAnnotion("获取角色已分配的权限ID列表")
    @PostMapping("/role/assigned")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<Integer>>> getRoleAssignedPermissionIds(@RequestBody Map<String, Long> request) {
        Long roleId = request.get("role_id");
        if (roleId == null) {
            throw new BusinessException("角色ID不能为空");
        }
        List<Integer> permissionIds = service.getRoleAssignedPermissionIds(roleId);
        return ResponseEntity.ok().body(ResultUtil.OK(permissionIds));
    }

    @SysLogAnnotion("权限权限表数据获取系统菜单根节点")
    // @ApiOperation(value = "权限权限表数据获取系统菜单根节点")
    @PostMapping("/get_sys_menu_root_node")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MMenuRootNodeListVo>> getSystemMenuRootList() {
        MMenuRootNodeListVo searchCondition = new MMenuRootNodeListVo();
        return ResponseEntity.ok().body(ResultUtil.OK(service.getSystemMenuRootList(searchCondition)));
    }

    @SysLogAnnotion("保存角色权限关系（全删全插）")
    @PostMapping("/role/permissions/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> saveRolePermissions(@RequestBody Map<String, Object> request) {
        Object roleIdObj = request.get("roleId");
        Object permissionIdsObj = request.get("permissionIds");
        
        // 处理roleId参数
        Long roleId;
        if (roleIdObj instanceof Number) {
            roleId = ((Number) roleIdObj).longValue();
        } else if (roleIdObj instanceof String) {
            try {
                roleId = Long.parseLong((String) roleIdObj);
            } catch (NumberFormatException e) {
                throw new BusinessException("角色ID格式错误");
            }
        } else {
            throw new BusinessException("角色ID不能为空");
        }
        
        // 处理permissionIds参数
        List<Integer> permissionIds = new ArrayList<>();
        if (permissionIdsObj instanceof List) {
            List<?> list = (List<?>) permissionIdsObj;
            for (Object item : list) {
                if (item instanceof Number) {
                    permissionIds.add(((Number) item).intValue());
                }
            }
        }
        
        boolean success = permissionRoleService.saveRolePermissions(roleId, permissionIds);
        if (success) {
            return ResponseEntity.ok().body(ResultUtil.OK("角色权限保存成功"));
        } else {
            throw new BusinessException("角色权限保存失败");
        }
    }

//    @SysLogAnnotion("判断是否已经选择了菜单")
//    // @ApiOperation(value = "判断是否已经选择了菜单")
//    @PostMapping("/setted")
//    @ResponseBody
//    public ResponseEntity<JsonResult<Boolean>> isAlreadySetMenuId(@RequestBody(required = false) MPermissionVo searchCondition) {
//        searchCondition.setTenant_id(getUserSessionTenantId());
//        return ResponseEntity.ok().body(ResultUtil.OK(service.isAlreadySetMenuId(searchCondition)));
//    }

}
