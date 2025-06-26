package com.xinyirun.scm.core.system.serviceimpl.sys.schedule.v5;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.bo.business.alarm.BAlarmRulesBo;
import com.xinyirun.scm.bean.system.vo.master.user.MUserVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.constant.WebSocketConstants;
import com.xinyirun.scm.core.system.mapper.sys.schedule.v5.SBUserPwdWarningBatchV5Mapper;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.schedule.v5.ISBDUserPwdWarningV5Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.websocket.WebSocket2Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class SBDUserPwdWarningV5ServiceImpl extends BaseServiceImpl<SBUserPwdWarningBatchV5Mapper, MUserEntity> implements ISBDUserPwdWarningV5Service {

    @Autowired
    private ISConfigService configService;

    @Autowired
    private SBUserPwdWarningBatchV5Mapper sbUserPwdBatchV5Mapper;

    @Autowired
    private WebSocket2Utils webSocket2Util;


//    @Autowired
//    private SessionRegistry sessionRegistry;



    /**
     * 用户密码预警
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
//    @Scheduled(cron = "0/30 * * * * ?")
    public void userPwdWarning(String parameterClass , String parameter) {

        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.PWD_SWITCH);
        if (sConfigEntity != null && sConfigEntity.getValue().equals("1") && sConfigEntity.getExtra1() != null) {
            LocalDate dataTime = LocalDate.parse(sConfigEntity.getExtra1(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            List<MUserVo> mUserVoList = sbUserPwdBatchV5Mapper.selectUserByPwd(dataTime);

            List<BAlarmRulesBo> bAlarmRulesBoList = new ArrayList<>();
            mUserVoList.forEach(item -> {
                bAlarmRulesBoList.add(BAlarmRulesBo.builder()
                        .staff_id(item.getStaff_id().intValue())
                        .notice_type("2")
                        .is_using(true)
                        .build());
            });
            webSocket2Util.convertAndSendUser(bAlarmRulesBoList, WebSocketConstants.WEBSOCKET_SYNC_LOG);
        }
    }

//    /**
//     * 获取所在在线用户id
//     */
//    public List<Long> getLoginUser() {
//        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
//
//        List<Long>  ids = new ArrayList<>();
//        for (Object allPrincipal : allPrincipals) {
//            if (allPrincipal instanceof UserDetails){
//                String jsonString = JSONObject.toJSONString(allPrincipal);
//                Map<String, Object> map = JSON.parseObject(jsonString,Map.class);
//                ids.add(Long.parseLong(map.get("id").toString()));
//            }
//        }
//
//        return ids;
//    }

}
