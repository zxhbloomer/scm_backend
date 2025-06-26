package com.xinyirun.scm.controller.mongobackup.log.mq;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.mongo.log.SLogMqConsumerMongoVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.mongodb.service.log.mq.ISLogMqConsumerService;
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
@RequestMapping("/api/v1/log/mq/consumer")
public class SLogMqConsumerController {

    @Autowired
    private ISLogMqConsumerService service;

    @SysLogAnnotion("根据查询条件，获取系统日志数据表信息")
    @PostMapping("/page_list")
    public ResponseEntity<JsonResultAo<IPage<SLogMqConsumerMongoVo>>> list(@RequestBody(required = false) SLogMqConsumerMongoVo searchCondition)  {
        IPage<SLogMqConsumerMongoVo> list = service.selectPageList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("查询消费者详情")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<SLogMqConsumerMongoVo>> get(@RequestBody(required = false) SLogMqConsumerMongoVo searchCondition)  {
        SLogMqConsumerMongoVo list = service.getById(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

}
