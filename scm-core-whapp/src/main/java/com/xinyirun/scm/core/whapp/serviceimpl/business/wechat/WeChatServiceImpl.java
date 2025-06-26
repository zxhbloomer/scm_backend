package com.xinyirun.scm.core.whapp.serviceimpl.business.wechat;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinyirun.scm.bean.app.bo.jwt.user.AppJwtBaseBo;
import com.xinyirun.scm.bean.app.vo.master.user.jwt.AppMUserJwtTokenVo;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.enums.ResultEnum;
import com.xinyirun.scm.common.exception.app.AppBusinessException;
import com.xinyirun.scm.common.utils.LocalDateTimeUtils;
import com.xinyirun.scm.core.system.service.client.user.IMUserService;
import com.xinyirun.scm.core.whapp.service.business.wechat.WeChatService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * <p>
 * wx
 * </p>
 */
@Service
@Slf4j
public class WeChatServiceImpl implements WeChatService {

    @Autowired
    private WxMpService wxMpService;

    private static final String AUTHORITIES_KEY = "auth";

    @Value("${wms.security.jwt.base64-secret}")
    private String base64Secret;

    @Value("${wms.wx.open.config.redirectUrl}")
    private String wxRedirectUrl;

    @Value("${wms.wx.open.config.csrfKey}")
    private String CSRF_KEY;

    @Value("${wms.security.jwt.token-validity-in-seconds}")
    private long tokenValidityInSeconds;

    /**
     * appid
     */
    @Value("${wms.wx.open.config.appid}")
    private String wechatAppid;

    /**
     * app secret
     */
    @Value("${wms.wx.open.config.secret}")
    private String wechatSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IMUserService imUserService;

    public JSONObject getAccessToken(String code) {

        String appId = wechatAppid;
        String appSecret = wechatSecret;

        String accesstoken;
        String openid = null;
        String refreshtoken;
        int expiresIn;
        // 可通过获取用户基本信息中的unionid来区分用户的唯一性，因为只要是同一个微信开放平台帐号下的移动应用、网站应用和公众帐号，
        // 用户的unionid是唯一的。换句话说，同一用户，对同一个微信开放平台下的不同应用，unionid是相同的。
        String unionid;
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appId+"&secret="+appSecret+"&code="+code+"&grant_type=authorization_code";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        JSONObject wxObject = JSONObject.parseObject(response.getBody());

        if (wxObject.containsKey("errcode")) {
            log.error("获取微信access_token失败，错误信息：{}", wxObject.getString("errmsg"));
            throw new AppBusinessException("获取微信access_token失败，错误信息：" + wxObject.getString("errmsg"));
        } else {
//            accesstoken = wxObject.getString("access_token");
//            openid = wxObject.getString("openid");
//            refreshtoken = wxObject.getString("refresh_token");
//            expiresIn = wxObject.getInteger("expires_in");
//            unionid = wxObject.getString("unionid");

            // 保存用户信息 accesstoken openid refreshtoken expiresIn unionid
            return wxObject;
        }
    }

    @Override
    @Transactional
    public String login(String code, HttpServletRequest request, HttpServletResponse response) throws IOException {

        JSONObject wxObject = getAccessToken(code);
        String accesstoken = wxObject.getString("access_token");
        String refreshtoken = wxObject.getString("refresh_token");
        Integer expiresIn = wxObject.getInteger("expires_in");
        String unionid = wxObject.getString("unionid");

        MUserEntity mUserEntity = imUserService.getDataByWxUnionid(unionid);
        if (mUserEntity == null) {
            throw new AppBusinessException("该微信未绑定用户,请先绑定");
        }

        mUserEntity.setWx_access_token(accesstoken);
        mUserEntity.setWx_refresh_token(refreshtoken);
        mUserEntity.setWx_expires_in(expiresIn);
//        imUserService.update(mUserEntity);

        AppJwtBaseBo appJwtBaseBo = new AppJwtBaseBo();
        appJwtBaseBo.setUsername(mUserEntity.getLogin_name());
        appJwtBaseBo.setUser_Id(mUserEntity.getId());
        appJwtBaseBo.setStaff_Id(mUserEntity.getStaff_id());


        UserDetails users = imUserService.loadUserByUsername(appJwtBaseBo.getUsername());

        appJwtBaseBo.setWx_unionid(unionid);

        Authentication authentication = new UsernamePasswordAuthenticationToken(users, users.getPassword(), users.getAuthorities());

        AppMUserJwtTokenVo jwtTokenVo = createToken(authentication, appJwtBaseBo, true);

        //登录成功 记录最新登录时间
        imUserService.updateLoginDate(mUserEntity.getId());

        return jwtTokenVo.getToken();

//        return "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJvcGVyYXRpb25TZXRcIjpbXSxcInN0YWZmX0lkXCI6MTIyLFwidG9rZW5fZXhwaXJlc19hdFwiOlwiMjAyNC0xMS0wOCAxMDoyMDo0MS45OTZcIixcInVzZXJfSWRcIjo0MTUsXCJ1c2VybmFtZVwiOlwiMTg3MjAwMDAwMDFcIixcInd4X3VuaW9uaWRcIjpcIm9LQmRfNS1lMW85bTB0UHppb2hFd1FYVTVKWG9cIn0iLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNzMxMDMyNDQxfQ.f3AL1Us26kKDzZF7DnMH4NeObO-N9bb9lxgwmVECeGM";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bingWechat(String code) {
        AppJwtBaseBo appJwtBaseBo = SecurityUtil.getAppJwtBaseBo();

//        MUserEntity mUserEntity = imUserService.getDataById(SecurityUtil.getLoginUser_id().intValue());

        JSONObject wxObject = getAccessToken(code);

        String accesstoken = wxObject.getString("access_token");
        String openid = wxObject.getString("openid");
        String refreshtoken = wxObject.getString("refresh_token");
        Integer expiresIn = wxObject.getInteger("expires_in");
        String unionid = wxObject.getString("unionid");

        MUserEntity mUserEntity = imUserService.getDataByWxUnionid(unionid);
        MUserEntity currentUserEntity = imUserService.getDataById(appJwtBaseBo.getUser_Id().intValue());
        if (mUserEntity != null && mUserEntity.getLogin_name().equals(currentUserEntity.getLogin_name())) {
            throw new AppBusinessException("该微信已绑定其他用户,请先解绑");
        }

        currentUserEntity.setWx_unionid(unionid);
        currentUserEntity.setWx_openid(openid);
        currentUserEntity.setWx_access_token(accesstoken);
        currentUserEntity.setWx_refresh_token(refreshtoken);
        currentUserEntity.setWx_expires_in(expiresIn);
        imUserService.update(currentUserEntity);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbingWechat() {
        AppJwtBaseBo appJwtBaseBo = SecurityUtil.getAppJwtBaseBo();
        MUserEntity currentUserEntity = imUserService.getDataById(appJwtBaseBo.getUser_Id().intValue());
        currentUserEntity.setWx_unionid(null);
        currentUserEntity.setWx_openid(null);
        currentUserEntity.setWx_access_token(null);
        currentUserEntity.setWx_refresh_token(null);
        currentUserEntity.setWx_expires_in(null);
        imUserService.update(currentUserEntity);
    }

    private AppMUserJwtTokenVo createToken(Authentication authentication, AppJwtBaseBo bean, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        long now = (new Date()).getTime();
        Date validity;

        validity = new Date(now + tokenValidityInSeconds);

        bean.setToken_expires_at(LocalDateTimeUtils.convertDateToLDT(validity));
        String token = Jwts.builder()
                .setSubject(JSON.toJSONString(bean))
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(validity)
                .compact();

        AppMUserJwtTokenVo vo = new AppMUserJwtTokenVo();
        vo.setUser_id(bean.getUser_Id());
        vo.setStaff_id(bean.getStaff_Id());
        vo.setToken(token);
        vo.setToken_expires_at(LocalDateTimeUtils.convertDateToLDT(validity));
        vo.setLast_login_date(LocalDateTime.now());
        vo.setC_id(bean.getUser_Id());
        vo.setC_time( LocalDateTime.now());
        return vo;
    }
}
