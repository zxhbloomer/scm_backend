package com.xinyirun.scm.controller.master.goods;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.goods.*;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.master.goods.IMGoodsSpecService;
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
 *  前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
// @Api(tags = "物料规格")
@RestController
@RequestMapping(value = "/api/v1/goodsspec")
public class MGoodsSpecController extends SystemBaseController {

    @Autowired
    private IMGoodsSpecService service;

    @Autowired
    private ISPagesService sPagesService;

    @Autowired
    private SPagesMapper sPagesMapper;

    @SysLogAnnotion("根据查询条件，获取物料规格信息")
    // @ApiOperation(value = "根据参数获取物料规格信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MGoodsSpecVo>>> list(@RequestBody(required = false) MGoodsSpecVo searchCondition) {
        IPage<MGoodsSpecVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取物料规格信息")
    // @ApiOperation(value = "根据参数获取物料规格信息")
    @PostMapping("/search_by_goods")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MGoodsSpecVo>>> selectListByGoodsId(@RequestBody(required = false) MGoodsSpecVo searchCondition) {
        List<MGoodsSpecVo> list = service.selectListByGoodsId(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取物料规格树状信息")
    // @ApiOperation(value = "根据参数获取物料规格树状信息")
    @PostMapping("/leftList")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MGoodsSpecLeftVo>>> leftList(@RequestBody(required = false) MGoodsSpecLeftVo searchCondition) {
        List<MGoodsSpecLeftVo> list = service.selectLeft(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("物料规格数据更新保存")
    // @ApiOperation(value = "根据参数物料规格数据更新保存")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MGoodsSpecVo>> insert(@RequestBody(required = false) MGoodsSpecVo bean) {

        if(service.insert(bean).isSuccess()){
//            unitConverService.insert(bean.getUnitList());
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
    public ResponseEntity<JsonResultAo<MGoodsSpecVo>> save(@RequestBody(required = false) MGoodsSpecVo bean) {

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
    public ResponseEntity<JsonResultAo<MGoodsSpecVo>> enabled(@RequestBody(required = false) MGoodsSpecVo specVo) {
        MGoodsSpecVo updatedSpec = service.enabledById(specVo);
        return ResponseEntity.ok().body(ResultUtil.OK(updatedSpec, "启用成功"));
    }

    @SysLogAnnotion("根据选择的数据禁用，部分数据")
    // @ApiOperation(value = "根据参数id，禁用数据")
    @PostMapping("/disabled")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MGoodsSpecVo>> disabled(@RequestBody(required = false) MGoodsSpecVo specVo) {
        MGoodsSpecVo updatedSpec = service.disabledById(specVo);
        return ResponseEntity.ok().body(ResultUtil.OK(updatedSpec, "停用成功"));
    }


    @SysLogAnnotion("规格信息导出")
    @PostMapping("/exportall")
    public void exportAll(@RequestBody(required = false) MGoodsSpecVo searchCondition, HttpServletResponse response) throws IOException {
        // 通过页面编码获取完整的页面对象（包含id字段）
        SPagesVo sPagesVo = sPagesMapper.selectByCode(PageCodeConstant.PAGE_GOODS_SPEC);
        
        try {
            // 设置导出处理状态为true
            sPagesService.updateExportProcessingTrue(sPagesVo);
            
            // 全部导出：直接调用selectExportList查询方法
            List<MGoodsSpecExportVo> exportDataList = service.selectExportList(searchCondition);
            log.info("全部导出：查询到规格数据 {} 条", exportDataList.size());
            
            EasyExcelUtil<MGoodsSpecExportVo> util = new EasyExcelUtil<>(MGoodsSpecExportVo.class);
            String fileName = "规格信息导出_" + DateTimeUtil.dateTimeNow();
            util.exportExcel(fileName, "规格信息", exportDataList, response);
        } finally {
            // 无论成功失败都要恢复导出处理状态为false
            sPagesService.updateExportProcessingFalse(sPagesVo);
        }
    }

    @SysLogAnnotion("规格信息导出")
    @PostMapping("/export")
    public void export(@RequestBody(required = false) MGoodsSpecVo searchCondition, HttpServletResponse response) throws IOException {
        // 通过页面编码获取完整的页面对象（包含id字段）
        SPagesVo sPagesVo = sPagesMapper.selectByCode(PageCodeConstant.PAGE_GOODS_SPEC);
        
        try {
            // 设置导出处理状态为true
            sPagesService.updateExportProcessingTrue(sPagesVo);
            
            // 选中导出：参数验证
            if (searchCondition == null || searchCondition.getIds() == null || searchCondition.getIds().length == 0) {
                throw new BusinessException("请选择要导出的规格记录");
            }
            
            // 选中导出：直接调用selectExportList查询方法
            List<MGoodsSpecExportVo> exportDataList = service.selectExportList(searchCondition);
            log.info("选中导出：查询到规格数据 {} 条", exportDataList.size());
            
            EasyExcelUtil<MGoodsSpecExportVo> util = new EasyExcelUtil<>(MGoodsSpecExportVo.class);
            String fileName = "规格信息导出_" + DateTimeUtil.dateTimeNow();
            util.exportExcel(fileName, "规格信息", exportDataList, response);
        } finally {
            // 无论成功失败都要恢复导出处理状态为false
            sPagesService.updateExportProcessingFalse(sPagesVo);
        }
    }

    @SysLogAnnotion("根据查询条件，查询物料转换商品")
    // @ApiOperation(value = "根据参数获取物料规格信息")
    @PostMapping("/convert/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MGoodsSpecVo>>> getConvertGoodsList(@RequestBody(required = false) MGoodsSpecVo searchCondition) {
        IPage<MGoodsSpecVo> list = service.getConvertGoodsList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }
}
