package com.xinyirun.scm.controller.master.vehicle;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.vehicle.MVehicleVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.master.vehicle.MVehicleService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  车辆管理
 * </p>
 *
 * @author htt
 * @since 2021-12-20
 */
@Slf4j
// @Api(tags = "车辆管理")
@RestController
@RequestMapping(value = "/api/v1/vehicle")
public class MVehicleController extends SystemBaseController {

    @Autowired
    private MVehicleService service;

    @SysLogAnnotion("根据查询条件，获取车辆分页列表")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MVehicleVo>>> pageList(@RequestBody(required = false) MVehicleVo searchCondition) {
        IPage<MVehicleVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取车辆详情")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MVehicleVo>> getDetail(@RequestBody(required = false) MVehicleVo searchCondition) {
        MVehicleVo list = service.getDetail(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，删除或恢复车辆信息")
    @PostMapping("/enable")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> enable(@RequestBody(required = false) List<MVehicleVo> searchCondition) {
        service.enable(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("车辆数据新增")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MVehicleVo>> insert(@RequestBody(required = false) MVehicleVo bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("新增失败。");
        }
    }

    @SysLogAnnotion("车辆数据更新保存")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MVehicleVo>> save(@RequestBody(required = false) MVehicleVo bean) {
        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("删除车辆")
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) MVehicleVo searchCondition) {
        service.delete(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

}
