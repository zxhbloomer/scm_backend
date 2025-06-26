//package com.xinyirun.scm.api.controller.steel.util;
//
//import cn.hutool.core.collection.CollectionUtil;
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
//import com.xinyirun.scm.bean.api.bo.steel.*;
//import com.xinyirun.scm.bean.api.vo.business.sync.ApiBSyncStatusErrorVo;
//import com.xinyirun.scm.bean.api.vo.sync.ApiSyncVo;
//import com.xinyirun.scm.bean.entity.busniess.in.BInEntity;
//import com.xinyirun.scm.bean.entity.busniess.in.BInPlanDetailEntity;
//import com.xinyirun.scm.bean.entity.busniess.in.delivery.BDeliveryEntity;
//import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorEntity;
//import com.xinyirun.scm.bean.entity.busniess.out.BOutEntity;
//import com.xinyirun.scm.bean.entity.busniess.out.BOutPlanDetailEntity;
//import com.xinyirun.scm.bean.entity.busniess.out.receive.BReceiveEntity;
//import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
//import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
//import com.xinyirun.scm.bean.system.bo.business.alarm.BAlarmRulesBo;
//import com.xinyirun.scm.bean.system.bo.business.message.BMessageBo;
//import com.xinyirun.scm.bean.system.vo.business.sync.BSyncStatusVo;
//import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
//import com.xinyirun.scm.common.constant.ApiSteelConstants;
//import com.xinyirun.scm.common.constant.DictConstant;
//import com.xinyirun.scm.common.constant.SystemConstants;
//import com.xinyirun.scm.common.constant.WebSocketConstants;
//import com.xinyirun.scm.common.exception.api.ApiBusinessException;
//import com.xinyirun.scm.common.utils.string.StringUtils;
//import com.xinyirun.scm.core.api.service.business.v1.sync.ApiIBSyncStatusErrorService;
//import com.xinyirun.scm.core.api.service.business.v1.sync.ApiIBSyncStatusService;
//import com.xinyirun.scm.core.system.mapper.business.in.v1.BInMapper;
//import com.xinyirun.scm.core.system.mapper.business.in.v1.BInPlanDetailMapper;
//import com.xinyirun.scm.core.system.mapper.business.out.BOutMapper;
//import com.xinyirun.scm.core.system.mapper.business.out.BOutPlanDetailMapper;
//import com.xinyirun.scm.core.system.service.business.alarm.IBAlarmRulesService;
//import com.xinyirun.scm.core.system.service.business.in.v1.IBInPlanDetailService;
//import com.xinyirun.scm.core.system.service.business.in.v1.IBInService;
//import com.xinyirun.scm.core.system.service.business.in.delivery.IBDeliveryService;
//import com.xinyirun.scm.core.system.service.business.message.IBMessageService;
//import com.xinyirun.scm.core.system.service.business.monitor.IBMonitorService;
//import com.xinyirun.scm.core.system.service.business.out.IBOutPlanDetailService;
//import com.xinyirun.scm.core.system.service.business.out.IBOutService;
//import com.xinyirun.scm.core.system.service.business.out.receive.IBReceiveService;
//import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
//import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
//import com.xinyirun.scm.framework.config.websocket.util.WebSocket2Util;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.*;
//import org.springframework.retry.annotation.Backoff;
//import org.springframework.retry.annotation.Retryable;
//import org.springframework.stereotype.Component;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.net.URI;
//import java.util.List;
//
//@Slf4j
//@Component
//public class ApiSteelInUtilController extends SystemBaseController {
//
//    @Autowired
//    protected RestTemplate restTemplate;
//
//    @Autowired
//    private ISConfigService isConfigService;
//
//
//    @Autowired
//    private IBInPlanDetailService ibInPlanDetailService;
//
//    @Autowired
//    private IBInService ibInService;
//
//    @Autowired
//    private IBOutPlanDetailService ibOutPlanDetailService;
//
//    @Autowired
//    private IBOutService ibOutService;
//
//    @Autowired
//    private BInPlanDetailMapper bInPlanDetailMapper;
//
//    @Autowired
//    private BInMapper bInMapper;
//
//    @Autowired
//    private BOutPlanDetailMapper bOutPlanDetailMapper;
//
//    @Autowired
//    private BOutMapper bOutMapper;
//
//    @Autowired
//    private ApiIBSyncStatusService apiIBSyncStatusService;
//
//    @Autowired
//    private ApiIBSyncStatusErrorService apiIBSyncStatusErrorService;
//
//    protected String baseUrl;
//
//    protected String secret_key;
//
//    protected String token_secret_key;
//
//    protected HttpHeaders headers;
//
//    @Autowired
//    private WebSocket2Util webSocket2Util;
//
//    @Autowired
//    private IBAlarmRulesService rulesService;
//
//    @Autowired
//    private IBMessageService messageService;
//
//    @Autowired
//    private IBMonitorService monitorService;
//
//    @Autowired
//    private IBDeliveryService deliveryService;
//
//    @Autowired
//    private IBReceiveService ibReceiveService;
//
//
//    /**
//     * 获取业务中台的token
//     */
//    @SysLogApiAnnotion("获取业务中台的token")
//    public String getToken(String apiSteelUrl) {
//        if (StringUtils.isEmpty(apiSteelUrl)) {
//            apiSteelUrl = ApiSteelConstants.API_STEEL_URL;
//        }
//        log.debug("调用业务中台接口获取token开始-----------");
//        SConfigEntity config = isConfigService.selectByKey(apiSteelUrl);
//        baseUrl = config.getValue();
//
//        config = isConfigService.selectByKey(ApiSteelConstants.API_STEEL_SECRET_KEY);
//        token_secret_key = "Basic " + config.getValue();
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
//        params.add("grant_type", "client_credentials");
//        params.add("scope", "all");
//
//        String url = getUrl(ApiSteelConstants.API_STEEL_TOKEN_URI);
//
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
//        URI uri = builder.queryParams(params).build().encode().toUri();
//
//        headers = new HttpHeaders();
//        headers.set("Authorization", token_secret_key);
//
//        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
//
//        ResponseEntity<ApiTokenResultBo> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, ApiTokenResultBo.class);
//
//        secret_key = "Bearer " + response.getBody().getAccess_token();
//        log.debug("调用业务中台接口获取token结束-----------");
//        return secret_key;
//    }
//
//    /**
//     * 调用外部系统的url
//     *
//     * @param vo
//     * @return
//     */
//    @SysLogApiAnnotion("api-base-调用外部系统过程")
//    public ResponseEntity<JSONObject> doForward(ApiSyncVo vo) {
////        getToken();
////        String url = getUrl(vo.getSync_type());
////        headers = new HttpHeaders();
////        headers.set("Authorization", secret_key);
////
////        log.debug("传输数据的日志");
////        log.debug(JSONObject.toJSONString(vo));
////
////        HttpEntity<String> requestEntity = new HttpEntity(JSONObject.parseObject(JSONObject.toJSONString(vo.getData())), headers);
////        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, requestEntity, JSONObject.class);
////
////        // 定义执行条件，记录执行是否成功
////        BSyncStatusVo bsyncStatus = new BSyncStatusVo();
////        bsyncStatus.setSerial_id(vo.getStatusBean().getSerial_id());
////        bsyncStatus.setSerial_code(vo.getStatusBean().getSerial_code());
////        bsyncStatus.setSerial_type(vo.getStatusBean().getSerial_type());
////
////        ApiBSyncStatusErrorVo bsyncStatusError = new ApiBSyncStatusErrorVo();
////        bsyncStatusError.setSerial_id(vo.getStatusBean().getSerial_id());
////        bsyncStatusError.setSerial_code(vo.getStatusBean().getSerial_code());
////        bsyncStatusError.setSerial_type(vo.getStatusBean().getSerial_type());
////
////        /**
////         * 同步入出库单，入出库计划到
////         */
////        if (StringUtils.isEmpty(response.getBody().get("msg").toString())) {
////            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.SUCCESS);
////            bsyncStatus.setMsg("同步成功");
////            bsyncStatusError.setStatus(SystemConstants.B_SYNC_STATUS.SUCCESS);
////            bsyncStatusError.setMsg("同步成功");
////            // 保存数据，记录是否成功
////            apiIBSyncStatusService.save(bsyncStatus);
////            apiIBSyncStatusErrorService.save(bsyncStatusError);
////        } else {
////            /**
////             * 同步失败
////             */
////            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.FAILED);
////            // 保存数据，记录是否成功
////            bsyncStatus.setMsg(response.getBody().get("msg").toString());
////
////            bsyncStatusError.setStatus(SystemConstants.B_SYNC_STATUS.FAILED);
////            // 保存数据，记录是否成功
////            bsyncStatusError.setMsg(response.getBody().get("msg").toString());
////            apiIBSyncStatusService.save(bsyncStatus);
////            apiIBSyncStatusErrorService.save(bsyncStatusError);
////
////            throw new ApiBusinessException("同步失败："+response.getBody().get("msg").toString());
////            // 同步失败后，发送消息通知
//////            syncDataErrorMsgQueueMqProducter.mqSendMq(bsyncStatus);
////        }
////
////        // 返写sync_id到业务单据表
////        switch (bsyncStatus.getSerial_type()) {
////            case DictConstant.DICT_SYS_CODE_TYPE_B_OUT_PLAN:
////                ApiOutPlanResultBo apiOutPlanResultBo = (ApiOutPlanResultBo) vo.getData();
////                for (ApiOutPlanDetailResultBo resultBo : apiOutPlanResultBo.getWmsHouseOutPlanItemDtoList()) {
////                    BOutPlanDetailEntity bOutPlanDetailEntity = bOutPlanDetailMapper.selectById(resultBo.getPlan_detail_id());
////                    bOutPlanDetailEntity.setSync_id(bsyncStatus.getId());
////                    ibOutPlanDetailService.updateById(bOutPlanDetailEntity);
////                }
////                break;
////            case DictConstant.DICT_SYS_CODE_TYPE_B_OUT:
////                BOutEntity bOutEntity = bOutMapper.selectById(bsyncStatus.getSerial_id());
////                bOutEntity.setSync_id(bsyncStatus.getId());
////                ibOutService.updateById(bOutEntity);
////                break;
////            case DictConstant.DICT_SYS_CODE_TYPE_B_IN_PLAN:
////                ApiInPlanResultBo apiInPlanResultBo = (ApiInPlanResultBo) vo.getData();
////                for (ApiInPlanDetailResultBo resultBo : apiInPlanResultBo.getWmsHousePutPlanItemDtoList()) {
////                    BInPlanDetailEntity bInPlanDetailEntity = bInPlanDetailMapper.selectById(resultBo.getPlan_detail_id());
////                    bInPlanDetailEntity.setSync_id(bsyncStatus.getId());
////                    ibInPlanDetailService.updateById(bInPlanDetailEntity);
////                }
////                break;
////            case DictConstant.DICT_SYS_CODE_TYPE_B_IN:
////                BInEntity bInEntity = bInMapper.selectById(bsyncStatus.getSerial_id());
////                bInEntity.setSync_id(bsyncStatus.getId());
////                ibInService.updateById(bInEntity);
////                break;
////
////        }
////
////        return response;
//
//        try {
//            return doForwardRetryable(vo);
//        } catch (ApiBusinessException e) {
//            log.error(e.getMessage());
//            // 同步失败后，发送消息通知
////            syncDataErrorMsgQueueMqProducter.mqSendMq(bsyncStatus);
//            throw e;
//        }
//    }
//
//    /**
//     * 调用外部系统的url
//     *
//     * @param vo
//     * @return
//     */
//    @SysLogApiAnnotion("api-base-调用外部系统过程Retryable:3")
//    @Retryable(value = {ApiBusinessException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 1.5))
//    public ResponseEntity<JSONObject> doForwardRetryable(ApiSyncVo vo) {
//        getToken(vo.getApiSteelUrl());
//        String url = getUrl(vo.getSync_type());
//        headers = new HttpHeaders();
//        headers.set("Authorization", secret_key);
//
//        log.debug("传输数据的日志");
//        log.debug("参数 --> {}", JSONObject.toJSONString(vo.getData()));
//
//        HttpEntity<String> requestEntity = new HttpEntity(JSONObject.parseObject(JSONObject.toJSONString(vo.getData())), headers);
//        JSONObject responseBody = new JSONObject();
//        ResponseEntity<JSONObject> response = null;
//        // 如果对方直接返回500, 可能会有问题
//        try {
//            response = restTemplate.postForEntity(url, requestEntity, JSONObject.class);
//            responseBody = response.getBody();
//        } catch (Exception e) {
//            responseBody.put("msg", e.getMessage());
//            log.error("参数 --> {}", JSONObject.toJSONString(vo.getData()));
//            log.error("doForwardRetryable error", e.getMessage());
////            throw new ApiBusinessException("同步失败："+responseBody.get("msg").toString());
//        } finally {
//            // 执行后续
//            doAfter(responseBody, vo);
//        }
//
//        return response;
//    }
//
//    private void doAfter(JSONObject responseBody , ApiSyncVo vo) {
//        // 定义执行条件，记录执行是否成功
//        BSyncStatusVo bsyncStatus = new BSyncStatusVo();
//        bsyncStatus.setSerial_id(vo.getStatusBean().getSerial_id());
//        bsyncStatus.setSerial_code(vo.getStatusBean().getSerial_code());
//        bsyncStatus.setSerial_detail_code(vo.getStatusBean().getSerial_detail_code());
//        bsyncStatus.setSerial_detail_id(vo.getStatusBean().getSerial_detail_id());
//        bsyncStatus.setSerial_type(vo.getStatusBean().getSerial_type());
//
//        ApiBSyncStatusErrorVo bsyncStatusError = new ApiBSyncStatusErrorVo();
//        bsyncStatusError.setSerial_id(vo.getStatusBean().getSerial_id());
//        bsyncStatusError.setSerial_code(vo.getStatusBean().getSerial_code());
//        bsyncStatusError.setSerial_type(vo.getStatusBean().getSerial_type());
//        bsyncStatusError.setSerial_detail_code(vo.getStatusBean().getSerial_detail_code());
//        bsyncStatusError.setSerial_detail_id(vo.getStatusBean().getSerial_detail_id());
//        Integer sync_id;
//
//        /**
//         * 同步入出库单，入出库计划到
//         */
//        if (StringUtils.isEmpty(responseBody.get("msg").toString())) {
//            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.SUCCESS);
//            bsyncStatus.setMsg("同步成功");
//            bsyncStatusError.setStatus(SystemConstants.B_SYNC_STATUS.SUCCESS);
//            bsyncStatusError.setMsg("同步成功");
//            // 保存数据，记录是否成功
//            apiIBSyncStatusService.save(bsyncStatus);
//            InsertResultAo<Integer> save = apiIBSyncStatusErrorService.save(bsyncStatusError);
//            // 删除 b_message 表相关信息
//            messageService.deleteNotice(vo.getStatusBean().getSerial_id(), vo.getStatusBean().getSerial_code(), vo.getStatusBean().getSerial_type());
//            sync_id = save.getData();
//        } else {
//            /**
//             * 同步失败
//             */
//            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.FAILED);
//            // 保存数据，记录是否成功
//            bsyncStatus.setMsg(responseBody.get("msg").toString());
//
//            bsyncStatusError.setStatus(SystemConstants.B_SYNC_STATUS.FAILED);
//            // 保存数据，记录是否成功
//            bsyncStatusError.setMsg(responseBody.get("msg").toString());
//            apiIBSyncStatusService.save(bsyncStatus);
//            sync_id = apiIBSyncStatusErrorService.save(bsyncStatusError).getData();
//            log.debug("service层, 发送失败");
//            try {
//                List<BAlarmRulesBo> bAlarmRulesBoList = rulesService.selectStaffAlarm(DictConstant.DICT_B_ALARM_RULES_TYPE_0);
//                // 将错误信息存入 b_message 表
//                BMessageBo messageBo = new BMessageBo();
//                messageBo.setSerial_id(vo.getStatusBean().getSerial_id());
//                messageBo.setSerial_code(vo.getStatusBean().getSerial_code());
//                // 删除serial_id 和 serial_code 相同的
//                messageService.deleteNotice(vo.getStatusBean().getSerial_id(), vo.getStatusBean().getSerial_code(), vo.getStatusBean().getSerial_type());
//                messageService.insert(CollectionUtil.toList(messageBo), DictConstant.DICT_B_MESSAGE_TYPE_1, vo.getStatusBean().getSerial_type());
//                log.debug("发送参数, {}", JSONObject.toJSONString(bAlarmRulesBoList));
//                webSocket2Util.convertAndSendUserAlarmNotice(bAlarmRulesBoList, WebSocketConstants.WEBSOCKET_SYNC_LOG);
////                simpMessagingTemplate.convertAndSend(WebSocketConstants.WEBSOCKET_SYNC_LOG, true);
//            } catch (Exception e) {
//                log.error("websocket 发送日志错误, {}", e.getMessage());
//            }
//        }
//
//        // 返写sync_id到业务单据表
//        switch (bsyncStatus.getSerial_type()) {
//            case DictConstant.DICT_SYS_CODE_TYPE_B_OUT_PLAN:
//                ApiOutPlanResultBo apiOutPlanResultBo = (ApiOutPlanResultBo) vo.getData();
//                for (ApiOutPlanDetailResultBo resultBo : apiOutPlanResultBo.getWmsHouseOutPlanItemDtoList()) {
//                    BOutPlanDetailEntity bOutPlanDetailEntity = bOutPlanDetailMapper.selectById(resultBo.getPlan_detail_id());
//                    bOutPlanDetailEntity.setSync_id(sync_id);
//                    ibOutPlanDetailService.updateById(bOutPlanDetailEntity);
//                }
//                break;
//            case DictConstant.DICT_SYS_CODE_TYPE_B_OUT:
//             /*   BOutEntity bOutEntity = bOutMapper.selectById(bsyncStatus.getSerial_id());
//                bOutEntity.setSync_id(sync_id);
//                ibOutService.updateById(bOutEntity);*/
//
//                ibOutService.update(new LambdaUpdateWrapper<BOutEntity>()
//                        .eq(BOutEntity::getId, bsyncStatus.getSerial_id())
//                        .set(BOutEntity::getSync_id, sync_id));
//                break;
//            case DictConstant.DICT_SYS_CODE_TYPE_B_IN_PLAN:
//                ApiInPlanResultBo apiInPlanResultBo = (ApiInPlanResultBo) vo.getData();
//                for (ApiInPlanDetailResultBo resultBo : apiInPlanResultBo.getWmsHousePutPlanItemDtoList()) {
//                    BInPlanDetailEntity bInPlanDetailEntity = bInPlanDetailMapper.selectById(resultBo.getPlan_detail_id());
//                    bInPlanDetailEntity.setSync_id(sync_id);
//                    ibInPlanDetailService.updateById(bInPlanDetailEntity);
//                }
//                break;
//            case DictConstant.DICT_SYS_CODE_TYPE_B_IN:
//             /*   BInEntity bInEntity = bInMapper.selectById(bsyncStatusError.getSerial_id());
//                bInEntity.setSync_id(sync_id);
//                ibInService.updateById(bInEntity);*/
//
//                ibInService.update(new LambdaUpdateWrapper<BInEntity>().eq(BInEntity::getId, bsyncStatusError.getSerial_id())
//                        .set(BInEntity::getSync_id, sync_id));
//                break;
//            case DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR:
//                // 监管任务
//
//
//                break;
//            case DictConstant.DICT_SYS_CODE_TYPE_B_DELIVERY:
//                deliveryService.update(new LambdaUpdateWrapper<BDeliveryEntity>().eq(BDeliveryEntity::getId, bsyncStatusError.getSerial_id())
//                        .set(BDeliveryEntity::getSync_id, sync_id));
//                break;
//            case DictConstant.DICT_SYS_CODE_TYPE_B_RECEIVE:
//                ibReceiveService.update(new LambdaUpdateWrapper<BReceiveEntity>().eq(BReceiveEntity::getId, bsyncStatusError.getSerial_id())
//                        .set(BReceiveEntity::getSync_id, sync_id));
//                break;
//            default:
//                break;
//        }
//
//    }
//
//    @SysLogApiAnnotion("api-base-调用外部系统过程Retryable:3")
//    @Retryable(value = {ApiBusinessException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 1.5))
//    public ResponseEntity<JSONObject> doForwardRetryableList(ApiSyncVo vo) {
//        getToken(vo.getApiSteelUrl());
//        String url = getUrl(vo.getSync_type());
//        headers = new HttpHeaders();
//        headers.set("Authorization", secret_key);
//
//        log.debug("传输数据的日志");
//        log.debug(JSONObject.toJSONString(vo));
//
//        HttpEntity<String> requestEntity = new HttpEntity(JSONArray.parseArray(JSONObject.toJSONString(vo.getData())), headers);
//        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, requestEntity, JSONObject.class);
//
//        // 定义执行条件，记录执行是否成功
//        BSyncStatusVo bsyncStatus = new BSyncStatusVo();
//        bsyncStatus.setSerial_id(vo.getStatusBean().getSerial_id());
//        bsyncStatus.setSerial_code(vo.getStatusBean().getSerial_code());
//        bsyncStatus.setSerial_detail_code(vo.getStatusBean().getSerial_detail_code());
//        bsyncStatus.setSerial_detail_id(vo.getStatusBean().getSerial_detail_id());
//        bsyncStatus.setSerial_type(vo.getStatusBean().getSerial_type());
//
//        ApiBSyncStatusErrorVo bsyncStatusError = new ApiBSyncStatusErrorVo();
//        bsyncStatusError.setSerial_id(vo.getStatusBean().getSerial_id());
//        bsyncStatusError.setSerial_code(vo.getStatusBean().getSerial_code());
//        bsyncStatusError.setSerial_type(vo.getStatusBean().getSerial_type());
//        bsyncStatusError.setSerial_detail_code(vo.getStatusBean().getSerial_detail_code());
//        bsyncStatusError.setSerial_detail_id(vo.getStatusBean().getSerial_detail_id());
//        Integer sync_id;
//        String sync_status;
//
//        /**
//         * 同步入出库单，入出库计划到
//         */
//        if ("0".equals(response.getBody().get("code").toString())) {
//            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.SUCCESS);
//            bsyncStatus.setMsg("同步成功");
//            bsyncStatusError.setStatus(SystemConstants.B_SYNC_STATUS.SUCCESS);
//            bsyncStatusError.setMsg("同步成功");
//            // 保存数据，记录是否成功
//            apiIBSyncStatusService.save(bsyncStatus);
//            InsertResultAo<Integer> save = apiIBSyncStatusErrorService.save(bsyncStatusError);
//            // 删除 b_message 表相关信息
//            messageService.deleteNotice(vo.getStatusBean().getSerial_id(), vo.getStatusBean().getSerial_code(), vo.getStatusBean().getSerial_type());
//            sync_id = save.getData();
//            sync_status = DictConstant.DICT_B_MONITOR_IS_SYNC_1;
//        } else {
//            /**
//             * 同步失败
//             */
//            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.FAILED);
//            // 保存数据，记录是否成功
//            bsyncStatus.setMsg(response.getBody().get("msg").toString());
//
//            bsyncStatusError.setStatus(SystemConstants.B_SYNC_STATUS.FAILED);
//            // 保存数据，记录是否成功
//            bsyncStatusError.setMsg(response.getBody().get("msg").toString());
//            apiIBSyncStatusService.save(bsyncStatus);
//            sync_id = apiIBSyncStatusErrorService.save(bsyncStatusError).getData();
//            sync_status = DictConstant.DICT_B_MONITOR_IS_SYNC_2;
//            log.debug("service层, 发送失败");
//            try {
//                List<BAlarmRulesBo> bAlarmRulesBoList = rulesService.selectStaffAlarm(DictConstant.DICT_B_ALARM_RULES_TYPE_0);
//                // 将错误信息存入 b_message 表
//                BMessageBo messageBo = new BMessageBo();
//                messageBo.setSerial_id(vo.getStatusBean().getSerial_id());
//                messageBo.setSerial_code(vo.getStatusBean().getSerial_code());
//                // 删除serial_id 和 serial_code 相同的
//                messageService.deleteNotice(vo.getStatusBean().getSerial_id(), vo.getStatusBean().getSerial_code(), DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR_SYNC);
//                messageService.insert(CollectionUtil.toList(messageBo), DictConstant.DICT_B_MESSAGE_TYPE_1, DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR_SYNC);
//                log.debug("发送参数, {}", JSONObject.toJSONString(bAlarmRulesBoList));
//                webSocket2Util.convertAndSendUserAlarmNotice(bAlarmRulesBoList, WebSocketConstants.WEBSOCKET_SYNC_LOG);
//            } catch (Exception e) {
//                log.error("websocket 发送日志错误, {}", e.getMessage());
//            }
////            throw new ApiBusinessException("同步失败："+response.getBody().get("msg").toString());
//        }
//
//        // 返写sync_id到业务单据表
//        switch (bsyncStatus.getSerial_type()) {
//            case DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR:
//                // 监管任务
//                // 更新监管任务同步的ID
//                monitorService.update(new LambdaUpdateWrapper<BMonitorEntity>().eq(BMonitorEntity::getId, bsyncStatusError.getSerial_id())
//                        .set(BMonitorEntity::getSync_id, sync_id)
//                        .set(BMonitorEntity::getIs_sync, sync_status));
//                break;
//            default:
//                break;
//        }
//
//        return response;
//    }
//
//
//
//    /**
//     * 调用外部系统的url
//     *
//     * @param vo
//     * @return
//     */
//    @SysLogApiAnnotion("api-base-调用外部系统过程")
//    public ResponseEntity<JSONObject> doForwardForm(ApiSyncVo vo) {
//        getToken(vo.getApiSteelUrl());
//        String url = getUrl(vo.getSync_type());
//        headers = new HttpHeaders();
//        headers.set("Authorization", secret_key);
//
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>((MultiValueMap<String, String>) vo.getData(), headers);
//
//        log.debug("传输数据的日志");
//        log.debug(JSONObject.toJSONString(vo));
//
//        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, requestEntity, JSONObject.class);
//        return response;
//    }
//
//    /**
//     * 调用外部系统的url
//     *
//     * @param vo
//     * @return
//     */
//    @SysLogApiAnnotion("api-base-调用外部系统过程")
//    public ResponseEntity<JSONObject> doForwardFormGet(ApiSyncVo vo) throws Exception {
//        getToken(vo.getApiSteelUrl());
//        String url = getUrl(vo.getSync_type());
//        headers = new HttpHeaders();
//        headers.set("Authorization", secret_key);
//
//        // 设置请求头
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        // 构建URL参数
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
//        MultiValueMap<String, Object> params = (MultiValueMap<String, Object>) vo.getData();
//        params.forEach((key, values) -> values.forEach(value -> builder.queryParam(key, value)));
//
//
//        // 创建HttpEntity对象，只包含请求头
//        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
//
//
//        log.debug("传输数据的日志");
//        log.debug(JSONObject.toJSONString(vo));
//
//        // 发送GET请求，并返回响应
//        ResponseEntity<JSONObject> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, requestEntity, JSONObject.class);
//        System.out.println(response);
//        return response;
//    }
//
//    /**
//     * 调用外部系统的url
//     *
//     * @param vo
//     * @return
//     */
//    @SysLogApiAnnotion("api-base-调用外部系统过程")
//    public ResponseEntity<JSONObject> doForwardArray(ApiSyncVo vo) {
//        getToken(vo.getApiSteelUrl());
//        String url = getUrl(vo.getSync_type());
//        headers = new HttpHeaders();
//        headers.set("Authorization", secret_key);
//
//        log.debug("传输数据的日志");
//        log.debug(JSONObject.toJSONString(vo));
//
//        HttpEntity<String> requestEntity = new HttpEntity(JSONArray.parseArray(JSONObject.toJSONString(vo.getData())), headers);
//        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, requestEntity, JSONObject.class);
//
//        return response;
//    }
//
////    /**
////     *
////     * @param plan_detail_id
////     * @param app_config_type
////     */
////    @SysLogAnnotion("调用API接口，同步入库信息")
////    private void callSyncApiController(Integer plan_detail_id, String app_config_type) {
////        BInPlanListVo vo = new BInPlanListVo();
////        vo.setId(plan_detail_id);
////        callSyncApiController(service.selectList(vo), app_config_type);
////    }
//
////    /**
////     * 调用API接口，同步入库信息
////     * @param beans
////     * @param app_config_type
////     */
////    @SysLogAnnotion("调用API接口，同步入库信息，多条")
////    private void callSyncApiController(List<BInPlanListVo> beans, String app_config_type) {
////        if(beans == null || beans.size() == 0){
////            return;
////        }
////        for (BInPlanListVo vo : beans) {
////            if(vo.getExtra_code() == null){
////                continue;
////            }
////            ApiInPlanIdCodeVo apiInPlanIdCodeBo = new ApiInPlanIdCodeVo();
////            apiInPlanIdCodeBo.setPlan_code(vo.getPlan_code());
////            apiInPlanIdCodeBo.setPlan_id(vo.getPlan_id());
////            SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, app_config_type);
////            String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
////            ResponseEntity<ApiInPlanIdCodeVo> response = restTemplate.postForEntity(url, apiInPlanIdCodeBo, ApiInPlanIdCodeVo.class);
////            System.out.println(response.getBody());
////        }
////    }
//
//    /**
//     * 获取业务中调用的url
//     *
//     * @param key
//     * @return
//     */
//    private String getUrl(String key) {
//        SConfigEntity config = isConfigService.selectByKey(key);
//        return baseUrl + config.getValue();
//    }
//}
