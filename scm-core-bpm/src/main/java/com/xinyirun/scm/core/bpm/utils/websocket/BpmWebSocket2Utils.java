package com.xinyirun.scm.core.bpm.utils.websocket;

import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Author:      Wqf
 * Description: 指定人员发送websocket
 * CreateTime : 2023/3/15 14:45
 */

@Slf4j
@Component
public class BpmWebSocket2Utils {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 发送给指定人员，審批流消息通知
     *
     * @param bos 发送信息
     * @param path  发送地址
     */
    public void convertAndSendUser(List<MStaffVo> bos, String path, Object param) {
        bos.forEach(item -> {
            log.debug("websocket 开发发送, 接收人员: {}", item);
            simpMessagingTemplate.convertAndSendToUser(item.getId().toString(), path, param);
            log.debug("websocket 开发发送, 接收人员 -- end: {}", item);
        });
    }
}
