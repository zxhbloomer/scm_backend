package com.xinyirun.scm.core.system.serviceimpl.sys.schedule.v2;

import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.properies.SystemConfigProperies;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileInfoService;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISFileBackupV2Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * <p>
 *  物料转换
 * </p>
 * @author wwl
 * @since 2022-05-09
 */
@Service
public class SBFileBackupV2ServiceImpl extends BaseServiceImpl<SFileInfoMapper, SFileInfoEntity> implements ISFileBackupV2Service {

    @Autowired
    private ISFileInfoService service;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private SystemConfigProperies systemConfigProperies;

    @Autowired
    private WebClient webClient;

    @Override
    public void backup(String parameterClass , String parameter) {
        service.backup(null);
    }

    @Override
    public void delete(String parameterClass, String parameter) {

        SConfigEntity delete_backup_uri = isConfigService.selectByKey(SystemConstants.DELETE_BACKUP_URI);

        String uri = delete_backup_uri.getValue() + "?app_key="+systemConfigProperies.getApp_key()+"&secret_key="+systemConfigProperies.getSecret_key();

        Mono<String> mono = webClient
                .post() // 发送POST 请求
                .uri(uri) // 服务请求路径，基于baseurl
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("{}"))
                .retrieve() // 获取响应体
                .bodyToMono(String.class); // 响应数据类型转换

        //异步非阻塞处理响应结果
        mono.subscribe(this::callback);
    }

    private void callback(String res) {
        log.debug("=====删除备份文件================："+res);
    }
}
