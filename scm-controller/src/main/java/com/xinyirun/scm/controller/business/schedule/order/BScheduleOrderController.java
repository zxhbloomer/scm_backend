package com.xinyirun.scm.controller.business.schedule.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.schedule.BScheduleOrderVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.business.schedule.order.IBScheduleOrderService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 调度订单 前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-11-02
 */
@Slf4j
// @Api(tags = "调度订单")
@RestController
@RequestMapping(value = "/api/v1/scheduleorder")
public class BScheduleOrderController extends SystemBaseController {

    @Autowired
    private IBScheduleOrderService service;

    @SysLogAnnotion("根据查询条件，获取调度订单列表")
    // @ApiOperation(value = "根据参数获取调度订单列表")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BScheduleOrderVo>>> pagelist(@RequestBody(required = false) BScheduleOrderVo searchCondition) {
        IPage<BScheduleOrderVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("调度订单数据更新保存")
    // @ApiOperation(value = "根据参数id，获取调度订单信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BScheduleOrderVo>> insert(@RequestBody(required = false) BScheduleOrderVo bean) {

        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("调度订单数据更新保存")
    // @ApiOperation(value = "根据参数id，获取调度订单信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BScheduleOrderVo>> save(@RequestBody(required = false) BScheduleOrderVo bean) {

        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("调度订单数据更新保存")
    // @ApiOperation(value = "根据参数id，获取调度订单信息")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BScheduleOrderVo>> delete(@RequestBody(required = false) BScheduleOrderVo bean) {

        if(service.delete(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"删除成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }
}
