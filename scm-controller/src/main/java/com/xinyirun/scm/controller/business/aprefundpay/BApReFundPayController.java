package com.xinyirun.scm.controller.business.aprefundpay;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundPayVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.core.system.service.business.aprefundpay.IBApReFundPayService;
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
@RequestMapping("/api/v1/ap/refund/pay")
public class BApReFundPayController {

    @Autowired
    private IBApReFundPayService service;

    /**
     * 付款单表  下推付款单
     */
    @PostMapping("/insert")
    @SysLogAnnotion("付款单表 下推付款单")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BApReFundPayVo>> insert(@RequestBody BApReFundPayVo searchCondition) {
        InsertResultAo<BApReFundPayVo> resultAo = service.insert(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(null));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }


    @SysLogAnnotion("付款单表，获取列表信息")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BApReFundPayVo>>> selectPagelist(@RequestBody(required = false) BApReFundPayVo searchCondition) {
        IPage<BApReFundPayVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("付款单表，获取单条数据")
    @PostMapping("/getdetail")
    public ResponseEntity<JsonResultAo<BApReFundPayVo>> get(@RequestBody(required = false) BApReFundPayVo searchCondition) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId())));
    }

    @SysLogAnnotion("付款单表，付款复核")
    @PostMapping("/payment_review")
    public ResponseEntity<JsonResultAo<BApReFundPayVo>> paymentReview(@RequestBody(required = false) BApReFundPayVo searchCondition) {
        UpdateResultAo<BApReFundPayVo> resultAo = service.paymentReview(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(null));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("付款单表，作废")
    @PostMapping("/cancel")
    public ResponseEntity<JsonResultAo<BApReFundPayVo>> cancel(@RequestBody(required = false) BApReFundPayVo searchCondition) {
        UpdateResultAo<BApReFundPayVo> resultAo = service.cancel(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(null));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }
}
