package com.xinyirun.scm.controller.business.warehouse.position;


import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.warehouse.position.BWarehousePositionDataVo;
import com.xinyirun.scm.bean.system.vo.business.warehouse.position.MWarehousePositionTransferVo;
import com.xinyirun.scm.bean.system.vo.business.warehouse.position.MWarehouseTransferVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.business.warehouse.relation.IBWarehousePositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 仓库组权限关系 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2022-03-29
 */
@RestController
@RequestMapping("/api/v1/position/warehouse")
@Slf4j
public class BWarehousePositionController {

    @Autowired
    private IBWarehousePositionService service;

    @SysLogAnnotion("保存所选的数据")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<Boolean>> setRelation(@RequestBody(required = false) BWarehousePositionDataVo bean) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.setWarehouse(bean).getData()));
    }

    @SysLogAnnotion("获取所有仓库的数据，为穿梭框服务")
    @PostMapping("/transfer/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MWarehousePositionTransferVo>> getWarehouseTransferList(@RequestBody(required = false) MWarehouseTransferVo bean) {
        MWarehousePositionTransferVo rtn = service.getWarehouseTransferList(bean);
        return ResponseEntity.ok().body(ResultUtil.OK(rtn));
    }

    @SysLogAnnotion("保存穿梭框数据，仓库设置")
    @PostMapping("/transfer/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> setWarehouseTransferList(@RequestBody(required = false) MWarehouseTransferVo bean) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.setWarehouseTransfer(bean)));
    }

}
