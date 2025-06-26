package com.xinyirun.scm.controller.business.rtwo;


import cn.idev.excel.EasyExcel;
import cn.idev.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoExportVo;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoMaterialVo;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoProductVo;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoExportUtilVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.rtwo.IBRtWoService;
import com.xinyirun.scm.excel.export.CustomMergeStrategy;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
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
@RequestMapping("/api/v1/rt/wo")
public class BRtWoController extends SystemBaseController {

    @Autowired
    private IBRtWoService service;

    @PostMapping("/insert")
    @SysLogAnnotion("生产管理 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BRtWoVo>> insert(@RequestBody BRtWoVo param) {
        InsertResultAo<BRtWoVo> rtn = service.insert(param);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"新增成功"));
        } else {
            throw new InsertErrorException("新增失败");
        }
    }

    @PostMapping("/check")
    @SysLogAnnotion("生产管理 公式校验")
    public ResponseEntity<JsonResultAo<List<Map<String, String>>>> check(@RequestBody BRtWoVo param) {
        List<Map<String, String>> result = service.check(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/update")
    @SysLogAnnotion("生产管理 修改")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BRtWoVo>> update(@RequestBody BRtWoVo param) {
        UpdateResultAo<BRtWoVo> rtn = service.updateParam(param);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"更新成功"));
        } else {
            throw new UpdateErrorException("更新失败");
        }
    }

    @PostMapping("/submit")
    @SysLogAnnotion("生产管理 提交")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> submit(@RequestBody List<BRtWoVo> param) {
        service.submit(param);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/pagelist")
    @SysLogAnnotion("生产管理 列表查询")
    public ResponseEntity<JsonResultAo<IPage<BRtWoVo>>> selectPageList(@RequestBody BRtWoVo param) {
        IPage<BRtWoVo> result = service.selectPageList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/todo/count")
    @SysLogAnnotion("生产管理 查询待办数量")
    public ResponseEntity<JsonResultAo<Integer>> selectTodoCount(@RequestBody BRtWoVo param) {
        Integer result = service.selectTodoCount(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/getDetail")
    @SysLogAnnotion("生产管理 新增")
    public ResponseEntity<JsonResultAo<BRtWoVo>> getDetail(@RequestBody BRtWoVo param) {
        BRtWoVo detail = service.getDetail(param.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(detail));
    }

    @PostMapping("/cancel")
    @SysLogAnnotion("生产管理 作废")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> submit(@RequestBody BRtWoVo param) {
        service.cancel(param);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/audit")
    @SysLogAnnotion("生产管理 审核通过")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> audit(@RequestBody List<BRtWoVo> param) {
        service.audit(param);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/reject")
    @SysLogAnnotion("生产管理 审核驳回")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> reject(@RequestBody List<BRtWoVo> param) {
        service.reject(param);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/check_qty")
    @SysLogAnnotion("生产管理 计算产量")
    public ResponseEntity<JsonResultAo<BRtWoVo>> check_qty(@RequestBody BRtWoVo param) {
        BRtWoVo result = service.checkQty(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/inventory/calc")
    @SysLogAnnotion("生产管理 获取商品库用库存")
    public ResponseEntity<JsonResultAo<BRtWoVo>> calcInventory(@RequestBody BRtWoVo param) {
        BRtWoVo result = service.calcInventory(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/export")
    @SysLogAnnotion("导出")
    public void export(@RequestBody(required = false) BRtWoVo param, HttpServletResponse response) throws IOException {
        List<BRtWoVo> result = service.selectExportList(param);

        // 创建导出的数据列表
        List<BRtWoExportVo> exportDataList = new ArrayList<>();

        for (BRtWoVo bRtWoVo : result) {
            List<BRtWoProductVo> productList = JSON.parseArray(bRtWoVo.getJson_product_list(), BRtWoProductVo.class);
            List<BRtWoMaterialVo> materialList = JSON.parseArray(bRtWoVo.getJson_material_list(), BRtWoMaterialVo.class);
            List<BRtWoProductVo> coproductList = JSON.parseArray(bRtWoVo.getJson_coproduct_list(), BRtWoProductVo.class);

            BRtWoProductVo product = productList.get(0);

            // 原材料 和 副产品数量大小比较, 产成品 肯定只有一个
            int materialSize = materialList != null  ? materialList.size() : 0;
            int coproductSize = coproductList != null ? coproductList.size() : 0;
            int mergeRowCount = Math.max(materialSize, coproductSize);

            for (int i = 0; i < mergeRowCount; i++) {
                BRtWoMaterialVo material = materialList != null && i < materialSize ? materialList.get(i) : null;
                BRtWoProductVo coproduct = coproductList != null && i < coproductSize ? coproductList.get(i) : null;

                BRtWoExportVo vo = new BRtWoExportVo(
                        bRtWoVo.getNo(),
                        bRtWoVo.getCode(),
                        bRtWoVo.getDelivery_order_code(),
                        bRtWoVo.getWc_warehouse_name(),
                        bRtWoVo.getStatus_name(),
                        product != null ? (i == 0 ? product.getGoods_name(): null) : null,
                        product != null ? (i == 0 ? product.getSpec() : null) : null,
                        product != null ? (i == 0 ? product.getWo_qty() : null) : null,
                        coproduct != null ? coproduct.getGoods_name() : null,
                        coproduct != null ? coproduct.getSpec() : null,
                        coproduct != null ? coproduct.getWo_qty() : null,
                        material != null ? material.getGoods_name() : null,
                        material != null ? material.getSpec() : null,
                        material != null ? material.getWo_qty() : null,
                        bRtWoVo.getRemark(),
                        bRtWoVo.getC_name(),
                        bRtWoVo.getC_time(),
                        bRtWoVo.getU_name(),
                        bRtWoVo.getU_time(),
                        bRtWoVo.getE_name(),
                        bRtWoVo.getE_time(),
                        coproduct != null ? coproduct.getId().toString() : coproductList.get(coproductList.size() - 1).getId().toString(),
                        material != null ? material.getId().toString() : materialList.get(materialList.size() - 1).getId().toString()
                );
                exportDataList.add(vo);
            }
        }
        List<String> strategy_1 = exportDataList.stream().map(BRtWoExportVo::getCode).collect(Collectors.toList());
        List<BWoExportUtilVo> strategy_2 = exportDataList.stream().map(item -> new BWoExportUtilVo(item.getCode(), item.getCoproduct_id())).collect(Collectors.toList());
        List<BWoExportUtilVo> strategy_3 = exportDataList.stream().map(item -> new BWoExportUtilVo(item.getCode(), item.getMaterial_id())).collect(Collectors.toList());

        // 写sheet的时候注册相应的自定义合并单元格策略
        WriteSheet writeSheet = EasyExcel.writerSheet("生产订单").head(BRtWoExportVo.class)
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
        new EasyExcelUtil<>(BRtWoExportVo.class).exportExcel("生产订单(配比)" + DateTimeUtil.getDate(), exportDataList, response, writeSheet);
    }

}
