package com.xinyirun.scm.controller.query.inventory;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.BDirectlyWarehouseVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.business.wms.out.order.IBOutOrderService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * @author Wang Qianfeng
 * @Description 直属库统计
 * @date 2022/9/22 9:51
 */
@RestController
@RequestMapping(value = "/api/v1/warehouse")
public class MDirectlyWarehouseController extends SystemBaseController {

    @Resource
    private IBOutOrderService service;

    @SysLogAnnotion("直属库统计")
    @PostMapping("/directly/list")
    public ResponseEntity<JsonResultAo<IPage<BDirectlyWarehouseVo>>> getDirectlyWarehouseList(@RequestBody(required = false) BDirectlyWarehouseVo param) {
        IPage<BDirectlyWarehouseVo> result = service.getDirectlyWarehouseList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }
}
