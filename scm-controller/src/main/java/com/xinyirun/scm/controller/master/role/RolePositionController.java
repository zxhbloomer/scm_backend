package com.xinyirun.scm.controller.master.role;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.org.MRolePositionTransferVo;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.MRoleTransferVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.sys.rbac.role.ISRoleService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/role/position")
@Slf4j
public class RolePositionController extends SystemBaseController {

    @Autowired
    private ISRoleService service;

    @SysLogAnnotion("获取所有角色的数据，为穿梭框服务")
    @PostMapping("/transfer/list")

    @ResponseBody
    public ResponseEntity<JsonResultAo<MRolePositionTransferVo>> getRoleTransferList(@RequestBody(required = false) MRoleTransferVo bean) {
        MRolePositionTransferVo rtn = service.getRoleTransferList(bean);
        return ResponseEntity.ok().body(ResultUtil.OK(rtn));
    }

    @SysLogAnnotion("保存穿梭框数据，权限角色设置")
    @PostMapping("/transfer/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MRolePositionTransferVo>> setRoleTransferList(@RequestBody(required = false) MRoleTransferVo bean) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.setRoleTransfer(bean)));
    }

}
