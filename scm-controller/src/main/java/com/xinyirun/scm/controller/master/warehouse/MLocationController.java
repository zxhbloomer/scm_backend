package com.xinyirun.scm.controller.master.warehouse;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MLocationExportVo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MLocationVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.mapper.sys.pages.SPagesMapper;
import com.xinyirun.scm.core.system.service.master.warehouse.IMLocationService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
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
 *  前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
// @Api(tags = "库区")
@RestController
@RequestMapping(value = "/api/v1/location")
public class MLocationController extends SystemBaseController {

    @Autowired
    private IMLocationService service;
    
    @Autowired
    private SPagesMapper sPagesMapper;
    
    @Autowired
    private ISPagesService sPagesService;

    @SysLogAnnotion("根据查询条件，获取库区信息")
    // @ApiOperation(value = "根据参数获取库区数信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MLocationVo>>> list(@RequestBody(required = false) MLocationVo searchCondition) {
        IPage<MLocationVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取库区信息")
    // @ApiOperation(value = "根据参数获取库区数信息")
    @PostMapping("/combolist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MLocationVo>>> comboList(@RequestBody(required = false) MLocationVo searchCondition) {
        List<MLocationVo> list = service.selectList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("新增库区")
    // @ApiOperation(value = "新增库区")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MLocationVo>> insert(@RequestBody(required = false) MLocationVo bean) {

        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("库区数据更新保存")
    // @ApiOperation(value = "库区数据更新保存")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MLocationVo>> save(@RequestBody(required = false) MLocationVo bean) {

        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("根据选择的数据启用，部分数据")
    // @ApiOperation(value = "根据参数id，启用数据")
    @PostMapping("/enabled")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> enabled(@RequestBody(required = false) List<MLocationVo> searchConditionList) {
        service.enabledByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据禁用，部分数据")
    // @ApiOperation(value = "根据参数id，禁用数据")
    @PostMapping("/disabled")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> disabled(@RequestBody(required = false) List<MLocationVo> searchConditionList) {
        service.disSabledByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据启用/停用，部分数据")
    // @ApiOperation(value = "根据参数id，启用数据")
    @PostMapping("/enable")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> enable(@RequestBody(required = false) List<MLocationVo> searchConditionList) {
        service.enableByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("库区信息导出(全部)")
    @PostMapping("/exportall")
    public void exportAll(@RequestBody(required = false) MLocationVo searchCondition, HttpServletResponse response) throws IOException {
        // 通过页面编码获取完整的页面对象（包含id字段）
        SPagesVo sPagesVo = sPagesMapper.selectByCode(PageCodeConstant.PAGE_LOCATION);
        
        try {
            // 设置导出处理状态为true
            sPagesService.updateExportProcessingTrue(sPagesVo);
            
            // 全部导出：直接调用selectExportList查询方法
            List<MLocationExportVo> exportDataList = service.selectExportList(searchCondition);
            log.info("全部导出：查询到库区数据 {} 条", exportDataList.size());
            
            EasyExcelUtil<MLocationExportVo> util = new EasyExcelUtil<>(MLocationExportVo.class);
            String fileName = "库区信息导出_" + DateTimeUtil.dateTimeNow();
            util.exportExcel(fileName, "库区信息", exportDataList, response);
        } finally {
            // 无论成功失败都要恢复导出处理状态为false
            sPagesService.updateExportProcessingFalse(sPagesVo);
        }
    }

    @SysLogAnnotion("库区信息导出")
    @PostMapping("/export")
    public void export(@RequestBody(required = false) MLocationVo searchCondition, HttpServletResponse response) throws IOException {
        // 通过页面编码获取完整的页面对象（包含id字段）
        SPagesVo sPagesVo = sPagesMapper.selectByCode(PageCodeConstant.PAGE_LOCATION);
        
        try {
            // 设置导出处理状态为true
            sPagesService.updateExportProcessingTrue(sPagesVo);
            
            // 选中导出：参数验证
            if (searchCondition == null || searchCondition.getIds() == null || searchCondition.getIds().length == 0) {
                throw new BusinessException("请选择要导出的库区记录");
            }
            
            // 选中导出：直接调用selectExportList查询方法
            List<MLocationExportVo> exportDataList = service.selectExportList(searchCondition);
            log.info("选中导出：查询到库区数据 {} 条", exportDataList.size());
            
            EasyExcelUtil<MLocationExportVo> util = new EasyExcelUtil<>(MLocationExportVo.class);
            String fileName = "库区信息导出_" + DateTimeUtil.dateTimeNow();
            util.exportExcel(fileName, "库区信息", exportDataList, response);
        } finally {
            // 无论成功失败都要恢复导出处理状态为false
            sPagesService.updateExportProcessingFalse(sPagesVo);
        }
    }

    @SysLogAnnotion("库区数据逻辑删除复原")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) MLocationVo searchCondition) {
        service.delete(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

}
