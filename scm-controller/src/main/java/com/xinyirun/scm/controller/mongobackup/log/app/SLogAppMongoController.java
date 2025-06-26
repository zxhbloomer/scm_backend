package com.xinyirun.scm.controller.mongobackup.log.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.mongo.log.SLogAppMongoVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.mongodb.service.log.app.LogAppMongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author wwl
 */
@RestController
@RequestMapping(value = "/api/v1/log/app/new")
@Slf4j
public class SLogAppMongoController {

    @Autowired
    private LogAppMongoService service;


    @SysLogAnnotion("根据查询条件，获取系统日志数据表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<SLogAppMongoVo>>> list(@RequestBody(required = false) SLogAppMongoVo searchCondition)  {
        IPage<SLogAppMongoVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取系统日志数据表信息")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SLogAppMongoVo>> get(@RequestBody(required = false) SLogAppMongoVo searchCondition)  {
        SLogAppMongoVo result = service.getById(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

}
