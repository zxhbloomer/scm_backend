package com.xinyirun.scm.api.controller.steel.async.v1;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v1.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v1.BBkMonitorVo;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.system.service.business.bkmonitor.v1.IBBkMonitorLogDetailService;
import com.xinyirun.scm.core.system.service.business.bkmonitor.v1.IBBkMonitorLogService;
import com.xinyirun.scm.core.system.service.business.bkmonitor.v1.IBBkMonitorSyncLogService;
import com.xinyirun.scm.core.system.service.mongobackup.monitor.v1.IBMonitorBackupBusinessService;
import com.xinyirun.scm.mongodb.service.monitor.v1.IMonitorDataMongoService;
import com.xinyirun.scm.mq.rabbitmq.producer.business.monitor.v1.MonitorBackupProducer;
import com.xinyirun.scm.mq.rabbitmq.producer.business.monitor.v1.MonitorRenewProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/3/31 11:00
 */

@Slf4j
@RestController
@RequestMapping(value = "/api/service/v1/async/monitor")
public class ApiBMonitorBackupController {

    @Autowired
    private IBMonitorBackupBusinessService service;

    @Autowired
    private IBBkMonitorLogDetailService logDetailService;

    @Autowired
    private MonitorBackupProducer backupProducer;

    @Autowired
    private IMonitorDataMongoService monitorDataMongoService;

    @Autowired
    private MonitorRenewProducer renewProducer;

    @Autowired
    private IBBkMonitorLogService logService;

    @Autowired
    private IBBkMonitorSyncLogService syncLogService;

    @PostMapping("/backup/execute")
    @SysLogApiAnnotion(value = "监管任务备份", noParam = true)
    public ResponseEntity<JsonResultAo<String>> backup(@RequestBody(required = false) BBkMonitorVo param) {
        // 循环请求数据库, 查出来数据插入rabbitmq
        backupSend2Mq(param);

        return ResponseEntity.ok().body(ResultUtil.OK("ok"));
    }

    /**
     * @param param
     * @return
     */
    @PostMapping("/restore/execute")
    @SysLogApiAnnotion(value = "监管任务恢复", noParam = true)
    public ResponseEntity<JsonResultAo<String>> renew(@RequestBody(required = false) BBkMonitorVo param) {
        // 循环请求数据库, 查出来数据插入rabbitmq
        renewSend2Mq(param);
        return ResponseEntity.ok().body(ResultUtil.OK("ok"));
    }

    private void renewSend2Mq(BBkMonitorVo param) {
        try {
            List<BBkMonitorLogDetailVo> list = monitorDataMongoService.selectLogDetailListByIds(param.getIds());
            if (CollectionUtils.isEmpty(list)) return;
            for (BBkMonitorLogDetailVo vo : list) {
                // 更新是否可查看状态, 如果是
                vo.setVersion("1");
                syncLogService.saveAndFlushByMonitorId(vo, DictConstant.DICT_B_MONITOR_BACKUP_TYPE_2, param.getStaff_id());
                // 新增到日志表
                vo.setLog_id(param.getLog_id());
                Integer detail_id = logDetailService.insertLog(vo);
                vo.setLog_detail_id(detail_id);
                renewProducer.mqSendMq(vo);
            }
            param.setFlag("OK");
        } catch (Exception e) {
            log.error("备份数据发送mq部分失败-> {}", e);
            param.setFlag("NG");
            param.setException(e.toString());
        } finally {
            logService.updateLog(param);
        }
    }


    private void backupSend2Mq(BBkMonitorVo param) {
        try {
            List<BBkMonitorLogDetailVo> list = service.selectData2Mq(param);
            if (CollectionUtils.isEmpty(list)) return;
            for (BBkMonitorLogDetailVo vo : list) {
                vo.setVersion("1");
                syncLogService.saveAndFlushByMonitorId(vo, DictConstant.DICT_B_MONITOR_BACKUP_TYPE_1, param.getStaff_id());
                // 新增到日志表
                vo.setLog_id(param.getLog_id());
                Integer detail_id = logDetailService.insertLog(vo);
                vo.setLog_detail_id(detail_id);
                backupProducer.mqSendMq(vo);
            }
            param.setFlag("OK");
        } catch (Exception e) {
            log.error("恢复数据发送mq部分失败-> {}", e);
            param.setFlag("NG");
            param.setException(e.toString());
        } finally {
            logService.updateLog(param);
        }
    }

    private void backupSend2Mq(int page, int pageSize, BBkMonitorVo param) {
//        log.error("备份当前页-->{}", page);
//        log.error("备份当前条数-->{}", page * pageSize);
        // 备份
  /*      List<BBkMonitorLogDetailVo> list = service.selectData2Mq(page * pageSize, pageSize, param);
        if (CollectionUtils.isEmpty(list)) return;
        for (BBkMonitorLogDetailVo vo : list) {
            // 新增到日志表
            vo.setLog_id(param.getLog_id());
            Integer detail_id = logDetailService.insertLog(vo);
            vo.setLog_detail_id(detail_id);
            backupProducer.mqSendMq(vo);
        }*/
        // 设置退出循环条件
//        if (list.size() < pageSize) {
//            log.error("带备份数据的添加到mq完毕, 总备份条数--> {}, {}, ", page, pageSize*pageSize + list.size());
//            return;
//        }
//        page += 1;
//        backupSend2Mq(page, pageSize, param);
    }

}
