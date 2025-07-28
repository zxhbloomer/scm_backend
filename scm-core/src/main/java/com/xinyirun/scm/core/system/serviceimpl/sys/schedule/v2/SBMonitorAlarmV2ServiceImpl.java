package com.xinyirun.scm.core.system.serviceimpl.sys.schedule.v2;

import com.xinyirun.scm.bean.entity.business.monitor.BMonitorEntity;
import com.xinyirun.scm.bean.system.bo.business.alarm.BAlarmRulesBo;
import com.xinyirun.scm.bean.system.bo.business.message.BMessageBo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.WebSocketConstants;
import com.xinyirun.scm.common.utils.DateUtils;
import com.xinyirun.scm.core.system.mapper.sys.schedule.v2.SBMonitorAlarmV2Mapper;
import com.xinyirun.scm.core.system.service.business.alarm.IBAlarmRulesService;
import com.xinyirun.scm.core.system.service.business.message.IBMessageService;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBMonitorAlarmV2Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.websocket.WebSocket2Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Wqf
 * @Description: 监管任务预警
 * @CreateTime : 2023/3/21 11:46
 */

@Service
@Slf4j
public class SBMonitorAlarmV2ServiceImpl extends BaseServiceImpl<SBMonitorAlarmV2Mapper, BMonitorEntity> implements ISBMonitorAlarmV2Service {

    @Autowired
    private SBMonitorAlarmV2Mapper mapper;

    @Autowired
    private IBAlarmRulesService rulesService;

    @Autowired
    private IBMessageService messageService;

    @Autowired
    private WebSocket2Utils webSocket2Util;

    private static final int hour = 24;


    /**
     * 查询需要预警的监管任务
     */
    @Override
//    @Scheduled(cron = "0 0/5 * * * ? ")
//    @PostConstruct
    @Transactional(rollbackFor = Exception.class)
//    public void getMonitorAlarmList() {
    public void getMonitorAlarmList(String parameterClass, String parameter) {
        // 删除 已经处理的
        List<BMessageBo> deleteList = mapper.selectIdAndStatus();
        messageService.deleteNoticeList(deleteList);
        List<BMessageBo> result = new ArrayList<>();
        // 查询 1正在装货，2重车出库，4重车过磅，5正在卸货，6空车出库 状态下的监管任务
        List<String> statusList = List.of(DictConstant.DICT_B_MONITOR_STATUS_ONE, DictConstant.DICT_B_MONITOR_STATUS_ZERO,
                DictConstant.DICT_B_MONITOR_STATUS_TWO, DictConstant.DICT_B_MONITOR_STATUS_FOUR,
                DictConstant.DICT_B_MONITOR_STATUS_FIVE, DictConstant.DICT_B_MONITOR_STATUS_SIX);
        // 查询位添加到 message 表的监管任务
        List<BMessageBo> list = mapper.selectByStatusList(statusList);
        if (!CollectionUtils.isEmpty(list)) {
            list.stream().collect(Collectors.groupingBy(BMessageBo::getStatus))
                    .forEach((k, v) -> getOverMonitor(k, v, result));
        }

        // 查询未审核预警的(即 卸货完成状态, 未审核的)
        List<BMessageBo> unAuditAlarmList = mapper.selectNotAuditMonitor(DictConstant.DICT_B_MONITOR_STATUS_SEVEN,
                DictConstant.DICT_B_MONITOR_AUDIT_STATUS_ZERO);
        if (!CollectionUtils.isEmpty(unAuditAlarmList)) {
            result.addAll(unAuditAlarmList);
        }

        if (CollectionUtils.isEmpty(result)) return;
//        log.debug("超时的 --> , {}", JSONObject.toJSONString(result));
//         将超时的存入 b_message, 获取参与预警的员工id
        List<BAlarmRulesBo> bAlarmRulesBoList = rulesService.selectStaffAlarm(DictConstant.DICT_B_ALARM_RULES_TYPE_0);
        // 插入 message 表
        messageService.insert(result, DictConstant.DICT_B_MESSAGE_TYPE_1, DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR);
        // 发送websocket
        webSocket2Util.convertAndSendUser(bAlarmRulesBoList, WebSocketConstants.WEBSOCKET_SYNC_LOG);
    }

    /**
     * 获得超出 范围的监管任务
     * null 在方法里就是当前时间
     *
     * @return
     */
    private void getOverMonitor(String status, List<BMessageBo> list, List<BMessageBo> result) {
        switch (status) {
            // 空车过磅状态 当前时间-空车过磅.创建时间>24h，需预警
            case DictConstant.DICT_B_MONITOR_STATUS_ZERO:
                for (BMessageBo bo : list) {
                    if (calcTimeDiff(null, bo.getOut_empty_time(), hour)) {
                        result.add(bo);
                    }
                }
                break;
            // 正在装货状态 当前时间-正在装货.创建时间>24h，需预警
            case DictConstant.DICT_B_MONITOR_STATUS_ONE:
                for (BMessageBo bo : list) {
                    if (calcTimeDiff(bo.getOut_loading_time(), null, hour)) {
                        result.add(bo);
                    }
                }
                break;
            // 重车出库状态 当前时间-重车出库.创建时间>24h，需预警
            case DictConstant.DICT_B_MONITOR_STATUS_TWO:
                for (BMessageBo bo : list) {
                    if (calcTimeDiff(bo.getOut_heavy_time(), null, hour)) {
                        result.add(bo);
                    }
                }
                break;
            // 重车过磅状态 当前时间-重车过磅.创建时间>5天，需预警
            case DictConstant.DICT_B_MONITOR_STATUS_FOUR:
                for (BMessageBo bo : list) {
                    if (calcTimeDiff(bo.getIn_heavy_time(), null, hour * 5)) {
                        result.add(bo);
                    }
                }
                break;
            // 正在卸货状态 当前时间-正在卸货.创建时间>24h，需预警
            case DictConstant.DICT_B_MONITOR_STATUS_FIVE:
                for (BMessageBo bo : list) {
                    if (calcTimeDiff(bo.getIn_unloading_time(), null, hour)) {
                        result.add(bo);
                    }
                }
                break;
            // 空车出库状态 当前时间-空车出库.创建时间>24h，需预警
            case DictConstant.DICT_B_MONITOR_STATUS_SIX:
                for (BMessageBo bo : list) {
                    if (calcTimeDiff(bo.getIn_empty_time(), null, hour)) {
                        result.add(bo);
                    }
                }
                break;
        }
    }

    /**
     * 计算时间差
     *
     * @param start 开始时间
     * @param end   结束时间
     * @param range 时间差允许范围, 大于 range 返回true, 小于false
     * @return true or false
     */
    private boolean calcTimeDiff(LocalDateTime start, LocalDateTime end, int range) {
        int diff = DateUtils.differentHoursByMillisecond(start, end);
        return diff >= range;
    }
}
