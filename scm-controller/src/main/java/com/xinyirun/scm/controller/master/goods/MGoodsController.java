package com.xinyirun.scm.controller.master.goods;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsExportVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.common.annotations.DataChangeOperateAnnotation;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.master.goods.IMGoodsService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.mapper.sys.pages.SPagesMapper;
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
// @Api(tags = "物料")
@RestController
@RequestMapping(value = "/api/v1/goods")
public class MGoodsController extends SystemBaseController {

    @Autowired
    private IMGoodsService service;

    @Autowired
    private ISPagesService sPagesService;

    @Autowired
    private SPagesMapper sPagesMapper;

    @SysLogAnnotion("根据查询条件，获取物料信息")
    // @ApiOperation(value = "根据参数获取物料信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MGoodsVo>>> list(@RequestBody(required = false) MGoodsVo searchCondition) {
        IPage<MGoodsVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("系统参数数据更新保存")
    // @ApiOperation(value = "根据参数id，获取系统参数信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MGoodsVo>> insert(@RequestBody(required = false) MGoodsVo bean) {

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
    public ResponseEntity<JsonResultAo<MGoodsVo>> save(@RequestBody(required = false) MGoodsVo bean) {

        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @DataChangeOperateAnnotation(page_name = "物料管理页面", value = "启用物料")
    @SysLogAnnotion("根据选择的数据启用，部分数据")
    // @ApiOperation(value = "根据参数id，启用数据")
    @PostMapping("/enabled")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> enabled(@RequestBody(required = false) List<MGoodsVo> searchConditionList) {
        service.enabledByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @DataChangeOperateAnnotation(page_name = "物料管理页面", value = "停用物料")
    @SysLogAnnotion("根据选择的数据禁用，部分数据")
    // @ApiOperation(value = "根据参数id，禁用数据")
    @PostMapping("/disabled")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> disabled(@RequestBody(required = false) List<MGoodsVo> searchConditionList) {
        service.disSabledByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @DataChangeOperateAnnotation(page_name = "物料管理页面", value = "启用/停用物料")
    @SysLogAnnotion("根据选择的数据启用/停用，部分数据")
    // @ApiOperation(value = "根据参数id，启用数据")
    @PostMapping("/enable")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> enable(@RequestBody(required = false) List<MGoodsVo> searchConditionList) {
        service.enableByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @DataChangeOperateAnnotation(page_name = "物料管理页面", value = "删除物料")
    @SysLogAnnotion("物料数据逻辑删除复原")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) MGoodsVo searchCondition) {
        service.delete(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("物料信息导出")
    @PostMapping("/exportall")
    public void exportAll(@RequestBody(required = false) MGoodsVo searchCondition, HttpServletResponse response) throws IOException {
        // 通过页面编码获取完整的页面对象（包含id字段）
        SPagesVo sPagesVo = sPagesMapper.selectByCode(PageCodeConstant.PAGE_GOODS);
        
        try {
            // 设置导出处理状态为true
            sPagesService.updateExportProcessingTrue(sPagesVo);
            
            // 全部导出：直接调用export方法查询
            List<MGoodsExportVo> exportDataList = service.export(searchCondition);
            log.info("全部导出：查询到物料数据 {} 条", exportDataList.size());
            
            EasyExcelUtil<MGoodsExportVo> util = new EasyExcelUtil<>(MGoodsExportVo.class);
            String fileName = "物料信息导出_" + DateTimeUtil.dateTimeNow();
            util.exportExcel(fileName, "物料信息", exportDataList, response);
        } finally {
            // 无论成功失败都要恢复导出处理状态为false
            sPagesService.updateExportProcessingFalse(sPagesVo);
        }
    }

    @SysLogAnnotion("物料信息导出")
    @PostMapping("/export")
    public void export(@RequestBody(required = false) MGoodsVo searchCondition, HttpServletResponse response) throws IOException {
        // 通过页面编码获取完整的页面对象（包含id字段）
        SPagesVo sPagesVo = sPagesMapper.selectByCode(PageCodeConstant.PAGE_GOODS);
        
        try {
            // 设置导出处理状态为true
            sPagesService.updateExportProcessingTrue(sPagesVo);
            
            // 选中导出：参数验证
            if (searchCondition == null || searchCondition.getIds() == null || searchCondition.getIds().length == 0) {
                throw new BusinessException("请选择要导出的物料记录");
            }
            
            // 选中导出：直接调用export方法查询
            List<MGoodsExportVo> exportDataList = service.export(searchCondition);
            log.info("选中导出：查询到物料数据 {} 条", exportDataList.size());
            
            EasyExcelUtil<MGoodsExportVo> util = new EasyExcelUtil<>(MGoodsExportVo.class);
            String fileName = "物料信息导出_" + DateTimeUtil.dateTimeNow();
            util.exportExcel(fileName, "物料信息", exportDataList, response);
        } finally {
            // 无论成功失败都要恢复导出处理状态为false
            sPagesService.updateExportProcessingFalse(sPagesVo);
        }
    }
}
