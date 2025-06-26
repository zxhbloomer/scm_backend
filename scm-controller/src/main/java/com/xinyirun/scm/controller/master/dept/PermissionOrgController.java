package com.xinyirun.scm.controller.master.dept;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.dept.MOrgDeptPermissionTreeVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.master.rbac.permission.dept.IMPermissionOrgService;
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
@RequestMapping(value = "/api/v1/permission/org")
@Slf4j
// @Api(tags = "权限类页面左侧的树")
public class PermissionOrgController extends SystemBaseController {

    @Autowired
    private IMPermissionOrgService service;

    @SysLogAnnotion("根据查询条件，获取组织机构信息")
    // @ApiOperation(value = "获取组织机构树数据")
    @PostMapping("/tree/dept/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MOrgDeptPermissionTreeVo>>> treeList(@RequestBody(required = false) MOrgDeptPermissionTreeVo searchCondition) {
        List<MOrgDeptPermissionTreeVo> vo = service.getTreeList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }
}
