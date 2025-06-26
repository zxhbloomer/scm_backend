package com.xinyirun.scm.controller.mongobackup.log.sys;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.mongo.log.SLogSysMongoVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.mongodb.service.log.sys.LogPcSystemMongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author wwl
 */
@RestController
@RequestMapping(value = "/api/v1/log/sys/new")
@Slf4j
public class SLogSysMongoController {

    @Autowired
    private LogPcSystemMongoService service;


    @SysLogAnnotion("根据查询条件，获取系统日志数据表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<SLogSysMongoVo>>> list(@RequestBody(required = false) SLogSysMongoVo searchCondition)  {
        IPage<SLogSysMongoVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取系统日志数据表信息")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SLogSysMongoVo>> get(@RequestBody(required = false) SLogSysMongoVo searchCondition)  {
        SLogSysMongoVo list = service.getById(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

}
