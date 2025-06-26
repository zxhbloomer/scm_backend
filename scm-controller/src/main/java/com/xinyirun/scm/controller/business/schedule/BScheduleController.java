package com.xinyirun.scm.controller.business.schedule;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.schedule.BScheduleSumVo;
import com.xinyirun.scm.bean.system.vo.business.schedule.BScheduleVo;
import com.xinyirun.scm.bean.system.vo.excel.schedule.BScheduleExcelVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.schedule.IBScheduleService;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBScheduleV2Service;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 入库单 前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
// @Api(tags = "入库单")
@RestController
@RequestMapping(value = "/api/v1/schedule")
public class BScheduleController extends SystemBaseController {

    @Autowired
    private IBScheduleService service;

    @Autowired
    private ISBScheduleV2Service isbScheduleV2Service;

    @SysLogAnnotion("根据查询条件，获取调度单信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BScheduleVo>>> list(@RequestBody(required = false) BScheduleVo searchCondition) {
        IPage<BScheduleVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取调度单信息")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BScheduleVo>> get(@RequestBody(required = false) BScheduleVo searchCondition) {
        BScheduleVo vo = service.get(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("调度单数据新增")
    @PostMapping("/insert1")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BScheduleVo>> insert(@RequestBody(required = false) BScheduleVo bean) {
        InsertResultAo<BScheduleVo> rtn = service.insert1(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"新增成功"));
        } else {
            throw new InsertErrorException("新增失败");
        }
    }

    @SysLogAnnotion("调度单数据新增")
    @PostMapping("/insert2")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BScheduleVo>> insert1(@RequestBody(required = false) BScheduleVo bean) {
        InsertResultAo<BScheduleVo> rtn = service.insert2(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"新增成功"));
        } else {
            throw new InsertErrorException("新增失败");
        }
    }

    @SysLogAnnotion("物流直达单数据新增")
    @PostMapping("/insert3")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BScheduleVo>> insert3(@RequestBody(required = false) BScheduleVo bean) {
        InsertResultAo<BScheduleVo> rtn = service.insert3(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"新增成功"));
        } else {
            throw new InsertErrorException("新增失败");
        }
    }

    @SysLogAnnotion("物流直达单数据修改")
    @PostMapping("/update3")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BScheduleVo>> update3(@RequestBody(required = false) BScheduleVo bean) {
        UpdateResultAo<BScheduleVo> rtn = service.update3(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"更新成功"));
        } else {
            throw new InsertErrorException("更新失败");
        }
    }


    @SysLogAnnotion("物流直采单数据新增")
    @PostMapping("/insert4")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BScheduleVo>> insert4(@RequestBody(required = false) BScheduleVo bean) {
        InsertResultAo<BScheduleVo> rtn = service.insert4(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"新增成功"));
        } else {
            throw new InsertErrorException("新增失败");
        }
    }

    @SysLogAnnotion("物流直采单数据修改")
    @PostMapping("/update4")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BScheduleVo>> update4(@RequestBody(required = false) BScheduleVo bean) {
        UpdateResultAo<BScheduleVo> rtn = service.update4(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"更新成功"));
        } else {
            throw new InsertErrorException("更新失败");
        }
    }


    @SysLogAnnotion("物流直销单数据新增")
    @PostMapping("/insert5")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BScheduleVo>> insert5(@RequestBody(required = false) BScheduleVo bean) {
        InsertResultAo<BScheduleVo> rtn = service.insert5(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"新增成功"));
        } else {
            throw new InsertErrorException("新增失败");
        }
    }

    @SysLogAnnotion("物流直销单数据修改")
    @PostMapping("/update5")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BScheduleVo>> update5(@RequestBody(required = false) BScheduleVo bean) {
        UpdateResultAo<BScheduleVo> rtn = service.update5(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"更新成功"));
        } else {
            throw new InsertErrorException("更新失败");
        }
    }

    @SysLogAnnotion("根据选择的数据提交，部分数据")
    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> submit(@RequestBody(required = false) List<BScheduleVo> searchConditionList) {
        service.submit(searchConditionList);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据审核，部分数据")
    @PostMapping("/audit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> audit(@RequestBody(required = false) List<BScheduleVo> searchConditionList) {
        service.audit(searchConditionList);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据驳回，部分数据")
    @PostMapping("/return")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> reject(@RequestBody(required = false) List<BScheduleVo> searchConditionList) {
        service.reject(searchConditionList);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("调度完成")
    @PostMapping("/finish")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> finish(@RequestBody(required = false) List<BScheduleVo> searchConditionList) {
        service.finish(searchConditionList);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("调度启用")
    @PostMapping("/enable")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> enable(@RequestBody(required = false) List<BScheduleVo> searchConditionList) {
        service.enable(searchConditionList);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("导出物流调度")
    @PostMapping("/export")
    public void export(@RequestBody(required = false) List<BScheduleVo> searchCondition, HttpServletResponse response) throws IOException {
        List<BScheduleExcelVo> list = service.selectList(searchCondition);
        new EasyExcelUtil<>(BScheduleExcelVo.class).exportExcel("物流订单"  + DateTimeUtil.getDate(),"物流订单",list, response);
    }

    @SysLogAnnotion("导出物流调度全部")
    @PostMapping("/export_all")
    public void export_all(@RequestBody(required = false) BScheduleVo searchCondition, HttpServletResponse response) throws IOException {
        List<BScheduleExcelVo> list = service.selectListExportAll(searchCondition);
        new EasyExcelUtil<>(BScheduleExcelVo.class).exportExcel("物流订单"  + DateTimeUtil.getDate(),"物流订单",list, response);
    }

    @SysLogAnnotion("根据查询条件，获取调度单信息")
    @PostMapping("/list/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BScheduleSumVo>> sumData(@RequestBody(required = false) BScheduleVo searchCondition) {
        BScheduleSumVo list = service.sumData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("查询可调度数量")
    @PostMapping("/getScheduleQty")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BigDecimal>> getScheduleQty(@RequestBody(required = false) BScheduleVo searchCondition) {
        BigDecimal result = service.getScheduleQty(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("物流订单数据修改")
    @PostMapping("/update1")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BScheduleVo>> update1(@RequestBody(required = false) BScheduleVo bean) {
        UpdateResultAo<BScheduleVo> rtn = service.update1(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"更新成功"));
        } else {
            throw new InsertErrorException("更新失败");
        }
    }

    @SysLogAnnotion("物流订单单数据修改")
    @PostMapping("/update2")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BScheduleVo>> update2(@RequestBody(required = false) BScheduleVo bean) {
        UpdateResultAo<BScheduleVo> rtn = service.update2(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"更新成功"));
        } else {
            throw new InsertErrorException("更新失败");
        }
    }

    @SysLogAnnotion("物流订单作废")
    @PostMapping("/cancel")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> cancel(@RequestBody(required = false) BScheduleVo bean) {
        service.cancel(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("物流订单删除")
    @PostMapping("/delete")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<BScheduleVo> bean) {
        service.delete(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("物流订单 发车数量 是否备份查询")
    @PostMapping("/is_backup")
    public ResponseEntity<JsonResultAo<BScheduleVo>> selectMonitorIsBackup(@RequestBody(required = false) BScheduleVo bean) {
        BScheduleVo result = service.selectMonitorIsBackup(bean);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("物流订单同步")
    @GetMapping("/sync")
    public ResponseEntity<JsonResultAo<String>> selectMonitorIsBackup() {
        isbScheduleV2Service.createScheduleAll(null, null);
        return ResponseEntity.ok().body(ResultUtil.OK("ok"));
    }

}
