package com.xinyirun.scm.controller.query.inventory;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.BQtyLossScheduleReportVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.business.wms.in.IBInService;
import com.xinyirun.scm.core.system.service.business.monitor.IBMonitorService;
import com.xinyirun.scm.core.system.service.business.wms.out.IBOutService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Wang Qianfeng
 * @Description 数据看板
 * @date 2022/9/13 15:17
 */
@RestController
@RequestMapping(value = "/api/v1/data_board/")
public class MDataBoardController extends SystemBaseController {

    @Resource
    private IBMonitorService monitorService;

    @Resource
    private IBInService inService;

    @Resource
    private IBOutService outService;

    @SysLogAnnotion("当日累计调度统计")
    @PostMapping("/get/schedule")
    public ResponseEntity<JsonResultAo<BQtyLossScheduleReportVo>> getScheduleStatistics(@RequestBody(required = false) BQtyLossScheduleReportVo param) {
        BQtyLossScheduleReportVo result = monitorService.getScheduleStatistics(param);

        // WMS-702 当日累计物流统计区域，增加原粮出库数量，取值采购合同关联的，且审批通过时间是当天的，且仓库类型是直属库的出库单.出库数量(换算前)
        BigDecimal outRawGrainCount = outService.getOutRawGrainCount(param);
        result.setOut_raw_grain_count(outRawGrainCount);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("当日累计出库量(吨)")
    @PostMapping("/get/out")
    public ResponseEntity<JsonResultAo<List<BQtyLossScheduleReportVo>>> getOutStatistics(@RequestBody(required = false) BQtyLossScheduleReportVo param) {
        List<BQtyLossScheduleReportVo> result = outService.getOutStatistics(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("当日累计入库量(吨)")
    @PostMapping("/get/in")
    public ResponseEntity<JsonResultAo<List<BQtyLossScheduleReportVo>>> getInStatistics(@RequestBody(required = false) BQtyLossScheduleReportVo param) {
//        List<BQtyLossScheduleReportVo> result = inService.getInStatistics(param);
//        return ResponseEntity.ok().body(ResultUtil.OK(result));
        return null;
    }

}
