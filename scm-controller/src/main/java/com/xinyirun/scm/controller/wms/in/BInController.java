package com.xinyirun.scm.controller.wms.in;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.wms.in.BInVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.wms.in.IBInService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 入库单 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-26
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/in")
public class BInController {

    @Autowired
    private IBInService service;

    /**
     * 入库单 新增
     */
    @PostMapping("/insert")
    @SysLogAnnotion("入库单 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BInVo>> insert(@RequestBody BInVo searchCondition) {
        InsertResultAo<BInVo> resultAo = service.startInsert(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"新增成功"));
        } else {
            throw new InsertErrorException("新增失败，请编辑后重新新增。");
        }
    }

    /**
     * 入库单校验
     */
    @SysLogAnnotion("入库单校验")
    @PostMapping("/validate")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> checkLogic(@RequestBody(required = false) BInVo searchCondition) {
        CheckResultAo checkResultAo = service.checkLogic(searchCondition, searchCondition.getCheck_type());
        if (!checkResultAo.isSuccess()) {
            throw new BusinessException(checkResultAo.getMessage());
        } else {
            return ResponseEntity.ok().body(ResultUtil.OK("OK"));
        }
    }

    /**
     * 根据查询条件，获取入库单集合信息
     */
    @SysLogAnnotion("根据查询条件，获取入库单集合信息")
    @PostMapping("/pagelist")
    public ResponseEntity<JsonResultAo<IPage<BInVo>>> list(@RequestBody(required = false) BInVo searchCondition) {
        IPage<BInVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    /**
     * 按入库单合计
     */
    @SysLogAnnotion("按入库单合计")
    @PostMapping("/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BInVo>> querySum(@RequestBody(required = false) BInVo searchCondition) {
        BInVo result = service.querySum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 根据查询条件，获取入库单信息
     */
    @SysLogAnnotion("根据查询条件，获取入库单信息")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<BInVo>> get(@RequestBody(required = false) BInVo searchCondition) {
        BInVo vo = service.selectById(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 入库单更新保存
     */
    @SysLogAnnotion("入库单更新保存")
    @PostMapping("/save")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BInVo>> save(@RequestBody(required = false) BInVo searchCondition) {
        if(service.startUpdate(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    /**
     * 根据查询条件，入库单逻辑删除
     */
    @SysLogAnnotion("根据查询条件，入库单逻辑删除")
    @PostMapping("/delete")
    public ResponseEntity<JsonResultAo<BInVo>> delete(@RequestBody(required = false) List<BInVo> searchCondition) {
        if(service.delete(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"删除成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    /**
     * 获取报表系统参数，并组装打印参数
     */
    @SysLogAnnotion("获取报表系统参数，并组装打印参数")
    @PostMapping("/print")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BInVo>> print(@RequestBody(required = false) BInVo searchCondition) {
        BInVo printInfo = service.getPrintInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(printInfo));
    }

    /**
     * 入库单，作废
     */
    @SysLogAnnotion("入库单，作废")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BInVo>> cancel(@RequestBody(required = false) BInVo searchCondition) {
        if(service.cancel(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    /**
     * 入库单，完成
     */
    @SysLogAnnotion("入库单，完成")
    @PostMapping("/finish")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BInVo>> finish(@RequestBody(required = false) BInVo searchCondition) {
        if(service.finish(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    /**
     * 导出
     */
    @PostMapping("/export")
    @SysLogAnnotion("导出")
    public void export(@RequestBody(required = false) BInVo param, HttpServletResponse response) throws IOException {
        @SuppressWarnings("unused")
        List<BInVo> result = service.selectExportList(param);
        // TODO: 使用EasyExcel进行导出
        // EasyExcelUtil.export(response, "入库单", BInVo.class, result);
    }
}
