package com.xinyirun.scm.framework.config.websocket;

import com.xinyirun.scm.common.constant.WebSocketConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * websocket心跳
 */
@Slf4j
@Component
public class WebSocketHeartBeatTask {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 可以试试ping - pong心跳
     * 30秒
     */
    @Scheduled(cron = "0/30 * * * * ? ")
    public void heartBeat() {
        log.debug("websocket heartbeat：pong！！");
        simpMessagingTemplate.convertAndSend(WebSocketConstants.WEBSOCKET_HEARTBEATING_PATH, new Date().toString());
    }

}
