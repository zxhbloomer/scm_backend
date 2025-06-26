package com.xinyirun.scm.framework.base.controller.app.v1;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.bean.app.bo.jwt.user.AppUserBo;
import com.xinyirun.scm.bean.app.bo.sys.AppSysInfoBo;
import com.xinyirun.scm.bean.app.bo.jwt.user.AppJwtBaseBo;
import com.xinyirun.scm.bean.system.ao.fs.UploadFileResultAo;
import com.xinyirun.scm.bean.system.utils.servlet.ServletUtil;
import com.xinyirun.scm.common.annotations.SysLogAppAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.app.AppBusinessException;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.properies.SystemConfigProperies;
import com.xinyirun.scm.common.utils.CommonUtil;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.app.service.cilent.user.AppIMUserService;
import com.xinyirun.scm.excel.bean.importconfig.template.ExcelTemplate;
import com.xinyirun.scm.excel.upload.SystemExcelReader;
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
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * controller父类
 *
 * @author zhangxh
 */
@Slf4j
@Component
public class AppBaseController {

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    private SystemConfigProperies systemConfigProperies;

    @Autowired
    private AppIMUserService service;

    @Value("${server.port}")
    private int port;

    /** 开发者模式，可以跳过验证码 */
    @Value("${wms.security.develop-model}")
    private Boolean developModel;

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
    public AppUserBo getUserBo(){
        // todo：从jwt中获取userbo
        return null;
    }

    /**
     * 获取当前登录用户的session数据:账户id
     * @return
     */
    public Long getJwtUserAccountId(){
        // todo：从jwt中获取user_id
        return null;
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
        if(loginUser_id.equals(this.getJwtUserAccountId())) {
            this.resetAppUser(this.getJwtUserAccountId(), SystemConstants.LOGINUSER_OR_STAFF_ID.LOGIN_USER_ID);
        }
    }

    /**
     * 获取user信息，权限信息，并保存到redis中
     * 1：userbean信息
     * 2：系统参数
     * 执行usersession往session中保存的逻辑
     */
    @SysLogAppAnnotion("重置token中的数据")
    public void resetAppUser(Long id, String loginOrStaffId ) {
        /** 设置1：userbean信息  */
        AppUserBo app_user_bo = service.getUserBean(id, loginOrStaffId);
        String sessionId = ServletUtil.getSession().getId();

        // 设置系统信息
        AppSysInfoBo sysInfoBo = new AppSysInfoBo();
        sysInfoBo.setDevelopModel(developModel);
        /** 设置2：系统参数  */
        app_user_bo.setApp_sys_Info(sysInfoBo);
    }

    @SysLogAppAnnotion("app_jwt_登录:UserBean长bean")
    public AppUserBo getAppLoginUserBean(Long id, String loginOrStaffId ) {
        /** 设置1：userbean信息  */
        AppUserBo appUserBo = service.getUserBean(id, loginOrStaffId);
        // 设置系统信息
        AppSysInfoBo sysInfoBo = new AppSysInfoBo();
        sysInfoBo.setDevelopModel(developModel);
        /** 设置2：系统参数  */
        appUserBo.setApp_sys_Info(sysInfoBo);
        return appUserBo;
    }

    public AppJwtBaseBo getAppLoginBean(Long id, String loginOrStaffId ) {
        /** 设置1：userbean信息  */
        AppUserBo appUserBo = service.getUserBean(id, loginOrStaffId);
        AppJwtBaseBo bean = (AppJwtBaseBo) BeanUtilsSupport.copyProperties(appUserBo, AppJwtBaseBo.class);
        bean.setStaff_code(appUserBo.getStaff_code());
        return bean;
    }

    /**
     * 获取当前登录用户的session数据:员工id
     * @return
     */
    public Long getUserStaffId(){
        // todo:从jwt中获取
        return null;
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
            throw new AppBusinessException("错误文件处理失败");
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

}
