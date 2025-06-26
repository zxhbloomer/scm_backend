package com.xinyirun.scm.core.system.serviceimpl.sys.file;

import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.file.BackupFileVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SBackupLogVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.properies.SystemConfigProperies;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.spring.SpringUtils;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileInfoService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.mq.rabbitmq.producer.business.file.SFileBackupProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 附件详情 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
@Slf4j
public class SFileInfoServiceImpl extends BaseServiceImpl<SFileInfoMapper, SFileInfoEntity> implements ISFileInfoService {

    @Autowired
    private SFileInfoMapper mapper;

    @Autowired
    private SystemConfigProperies systemConfigProperies;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private WebClient webClient;

    @Autowired
    private SFileBackupProducer producer;

    /**
     * 查询分页列表
     * @param searchCondition
     * @return
     */
    @Override
    public List<SFileInfoVo> selectList(SFileInfoVo searchCondition) {
        return mapper.selectLists( searchCondition);
    }

    /**
     * 插入记录（选择字段，策略插入）
     *
     * @param vos 实体对象
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(List<SFileInfoVo> vos) {
        int rtn = 0;
        for(SFileInfoVo vo:vos){
            // 插入逻辑保存
            SFileInfoEntity entity = (SFileInfoEntity) BeanUtilsSupport.copyProperties(vo, SFileInfoEntity.class);
            rtn = mapper.insert(entity);
            vo.setId(entity.getId());
        }
        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 保存记录（选择字段，策略插入）
     *
     * @param vos 实体对象
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> save(List<SFileInfoVo> vos) {
        int rtn = 0;
        for(SFileInfoVo vo:vos){
            // 插入逻辑保存
            SFileInfoEntity entity = (SFileInfoEntity) BeanUtilsSupport.copyProperties(vo, SFileInfoEntity.class);
            entity.setTimestamp(LocalDateTime.now());
            if (entity.getId() == null) {
                rtn = mapper.insert(entity);
            } else {
                rtn = mapper.updateById(entity);
            }
            vo.setId(entity.getId());
        }
        // 插入逻辑保存
        return UpdateResultUtil.OK(rtn);
    }

    @Override
    public UpdateResultAo<Integer> save(SFileInfoVo vo) {
        int rtn = 0;
        // 插入逻辑保存
        SFileInfoEntity entity = (SFileInfoEntity) BeanUtilsSupport.copyProperties(vo, SFileInfoEntity.class);
        entity.setTimestamp(LocalDateTime.now());
        if (entity.getId() == null) {
            rtn = mapper.insert(entity);
        } else {
            rtn = mapper.updateById(entity);
        }
        vo.setId(entity.getId());
        return null;
    }

    @Override
    public SFileInfoVo selectById(int id) {
        return mapper.selectId(id);
    }

    /**
     * 批量删除复原
     *
     * @param searchCondition
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public DeleteResultAo<Integer> realDeleteByIdsIn(List<SFileInfoVo> searchCondition) {
        List<Integer> idList = new ArrayList<>();
        searchCondition.forEach(bean -> {
            idList.add(bean.getId());
        });
        int result=mapper.deleteBatchIds(idList);
        return DeleteResultUtil.OK(result);
    }

  /*  @Override
    @Transactional(rollbackFor = Exception.class)
    @SysLogAnnotion("定时任务备份附件信息")
    public void backup(Integer backup_now_count) {

        log.debug("=============定时任务备份附件信息开始=============");
        SConfigEntity backup = isConfigService.selectByKey(SystemConstants.BACKUP);

        if ("0".equals(backup.getValue())) {
            log.debug("=============备份文件开关未开启，任务中断=============");
            return;
        }

        SConfigEntity backup_url = isConfigService.selectByKey(SystemConstants.BACKUP_URL);

        SConfigEntity backup_uri = isConfigService.selectByKey(SystemConstants.BACKUP_URI);

        SConfigEntity backup_days = isConfigService.selectByKey(SystemConstants.BACKUP_DAYS);
        log.debug("===================baseurl:"+backup_url.getValue());
//        WebClient webClient = WebClient.builder().baseUrl(backup_url.getValue()).build();


        // 查询待备份文件
        log.debug("=============查询待备份文件开始=============");
        List<SBackupLogVo> list = mapper.selectBackupFileList(Integer.parseInt(backup_days.getValue()), backup_now_count);
        log.debug("=============查询待备份文件结束=============");

        List<List<SBackupLogVo>> result = new ArrayList<List<SBackupLogVo>>();
        // 每次备份1000条数据
        int len = 1000;
        int size = list.size();
        int count = (size + len - 1) / len;

        for (int i = 0; i < count; i++) {
            List<SBackupLogVo> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }

        for (List<SBackupLogVo> voList: result) {
            for (SBackupLogVo vo: voList) {
                log.debug("=============备份文件============="+vo.getSource_file_url());
                vo.setOriginal_urldisk(getPath(vo.getSource_file_url()));
            }

            BackupFileVo backupFileVo = new BackupFileVo();
            backupFileVo.setItems(voList);
            backupFileVo.setApp_key(systemConfigProperies.getApp_key());

            String jsonString = JSON.toJSONString(backupFileVo);

            String uri = backup_uri.getValue() + "?app_key="+systemConfigProperies.getApp_key()+"&secret_key="+systemConfigProperies.getSecret_key();

            Mono<BackupFileVo> mono = webClient
                    .post() // 发送POST 请求
                    .uri(uri) // 服务请求路径，基于baseurl
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(jsonString))
                    .retrieve() // 获取响应体
                    .bodyToMono(BackupFileVo.class); // 响应数据类型转换

            //异步非阻塞处理响应结果
            mono.subscribe(this::updateUrl);
        }

        log.debug("=============定时任务备份附件信息结束=============");
    }*/

    /**
     * 使用mq 的方式, 进行回调
     * @param backup_now_count
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
//    @SysLogAnnotion("定时任务备份附件信息")
    public void backup(Integer backup_now_count) {

        log.debug("=============定时任务备份附件信息开始=============");
        SConfigEntity backup = isConfigService.selectByKey(SystemConstants.BACKUP);

        if ("0".equals(backup.getValue())) {
            log.debug("=============备份文件开关未开启，任务中断=============");
            return;
        }

        SConfigEntity backup_url = isConfigService.selectByKey(SystemConstants.BACKUP_URL);

        SConfigEntity backup_uri = isConfigService.selectByKey(SystemConstants.BACKUP_URI);

        SConfigEntity backup_days = isConfigService.selectByKey(SystemConstants.BACKUP_DAYS);
//        log.debug("===================baseurl:"+backup_url.getValue());
//        WebClient webClient = WebClient.builder().baseUrl(backup_url.getValue()).build();


        // 查询待备份文件
        log.debug("=============查询待备份文件开始=============");
        List<SBackupLogVo> list = mapper.selectBackupFileList(Integer.parseInt(backup_days.getValue()), backup_now_count);
        log.debug("=============查询待备份文件结束=============");

        for (SBackupLogVo sBackupLogVo : list) {
            sBackupLogVo.setUri(backup_uri.getValue());
            sBackupLogVo.setUrl(backup_url.getValue());
            sBackupLogVo.setOriginal_urldisk(getPath(sBackupLogVo.getSource_file_url()));

            producer.mqSendMq(sBackupLogVo);
        }

      /*  List<List<SBackupLogVo>> result = new ArrayList<List<SBackupLogVo>>();
        // 每次备份1000条数据
        int len = 1000;
        int size = list.size();
        int count = (size + len - 1) / len;

        for (int i = 0; i < count; i++) {
            List<SBackupLogVo> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }

        for (List<SBackupLogVo> voList: result) {
            for (SBackupLogVo vo: voList) {
                log.debug("=============备份文件============="+vo.getSource_file_url());
                vo.setOriginal_urldisk(getPath(vo.getSource_file_url()));
            }

            BackupFileVo backupFileVo = new BackupFileVo();
            backupFileVo.setItems(voList);
            backupFileVo.setApp_key(systemConfigProperies.getApp_key());

            String jsonString = JSON.toJSONString(backupFileVo);

            String uri = backup_uri.getValue() + "?app_key="+systemConfigProperies.getApp_key()+"&secret_key="+systemConfigProperies.getSecret_key();

            Mono<BackupFileVo> mono = webClient
                    .post() // 发送POST 请求
                    .uri(uri) // 服务请求路径，基于baseurl
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(jsonString))
                    .retrieve() // 获取响应体
                    .bodyToMono(BackupFileVo.class); // 响应数据类型转换

            //异步非阻塞处理响应结果
            mono.subscribe(this::updateUrl);
        }
*/
        log.debug("=============定时任务备份附件信息结束=============");
    }




    //响应结果处理回调方法
//    @SysLogAnnotion("定时任务备份附件回调方法")
    public void updateUrl(BackupFileVo vo) {

        log.debug("==================文件备份回调开始=================");
//        log.debug(str);
//        JSONObject jsonObject = JSONObject.parseObject(str);
//        BackupFileVo vo = JSONObject.parseObject(jsonObject.getString("data"), BackupFileVo.class);
//        BackupFileVo vo = (BackupFileVo) data.getData();
        SFileInfoMapper sFileInfoMapper = SpringUtils.getBean(SFileInfoMapper.class);
        for (SBackupLogVo item:vo.getItems()) {
            log.debug("==================返写url开始================="+item);
            SFileInfoEntity sFileInfoEntity = sFileInfoMapper.selectById(item.getId());
            sFileInfoEntity.setStatus(item.getStatus());
            sFileInfoEntity.setRemark(item.getRemark());
//            sFileInfoEntity.setU_time(LocalDateTime.now());
            sFileInfoEntity.setUrl(item.getTarget_file_url());
            sFileInfoEntity.setBackup_time(item.getBackup_time());
            sFileInfoEntity.setType(SystemConstants.FILE_TYPE.ALI_OSS);
            sFileInfoMapper.update(sFileInfoEntity);
            log.debug("==================返写url结束================="+item);
        }
        log.debug("==================文件备份回调结束=================");
    }

    private String getPath(String url) {
        try {
            URL urls = new URL(url);
            return urls.getPath();
        } catch (MalformedURLException e) {
            log.error("getPath error", e);
        }
        return null;
    }


}
