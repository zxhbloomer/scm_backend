package com.xinyirun.scm.controller.master.container;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.excel.container.MContainerExcelVo;
import com.xinyirun.scm.bean.system.vo.master.container.MContainerVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.master.container.IMContainerService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Author: Wqf
 * @Description: 集装箱列表
 * @CreateTime : 2023/5/30 16:06
 */

@RestController
@RequestMapping("/api/v1/container")
public class MContainerController {

    @Autowired
    private IMContainerService service;

    @GetMapping("list")
    @SysLogAnnotion("查询 箱号 列表, 用于下拉框")
    public ResponseEntity<JsonResultAo<List<MContainerVo>>> selectList() {
        List<MContainerVo> result = service.selectList();
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("page_list")
    @SysLogAnnotion("分页查询 箱号 列表")
    public ResponseEntity<JsonResultAo<IPage<MContainerVo>>> selectPageList(@RequestBody MContainerVo vo) {
        IPage<MContainerVo> result = service.selectPageList(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/insert")
    @SysLogAnnotion("新增 箱号")
    public ResponseEntity<JsonResultAo<MContainerVo>> insert(@RequestBody MContainerVo vo) {
        InsertResultAo<MContainerVo> result = service.insert(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(result.getData()));
    }

    @PostMapping("/save")
    @SysLogAnnotion("更新 箱号")
    public ResponseEntity<JsonResultAo<MContainerVo>> save(@RequestBody MContainerVo vo) {
        UpdateResultAo<MContainerVo> result = service.save(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(result.getData()));
    }

    @PostMapping("/delete")
    @SysLogAnnotion("删除 箱号")
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody MContainerVo vo) {
        service.delete(vo);
        return ResponseEntity.ok().body(ResultUtil.OK("删除成功"));
    }

    @PostMapping("/export")
    @SysLogAnnotion("导出 箱号")
    public void export(@RequestBody MContainerVo vo, HttpServletResponse response) throws IOException {
        List<MContainerExcelVo> list = service.selectExportList(vo);
        EasyExcelUtil<MContainerExcelVo> util = new EasyExcelUtil<>(MContainerExcelVo.class);
        util.exportExcel("箱号管理" + DateTimeUtil.getDate(), "箱号管理", list, response);
    }




}
