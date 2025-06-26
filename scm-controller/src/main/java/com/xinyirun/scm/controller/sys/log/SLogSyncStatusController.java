package com.xinyirun.scm.controller.sys.log;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.sync.BSyncStatusVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.log.sys.ISLogSyncStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Wang Qianfeng
 * @date 2022/10/21 10:50
 */
@RestController
@RequestMapping("/api/v1/log/sync")
public class SLogSyncStatusController {

    @Autowired
    private ISLogSyncStatusService service;

    @PostMapping("list")
    @SysLogAnnotion("根据查询条件，获取同步日志数据表信息")
    public ResponseEntity<JsonResultAo<IPage<BSyncStatusVo>>> list(@RequestBody(required = false) BSyncStatusVo searchCondition)  {
        IPage<BSyncStatusVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }
}


