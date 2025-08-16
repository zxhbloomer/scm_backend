package com.xinyirun.scm.controller.master.org;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.entity.master.org.MDeptEntity;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.org.MDeptExportVo;
import com.xinyirun.scm.bean.system.vo.master.org.MDeptVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.service.master.org.IMDeptService;
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
@RequestMapping(value = "/api/v1/org/dept")
@Slf4j
// @Api(tags = "部门表相关")
public class OrgDeptController extends SystemBaseController {

    @Autowired
    private IMDeptService service;

    @Autowired
    private RestTemplate restTemplate;

    @SysLogAnnotion("根据id获取部门信息")
    // @ApiOperation(value = "根据参数id，获取部门主表信息")
    @PostMapping("/id")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MDeptVo>> id(@RequestBody(required = false) MDeptVo searchCondition) {
        MDeptVo entity = service.selectByid(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("根据查询条件，获取部门主表信息")
    // @ApiOperation(value = "根据参数id，获取部门主表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MDeptVo>>> list(@RequestBody(required = false)
        MDeptVo searchCondition)  {
        IPage<MDeptVo> entity = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("部门主表数据更新保存")
    // @ApiOperation(value = "根据参数id，获取部门主表信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MDeptVo>> save(@RequestBody(required = false) MDeptEntity bean) {

        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("部门主表数据新增保存")
    // @ApiOperation(value = "根据参数id，获取部门主表信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MDeptVo>> insert(@RequestBody(required = false) MDeptEntity bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("部门主表数据导出")
    // @ApiOperation(value = "根据选择的数据，部门主表数据导出")
    @PostMapping("/export_all")
    public void exportAll(@RequestBody(required = false) MDeptVo searchCondition, HttpServletResponse response) throws IOException {
        List<MDeptVo> searchResult = service.select(searchCondition);
        List<MDeptExportVo> rtnList = BeanUtilsSupport.copyProperties(searchResult, MDeptExportVo.class);
        ExcelUtil<MDeptExportVo> util = new ExcelUtil<>(MDeptExportVo.class);
        util.exportExcel("部门主表数据导出", "部门主表数据", rtnList, response);
    }

    @SysLogAnnotion("部门主表数据导出")
    // @ApiOperation(value = "根据选择的数据，部门主表数据导出")
    @PostMapping("/export_selection")
    public void exportSelection(@RequestBody(required = false) List<MDeptVo> searchConditionList, HttpServletResponse response) throws IOException {
        List<MDeptVo> searchResult = service.selectIdsInForExport(searchConditionList);
        List<MDeptExportVo> rtnList = BeanUtilsSupport.copyProperties(searchResult, MDeptExportVo.class);
        ExcelUtil<MDeptExportVo> util = new ExcelUtil<>(MDeptExportVo.class);
        util.exportExcel("部门主表数据导出", "部门主表数据", rtnList, response);
    }

    @SysLogAnnotion("部门主表数据逻辑删除复原")
    // @ApiOperation(value = "根据参数id，逻辑删除复原数据")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<MDeptVo> searchConditionList) {
        service.deleteByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }
}
