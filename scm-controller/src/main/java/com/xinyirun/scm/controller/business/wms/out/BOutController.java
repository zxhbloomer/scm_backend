package com.xinyirun.scm.controller.business.wms.out;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.business.wms.out.IBOutService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 出库单 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-26
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/out")
public class BOutController {

    @Autowired
    private IBOutService service;

    /**
     * 出库单 新增
     */
    @PostMapping("/insert")
    @SysLogAnnotion("出库单 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BOutVo>> insert(@RequestBody BOutVo searchCondition) {
        InsertResultAo<BOutVo> resultAo = service.startInsert(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"新增成功"));
        } else {
            throw new InsertErrorException("新增失败，请编辑后重新新增。");
        }
    }

    /**
     * 出库单校验
     */
    @SysLogAnnotion("出库单校验")
    @PostMapping("/validate")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> checkLogic(@RequestBody(required = false) BOutVo searchCondition) {
        CheckResultAo checkResultAo = service.checkLogic(searchCondition, searchCondition.getCheck_type());
        if (!checkResultAo.isSuccess()) {
            throw new BusinessException(checkResultAo.getMessage());
        } else {
            return ResponseEntity.ok().body(ResultUtil.OK("OK"));
        }
    }

    /**
     * 根据查询条件，获取出库单集合信息
     */
    @SysLogAnnotion("根据查询条件，获取出库单集合信息")
    @PostMapping("/pagelist")
    public ResponseEntity<JsonResultAo<IPage<BOutVo>>> list(@RequestBody(required = false) BOutVo searchCondition) {
        IPage<BOutVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    /**
     * 按出库单合计
     */
    @SysLogAnnotion("按出库单合计")
    @PostMapping("/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutVo>> querySum(@RequestBody(required = false) BOutVo searchCondition) {
        BOutVo result = service.querySum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 根据查询条件，获取出库单信息
     */
    @SysLogAnnotion("根据查询条件，获取出库单信息")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<BOutVo>> get(@RequestBody(required = false) BOutVo searchCondition) {
        BOutVo vo = service.selectById(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 出库单更新保存
     */
    @SysLogAnnotion("出库单更新保存")
    @PostMapping("/save")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BOutVo>> save(@RequestBody(required = false) BOutVo searchCondition) {
        if(service.startUpdate(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    /**
     * 根据查询条件，出库单逻辑删除
     */
    @SysLogAnnotion("根据查询条件，出库单逻辑删除")
    @PostMapping("/delete")
    public ResponseEntity<JsonResultAo<BOutVo>> delete(@RequestBody(required = false) List<BOutVo> searchCondition) {
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
    public ResponseEntity<JsonResultAo<BOutVo>> print(@RequestBody(required = false) BOutVo searchCondition) {
        BOutVo printInfo = service.getPrintInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(printInfo));
    }

    /**
     * 出库单，作废
     */
    @SysLogAnnotion("出库单，作废")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutVo>> cancel(@RequestBody(required = false) BOutVo searchCondition) {
        if(service.cancel(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    /**
     * 出库单，完成
     */
    @SysLogAnnotion("出库单，完成")
    @PostMapping("/finish")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutVo>> finish(@RequestBody(required = false) BOutVo searchCondition) {
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
    public void export(@RequestBody(required = false) BOutVo param, HttpServletResponse response) throws IOException {
        @SuppressWarnings("unused")
        List<BOutVo> result = service.selectExportList(param);
        // TODO: 使用EasyExcel进行导出
        // EasyExcelUtil.export(response, "出库单", BOutVo.class, result);
    }
}