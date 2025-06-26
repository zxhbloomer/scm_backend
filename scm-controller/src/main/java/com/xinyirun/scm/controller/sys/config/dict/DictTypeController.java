package com.xinyirun.scm.controller.sys.config.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictTypeEntity;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictTypeExportVo;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictTypeVo;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.SRoleVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.enums.ResultEnum;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.sys.config.dict.ISDictTypeService;
import com.xinyirun.scm.excel.export.ExcelUtil;
import com.xinyirun.scm.excel.upload.SystemExcelReader;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/dicttype")
@Slf4j
// @Api(tags = "字典主表相关")
public class DictTypeController extends SystemBaseController {

    @Autowired
    private ISDictTypeService isDictTypeService;

    @Autowired
    private RestTemplate restTemplate;

//    @SysLogAnnotion("根据参数id，获取字典主表信息")
//    // @ApiOperation(value = "根据参数id，获取字典主表信息")
//    @PostMapping("{ id }")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<SDictTypeEntity>> info(@RequestParam("id") String id) {
//
//        SDictTypeEntity entity = isDictTypeService.getById(id);
//
////        ResponseEntity<OAuth2AccessToken
//        return ResponseEntity.ok().body(ResultUtil.OK(entity));
//    }

    @SysLogAnnotion("根据查询条件，获取字典主表信息")
    // @ApiOperation(value = "根据参数id，获取字典主表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<SDictTypeEntity>>> list(@RequestBody(required = false) SDictTypeVo searchCondition) {
        IPage<SDictTypeEntity> entity = isDictTypeService.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("字典主表数据更新保存")
    // @ApiOperation(value = "根据参数id，获取字典主表信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SDictTypeEntity>> save(@RequestBody(required = false) SDictTypeEntity bean) {

        if(isDictTypeService.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(isDictTypeService.getById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("字典主表数据新增保存")
    // @ApiOperation(value = "根据参数id，获取字典主表信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SDictTypeEntity>> insert(@RequestBody(required = false) SDictTypeEntity bean) {
        if(isDictTypeService.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(isDictTypeService.getById(bean.getId()),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("字典主表数据导出")
    // @ApiOperation(value = "根据选择的数据，字典主表数据导出")
    @PostMapping("/export_all")
    public void exportAll(@RequestBody(required = false) SDictTypeVo searchCondition, HttpServletResponse response) throws IOException {
        List<SDictTypeExportVo> searchResult = isDictTypeService.selectAllExport(searchCondition);
        ExcelUtil<SDictTypeExportVo> util = new ExcelUtil<>(SDictTypeExportVo.class);
        util.exportExcel("字典主表数据导出", "字典主表数据", searchResult, response);
    }

    @SysLogAnnotion("字典主表数据导出")
    // @ApiOperation(value = "根据选择的数据，字典主表数据导出")
    @PostMapping("/export_selection")
    public void exportSelection(@RequestBody(required = false) List<SDictTypeVo> searchConditionList, HttpServletResponse response) throws IOException {
        List<SDictTypeExportVo> rtnList = isDictTypeService.selectListExport(searchConditionList);
        ExcelUtil<SDictTypeExportVo> util = new ExcelUtil<>(SDictTypeExportVo.class);
        util.exportExcel("字典主表数据导出", "字典主表数据", rtnList, response);
    }

    @SysLogAnnotion("字典主表数据逻辑删除复原")
    // @ApiOperation(value = "根据参数id，逻辑删除复原数据")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<SDictTypeVo> searchConditionList) {
        isDictTypeService.deleteByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("字典类型数据导入")
    // @ApiOperation(value = "字典类型数据模板导入")
    @PostMapping("/import")
    public ResponseEntity<JsonResultAo<Object>> importData(@RequestBody(required = false) SDictTypeVo uploadData,
                                                         HttpServletResponse response) throws Exception {

        // file bean 保存数据库

        // 文件下载并check类型
        // 1、获取模板配置类
//        String json = "{\"dataRows\":{\"dataCols\":[{\"index\":0,\"name\":\"type\"},{\"index\":1,\"name\":\"code\"},{\"index\":2,\"name\":\"name\"},{\"index\":3,\"name\":\"descr\"},{\"index\":4,\"name\":\"simple_name\"}]},\"titleRows\":[{\"cols\":[{\"colSpan\":1,\"title\":\"角色类型\"},{\"colSpan\":1,\"title\":\"角色编码\"},{\"colSpan\":1,\"title\":\"角色名称\"},{\"colSpan\":1,\"title\":\"说明\"},{\"colSpan\":1,\"title\":\"简称\"}]}]}";
        String json = "{\"dataRows\":{\"dataCols\":[{\"index\":0,\"name\":\"type\"},{\"convertor\":\"date\",\"index\":1,\"name\":\"code\"},{\"index\":2,\"listValidators\":[{\"param\":[{\"name\":\"className\",\"value\":\"com.xinyirun.scm.core.logic.system.v1.serviceimpl.sys.config.dict.SDictTypeServiceImpl\"},{\"name\":\"functionName\",\"value\":\"testCheck\"}],\"validtorName\":\"reflection\"}],\"name\":\"name\"},{\"index\":3,\"name\":\"descr\"},{\"index\":4,\"name\":\"simpleName\"}]},\"titleRows\":[{\"cols\":[{\"colSpan\":1,\"title\":\"角色类型\"},{\"colSpan\":1,\"title\":\"角色编码\"},{\"colSpan\":1,\"title\":\"角色名称\"},{\"colSpan\":1,\"title\":\"说明\"},{\"colSpan\":1,\"title\":\"简称\"}]}]}";
        SystemExcelReader pr = super.downloadExcelAndImportData(uploadData.getUrl(), json);
        List<SDictTypeEntity> beans = pr.readBeans(SDictTypeEntity.class);
        if (pr.isDataValid()) {
            pr.closeAll();
            // 读取没有错误，开始插入
            isDictTypeService.saveBatches(beans);
            return ResponseEntity.ok().body(ResultUtil.OK(beans));
        } else {
            // 读取失败，需要返回错误
            File rtnFile = pr.getValidateResultsInFile(pr.getFileName());
            SRoleVo errorInfo = super.uploadFile(rtnFile.getAbsolutePath(), SRoleVo.class);
            pr.closeAll();
            return ResponseEntity.ok().body(ResultUtil.OK(errorInfo, ResultEnum.IMPORT_DATA_ERROR));
        }
    }
}
