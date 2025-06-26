package com.xinyirun.scm.framework.config.websocket;

import com.xinyirun.scm.bean.system.bo.websocket.WeSocketSessionBo;
import com.xinyirun.scm.bean.system.utils.servlet.ServletUtil;
import com.xinyirun.scm.common.constant.WebSocketConstants;
import com.xinyirun.scm.common.utils.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * websocket拦截器，
 * @author zhuxiaojin
 * @Date 2018-11-19
 */
@Slf4j
@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    RedisUtil redisUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            //servletRequest.getServletRequest().changeSessionId();
            //HttpSession session = servletRequest.getServletRequest().getSession();
            //System.out.println("=========sessionId:"+session.getId()+"======================");
            //获取token认证, 放到ws连接后面，get方式获取
            //或者 登陆时放到容器的HttpSession中，ws连接时从在容器的session中获取
            WeSocketSessionBo bo = ServletUtil.getWebSocketSessionBo();
            //            String token = bo.getSession_id();
            if (null != bo && StringUtils.hasText(bo.getSession_id())) {
                //访问redis获取token是否有效
/*                UserVo userVo = redisHelper.getUserByToken(token);
                if(userVo==null){
                    return false;
                }*/
                attributes.put(WebSocketConstants.WEBSOCKET_SESSION, ServletUtil.getWebSocketSessionBo());
                return true;
            }

        }
        log.warn("请求无效，禁止登录websocket!");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("进来webSocket的afterHandshake拦截器！");
    }
}
