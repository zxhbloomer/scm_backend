package com.xinyirun.scm.controller.sys.datachangelog;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.mongo.datachange.SLogDataChangeOperateMongoVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.mongodb.service.log.datachange.LogChangeMongoService;
import com.xinyirun.scm.mongodb.service.log.datachange.LogChangeOperateMongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author wwl
 */
@RestController
@RequestMapping(value = "/api/v1/log/datachange/operation")
@Slf4j
public class SDataChangeLogOperateMongoController {

    @Autowired
    private LogChangeOperateMongoService logChangeOperateMongoService;

    @Autowired
    private LogChangeMongoService logChangeMongoService;


    @SysLogAnnotion("根据查询条件，获取数据变更日志数据表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<SLogDataChangeOperateMongoVo>>> list(@RequestBody(required = false) SLogDataChangeOperateMongoVo searchCondition)  {
        IPage<SLogDataChangeOperateMongoVo> list = logChangeOperateMongoService.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取数据变更日志数据表详情")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SLogDataChangeOperateMongoVo>> get(@RequestBody(required = false) SLogDataChangeOperateMongoVo searchCondition)  {
        SLogDataChangeOperateMongoVo detail = logChangeMongoService.findOperationByRequestId(searchCondition.getRequest_id());
        return ResponseEntity.ok().body(ResultUtil.OK(detail));
    }

//
//    @SysLogAnnotion("根据查询条件，获取数据变更日志数据表信息")
//    @PostMapping("/get")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<SLogApiMongoVo>> get(@RequestBody(required = false) SLogApiMongoVo searchCondition)  {
//        SLogApiMongoVo result = service.getById(searchCondition);
//        return ResponseEntity.ok().body(ResultUtil.OK(result));
//    }

}
