package com.xinyirun.scm.controller.sys.datachangelog;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeOperateClickHouseVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.clickhouse.service.datachange.SLogDataChangeDetailClickHouseService;
import com.xinyirun.scm.clickhouse.service.datachange.SLogDataChangeOperateClickHouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 按员工操作查询数据变更日志
 */
@RestController
@RequestMapping(value = "/api/v1/log/datachange/operation")
@Slf4j
public class SDataChangeLogOperateClickHouseController {

    @Autowired
    private SLogDataChangeOperateClickHouseService logChangeOperateMongoService;

    @Autowired
    private SLogDataChangeDetailClickHouseService logChangeMongoService;


    @SysLogAnnotion("根据查询条件，获取数据变更日志数据表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<SLogDataChangeOperateClickHouseVo>>> list(@RequestBody(required = false) SLogDataChangeOperateClickHouseVo searchCondition)  {
        IPage<SLogDataChangeOperateClickHouseVo> list = logChangeOperateMongoService.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取数据变更日志数据表详情")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SLogDataChangeOperateClickHouseVo>> get(@RequestBody(required = false) SLogDataChangeOperateClickHouseVo searchCondition)  {
        SLogDataChangeOperateClickHouseVo detail = logChangeMongoService.findOperationByRequestId(searchCondition.getRequest_id());
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
