package com.xinyirun.scm.controller.business.so.settlement;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.so.settlement.BSoSettlementVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.business.so.settlement.IBSoSettlementService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 销售结算表 前端控制器
 *
 * @author xinyirun
 * @since 2025-01-25
 */
@RestController
@RequestMapping("/api/v1/so/settlement")
public class BSoSettlementController extends SystemBaseController {

    @Autowired
    private IBSoSettlementService service;

    @Autowired
    private ISPagesService isPagesService;

    /**
     * 销售结算 新增
     */
    @PostMapping("/insert")
    @SysLogAnnotion("销售结算 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BSoSettlementVo>> insert(@RequestBody BSoSettlementVo searchCondition) {
        InsertResultAo<BSoSettlementVo> resultAo = service.startInsert(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"新增成功"));
        } else {
            throw new InsertErrorException("新增失败，请编辑后重新新增。");
        }
    }

    /**
     * 销售结算校验
     */
    @SysLogAnnotion("销售结算校验")
    @PostMapping("/validate")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> checkLogic(@RequestBody(required = false) BSoSettlementVo searchCondition) {
        CheckResultAo checkResultAo = service.checkLogic(searchCondition, searchCondition.getCheck_type());
        if (!checkResultAo.isSuccess()) {
            throw new BusinessException(checkResultAo.getMessage());
        } else {
            return ResponseEntity.ok().body(ResultUtil.OK("OK"));
        }
    }

    /**
     * 根据查询条件，获取销售结算集合信息
     */
    @SysLogAnnotion("根据查询条件，获取销售结算集合信息")
    @PostMapping("/pagelist")
    public ResponseEntity<JsonResultAo<IPage<BSoSettlementVo>>> list(@RequestBody(required = false) BSoSettlementVo searchCondition) {
        IPage<BSoSettlementVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    /**
     * 按销售结算合计
     */
    @SysLogAnnotion("按销售结算合计")
    @PostMapping("/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BSoSettlementVo>> querySum(@RequestBody(required = false) BSoSettlementVo searchCondition) {
        BSoSettlementVo result = service.querySum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 根据查询条件，获取销售结算信息
     */
    @SysLogAnnotion("根据查询条件，获取销售结算信息")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<BSoSettlementVo>> get(@RequestBody(required = false) BSoSettlementVo searchCondition) {
        BSoSettlementVo vo = service.selectById(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 销售结算更新保存
     */
    @SysLogAnnotion("销售结算更新保存")
    @PostMapping("/save")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BSoSettlementVo>> save(@RequestBody(required = false) BSoSettlementVo searchCondition) {
        if(service.startUpdate(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    /**
     * 根据查询条件，销售结算逻辑删除
     */
    @SysLogAnnotion("根据查询条件，销售结算逻辑删除")
    @PostMapping("/delete")
    public ResponseEntity<JsonResultAo<BSoSettlementVo>> delete(@RequestBody(required = false) List<BSoSettlementVo> searchCondition) {
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
    public ResponseEntity<JsonResultAo<BSoSettlementVo>> print(@RequestBody(required = false) BSoSettlementVo searchCondition) {
        BSoSettlementVo printInfo = service.getPrintInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(printInfo));
    }

    /**
     * 销售结算，作废
     */
    @SysLogAnnotion("销售结算，作废")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BSoSettlementVo>> cancel(@RequestBody(required = false) BSoSettlementVo searchCondition) {
        if(service.cancel(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    /**
     * 销售结算，完成
     */
    @SysLogAnnotion("销售结算，完成")
    @PostMapping("/finish")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BSoSettlementVo>> finish(@RequestBody(required = false) BSoSettlementVo searchCondition) {
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
    public void export(@RequestBody(required = false) BSoSettlementVo param, HttpServletResponse response) throws IOException {
    }
}