package com.xinyirun.scm.controller.business.settlement;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.po.settlement.BPoSettlementVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.business.po.settlement.IBPoSettlementService;
import com.xinyirun.scm.core.system.service.log.sys.ISLogImportService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 采购结算表 前端控制器
 *
 * @author xinyirun
 * @since 2025-01-19
 */
@RestController
@RequestMapping("/api/v1/settlement")
public class BSettlementController extends SystemBaseController {

    @Autowired
    private IBPoSettlementService service;

    @Autowired
    private ISPagesService isPagesService;

    @Autowired
    private ISLogImportService isLogImportService;

    /**
     * 采购结算 新增
     */
    @PostMapping("/insert")
    @SysLogAnnotion("采购结算 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BPoSettlementVo>> insert(@RequestBody BPoSettlementVo searchCondition) {
        InsertResultAo<BPoSettlementVo> resultAo = service.startInsert(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"新增成功"));
        } else {
            throw new InsertErrorException("新增失败，请编辑后重新新增。");
        }
    }

    /**
     * 采购结算校验
     */
    @SysLogAnnotion("采购结算校验")
    @PostMapping("/validate")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> checkLogic(@RequestBody(required = false) BPoSettlementVo searchCondition) {
        CheckResultAo checkResultAo = service.checkLogic(searchCondition, searchCondition.getCheck_type());
        if (!checkResultAo.isSuccess()) {
            throw new BusinessException(checkResultAo.getMessage());
        } else {
            return ResponseEntity.ok().body(ResultUtil.OK("OK"));
        }
    }

    /**
     * 根据查询条件，获取采购结算集合信息
     */
    @SysLogAnnotion("根据查询条件，获取采购结算集合信息")
    @PostMapping("/pagelist")
    public ResponseEntity<JsonResultAo<IPage<BPoSettlementVo>>> list(@RequestBody(required = false) BPoSettlementVo searchCondition) {
        IPage<BPoSettlementVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    /**
     * 按采购结算合计
     */
    @SysLogAnnotion("按采购结算合计")
    @PostMapping("/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BPoSettlementVo>> querySum(@RequestBody(required = false) BPoSettlementVo searchCondition) {
        BPoSettlementVo result = service.querySum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 根据查询条件，获取采购结算信息
     */
    @SysLogAnnotion("根据查询条件，获取采购结算信息")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<BPoSettlementVo>> get(@RequestBody(required = false) BPoSettlementVo searchCondition) {
        BPoSettlementVo vo = service.selectById(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 采购结算更新保存
     */
    @SysLogAnnotion("采购结算更新保存")
    @PostMapping("/save")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BPoSettlementVo>> save(@RequestBody(required = false) BPoSettlementVo searchCondition) {
        if(service.startUpdate(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    /**
     * 根据查询条件，采购结算逻辑删除
     */
    @SysLogAnnotion("根据查询条件，采购结算逻辑删除")
    @PostMapping("/delete")
    public ResponseEntity<JsonResultAo<BPoSettlementVo>> delete(@RequestBody(required = false) List<BPoSettlementVo> searchCondition) {
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
    public ResponseEntity<JsonResultAo<BPoSettlementVo>> print(@RequestBody(required = false) BPoSettlementVo searchCondition) {
        BPoSettlementVo printInfo = service.getPrintInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(printInfo));
    }

    /**
     * 采购结算，作废
     */
    @SysLogAnnotion("采购结算，作废")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BPoSettlementVo>> cancel(@RequestBody(required = false) BPoSettlementVo searchCondition) {
        if(service.cancel(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    /**
     * 采购结算，完成
     */
    @SysLogAnnotion("采购结算，完成")
    @PostMapping("/finish")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BPoSettlementVo>> finish(@RequestBody(required = false) BPoSettlementVo searchCondition) {
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
    public void export(@RequestBody(required = false) BPoSettlementVo param, HttpServletResponse response) throws IOException {
    }
}