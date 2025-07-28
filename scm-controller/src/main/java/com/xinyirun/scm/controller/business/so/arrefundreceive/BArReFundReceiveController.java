package com.xinyirun.scm.controller.business.so.arrefundreceive;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.so.arrefundreceive.BArReFundReceiveVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.core.system.service.business.so.arrefundreceive.IBArReFundReceiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 退款单表 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@RestController
@RequestMapping("/api/v1/ar/refund/receive")
public class BArReFundReceiveController {

    @Autowired
    private IBArReFundReceiveService service;

    /**
     * 退款单表  新增
     */
    @PostMapping("/insert")
    @SysLogAnnotion("退款单表 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BArReFundReceiveVo>> insert(@RequestBody BArReFundReceiveVo searchCondition) {
        InsertResultAo<BArReFundReceiveVo> resultAo = service.startInsert(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"新增成功"));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }


    @SysLogAnnotion("退款单表，获取列表信息")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BArReFundReceiveVo>>> selectPagelist(@RequestBody(required = false) BArReFundReceiveVo searchCondition) {
        IPage<BArReFundReceiveVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("退款单表，获取单条数据")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<BArReFundReceiveVo>> get(@RequestBody(required = false) BArReFundReceiveVo searchCondition) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId())));
    }

    @SysLogAnnotion("退款单表，凭证上传，完成退款")
    @PostMapping("/complete")
    public ResponseEntity<JsonResultAo<BArReFundReceiveVo>> refundComplete(@RequestBody(required = false) BArReFundReceiveVo searchCondition) {
        UpdateResultAo<BArReFundReceiveVo> resultAo = service.refundComplete(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(resultAo.getData(), "操作成功"));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("退款单表，作废")
    @PostMapping("/cancel")
    public ResponseEntity<JsonResultAo<BArReFundReceiveVo>> cancel(@RequestBody(required = false) BArReFundReceiveVo searchCondition) {
        UpdateResultAo<BArReFundReceiveVo> resultAo = service.cancel(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(null));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("退款单表，汇总查询")
    @PostMapping("/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BArReFundReceiveVo>> querySum(@RequestBody(required = false) BArReFundReceiveVo searchCondition) {
        BArReFundReceiveVo result = service.querySum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("退款单表，单条汇总查询")
    @PostMapping("/view/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BArReFundReceiveVo>> queryViewSum(@RequestBody(required = false) BArReFundReceiveVo searchCondition) {
        BArReFundReceiveVo result = service.queryViewSum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }
}