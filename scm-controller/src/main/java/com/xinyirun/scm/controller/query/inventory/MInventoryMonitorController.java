package com.xinyirun.scm.controller.query.inventory;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.inventory.query.MMonitorInventoryVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.master.inventory.IMInventoryService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Wang Qianfeng
 * @Description 库存异常监管
 * @date 2022/11/23 11:18
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/monitor/inventory")
public class MInventoryMonitorController extends SystemBaseController {

    @Autowired
    private IMInventoryService service;

    @SysLogAnnotion("库存任务监管查询")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MMonitorInventoryVo>>> list(@RequestBody(required = false) MMonitorInventoryVo searchCondition) {
        IPage<MMonitorInventoryVo> list = service.selectInventoryDiff(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }
}
