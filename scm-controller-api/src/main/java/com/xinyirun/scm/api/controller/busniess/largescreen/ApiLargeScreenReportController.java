package com.xinyirun.scm.api.controller.busniess.largescreen;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.api.vo.business.largescreen.ApiContractQtyStatisticsVo;
import com.xinyirun.scm.bean.api.vo.business.largescreen.ApiTodayQtyStatisticsVo;
import com.xinyirun.scm.bean.api.vo.business.largescreen.ApiWarehouseInventoryStatisticsVo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.api.service.business.v1.largescreen.ApiLargeScreenReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Wqf
 * @Description: 大屏 报表 调用 接口
 * @CreateTime : 2023/7/18 9:46
 */

@Slf4j
// @Api(tags = "分页查询库存")
@RestController
@RequestMapping(value = "/api/service/v1/largescreen")
public class ApiLargeScreenReportController {

    @Autowired
    private ApiLargeScreenReportService service;

    @SysLogApiAnnotion("库存量查询")
    @GetMapping("/warehouse_type/inventory")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<ApiWarehouseInventoryStatisticsVo>> getWarehouseTypeInventory(){
        ApiWarehouseInventoryStatisticsVo result = service.getWarehouseTypeInventory();
        return ResponseEntity.ok().body(ApiResultUtil.OK(result));
    }

    @SysLogApiAnnotion("合同量查询")
    @GetMapping("/contract/qty")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<ApiContractQtyStatisticsVo>> getContractQty(){
        ApiContractQtyStatisticsVo result = service.getContractQty();
        return ResponseEntity.ok().body(ApiResultUtil.OK(result));
    }

    @SysLogApiAnnotion("当日数据统计")
    @GetMapping("/today/qty")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<ApiTodayQtyStatisticsVo>> getTodayQty(){
        ApiTodayQtyStatisticsVo result = service.getTodayQty();
        return ResponseEntity.ok().body(ApiResultUtil.OK(result));
    }

    @SysLogApiAnnotion("查询监管任务预警数量")
    @GetMapping("/alarm/monitor")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<Integer>> selectMonitorAlarmCount(){
        Integer result = service.selectMonitorAlarmCount();
        return ResponseEntity.ok().body(ApiResultUtil.OK(result));
    }


}
