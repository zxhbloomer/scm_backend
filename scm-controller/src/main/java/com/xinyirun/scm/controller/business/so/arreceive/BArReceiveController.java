package com.xinyirun.scm.controller.business.so.arreceive;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.so.arreceive.BArReceiveVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.core.system.service.business.so.arreceive.IBArReceiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 收款单表 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@RestController
@RequestMapping("/api/v1/ar/receive")
public class BArReceiveController {

    @Autowired
    private IBArReceiveService service;

    /**
     * 收款单  新增
     */
    @PostMapping("/insert")
    @SysLogAnnotion("收款单 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BArReceiveVo>> insert(@RequestBody BArReceiveVo searchCondition) {
        InsertResultAo<BArReceiveVo> resultAo = service.startInsert(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"新增成功"));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }


    @SysLogAnnotion("收款单表，获取列表信息")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BArReceiveVo>>> selectPagelist(@RequestBody(required = false) BArReceiveVo searchCondition) {
        IPage<BArReceiveVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("收款单表，获取单条数据")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<BArReceiveVo>> get(@RequestBody(required = false) BArReceiveVo searchCondition) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId())));
    }

    @SysLogAnnotion("收款单表，凭证上传，完成收款")
    @PostMapping("/complete")
    public ResponseEntity<JsonResultAo<BArReceiveVo>> receiveComplete(@RequestBody(required = false) BArReceiveVo searchCondition) {
        UpdateResultAo<BArReceiveVo> resultAo = service.receiveComplete(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(resultAo.getData(), "操作成功"));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("收款单表，作废")
    @PostMapping("/cancel")
    public ResponseEntity<JsonResultAo<BArReceiveVo>> cancel(@RequestBody(required = false) BArReceiveVo searchCondition) {
        UpdateResultAo<BArReceiveVo> resultAo = service.cancel(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(null));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("收款单表，汇总查询")
    @PostMapping("/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BArReceiveVo>> querySum(@RequestBody(required = false) BArReceiveVo searchCondition) {
        BArReceiveVo result = service.querySum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("收款单表，单条汇总查询")
    @PostMapping("/view/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BArReceiveVo>> queryViewSum(@RequestBody(required = false) BArReceiveVo searchCondition) {
        BArReceiveVo result = service.queryViewSum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }
}
