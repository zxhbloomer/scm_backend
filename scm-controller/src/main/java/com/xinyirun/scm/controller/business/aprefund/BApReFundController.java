package com.xinyirun.scm.controller.business.aprefund;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.business.po.aprefund.IBApReFundService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 应付退款管理表（Accounts Payable） 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@RestController
@RequestMapping("/api/v1/ap/refund")
public class BApReFundController {

    @Autowired
    private IBApReFundService service;

    @SysLogAnnotion("应付退款管理,获取业务类型")
    @PostMapping("/gettype")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<BApReFundVo>>> getType(@RequestBody(required = false) BApReFundVo searchCondition) {
        List<BApReFundVo> list = service.getType();
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    /**
     * 应付账款管理表  新增
     */
    @PostMapping("/insert")
    @SysLogAnnotion("应付退款管理 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BApReFundVo>> insert(@RequestBody BApReFundVo searchCondition) {
        InsertResultAo<BApReFundVo> resultAo = service.startInsert(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"新增成功"));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("应付退款管理 更新保存")
    @PostMapping("/save")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BApReFundVo>> save(@RequestBody(required = false) BApReFundVo searchCondition) {
        if(service.startUpdate(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("应付退款管理，获取列表信息")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BApReFundVo>>> selectPagelist(@RequestBody(required = false) BApReFundVo searchCondition) {
        IPage<BApReFundVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("应付退款管理，获取单条数据")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<BApReFundVo>> get(@RequestBody(required = false) BApReFundVo searchCondition) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId())));
    }

    @SysLogAnnotion("应付退款管理，校验")
    @PostMapping("/validate")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> checkLogic(@RequestBody(required = false) BApReFundVo searchCondition) {
        CheckResultAo checkResultAo = service.checkLogic(searchCondition, searchCondition.getCheck_type());
        if (!checkResultAo.isSuccess()) {
            throw new BusinessException(checkResultAo.getMessage());
        }else {
            return ResponseEntity.ok().body(ResultUtil.OK("OK"));
        }
    }

    @SysLogAnnotion("应付退款管理，获取报表系统参数，并组装打印参数")
    @PostMapping("/print")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BApReFundVo>> print(@RequestBody(required = false) BApReFundVo searchCondition) {
        BApReFundVo printInfo = service.getPrintInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(printInfo));
    }


    @SysLogAnnotion("应付退款管理，逻辑删除")
    @PostMapping("/delete")
    public ResponseEntity<JsonResultAo<BApReFundVo>> delete(@RequestBody(required = false) List<BApReFundVo> searchCondition) {
        if(service.delete(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"删除成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }


    @SysLogAnnotion("应付退款管理，作废")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BApReFundVo>> cancel(@RequestBody(required = false) BApReFundVo vo) {
        if(service.cancel(vo).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("应付退款管理，汇总查询")
    @PostMapping("/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BApReFundVo>> querySum(@RequestBody(required = false) BApReFundVo vo) {
        BApReFundVo result = service.querySum(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/export")
    @SysLogAnnotion("应付退款管理 导出")
    public void export(@RequestBody(required = false) BApReFundVo param, HttpServletResponse response) throws IOException {

    }

}
