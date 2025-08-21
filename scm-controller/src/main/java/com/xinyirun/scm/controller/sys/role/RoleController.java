package com.xinyirun.scm.controller.sys.role;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.entity.sys.rbac.role.SRoleEntity;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.SRoleExportVo;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.SRoleVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.enums.ResultEnum;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.sys.rbac.role.ISRoleService;
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
@RequestMapping(value = "/api/v1/role")
@Slf4j
// @Api(tags = "角色相关")
public class RoleController extends SystemBaseController {

    @Autowired
    private ISRoleService isRoleService;

    @Autowired
    private RestTemplate restTemplate;

//    @SysLogAnnotion("根据参数id，获取角色信息")
//    // @ApiOperation(value = "根据参数id，获取角色信息")
//    @PostMapping("{ id }")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<SRoleEntity>> info(@RequestParam("id") String id) {
//
//        SRoleEntity sRoleEntity = isRoleService.getById(id);
//
////        ResponseEntity<OAuth2AccessToken
//        return ResponseEntity.ok().body(ResultUtil.OK(sRoleEntity));
//    }

    @SysLogAnnotion("根据查询条件，获取角色信息")
    // @ApiOperation(value = "根据参数id，获取角色信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<SRoleVo>>> list(@RequestBody(required = false) SRoleVo searchCondition) {
        IPage<SRoleVo> sRoleEntity = isRoleService.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(sRoleEntity));
    }

    @SysLogAnnotion("角色数据更新保存")
    // @ApiOperation(value = "根据参数id，获取角色信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SRoleEntity>> save(@RequestBody(required = false) SRoleEntity sRoleEntity) {
//        sRoleEntity.setC_id(null);
//        sRoleEntity.setC_time(null);
        if(isRoleService.updateById(sRoleEntity)){
            return ResponseEntity.ok().body(ResultUtil.OK(isRoleService.getById(sRoleEntity.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("角色数据新增保存")
    // @ApiOperation(value = "根据参数id，获取角色信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SRoleEntity>> insert(@RequestBody(required = false) SRoleEntity sRoleEntity) {
        if(isRoleService.save(sRoleEntity)){
            return ResponseEntity.ok().body(ResultUtil.OK(isRoleService.getById(sRoleEntity.getId()),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("角色数据导出")
    @PostMapping("/export_all")
    public void exportAll(@RequestBody(required = false) SRoleVo searchCondition, HttpServletResponse response) throws IOException {
        List<SRoleExportVo> searchResult = isRoleService.selectExportAll(searchCondition);
        ExcelUtil<SRoleExportVo> util = new ExcelUtil<>(SRoleExportVo.class);
        util.exportExcel("角色数据导出", "角色数据", searchResult, response);
    }

    @SysLogAnnotion("角色数据导出")
    @PostMapping("/export_selection")
    public void exportSelection(@RequestBody(required = false) List<SRoleVo> searchConditionList, HttpServletResponse response) throws IOException {
        List<SRoleExportVo> searchResult = isRoleService.selectExportList(searchConditionList);
        ExcelUtil<SRoleExportVo> util = new ExcelUtil<>(SRoleExportVo.class);
        util.exportExcel("角色数据导出", "角色数据", searchResult, response);
    }

    @SysLogAnnotion("角色数据导入")
    // @ApiOperation(value = "角色数据模板导入")
    @PostMapping("/import")
    public ResponseEntity<JsonResultAo<Object>> importData(@RequestBody(required = false) SRoleVo uploadData,
        HttpServletResponse response) throws Exception {

        // file bean 保存数据库

        // 文件下载并check类型
        // 1、获取模板配置类
//        String json = "{\"dataRows\":{\"dataCols\":[{\"index\":0,\"name\":\"type\"},{\"convertor\":\"datetime\",\"index\":1,\"listValiDator\":[{\"validtorName\":\"required\"},{\"param\":[{\"name\":\"dateFormat\",\"value\":\"yyyy-MM-dd HH:mm:ss\"}],\"validtorName\":\"datetime\"}],\"name\":\"code\"},{\"index\":2,\"name\":\"name\"},{\"index\":3,\"name\":\"descr\"},{\"index\":4,\"name\":\"simple_name\"}]},\"titleRows\":[{\"cols\":[{\"colSpan\":1,\"title\":\"角色类型\"},{\"colSpan\":1,\"title\":\"角色编码\"},{\"colSpan\":1,\"title\":\"角色名称\"},{\"colSpan\":1,\"title\":\"说明\"},{\"colSpan\":1,\"title\":\"简称\"}]}]}";
        String json = "{\"dataRows\":{\"dataCols\":[{\"index\":0,\"name\":\"type\"},{\"index\":1,\"name\":\"code\"},{\"index\":2,\"name\":\"name\"},{\"index\":3,\"name\":\"descr\"},{\"index\":4,\"name\":\"simple_name\"}]},\"titleRows\":[{\"cols\":[{\"colSpan\":1,\"title\":\"角色类型\"},{\"colSpan\":1,\"title\":\"角色编码\"},{\"colSpan\":1,\"title\":\"角色名称\"},{\"colSpan\":1,\"title\":\"说明\"},{\"colSpan\":1,\"title\":\"简称\"}]}]}";
        SystemExcelReader pr = super.downloadExcelAndImportData(uploadData.getUrl(), json);
        List<SRoleEntity> beans = pr.readBeans(SRoleEntity.class);
        SRoleVo SRoleVo = new SRoleVo();
        if (pr.isDataValid()) {
            pr.closeAll();
            // 读取没有错误，开始插入
            isRoleService.saveBatches(beans);
            return ResponseEntity.ok().body(ResultUtil.OK(beans));
        } else {
            // 读取失败，需要返回错误
            File rtnFile = pr.getValidateResultsInFile("角色数据导入错误");
            pr.closeAll();
            SRoleVo errorInfo = super.uploadFile(rtnFile.getAbsolutePath(), SRoleVo.class);
            return ResponseEntity.ok().body(ResultUtil.OK(errorInfo, ResultEnum.IMPORT_DATA_ERROR));
        }
    }

    @SysLogAnnotion("角色数据逻辑删除复原")
    // @ApiOperation(value = "根据参数id，逻辑删除复原数据")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<SRoleVo> searchConditionList) {
        isRoleService.deleteByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

}
