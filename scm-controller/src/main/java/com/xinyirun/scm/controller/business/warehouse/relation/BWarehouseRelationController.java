package com.xinyirun.scm.controller.business.warehouse.relation;


import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.warehouse.relation.BWarehouseRelationDataVo;
import com.xinyirun.scm.bean.system.vo.business.warehouse.relation.BWarehouseRelationVo;
import com.xinyirun.scm.bean.system.vo.business.warehouse.relation.MWarehouseTransferVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.business.warehouse.relation.IBWarehouseRelationService;
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
 * @since 2022-01-30
 */
@RestController
@RequestMapping("/api/v1/position/whgroup/relation")
@Slf4j
public class BWarehouseRelationController {

    @Autowired
    private IBWarehouseRelationService service;


    @SysLogAnnotion("保存所选的数据")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<Boolean>> setRelation(@RequestBody(required = false) BWarehouseRelationDataVo bean) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.setRelation(bean).getData()));
    }

}
