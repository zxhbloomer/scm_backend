//package com.xinyirun.scm.api.controller.steel.async;
//
//import com.xinyirun.scm.api.controller.steel.base.v1.ApiBaseController;
//import com.xinyirun.scm.api.controller.steel.util.ApiSteelInUtilController;
//import com.xinyirun.scm.bean.api.vo.business.monitor.ApiBMonitorAsyncVo;
//import com.xinyirun.scm.bean.api.vo.business.monitor.ApiMonitorVo;
//import com.xinyirun.scm.bean.system.vo.business.monitor.BMonitorVo;
//import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigDetailVo;
//import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
//import com.xinyirun.scm.common.constant.SystemConstants;
//import com.xinyirun.scm.common.utils.RuntimeEnvUtil;
//import com.xinyirun.scm.core.system.service.business.monitor.IBMonitorService;
//import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigDetailService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
///**
// * @Author: Wqf
// * @Description: 监管任务同步
// * @CreateTime : 2023/5/10 13:47
// */
//
//@Slf4j
//@RestController
//@RequestMapping(value = "/api/service/v1/steel/async/monitor")
//public class ApiMonitorController extends ApiBaseController {
//
//    @Autowired
//    private IBMonitorService service;
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Autowired
//    ApiSteelInUtilController apiSteelInUtilController;
//
//    @Autowired
//    private ISAppConfigDetailService isAppConfigDetailService;
//
//    @PostMapping("/execute")
//    @SysLogApiAnnotion("监管任务同步")
//    public void monitorSync(@RequestBody(required = false) ApiBMonitorAsyncVo asyncVo) {
//        if(asyncVo.getBeans() == null || asyncVo.getBeans().size() == 0){
//            return;
//        }
//
//        // 查询数据
//        for (BMonitorVo monitor : asyncVo.getBeans()) {
//            ApiMonitorVo bean = service.selectMonitor2Sync(monitor.getId());
//            if (null == bean) {
//                continue;
//            }
//            SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, asyncVo.getApp_config_type());
//            String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
//            ResponseEntity<String> response = restTemplate.postForEntity(url, bean, String.class);
//        }
//
//
//    }
//
//
//}
