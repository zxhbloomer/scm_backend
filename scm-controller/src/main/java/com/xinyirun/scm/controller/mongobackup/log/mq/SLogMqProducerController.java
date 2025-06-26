package com.xinyirun.scm.controller.mongobackup.log.mq;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.mongo.log.SLogMqProducerMongoVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.mongodb.service.log.mq.ISLogMqProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Wqf
 * @Description: mq 生产者日志查询
 * @CreateTime : 2023/4/17 16:13
 */

@RestController
@RequestMapping("/api/v1/log/mq/producer")
public class SLogMqProducerController {

    @Autowired
    private ISLogMqProducerService service;

    @SysLogAnnotion("根据查询条件，获取系统日志数据表信息")
    @PostMapping("/page_list")
    public ResponseEntity<JsonResultAo<IPage<SLogMqProducerMongoVo>>> list(@RequestBody(required = false) SLogMqProducerMongoVo searchCondition)  {
        IPage<SLogMqProducerMongoVo> list = service.selectPageList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("查询生产者详情")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<SLogMqProducerMongoVo>> get(@RequestBody(required = false) SLogMqProducerMongoVo searchCondition)  {
        SLogMqProducerMongoVo list = service.getById(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

}
