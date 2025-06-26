package com.xinyirun.scm.core.system.serviceimpl.sys.schedule.v5;

import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorEntity;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.bo.business.alarm.BAlarmRulesBo;
import com.xinyirun.scm.bean.system.bo.business.message.BMessageBo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.constant.WebSocketConstants;
import com.xinyirun.scm.core.system.mapper.sys.schedule.v5.SBInventoryStagnationWarningBatchV5Mapper;
import com.xinyirun.scm.core.system.mapper.sys.schedule.v5.SBMonitorLossWarningBatchV5Mapper;
import com.xinyirun.scm.core.system.service.business.alarm.IBAlarmRulesService;
import com.xinyirun.scm.core.system.service.business.message.IBMessageService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.schedule.v5.ISBDMonitorLossWarningV5Service;
import com.xinyirun.scm.core.system.service.sys.schedule.v5.ISInventoryStagnationWarningV5Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.websocket.WebSocket2Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@Slf4j
public class SBDMonitorLossWarningV5ServiceImpl extends BaseServiceImpl<SBMonitorLossWarningBatchV5Mapper, BMonitorEntity> implements ISBDMonitorLossWarningV5Service {

    @Autowired
    private SBMonitorLossWarningBatchV5Mapper mapper;

    @Autowired
    private WebSocket2Utils webSocket2Util;

    @Autowired
    public WebClient webClient;

    @Autowired
    private IBMessageService messageService;

    @Autowired
    private IBAlarmRulesService rulesService;

    @Autowired
    private ISConfigService configService;

    /**
     * 监管任务损耗预警（定时任务入参必须有,可以为空）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    //@Scheduled(cron = "0/30 * * * * ?")
    public void monitorLossWarning(String parameterClass , String parameter) {

        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.WARNING_TYPE.M_MONITOR_LOSS_PERCENTAGE);
        if (sConfigEntity == null||sConfigEntity.getValue()==null||sConfigEntity.getExtra1()==null){
            log.debug("====》监管任务损耗预警百分比(m_monitor_loss_percentage)未配置《====");
        }

        // 获取预警损耗百分比
        Double value = Double.valueOf(sConfigEntity.getValue());

        // 获取业务开始时间
        LocalDate dataTime = LocalDate.parse(sConfigEntity.getExtra1(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 1.删除预警
        List<BMessageBo> deleteList = mapper.selectByLossWarningIsDel(value);
        if (!CollectionUtils.isEmpty(deleteList)) {
            log.debug("====》监管损耗有变动，删除预警信息{}《====", deleteList);
            messageService.deleteNoticeList(deleteList);
        }

        // 2.查询 非作废状态的监管任务.损耗百分比>=value时，则预警 | 损耗百分比公式为:监管任务(损耗/发货数量)*100%
        List<BMessageBo> list = mapper.selectByLossWarning(value,dataTime);
        if (CollectionUtils.isEmpty(list)) return;
        log.debug("====》监管损耗异常，增加预警信息《====");
        list.stream().forEach(k->{k.setM_monitor_loss_time(value);});
        messageService.insert(list, DictConstant.DICT_B_MESSAGE_TYPE_1, DictConstant.DICT_SYS_CODE_TYPE_M_MONITOR_LOSS);

        // 3.查询预警人员 发送通知
        List<BAlarmRulesBo> bAlarmRulesBoList = rulesService.selectStaffAlarm(DictConstant.DICT_B_ALARM_RULES_TYPE_0);
        if (CollectionUtils.isEmpty(bAlarmRulesBoList)) return;
        log.debug("====》监管损耗有变动，通知预警人员{}《====", bAlarmRulesBoList);
        webSocket2Util.convertAndSendUser(bAlarmRulesBoList, WebSocketConstants.WEBSOCKET_SYNC_LOG);
    }
}
