package com.xinyirun.scm.controller.business.carriage;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.carriage.BCarriageOrderExportVo;
import com.xinyirun.scm.bean.system.vo.business.carriage.BCarriageOrderVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.monitor.IBCarriageOrderService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 订单管理 前端控制器
 * </p>
 *
 * @author wwl
 * @since 2021-03-03
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/carriage")
public class BCarriageController extends SystemBaseController {

    @Autowired
    private IBCarriageOrderService service;

    @SysLogAnnotion("承运订单 列表")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BCarriageOrderVo>>> pageList(@RequestBody(required = false) BCarriageOrderVo param) {
        IPage<BCarriageOrderVo> list = service.selectPage(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("承运订单 导出")
    @PostMapping("/export")
    @ResponseBody
    public void export(@RequestBody(required = false) BCarriageOrderVo param, HttpServletResponse response) throws IOException {
        List<BCarriageOrderExportVo> list = service.exportList(param);
        new EasyExcelUtil<>(BCarriageOrderExportVo.class).exportExcel("承运订单" + DateTimeUtil.getDate(),"承运订单", list, response);
    }

    @SysLogAnnotion("承运订单 新增")
    @PostMapping("/insert")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BCarriageOrderVo>> insert(@RequestBody(required = false) BCarriageOrderVo param) {
        InsertResultAo<BCarriageOrderVo> result = service.insert(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result.getData(),"插入成功"));
    }

    @SysLogAnnotion("承运订单 详情")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BCarriageOrderVo>> get(@RequestBody(required = false) BCarriageOrderVo param) {
        BCarriageOrderVo result = service.getVoById(param.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("承运订单 修改")
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BCarriageOrderVo>> update(@RequestBody(required = false) BCarriageOrderVo param) {
        UpdateResultAo<BCarriageOrderVo> result = service.updateByParam(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result.getData()));
    }
}
