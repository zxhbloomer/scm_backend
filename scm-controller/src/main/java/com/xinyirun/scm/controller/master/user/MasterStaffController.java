package com.xinyirun.scm.controller.master.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.app.ao.result.AppJsonResultAo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppResultUtil;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.org.MStaffOrgVo;
import com.xinyirun.scm.bean.system.vo.master.org.MStaffPositionVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffExportVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.master.user.IMStaffService;
import com.xinyirun.scm.core.system.service.sys.rbac.role.IMRoleStaffService;
import com.xinyirun.scm.core.system.service.master.rbac.permission.IMPermissionStaffService;
import com.xinyirun.scm.core.system.service.master.rbac.permission.IMPermissionStaffExcludeService;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.excel.export.ExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/staff")
@Slf4j
// @Api(tags = "员工主表相关")
public class MasterStaffController extends SystemBaseController {

    @Autowired
    private IMStaffService service;

    @Autowired
    private IMRoleStaffService roleStaffService;

    @Autowired
    private IMPermissionStaffService permissionStaffService;

    @Autowired
    private IMPermissionStaffExcludeService permissionStaffExcludeService;

    @Autowired
    private RestTemplate restTemplate;

    @SysLogAnnotion("根据查询条件，获取员工主表信息")
    // @ApiOperation(value = "根据参数id，获取员工主表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MStaffVo>>> list(@RequestBody(required = false) MStaffVo searchCondition) {
        IPage<MStaffVo> entity = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("获取个人信息接口")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MStaffVo>> getDetail(@RequestBody(required = false) MStaffVo searchCondition) {
        MStaffVo vo = service.getDetail(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("员工主表数据更新保存")
    // @ApiOperation(value = "根据参数id，获取员工主表信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MStaffVo>> save(@RequestBody(required = false) MStaffVo bean, HttpServletRequest request) {
        if (service.update(bean, request.getSession()).isSuccess()) {
            super.doResetUserSessionByStaffId(bean.getId());
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()), "更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("当前员工数据更新保存")
    @PostMapping("/self/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MStaffVo>> saveSelf(@RequestBody(required = false) MStaffVo bean, HttpServletRequest request) {
        if (service.updateSelf(bean).isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()), "更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("员工主表数据新增保存")
    // @ApiOperation(value = "根据参数id，获取员工主表信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MStaffVo>> insert(@RequestBody(required = false) MStaffVo bean, HttpServletRequest request) {
        if (service.insert(bean, request.getSession()).isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()), "插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("员工头像初始化")
    @GetMapping("/avatar/init")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> insertAvatar() {
        service.initAvatar();
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("员工主表数据导出 全部")
    // @ApiOperation(value = "根据选择的数据，员工主表数据导出")
    @PostMapping("/export_all")
    public void exportAll(@RequestBody(required = false) MStaffVo searchCondition, HttpServletResponse response)
            throws IOException {
        List<MStaffExportVo> searchResult = service.selectExportAllList(searchCondition);
        ExcelUtil<MStaffExportVo> util = new ExcelUtil<>(MStaffExportVo.class);
        util.exportExcel("员工主表数据导出", "员工主表数据", searchResult, response);
    }

    @SysLogAnnotion("员工主表数据导出 部分")
    @PostMapping("/export_selection")
    public void exportSelection(@RequestBody(required = false) List<MStaffVo> searchConditionList,
                                HttpServletResponse response) throws IOException {
        List<MStaffExportVo> rtnList = service.selectExportList(searchConditionList);
//        List<MStaffExportVo> rtnList = BeanUtilsSupport.copyProperties(searchResult, MStaffExportVo.class);
        ExcelUtil<MStaffExportVo> util = new ExcelUtil<>(MStaffExportVo.class);
        util.exportExcel("员工主表数据导出", "员工主表数据", rtnList, response);
    }

    @SysLogAnnotion("员工数据逻辑删除")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(
        @RequestBody(required = false) MStaffVo searchCondition) {
        
        // 参数验证
        if (searchCondition == null || searchCondition.getId() == null) {
            throw new BusinessException("请选择要删除的员工记录");
        }
        
        // 调用服务层删除方法
        service.deleteByIdWithValidation(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK("删除成功"));
    }

    @SysLogAnnotion("从组织架构移除员工")
    @PostMapping("/remove/orgtree")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> removeFromOrgTree(
        @RequestBody(required = false) List<MStaffVo> staffList) {
        
        // 参数验证
        if (staffList == null || staffList.isEmpty()) {
            throw new BusinessException("请选择要移除的员工记录");
        }
        
        // 调用服务层移除方法
        service.removeFromOrgTree(staffList);
        return ResponseEntity.ok().body(ResultUtil.OK("已从组织架构中移除"));
    }

    @SysLogAnnotion("从组织删除员工")
    @PostMapping("/delete/org")
    @ResponseBody
    @RepeatSubmitAnnotion  
    public ResponseEntity<JsonResultAo<String>> deleteFromOrg(
        @RequestBody(required = false) List<MStaffVo> staffList) {
        
        // 参数验证
        if (staffList == null || staffList.isEmpty()) {
            throw new BusinessException("请选择要删除的员工记录");
        }
        
        // 调用服务层删除方法
        service.deleteByIdsFromOrg(staffList);
        return ResponseEntity.ok().body(ResultUtil.OK("员工已删除"));
    }

    @SysLogAnnotion("查询岗位员工")
    @PostMapping("/position/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MStaffPositionVo>> getPositionStaffData(@RequestBody(required = false) MStaffPositionVo searchCondition) {
        MStaffPositionVo vo = service.getPositionStaffData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("设置员工岗位")
    @PostMapping("/position/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> setPositionStaff(@RequestBody(required = false) MStaffPositionVo searchCondition) {
        service.setPositionStaff(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("头像上传")
    // @ApiOperation(value = "app:头像上传")
    @PostMapping("/avatar")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<AppJsonResultAo<String>> saveAvatar(String url) {
        service.saveAvatar(url);
        return ResponseEntity.ok().body(AppResultUtil.OK("OK"));
    }

    @SysLogAnnotion("获取员工组织关系信息")
    @PostMapping("/org/relation")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MStaffOrgVo>>> getStaffOrgRelation(@RequestBody Map<String, Object> request) {
        // 参数提取和验证
        Object staffIdObj = request.get("staffId");
        if (staffIdObj == null) {
            throw new BusinessException("员工ID不能为空");
        }
        
        Long staffId;
        if (staffIdObj instanceof Number) {
            staffId = ((Number) staffIdObj).longValue();
        } else if (staffIdObj instanceof String) {
            try {
                staffId = Long.parseLong((String) staffIdObj);
            } catch (NumberFormatException e) {
                throw new BusinessException("员工ID格式错误");
            }
        } else {
            throw new BusinessException("员工ID格式错误");
        }
        
        // 调用服务层查询方法
        List<MStaffOrgVo> orgRelations = service.getStaffOrgRelation(staffId);
        
        return ResponseEntity.ok().body(ResultUtil.OK(orgRelations));
    }

    // ===================【员工角色权限管理API】===================
    
    @SysLogAnnotion("获取员工已分配的角色ID列表")
    @PostMapping("/role/assigned")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<Integer>>> getStaffAssignedRoleIds(@RequestBody Map<String, Long> request) {
        Long staffId = request.get("staff_id");
        if (staffId == null) {
            throw new BusinessException("员工ID不能为空");
        }
        List<Integer> roleIds = roleStaffService.getStaffAssignedRoleIds(staffId);
        return ResponseEntity.ok().body(ResultUtil.OK(roleIds));
    }

    @SysLogAnnotion("保存员工角色关系（全删全插）")
    @PostMapping("/role/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> saveStaffRoles(@RequestBody Map<String, Object> request) {
        Object staffIdObj = request.get("staffId");
        Object roleIdsObj = request.get("roleIds");
        
        // 处理staffId参数
        Long staffId;
        if (staffIdObj instanceof Number) {
            staffId = ((Number) staffIdObj).longValue();
        } else if (staffIdObj instanceof String) {
            try {
                staffId = Long.parseLong((String) staffIdObj);
            } catch (NumberFormatException e) {
                throw new BusinessException("员工ID格式错误");
            }
        } else {
            throw new BusinessException("员工ID不能为空");
        }
        
        // 处理roleIds参数
        List<Integer> roleIds = new ArrayList<>();
        if (roleIdsObj instanceof List) {
            List<?> list = (List<?>) roleIdsObj;
            for (Object item : list) {
                if (item instanceof Number) {
                    roleIds.add(((Number) item).intValue());
                }
            }
        }
        
        boolean success = roleStaffService.saveStaffRoles(staffId, roleIds);
        if (success) {
            return ResponseEntity.ok().body(ResultUtil.OK("员工角色保存成功"));
        } else {
            throw new BusinessException("员工角色保存失败");
        }
    }

    @SysLogAnnotion("获取员工已分配的权限ID列表")
    @PostMapping("/permissions/assigned")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<Long>>> getStaffAssignedPermissionIds(@RequestBody Map<String, Object> request) {
        Object staffIdObj = request.get("staff_id");
        
        // 处理staffId参数
        Long staffId;
        if (staffIdObj instanceof Number) {
            staffId = ((Number) staffIdObj).longValue();
        } else if (staffIdObj instanceof String) {
            try {
                staffId = Long.parseLong((String) staffIdObj);
            } catch (NumberFormatException e) {
                throw new BusinessException("员工ID格式错误");
            }
        } else {
            throw new BusinessException("员工ID不能为空");
        }
        
        List<Long> permissionIds = permissionStaffService.getAssignedPermissionIds(staffId);
        return ResponseEntity.ok().body(ResultUtil.OK(permissionIds));
    }

    @SysLogAnnotion("保存员工权限关系（全删全插）")
    @PostMapping("/permissions/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> saveStaffPermissions(@RequestBody Map<String, Object> request) {
        Object staffIdObj = request.get("staffId");
        Object permissionIdsObj = request.get("permissionIds");
        
        // 处理staffId参数
        Long staffId;
        if (staffIdObj instanceof Number) {
            staffId = ((Number) staffIdObj).longValue();
        } else if (staffIdObj instanceof String) {
            try {
                staffId = Long.parseLong((String) staffIdObj);
            } catch (NumberFormatException e) {
                throw new BusinessException("员工ID格式错误");
            }
        } else {
            throw new BusinessException("员工ID不能为空");
        }
        
        // 处理permissionIds参数
        List<Long> permissionIds = new ArrayList<>();
        if (permissionIdsObj instanceof List) {
            List<?> list = (List<?>) permissionIdsObj;
            for (Object item : list) {
                if (item instanceof Number) {
                    permissionIds.add(((Number) item).longValue());
                }
            }
        }
        
        boolean success = permissionStaffService.saveStaffPermissions(staffId, permissionIds);
        if (success) {
            return ResponseEntity.ok().body(ResultUtil.OK("员工权限保存成功"));
        } else {
            throw new BusinessException("员工权限保存失败");
        }
    }

    @SysLogAnnotion("获取员工已排除的权限ID列表")
    @PostMapping("/permissions/excluded")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<Long>>> getStaffExcludedPermissionIds(@RequestBody Map<String, Object> request) {
        Object staffIdObj = request.get("staff_id");
        
        // 处理staffId参数
        Long staffId;
        if (staffIdObj instanceof Number) {
            staffId = ((Number) staffIdObj).longValue();
        } else if (staffIdObj instanceof String) {
            try {
                staffId = Long.parseLong((String) staffIdObj);
            } catch (NumberFormatException e) {
                throw new BusinessException("员工ID格式错误");
            }
        } else {
            throw new BusinessException("员工ID不能为空");
        }
        
        List<Long> excludePermissionIds = permissionStaffExcludeService.getStaffExcludedPermissionIds(staffId);
        return ResponseEntity.ok().body(ResultUtil.OK(excludePermissionIds));
    }

    @SysLogAnnotion("保存员工排除权限关系（全删全插）")
    @PostMapping("/permissions/exclude/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> saveStaffExcludePermissions(@RequestBody Map<String, Object> request) {
        Object staffIdObj = request.get("staffId");
        Object excludePermissionIdsObj = request.get("excludePermissionIds");
        
        // 处理staffId参数
        Long staffId;
        if (staffIdObj instanceof Number) {
            staffId = ((Number) staffIdObj).longValue();
        } else if (staffIdObj instanceof String) {
            try {
                staffId = Long.parseLong((String) staffIdObj);
            } catch (NumberFormatException e) {
                throw new BusinessException("员工ID格式错误");
            }
        } else {
            throw new BusinessException("员工ID不能为空");
        }
        
        // 处理excludePermissionIds参数
        List<Long> excludePermissionIds = new ArrayList<>();
        if (excludePermissionIdsObj instanceof List) {
            List<?> list = (List<?>) excludePermissionIdsObj;
            for (Object item : list) {
                if (item instanceof Number) {
                    excludePermissionIds.add(((Number) item).longValue());
                }
            }
        }
        
        boolean success = permissionStaffExcludeService.saveStaffExcludePermissions(staffId, excludePermissionIds);
        if (success) {
            return ResponseEntity.ok().body(ResultUtil.OK("员工排除权限保存成功"));
        } else {
            throw new BusinessException("员工排除权限保存失败");
        }
    }

}
