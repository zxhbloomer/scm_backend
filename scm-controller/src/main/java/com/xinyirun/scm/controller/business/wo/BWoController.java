package com.xinyirun.scm.controller.business.wo;


import cn.idev.excel.EasyExcel;
import cn.idev.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.wo.*;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.wo.IBWoService;
import com.xinyirun.scm.excel.export.CustomMergeStrategy;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  生产管理 controller
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-29
 */
@RestController
@RequestMapping("/api/v1/wo")
public class BWoController {

    @Autowired
    private IBWoService service;

    @PostMapping("/insert")
    @SysLogAnnotion("生产管理 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BWoVo>> insert(@RequestBody BWoVo param) {
        InsertResultAo<BWoVo> rtn = service.insert(param);
        if (rtn.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(), "新增成功"));
        } else {
            throw new InsertErrorException("新增失败");
        }
    }

    @PostMapping("/check")
    @SysLogAnnotion("生产管理 公式校验")
    public ResponseEntity<JsonResultAo<List<Map<String, String>>>> check(@RequestBody BWoVo param) {
        List<Map<String, String>> result = service.check(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/update")
    @SysLogAnnotion("生产管理 修改")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BWoVo>> update(@RequestBody BWoVo param) {
        UpdateResultAo<BWoVo> rtn = service.updateParam(param);
        if (rtn.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(), "更新成功"));
        } else {
            throw new UpdateErrorException("更新失败");
        }
    }

    @PostMapping("/submit")
    @SysLogAnnotion("生产管理 提交")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> submit(@RequestBody List<BWoVo> param) {
        service.submit(param);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/pagelist")
    @SysLogAnnotion("生产管理 列表")
    public ResponseEntity<JsonResultAo<IPage<BWoVo>>> selectPageList(@RequestBody BWoVo param) {
        IPage<BWoVo> result = service.selectPageList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/list/sum")
    @SysLogAnnotion("生产管理 列表")
    public ResponseEntity<JsonResultAo<BWoVo>> selectListSum(@RequestBody BWoVo param) {
        BWoVo result = service.selectListSum(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/todo/count")
    @SysLogAnnotion("生产管理 查询待办数量")
    public ResponseEntity<JsonResultAo<Integer>> selectTodoCount(@RequestBody BWoVo param) {
        Integer result = service.selectTodoCount(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/getDetail")
    @SysLogAnnotion("生产管理 明细")
    public ResponseEntity<JsonResultAo<BWoVo>> getDetail(@RequestBody BWoVo param) {
        BWoVo detail = service.getDetail(param.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(detail));
    }

    @PostMapping("/cancel")
    @SysLogAnnotion("生产管理 作废")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> cancel(@RequestBody BWoVo param) {
        service.cancel(param);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/audit")
    @SysLogAnnotion("生产管理 审核通过")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> audit(@RequestBody List<BWoVo> param) {
        service.audit(param);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/reject")
    @SysLogAnnotion("生产管理 审核驳回")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> reject(@RequestBody List<BWoVo> param) {
        service.reject(param);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/check_qty")
    @SysLogAnnotion("生产管理 计算产量")
    public ResponseEntity<JsonResultAo<BWoVo>> check_qty(@RequestBody BWoVo param) {
        BWoVo result = service.checkQty(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/inventory/calc")
    @SysLogAnnotion("生产管理 获取商品库用库存")
    public ResponseEntity<JsonResultAo<BWoVo>> calcInventory(@RequestBody BWoVo param) {
        BWoVo result = service.calcInventory(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/export")
    @SysLogAnnotion("导出")
    public void export(@RequestBody(required = false) BWoVo param, HttpServletResponse response) throws IOException {
        // 获取数据
        List<BWoVo> result = service.exportList(param);
        // 创建导出的数据列表
        List<BWoExportVo> exportDataList = new ArrayList<>();

        for (BWoVo bWoVo : result) {

            List<BWoMaterialVo> material_list = JSON.parseArray(bWoVo.getJson_material_list(), BWoMaterialVo.class);
            List<BWoProductVo> product_list = JSON.parseArray(bWoVo.getJson_product_list(), BWoProductVo.class);

            int materialSize = material_list != null ? material_list.size() : 0;
            int productSize = product_list != null ? product_list.size() : 0;
            int mergeRowCount = Math.max(materialSize, productSize);

            for (int i = 0; i < mergeRowCount; i++) {
                BWoMaterialVo material = material_list != null && i < materialSize ? material_list.get(i) : null;
                BWoProductVo product = product_list != null && i < productSize ? product_list.get(i) : null;
                BWoExportVo vo = new BWoExportVo(
                        bWoVo.getNo(),
                        bWoVo.getCode(),
                        bWoVo.getDelivery_order_code(),
                        bWoVo.getWc_warehouse_name(),
                        bWoVo.getStatus_name(),
                        bWoVo.getRouter_code(),
                        bWoVo.getRouter_name(),
                        product != null ? product.getGoods_name() : null,
                        product != null ? product.getSpec() : null,
                        product != null ? product.getWo_qty() : null,
//                        product != null ? product.getWo_qty().toString() : "0",
                        material != null ? material.getGoods_name() : null,
                        material != null ? material.getSpec() : null,
                        material != null ? material.getWo_router() : null,
                        material != null ? material.getWo_qty() : null,
                        bWoVo.getRemark(),
                        bWoVo.getC_name(),
                        bWoVo.getC_time(),
                        bWoVo.getU_name(),
                        bWoVo.getU_time(),
                        bWoVo.getE_name(),
                        bWoVo.getE_time(),
                        product != null ? product.getId().toString() : product_list.get(product_list.size() - 1).getId().toString(),
                        material != null ? material.getId().toString() : material_list.get(material_list.size() - 1).getId().toString()
                );
                exportDataList.add(vo);
            }
        }

        // 合并策略
        List<String> strategy_1 = exportDataList.stream().map(BWoExportVo::getCode).collect(Collectors.toList());
        List<BWoExportUtilVo> strategy_2 = exportDataList.stream().map(item -> new BWoExportUtilVo(item.getCode(), item.getProduct_id())).collect(Collectors.toList());
        List<BWoExportUtilVo> strategy_3 = exportDataList.stream().map(item -> new BWoExportUtilVo(item.getCode(), item.getMaterial_id())).collect(Collectors.toList());

        // 写sheet的时候注册相应的自定义合并单元格策略
        WriteSheet writeSheet = EasyExcel.writerSheet("生产订单").head(BWoExportVo.class)
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 0))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 1))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 2))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 3))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 4))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 5))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 6))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 7, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 8, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 9, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_3, 10, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_3, 11, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_3, 12, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_3, 13, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 14))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 15))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 16))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 17))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 18))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 19))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 20))
                .build();

        new EasyExcelUtil<>(BWoExportVo.class).exportExcel("生产订单(配方)" + DateTimeUtil.getDate(), exportDataList, response, writeSheet);

    }

}