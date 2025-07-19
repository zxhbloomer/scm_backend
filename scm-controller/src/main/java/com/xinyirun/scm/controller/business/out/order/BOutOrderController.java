package com.xinyirun.scm.controller.business.out.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutOrderExportVo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutOrderVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.wms.out.order.IBOutOrderService;
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
 * 出库订单 前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-11-02
 */
@Slf4j
// @Api(tags = "出库订单")
@RestController
@RequestMapping(value = "/api/v1/outorder")
public class BOutOrderController extends SystemBaseController {

    @Autowired
    private IBOutOrderService service;

    @SysLogAnnotion("根据查询条件，获取出库订单列表")
    // @ApiOperation(value = "根据参数获取出库订单列表")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BOutOrderVo>>> pagelist(@RequestBody(required = false) BOutOrderVo searchCondition) {
        IPage<BOutOrderVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，订单量求和")
    // @ApiOperation(value = "根据参数获取出库订单列表")
    @PostMapping("/pagelist/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutOrderVo>> getListSum(@RequestBody(required = false) BOutOrderVo searchCondition) {
        BOutOrderVo result = service.getListSum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("根据查询条件，获取出库订单列表")
    // @ApiOperation(value = "根据参数获取出库订单列表")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BOutOrderVo>>> list(@RequestBody(required = false) BOutOrderVo searchCondition) {
        IPage<BOutOrderVo> list = service.selectList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取出库订单")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutOrderVo>> get(@RequestBody(required = false) BOutOrderVo searchCondition) {
        BOutOrderVo vo = service.get(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("出库订单数据更新保存")
    // @ApiOperation(value = "根据参数id，获取出库订单信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BOutOrderVo>> insert(@RequestBody(required = false) BOutOrderVo bean) {

        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("出库订单数据更新保存")
    // @ApiOperation(value = "根据参数id，获取出库订单信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BOutOrderVo>> save(@RequestBody(required = false) BOutOrderVo bean) {

        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("出库订单数据更新保存")
    // @ApiOperation(value = "根据参数id，获取出库订单信息")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<BOutOrderVo> bean) {
        service.delete(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));

    }

    @SysLogAnnotion("销售订单导出")
    @PostMapping("/export")
    public void exportOutOrder(@RequestBody(required = false) BOutOrderVo param, HttpServletResponse response) throws IOException {
        new EasyExcelUtil<>(BOutOrderExportVo.class)
                .exportExcel("销售订单导出" + DateTimeUtil.getDate(), "销售订单", service.exportOutOrder(param), response);
    }
}
