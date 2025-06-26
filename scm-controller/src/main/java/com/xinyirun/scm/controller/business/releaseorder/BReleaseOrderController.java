package com.xinyirun.scm.controller.business.releaseorder;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.releaseorder.BReleaseOrderVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.business.releaseorder.IBReleaseOrderService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Wang Qianfeng
 * @since 2022-11-30
 */
@Slf4j
@RestController
@RequestMapping(value = "api/v1/releaseorder")
public class BReleaseOrderController extends SystemBaseController {

    @Autowired
    private IBReleaseOrderService service;

    @SysLogAnnotion("放货指令列表")
    @PostMapping("/pagelist")
    public ResponseEntity<JsonResultAo<IPage<BReleaseOrderVo>>> list(@RequestBody BReleaseOrderVo param){
        IPage<BReleaseOrderVo> page = service.selectPage(param);
        return ResponseEntity.ok().body(ResultUtil.OK(page));
    }

    @SysLogAnnotion("放货指令详情")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<BReleaseOrderVo>> get(@RequestBody BReleaseOrderVo param){
        BReleaseOrderVo bReleaseOrderVo = service.get(param);
        return ResponseEntity.ok().body(ResultUtil.OK(bReleaseOrderVo));
    }

    @SysLogAnnotion("放货指令详情")
    @PostMapping("/detail/get")
    public ResponseEntity<JsonResultAo<BReleaseOrderVo>> getDetail(@RequestBody BReleaseOrderVo param){
        BReleaseOrderVo bReleaseOrderVo = service.getDetail(param);
        return ResponseEntity.ok().body(ResultUtil.OK(bReleaseOrderVo));
    }

    @SysLogAnnotion("放货指令列表")
    @PostMapping("/pagecommlist")
    public ResponseEntity<JsonResultAo<IPage<BReleaseOrderVo>>> getCommList(@RequestBody BReleaseOrderVo param){
        IPage<BReleaseOrderVo> page = service.selectCommPage(param);
        return ResponseEntity.ok().body(ResultUtil.OK(page));
    }

    @SysLogAnnotion("放货指令 新增")
    @PostMapping("/insert")
    public ResponseEntity<JsonResultAo<String>> insert(@RequestBody BReleaseOrderVo param){
        InsertResultAo<String> resultAo = service.insert(param);
        return ResponseEntity.ok().body(ResultUtil.OK(resultAo.getData()));
    }

    @SysLogAnnotion("放货指令 更新")
    @PostMapping("/update")
    public ResponseEntity<JsonResultAo<String>> update(@RequestBody BReleaseOrderVo param){
        UpdateResultAo<String> resultAo = service.updateByParam(param);
        return ResponseEntity.ok().body(ResultUtil.OK(resultAo.getData()));
    }
}
