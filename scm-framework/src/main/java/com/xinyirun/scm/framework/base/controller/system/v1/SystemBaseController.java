package com.xinyirun.scm.framework.base.controller.system.v1;

import cn.hutool.core.net.url.UrlBuilder;
import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigEntity;
import com.xinyirun.scm.bean.system.ao.fs.UploadFileResultAo;
import com.xinyirun.scm.bean.system.bo.session.user.system.UserSessionBo;
import com.xinyirun.scm.bean.system.bo.sys.SysInfoBo;
import com.xinyirun.scm.bean.system.utils.servlet.ServletUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.properies.SystemConfigProperies;
import com.xinyirun.scm.common.utils.CommonUtil;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.service.client.user.IMUserService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigService;
import com.xinyirun.scm.excel.bean.importconfig.template.ExcelTemplate;
import com.xinyirun.scm.excel.upload.SystemExcelReader;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

/**
 * controller父类
 * 
 * @author zhangxh
 */
@Slf4j
@Component
public class SystemBaseController {

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    private SystemConfigProperies systemConfigProperies;

    @Autowired
    private IMUserService service;

    @Autowired
    private ISAppConfigService isAppConfigService;

    @Value("${server.port}")
    private int port;

    /** 开发者模式，可以跳过验证码 */
    @Value("${scm.security.develop-model}")
    private Boolean developModel;

    @Autowired
    public WebClient webClient;

    protected String uuid;

    @ModelAttribute
    public void initializeUuid() {
        uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return uuid;
    }

    /**
     * 获取excel导入文件，并check是否是excel文件，然后根据模板定义进行导入
     * 如果有错误，则会生成错误excel，供客户下载查看。
     * @param fileUrl
     * @return
     * @throws IOException
     */
    public SystemExcelReader downloadExcelAndImportData(String fileUrl, String jsonConfig) throws IOException {
        ExcelTemplate et = initExcelTemplate(jsonConfig);

        // 文件下载到流
        ResponseEntity<byte[]> rtnResponse = restTemplate.getForEntity(fileUrl, byte[].class);
        InputStream is =  new ByteArrayInputStream(rtnResponse.getBody());

        // 文件分析，判断是否是excel文档
        if (FileMagic.valueOf(is) == FileMagic.OLE2){
            // Office 2003 ，xls
        } else if (FileMagic.valueOf(is) == FileMagic.OOXML) {
            // Office 2007 +，xlsx
        } else {
            // 非excel文档，报错
            throw new IllegalArgumentException("导入的文件不是office excel，请选择正确的文件来进行上传");
        }

        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/')+1);
        // 2、按模版进行读取数据
        SystemExcelReader wmsExcelReader = new SystemExcelReader(is, et, fileName);

        return wmsExcelReader;
    }

    /**
     * 获取excel模版
     * @param jsonConfig
     * @return
     */
    public ExcelTemplate initExcelTemplate(String jsonConfig){
        // 1、获取模板配置类
        ExcelTemplate et = JSON.parseObject(jsonConfig, ExcelTemplate.class);
        // 初始化
        et.initValidator();
        return et;
    }

    /**
     * 通用文件下载
     * @param filePath
     * @param fileName
     * @param response
     */
    public void fileDownLoad(String filePath, String fileName, HttpServletResponse response) throws IOException {
        CommonUtil.download(filePath, fileName , response);
    }

    /**
     * 获取当前登录用户的session数据
     * @return
     */
    public UserSessionBo getUserSession(){
        UserSessionBo bo = ServletUtil.getUserSession();
        return bo;
    }

    /**
     * 获取当前登录用户的session数据:账户id
     * @return
     */
    public Long getUserSessionAccountId(){
        Long id = getUserSession().getAccountId();
        return id;
    }

    /**
     * 加密密码
     * @param psdOrignalCode
     * @return
     */
    public String getPassword(String psdOrignalCode){
        //加密对象
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePassword = passwordEncoder.encode(psdOrignalCode);
        return encodePassword;
    }

    /**
     * 执行usersession往session中保存的逻辑
     *
     */
    public void doResetUserSessionByLoginUserId(Long loginUser_id) {
        // 如果更新的是当前登录的用户则，刷新当前登录用户，否则pass
        if(loginUser_id.equals(this.getUserSessionAccountId())) {
            this.resetUserSession(this.getUserSessionAccountId(), SystemConstants.LOGINUSER_OR_STAFF_ID.LOGIN_USER_ID);
        }
    }

    /**
     * 获取user信息，权限信息，并保存到redis中
     * 1：userbean信息
     * 2：系统参数
     * 执行usersession往session中保存的逻辑
     */
    @SysLogAnnotion("设置用户session，包含：用户、员工、租户、系统参数、菜单权限、操作权限数据")
    public void resetUserSession(Long id, String loginOrStaffId ) {
        /** 设置1：userbean信息  */
        UserSessionBo userSessionBo = service.getUserBean(id, loginOrStaffId);
        String sessionId = ServletUtil.getSession().getId();

        // 设置系统信息
        SysInfoBo sysInfoBo = new SysInfoBo();
        sysInfoBo.setDevelopModel(developModel);
        /** 设置2：系统参数  */
        userSessionBo.setSys_Info(sysInfoBo);

        /** 设置session id */
        userSessionBo.setSession_id(sessionId);

        /** 把用户session，保存到redis中 */
        HttpSession session = ServletUtil.getSession();
        String key_session = SystemConstants.SESSION_PREFIX.SESSION_USER_PREFIX_PREFIX + "_" + sessionId;
        if (ServletUtil.getUserSession() != null) {
            session.removeAttribute(key_session);
            session.setAttribute(key_session, userSessionBo);
        } else {
            session.setAttribute(key_session, userSessionBo);
        }
    }

    /**
     * 获取当前登录用户的session数据:员工id
     * @return
     */
    public Long getUserSessionStaffId(){
        Long id = getUserSession().getStaff_Id();
        return id;
    }

    /**
     * 执行usersession往session中保存的逻辑
     *
     */
    public void doResetUserSessionByStaffId(Long staff_id) {
        // 如果更新的是当前登录的用户则，刷新当前登录用户，否则pass
        if(staff_id.equals(this.getUserSessionStaffId())) {
            this.resetUserSession(this.getUserSessionStaffId(), SystemConstants.LOGINUSER_OR_STAFF_ID.STAFF_ID);
        }
    }

    /**
     * 通用文件上传
     * @param fileUrl
     * @return
     */
    public <T> T uploadFile(String fileUrl, Class<T> classOfT) throws IllegalAccessException, InstantiationException {
        // 上传的url
        String uploadFileUrl = systemConfigProperies.getFsUrl();
        FileSystemResource resource = new FileSystemResource(fileUrl);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("file", resource);
        param.add("app_key", systemConfigProperies.getApp_key());
        param.add("secret_key", systemConfigProperies.getSecret_key());
        /**
         * request 头信息
         */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(param, headers);
        ResponseEntity<Map> re = restTemplate.exchange(uploadFileUrl, HttpMethod.POST, httpEntity, Map.class);
        if (re.getStatusCode().value() != HttpStatus.OK.value()) {
            throw new BusinessException("错误文件处理失败");
        }
        UploadFileResultAo uploadFileResultAo = JSON.parseObject(JSON.toJSONString(re.getBody().get("data")), UploadFileResultAo.class);
        // 判断文件是否存在
        File file = new File(fileUrl);
        if (file.exists()) {
            if(!file.delete()) {
                throw new RuntimeException("文件删除失败");
            }
        }
        // 复制UploadFileResultAo类中的属性到，返回的bean中
        T rtnBean = classOfT.newInstance();
        BeanUtilsSupport.copyProperties(uploadFileResultAo, rtnBean);

        return rtnBean;
    }

    /**
     * 拼接中台同步数据url
     * @param uri
     * @param appCode
     * @return
     */
    protected String getBusinessCenterUrl(String uri, String appCode) {
        try {
            SAppConfigEntity sAppConfigEntity = isAppConfigService.getDataByAppCode(appCode);
            String app_key= sAppConfigEntity.getApp_key();
            String secret_key = sAppConfigEntity.getSecret_key();
            String host = InetAddress.getLocalHost().getHostAddress();

            String url = UrlBuilder.create()
                    .setScheme("http")
                    .setHost(host)
                    .setPort(port)
                    .addPath(uri)
                    .addQuery("app_key", app_key)
                    .addQuery("secret_key", secret_key)
                    .build();
            return url.replaceAll("%2F", "/");
        } catch (Exception e) {
            log.error("getBusinessCenterUrl error", e);
        }
        return "";
    }

    /**
     * 拼接fs api url
     * @return
     */
    public String getFileSystemUrl(String urlWithoutKey) {

        return urlWithoutKey+"?app_key="+systemConfigProperies.getApp_key()+"&secret_key="+systemConfigProperies.getSecret_key();
//        return urlWithoutKey;
    }

    /**
     * 调用fs接口
     * @param paraMap
     * @param urlWithoutKey
     * @return
     */
    public Object executeFileSystemDownloadUrlLogic(Map<String, Object> paraMap, String urlWithoutKey) {

        // 获取fs api url
        String url = getFileSystemUrl(urlWithoutKey);

        //postForEntity  -》 直接传递map参数
        ResponseEntity<String> response = restTemplate.postForEntity(url, paraMap, String.class);

//        JSONObject jsonObject = JSONObject.parseObject(response);
//        Boolean status = jsonObject.getBoolean("success");
//        if (response.getsta == Boolean.FALSE) {
//            throw new BusinessException(jsonObject.getString("message"));
//        }

        // 返回
        return response.getBody();
    }

}
