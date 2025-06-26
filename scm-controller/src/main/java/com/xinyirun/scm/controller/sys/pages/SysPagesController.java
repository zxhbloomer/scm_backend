package com.xinyirun.scm.controller.sys.pages;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.entity.sys.pages.SPagesEntity;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesExportVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
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
@RequestMapping(value = "/api/v1/sys/pages")
@Slf4j
// @Api(tags = "页面维护相关")
public class SysPagesController extends SystemBaseController {

    @Autowired
    private ISPagesService service;

    @SysLogAnnotion("根据查询条件，获取vue页面设置信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<SPagesVo>>> list(@RequestBody(required = false) SPagesVo searchCondition) {
        IPage<SPagesVo> entity = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("根据查询条件，获取页面信息")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SPagesVo>> get(@RequestBody(required = false) SPagesVo searchCondition) {
        SPagesVo sPagesVo = new SPagesVo();
        sPagesVo.setCode(searchCondition.getCode());
        SPagesVo pagesVo = service.get(sPagesVo);
        return ResponseEntity.ok().body(ResultUtil.OK(pagesVo));
    }

    @SysLogAnnotion("页面设置数据更新保存")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SPagesVo>> save(@RequestBody(required = false) SPagesEntity bean) {

        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("页面设置数据新增保存")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SPagesVo>> insert(@RequestBody(required = false)
        SPagesEntity bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("页面设置表数据物理删除，部分数据")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false)
        List<SPagesVo> searchConditionList) {
        service.realDeleteByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("页面设置导出")
    @PostMapping("/export")
    public void export(@RequestBody(required = false) SPagesVo searchCondition, HttpServletResponse response) throws IOException {
        List<SPagesExportVo> exportList = service.selectExportList(searchCondition);
        EasyExcelUtil<SPagesExportVo> util = new EasyExcelUtil<>(SPagesExportVo.class);
        util.exportExcel("页面设置" + DateTimeUtil.getDate(), "页面设置", exportList, response);
    }
}
