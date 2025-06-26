package com.xinyirun.scm.controller.master.inventory;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryAccountExportVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryAccountVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.master.inventory.IMInventoryAccountService;
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
 * <p>
 *  前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
// @Api(tags = "库存流水")
@RestController
@RequestMapping(value = "/api/v1/inventoryaccount")
public class MInventoryAccountController extends SystemBaseController {

    @Autowired
    private IMInventoryAccountService service;

    @SysLogAnnotion("根据查询条件，获取库存流水信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MInventoryAccountVo>>> list(@RequestBody(required = false) MInventoryAccountVo searchCondition) {
        IPage<MInventoryAccountVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }


    @SysLogAnnotion("根据查询条件，获取库存流水信息")
    @PostMapping("/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MInventoryAccountVo>> listSum(@RequestBody(required = false) MInventoryAccountVo searchCondition) {
        MInventoryAccountVo list = service.selectListSum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取库存流水信息")
    @PostMapping("/export")
    public void export(@RequestBody(required = false) MInventoryAccountVo searchCondition, HttpServletResponse response) throws IOException {
        List<MInventoryAccountExportVo> list = service.selectExportList(searchCondition);
        EasyExcelUtil<MInventoryAccountExportVo> util = new EasyExcelUtil<>(MInventoryAccountExportVo.class);
        util.exportExcel("库存流水" + DateTimeUtil.getDate(), "库存流水", list, response);
    }
}
