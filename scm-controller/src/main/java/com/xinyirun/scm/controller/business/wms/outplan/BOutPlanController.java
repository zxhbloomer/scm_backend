package com.xinyirun.scm.controller.business.wms.outplan;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.wms.outplan.BOutPlanVo;
import com.xinyirun.scm.bean.system.vo.business.wms.outplan.BOutPlanDetailVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.business.wms.outplan.IBOutPlanService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 出库计划管理 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-19
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/outplan")
public class BOutPlanController extends SystemBaseController {

    @Autowired
    private IBOutPlanService service;

    /**
     * 出库计划  新增
     */
    @PostMapping("/insert")
    @SysLogAnnotion("出库计划 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BOutPlanVo>> insert(@RequestBody BOutPlanVo searchCondition) {
        InsertResultAo<BOutPlanVo> resultAo = service.startInsert(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"新增成功"));
        } else {
            throw new InsertErrorException("新增失败，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("出库计划校验")
    @PostMapping("/validate")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> checkLogic(@RequestBody(required = false) BOutPlanVo searchCondition) {
        CheckResultAo checkResultAo = service.checkLogic(searchCondition, searchCondition.getCheck_type());
        if (!checkResultAo.isSuccess()) {
            throw new BusinessException(checkResultAo.getMessage());
        }else {
            return ResponseEntity.ok().body(ResultUtil.OK("OK"));
        }
    }

    @SysLogAnnotion("根据查询条件，获取出库计划集合信息")
    @PostMapping("/pagelist")
    public ResponseEntity<JsonResultAo<IPage<BOutPlanVo>>> list(@RequestBody(required = false) BOutPlanVo searchCondition) {
        IPage<BOutPlanVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("按出库计划合计")
    @PostMapping("/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutPlanVo>> querySum(@RequestBody(required = false) BOutPlanVo searchCondition) {
        BOutPlanVo result = service.querySum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("根据查询条件，获取出库计划信息")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<BOutPlanVo>> get(@RequestBody(required = false) BOutPlanVo searchCondition) {
        BOutPlanVo vo = service.selectById(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 初始化计划数据
     */
    @SysLogAnnotion("初始化计划数据")
    @PostMapping("/init/plan/data")
    public ResponseEntity<JsonResultAo<List<BOutPlanDetailVo>>> initPlanData(@RequestBody(required = false) BOutPlanDetailVo searchCondition) {
        List<BOutPlanDetailVo> result = service.initPlanData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("出库计划更新保存")
    @PostMapping("/save")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BOutPlanVo>> save(@RequestBody(required = false) BOutPlanVo searchCondition) {
        if(service.startUpdate(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("根据查询条件，出库计划逻辑删除")
    @PostMapping("/delete")
    public ResponseEntity<JsonResultAo<BOutPlanVo>> delete(@RequestBody(required = false) List<BOutPlanVo> searchCondition) {
        if(service.delete(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"删除成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("获取报表系统参数，并组装打印参数")
    @PostMapping("/print")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutPlanVo>> print(@RequestBody(required = false) BOutPlanVo searchCondition) {
        BOutPlanVo printInfo = service.getPrintInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(printInfo));
    }

    @SysLogAnnotion("出库计划，作废")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutPlanVo>> cancel(@RequestBody(required = false) BOutPlanVo searchCondition) {
        if(service.cancel(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("出库计划，完成")
    @PostMapping("/finish")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutPlanVo>> finish(@RequestBody(required = false) BOutPlanVo searchCondition) {
        if(service.finish(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }    }

    @PostMapping("/export")
    @SysLogAnnotion("导出")
    public ResponseEntity<JsonResultAo<List<BOutPlanVo>>> export(@RequestBody(required = false) BOutPlanVo param) {
        List<BOutPlanVo> result = service.selectExportList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

}