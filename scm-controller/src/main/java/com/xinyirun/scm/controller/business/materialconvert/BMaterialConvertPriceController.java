package com.xinyirun.scm.controller.business.materialconvert;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BMaterialConvertPriceExportVo;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BMaterialConvertPriceVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.materialconvert.IBMaterialConvertService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Wang Qianfeng
 * @Description 物料转换商品价格
 * @date 2022/10/19 15:41
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/materialConvertPrice")
public class BMaterialConvertPriceController extends SystemBaseController {

    @Autowired
    private IBMaterialConvertService service;

    @SysLogAnnotion("根据查询条件，查询列表")
    @PostMapping("/list")
    public ResponseEntity<JsonResultAo<IPage<BMaterialConvertPriceVo>>> list(@RequestBody(required = false) BMaterialConvertPriceVo searchCondition) {
        IPage<BMaterialConvertPriceVo> list = service.selectConvertPricePage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("导出")
    @PostMapping("/export")
    public void exportList(@RequestBody(required = false) BMaterialConvertPriceVo searchCondition, HttpServletResponse response) throws IOException {
        List<BMaterialConvertPriceExportVo> list = service.exportList(searchCondition);
        new EasyExcelUtil<>(BMaterialConvertPriceExportVo.class).exportExcel("物料转换商品价格" + DateTimeUtil.getDate(), "物料转换商品价格", list, response);
    }

    @SysLogAnnotion("根据查询条件，查询列表")
    @PostMapping("/list/sum")
    public ResponseEntity<JsonResultAo<BMaterialConvertPriceVo>> listSum(@RequestBody(required = false) BMaterialConvertPriceVo searchCondition) {
        BMaterialConvertPriceVo result = service.selectConvertPriceSum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }
}
