package com.xinyirun.scm.controller.master.org;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.entity.master.org.MPositionEntity;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.org.MPositionExportVo;
import com.xinyirun.scm.bean.system.vo.master.org.MPositionVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.master.org.IMPositionService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/org/position")
@Slf4j
// @Api(tags = "岗位表相关")
public class OrgPositionController extends SystemBaseController {

    @Autowired
    private IMPositionService service;

    @Autowired
    private RestTemplate restTemplate;

    @SysLogAnnotion("根据查询条件，获取岗位主表信息")
    @PostMapping("/id")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MPositionVo>> id(@RequestBody(required = false)
        MPositionVo searchCondition)  {
        MPositionVo entity = service.selectByid(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("根据查询条件，获取岗位主表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MPositionVo>>> list(@RequestBody(required = false) MPositionVo searchCondition)  {
        IPage<MPositionVo> entity = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("根据查询条件，获取岗位仓库权限信息")
    @PostMapping("/detail")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MPositionVo>> getWarehousePermission(@RequestBody(required = false) MPositionVo searchCondition)  {
        MPositionVo vo = service.getDetail(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("岗位主表数据更新保存")
    @PostMapping("/warehousepermission/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MPositionVo>> saveWarehousePermission(@RequestBody(required = false) MPositionVo vo) {
        service.updateWarehousePermission(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("岗位主表数据更新保存")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MPositionVo>> save(@RequestBody(required = false) MPositionEntity bean) {
        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("岗位主表数据新增保存")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MPositionVo>> insert(@RequestBody(required = false) MPositionEntity bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("岗位主表数据导出")
    @PostMapping("/export_all")
    public void exportAll(@RequestBody(required = false) MPositionVo searchCondition, HttpServletResponse response) throws Exception {
        List<MPositionExportVo> searchResult = service.select(searchCondition);
        EasyExcelUtil<MPositionExportVo> util = new EasyExcelUtil<>(MPositionExportVo.class);
        util.exportExcel("岗位主表数据导出", "岗位主表数据", searchResult, response);
    }

    @SysLogAnnotion("岗位主表数据导出")
    @PostMapping("/export_selection")
    public void exportSelection(@RequestBody(required = false) List<MPositionVo> searchConditionList, HttpServletResponse response) throws Exception {
        List<MPositionExportVo> searchResult = service.selectIdsInForExport(searchConditionList);
        EasyExcelUtil<MPositionExportVo> util = new EasyExcelUtil<>(MPositionExportVo.class);
        util.exportExcel("岗位主表数据导出", "岗位主表数据", searchResult, response);
    }

    @SysLogAnnotion("岗位主表数据逻辑删除复原")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<MPositionVo> searchConditionList) {
        service.deleteByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }
}
