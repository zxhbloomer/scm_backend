package com.xinyirun.scm.controller.sys.datachangelog;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeDetailClickHouseVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeMainClickHouseVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeOperateClickHouseVo;
import com.xinyirun.scm.clickhouse.service.datachange.SLogDataChangeDetailClickHouseService;
import com.xinyirun.scm.clickhouse.service.datachange.SLogDataChangeMainClickHouseService;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 按单号查询数据变更
 */
@RestController
@RequestMapping(value = "/api/v1/log/datachange/main")
@Slf4j
public class SDataChangeLogMainClickHouseController {

    @Autowired
    private SLogDataChangeMainClickHouseService logChangeMainMongoService;

    @Autowired
    private SLogDataChangeDetailClickHouseService logChangeMongoService;


    @SysLogAnnotion("根据查询条件，获取数据变更日志数据表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<SLogDataChangeMainClickHouseVo>>> list(@RequestBody(required = false) SLogDataChangeMainClickHouseVo searchCondition)  {
        IPage<SLogDataChangeMainClickHouseVo> list = logChangeMainMongoService.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取数据变更日志数据表详情")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SLogDataChangeOperateClickHouseVo>> get(@RequestBody(required = false) SLogDataChangeDetailClickHouseVo searchCondition)  {
        SLogDataChangeOperateClickHouseVo detail = logChangeMongoService.findMainByOrderCode(searchCondition.getOrder_code());
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
