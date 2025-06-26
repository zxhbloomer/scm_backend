package com.xinyirun.scm.api.controller.busniess.out.v2;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.api.bo.steel.ApiOutPlanResultBo;
import com.xinyirun.scm.bean.api.vo.business.ApiBAllAsyncVo;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutPlanDiscontinueVo;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutPlanVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiOutPlanIdCodeVo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanDetailVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanListVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutVo;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigDetailVo;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.core.api.service.business.v1.out.v2.ApiIOutV2Service;
import com.xinyirun.scm.core.system.service.business.out.IBOutPlanDetailService;
import com.xinyirun.scm.core.system.service.business.out.IBOutService;
import com.xinyirun.scm.core.system.service.business.sync.IBSyncStatusErrorService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigDetailService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import com.xinyirun.scm.mq.rabbitmq.producer.business.sync.ywzt.SyncPush2BusinessPlatformAllInOneMqProducter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 添加出库计划
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Slf4j
// @Api(tags = "添加出库计划")
@RestController
@RequestMapping(value = "/api/service/v2/outplan")
public class ApiOutPlanInfoV2Controller extends SystemBaseController {

    @Autowired
    private ApiIOutV2Service service;

    @Autowired
    private IBOutService iBoutService;

    @Autowired
    private ISAppConfigDetailService isAppConfigDetailService;

    @Autowired
    private IBOutPlanDetailService outPlanDetailService;

    @Autowired
    private SyncPush2BusinessPlatformAllInOneMqProducter producter;

    @Autowired
    private IBSyncStatusErrorService syncErrorService;

    @SysLogApiAnnotion("7、添加出库计划")
    // @ApiOperation(value = "7、添加出库计划")
    @PostMapping("/new")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<List<ApiOutPlanResultBo>>> insert(@RequestBody ApiOutPlanVo vo, HttpServletRequest request){
        try {
            InsertResultAo<BOutPlanVo> result = service.save(vo);

            // 定义执行条件，记录执行是否成功
            ApiOutPlanIdCodeVo bean = new ApiOutPlanIdCodeVo();
            bean.setPlan_id(result.getData().getPlan_id());
            bean.setPlan_code(result.getData().getPlan_code());

            List<BOutPlanListVo> beans = outPlanDetailService.selectByPlanId(result.getData().getPlan_id());

            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
            callAsyncApiController(beans, SystemConstants.APP_URI_TYPE.OUT_PLAN_AUDIT, null);

            // 查询数据
            List<ApiOutPlanResultBo> rtns = service.getSyncOutResultAppCode10(bean);

            // 返回
            return ResponseEntity.ok().body(ApiResultUtil.OK(rtns));
        } catch (Exception e) {
            log.error("添加出库计划 new error", e);
            //返回
            return ResponseEntity.ok().body(ApiResultUtil.NG(ApiResultEnum.UNKNOWN_ERROR, SystemConstants.API_ERROR + e.getMessage(), request));
        }

    }

    @SysLogApiAnnotion("21、通知过期出库计划")
    @PostMapping("/expires")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> expires(@RequestBody String code, HttpServletRequest request){
        try {
            service.expires(code);
            // 返回
            return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
        } catch (Exception e) {
            log.error("通知过期出库计划 expires error", e);
            //返回
            return ResponseEntity.ok().body(ApiResultUtil.NG(ApiResultEnum.UNKNOWN_ERROR, SystemConstants.API_ERROR + e.getMessage(), request));
        }
    }

    @SysLogApiAnnotion("25、中止出库计划")
    @PostMapping("/discontinue")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> discontinue(@RequestBody ApiOutPlanDiscontinueVo vo, HttpServletRequest request){
        try {
            List<BOutPlanListVo> beans = service.discontinue(vo);
            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
            callAsyncApiController(beans, SystemConstants.APP_URI_TYPE.OUT_PLAN_SUBMIT, true);

        } catch (Exception e) {
            log.error("中止出库计划 discontinue error", e);
            //返回
            return ResponseEntity.ok().body(ApiResultUtil.NG(ApiResultEnum.UNKNOWN_ERROR, SystemConstants.API_ERROR + e.getMessage(), request));
        }
        //返回
        return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
    }

    @SysLogApiAnnotion("26、判断放货指令能否作废")
    @PostMapping("/cancelable")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> cancelable(@RequestBody String code, HttpServletRequest request){
        try {
            service.cancelable(code);
        } catch (Exception e) {
            log.error("判断放货指令能否作废 cancelable error", e);
            //返回
            return ResponseEntity.ok().body(ApiResultUtil.NG(ApiResultEnum.UNKNOWN_ERROR, SystemConstants.API_ERROR + e.getMessage(), request));
        }
        //返回
        return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
    }

    @SysLogApiAnnotion("27、作废放货通知下的出库单")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> cancel(@RequestBody String code, HttpServletRequest request){
        try {
            List<BOutPlanListVo> beans = service.cancel(code);
            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
            callAsyncApiController(beans, SystemConstants.APP_URI_TYPE.OUT_PLAN_CANCEL, false);
        } catch (Exception e) {
            log.error("作废放货通知下的出库单 cancel error", e);
            //返回
            return ResponseEntity.ok().body(ApiResultUtil.NG(ApiResultEnum.UNKNOWN_ERROR, SystemConstants.API_ERROR + e.getMessage(), request));
        }
        //返回
        return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
    }

    @SysLogApiAnnotion("41、完成出库计划")
    @PostMapping("/finish")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> finish(@RequestBody String code, HttpServletRequest request){
        try {
            service.finish(code);

        } catch (Exception e) {
            log.error("完成出库计划 finish error", e);
            //返回
            return ResponseEntity.ok().body(ApiResultUtil.NG(ApiResultEnum.UNKNOWN_ERROR, SystemConstants.API_ERROR + e.getMessage(), request));
        }
        //返回
        return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
    }

    /**
     * 调用API接口，同步出库信息
     * @param beans
     * @param app_config_type
     */
    private void callAsyncApiController(List<BOutPlanListVo> beans, String app_config_type, Boolean discontinue) {
        if(beans == null || beans.size() == 0){
            return;
        }
        for (BOutPlanListVo vo : beans) {
            log.debug("=============同步出库信息start=============");
            BOutPlanDetailVo data = outPlanDetailService.selectById(vo.getId());
            if(data.getExtra_code() == null  || DictConstant.DICT_B_OUT_PLAN_STATUS_CANCEL_BEING_AUDITED.equals(data.getStatus())){
                continue;
            }
            ApiOutPlanIdCodeVo apiOutPlanIdCodeBo = new ApiOutPlanIdCodeVo();
            apiOutPlanIdCodeBo.setPlan_code(data.getPlan_code());
            apiOutPlanIdCodeBo.setPlan_id(data.getPlan_id());
            apiOutPlanIdCodeBo.setDiscontinue(discontinue);
            SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, app_config_type);
            String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
            ResponseEntity<ApiOutPlanIdCodeVo> response = restTemplate.postForEntity(url, apiOutPlanIdCodeBo, ApiOutPlanIdCodeVo.class);
            log.debug("==========================="+response.getBody());

//            String jsonString = JSON.toJSONString(apiOutPlanIdCodeBo);
//            Mono<String> mono = webClient
//                    .post() // 发送POST 请求
//                    .uri(url) // 服务请求路径，基于baseurl
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(BodyInserters.fromValue(jsonString))
//                    .retrieve() // 获取响应体
//                    .bodyToMono(String.class); // 响应数据类型转换
//
//            //异步非阻塞处理响应结果
//            mono.subscribe(this::callback);
            List<BOutVo> boutVos = iBoutService.selectListByPlanId(vo.getPlan_id());
            callAsyncApiController(boutVos, SystemConstants.APP_URI_TYPE.OUT_SUBMIT);
            log.debug("=============同步出库信息end=============");
        }
    }


//    /**
//     * 响应结果处理回调方法
//     * @param result
//     */
//    public void callback(String result) {
//        log.debug("=============同步出库信息result=============" + result);
//    }

    /**
     * 调用API接口，同步出库信息
     * @param beans
     * @param app_config_type
     */
    private void callAsyncApiController(List<BOutVo> beans, String app_config_type) {
        log.debug("=============中止同步出库信息start=============");
        for (BOutVo bOutVo: beans) {
            log.debug("=============中止同步出库单code：" + bOutVo.getCode() + "=============");
        }

        if(beans == null || beans.size() == 0){
            return;
        }

        ApiBAllAsyncVo asyncVo = new ApiBAllAsyncVo();
        asyncVo.setBeans(beans);
        asyncVo.setApp_config_type(app_config_type);
        producter.mqSendMq(asyncVo, DictConstant.DICT_SYS_CODE_TYPE_B_OUT);
        Set<Integer> collect = beans.stream().map(BOutVo::getId).collect(Collectors.toSet());
        syncErrorService.updateSyncErrorStatus(collect, DictConstant.DICT_SYS_CODE_TYPE_B_OUT, "ING");
        log.debug("=============中止同步出库信息end=============");
    }

}
