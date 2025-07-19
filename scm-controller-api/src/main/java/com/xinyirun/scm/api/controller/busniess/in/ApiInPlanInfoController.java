package com.xinyirun.scm.api.controller.busniess.in;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.api.bo.steel.ApiInPlanResultBo;
import com.xinyirun.scm.bean.api.vo.business.in.ApiInPlanDisContinuedVo;
import com.xinyirun.scm.bean.api.vo.business.in.ApiInPlanVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiInPlanIdCodeVo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.core.api.service.business.v1.in.ApiIInService;
import com.xinyirun.scm.core.system.service.business.wms.inplan.IBInPlanDetailService;
import com.xinyirun.scm.core.system.service.business.wms.inplan.IBInPlanService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigDetailService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 添加入库计划
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/service/v1/inplan")
public class ApiInPlanInfoController extends SystemBaseController {

    @Autowired
    private IBInPlanService iBinService;

    @Autowired
    private ApiIInService service;

    @Autowired
    private IBInPlanDetailService inPlanDetailService;

    @Autowired
    private ISAppConfigDetailService isAppConfigDetailService;

    @SysLogApiAnnotion("3、添加入库计划")
    @PostMapping("/new")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<List<ApiInPlanResultBo>>> save(@RequestBody ApiInPlanVo vo, HttpServletRequest request){
        try {
            InsertResultAo<ApiInPlanIdCodeVo> result = service.save(vo);

            // 定义执行条件，记录执行是否成功
            // 查询数据
            List<ApiInPlanResultBo> rtns = service.getSyncInResultAppCode10(result.getData());

//            List<BInPlanVo> beans = inPlanDetailService.selectByPlanId(result.getData().getPlan_id());
//            callAsyncApiController(beans, SystemConstants.APP_URI_TYPE.IN_PLAN_AUDIT);

            // 返回
            return ResponseEntity.ok().body(ApiResultUtil.OK(rtns));
        } catch (Exception e) {
            log.error("3、添加入库计划错误", e);
            //返回
            return ResponseEntity.ok().body(ApiResultUtil.NG(ApiResultEnum.UNKNOWN_ERROR, SystemConstants.API_ERROR + e.getMessage(), request));
        }

    }

    @SysLogApiAnnotion("37. 入库通知中止")
    @PostMapping("/discontinue")
    public ResponseEntity<ApiJsonResultAo<String>> discontinue(@RequestBody ApiInPlanDisContinuedVo param, HttpServletRequest request){
        try {
            service.discontinue(param);
            // 返回
            return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
        } catch (Exception e) {
            log.error("入库通知中止 discontinue:", e);
            //返回
            return ResponseEntity.ok().body(ApiResultUtil.NG(ApiResultEnum.UNKNOWN_ERROR, SystemConstants.API_ERROR + e.getMessage(), request));
        }

    }

    /**
     * 调用API接口，同步入库信息
     * @param beans
     * @param app_config_type
     */
//    private void callAsyncApiController(List<BInPlanListVo> beans, String app_config_type) {
//        if(beans == null || beans.size() == 0){
//            return;
//        }
//        for (BInPlanListVo vo : beans) {
//            log.debug("=============同步入库信息start=============");
//            BInPlanDetailVo data = inPlanDetailService.selectById(vo.getId());
//            if(data.getExtra_code() == null){
//                continue;
//            }
//            ApiInPlanIdCodeVo apiInPlanIdCodeBo = new ApiInPlanIdCodeVo();
//            apiInPlanIdCodeBo.setPlan_code(data.getPlan_code());
//            apiInPlanIdCodeBo.setPlan_id(data.getPlan_id());
//            SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, app_config_type);
//            String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
//            ResponseEntity<ApiInPlanIdCodeVo> response = restTemplate.postForEntity(url, apiInPlanIdCodeBo, ApiInPlanIdCodeVo.class);
//            log.debug("==============================="+response.getBody());
//
////            String jsonString = JSON.toJSONString(apiInPlanIdCodeBo);
////            Mono<String> mono = webClient
////                    .post() // 发送POST 请求
////                    .uri(url) // 服务请求路径，基于baseurl
////                    .contentType(MediaType.APPLICATION_JSON)
////                    .body(BodyInserters.fromValue(jsonString))
////                    .retrieve() // 获取响应体
////                    .bodyToMono(String.class); // 响应数据类型转换
////
////            //异步非阻塞处理响应结果
////            mono.subscribe(this::callback);
//
//            log.debug("=============同步入库信息end=============");
//        }
//    }

//    /**
//     * 响应结果处理回调方法
//     * @param result
//     */
//    public void callback(String result) {
//        log.debug("=============同步入库信息result=============" + result);
//    }
}
