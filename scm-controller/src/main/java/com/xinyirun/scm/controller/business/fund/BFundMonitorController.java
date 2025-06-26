package com.xinyirun.scm.controller.business.fund;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.fund.BFundMonitorVo;
import com.xinyirun.scm.bean.system.vo.business.fund.BFundUsageVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.business.fund.IBFundMonitorService;
import com.xinyirun.scm.core.system.service.business.fund.IBFundUsageService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 资金流水情况表 前端控制器
 * </p>
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/fund/monitor")
public class BFundMonitorController extends SystemBaseController {

    @Autowired
    private IBFundMonitorService service;

    @SysLogAnnotion("资金流水情况表，获取列表信息")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BFundMonitorVo>>> selectPagelist(@RequestBody(required = false) BFundMonitorVo searchCondition) {
        IPage<BFundMonitorVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }
}
