package com.xinyirun.scm.controller.business.wo;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoRouterVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.core.system.service.business.wo.IBWoRouterService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  生产配方 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
@RestController
@RequestMapping("/api/v1/worouter")
public class BWoRouterController extends SystemBaseController {

    @Autowired
    private IBWoRouterService service;

    @PostMapping("/insert")
    @SysLogAnnotion("新增 生产配方")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BWoRouterVo>> insert(@RequestBody BWoRouterVo param) {
        InsertResultAo<BWoRouterVo> rtn = service.insert(param);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"新增成功"));
        } else {
            throw new InsertErrorException("新增失败");
        }
    }

    @PostMapping("/update")
    @SysLogAnnotion("更新 生产配方")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BWoRouterVo>> update(@RequestBody BWoRouterVo param) {
        UpdateResultAo<BWoRouterVo> rtn = service.updateParam(param);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"更新成功"));
        } else {
            throw new InsertErrorException("更新失败");
        }
    }

    @PostMapping("/getDetail")
    @SysLogAnnotion("更新 生产配方")
    public ResponseEntity<JsonResultAo<BWoRouterVo>> getDetail(@RequestBody BWoRouterVo param) {
        BWoRouterVo result = service.getDetail(param.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/enable")
    @SysLogAnnotion("启用 生产配方")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> enable(@RequestBody List<BWoRouterVo> param) {
        service.enable(param);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/disabled")
    @SysLogAnnotion("禁用 生产配方")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> disabled(@RequestBody List<BWoRouterVo> param) {
        service.disabled(param);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/pagelist")
    @SysLogAnnotion("分页查询 生产配方")
    public ResponseEntity<JsonResultAo<IPage<BWoRouterVo>>> selectPageList(@RequestBody(required = false) BWoRouterVo param) {
        IPage<BWoRouterVo> result = service.selectPageList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }
}
