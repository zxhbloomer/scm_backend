package com.xinyirun.scm.controller.sys.pages;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesFunctionExportVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesFunctionVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesFunctionService;
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
@RequestMapping(value = "/api/v1/sys/pages_fun")
@Slf4j
// @Api(tags = "页面按钮相关")
public class SysPagesFunctionController extends SystemBaseController {

    @Autowired
    private ISPagesFunctionService service;

    @SysLogAnnotion("根据查询条件，获取vue页面按钮信息")
    // @ApiOperation(value = "根据参数id，获取vue页面按钮信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<SPagesFunctionVo>>> list(@RequestBody(required = false) SPagesFunctionVo searchCondition) {
        IPage<SPagesFunctionVo> entity = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("页面按钮数据更新保存")
    // @ApiOperation(value = "页面按钮数据更新保存")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SPagesFunctionVo>> save(@RequestBody(required = false) SPagesFunctionVo bean) {
        UpdateResultAo<SPagesFunctionVo> rtn = service.update(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"更新成功"));
        } else {
            throw new UpdateErrorException(rtn.getMessage());
        }
    }

    @SysLogAnnotion("快速编辑按钮")
    // @ApiOperation(value = "快速编辑按钮")
    @PostMapping("/save_assign")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SPagesFunctionVo>> save_assign(@RequestBody(required = false) SPagesFunctionVo bean) {
        UpdateResultAo<SPagesFunctionVo> rtn = service.update_assign(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("页面按钮数据新增保存")
    // @ApiOperation(value = "页面按钮数据新增保存")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SPagesFunctionVo>> insert(@RequestBody(required = false)
        SPagesFunctionVo bean) {
        InsertResultAo<SPagesFunctionVo> rtn = service.insert(bean);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("页面按钮表数据物理删除，部分数据")
    // @ApiOperation(value = "页面按钮表数据物理删除，部分数据")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false)
        List<SPagesFunctionVo> searchConditionList) {
        service.realDeleteByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("页面按钮表数据 导出")
    @PostMapping("/export")
    public void export(@RequestBody(required = false) SPagesFunctionVo searchConditionList
            , HttpServletResponse response) throws IOException {
        List<SPagesFunctionExportVo> list = service.selectExportList(searchConditionList);
        EasyExcelUtil<SPagesFunctionExportVo> util = new EasyExcelUtil<>(SPagesFunctionExportVo.class);
        util.exportExcel("页面按钮表数据" + DateTimeUtil.getDate(), "页面按钮表数据", list, response);

    }
}
