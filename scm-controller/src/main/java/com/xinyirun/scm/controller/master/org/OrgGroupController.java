package com.xinyirun.scm.controller.master.org;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.entity.master.org.MGroupEntity;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.org.MGroupExportVo;
import com.xinyirun.scm.bean.system.vo.master.org.MGroupVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.service.master.org.IMGroupService;
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
@RequestMapping(value = "/api/v1/org/group")
@Slf4j
// @Api(tags = "集团表相关")
public class OrgGroupController extends SystemBaseController {

    @Autowired
    private IMGroupService service;

    @Autowired
    private RestTemplate restTemplate;

    @SysLogAnnotion("根据id获取集团信息")
    // @ApiOperation(value = "根据参数id，获取集团主表信息")
    @PostMapping("/id")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MGroupVo>> id(@RequestBody(required = false) MGroupVo searchCondition) {
        MGroupVo entity = service.selectByid(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("根据查询条件，获取集团主表信息")
    // @ApiOperation(value = "根据参数id，获取集团主表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MGroupVo>>> list(@RequestBody(required = false) MGroupVo searchCondition) {
        IPage<MGroupVo> entity = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("集团主表数据更新保存")
    // @ApiOperation(value = "根据参数id，获取集团主表信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MGroupVo>> save(@RequestBody(required = false) MGroupEntity bean) {

        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("集团主表数据新增保存")
    // @ApiOperation(value = "根据参数id，获取集团主表信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MGroupVo>> insert(@RequestBody(required = false) MGroupEntity bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("集团主表数据导出")
    // @ApiOperation(value = "根据选择的数据，集团主表数据导出")
    @PostMapping("/export_all")
    public void exportAll(@RequestBody(required = false) MGroupVo searchCondition, HttpServletResponse response) throws IOException {
        List<MGroupVo> searchResult = service.select(searchCondition);
        List<MGroupExportVo> rtnList = BeanUtilsSupport.copyProperties(searchResult, MGroupExportVo.class);
        ExcelUtil<MGroupExportVo> util = new ExcelUtil<>(MGroupExportVo.class);
        util.exportExcel("集团主表数据导出", "集团主表数据", rtnList, response);
    }

    @SysLogAnnotion("集团主表数据导出")
    // @ApiOperation(value = "根据选择的数据，集团主表数据导出")
    @PostMapping("/export_selection")
    public void exportSelection(@RequestBody(required = false) List<MGroupVo> searchConditionList, HttpServletResponse response) throws IOException {
        List<MGroupVo> searchResult = service.selectIdsInForExport(searchConditionList);
        List<MGroupExportVo> rtnList = BeanUtilsSupport.copyProperties(searchResult, MGroupExportVo.class);
        ExcelUtil<MGroupExportVo> util = new ExcelUtil<>(MGroupExportVo.class);
        util.exportExcel("集团主表数据导出", "集团主表数据", rtnList, response);
    }

    @SysLogAnnotion("集团主表数据逻辑删除复原")
    // @ApiOperation(value = "根据参数id，逻辑删除复原数据")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<MGroupVo> searchConditionList) {
        service.deleteByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }
}
