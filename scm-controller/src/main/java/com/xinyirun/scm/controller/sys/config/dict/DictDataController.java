package com.xinyirun.scm.controller.sys.config.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.app.ao.result.AppJsonResultAo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppResultUtil;
import com.xinyirun.scm.bean.app.vo.sys.config.dict.AppNutuiNameAndValue;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictDataEntity;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictDataExportVo;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictDataVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAppAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.sys.config.dict.ISDictDataService;
import com.xinyirun.scm.excel.export.ExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/dictdata")
@Slf4j
// @Api(tags = "字典数据表相关")
public class DictDataController extends SystemBaseController {

    @Autowired
    private ISDictDataService service;

//    @Autowired
//    private RestTemplate restTemplate;

//    @SysLogAnnotion("根据参数id，获取字典数据表信息")
//    // @ApiOperation(value = "根据参数id，获取字典数据表信息")
//    @PostMapping("{ id }")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<SDictDataEntity>> info(@RequestParam("id") String id) {
//
//        SDictDataEntity entity = service.getById(id);
//
////        ResponseEntity<OAuth2AccessToken
//        return ResponseEntity.ok().body(ResultUtil.OK(entity));
//    }

    @SysLogAnnotion("根据查询条件，获取字典数据表信息")
    // @ApiOperation(value = "根据参数id，获取字典数据表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<SDictDataVo>>> list(@RequestBody(required = false) SDictDataVo searchCondition)  {
        IPage<SDictDataVo> entity = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("字典数据表数据更新保存")
    // @ApiOperation(value = "根据参数id，获取字典数据表信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SDictDataVo>> save(@RequestBody(required = false) SDictDataEntity bean) {
        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("字典数据表数据新增保存")
    // @ApiOperation(value = "根据参数id，获取字典数据表信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SDictDataVo>> insert(@RequestBody(required = false) SDictDataEntity bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("字典数据表数据导出")
    // @ApiOperation(value = "根据选择的数据，字典数据表数据导出")
    @PostMapping("/export_all")
    public void exportAll(@RequestBody(required = false) SDictDataVo searchCondition, HttpServletResponse response) throws IOException {
        List<SDictDataExportVo> rtnList = service.selectAllExport(searchCondition);
        ExcelUtil<SDictDataExportVo> util = new ExcelUtil<>(SDictDataExportVo.class);
        util.exportExcel("字典数据表数据导出", "字典数据表数据", rtnList, response);
    }

    @SysLogAnnotion("字典数据表数据导出")
    // @ApiOperation(value = "根据选择的数据，字典数据表数据导出")
    @PostMapping("/export_selection")
    public void exportSelection(@RequestBody(required = false) List<SDictDataVo> searchConditionList, HttpServletResponse response) throws IOException {
        List<SDictDataExportVo> searchResult = service.selectListExport(searchConditionList);
        ExcelUtil<SDictDataExportVo> util = new ExcelUtil<>(SDictDataExportVo.class);
        util.exportExcel("字典数据表数据导出", "字典数据表数据", searchResult, response);
    }

    @SysLogAnnotion("字典数据表数据逻辑删除复原")
    // @ApiOperation(value = "根据参数id，逻辑删除复原数据")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<SDictDataVo> searchConditionList) {
        service.deleteByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("字典数据表排序后保存")
    // @ApiOperation(value = "list数据的保存")
    @PostMapping("/save_list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<SDictDataVo>>> saveList(@RequestBody(required = false) List<SDictDataVo> beanList) {
        UpdateResultAo<List<SDictDataVo>> result = service.saveList(beanList);
        if(result.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(result.getData(),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("字典排序, 上移")
    @PostMapping("/update_sort_up")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> updateSortUp(@RequestBody(required = false) SDictDataVo bean) {
        service.updateSortUp(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("更新成功"));
    }

    @SysLogAnnotion("字典排序, 下移")
    @PostMapping("/update_sort_down")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> updateSortDown(@RequestBody(required = false) SDictDataVo bean) {
        service.updateSortDown(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("更新成功"));
    }

    @SysLogAppAnnotion("根据查询条件，获取字典数据表信息")
    // @ApiOperation(value = "根据查询条件，获取字典数据表信息")
    @PostMapping("/list/data")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<SDictDataVo>>> selectListData(@RequestBody(required = false) SDictDataVo searchCondition)  {
        List<SDictDataVo> vo = service.selectData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }
}
