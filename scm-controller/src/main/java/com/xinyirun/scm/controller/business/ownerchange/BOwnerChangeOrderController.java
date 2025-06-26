package com.xinyirun.scm.controller.business.ownerchange;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.ownerchange.BOwnerChangeOrderVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.business.ownerchange.IBOwnerChangeOrderService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 货权转移订单 前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-11-02
 */
@Slf4j
// @Api(tags = "货权转移订单")
@RestController
@RequestMapping(value = "/api/v1/ownerchangeorder")
public class BOwnerChangeOrderController extends SystemBaseController {

    @Autowired
    private IBOwnerChangeOrderService service;

    @SysLogAnnotion("根据查询条件，获取货权转移订单列表")
    // @ApiOperation(value = "根据参数获取货权转移订单列表")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BOwnerChangeOrderVo>>> pagelist(@RequestBody(required = false) BOwnerChangeOrderVo searchCondition) {
        IPage<BOwnerChangeOrderVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("货权转移订单数据更新保存")
    // @ApiOperation(value = "根据参数id，获取货权转移订单信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BOwnerChangeOrderVo>> insert(@RequestBody(required = false) BOwnerChangeOrderVo bean) {

        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("货权转移订单数据更新保存")
    // @ApiOperation(value = "根据参数id，获取货权转移订单信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BOwnerChangeOrderVo>> save(@RequestBody(required = false) BOwnerChangeOrderVo bean) {

        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("货权转移订单数据更新保存")
    // @ApiOperation(value = "根据参数id，获取货权转移订单信息")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BOwnerChangeOrderVo>> delete(@RequestBody(required = false) BOwnerChangeOrderVo bean) {

        if(service.delete(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"删除成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }
}
