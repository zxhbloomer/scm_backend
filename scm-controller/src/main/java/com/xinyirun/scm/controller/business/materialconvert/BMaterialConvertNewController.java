package com.xinyirun.scm.controller.business.materialconvert;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BMaterialConvertDetailVo;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BMaterialConvertNewVo;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BMaterialConvertVo;
import com.xinyirun.scm.bean.system.vo.excel.materialconvert.BMaterialConvertExportVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.materialconvert.IBMaterialConvertDetailService;
import com.xinyirun.scm.core.system.service.business.materialconvert.IBMaterialConvertService;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBMaterialConvertV2Service;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 物料转换 前端控制器
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/materialconvert/new")
public class BMaterialConvertNewController extends SystemBaseController {

    @Autowired
    private IBMaterialConvertService service;

    @Autowired
    private IBMaterialConvertDetailService iBMaterialConvertDetailService;

    @Autowired
    ISBMaterialConvertV2Service isbMaterialConvertV2Service;

    @SysLogAnnotion("根据查询条件，获取物料转换分页集合信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BMaterialConvertNewVo>>> list(@RequestBody(required = false) BMaterialConvertNewVo searchCondition) {
        IPage<BMaterialConvertNewVo> list = service.selectPageNew(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取物料转换信息")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BMaterialConvertVo>> get(@RequestBody(required = false) BMaterialConvertVo vo) {
        BMaterialConvertVo allocateVo = service.get(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(allocateVo));
    }
    
    @SysLogAnnotion("新增物料转换单")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BMaterialConvertVo>> insert(@RequestBody(required = false) BMaterialConvertNewVo vo) {
        if(service.newInsert(vo).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(vo.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("新增失败，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("编辑物料转换单")
    @PostMapping("/update")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BMaterialConvertVo>> update(@RequestBody(required = false) BMaterialConvertVo vo) {
        if(service.update(vo).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(vo.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("编辑失败，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("物料转换单启用停用")
    @PostMapping("/enable")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> enabled(@RequestBody(required = false) List<BMaterialConvertVo> list) {
        service.enabled(list);
        return ResponseEntity.ok().body(ResultUtil.OK("ok"));
    }

    @SysLogAnnotion("执行物料转")
    @PostMapping("/execute")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> execute(@RequestBody(required = false) List<BMaterialConvertVo> list) {
        isbMaterialConvertV2Service.execute(list);
        return ResponseEntity.ok().body(ResultUtil.OK("ok"));
    }

    @SysLogAnnotion("根据选择的数据提交，部分数据")
    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> submit(@RequestBody(required = false) List<BMaterialConvertDetailVo> searchConditionList) {
        service.submit(searchConditionList);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据审核，部分数据")
    @PostMapping("/audit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> audit(@RequestBody(required = false) List<BMaterialConvertDetailVo> searchConditionList) {
        Boolean flag = service.audit(searchConditionList);

        if (flag) {
//            isbMaterialConvertV2Service.materialConvert(null, null);
        }

        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }


    @SysLogAnnotion("根据选择的数据废除，部分数据")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> cancel(@RequestBody(required = false) List<BMaterialConvertDetailVo> searchConditionList) {
        service.cancel(searchConditionList);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据驳回，部分数据")
    // @ApiOperation(value = "驳回数据")
    @PostMapping("/return")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> reject(@RequestBody(required = false) List<BMaterialConvertDetailVo> searchConditionList) {
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }



    @SysLogAnnotion("根据查询条件，获取物料转换信息")
    @PostMapping("/getDetail")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BMaterialConvertVo>> getDetail(@RequestBody(required = false) BMaterialConvertVo vo) {
        BMaterialConvertVo allocateVo = service.getDetail(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(allocateVo));
    }

    @SysLogAnnotion("根据ID 懒加载详情列表")
    @PostMapping("/getDetailList")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<BMaterialConvertDetailVo>>> getDetailList(@RequestBody(required = false) BMaterialConvertVo vo) {
        List<BMaterialConvertDetailVo> result = iBMaterialConvertDetailService.getList(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("根据查询条件，获取物料转换信息")
    // @ApiOperation(value = "根据参数获取物料转换信息")
    @PostMapping("/get1")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BMaterialConvertVo>> get1(@RequestBody(required = false) BMaterialConvertVo vo) {
        BMaterialConvertVo allocateVo = service.get1(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(allocateVo));
    }

    @SysLogAnnotion("物料转换单启用停用")
    @PostMapping("/enable1")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> enabled1(@RequestBody(required = false) List<BMaterialConvertVo> list) {
        service.enabled1(list);
        return ResponseEntity.ok().body(ResultUtil.OK("ok"));
    }

    @SysLogAnnotion("物料转换, 导出")
    @PostMapping("/export")
    public void export(@RequestBody(required = false) List<BMaterialConvertNewVo> searchCondition, HttpServletResponse response) throws Exception {
        List<BMaterialConvertExportVo> list = service.selectExportList(searchCondition);
        EasyExcelUtil<BMaterialConvertExportVo> util = new EasyExcelUtil<>(BMaterialConvertExportVo.class);
        util.exportExcel("物料转换" + DateTimeUtil.getDate(), "物料转换", list, response);
    }

    @SysLogAnnotion("物料转换, 导出")
    @PostMapping("/export_all")
    public void exportAll(@RequestBody(required = false) BMaterialConvertNewVo searchCondition, HttpServletResponse response) throws Exception {
        List<BMaterialConvertExportVo> list = service.selectExportAll(searchCondition);
        EasyExcelUtil<BMaterialConvertExportVo> util = new EasyExcelUtil<>(BMaterialConvertExportVo.class);
        util.exportExcel("物料转换" + DateTimeUtil.getDate(), "物料转换", list, response);
    }

}
