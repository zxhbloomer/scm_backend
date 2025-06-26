package com.xinyirun.scm.controller.business.rpt;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyExportVo;
import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.rpd.IRProductDailyAService;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBDailyProductV2Service;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author: Wqf
 * @Description: 每日
 * @CreateTime : 2023/5/16 14:49
 */

@RestController()
@RequestMapping("api/v1/product_daily")
public class BProductDailyController {

    @Autowired
    private ISBDailyProductV2Service service;

    @Autowired
    private IRProductDailyAService aService;


    @GetMapping("/create")
    @SysLogAnnotion("日加工报表生成")
    public ResponseEntity<JsonResultAo<String>> create(BProductDailyVo vo) {
        vo.setType("1");
        service.create("","");
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @GetMapping("/recreate")
    @SysLogAnnotion("日加工报表生成")
    public ResponseEntity<JsonResultAo<String>> recreate(BProductDailyVo vo) {
        vo.setType("1");
        service.recreate(vo);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/pagelist")
    @SysLogAnnotion("日加工报表分页查询")
    public ResponseEntity<JsonResultAo<IPage<BProductDailyVo>>> selectPageList(@RequestBody(required = false) BProductDailyVo vo) {
        IPage<BProductDailyVo> result = aService.selectPageList(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/export")
    @SysLogAnnotion("日加工报表 导出")
    public void export(@RequestBody(required = false) BProductDailyVo vo, HttpServletResponse response) throws Exception {
        List<BProductDailyExportVo> list = aService.exportList(vo);
        EasyExcelUtil<BProductDailyExportVo> util = new EasyExcelUtil<>(BProductDailyExportVo.class);
        util.exportExcel("加工报表" + DateTimeUtil.getDate(), "加工报表", list, response);
    }

    @PostMapping("/sum")
    @SysLogAnnotion("日加工报表分页查询")
    public ResponseEntity<JsonResultAo<List<BProductDailyVo>>> selectListSumApi(@RequestBody(required = false) BProductDailyVo vo) {
        List<BProductDailyVo> result = aService.selectListSumApi(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

}
