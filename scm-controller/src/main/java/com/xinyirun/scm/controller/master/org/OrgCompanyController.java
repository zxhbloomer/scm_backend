package com.xinyirun.scm.controller.master.org;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.entity.master.org.MCompanyEntity;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.org.MCompanyExportVo;
import com.xinyirun.scm.bean.system.vo.master.org.MCompanyVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.service.master.org.IMCompanyService;
import com.xinyirun.scm.excel.export.ExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/org/company")
@Slf4j
// @Api(tags = "公司表相关")
public class OrgCompanyController extends SystemBaseController {

    @Autowired
    private IMCompanyService service;

    @Autowired
    private RestTemplate restTemplate;

    @SysLogAnnotion("根据查询条件，获取公司主表信息")
    // @ApiOperation(value = "根据参数id，获取公司主表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MCompanyVo>>> list(@RequestBody(required = false) MCompanyVo searchCondition){
        IPage<MCompanyVo> entity = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("公司主表数据更新保存")
    // @ApiOperation(value = "根据参数id，获取公司主表信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MCompanyVo>> save(@RequestBody(required = false) MCompanyEntity bean) {

        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("公司主表数据新增保存")
    // @ApiOperation(value = "根据参数id，获取公司主表信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MCompanyVo>> insert(@RequestBody(required = false) MCompanyEntity bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("公司主表数据导出")
    // @ApiOperation(value = "根据选择的数据，公司主表数据导出")
    @PostMapping("/export_all")
    public void exportAll(@RequestBody(required = false) MCompanyVo searchCondition, HttpServletResponse response) throws IOException {
        List<MCompanyVo> searchResult = service.select(searchCondition);
        List<MCompanyExportVo> rtnList = BeanUtilsSupport.copyProperties(searchResult, MCompanyExportVo.class);
        ExcelUtil<MCompanyExportVo> util = new ExcelUtil<>(MCompanyExportVo.class);
        util.exportExcel("公司主表数据导出", "公司主表数据", rtnList, response);
    }

    @SysLogAnnotion("公司主表数据导出")
    // @ApiOperation(value = "根据选择的数据，公司主表数据导出")
    @PostMapping("/export_selection")
    public void exportSelection(@RequestBody(required = false) List<MCompanyVo> searchConditionList, HttpServletResponse response) throws IOException {
        List<MCompanyVo> searchResult = service.selectIdsInForExport(searchConditionList);
        List<MCompanyExportVo> rtnList = BeanUtilsSupport.copyProperties(searchResult, MCompanyExportVo.class);
        ExcelUtil<MCompanyExportVo> util = new ExcelUtil<>(MCompanyExportVo.class);
        util.exportExcel("公司主表数据导出", "公司主表数据", rtnList, response);
    }

    @SysLogAnnotion("公司主表数据逻辑删除复原")
    // @ApiOperation(value = "根据参数id，逻辑删除复原数据")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<MCompanyVo> searchConditionList) {
        service.deleteByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }
}
