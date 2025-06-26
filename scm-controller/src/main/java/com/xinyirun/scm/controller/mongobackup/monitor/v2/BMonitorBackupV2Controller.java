package com.xinyirun.scm.controller.mongobackup.monitor.v2;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorVo;
import com.xinyirun.scm.bean.system.vo.mongo.monitor.v2.*;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.LimitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.properies.SystemConfigProperies;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.mongobackup.monitor.v2.IBMonitorBackupBusinessV2Service;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import com.xinyirun.scm.mongodb.service.monitor.v2.IMonitorDataMongoV2Service;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Wang Qianfeng
 * @Description 监管任务备份
 * @date 2023/2/10 11:07
 */
@RestController
@RequestMapping("/api/v2/monitor/backup")
@Slf4j
public class BMonitorBackupV2Controller extends SystemBaseController {

    @Autowired
    private IBMonitorBackupBusinessV2Service service;

    @Autowired
    private ISPagesService isPagesService;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    private SystemConfigProperies systemConfigProperies;

    @Autowired
    private IMonitorDataMongoV2Service mongoService;

    @Autowired
    private WebClient webClient;


//    @PostMapping("/{size}")
//    public ResponseEntity<JsonResultAo<String>> selectList(@PathVariable(value = "size") @NotNull Integer size) {
//        service.backupDataLimitSize(size);
//        return ResponseEntity.ok().body(ResultUtil.OK("ok"));
//    }

    /**
     * 同步数据, 限流
     * @param param
     * @return
     */
    @PostMapping("/async")
    @LimitAnnotion(key = "BMonitorBackupV2Controller.async", period = 10, count = 1, name = "监管任务数据备份", prefix = "limit")
    public ResponseEntity<JsonResultAo<String>> backup(@RequestBody(required = false) BBkMonitorVo param) {
        param.setType(DictConstant.DICT_B_MONITOR_BACKUP_TYPE_1);
        Integer integer = service.insertBackupLog(param);
        param.setStaff_id(SecurityUtil.getStaff_id());
        param.setLog_id(integer);
        callAsyncApiController(param);
        return ResponseEntity.ok().body(ResultUtil.OK("ok"));
    }

    @PostMapping("/count")
    public ResponseEntity<JsonResultAo<BBkMonitorVo>> count(@RequestBody(required = false) BBkMonitorVo param) {
        if (null == param.getStart_time() && null == param.getOver_time()) {
            return ResponseEntity.ok().body(ResultUtil.OK(new BBkMonitorVo()));
        }
        BBkMonitorVo bBkMonitorVo = service.selectPageMyCount(param);
        return ResponseEntity.ok().body(ResultUtil.OK(bBkMonitorVo));
    }

    @PostMapping("/restore")
    @LimitAnnotion(key = "BMonitorBackupController.restore", period = 10, count = 1, name = "监管任务数据恢复", prefix = "limit")
    public ResponseEntity<JsonResultAo<String>> restore(@RequestBody(required = false) BBkMonitorVo param) {
        param.setType(DictConstant.DICT_B_MONITOR_BACKUP_TYPE_2);
        Integer integer = service.insertBackupLog(param);

        // 更新所有监管任务为不可见状态
        mongoService.updateVisibilityStatusByIds(param.getIds(), DictConstant.DICT_B_MONITOR_MONGO_IS_SHOW_F);

        param.setStaff_id(SecurityUtil.getStaff_id());
        param.setLog_id(integer);
        callRenewAsyncApiController(param);
        return ResponseEntity.ok().body(ResultUtil.OK("ok"));
    }

    @PostMapping("/getfiles")
    public ResponseEntity<JsonResultAo<BBkMonitorVo>> getFiles(@RequestBody(required = false) BBkMonitorVo param) {
        BBkMonitorVo bBkMonitorVo = mongoService.getFiles(param);
        return ResponseEntity.ok().body(ResultUtil.OK(bBkMonitorVo));
    }


/*    @PostMapping("/restore/count")
    public ResponseEntity<JsonResultAo<Long>> restoreCount(@RequestBody(required = false) BBkMonitorVo param) {
        long count = monitorMongoService.getCount2Restore(param);
        return ResponseEntity.ok().body(ResultUtil.OK(count));
    }*/

    @PostMapping("/list")
    public ResponseEntity<JsonResultAo<Page<BMonitorBackupV2Vo>>> selectList(@RequestBody(required = false) BMonitorBackupV2Vo searchCondition) {
        Page<BMonitorBackupV2Vo> result = mongoService.selectPageList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/sum")
    public ResponseEntity<JsonResultAo<BMonitorBackupSumV2Vo>> sumData(@RequestBody(required = false) BMonitorBackupV2Vo searchCondition) {
        BMonitorBackupSumV2Vo result = mongoService.selectSumData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<BMonitorBackupDetailV2Vo>> getDetail(@RequestBody(required = false) BMonitorBackupV2Vo searchCondition) {
        BMonitorBackupDetailV2Vo result = mongoService.getDetail(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("导出监管任务历史记录附件信息")
    @PostMapping("/export_file")
    public ResponseEntity<JsonResultAo<String>> export(@RequestBody(required = false) @NotEmpty(message = "请选择导出数据")
                                                               List<BMonitorBackupV2Vo> searchCondition) throws Exception {
        SPagesVo sPagesVo = new SPagesVo();
        sPagesVo.setCode(PageCodeConstant.P_MONITOR_HISTORY);
        SPagesVo pagesVo = isPagesService.get(sPagesVo);
        if (Objects.equals(pagesVo.getExport_processing(), Boolean.TRUE)) {
            throw new BusinessException("还有未完成的导出任务，请稍后重试");
        }
        // 执行导出操作
        try {
            isPagesService.updateExportProcessingTrue(pagesVo);
            List<BMonitorFileDownloadMongoV2Vo> list = mongoService.exportFile(searchCondition);
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("items", list);
            SConfigEntity exportUrl = isConfigService.selectByKey(SystemConstants.EXPORT_URL);
            String downloadUrl = (String) executeFileSystemDownloadUrlLogic(paramMap, exportUrl.getValue());
            return ResponseEntity.ok().body(ResultUtil.OK(downloadUrl));
        } catch (Exception e) {
            throw e;
        } finally {
            isPagesService.updateExportProcessingFalse(pagesVo);
        }
    }

    @SysLogAnnotion("监管任务数据部分导出")
    @PostMapping("/export_data")
    public void exportData(@RequestBody(required = false) List<BMonitorBackupV2Vo> searchCondition, HttpServletResponse response) throws Exception {
        List<BMonitorMongoExportV2Vo> list = mongoService.selectExportList(searchCondition);
        EasyExcelUtil<BMonitorMongoExportV2Vo> util = new EasyExcelUtil<>(BMonitorMongoExportV2Vo.class);
        util.exportExcel("监管任务单" + DateTimeUtil.getDate(), "监管任务单", list, response);
    }

    @SysLogAnnotion("监管任务数据全部导出")
    @PostMapping("/exportall_data")
    public void exportData(@RequestBody(required = false) BMonitorBackupV2Vo searchCondition, HttpServletResponse response) throws Exception {
        List<BMonitorMongoExportV2Vo> list = mongoService.selectExportAllList(searchCondition);
        EasyExcelUtil<BMonitorMongoExportV2Vo> util = new EasyExcelUtil<>(BMonitorMongoExportV2Vo.class);
        util.exportExcel("监管任务单" + DateTimeUtil.getDate(), "监管任务单", list, response);
    }



    /**
     * 调用fs接口
     * @param paraMap
     * @param urlWithoutKey
     * @return
     */
    public Object executeFileSystemDownloadUrlLogic(Map<String, Object> paraMap, String urlWithoutKey) {

        // 获取fs api url
        String url = getFileSystemUrl(urlWithoutKey);

        //postForEntity  -》 直接传递map参数
        ResponseEntity<String> response = restTemplate.postForEntity(url, paraMap, String.class);
        // 返回
        return response.getBody();
    }

    /**
     * 拼接fs api url
     * @return
     */
    public String getFileSystemUrl(String urlWithoutKey) {

        return urlWithoutKey+"?app_key="+systemConfigProperies.getApp_key()+"&secret_key="+systemConfigProperies.getSecret_key();
    }

    private void callAsyncApiController(BBkMonitorVo param) {
        String url = getBusinessCenterUrl("/wms/api/service/v2/async/monitor/backup/execute", SystemConstants.APP_CODE.ZT);
        String jsonString = JSON.toJSONString(param);
            Mono<String> mono = webClient
                    .post() // 发送POST 请求
                    .uri(url) // 服务请求路径，基于baseurl
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(jsonString))
                    .retrieve() // 获取响应体
                    .bodyToMono(String.class); // 响应数据类型转换

            //异步非阻塞处理响应结果
            mono.subscribe(this::callback);
    }

    private void callRenewAsyncApiController(BBkMonitorVo param) {
        String url = getBusinessCenterUrl("/wms/api/service/v2/async/monitor/restore/execute", SystemConstants.APP_CODE.ZT);
        String jsonString = JSON.toJSONString(param);
        Mono<String> mono = webClient
                .post() // 发送POST 请求
                .uri(url) // 服务请求路径，基于baseurl
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(jsonString))
                .retrieve() // 获取响应体
                .bodyToMono(String.class); // 响应数据类型转换

        //异步非阻塞处理响应结果
        mono.subscribe(this::callback);
    }

    /**
     * 响应结果处理回调方法
     * @param result
     */
    public void callback(String result) {
        log.debug("=============同步入库信息result=============" + result);
    }
}
