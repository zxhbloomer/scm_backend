package com.xinyirun.scm.controller.master.dept;

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
@RequestMapping(value = "/api/v1/permission/dept")
@Slf4j
// @Api(tags = "部门权限相关")
public class PermissionDeptController extends SystemBaseController {

    @Autowired
    private IMPermissionService service;

    @SysLogAnnotion("根据查询条件，获取部门权限表信息")
    // @ApiOperation(value = "根据参数id，获取部门权限表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MPermissionVo>>> list(@RequestBody(required = false)
        MPermissionVo searchCondition)  {
        IPage<MPermissionVo> entity = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("部门权限表数据更新保存")
    // @ApiOperation(value = "根据参数id，获取部门权限表信息")
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

    @SysLogAnnotion("部门权限表数据新增保存")
    // @ApiOperation(value = "根据参数id，获取部门权限表信息")
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

    @SysLogAnnotion("部门权限表数据逻辑删除复原")
    // @ApiOperation(value = "根据参数id，逻辑删除复原数据")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<MPermissionVo> searchConditionList) {
        service.deleteByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }


    @SysLogAnnotion("部门权限表数据获取系统菜单根节点")
    // @ApiOperation(value = "部门权限表数据获取系统菜单根节点")
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
