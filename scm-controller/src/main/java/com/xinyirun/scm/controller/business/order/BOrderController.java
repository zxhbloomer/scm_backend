package com.xinyirun.scm.controller.business.order;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.business.order.IBOrderService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 订单管理 前端控制器
 * </p>
 *
 * @author wwl
 * @since 2021-03-03
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/order")
public class BOrderController extends SystemBaseController {

    @Autowired
    private IBOrderService service;

    @SysLogAnnotion("根据查询条件，获取订单信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BOrderVo>>> list(@RequestBody(required = false) BOrderVo searchCondition) {
        IPage<BOrderVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取订单信息 用于合同号是多个的情况")
    @PostMapping("/list2")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BOrderVo>>> list2(@RequestBody(required = false) BOrderVo searchCondition) {
        IPage<BOrderVo> list = service.selectPage2(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }


}
