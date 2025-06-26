package com.xinyirun.scm.core.system.serviceimpl.base.v1;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.system.vo.business.pocontract.PoContractImportVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseImportVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseVo;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictDataVo;
import com.xinyirun.scm.common.properies.SystemConfigProperies;
import com.xinyirun.scm.core.system.mapper.sys.config.dict.SDictDataMapper;
import com.xinyirun.scm.core.system.service.sys.config.dict.ISDictDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * 扩展Mybatis-Plus接口
 *
 * @author
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IService<T> {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SystemConfigProperies systemConfigProperies;

    public String uploadFile(String fileUrl, String fileName, long contentLength) throws Exception {
        InputStream inputStream = null;
        if (fileUrl.startsWith("http")) {
            URL url = new URL(fileUrl);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();
        } else {
            inputStream = new FileInputStream(fileUrl);
        }

        if (inputStream == null) {
//            throw new Exception("文件不存在");
            return "";
        } else {
            String uploadUrl = systemConfigProperies.getFsUrl() + "?app_key="+systemConfigProperies.getApp_key()+"&secret_key="+systemConfigProperies.getSecret_key();
//        String uploadUrl = "http://file.xinyirunscm.com/fs/api/service/v1/upload" + "?app_key="+"8a90e44e-2a14-5c02-b3a5-95a1ce3a9eb6"+"&secret_key="+"1d7ee618-2fcb-5ec3-b0b2-d6df9115301d";
            // 1、封装请求头
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("multipart/form-data");
            headers.setContentType(type);
            headers.setContentDispositionFormData("media", fileName);
            // 2、封装请求体
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
            InputStreamResource resource = new InputStreamResource(inputStream){
                @Override
                public long contentLength(){
                    return contentLength;
                }
                @Override
                public String getFilename(){
                    return fileName;
                }
            };
            param.add("file", resource);
            // 3、封装整个请求报文
            HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<>(param, headers);
            // 4、发送请求
            ResponseEntity<String> data = restTemplate.postForEntity(uploadUrl, formEntity, String.class);
            // 5、请求结果处理
            JSONObject result = JSONObject.parseObject(data.getBody());

            System.out.println(result);


            return JSONObject.parseObject(result.getString("data")).getString("url");
        }
    }
}
