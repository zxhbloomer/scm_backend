package com.xinyirun.scm.controller.query.largescreen;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.report.largescreen.LogisticsLargeScreenVo;
import com.xinyirun.scm.bean.system.vo.report.largescreen.LogisticsMonitorLargeScreenVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.query.largescreen.LogisticsLargeScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/9/27 14:39
 */

@RestController
@RequestMapping(value = "/api/v1/largescreen")
public class LogisticsLargeScreenController {

    @Autowired
    private LogisticsLargeScreenService service;

    @GetMapping("/logistics/supply")
    @SysLogAnnotion("物流数据管理驾驶舱 供应链")
    public ResponseEntity<JsonResultAo<LogisticsLargeScreenVo>> querySupply() {
        LogisticsLargeScreenVo result = service.querySupply();
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @GetMapping("/logistics/train_count")
    @SysLogAnnotion("物流数据管理驾驶舱 车次相关")
    public ResponseEntity<JsonResultAo<LogisticsLargeScreenVo>> queryTrainCount() {
        LogisticsLargeScreenVo result = service.queryTrainCount();
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @GetMapping("/logistics/carriage")
    @SysLogAnnotion("物流数据管理驾驶舱 物流合同")
    public ResponseEntity<JsonResultAo<LogisticsLargeScreenVo>> querySchedule() {
        LogisticsLargeScreenVo result = service.querySchedule();
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @GetMapping("/logistics/month")
    @SysLogAnnotion("物流数据管理驾驶舱 当月数据")
    public ResponseEntity<JsonResultAo<LogisticsLargeScreenVo>> queryMonthData() {
        LogisticsLargeScreenVo result = service.queryMonthData();
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @GetMapping("/logistics/monitor")
    @SysLogAnnotion("物流数据管理驾驶舱 实时运输状态")
    public ResponseEntity<JsonResultAo<List<LogisticsMonitorLargeScreenVo>>> queryMonitor() {
        List<LogisticsMonitorLargeScreenVo> result = service.queryMonitor();
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

}
