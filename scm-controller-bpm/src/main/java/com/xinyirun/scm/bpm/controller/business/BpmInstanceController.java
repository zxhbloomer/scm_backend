package com.xinyirun.scm.bpm.controller.business;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmInstanceVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.bpm.service.business.IBpmInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 我发起的实例
 * @since 2024-10-11
 */
@RestController
@RequestMapping("/api/v1/bpm/instance")
public class BpmInstanceController {

    @Autowired
    private IBpmInstanceService service;


    @SysLogAnnotion("查看我发起的实例（流程）")
    @PostMapping("/list")
    public ResponseEntity<JsonResultAo<IPage<BBpmInstanceVo>>> selectPageList(@RequestBody BBpmInstanceVo param){
        IPage<BBpmInstanceVo> list = service.selectPageList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

}
