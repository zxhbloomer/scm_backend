package com.xinyirun.scm.controller.master.warehouse;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.warhouse.*;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.common.annotations.DataChangeOperateAnnotation;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.master.warehouse.IMWarehouseService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.mapper.sys.pages.SPagesMapper;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
// @Api(tags = "仓库")
@RestController
@RequestMapping(value = "/api/v1/warehouse")
public class MWarehouseController extends SystemBaseController {

    @Autowired
    private IMWarehouseService service;

    @Autowired
    private ISPagesService sPagesService;

    @Autowired
    private SPagesMapper sPagesMapper;

    @SysLogAnnotion("根据查询条件，获取仓库信息")
    // @ApiOperation(value = "根据参数获取仓库数信息")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MWarehouseVo>>> pageList(@RequestBody(required = false) MWarehouseVo searchCondition) {
        IPage<MWarehouseVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取仓库信息")
    // @ApiOperation(value = "根据参数获取仓库数信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MWarehouseVo>>> list(@RequestBody(required = false) MWarehouseVo searchCondition) {
        if (null == searchCondition) {
            searchCondition = new MWarehouseVo();
        }
        List<MWarehouseVo> list = service.selectList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取仓库库区库位信息")
    // @ApiOperation(value = "根据参数获取仓库库区库位信息")
    @PostMapping("/ware_loc_bin")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MWarehouseLocationBinVo>> getWarehouseLocationBin(@RequestBody(required = false) MWarehouseLocationBinVo searchCondition) {
        MWarehouseLocationBinVo warehouseLocationBinVo = service.selectWarehouseLocationBin(searchCondition.getWarehouse_id());
        return ResponseEntity.ok().body(ResultUtil.OK(warehouseLocationBinVo));
    }

    @DataChangeOperateAnnotation(page_name = "仓库管理页面", value = "新增仓库")
    @SysLogAnnotion("仓库信息保存")
    // @ApiOperation(value = "仓库信息保存")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MWarehouseVo>> insert(@RequestBody(required = false) MWarehouseVo bean) {

        if (service.insert(bean).isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()), "更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @DataChangeOperateAnnotation(page_name = "仓库管理页面", value = "更新仓库")
    @SysLogAnnotion("仓库信息更新")
    // @ApiOperation(value = "仓库信息更新")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MWarehouseVo>> save(@RequestBody(required = false) MWarehouseVo bean) {

        if (service.update(bean).isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()), "更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @DataChangeOperateAnnotation(page_name = "仓库管理页面", value = "启用仓库")
    @SysLogAnnotion("根据选择的数据启用，部分数据")
    // @ApiOperation(value = "根据参数id，启用数据")
    @PostMapping("/enabled")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> enabled(@RequestBody(required = false) List<MWarehouseVo> searchConditionList) {
        service.enabledByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @DataChangeOperateAnnotation(page_name = "仓库管理页面", value = "停用仓库")
    @SysLogAnnotion("根据选择的数据禁用，部分数据")
    // @ApiOperation(value = "根据参数id，禁用数据")
    @PostMapping("/disabled")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> disabled(@RequestBody(required = false) List<MWarehouseVo> searchConditionList) {
        service.disSabledByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @DataChangeOperateAnnotation(page_name = "仓库管理页面", value = "启用/停用仓库")
    @SysLogAnnotion("根据选择的数据启用/停用，部分数据")
    // @ApiOperation(value = "根据参数id，启用数据")
    @PostMapping("/enable")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> enable(@RequestBody(required = false) List<MWarehouseVo> searchConditionList) {
        service.enableByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("仓库信息导出")
    @PostMapping("/exportall")
    public void exportAll(@RequestBody(required = false) MWarehouseVo searchCondition, HttpServletResponse response) throws IOException {
        // 通过页面编码获取完整的页面对象（包含id字段）
        SPagesVo sPagesVo = sPagesMapper.selectByCode(PageCodeConstant.PAGE_WAREHOUSE);
        
        try {
            // 设置导出处理状态为true
            sPagesService.updateExportProcessingTrue(sPagesVo);
            
            // 全部导出：直接调用selectExportList查询方法
            List<MWarehouseExportVo> exportDataList = service.selectExportList(searchCondition);
            log.info("全部导出：查询到仓库数据 {} 条", exportDataList.size());
            
            EasyExcelUtil<MWarehouseExportVo> util = new EasyExcelUtil<>(MWarehouseExportVo.class);
            String fileName = "仓库信息导出_" + DateTimeUtil.dateTimeNow();
            util.exportExcel(fileName, "仓库信息", exportDataList, response);
        } finally {
            // 无论成功失败都要恢复导出处理状态为false
            sPagesService.updateExportProcessingFalse(sPagesVo);
        }
    }

    @SysLogAnnotion("仓库信息导出")
    @PostMapping("/export")
    public void export(@RequestBody(required = false) MWarehouseVo searchCondition, HttpServletResponse response) throws IOException {
        // 通过页面编码获取完整的页面对象（包含id字段）
        SPagesVo sPagesVo = sPagesMapper.selectByCode(PageCodeConstant.PAGE_WAREHOUSE);
        
        try {
            // 设置导出处理状态为true
            sPagesService.updateExportProcessingTrue(sPagesVo);
            
            // 选中导出：参数验证
            if (searchCondition == null || searchCondition.getIds() == null || searchCondition.getIds().length == 0) {
                throw new BusinessException("请选择要导出的仓库记录");
            }
            
            // 选中导出：直接调用selectExportList查询方法
            List<MWarehouseExportVo> exportDataList = service.selectExportList(searchCondition);
            log.info("选中导出：查询到仓库数据 {} 条", exportDataList.size());
            
            EasyExcelUtil<MWarehouseExportVo> util = new EasyExcelUtil<>(MWarehouseExportVo.class);
            String fileName = "仓库信息导出_" + DateTimeUtil.dateTimeNow();
            util.exportExcel(fileName, "仓库信息", exportDataList, response);
        } finally {
            // 无论成功失败都要恢复导出处理状态为false
            sPagesService.updateExportProcessingFalse(sPagesVo);
        }
    }

    @SysLogAnnotion("获取穿梭框数据")
    @PostMapping("/transfer/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MWarehouseGroupTransferVo>> getWarehouseGroupTransferList(@RequestBody(required = false) MWGroupTransferVo searchCondition) {
        MWarehouseGroupTransferVo vo = service.getWarehouseStaffTransferList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("保存穿梭框数据，仓库设置")
    @PostMapping("/transfer/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> setWarehouseGroupTransferList(@RequestBody(required = false) MWGroupTransferVo bean) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.setWarehouseGroupTransfer(bean)));
    }

    @SysLogAnnotion("获取穿梭框数据")
    @PostMapping("/staff/transfer/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MWarehouseStaffTransferVo>> getWarehouseStaffTransferList(@RequestBody(required = false) MWStaffTransferVo searchCondition) {
        MWarehouseStaffTransferVo vo = service.getWarehouseStaffTransferList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("保存穿梭框数据，仓库设置")
    @PostMapping("/staff/transfer/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> setWarehouseStaffTransferList(@RequestBody(required = false) MWStaffTransferVo bean) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.setWarehouseStaffTransfer(bean)));
    }

    @DataChangeOperateAnnotation(page_name = "仓库管理页面", value = "删除仓库")
    @SysLogAnnotion("仓库数据逻辑删除复原")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) MWarehouseVo searchCondition) {
        service.delete(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

}
