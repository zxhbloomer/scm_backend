package com.xinyirun.scm.controller.business.rtwo;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoRouterVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.core.system.service.business.rtwo.IBRtWoRouterService;
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
@RequestMapping("/api/v1/rt/worouter")
public class BRtWoRouterController extends SystemBaseController {

    @Autowired
    private IBRtWoRouterService service;

    @PostMapping("/insert")
    @SysLogAnnotion("新增 生产配方")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BRtWoRouterVo>> insert(@RequestBody BRtWoRouterVo param) {
        InsertResultAo<BRtWoRouterVo> rtn = service.insert(param);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"新增成功"));
        } else {
            throw new InsertErrorException("新增失败");
        }
    }

    @PostMapping("/update")
    @SysLogAnnotion("更新 生产配方")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BRtWoRouterVo>> update(@RequestBody BRtWoRouterVo param) {
        UpdateResultAo<BRtWoRouterVo> rtn = service.updateParam(param);
        if(rtn.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(rtn.getData(),"更新成功"));
        } else {
            throw new InsertErrorException("更新失败");
        }
    }

    @PostMapping("/getDetail")
    @SysLogAnnotion("更新 生产配方")
    public ResponseEntity<JsonResultAo<BRtWoRouterVo>> getDetail(@RequestBody BRtWoRouterVo param) {
        BRtWoRouterVo result = service.getDetail(param.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/enable")
    @SysLogAnnotion("启用 生产配方")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> enable(@RequestBody List<BRtWoRouterVo> param) {
        service.enable(param);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/disabled")
    @SysLogAnnotion("禁用 生产配方")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> disabled(@RequestBody List<BRtWoRouterVo> param) {
        service.disabled(param);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/pagelist")
    @SysLogAnnotion("分页查询 生产配方")
    public ResponseEntity<JsonResultAo<IPage<BRtWoRouterVo>>> selectPageList(@RequestBody(required = false) BRtWoRouterVo param) {
        IPage<BRtWoRouterVo> result = service.selectPageList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }
}
