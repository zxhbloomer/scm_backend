package com.xinyirun.scm.controller.business.warehouse;


import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.warehouse.BWarehouseGroupTransferVo;
import com.xinyirun.scm.bean.system.vo.business.warehouse.BWarehouseTransferVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.business.warehouse.IBWarehouseGroupRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 仓库关系表-一级 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2022-01-30
 */
@RestController
//@RequestMapping("/api/v1/warehouse/group/one")
public class BWarehouseGroupRelationController {
//
//    @Autowired
//    private IBWarehouseGroupRelationService service;
//
//    @SysLogAnnotion("获取穿梭框数据")
//    @PostMapping("/transfer/get")
//    @ResponseBody
//    @RepeatSubmitAnnotion
//    public ResponseEntity<JsonResultAo<BWarehouseGroupTransferVo>> getRoleTransferList(@RequestBody(required = false) BWarehouseTransferVo bean) {
//        BWarehouseGroupTransferVo rtn = service.getWarehouseTransferList(bean);
//        return ResponseEntity.ok().body(ResultUtil.OK(rtn));
//    }
//
//    @SysLogAnnotion("保存穿梭框数据，仓库组仓库设置")
//    @PostMapping("/transfer/save")
//    @ResponseBody
//    @RepeatSubmitAnnotion
//    public ResponseEntity<JsonResultAo<BWarehouseGroupTransferVo>> setRoleTransferList(@RequestBody(required = false) BWarehouseTransferVo bean) {
//        return ResponseEntity.ok().body(ResultUtil.OK(service.setWarehouseTransfer(bean)));
//    }

}
