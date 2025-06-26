package com.xinyirun.scm.bpm.controller.business;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmCcVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.bpm.service.business.IBpmCcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 抄送
 * @since 2024-10-11
 */
@RestController
@RequestMapping("/api/v1/bpm/cc")
public class BpmCcController {

    @Autowired
    private IBpmCcService service;


    @SysLogAnnotion("查看抄送我的")
    @PostMapping("/list")
    public ResponseEntity<JsonResultAo<IPage<BBpmCcVo>>> selectPageList(@RequestBody BBpmCcVo param){
        IPage<BBpmCcVo> list = service.selectPageList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

}
