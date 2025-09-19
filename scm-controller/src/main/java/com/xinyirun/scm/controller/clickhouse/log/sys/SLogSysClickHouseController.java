package com.xinyirun.scm.controller.clickhouse.log.sys;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.SLogSysClickHouseVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirunscm.scm.clickhouse.service.SLogSysClickHouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author wwl
 */
@RestController
@RequestMapping(value = "/api/v1/log/sys/")
@Slf4j
public class SLogSysClickHouseController {

    @Autowired
    private SLogSysClickHouseService service;


    @SysLogAnnotion("根据查询条件，获取系统日志数据表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<SLogSysClickHouseVo>>> list(@RequestBody(required = false) SLogSysClickHouseVo searchCondition)  {
        searchCondition.setTenant_code(DataSourceHelper.getCurrentDataSourceName());
        IPage<SLogSysClickHouseVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取系统日志数据表信息")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SLogSysClickHouseVo>> get(@RequestBody(required = false) SLogSysClickHouseVo searchCondition)  {
        searchCondition.setTenant_code(DataSourceHelper.getCurrentDataSourceName());
        SLogSysClickHouseVo list = service.getById(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

}
