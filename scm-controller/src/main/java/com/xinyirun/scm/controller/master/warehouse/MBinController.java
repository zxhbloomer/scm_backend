package com.xinyirun.scm.controller.master.warehouse;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.warehouse.MBinExportVo;
import com.xinyirun.scm.bean.system.vo.master.warehouse.MBinVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.master.warehouse.IMBinService;
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
 * 库位 前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
// @Api(tags = "库位")
@RestController
@RequestMapping(value = "/api/v1/bin")
public class MBinController extends SystemBaseController {

    @Autowired
    private IMBinService service;

    @Autowired
    private ISPagesService sPagesService;

    @Autowired
    private SPagesMapper sPagesMapper;

    @SysLogAnnotion("根据查询条件，获取库位信息")
    // @ApiOperation(value = "根据参数获取库位数信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MBinVo>>> list(@RequestBody(required = false) MBinVo searchCondition) {
        IPage<MBinVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取库位信息")
    // @ApiOperation(value = "根据参数获取库位数信息")
    @PostMapping("/combolist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MBinVo>>> comboList(@RequestBody(required = false) MBinVo searchCondition) {
        List<MBinVo> list = service.selecList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("系统参数数据更新保存")
    // @ApiOperation(value = "根据参数id，获取系统参数信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MBinVo>> insert(@RequestBody(required = false) MBinVo bean) {

        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("系统参数数据更新保存")
    // @ApiOperation(value = "根据参数id，获取系统参数信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MBinVo>> save(@RequestBody(required = false) MBinVo bean) {

        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("库位数据启用")
    // @ApiOperation(value = "根据参数id，启用数据")
    @PostMapping("/enabled")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MBinVo>> enabled(@RequestBody(required = false) MBinVo bean) {
        if(service.enabled(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"启用成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("库位数据停用")
    // @ApiOperation(value = "根据参数id，禁用数据")
    @PostMapping("/disabled")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MBinVo>> disabled(@RequestBody(required = false) MBinVo bean) {
        if(service.disabled(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"停用成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }


    @SysLogAnnotion("库位信息导出")
    @PostMapping("/exportall")
    public void exportAll(@RequestBody(required = false) MBinVo searchCondition, HttpServletResponse response) throws IOException {
        // 通过页面编码获取完整的页面对象（包含id字段）
        SPagesVo sPagesVo = sPagesMapper.selectByCode(PageCodeConstant.PAGE_BIN);
        
        try {
            // 设置导出处理状态为true
            sPagesService.updateExportProcessingTrue(sPagesVo);
            
            // 全部导出：直接调用selectExportList查询方法
            List<MBinExportVo> exportDataList = service.selectExportList(searchCondition);
            log.info("全部导出：查询到库位数据 {} 条", exportDataList.size());
            
            EasyExcelUtil<MBinExportVo> util = new EasyExcelUtil<>(MBinExportVo.class);
            String fileName = "库位信息导出_" + DateTimeUtil.dateTimeNow();
            util.exportExcel(fileName, "库位信息", exportDataList, response);
        } finally {
            // 无论成功失败都要恢复导出处理状态为false
            sPagesService.updateExportProcessingFalse(sPagesVo);
        }
    }

    @SysLogAnnotion("库位信息导出")
    @PostMapping("/export")
    public void export(@RequestBody(required = false) MBinVo searchCondition, HttpServletResponse response) throws IOException {
        // 通过页面编码获取完整的页面对象（包含id字段）
        SPagesVo sPagesVo = sPagesMapper.selectByCode(PageCodeConstant.PAGE_BIN);
        
        try {
            // 设置导出处理状态为true
            sPagesService.updateExportProcessingTrue(sPagesVo);
            
            // 选中导出：参数验证
            if (searchCondition == null || searchCondition.getIds() == null || searchCondition.getIds().length == 0) {
                throw new BusinessException("请选择要导出的库位记录");
            }
            
            // 选中导出：直接调用selectExportList查询方法
            List<MBinExportVo> exportDataList = service.selectExportList(searchCondition);
            log.info("选中导出：查询到库位数据 {} 条", exportDataList.size());
            
            EasyExcelUtil<MBinExportVo> util = new EasyExcelUtil<>(MBinExportVo.class);
            String fileName = "库位信息导出_" + DateTimeUtil.dateTimeNow();
            util.exportExcel(fileName, "库位信息", exportDataList, response);
        } finally {
            // 无论成功失败都要恢复导出处理状态为false
            sPagesService.updateExportProcessingFalse(sPagesVo);
        }
    }

    @SysLogAnnotion("库位数据逻辑删除复原")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) MBinVo searchCondition) {
        service.delete(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }
}
