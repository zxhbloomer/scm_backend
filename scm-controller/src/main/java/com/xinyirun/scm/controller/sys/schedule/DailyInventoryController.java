package com.xinyirun.scm.controller.sys.schedule;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.sys.schedule.ISBDailyInventoryNewService;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBDailyInventoryV2Service;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/daily")
@Slf4j
public class DailyInventoryController extends SystemBaseController {

    @Autowired
    private ISBDailyInventoryNewService service;

    private ISBDailyInventoryV2Service isbDailyInventoryV2Service;

    @SysLogAnnotion("重新计算每日库存")
    @GetMapping("/inventory/recreate/all")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> reCreateAll() {
        service.reCreateDailyInventoryAll();
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("同步每日货值")
    @GetMapping("/inventory/price/sync")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> syncPrice() {
        isbDailyInventoryV2Service.syncDailyInventoryPriceLatest(null, null);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("同步所有每日货值")
    @GetMapping("/inventory/price/sync/all")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> syncAllPrice() {
        isbDailyInventoryV2Service.syncDailyInventoryPriceAll(null, null);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }
}
