package com.xinyirun.scm.controller.query.inventory;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.inventory.BDailyInventorySumVo;
import com.xinyirun.scm.bean.system.vo.business.inventory.BDailyInventoryVo;
import com.xinyirun.scm.bean.system.vo.excel.query.MDailyInventoryExportVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.inventory.IBDailyInventoryService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * <p>
 * 货主库存查询
 * </p>
 *
 * @author xyr
 * @since 2021-09-24
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/query/inventory/daily")
public class MInventoryDailyQueryController extends SystemBaseController {

    @Autowired
    private IBDailyInventoryService service;

    @SysLogAnnotion("根据查询条件，获取每日入库信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BDailyInventoryVo>>> list(@RequestBody(required = false) BDailyInventoryVo searchCondition) {
//        IPage<BDailyInventoryVo> list = service.selectPage(searchCondition);
        IPage<BDailyInventoryVo> list = service.selectPageNew(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取每日入库合计信息")
    @PostMapping("/list/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BDailyInventorySumVo>> sum(@RequestBody(required = false) BDailyInventoryVo searchCondition) {
        BDailyInventorySumVo vo = service.selectSumDataNew(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("每日数据导出")
    @PostMapping("/export")
    public void exportData(@RequestBody(required = false) List<BDailyInventoryVo> searchCondition, HttpServletResponse response) throws Exception {
        List<MDailyInventoryExportVo> list = service.selectExportList(searchCondition);
        EasyExcelUtil<MDailyInventoryExportVo> util = new EasyExcelUtil<>(MDailyInventoryExportVo.class);
        util.exportExcel("每日库存" + DateTimeUtil.getDate(), "每日库存", list, response);
    }

    @SysLogAnnotion("每日数据导出")
    @PostMapping("/export_all")
    public void exportAllData(@RequestBody(required = false) BDailyInventoryVo searchCondition, HttpServletResponse response) throws Exception {
        List<MDailyInventoryExportVo> list = service.selectExportAllList(searchCondition);
        EasyExcelUtil<MDailyInventoryExportVo> util = new EasyExcelUtil<>(MDailyInventoryExportVo.class);
        util.exportExcel("每日库存" + DateTimeUtil.getDate(), "每日库存", list, response);
    }
}
