package com.xinyirun.scm.core.system.serviceimpl.mongobackup.monitor.v2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorEntity;
import com.xinyirun.scm.bean.entity.mongo.monitor.v2.BMonitorDataMongoEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorVo;
import com.xinyirun.scm.bean.system.vo.business.monitor.BMonitorPreviewFileVo;
import com.xinyirun.scm.bean.system.vo.business.track.BTrackVo;
import com.xinyirun.scm.bean.system.vo.mongo.file.SFileMonitorInfoMongoVo;
import com.xinyirun.scm.bean.system.vo.mongo.monitor.v2.*;
import com.xinyirun.scm.bean.system.vo.mongo.track.BMonitorTrackMongoDataVo;
import com.xinyirun.scm.bean.system.vo.sys.file.BackupFileVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SBackupLogVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.properies.SystemConfigProperies;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.monitor.BMonitorMapper;
import com.xinyirun.scm.core.system.mapper.mongobackup.monitor.v2.BMonitorBackupV2Mapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.service.business.bkmonitor.v2.IBBkMonitorLogV2Service;
import com.xinyirun.scm.core.system.service.mongobackup.file.ISFileInfoMongoService;
import com.xinyirun.scm.core.system.service.mongobackup.monitor.v2.IBMonitorBackupBusinessV2Service;
import com.xinyirun.scm.core.system.service.mongobackup.monitor.v2.IBMonitorInBackupV2Service;
import com.xinyirun.scm.core.system.service.mongobackup.monitor.v2.IBMonitorOutBackupV2Service;
import com.xinyirun.scm.core.system.service.mongobackup.track.IBTrackBackupService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 监管任务 备份 业务逻辑
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-12
 */
@Service
@Slf4j
public class BMonitorBackupBusinessV2ServiceImpl extends ServiceImpl<BMonitorMapper, BMonitorEntity> implements IBMonitorBackupBusinessV2Service {

    @Autowired
    private BMonitorBackupV2Mapper mapper;

    @Autowired
    private IBMonitorInBackupV2Service monitorInService;

    @Autowired
    private IBMonitorOutBackupV2Service monitorOutService;

    @Autowired
    private ISFileInfoMongoService infoMongoService;

    @Autowired
    private IBTrackBackupService trackService;

    @Autowired
    private IBBkMonitorLogV2Service logService;

    @Autowired
    private ISConfigService configService;

    @Autowired
    private SystemConfigProperies systemConfigProperies;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    /**
     * mongo 备份
     *
     * @param size 条数
     */
//    @Override
//    public void backupDataLimitSize(Integer size) {
//        //初始时间
//        int currentSize = 0;
//        int page = 1;
//        int pageSize = 2;
//        saveData(currentSize, page, pageSize, size);
//    }

    /**
     * 同步数据
     *
     * @param param 同步数据条件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer insertBackupLog(BBkMonitorVo param) {
        // 保存日志, 返回新增id
        return logService.saveBackupData(param);
    }


    /**
     * 查询 数据传入 mq
     *
     * @param param
     * @return
     */
    @Override
    public List<BBkMonitorLogDetailVo> selectData2Mq(BBkMonitorVo param) {
        return mapper.selectData2Mq(param);
    }

    /**
     * 查询备份条数
     *
     * @param param
     * @return
     */
    @Override
    public BBkMonitorVo selectPageMyCount(BBkMonitorVo param) {
        BBkMonitorVo result = new BBkMonitorVo();
        Long count = mapper.selectPageMyCount(param);
        result.setCount(count);
        return result;
    }

    /**
     * 查询 分页显示数据, 根据id
     *
     * @param id
     * @return
     */
    @Override
    public BMonitorDataMongoEntity selectPageById(Integer id) {
        return mapper.selectPageById(id);
    }


//    /**
//     * 查询数据 并保存
//     *
//     * @param currentSize 当前条数
//     * @param page        分页页数
//     * @param pageSize    分页条数
//     * @param size        备份总条数
//     */
//    private void saveData(int currentSize, int page, int pageSize, Integer size) {
//        // 集合条数大于货等于 传入的 size , 结束
//        if (currentSize >= size) {
//            return;
//        }
//        // 当前查询的集合大小
//        int listSize = 0;
//
//        log.info("当前页数: ==>" + page);
//        log.info("当前条数: ==>" + currentSize);
//        try {
//            Page<BMonitorEntity> pageCondition = new Page<>(page, pageSize, false);
//            IPage<BMonitorDataMongoVo> bMonitorVoIPage = mapper.selectPage(pageCondition);
//            List<BMonitorDataMongoVo> records = bMonitorVoIPage.getRecords();
//
//            // 查询详情
//            records.forEach(item -> item.setDetailVo(getDetail(item.getId())));
//
//            // 每 500 条存储一次
//            saveMongo(records);
//
////            List<Integer> collect = records.stream().map(BMonitorDataMongoVo::getId).collect(Collectors.toList());
//            listSize = records.size();
//
//        } catch (Exception e) {
//            log.error("备份错误-> {}", e.getMessage());
//        }
//        // 查询条数少于分页条数, 返回
//        if (listSize < pageSize) {
//            return;
//        }
//        currentSize = currentSize + listSize;
//        // 判断查询条数
//        if ((currentSize + pageSize) > size) {
//            pageSize = size - currentSize;
//        }
//        page = page + 1;
//        saveData(currentSize, page, pageSize, size);
//    }

    /**
     * 查询详情
     *
     * @param id id
     * @return BMonitorDataDetailMongoVo
     */
    public BMonitorDataDetailMongoV2Vo getDetail(Integer id) {
        // 获取监管任务详情
        BMonitorDataDetailMongoV2Vo monitorVo = mapper.selectId(id);
//        StopWatchUtil.stopAndStartNew("查询详情", "111", "111");
        // 获取监管入库详情
        BMonitorInUnloadDataMongoV2Vo monitorInVo = monitorInService.selectMonitorInUnloadByMonitorId(id);
//        StopWatchUtil.print("查询详情", "111");
        // 设置返回页面附件对象
        if (monitorInVo != null) {
            setFile(monitorInVo);
//            monitorInVo.setContainerInfos(ibContainerInfoService.selectContainerInfos(monitorInVo.getId(), monitorInVo.getType()));
            // 获取监管出库详情
            monitorVo.setMonitorInVo(monitorInVo);
        }
        // 获取监管出库详情
        BMonitorOutDeliveryDataMongoV2Vo monitorOutVo = monitorOutService.selectOutDeliveryByMonitorId(id);

        if (monitorOutVo != null) {
            // 设置返回页面附件对象
            setFile(monitorOutVo);
//            monitorOutVo.setContainerInfos(ibContainerInfoService.selectContainerInfos(monitorOutVo.getId(), monitorOutVo.getType()));
            monitorVo.setMonitorOutVo(monitorOutVo);
        }
        // 获取轨迹
        BMonitorTrackMongoDataVo track = getTrack(monitorVo);
        monitorVo.setTrackContent(track);
        // 获取图片地址
        List<BPreviewBackupDataV2Vo> previewFiles = getPreviewFiles(id);
        monitorVo.setPreviewFiles(previewFiles);
        return monitorVo;
    }


    /**
     * @param vo
     */
    @Override
    public void selectForUpdate(BBkMonitorLogDetailVo vo) {
        mapper.selectForUpdate(vo);

    }

    /**
     * 查询 文件
     *
     * @param monitorId
     * @return
     */
    @Override
    public void getMonitorFiles(Integer monitorId) {
        BMonitorFilePreviewBackupDataV2Vo monitorFile = mapper.getMonitorFile(monitorId);
        List<Integer> allFileId = getAllFileId(monitorFile);

        log.debug("allFileId ==> {}", JSONObject.toJSONString(allFileId));

        if (!CollectionUtils.isEmpty(allFileId)) {
            SConfigEntity backup_url = configService.selectByKey(SystemConstants.BACKUP_URL);
            SConfigEntity backup_uri = configService.selectByKey(SystemConstants.BACKUP_URI);
            // 不为空, 存在没有备份的 文件, 进行备份
            List<SBackupLogVo> list = mapper.selectBackupFileList(allFileId);
            BackupFileVo backupFileVo = new BackupFileVo();
            list.forEach(item -> item.setOriginal_urldisk(getPath(item.getSource_file_url())));
            backupFileVo.setItems(list);
            backupFileVo.setApp_key(getApp_key());
            String fileUrl = getFileUrl(backup_uri.getValue(), backup_url.getValue());

            log.debug("backup_url ==> {}", JSONObject.toJSONString(backup_url));
            log.debug("backup_uri ==> {}", JSONObject.toJSONString(backup_uri));
            log.debug("list ==> {}", JSONObject.toJSONString(list));


            ResponseEntity<String> response = restTemplate.postForEntity(fileUrl, backupFileVo, String.class);

            log.debug("response ==> {}", JSONObject.toJSONString(response));


            BackupFileVo vo = JSON.parseObject(response.getBody(), BackupFileVo.class);
            for (SBackupLogVo item:vo.getItems()) {
                SFileInfoEntity sFileInfoEntity = fileInfoMapper.selectById(item.getId());
                sFileInfoEntity.setStatus(item.getStatus());
                sFileInfoEntity.setRemark(item.getRemark());
                sFileInfoEntity.setUrl(item.getTarget_file_url());
                sFileInfoEntity.setBackup_time(item.getBackup_time());
                sFileInfoEntity.setType(SystemConstants.FILE_TYPE.ALI_OSS);
                fileInfoMapper.update(sFileInfoEntity);
                log.debug("==================返写url结束================="+item);
            }
        }
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

    /**
     * 拼接中台同步数据url
     * @param uri
     * @return
     */
    protected String getFileUrl(String uri, String url) {
        try {
            String app_key= systemConfigProperies.getApp_key();
            String secret_key = systemConfigProperies.getSecret_key();

            return url + uri + "?app_key="+app_key+"&secret_key="+secret_key;

        } catch (Exception e) {
            log.error("getFileUrl error", e);
        }
        return "";
    }

    /**
     * 查询所有的 文件ID
         * @param vo
     */
    private List<Integer> getAllFileId(BMonitorFilePreviewBackupDataV2Vo vo) {
        List<Integer> ids = new ArrayList<>();
        // 空车过磅 start
        if (StringUtils.isNotEmpty(vo.getFile_1().getUrl()) && !"1".equals(vo.getFile_1().getStatus())) {
            // 空车过磅-司机车头照片
            ids.add(vo.getFile_1().getId());
        }

        if (StringUtils.isNotEmpty(vo.getFile_2().getUrl()) && !"1".equals(vo.getFile_2().getStatus())) {
            // 空车过磅-司机车尾照片
            ids.add(vo.getFile_2().getId());
        }

        if (StringUtils.isNotEmpty(vo.getFile_40().getUrl()) && !"1".equals(vo.getFile_40().getStatus())) {
            // 空车过磅-车厢情况照片
            ids.add(vo.getFile_40().getId());
        }

        if (StringUtils.isNotEmpty(vo.getFile_3().getUrl()) && !"1".equals(vo.getFile_3().getStatus())) {
            // 空车过磅-司机承诺书
            ids.add(vo.getFile_3().getId());
        }

        if (StringUtils.isNotEmpty(vo.getFile_4().getUrl()) && !"1".equals(vo.getFile_4().getStatus())) {
            // 空车过磅-司机身份证
            ids.add(vo.getFile_4().getId());
        }

        if (StringUtils.isNotEmpty(vo.getFile_38().getUrl()) && !"1".equals(vo.getFile_38().getStatus())) {
            // 空车过磅-司机驾驶证
            ids.add(vo.getFile_38().getId());
        }

        if (StringUtils.isNotEmpty(vo.getFile_39().getUrl()) && !"1".equals(vo.getFile_39().getStatus())) {
            // 空车过磅-车辆行驶证
            ids.add(vo.getFile_39().getId());
        }

        // 空车过磅 end
        // 正在装货 start
        if (StringUtils.isNotEmpty(vo.getFile_5().getUrl()) && !"1".equals(vo.getFile_5().getStatus())) {
            // 正在装货-司机车头照片
            ids.add(vo.getFile_5().getId());
        }

        if (StringUtils.isNotEmpty(vo.getFile_6().getUrl()) && !"1".equals(vo.getFile_6().getStatus())) {
            // 正在装货-司机车尾照片
            ids.add(vo.getFile_6().getId());
        }

        if (StringUtils.isNotEmpty(vo.getFile_7().getUrl()) && !"1".equals(vo.getFile_7().getStatus())) {
            // 正在装货-车侧身照片
            ids.add(vo.getFile_7().getId());
        }

        if (StringUtils.isNotEmpty(vo.getFile_8().getUrl()) && !"1".equals(vo.getFile_8().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_8().getId());
        }
        // 正在装货 end

        // 发货集装箱 start
        if (StringUtils.isNotEmpty(vo.getFile_9().getUrl()) && !"1".equals(vo.getFile_9().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_9().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_10().getUrl()) && !"1".equals(vo.getFile_10().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_10().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_11().getUrl()) && !"1".equals(vo.getFile_11().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_11().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_12().getUrl()) && !"1".equals(vo.getFile_12().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_12().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_13().getUrl()) && !"1".equals(vo.getFile_13().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_13().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_14().getUrl()) && !"1".equals(vo.getFile_14().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_14().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_15().getUrl()) && !"1".equals(vo.getFile_15().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_15().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_16().getUrl()) && !"1".equals(vo.getFile_16().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_16().getId());
        }
        // 发货集装箱 end

        // 重车出库 start
        if (StringUtils.isNotEmpty(vo.getFile_17().getUrl()) && !"1".equals(vo.getFile_17().getStatus())) {
            // 重车出库-司机车头照片
            ids.add(vo.getFile_17().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_18().getUrl()) && !"1".equals(vo.getFile_18().getStatus())) {
            // 重车出库-司机车头照片
            ids.add(vo.getFile_18().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_19().getUrl()) && !"1".equals(vo.getFile_19().getStatus())) {
            // 重车出库-磅单
            ids.add(vo.getFile_19().getId());
        }
        // 重车出库 end
        // 重车过磅 start
        if (StringUtils.isNotEmpty(vo.getFile_20().getUrl()) && !"1".equals(vo.getFile_20().getStatus())) {
            // 重车出库-司机车头照片
            ids.add(vo.getFile_20().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_21().getUrl()) && !"1".equals(vo.getFile_21().getStatus())) {
            // 重车出库-司机车尾照片
            ids.add(vo.getFile_21().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_22().getUrl()) && !"1".equals(vo.getFile_22().getStatus())) {
            // 重车出库-司机车尾照片
            ids.add(vo.getFile_22().getId());
        }
        // 重车过磅 end
        // 正在卸货 start
        if (StringUtils.isNotEmpty(vo.getFile_23().getUrl()) && !"1".equals(vo.getFile_23().getStatus())) {
            // 正在卸货-司机车头照片
            ids.add(vo.getFile_23().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_24().getUrl()) && !"1".equals(vo.getFile_24().getStatus())) {
            // 正在卸货-司机车尾照片
            ids.add(vo.getFile_24().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_25().getUrl()) && !"1".equals(vo.getFile_25().getStatus())) {
            // 正在卸货-车侧身照片
            ids.add(vo.getFile_25().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_26().getUrl()) && !"1".equals(vo.getFile_26().getStatus())) {
            // 正在卸货-卸货视频
            ids.add(vo.getFile_26().getId());
        }
        // 正在卸货 end
        // 收货集装箱 start
        if (StringUtils.isNotEmpty(vo.getFile_27().getUrl()) && !"1".equals(vo.getFile_27().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_27().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_28().getUrl()) && !"1".equals(vo.getFile_28().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_28().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_29().getUrl()) && !"1".equals(vo.getFile_29().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_29().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_30().getUrl()) && !"1".equals(vo.getFile_30().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_30().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_31().getUrl()) && !"1".equals(vo.getFile_31().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_31().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_32().getUrl()) && !"1".equals(vo.getFile_32().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_32().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_33().getUrl()) && !"1".equals(vo.getFile_33().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_33().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_34().getUrl()) && !"1".equals(vo.getFile_34().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_34().getId());
        }
        // 收货集装箱 end
        // 空车出库 start
        if (StringUtils.isNotEmpty(vo.getFile_35().getUrl()) && !"1".equals(vo.getFile_35().getStatus())) {
            // 正在装货-车侧身照片
            ids.add(vo.getFile_35().getId());
        }
        if (StringUtils.isNotEmpty(vo.getFile_36().getUrl()) && !"1".equals(vo.getFile_36().getStatus())) {
            // 正在装货-装货视频
            ids.add(vo.getFile_36().getId());
        }

        if (StringUtils.isNotEmpty(vo.getFile_37().getUrl()) && !"1".equals(vo.getFile_37().getStatus())) {
            // 重车出库-司机车头照片
            ids.add(vo.getFile_37().getId());
        }

        if (StringUtils.isNotEmpty(vo.getFile_41().getUrl())&& !"1".equals(vo.getFile_41().getStatus())) {
            //直采入库-空车出库-司机行驶证
            ids.add(vo.getFile_41().getId());
        }

        if (StringUtils.isNotEmpty(vo.getFile_42().getUrl())&& !"1".equals(vo.getFile_42().getStatus())) {
            //直采入库-空车出库-商品近照
            ids.add(vo.getFile_42().getId());
        }

        if (StringUtils.isNotEmpty(vo.getFile_43().getUrl())&& !"1".equals(vo.getFile_43().getStatus())) {
            //直销出库-重车出库-商品近照
            ids.add(vo.getFile_43().getId());
        }

        return ids;
    }

    /**
     * 获取轨迹
     *
     * @param detailMongoVo vo
     * @return BMonitorTrackMongoDataVo
     */
    private BMonitorTrackMongoDataVo getTrack(BMonitorDataDetailMongoV2Vo detailMongoVo) {
        BTrackVo vo = new BTrackVo();
        vo.setVehicle_no(detailMongoVo.getVehicle_no());
        vo.setWaybill_no(detailMongoVo.getWaybill_code());
        vo.setStart_time(detailMongoVo.getTrack_start_time());
        vo.setEnd_time(detailMongoVo.getTrack_end_time());
        return trackService.get(vo);
    }

    /**
     * 备份 mongodb
     *
     * @param list
     */
//    @Transactional(rollbackFor = Exception.class, value = "transactionManager")
//    private void saveMongo(List<BMonitorDataMongoVo> list) {
//        monitorMongoService.saveAll(list);
//    }

    /**
     * 设置返回页面入库监管附件对象
     */
    public void setFile(BMonitorInUnloadDataMongoV2Vo vo) {
        SFileMonitorInfoMongoVo emptyVo = new SFileMonitorInfoMongoVo();
        SFileMonitorInfoMongoVo fileInfoVo;
        // 车头车尾带司机附件
        if (vo.getOne_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getOne_file());
            vo.setOne_fileVo(fileInfoVo);
        } else {
            vo.setOne_fileVo(emptyVo);
        }
        // 重车过磅附件
        if (vo.getTwo_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getTwo_file());
            vo.setTwo_fileVo(fileInfoVo);
        } else {
            vo.setTwo_fileVo(emptyVo);
        }
        // 卸货照片附件
        if (vo.getThree_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getThree_file());
            vo.setThree_fileVo(fileInfoVo);
        } else {
            vo.setThree_fileVo(emptyVo);
        }
        // 卸货视频附件
        if (vo.getFour_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getFour_file());
            vo.setFour_fileVo(fileInfoVo);
        } else {
            vo.setFour_fileVo(emptyVo);
        }
        // 车头车尾带司机附件
        if (vo.getFive_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getFive_file());
            vo.setFive_fileVo(fileInfoVo);
        } else {
            vo.setFive_fileVo(emptyVo);
        }
        // 磅单(司机签字)附件
        if (vo.getSix_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getSix_file());
            vo.setSix_fileVo(fileInfoVo);
        } else {
            vo.setSix_fileVo(emptyVo);
        }
        if (vo.getSeven_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getSeven_file());
            vo.setSeven_fileVo(fileInfoVo);
        } else {
            vo.setSeven_fileVo(emptyVo);
        }
        if (vo.getEight_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getEight_file());
            vo.setEight_fileVo(fileInfoVo);
        } else {
            vo.setEight_fileVo(emptyVo);
        }
        if (vo.getNine_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getNine_file());
            vo.setNine_fileVo(fileInfoVo);
        } else {
            vo.setNine_fileVo(emptyVo);
        }
        if (vo.getTen_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getTen_file());
            vo.setTen_fileVo(fileInfoVo);
        } else {
            vo.setTen_fileVo(emptyVo);
        }

        if (vo.getEleven_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getEleven_file());
            vo.setEleven_fileVo(fileInfoVo);
        } else {
            vo.setEleven_fileVo(emptyVo);
        }

        if (vo.getTwelve_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getTwelve_file());
            vo.setTwelve_fileVo(fileInfoVo);
        } else {
            vo.setTwelve_fileVo(emptyVo);
        }
    }

    /**
     * 查询附件对象
     */
    private SFileMonitorInfoMongoVo getFileInfo(Integer id) {
        return infoMongoService.selectFId(id);
    }

    /**
     * 设置返回页面出库监管附件对象
     */
    public void setFile(BMonitorOutDeliveryDataMongoV2Vo vo) {
        SFileMonitorInfoMongoVo emptyVo = new SFileMonitorInfoMongoVo();
        SFileMonitorInfoMongoVo fileInfoVo;
        // 空车过磅附件
        if (vo.getOne_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getOne_file());
            vo.setOne_fileVo(fileInfoVo);
        } else {
            vo.setOne_fileVo(emptyVo);
        }
        // 车头车尾带司机附件
        if (vo.getTwo_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getTwo_file());
            vo.setTwo_fileVo(fileInfoVo);
        } else {
            vo.setTwo_fileVo(emptyVo);
        }
        // 车厢情况照片
        if (vo.getFourteen_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getFourteen_file());
            vo.setFourteen_fileVo(fileInfoVo);
        } else {
            vo.setFourteen_fileVo(emptyVo);
        }
        // 司机承诺书附件
        if (vo.getThree_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getThree_file());
            vo.setThree_fileVo(fileInfoVo);
        } else {
            vo.setThree_fileVo(emptyVo);
        }
        // 司机身份证
        if (vo.getFour_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getFour_file());
            vo.setFour_fileVo(fileInfoVo);
        } else {
            vo.setFour_fileVo(emptyVo);
        }
        // 司机驾驶证
        if (vo.getTwelve_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getTwelve_file());
            vo.setTwelve_fileVo(fileInfoVo);
        } else {
            vo.setTwelve_fileVo(emptyVo);
        }
        // 车辆行驶证
        if (vo.getThirteen_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getThirteen_file());
            vo.setThirteen_fileVo(fileInfoVo);
        } else {
            vo.setThirteen_fileVo(emptyVo);
        }
        // 车头过磅附件
        if (vo.getFive_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getFive_file());
            vo.setFive_fileVo(fileInfoVo);
        } else {
            vo.setFive_fileVo(emptyVo);
        }
        // 装货视频附件
        if (vo.getSix_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getSix_file());
            vo.setSix_fileVo(fileInfoVo);
        } else {
            vo.setSix_fileVo(emptyVo);
        }
        // 重车过磅附件
        if (vo.getSeven_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getSeven_file());
            vo.setSeven_fileVo(fileInfoVo);
        } else {
            vo.setSeven_fileVo(emptyVo);
        }
        // 车头车尾带司机附件(重车)
        if (vo.getEight_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getEight_file());
            vo.setEight_fileVo(fileInfoVo);
        } else {
            vo.setEight_fileVo(emptyVo);
        }
        // 磅单附件
        if (vo.getNine_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getNine_file());
            vo.setNine_fileVo(fileInfoVo);
        } else {
            vo.setNine_fileVo(emptyVo);
        }
        if (vo.getTen_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getTen_file());
            vo.setTen_fileVo(fileInfoVo);
        } else {
            vo.setTen_fileVo(emptyVo);
        }
        if (vo.getEleven_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getEleven_file());
            vo.setEleven_fileVo(fileInfoVo);
        } else {
            vo.setEleven_fileVo(emptyVo);
        }

        if (vo.getFifteen_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getFifteen_file());
            vo.setFifteen_fileVo(fileInfoVo);
        } else {
            vo.setFifteen_fileVo(emptyVo);
        }
    }

    private List<BPreviewBackupDataV2Vo> getPreviewFiles(Integer id) {
        BMonitorFilePreviewBackupDataV2Vo result = mapper.getMonitorFile(id);
        setPreviewMonitorFileTitle(result);
        int i = 0;
        List<BPreviewBackupDataV2Vo> bPreviewDataVoList = new ArrayList<>();
        if (result.getFile_1() != null && StringUtils.isNotEmpty(result.getFile_1().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_1(), i, 1);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_2() != null && StringUtils.isNotEmpty(result.getFile_2().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_2(), i, 2);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_40() != null && StringUtils.isNotEmpty(result.getFile_40().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_40(), i, 40);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_3() != null && StringUtils.isNotEmpty(result.getFile_3().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_3(), i, 3);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_4() != null && StringUtils.isNotEmpty(result.getFile_4().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_4(), i, 4);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_38() != null && StringUtils.isNotEmpty(result.getFile_38().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_38(), i, 38);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_39() != null && StringUtils.isNotEmpty(result.getFile_39().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_39(), i, 39);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_5() != null && StringUtils.isNotEmpty(result.getFile_5().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_5(), i, 5);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_6() != null && StringUtils.isNotEmpty(result.getFile_6().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_6(), i, 6);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_7() != null && StringUtils.isNotEmpty(result.getFile_7().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_7(), i, 7);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_8() != null && StringUtils.isNotEmpty(result.getFile_8().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_8(), i, 8);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_9() != null && StringUtils.isNotEmpty(result.getFile_9().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_9(), i, 9);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_10() != null && StringUtils.isNotEmpty(result.getFile_10().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_10(), i, 10);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_11() != null && StringUtils.isNotEmpty(result.getFile_11().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_11(), i, 11);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_12() != null && StringUtils.isNotEmpty(result.getFile_12().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_12(), i, 12);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_13() != null && StringUtils.isNotEmpty(result.getFile_13().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_13(), i, 13);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_14() != null && StringUtils.isNotEmpty(result.getFile_14().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_14(), i, 14);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_15() != null && StringUtils.isNotEmpty(result.getFile_15().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_15(), i, 15);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_16() != null && StringUtils.isNotEmpty(result.getFile_16().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_16(), i, 16);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_17() != null && StringUtils.isNotEmpty(result.getFile_17().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_17(), i, 17);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_18() != null && StringUtils.isNotEmpty(result.getFile_18().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_18(), i, 18);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_19() != null && StringUtils.isNotEmpty(result.getFile_19().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_19(), i, 19);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_20() != null && StringUtils.isNotEmpty(result.getFile_20().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_20(), i, 20);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_21() != null && StringUtils.isNotEmpty(result.getFile_21().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_21(), i, 21);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_22() != null && StringUtils.isNotEmpty(result.getFile_22().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_22(), i, 22);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_23() != null && StringUtils.isNotEmpty(result.getFile_23().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_23(), i, 23);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_24() != null && StringUtils.isNotEmpty(result.getFile_24().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_24(), i, 24);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_25() != null && StringUtils.isNotEmpty(result.getFile_25().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_25(), i, 25);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_26() != null && StringUtils.isNotEmpty(result.getFile_26().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_26(), i, 26);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_27() != null && StringUtils.isNotEmpty(result.getFile_27().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_27(), i, 27);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_28() != null && StringUtils.isNotEmpty(result.getFile_28().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_28(), i, 28);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_29() != null && StringUtils.isNotEmpty(result.getFile_29().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_29(), i, 29);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_30() != null && StringUtils.isNotEmpty(result.getFile_30().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_30(), i, 30);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_31() != null && StringUtils.isNotEmpty(result.getFile_31().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_31(), i, 31);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_32() != null && StringUtils.isNotEmpty(result.getFile_32().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_32(), i, 32);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_33() != null && StringUtils.isNotEmpty(result.getFile_33().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_33(), i, 33);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_34() != null && StringUtils.isNotEmpty(result.getFile_34().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_34(), i, 34);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_35() != null && StringUtils.isNotEmpty(result.getFile_35().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_35(), i, 35);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_36() != null && StringUtils.isNotEmpty(result.getFile_36().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_36(), i, 36);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_37() != null && StringUtils.isNotEmpty(result.getFile_37().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_37(), i, 37);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }

        if (result.getFile_41() != null && StringUtils.isNotEmpty(result.getFile_41().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_41(), i,41);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }

        if (result.getFile_42() != null && StringUtils.isNotEmpty(result.getFile_42().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_42(), i,42);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }

        if (result.getFile_43() != null && StringUtils.isNotEmpty(result.getFile_43().getUrl())) {
            BPreviewBackupDataV2Vo bPreviewDataVo = getBPreviewData(result.getFile_43(), i,43);
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        return bPreviewDataVoList;
    }

    private void setPreviewMonitorFileTitle(BMonitorFilePreviewBackupDataV2Vo vo) {

        // 空车过磅 start
        if (StringUtils.isNotEmpty(vo.getFile_1().getUrl())) {
            // 空车过磅-司机车头照片
            vo.getFile_1().setFile_title("司机车头照片");
        }

        if (StringUtils.isNotEmpty(vo.getFile_2().getUrl())) {
            // 空车过磅-司机车尾照片
            vo.getFile_2().setFile_title("司机车尾照片");
        }

        if (StringUtils.isNotEmpty(vo.getFile_40().getUrl())) {
            // 空车过磅-司机车尾照片
            vo.getFile_40().setFile_title("车厢情况照片");
        }

        if (StringUtils.isNotEmpty(vo.getFile_3().getUrl())) {
            // 空车过磅-司机承诺书
            vo.getFile_3().setFile_title("司机承诺书");
        }

        if (StringUtils.isNotEmpty(vo.getFile_4().getUrl())) {
            // 空车过磅-司机身份证
            vo.getFile_4().setFile_title("司机身份证");
        }

        if (StringUtils.isNotEmpty(vo.getFile_38().getUrl())) {
            // 空车过磅-司机驾驶证
            vo.getFile_38().setFile_title("司机驾驶证");
        }

        if (StringUtils.isNotEmpty(vo.getFile_39().getUrl())) {
            // 空车过磅-车辆行驶证
            vo.getFile_39().setFile_title("车辆行驶证");
        }

        // 空车过磅 end


        // 正在装货 start
        if (StringUtils.isNotEmpty(vo.getFile_5().getUrl())) {
            // 正在装货-司机车头照片
            vo.getFile_5().setFile_title("司机车头照片");
        }

        if (StringUtils.isNotEmpty(vo.getFile_6().getUrl())) {
            // 正在装货-司机车尾照片
            vo.getFile_6().setFile_title("司机车尾照片");
        }

        if (StringUtils.isNotEmpty(vo.getFile_7().getUrl())) {
            // 正在装货-车侧身照片
            vo.getFile_7().setFile_title("车侧身照片");
        }

        if (StringUtils.isNotEmpty(vo.getFile_8().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_8().setFile_title("装货视频");
        }
        // 正在装货 end

        // 发货集装箱 start
        if (StringUtils.isNotEmpty(vo.getFile_9().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_9().setFile_title("集装箱箱号照片");
        }
        if (StringUtils.isNotEmpty(vo.getFile_10().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_10().setFile_title("集装箱内部空箱照片");
        }
        if (StringUtils.isNotEmpty(vo.getFile_11().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_11().setFile_title("集装箱装货视频");
        }
        if (StringUtils.isNotEmpty(vo.getFile_12().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_12().setFile_title("磅单(司机签字)");
        }
        if (StringUtils.isNotEmpty(vo.getFile_13().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_13().setFile_title("集装箱箱号照片");
        }
        if (StringUtils.isNotEmpty(vo.getFile_14().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_14().setFile_title("集装箱内部空箱照片2");
        }
        if (StringUtils.isNotEmpty(vo.getFile_15().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_15().setFile_title("集装箱装货视频2");
        }
        if (StringUtils.isNotEmpty(vo.getFile_16().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_16().setFile_title("磅单2(司机签字)");
        }
        // 发货集装箱 end

        // 重车出库 start
        if (StringUtils.isNotEmpty(vo.getFile_17().getUrl())) {
            // 重车出库-司机车头照片
            vo.getFile_17().setFile_title("司机车头照片");
        }
        if (StringUtils.isNotEmpty(vo.getFile_18().getUrl())) {
            // 重车出库-司机车头照片
            vo.getFile_18().setFile_title("司机车尾照片");
        }
        if (StringUtils.isNotEmpty(vo.getFile_19().getUrl())) {
            // 重车出库-磅单
            vo.getFile_19().setFile_title("磅单");
        }
        // 重车出库 end
        // 重车过磅 start
        if (StringUtils.isNotEmpty(vo.getFile_20().getUrl())) {
            // 重车出库-司机车头照片
            vo.getFile_20().setFile_title("司机车头照片");
        }
        if (StringUtils.isNotEmpty(vo.getFile_21().getUrl())) {
            // 重车出库-司机车尾照片
            vo.getFile_21().setFile_title("司机车尾照片");
        }
        if (StringUtils.isNotEmpty(vo.getFile_22().getUrl())) {
            // 重车出库-司机车尾照片
            vo.getFile_22().setFile_title("行车轨迹");
        }
        // 重车过磅 end
        // 正在卸货 start
        if (StringUtils.isNotEmpty(vo.getFile_23().getUrl())) {
            // 正在卸货-司机车头照片
            vo.getFile_23().setFile_title("司机车头照片");
        }
        if (StringUtils.isNotEmpty(vo.getFile_24().getUrl())) {
            // 正在卸货-司机车尾照片
            vo.getFile_24().setFile_title("司机车尾照片");
        }
        if (StringUtils.isNotEmpty(vo.getFile_25().getUrl())) {
            // 正在卸货-车侧身照片
            vo.getFile_25().setFile_title("车侧身照片");
        }
        if (StringUtils.isNotEmpty(vo.getFile_26().getUrl())) {
            // 正在卸货-卸货视频
            vo.getFile_26().setFile_title("卸货视频");
        }
        // 正在卸货 end
        // 收货集装箱 start
        if (StringUtils.isNotEmpty(vo.getFile_27().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_27().setFile_title("集装箱箱号照片");
        }
        if (StringUtils.isNotEmpty(vo.getFile_28().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_28().setFile_title("集装箱内部空箱照片");
        }
        if (StringUtils.isNotEmpty(vo.getFile_29().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_29().setFile_title("集装箱卸货视频");
        }
        if (StringUtils.isNotEmpty(vo.getFile_30().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_30().setFile_title("磅单(司机签字)");
        }
        if (StringUtils.isNotEmpty(vo.getFile_31().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_31().setFile_title("集装箱箱号照片");
        }
        if (StringUtils.isNotEmpty(vo.getFile_32().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_32().setFile_title("集装箱内部空箱照片2");
        }
        if (StringUtils.isNotEmpty(vo.getFile_33().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_33().setFile_title("集装箱卸货视频");
        }
        if (StringUtils.isNotEmpty(vo.getFile_34().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_34().setFile_title("磅单(司机签字)");
        }
        // 收货集装箱 end
        // 空车出库 start
        if (StringUtils.isNotEmpty(vo.getFile_35().getUrl())) {
            // 正在装货-车侧身照片
            vo.getFile_35().setFile_title("司机车头照片");
        }
        if (StringUtils.isNotEmpty(vo.getFile_36().getUrl())) {
            // 正在装货-装货视频
            vo.getFile_36().setFile_title("司机车尾照片");
        }

        if (StringUtils.isNotEmpty(vo.getFile_37().getUrl())) {
            // 重车出库-司机车头照片
            vo.getFile_37().setFile_title("磅单");
        }

        if (StringUtils.isNotEmpty(vo.getFile_41().getUrl())) {
            // 直采入库-司机行驶证
            vo.getFile_41().setFile_title("司机行驶证");
        }

        if (StringUtils.isNotEmpty(vo.getFile_42().getUrl())) {
            // 直采入库-商品近照
            vo.getFile_42().setFile_title("商品近照");
        }

        if (StringUtils.isNotEmpty(vo.getFile_43().getUrl())) {
            // 直销出库-商品近照
            vo.getFile_43().setFile_title("商品近照");
        }
    }

    private BPreviewBackupDataV2Vo getBPreviewData(BMonitorPreviewFileVo vo, int i, int file_num) {
//        file_num = i + 1;
        BPreviewBackupDataV2Vo bPreviewDataVo = new BPreviewBackupDataV2Vo();
        bPreviewDataVo.setUrl(vo.getUrl());
        if (vo.getUrl().contains(".mp4")) {
            bPreviewDataVo.setHtml("<video class=\"lg-video-object lg-html5\" controls=\"controls\" preload=\"true\" autostart=\"true\" autoplay><source src=\"" + vo.getUrl() + "\" type=\"video/webm\">Your browser does not support HTML5 video</video>");
            bPreviewDataVo.setThumb("http://file.shyiyuanth.cn/file/steel/2022/20220720/149/aff7b2eef72343a69e49c27915cb31fb/ec7cf23cb186204a8e9635a57d863e50.jpeg");
        } else {
            bPreviewDataVo.setSrc(vo.getUrl());
            bPreviewDataVo.setThumb(vo.getUrl());
        }

//        bPreviewDataVo.setSubHtml("<h3>" + vo.getFile_title() + "</h3><p>" + vo.getUrl() + "</p><p>" + vo.getU_time() + "</p ><p>" + vo.getU_name() + "|" + vo.getLogin_name() + "</p >");
        bPreviewDataVo.setSubHtml("<h3>" + vo.getFile_title() + "</h3><p>" + vo.getU_time() + "</p ><p>" + vo.getU_name() + "|" + vo.getLogin_name() + "</p >");
        bPreviewDataVo.setIndex(i);
        bPreviewDataVo.setFile_num(file_num);
        return bPreviewDataVo;
    }

    protected String getApp_key() {
        return systemConfigProperies.getApp_key();
    }
}
