package com.xinyirun.scm.controller.business.pp;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.pp.BPpExportVo;
import com.xinyirun.scm.bean.system.vo.business.pp.BPpMaterialVo;
import com.xinyirun.scm.bean.system.vo.business.pp.BPpProductVo;
import com.xinyirun.scm.bean.system.vo.business.pp.BPpVo;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoVo;
import com.xinyirun.scm.bean.system.vo.business.wo.*;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.pp.IBPpService;
import com.xinyirun.scm.excel.export.CustomMergeStrategy;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: xtj
 * @Description:
 * @CreateTime : 2024/4/19 9:03
 */

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/pp")
public class BPpController extends SystemBaseController {

    @Autowired
    private IBPpService ibPpService;

    /**
     * 生产计划管理 列表
     */
    @PostMapping("/pagelist")
    @SysLogAnnotion("生产计划 列表")
    public ResponseEntity<JsonResultAo<IPage<BPpVo>>> selectPageList(@RequestBody BPpVo bPpVo) {
        IPage<BPpVo> result = ibPpService.selectPageList(bPpVo);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/list/sum")
    @SysLogAnnotion("生产计划 统计")
    public ResponseEntity<JsonResultAo<BPpVo>> selectListSum(@RequestBody BPpVo param) {
        BPpVo result = ibPpService.selectListSum(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }


    /**
     * 生产计划管理 新增
     */
    @PostMapping("/insert")
    @SysLogAnnotion("生产计划 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BPpVo>> insert(@RequestBody BPpVo bPpVo) {
        InsertResultAo<BPpVo> resultAo = ibPpService.insert(bPpVo);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(resultAo.getData(),"新增成功"));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }

    /**
     * 生产计划管理 新增
     */
    @PostMapping("/update")
    @SysLogAnnotion("生产计划 修改")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BPpVo>> update(@RequestBody BPpVo bPpVo) {
        UpdateResultAo<BPpVo> resultAo = ibPpService.updateParam(bPpVo);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(resultAo.getData(),"新增成功"));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }

    @PostMapping("/check")
    @SysLogAnnotion("生产计划 公式校验")
    public ResponseEntity<JsonResultAo<List<Map<String, String>>>> check(@RequestBody BPpVo bPpVo) {
        List<Map<String, String>> result = ibPpService.check(bPpVo);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/check_qty")
    @SysLogAnnotion("生产计划 计算产量")
    public ResponseEntity<JsonResultAo<BPpVo>> check_qty(@RequestBody BPpVo bPpVo) {
        BPpVo result = ibPpService.checkQty(bPpVo);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/submit")
    @SysLogAnnotion("生产计划 提交")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> submit(@RequestBody List<BPpVo> param) {
        ibPpService.submit(param);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/cancel")
    @SysLogAnnotion("生产管理 作废")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> cancel(@RequestBody BPpVo bPpVo) {
        ibPpService.cancel(bPpVo);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/audit")
    @SysLogAnnotion("生产计划 审核通过")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> audit(@RequestBody List<BPpVo> param) {
        ibPpService.audit(param);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/reject")
    @SysLogAnnotion("生产计划 审核驳回")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> reject(@RequestBody List<BPpVo> param) {
        ibPpService.reject(param);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/finish")
    @SysLogAnnotion("生产计划 审核通过")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> finish(@RequestBody List<BPpVo> param) {
        ibPpService.finish(param);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }


    @PostMapping("/getDetail")
    @SysLogAnnotion("生产计划 获取明细")
    public ResponseEntity<JsonResultAo<BPpVo>> getDetail(@RequestBody BPpVo bPpVo) {
        BPpVo detail = ibPpService.getDetail(bPpVo.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(detail));
    }

    @PostMapping("/todo/count")
    @SysLogAnnotion("生产计划 查询待办数量")
    public ResponseEntity<JsonResultAo<Integer>> selectTodoCount(@RequestBody BPpVo bPpVo) {
        Integer result = ibPpService.selectTodoCount(bPpVo);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }


    @PostMapping("/export")
    @SysLogAnnotion("导出")
    public void export(@RequestBody(required = false) BPpVo bPpVo, HttpServletResponse response) throws IOException {
        List<BPpVo> bPpVos = ibPpService.exportList(bPpVo);

        // 创建导出的数据列表
        List<BPpExportVo> exportDataList = new ArrayList<>();

        for (BPpVo ppVo : bPpVos) {

            List<BPpMaterialVo> material_list = JSON.parseArray(ppVo.getJson_material_list(), BPpMaterialVo.class);
            List<BPpProductVo> product_list = JSON.parseArray(ppVo.getJson_product_list(), BPpProductVo.class);

            int materialSize = material_list != null ? material_list.size() : 0;
            int productSize = product_list != null ?  product_list.size() : 0;
            int mergeRowCount = Math.max(materialSize, productSize);

            for (int i = 0; i < mergeRowCount; i++) {

                BPpMaterialVo material = material_list != null && i < materialSize ?  material_list.get(i) : null;
                BPpProductVo product = product_list != null && i < productSize ?product_list.get(i) : null;

                BPpExportVo bPpExportVo = new BPpExportVo();
                bPpExportVo.setNo(ppVo.getNo());
                bPpExportVo.setCode(ppVo.getCode());
                bPpExportVo.setStatus_name(ppVo.getStatus_name());
                bPpExportVo.setBwo_sum(ppVo.getBwo_sum());
                bPpExportVo.setRelease_order_code(ppVo.getRelease_order_code());
                bPpExportVo.setRouter_code(ppVo.getRouter_code());
                bPpExportVo.setRouter_name(ppVo.getRouter_name());
                bPpExportVo.setP_sku_name(product != null ? product.getGoods_name() : null);
                bPpExportVo.setP_spec(product != null ? product.getSpec() : null);
                bPpExportVo.setP_qty(product != null ? product.getQty() : BigDecimal.ZERO);
                bPpExportVo.setP_actual_qty(product != null ? product.getWo_qty() : BigDecimal.ZERO);
                bPpExportVo.setP_actual_wait(product != null ? product.getWo_unclaimed() : BigDecimal.ZERO);
                bPpExportVo.setM_sku_name(material != null ? material.getGoods_name() :null);
                bPpExportVo.setM_spec(material != null ? material.getSpec() : null);
                bPpExportVo.setM_router(material != null ? material.getPp_router() : null);
                bPpExportVo.setM_qty(material != null ? material.getQty() : BigDecimal.ZERO);
                bPpExportVo.setM_actual_qty(material != null ? material.getWo_qty() : BigDecimal.ZERO);
                bPpExportVo.setM_actual_wait(material != null ? material.getWo_unclaimed() : BigDecimal.ZERO);
                bPpExportVo.setRemark(ppVo.getRemark());
                bPpExportVo.setC_name(ppVo.getC_name());
                bPpExportVo.setC_time(ppVo.getC_time());
                bPpExportVo.setU_name(ppVo.getU_name());
                bPpExportVo.setU_time(ppVo.getU_time());
                bPpExportVo.setAudit_name(ppVo.getAudit_name());
                bPpExportVo.setAudit_time(ppVo.getAudit_time());
                bPpExportVo.setWarehouse_name(ppVo.getWarehouse_name());
                bPpExportVo.setMaterial_id(material != null ? material.getId().toString() : material_list.get(material_list.size() - 1).getId().toString());
                bPpExportVo.setProduct_id(product != null ? product.getId().toString() : product_list.get(product_list.size() - 1).getId().toString());

                exportDataList.add(bPpExportVo);
            }
        }
        // 合并策略
        List<String> strategy_1 = exportDataList.stream().map(BPpExportVo::getCode).collect(Collectors.toList());
        List<BWoExportUtilVo> strategy_2 = exportDataList.stream().map(item -> new BWoExportUtilVo(item.getCode(), item.getProduct_id())).collect(Collectors.toList());
        List<BWoExportUtilVo> strategy_3 = exportDataList.stream().map(item -> new BWoExportUtilVo(item.getCode(), item.getMaterial_id())).collect(Collectors.toList());


        // 写sheet的时候注册相应的自定义合并单元格策略
        WriteSheet writeSheet = EasyExcel.writerSheet("生产计划").head(BPpExportVo.class)
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 0))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 1))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 2))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 3))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 4))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 5))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 6))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 7))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 8, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 9, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 10, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 11, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 12, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_3, 13, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_3, 14, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_3, 15, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_3, 16, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_3, 17, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_3, 18, "1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 19))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 20))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 21))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 22))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 23))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 24))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 25))
                .build();

        new EasyExcelUtil<>(BPpExportVo.class).exportExcel("生产计划" + DateTimeUtil.getDate(), exportDataList, response, writeSheet);

    }
}
