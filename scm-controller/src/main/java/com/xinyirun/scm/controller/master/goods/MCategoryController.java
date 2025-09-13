package com.xinyirun.scm.controller.master.goods;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.category.MCategoryExportVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MCategoryVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.common.annotations.DataChangeOperateAnnotation;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.master.goods.IMCategoryService;
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
 *  物料类别 前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/category")
public class MCategoryController extends SystemBaseController {

    @Autowired
    private IMCategoryService service;

    @Autowired
    private ISPagesService sPagesService;

    @Autowired
    private SPagesMapper sPagesMapper;

    @SysLogAnnotion("根据查询条件，获取物料类别信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MCategoryVo>>> list(@RequestBody(required = false) MCategoryVo searchCondition) {
        IPage<MCategoryVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("类别数据新增保存")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MCategoryVo>> insert(@RequestBody(required = false) MCategoryVo bean) {

        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("类别数据更新保存")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MCategoryVo>> save(@RequestBody(required = false) MCategoryVo bean) {

        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("根据选择的数据启用，部分数据")
    @PostMapping("/enabled")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MCategoryVo>> enabled(@RequestBody(required = false) MCategoryVo categoryVo) {
        MCategoryVo updatedCategory = service.enabledById(categoryVo);
        return ResponseEntity.ok().body(ResultUtil.OK(updatedCategory, "启用成功"));
    }

    @SysLogAnnotion("根据选择的数据禁用，部分数据")
    @PostMapping("/disabled")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MCategoryVo>> disabled(@RequestBody(required = false) MCategoryVo categoryVo) {
        MCategoryVo updatedCategory = service.disabledById(categoryVo);
        return ResponseEntity.ok().body(ResultUtil.OK(updatedCategory, "停用成功"));
    }


    @SysLogAnnotion("类别信息导出")
    @PostMapping("/exportall")
    public void exportAll(@RequestBody(required = false) MCategoryVo searchCondition, HttpServletResponse response) throws IOException {
        // 通过页面编码获取完整的页面对象（包含id字段）
        SPagesVo sPagesVo = sPagesMapper.selectByCode(PageCodeConstant.PAGE_CATEGORY);
        
        try {
            // 设置导出处理状态为true
            sPagesService.updateExportProcessingTrue(sPagesVo);
            
            // 全部导出：直接调用export方法查询
            List<MCategoryExportVo> exportDataList = service.export(searchCondition);
            log.info("全部导出：查询到类别数据 {} 条", exportDataList.size());
            
            EasyExcelUtil<MCategoryExportVo> util = new EasyExcelUtil<>(MCategoryExportVo.class);
            String fileName = "类别信息导出_" + DateTimeUtil.dateTimeNow();
            util.exportExcel(fileName, "类别信息", exportDataList, response);
        } finally {
            // 无论成功失败都要恢复导出处理状态为false
            sPagesService.updateExportProcessingFalse(sPagesVo);
        }
    }

    @SysLogAnnotion("类别信息导出")
    @PostMapping("/export")
    public void export(@RequestBody(required = false) MCategoryVo searchCondition, HttpServletResponse response) throws IOException {
        // 通过页面编码获取完整的页面对象（包含id字段）
        SPagesVo sPagesVo = sPagesMapper.selectByCode(PageCodeConstant.PAGE_CATEGORY);
        
        try {
            // 设置导出处理状态为true
            sPagesService.updateExportProcessingTrue(sPagesVo);
            
            // 选中导出：参数验证
            if (searchCondition == null || searchCondition.getIds() == null || searchCondition.getIds().length == 0) {
                throw new BusinessException("请选择要导出的类别记录");
            }
            
            // 选中导出：直接调用export方法查询
            List<MCategoryExportVo> exportDataList = service.export(searchCondition);
            log.info("选中导出：查询到类别数据 {} 条", exportDataList.size());
            
            EasyExcelUtil<MCategoryExportVo> util = new EasyExcelUtil<>(MCategoryExportVo.class);
            String fileName = "类别信息导出_" + DateTimeUtil.dateTimeNow();
            util.exportExcel(fileName, "类别信息", exportDataList, response);
        } finally {
            // 无论成功失败都要恢复导出处理状态为false
            sPagesService.updateExportProcessingFalse(sPagesVo);
        }
    }

    @DataChangeOperateAnnotation(page_name = "类别管理页面", value = "删除类别")
    @SysLogAnnotion("类别数据逻辑删除复原")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) MCategoryVo searchCondition) {
        service.delete(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }
}
