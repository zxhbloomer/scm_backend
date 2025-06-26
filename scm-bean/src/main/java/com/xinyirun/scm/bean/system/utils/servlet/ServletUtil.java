package com.xinyirun.scm.bean.system.utils.servlet;

import com.xinyirun.scm.bean.system.bo.session.user.system.UserSessionBo;
import com.xinyirun.scm.bean.system.bo.websocket.WeSocketSessionBo;
import com.xinyirun.scm.bean.system.config.base.SessionBaseBean;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

/**
 * 获取session工具类
 */
public class ServletUtil {

    /**
     * 获取session
     *
     * @return
     */
    public static HttpSession getSession() {
        ServletRequestAttributes requestAttributes =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null != requestAttributes) {
            return (requestAttributes).getRequest().getSession();
        }
        return null;
      /*  HttpServletRequest request =
            ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getSession();*/
    }

    /**
     * 返回session中保存的user session
     *
     */
    public static UserSessionBo getUserSession() {
        HttpSession session = getSession();
        if (session != null) {
            String sessionId = ServletUtil.getSession().getId();
            String key = SystemConstants.SESSION_PREFIX.SESSION_USER_PREFIX_PREFIX + "_" + sessionId;
            return (UserSessionBo) session.getAttribute(key);
        } else {
            return null;
        }
    }

    /**
     * 返回session中保存的SessionBaseBean
     *
     */
    public static SessionBaseBean getSessionBaseBean() {
        HttpSession session = getSession();
        String sessionId = ServletUtil.getSession().getId();
        String key = SystemConstants.SESSION_PREFIX.SESSION_USER_PREFIX_PREFIX + "_" + sessionId;
        UserSessionBo bo = (UserSessionBo)session.getAttribute(key);
        SessionBaseBean baseBo = (SessionBaseBean) BeanUtilsSupport.copyProperties(bo, SessionBaseBean.class);
        return baseBo;
    }

    public static WeSocketSessionBo getWebSocketSessionBo() {
        HttpSession session = getSession();
        String sessionId = ServletUtil.getSession().getId();
        String key = SystemConstants.SESSION_PREFIX.SESSION_USER_PREFIX_PREFIX + "_" + sessionId;
        UserSessionBo bo = (UserSessionBo)session.getAttribute(key);
        WeSocketSessionBo baseBo = (WeSocketSessionBo) BeanUtilsSupport.copyProperties(bo, WeSocketSessionBo.class);
        return baseBo;
    }

    /**
     * 返回session中保存的SessionBaseBean
     *
     */
//    public static SessionBaseBean getSessionBaseBean(String session_id) {
//        HttpSession session = getSession();
//        String sessionId = ServletUtil.getSession().getId();
//        String key = SystemConstants.SESSION_PREFIX.SESSION_USER_PREFIX_PREFIX + "_" + sessionId;
//        UserSessionBo bo = (UserSessionBo)session.getAttribute(session_id);
//        SessionBaseBean baseBo = (SessionBaseBean) BeanUtilsSupport.copyProperties(bo, SessionBaseBean.class);
//        return baseBo;
//    }

    /**
     * 获取当前登录用户的staff id
     * @return
     */
    public static Long getStaffId(){
        Long staffId = ((UserSessionBo) ServletUtil.getUserSession()).getStaff_Id();
        return staffId;
    }

    /**
     * 获取当前登录用户的staff id
     * @return
     */
    public static String getStaffIdString(){
        Long staffId = ((UserSessionBo) ServletUtil.getUserSession()).getStaff_Id();
        return staffId.toString();
    }
}
