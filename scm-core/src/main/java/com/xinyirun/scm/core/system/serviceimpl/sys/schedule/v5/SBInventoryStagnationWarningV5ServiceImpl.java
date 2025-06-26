package com.xinyirun.scm.core.system.serviceimpl.sys.schedule.v5;

import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.bo.business.alarm.BAlarmRulesBo;
import com.xinyirun.scm.bean.system.bo.business.message.BMessageBo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.constant.WebSocketConstants;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.core.system.mapper.sys.schedule.v5.SBInventoryStagnationWarningBatchV5Mapper;
import com.xinyirun.scm.core.system.service.business.alarm.IBAlarmRulesService;
import com.xinyirun.scm.core.system.service.business.message.IBMessageService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
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

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
public class SBInventoryStagnationWarningV5ServiceImpl extends BaseServiceImpl<SBInventoryStagnationWarningBatchV5Mapper, MInventoryEntity> implements ISInventoryStagnationWarningV5Service {

    @Autowired
    private SBInventoryStagnationWarningBatchV5Mapper mapper;

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
     * 港口中转停滞预警（定时任务入参必须有,可以为空）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stagnationWarning(String parameterClass , String parameter) {

        // 1.删除预警 15内有入库，出库，调整操作
        LocalDateTime dateTime = LocalDateTime.now();
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.WARNING_TYPE.M_INVENTORY_STAGNATION_TIME);
        Integer startTime = Integer.valueOf(sConfigEntity.getValue());
        if (sConfigEntity == null||sConfigEntity.getValue()==null){
            log.error("====》港口允许停滞时间(m_inventory_stag_time)未配置《====");
        }

        List<BMessageBo> deleteList = mapper.selectIdAndStatus(dateTime.plusDays(-startTime));
        if (!CollectionUtils.isEmpty(deleteList)) {
            log.debug("====》库存停滞有变动，删除预警信息{}《====", deleteList);
            messageService.deleteNoticeList(deleteList);
        }

        // 2.查询中转停滞仓库
        List<BMessageBo> list = mapper.selectList(dateTime.plusDays(-startTime));
        if (CollectionUtils.isEmpty(list)) return;
        log.debug("====》库存停滞有变动，增加预警信息{}《====", list);
        messageService.insert(list, DictConstant.DICT_B_MESSAGE_TYPE_1, DictConstant.DICT_SYS_CODE_TYPE_M_INVENTORY_STAGNATION);

        // 3.查询预警人员 发送通知
        List<BAlarmRulesBo> bAlarmRulesBoList = rulesService.selectStaffAlarm(DictConstant.DICT_B_ALARM_RULES_TYPE_0);
        if (CollectionUtils.isEmpty(bAlarmRulesBoList)) return;
        log.debug("====》库存停滞有变动，通知预警人员{}《====", bAlarmRulesBoList);
        webSocket2Util.convertAndSendUser(bAlarmRulesBoList, WebSocketConstants.WEBSOCKET_SYNC_LOG);

    }
}
