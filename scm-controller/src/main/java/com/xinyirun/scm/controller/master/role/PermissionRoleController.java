package com.xinyirun.scm.controller.master.role;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.org.MPermissionRoleTransferVo;
import com.xinyirun.scm.bean.system.vo.master.org.MPermissionTransferVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.MMenuRootNodeListVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.MPermissionVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.operation.OperationMenuDataVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.master.rbac.permission.IMPermissionService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/permission/role")
@Slf4j
// @Api(tags = "角色权限相关")
public class PermissionRoleController extends SystemBaseController {

    @Autowired
    private IMPermissionService service;

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

    @SysLogAnnotion("获取所有权限的数据，为穿梭框服务")
    @PostMapping("/permission/transfer/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MPermissionRoleTransferVo>> getPermissionTransferList(@RequestBody(required = false) MPermissionTransferVo bean) {
        MPermissionRoleTransferVo rtn = service.getPermissionTransferList(bean);
        return ResponseEntity.ok().body(ResultUtil.OK(rtn));
    }

    @SysLogAnnotion("保存穿梭框数据，权限角色设置")
    @PostMapping("/permission/transfer/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MPermissionRoleTransferVo>> setPermissionTransferList(@RequestBody(required = false) MPermissionTransferVo bean) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.setPermissionTransfer(bean)));
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

    @SysLogAnnotion("权限权限表数据启用禁用")
    @PostMapping("/enable")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> enable(@RequestBody(required = false) MPermissionVo searchConditionList) {
        service.enableById(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("权限权限表数据设置为管理员")
    @PostMapping("/admin")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> admin(@RequestBody(required = false) MPermissionVo searchConditionList) {
        service.adminById(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("权限权限表数据获取系统菜单根节点")
    // @ApiOperation(value = "权限权限表数据获取系统菜单根节点")
    @PostMapping("/get_sys_menu_root_node")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MMenuRootNodeListVo>> getSystemMenuRootList() {
        MMenuRootNodeListVo searchCondition = new MMenuRootNodeListVo();
        return ResponseEntity.ok().body(ResultUtil.OK(service.getSystemMenuRootList(searchCondition)));
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
