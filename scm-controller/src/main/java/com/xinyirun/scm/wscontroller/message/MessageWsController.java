package com.xinyirun.scm.wscontroller.message;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.utils.servlet.ServletUtil;
import com.xinyirun.scm.bean.system.vo.websocket.WMessageCenterCountWsVo;
import com.xinyirun.scm.common.constant.WebSocketConstants;
import com.xinyirun.scm.framework.base.wscontroller.SystemBaseWsController;
import com.xinyirun.scm.mq.rabbitmq.producer.detail.TestMqProducter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/ws")
public class MessageWsController extends SystemBaseWsController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private TestMqProducter testMq;


    /**
     *  MessageMapping:浏览器 给 后端发消息，用这个地址
     *  SendTo:服务器有消息时，给浏览器的 这个地址。
     * @param wMessageCenterCountWsVo
     * @return
     */
    @PostMapping("/broadcast/test")
    public ResponseEntity<JsonResultAo<String>> testBroadcast(@RequestBody(required = false) WMessageCenterCountWsVo wMessageCenterCountWsVo) {
        WMessageCenterCountWsVo wMessage = new WMessageCenterCountWsVo();
        wMessage.setSubscriptions(10);
        wMessage.setTodos(20);
        simpMessagingTemplate.convertAndSend(WebSocketConstants.WEBSOCKET_BROADCAST_MESSAGE, wMessage);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @PostMapping("/2user/test")
    public ResponseEntity<JsonResultAo<String>> testSendUser(@RequestBody(required = false) WMessageCenterCountWsVo wMessageCenterCountWsVo) {
        WMessageCenterCountWsVo wMessage = new WMessageCenterCountWsVo();
        wMessage.setSubscriptions(11);
        wMessage.setTodos(21);
        simpMessagingTemplate.convertAndSendToUser(ServletUtil.getStaffIdString(), WebSocketConstants.WEBSOCKET_SENDTOUSER_MESSAGE, wMessage);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public Exception handleExceptions(Exception t){
        t.printStackTrace();
        return t;
    }
}
