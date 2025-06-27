package com.xinyirun.scm.core.system.serviceimpl.business.monitor;

import cn.hutool.core.net.url.UrlBuilder;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xinyirun.scm.bean.api.vo.business.ApiCanceledDataVo;
import com.xinyirun.scm.bean.api.vo.business.ApiCanceledVo;
import com.xinyirun.scm.bean.api.vo.business.monitor.ApiMonitorItemVo;
import com.xinyirun.scm.bean.api.vo.business.monitor.ApiMonitorVo;
import com.xinyirun.scm.bean.entity.busniess.in.BInEntity;
import com.xinyirun.scm.bean.entity.busniess.in.delivery.BDeliveryEntity;
import com.xinyirun.scm.bean.entity.busniess.inplan.BInPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.monitor.*;
import com.xinyirun.scm.bean.entity.busniess.out.BOutEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutExtraEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.out.receive.BReceiveEntity;
import com.xinyirun.scm.bean.entity.busniess.out.receive.BReceiveExtraEntity;
import com.xinyirun.scm.bean.entity.busniess.returnrelation.BReturnRelationEntity;
import com.xinyirun.scm.bean.entity.busniess.schedule.BScheduleEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.bo.business.alarm.BAlarmRulesBo;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.wms.in.delivery.BDeliveryVo;
import com.xinyirun.scm.bean.system.vo.wms.in.BInVo;
import com.xinyirun.scm.bean.system.vo.business.inventory.*;
import com.xinyirun.scm.bean.system.vo.business.monitor.*;
import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanDetailVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutVo;
import com.xinyirun.scm.bean.system.vo.business.out.receive.BReceiveVo;
import com.xinyirun.scm.bean.system.vo.business.returnrelation.BReturnRelationVo;
import com.xinyirun.scm.bean.system.vo.business.track.BTrackVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigDetailVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.system.vo.sys.unit.SUnitVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.app.AppBusinessException;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.common.utils.LocalDateTimeUtils;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.wms.in.delivery.BDeliveryExtraMapper;
import com.xinyirun.scm.core.system.mapper.wms.in.delivery.BDeliveryMapper;
import com.xinyirun.scm.core.system.mapper.wms.in.BInMapper;
import com.xinyirun.scm.core.system.mapper.wms.inplan.BInPlanDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.monitor.*;
import com.xinyirun.scm.core.system.mapper.business.out.BOutExtraMapper;
import com.xinyirun.scm.core.system.mapper.business.out.BOutMapper;
import com.xinyirun.scm.core.system.mapper.business.out.BOutPlanDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.out.receive.BReceiveExtraMapper;
import com.xinyirun.scm.core.system.mapper.business.out.receive.BReceiveMapper;
import com.xinyirun.scm.core.system.mapper.business.returnrelation.BReturnRelationMapper;
import com.xinyirun.scm.core.system.mapper.business.schedule.BScheduleMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.inventory.ICommonInventoryLogicService;
import com.xinyirun.scm.core.system.service.business.alarm.IBAlarmRulesService;
import com.xinyirun.scm.core.system.service.business.monitor.IBContainerInfoService;
import com.xinyirun.scm.core.system.service.business.monitor.IBMonitorFileExportSettingsService;
import com.xinyirun.scm.core.system.service.business.monitor.IBMonitorService;
import com.xinyirun.scm.core.system.service.business.out.IBOutService;
import com.xinyirun.scm.core.system.service.business.out.receive.IBReceiveService;
import com.xinyirun.scm.core.system.service.business.schedule.IBScheduleService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigDetailService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.unit.ISUnitService;
import com.xinyirun.scm.core.system.service.track.bestfriend.IBTrackBestFriendService;
import com.xinyirun.scm.core.system.service.track.gsh56.IBTrackGsh56Service;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BDeliveryAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BInAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BOutAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BReceiveAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import jakarta.servlet.http.HttpServletResponse;
import jodd.io.NetUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 监管任务_出库 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-12
 */
@Service
public class BMonitorServiceImpl extends ServiceImpl<BMonitorMapper, BMonitorEntity> implements IBMonitorService {

    @Autowired
    private ISAppConfigDetailService isAppConfigDetailService;

    @Autowired
    private ISAppConfigService isAppConfigService;

    @Autowired
    private BMonitorMapper mapper;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private IBContainerInfoService ibContainerInfoService;

    @Autowired
    private BMonitorOutMapper monitorOutMapper;

    @Autowired
    private BMonitorInMapper monitorInMapper;

    @Autowired
    private BMonitorDeliveryMapper monitorDeliveryMapper;

    @Autowired
    private BMonitorUnloadMapper monitorUnloadMapper;

    @Autowired
    private IBOutService ibOutService;

    @Autowired
    private BOutMapper bOutMapper;

    @Autowired
    private BInMapper bInMapper;

    @Autowired
    private IBScheduleService ibScheduleService;

    @Autowired
    private BOutPlanDetailMapper outPlanDetailMapper;

    @Autowired
    private ISUnitService isUnitService;

    @Autowired
    private BInMapper inMapper;

    @Autowired
    private BOutMapper outMapper;

    @Autowired
    private BOutExtraMapper outExtraMapper;

    @Autowired
    private BOutAutoCodeServiceImpl outCode;

    @Autowired
    private BInAutoCodeServiceImpl inCode;

    @Autowired
    private ICommonInventoryLogicService iCommonInventoryLogicService;

    @Autowired
    private BInPlanDetailMapper inPlanDetailMapper;

    @Autowired
    private BScheduleMapper bScheduleMapper;

    @Autowired
    private MCancelService mCancelService;

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    private IBTrackGsh56Service ibTrackGsh56Service;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    public WebClient webClient;

    @Value("${server.port}")
    private int port;

    @Autowired
    IBTrackBestFriendService bestFriendService;

    @Autowired
    private IBAlarmRulesService alarmRulesService;

    @Autowired
    private IBMonitorFileExportSettingsService exportSettingsService;

    @Autowired
    private BReceiveMapper bReceiveMapper;

    @Autowired
    private BReceiveExtraMapper bReceiveExtraMapper;

    @Autowired
    private BReceiveAutoCodeServiceImpl receiveCode;

    @Autowired
    private BDeliveryMapper bDeliveryMapper;

    @Autowired
    private BDeliveryExtraMapper bDeliveryExtraMapper;

    @Autowired
    private BDeliveryAutoCodeServiceImpl deliveryCode;

    @Autowired
    private IBReceiveService ibReceiveService;

//    @Autowired
//    private IBDeliveryService ibDeliveryService;

    @Autowired
    private BReturnRelationMapper bReturnRelationMapper;

//    @Autowired
//    private IBReturnRelationService ibReturnRelationService;



    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t4.in_warehouse_id,t4.out_warehouse_id")
    public IPage<BMonitorVo> selectPage(BMonitorVo searchCondition) {

        // 分页条件
        Page<BMonitorEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());

        // 替换分页插件自动count sql 因为该sql执行速度非常慢
        pageCondition.setCountId("selectPageMyCount");

        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    public List<BMonitorVo> selectList(BMonitorVo searchCondition) {
        return mapper.selectLists(searchCondition);
    }

    @Override
    public List<BMonitorExportVo> selectExportList(BMonitorVo searchCondition) {
        return mapper.selectExportList(searchCondition);
    }

    @Override
//    @DataScopeAnnotion(type = "02", type02_condition = "t4.in_warehouse_id,t4.out_warehouse_id")
    @DataScopeAnnotion(type = "02", type02_condition = "t4.in_warehouse_id,t4.out_warehouse_id")
    public List<BMonitorExportVo> selectExportAllList(BMonitorVo searchCondition) {
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            Long count = mapper.selectPageMyCount(searchCondition);
            if (StringUtils.isNotNull(count) && count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.selectExportAllList(searchCondition);
    }

    @Override
    public BMonitorVo getDetail(BMonitorVo searchCondition) {

        String sort_sql = "";
        Integer currentNo = null;
        // 判断查询条件中是否包含排序
        if (searchCondition.getPageCondition() != null) {
            sort_sql = searchCondition.getPageCondition().getSort();
            sort_sql = sort_sql.replaceAll("-", "");
            currentNo = (int) (searchCondition.getPageCondition().getSize() * (searchCondition.getPageCondition().getCurrent() - 1) + searchCondition.getNo());
        }

        if (StringUtils.isEmpty(sort_sql)) {
            // 无排序
            searchCondition.setSort_sql("");
        } else if (searchCondition.getPageCondition().getSort().contains("-")) {
            // 倒序
            searchCondition.setSort_sql("order by " + sort_sql + " desc");
        } else {
            // 正序
            searchCondition.setSort_sql("order by " + sort_sql);
        }

        // 获取监管任务详情
        BMonitorVo monitorVo = mapper.selectId(searchCondition.getId());
        BMonitorInUnloadVo in = new BMonitorInUnloadVo();
        BMonitorOutDeliveryVo out = new BMonitorOutDeliveryVo();
        in.setId(monitorVo.getMonitor_in_id());
        // 获取监管入库详情
        BMonitorInUnloadVo monitorInVo = monitorInMapper.selectMonitorInUnloadByMonitorId(searchCondition.getId());
        // 设置返回页面附件对象
        if (monitorInVo != null) {
            setFile(monitorInVo);
            monitorInVo.setContainerInfos(ibContainerInfoService.selectContainerInfos(monitorInVo.getId(), monitorInVo.getType()));
            // 获取监管出库详情
            monitorVo.setMonitorInVo(monitorInVo);
        }

        // 获取监管出库详情
        BMonitorOutDeliveryVo monitorOutVo = monitorOutMapper.selectOutDeliveryByMonitorId(searchCondition.getId());
        if (monitorOutVo != null) {
            // 设置返回页面附件对象
            setFile(monitorOutVo);
            monitorOutVo.setContainerInfos(ibContainerInfoService.selectContainerInfos(monitorOutVo.getId(), monitorOutVo.getType()));
            monitorVo.setMonitorOutVo(monitorOutVo);
        }

        out.setId(monitorVo.getMonitor_out_id());

        searchCondition.setNo(null);
//        BMonitorVo current = mapper.getMonitor(searchCondition);

        if (currentNo != null) {
            BMonitorVo nextCondition = (BMonitorVo) BeanUtilsSupport.copyProperties(searchCondition, BMonitorVo.class);

            nextCondition.setNo(currentNo + 1);
            nextCondition.setId(null);
            BMonitorVo prevCondition = (BMonitorVo) BeanUtilsSupport.copyProperties(searchCondition, BMonitorVo.class);
            prevCondition.setNo(currentNo - 1);
            prevCondition.setId(null);

            BMonitorVo next = mapper.getMonitor(nextCondition);

            BMonitorVo prev = mapper.getMonitor(prevCondition);

            if (next != null && StringUtils.isNotEmpty(sort_sql)) {
                monitorVo.setNext_id(next.getId());
            } else {
                monitorVo.setNext_id(null);
            }

            if (prev != null && StringUtils.isNotEmpty(sort_sql)) {
                monitorVo.setPrev_id(prev.getId());
            } else {
                monitorVo.setPrev_id(null);
            }
        }

        List<BPreviewDataVo> bPreviewDataVoList = getPreviewFiles(monitorVo);
        monitorVo.setPreview_data(bPreviewDataVoList);

        return monitorVo;
    }

    @Override
    public BMonitorVo selectById(Integer id) {
        // 获取监管任务详情
        BMonitorVo monitorVo = mapper.selectId(id);
        return monitorVo;
    }

    @Override
    public BMonitorVo getPrevData(BMonitorVo searchCondition) {
        String sort_sql = "";
        Integer currentNo = null;
        // 判断查询条件中是否包含排序
        if (searchCondition.getPageCondition() != null) {
            sort_sql = searchCondition.getPageCondition().getSort();
            sort_sql = sort_sql.replaceAll("-", "");
            currentNo = (int) (searchCondition.getPageCondition().getSize() * (searchCondition.getPageCondition().getCurrent() - 1) + searchCondition.getIndex() + 1);
        }

        if (StringUtils.isEmpty(sort_sql)) {
            // 无排序
            searchCondition.setSort_sql("");
        } else if (searchCondition.getPageCondition().getSort().contains("-")) {
            // 倒序
            searchCondition.setSort_sql("order by " + sort_sql + " desc");
        } else {
            // 正序
            searchCondition.setSort_sql("order by " + sort_sql);
        }

        // 获取监管任务详情
        BMonitorVo monitorVo = new BMonitorVo();
        monitorVo.setId(searchCondition.getId());

        searchCondition.setNo(null);

        if (currentNo != null) {

            BMonitorVo prevCondition = (BMonitorVo) BeanUtilsSupport.copyProperties(searchCondition, BMonitorVo.class);
            prevCondition.setNo(currentNo - 1);
            prevCondition.setId(null);

            BMonitorVo prev = mapper.getMonitor(prevCondition);

            if (prev != null && StringUtils.isNotEmpty(sort_sql)) {
                monitorVo.setPrev_id(prev.getId());
            } else {
                monitorVo.setPrev_id(null);
            }
        }

        return monitorVo;
    }

    @Override
    public BMonitorVo getNextData(BMonitorVo searchCondition) {
        String sort_sql = "";
        Integer currentNo = null;
        // 判断查询条件中是否包含排序
        if (searchCondition.getPageCondition() != null) {
            sort_sql = searchCondition.getPageCondition().getSort();
            sort_sql = sort_sql.replaceAll("-", "");
            currentNo = (int) (searchCondition.getPageCondition().getSize() * (searchCondition.getPageCondition().getCurrent() - 1) + searchCondition.getIndex() + 1);
        }

        if (StringUtils.isEmpty(sort_sql)) {
            // 无排序
            searchCondition.setSort_sql("");
        } else if (searchCondition.getPageCondition().getSort().contains("-")) {
            // 倒序
            searchCondition.setSort_sql("order by " + sort_sql + " desc");
        } else {
            // 正序
            searchCondition.setSort_sql("order by " + sort_sql);
        }

        // 获取监管任务详情
        BMonitorVo monitorVo = new BMonitorVo();
        monitorVo.setId(searchCondition.getId());

        searchCondition.setNo(null);

        if (currentNo != null) {
            BMonitorVo nextCondition = (BMonitorVo) BeanUtilsSupport.copyProperties(searchCondition, BMonitorVo.class);

            nextCondition.setNo(currentNo + 1);
            nextCondition.setId(null);

            BMonitorVo next = mapper.getMonitor(nextCondition);


            if (next != null && StringUtils.isNotEmpty(sort_sql)) {
                monitorVo.setNext_id(next.getId());
            } else {
                monitorVo.setNext_id(null);
            }

        }

        return monitorVo;
    }

    @Override
    public BMonitorVo get(BMonitorVo searchCondition) {

        // 获取监管任务详情
        BMonitorVo monitorVo = mapper.selectId(searchCondition.getId());
        BMonitorInUnloadVo in = new BMonitorInUnloadVo();
        BMonitorOutDeliveryVo out = new BMonitorOutDeliveryVo();
        in.setId(monitorVo.getMonitor_in_id());
        // 获取监管入库详情
        BMonitorInUnloadVo monitorInVo = monitorInMapper.selectMonitorInUnloadByMonitorId(searchCondition.getId());
        // 设置返回页面附件对象
        if (monitorInVo != null) {
            setFile(monitorInVo);
            monitorInVo.setContainerInfos(ibContainerInfoService.selectContainerInfos(monitorInVo.getId(), monitorInVo.getType()));
            // 获取监管出库详情
            monitorVo.setMonitorInVo(monitorInVo);
        }

        // 获取监管出库详情
        BMonitorOutDeliveryVo monitorOutVo = monitorOutMapper.selectOutDeliveryByMonitorId(searchCondition.getId());
        if (monitorOutVo != null) {
            // 设置返回页面附件对象
            setFile(monitorOutVo);
            monitorOutVo.setContainerInfos(ibContainerInfoService.selectContainerInfos(monitorOutVo.getId(), monitorOutVo.getType()));
            monitorVo.setMonitorOutVo(monitorOutVo);
        }

        out.setId(monitorVo.getMonitor_out_id());

//        List <BPreviewDataVo> bPreviewDataVoList = getPreviewFiles(monitorVo);
//        monitorVo.setPreview_data(bPreviewDataVoList);

        // 获取退货单信息
        BReturnRelationVo bReturnRelationVo = bReturnRelationMapper.selectBySerialIdAndSerialTypeVO(monitorVo.getId(), SystemConstants.SERIAL_TYPE.B_MONITOR);
        if (bReturnRelationVo !=null){
            if(bReturnRelationVo.getFiles_id() != null) {
                SFileEntity file = fileMapper.selectById(bReturnRelationVo.getFiles_id());
                bReturnRelationVo.setFiles(new ArrayList<>());
                List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id",file.getId()));
                for(SFileInfoEntity fileInfo:fileInfos) {
                    SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                    fileInfoVo.setFileName(fileInfo.getFile_name());
                    bReturnRelationVo.getFiles().add(fileInfoVo);
                }
            }
            monitorVo.setReturnRelationVo(bReturnRelationVo);
        }


        return monitorVo;
    }

    @Override
    public BMonitorVo getFiles(BMonitorVo searchCondition) {
        // 获取监管任务详情
        BMonitorVo monitorVo = mapper.selectId(searchCondition.getId());

        List<BPreviewDataVo> bPreviewDataVoList = getPreviewFiles(monitorVo);
        monitorVo.setPreview_data(bPreviewDataVoList);

        return monitorVo;
    }

    private List<BPreviewDataVo> getPreviewFiles(BMonitorVo vo) {
        BMonitorFilePreviewVo result = mapper.getMonitorFile(vo);
        setPreviewMonitorFileTitle(result);
        int i = 0;
        List<BPreviewDataVo> bPreviewDataVoList = new ArrayList<>();
        if (result.getFile_1() != null && StringUtils.isNotEmpty(result.getFile_1().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_1(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_2() != null && StringUtils.isNotEmpty(result.getFile_2().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_2(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_40() != null && StringUtils.isNotEmpty(result.getFile_40().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_40(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_3() != null && StringUtils.isNotEmpty(result.getFile_3().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_3(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_4() != null && StringUtils.isNotEmpty(result.getFile_4().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_4(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_38() != null && StringUtils.isNotEmpty(result.getFile_38().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_38(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_39() != null && StringUtils.isNotEmpty(result.getFile_39().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_39(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_5() != null && StringUtils.isNotEmpty(result.getFile_5().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_5(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_6() != null && StringUtils.isNotEmpty(result.getFile_6().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_6(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_7() != null && StringUtils.isNotEmpty(result.getFile_7().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_7(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_8() != null && StringUtils.isNotEmpty(result.getFile_8().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_8(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_9() != null && StringUtils.isNotEmpty(result.getFile_9().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_9(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_10() != null && StringUtils.isNotEmpty(result.getFile_10().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_10(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_11() != null && StringUtils.isNotEmpty(result.getFile_11().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_11(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_12() != null && StringUtils.isNotEmpty(result.getFile_12().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_12(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_13() != null && StringUtils.isNotEmpty(result.getFile_13().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_13(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_14() != null && StringUtils.isNotEmpty(result.getFile_14().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_14(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_15() != null && StringUtils.isNotEmpty(result.getFile_15().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_15(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_16() != null && StringUtils.isNotEmpty(result.getFile_16().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_16(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_17() != null && StringUtils.isNotEmpty(result.getFile_17().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_17(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_18() != null && StringUtils.isNotEmpty(result.getFile_18().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_18(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_19() != null && StringUtils.isNotEmpty(result.getFile_19().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_19(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_20() != null && StringUtils.isNotEmpty(result.getFile_20().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_20(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_21() != null && StringUtils.isNotEmpty(result.getFile_21().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_21(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_22() != null && StringUtils.isNotEmpty(result.getFile_22().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_22(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_23() != null && StringUtils.isNotEmpty(result.getFile_23().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_23(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_24() != null && StringUtils.isNotEmpty(result.getFile_24().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_24(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_25() != null && StringUtils.isNotEmpty(result.getFile_25().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_25(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_26() != null && StringUtils.isNotEmpty(result.getFile_26().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_26(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_27() != null && StringUtils.isNotEmpty(result.getFile_27().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_27(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_28() != null && StringUtils.isNotEmpty(result.getFile_28().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_28(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_29() != null && StringUtils.isNotEmpty(result.getFile_29().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_29(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_30() != null && StringUtils.isNotEmpty(result.getFile_30().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_30(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_31() != null && StringUtils.isNotEmpty(result.getFile_31().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_31(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_32() != null && StringUtils.isNotEmpty(result.getFile_32().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_32(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_33() != null && StringUtils.isNotEmpty(result.getFile_33().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_33(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_34() != null && StringUtils.isNotEmpty(result.getFile_34().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_34(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_35() != null && StringUtils.isNotEmpty(result.getFile_35().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_35(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_36() != null && StringUtils.isNotEmpty(result.getFile_36().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_36(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }
        if (result.getFile_37() != null && StringUtils.isNotEmpty(result.getFile_37().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_37(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }

        if (result.getFile_41() != null && StringUtils.isNotEmpty(result.getFile_41().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_41(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }

        if (result.getFile_42() != null && StringUtils.isNotEmpty(result.getFile_42().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_42(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }

        if (result.getFile_43() != null && StringUtils.isNotEmpty(result.getFile_43().getUrl())) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(result.getFile_43(), i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }

        // 退货附件
        List<BMonitorPreviewFileVo> bMonitorPreviewFileVos = mapper.getMonitorByReturnFile(vo.getId(), SystemConstants.SERIAL_TYPE.B_MONITOR);
        for (BMonitorPreviewFileVo bMonitorPreviewFileVo : bMonitorPreviewFileVos) {
            BPreviewDataVo bPreviewDataVo = getBPreviewData(bMonitorPreviewFileVo, i);
            i++;
            bPreviewDataVoList.add(bPreviewDataVo);
        }

        return bPreviewDataVoList;
    }

    private BPreviewDataVo getBPreviewData(BMonitorPreviewFileVo vo, int i) {
        BPreviewDataVo bPreviewDataVo = new BPreviewDataVo();

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
        return bPreviewDataVo;
    }

    private void setPreviewMonitorFileTitle(BMonitorFilePreviewVo vo) {

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
            // 空车过磅-车厢情况照片
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
            //直采入库-空车出库-司机行驶证
            vo.getFile_41().setFile_title("司机行驶证");
        }

        if (StringUtils.isNotEmpty(vo.getFile_42().getUrl())) {
            //直采入库-空车出库-商品近照
            vo.getFile_42().setFile_title("商品近照");
        }

        if (StringUtils.isNotEmpty(vo.getFile_43().getUrl())) {
            //直销出库-重车出库-商品近照
            vo.getFile_42().setFile_title("商品近照");
        }
        // 空车出库 end

    }

    // 调整列表元素顺序
    private void adjustListOrder(List<BMonitorFileVo> list) {
        if (list.size() > 0) {
            BMonitorFileVo next = list.get(0);
            list.remove(0);
            list.add(next);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> cancel(List<BMonitorVo> searchCondition) {
        int updCount = 0;
        List<BMonitorEntity> list = mapper.selectIdsIn(searchCondition);

        for (int i = 0; i < list.size(); i++) {
            BMonitorEntity entity = list.get(i);

            // check
            checkLogic(entity, CheckResultAo.CANCEL_CHECK_TYPE);

            entity.setStatus(DictConstant.DICT_B_MONITOR_STATUS_EIGHT);
            updCount = mapper.updateById(entity);

            // 获取监管出库详情
            BMonitorOutDeliveryVo monitorOutVo = monitorOutMapper.selectOutDeliveryByMonitorId(entity.getId());
            // 收货监管类型为入库
            if (monitorOutVo != null) {

//                if (Objects.equals(monitorOutVo.getType(), SystemConstants.MONITOR.B_MONITOR_OUT)) {
//                    BMonitorOutEntity monitorOutEntity = monitorOutMapper.selectById(monitorOutVo.getId());
//                    if (monitorOutEntity != null) {
//                        monitorOutMapper.updateById(monitorOutEntity);
//                    }
//                } else {
//                    BMonitorDeliveryEntity monitorDeliveryEntity = monitorDeliveryMapper.selectById(monitorOutVo.getId());
//                    if (monitorDeliveryEntity != null) {
//                        monitorDeliveryMapper.updateById(monitorDeliveryEntity);
//                    }
//                }

                if (null == monitorOutVo.getQty()) {
                    monitorOutVo.setQty(BigDecimal.ZERO);
                }

            }

            // 获取监管入库详情
            BMonitorInUnloadVo monitorInVo = monitorInMapper.selectMonitorInUnloadByMonitorId(entity.getId());
            // 收货监管类型为入库
            if (monitorInVo != null) {

//                if (Objects.equals(monitorInVo.getType(), SystemConstants.MONITOR.B_MONITOR_IN)) {
//                    BMonitorInEntity monitorInEntity = monitorInMapper.selectById(monitorInVo.getId());
//                    if (monitorInEntity != null) {
//                        monitorInMapper.updateById(monitorInEntity);
//                    }
//                } else {
//                    BMonitorUnloadEntity monitorUnloadEntity = monitorUnloadMapper.selectById(monitorInVo.getId());
//                    if (monitorUnloadEntity != null) {
//                        monitorUnloadMapper.updateById(monitorUnloadEntity);
//                    }
//                }

                if (null == monitorInVo.getQty()) {
                    monitorInVo.setQty(BigDecimal.ZERO);
                }

            }

            // 更新调度单
//            BScheduleVo bScheduleVo = new BScheduleVo();
//            bScheduleVo.setId(entity.getSchedule_id());
//            BScheduleVo scheduleVo = ibScheduleService.selectByScheduleId(bScheduleVo);
//            scheduleVo.setIn_operated_qty(scheduleVo.getIn_qty());
//            scheduleVo.setOut_operated_qty(scheduleVo.getOut_qty());

            // 待发货数量，已发货数量修改
//            if (BigDecimal.ZERO.compareTo(scheduleVo.getOut_balance_qty()) > 0) {
//                scheduleVo.setOut_balance_qty(BigDecimal.ZERO);
//            } else {
//                scheduleVo.setOut_balance_qty(scheduleVo.getOut_balance());
//            }
//
//            // 待收货数量，已收货数量修改
//            if (BigDecimal.ZERO.compareTo(scheduleVo.getIn_balance_qty()) > 0) {
//                scheduleVo.setIn_balance_qty(BigDecimal.ZERO);
//            } else {
//                scheduleVo.setIn_balance_qty(scheduleVo.getIn_balance());
//            }

//            ibScheduleService.save(scheduleVo);

            // 更新调度的数量
//            bScheduleMapper.updateScheduleQty(entity.getSchedule_id());

            // 作废记录
            MCancelVo mCancelVo = new MCancelVo();
            mCancelVo.setSerial_id(entity.getId());
            mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_MONITOR);
            mCancelVo.setRemark(searchCondition.get(i).getRemark());
            mCancelService.insert(mCancelVo);

            if (updCount == 0) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
            // 更新出库单
            if (monitorOutVo != null && monitorOutVo.getOut_id() != null) {
                cancelOut(monitorOutVo.getOut_id(), searchCondition.get(i).getRemark());
            }
            // 更新入库单
            if (monitorInVo != null && monitorInVo.getIn_id() != null) {
                cancelIn(monitorInVo.getIn_id(), searchCondition.get(i).getRemark());
            }
            // 更新收货单
            if (monitorOutVo != null && monitorOutVo.getReceive_id() != null) {
                cancelReceive(monitorOutVo.getReceive_id(), searchCondition.get(i).getRemark());
            }
            // 更新提货单
            if (monitorInVo != null && monitorInVo.getDelivery_id() != null) {
                cancelDelivery(monitorInVo.getDelivery_id(), searchCondition.get(i).getRemark());
            }

//            // 更新 派车数 - 1
//            BScheduleEntity scheduleEntity = ibScheduleService.getById(entity.getSchedule_id());
//            Integer monitorCount = scheduleEntity.getMonitor_count();
//            if (monitorCount != null && monitorCount != 0) {
//                monitorCount -= 1;
//            } else {
//                monitorCount = 0;
//            }
//            // 更新 物流订单 派车数
//            LambdaUpdateWrapper<BScheduleEntity> wrapper = new LambdaUpdateWrapper<BScheduleEntity>()
//                    .eq(BScheduleEntity::getId, entity.getSchedule_id())
//                    .set(BScheduleEntity::getMonitor_count, monitorCount);
//            ibScheduleService.update(null, wrapper);
            // 更新派车数
            updateScheduleMonitorCount(entity.getSchedule_id());

            // 作废退货单
//            ibReturnRelationService.toVoidInPlan(entity.getId());
        }
        return UpdateResultUtil.OK(true);
    }

    private void cancelOut(Integer out_id, String remark) {
        if (out_id == null) {
            return;
        }
        BOutEntity entity = bOutMapper.selectById(out_id);


        List<BOutVo> bOutVoList = new ArrayList<>();
        BOutVo bOutVo = new BOutVo();
        bOutVo.setId(out_id);
        bOutVo.setOut_id(out_id);
        bOutVoList.add(bOutVo);

        callOutCanceledAppCode10Api(bOutVoList, entity.getCode());

        if (Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_STATUS_CANCEL)) {
            return;
        }

        entity.setStatus(DictConstant.DICT_B_OUT_STATUS_CANCEL);
        entity.setInventory_account_id(null);
        bOutMapper.updateById(entity);
        // 更新库存
        if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_STATUS_SAVED)) {
            iCommonInventoryLogicService.updWmsStockByOutBill(entity.getId());
        }

        // 更新出库计划中的已出库数量，待出库数量
        if(entity.getPlan_detail_id() != null && !DictConstant.DICT_B_OUT_STATUS_SAVED.equals(entity.getStatus())) {
            BOutPlanDetailEntity detail = outPlanDetailMapper.selectById(entity.getPlan_detail_id());

            BOutPlanDetailVo bOutPlanDetailVo = outPlanDetailMapper.selectPlanDetailCount(detail.getId());

            detail.setPending_count(bOutPlanDetailVo.getPending_count()); // 更新待处理数量

            detail.setPending_weight(bOutPlanDetailVo.getPending_weight()); // 更新待处理重量

            detail.setHas_handle_count(bOutPlanDetailVo.getHas_handle_count()); // 更新已处理数量

            detail.setHas_handle_weight(bOutPlanDetailVo.getHas_handle_weight()); // 更新已处理重量

            outPlanDetailMapper.updateById(detail);
        }

        // 作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(entity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_OUT);
        mCancelVo.setRemark(remark);
        mCancelService.insert(mCancelVo);

    }

    private void cancelIn(Integer in_id, String remark) {
        if (in_id == null) {
            return;
        }
        BInEntity entity = (BInEntity) ((BaseMapper)bInMapper).selectById(in_id);

        if (Objects.equals(entity.getStatus(), DictConstant.DICT_B_IN_STATUS_TWO)) {
            return;
        }

        // 查询 业务中台, 是否可以结算
        BInVo inVo = new BInVo();
        inVo.setId(in_id);
        callInCanceledAppCode10Api(List.of(inVo));

        entity.setStatus(DictConstant.DICT_B_IN_STATUS_TWO);
//        entity.setInventory_account_id(null);
        bInMapper.updateById(entity);
        // 更新库存
        if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_IN_STATUS_TWO)) {
            iCommonInventoryLogicService.updWmsStockByInBill(entity.getId());
        }

        // 查询入库计划明细，更新已处理和待处理数量
        if(entity.getPlan_detail_id() != null && !DictConstant.DICT_B_IN_STATUS_TWO.equals(entity.getStatus())) {
            BInPlanDetailEntity detail = inPlanDetailMapper.selectById(entity.getPlan_detail_id());

//            BInPlanDetailVo bInPlanDetailVo = inPlanDetailMapper.selectPlanDetailCount(detail.getId());

//            detail.setPending_count(bInPlanDetailVo.getPending_count()); // 更新待处理数量

//            detail.setPending_weight(bInPlanDetailVo.getPending_weight()); // 更新待处理重量

//            detail.setHas_handle_count(bInPlanDetailVo.getHas_handle_count()); // 更新已处理数量

//            detail.setHas_handle_weight(bInPlanDetailVo.getHas_handle_weight()); // 更新已处理重量

            inPlanDetailMapper.updateById(detail);
        }

        // 作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(entity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_IN);
        mCancelVo.setRemark(remark);
        mCancelService.insert(mCancelVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> auditIn(List<BMonitorVo> searchCondition) {
        int updCount = 0;
        List<BMonitorEntity> list = mapper.selectIdsIn(searchCondition);
        for (BMonitorEntity entity : list) {
            // check
            checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE_IN);

            // 监管任务审核页面，点击入出库审核，需判断该监管任务关联的入出库单若已作废需报提示，http://yirunscm.com:8080/issue/WMS-975
            BMonitorInUnloadVo monitorInVo = monitorInMapper.selectMonitorInUnloadByMonitorId(entity.getId());
            if (monitorInVo != null && monitorInVo.getIn_id() != null) {
                BInEntity inEntity =  (BInEntity) ((BaseMapper)bInMapper).selectById(monitorInVo.getIn_id());
                if (DictConstant.DICT_B_IN_STATUS_TWO.equals(inEntity.getStatus())) {
                    throw new BusinessException("入库单【" + inEntity.getCode() + "】已作废，无法审核");
                }
            }

            if (DictConstant.DICT_B_MONITOR_AUDIT_STATUS_THREE.equals(entity.getAudit_status())) {
                // 出库已审核->已审核
                entity.setAudit_status(DictConstant.DICT_B_MONITOR_AUDIT_STATUS_TWO);
            } else {
                // 待审核->入库已审核
                entity.setAudit_status(DictConstant.DICT_B_MONITOR_AUDIT_STATUS_FOUR);
            }

            entity.setIn_audit_id(SecurityUtil.getStaff_id());
            entity.setIn_audit_time(LocalDateTime.now());
            updCount = mapper.updateById(entity);

            if (updCount == 0) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }

        return UpdateResultUtil.OK(true);
    }

    @Override
    public UpdateResultAo<Boolean> auditOut(List<BMonitorVo> searchCondition) {
        int updCount = 0;
        List<BMonitorEntity> list = mapper.selectIdsIn(searchCondition);
        for (BMonitorEntity entity : list) {
            // check
            checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE_OUT);

            // 监管任务审核页面，点击入出库审核，需判断该监管任务关联的入出库单若已作废需报提示，http://yirunscm.com:8080/issue/WMS-975
            BMonitorOutDeliveryVo monitorOutVo = monitorOutMapper.selectOutDeliveryByMonitorId(entity.getId());
            if (monitorOutVo != null && monitorOutVo.getOut_id() != null) {
                BOutEntity outEntity = bOutMapper.selectById(monitorOutVo.getOut_id());
                if (DictConstant.DICT_B_OUT_STATUS_CANCEL.equals(outEntity.getStatus())) {
                    throw new BusinessException("出库单【" + outEntity.getCode() + "】已作废，无法审核");
                }
            }

            if (DictConstant.DICT_B_MONITOR_AUDIT_STATUS_FOUR.equals(entity.getAudit_status())) {
                // 出库已审核->已审核
                entity.setAudit_status(DictConstant.DICT_B_MONITOR_AUDIT_STATUS_TWO);
            } else {
                // 待审核->出库已审核
                entity.setAudit_status(DictConstant.DICT_B_MONITOR_AUDIT_STATUS_THREE);
            }


            entity.setOut_audit_id(SecurityUtil.getStaff_id());
            entity.setOut_audit_time(LocalDateTime.now());
            updCount = mapper.updateById(entity);

            if (updCount == 0) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }

        return UpdateResultUtil.OK(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> reject(List<BMonitorVo> searchCondition) {
        int updCount = 0;
        List<BMonitorEntity> list = mapper.selectIdsIn(searchCondition);

        for (BMonitorEntity entity : list) {
            // check
            checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE);
            entity.setAudit_status(DictConstant.DICT_B_MONITOR_AUDIT_STATUS_ONE);
            updCount = mapper.updateById(entity);

            if (updCount == 0) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }

        return UpdateResultUtil.OK(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> settlement(List<BMonitorVo> searchCondition) {
        int updCount = 0;
        List<BMonitorEntity> list = mapper.selectIdsIn(searchCondition);

        for (BMonitorEntity entity : list) {
            // check
            checkLogic(entity, CheckResultAo.SETTLE_CHECK_TYPE);
            entity.setSettlement_status(DictConstant.DICT_B_MONITOR_SETTLEMENT_STATUS_ONE);
            updCount = mapper.updateById(entity);

            if (updCount == 0) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }

        return UpdateResultUtil.OK(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMonitorDriver(BMonitorVo vo) {
        BMonitorEntity monitor = mapper.selectById(vo.getId());
        monitor.setDriver_id(vo.getDriver_id());
        monitor.setDriver_code(vo.getDriver_code());
        mapper.updateById(monitor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMonitorVehicle(BMonitorVo vo) {
        BMonitorEntity monitor = mapper.selectById(vo.getId());
        monitor.setVehicle_id(vo.getVehicle_id());
        monitor.setVehicle_code(vo.getVehicle_code());
        mapper.updateById(monitor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMonitorOutDelivery(BMonitorVo vo) {

        if (SystemConstants.MONITOR.B_MONITOR_OUT.equals(vo.getMonitorOutVo().getType())) {
            // 出库监管
            BMonitorOutEntity monitorOutEntity = monitorOutMapper.selectById(vo.getMonitorOutVo().getId());
            BMonitorEntity monitor = mapper.selectById(monitorOutEntity.getMonitor_id());

            // 校验, 如果是出库已审核, 则不能修改
            if (DictConstant.DICT_B_MONITOR_AUDIT_STATUS_THREE.equals(monitor.getAudit_status()) || DictConstant.DICT_B_MONITOR_AUDIT_STATUS_TWO.equals(monitor.getAudit_status())) {
                throw new BusinessException("监管任务已出库审核, 无法修改");
            }

            monitorOutEntity.setQty(vo.getMonitorOutVo().getQty());
            monitorOutEntity.setTare_weight(vo.getMonitorOutVo().getTare_weight());
            monitorOutEntity.setNet_weight(vo.getMonitorOutVo().getNet_weight());
            monitorOutEntity.setGross_weight(vo.getMonitorOutVo().getGross_weight());

            monitorOutMapper.updateById(monitorOutEntity);

            // 保存逻辑
            saveOut(vo.getMonitorOutVo(), monitorOutEntity, monitor);

            monitorOutMapper.updateById(monitorOutEntity);

            if (Objects.equals(Boolean.TRUE, vo.getMonitorOutVo().getIs_container()) && vo.getMonitorOutVo().getContainerInfos() != null) {
                // 若为集装箱 保存集装箱信息
                for (BContainerInfoVo containerInfoVo : vo.getMonitorOutVo().getContainerInfos()) {
                    BContainerInfoEntity containerInfoEntity = ibContainerInfoService.selectById(containerInfoVo.getId());
                    BeanUtilsSupport.copyProperties(containerInfoVo, containerInfoEntity, new String[]{"file_one", "file_two", "file_three", "file_four"});
//                    saveContainerFile(containerInfoVo);
                    containerInfoEntity.setSerial_id(monitorOutEntity.getId());
                    containerInfoEntity.setSerial_type(SystemConstants.SERIAL_TYPE.B_MONITOR_OUT);
                    ibContainerInfoService.saveOrUpdate(containerInfoEntity);
                }
            }

            monitor.setRemark("备注");
            mapper.updateById(monitor);

            // 更新调度单
//            BScheduleVo bScheduleVo = new BScheduleVo();
//            bScheduleVo.setId(monitor.getSchedule_id());
//            BScheduleVo scheduleVo = ibScheduleService.selectByScheduleId(bScheduleVo);

//            // 待发货数量，已发货数量修改
//            scheduleVo.setOut_operated_qty(scheduleVo.getOut_qty());
//            if (BigDecimal.ZERO.compareTo(scheduleVo.getOut_balance()) > 0) {
//                scheduleVo.setOut_balance_qty(BigDecimal.ZERO);
//            } else {
//                scheduleVo.setOut_balance_qty(scheduleVo.getOut_balance());
//            }
//
//            // 待收货数量，已收货数量修改
//            scheduleVo.setIn_operated_qty(scheduleVo.getIn_qty());
//            if (BigDecimal.ZERO.compareTo(scheduleVo.getIn_balance()) > 0) {
//                scheduleVo.setIn_balance_qty(BigDecimal.ZERO);
//            } else {
//                scheduleVo.setIn_balance_qty(scheduleVo.getIn_balance());
//            }

            // 物流订单，发货执行情况>=99.9%时，系统自动完成物流订单
//            SConfigEntity config = isConfigService.selectByKey(SystemConstants.KEY_LOGISTICS_MAX_OUT);
//            if (config != null && "1".equals(config.getValue()) && new BigDecimal(config.getExtra1()).compareTo(scheduleVo.getOut_operated_qty().divide(scheduleVo.getOut_schedule_qty(), 4, BigDecimal.ROUND_HALF_UP)) <= 0) {
//                scheduleVo.setStatus(DictConstant.DICT_B_SCHEDULE_STATUS_ONE);
//            }
//
//            ibScheduleService.save(scheduleVo);
//
//            // 更新调度的数量
//            bScheduleMapper.updateScheduleQty(monitor.getSchedule_id());
        }

        if (SystemConstants.MONITOR.B_MONITOR_DELIVERY.equals(vo.getMonitorOutVo().getType())) {
            // 出库监管
            BMonitorDeliveryEntity monitorDeliveryEntity = monitorDeliveryMapper.selectById(vo.getMonitorOutVo().getId());
            BMonitorEntity monitor = mapper.selectById(monitorDeliveryEntity.getMonitor_id());
            monitorDeliveryEntity.setQty(vo.getMonitorOutVo().getQty());
            monitorDeliveryEntity.setTare_weight(vo.getMonitorOutVo().getTare_weight());
            monitorDeliveryEntity.setNet_weight(vo.getMonitorOutVo().getNet_weight());
            monitorDeliveryEntity.setGross_weight(vo.getMonitorOutVo().getGross_weight());

            monitorDeliveryMapper.updateById(monitorDeliveryEntity);

            // 保存逻辑
            saveDelivery(vo.getMonitorOutVo(), monitorDeliveryEntity, monitor);

            if (Objects.equals(Boolean.TRUE, vo.getMonitorOutVo().getIs_container()) && vo.getMonitorOutVo().getContainerInfos() != null) {
                // 若为集装箱 保存集装箱信息
                for (BContainerInfoVo containerInfoVo : vo.getMonitorOutVo().getContainerInfos()) {
                    BContainerInfoEntity containerInfoEntity = ibContainerInfoService.selectById(containerInfoVo.getId());
                    BeanUtilsSupport.copyProperties(containerInfoVo, containerInfoEntity, new String[]{"file_one", "file_two", "file_three", "file_four"});
//                    saveContainerFile(containerInfoVo);
                    containerInfoEntity.setSerial_id(monitorDeliveryEntity.getId());
                    containerInfoEntity.setSerial_type(SystemConstants.SERIAL_TYPE.B_MONITOR_DELIVERY);
//                    bContainerInfoMapper.insert(containerInfoEntity);
                    ibContainerInfoService.saveOrUpdate(containerInfoEntity);
                }
            }

            mapper.updateById(monitor);

            // 更新调度单
//            BScheduleVo bScheduleVo = new BScheduleVo();
//            bScheduleVo.setId(monitor.getSchedule_id());
//            BScheduleVo scheduleVo = ibScheduleService.selectByScheduleId(bScheduleVo);

//            // 待发货数量，已发货数量修改
//            scheduleVo.setOut_operated_qty(scheduleVo.getOut_qty());
//            if (BigDecimal.ZERO.compareTo(scheduleVo.getOut_balance()) > 0) {
//                scheduleVo.setOut_balance_qty(BigDecimal.ZERO);
//            } else {
//                scheduleVo.setOut_balance_qty(scheduleVo.getOut_balance());
//            }
//
//
//            // 待收货数量，已收货数量修改
//            scheduleVo.setIn_operated_qty(scheduleVo.getIn_qty());
//            if (BigDecimal.ZERO.compareTo(scheduleVo.getIn_balance()) > 0) {
//                scheduleVo.setIn_balance_qty(BigDecimal.ZERO);
//            } else {
//                scheduleVo.setIn_balance_qty(scheduleVo.getIn_balance());
//            }

            // 物流订单，发货执行情况>=99.9%时，系统自动完成物流订单
//            SConfigEntity config = isConfigService.selectByKey(SystemConstants.KEY_LOGISTICS_MAX_OUT);
//            if (config != null && "1".equals(config.getValue()) && new BigDecimal(config.getExtra1()).compareTo(scheduleVo.getOut_operated_qty().divide(scheduleVo.getOut_schedule_qty(), 4, BigDecimal.ROUND_HALF_UP)) <= 0) {
//                scheduleVo.setStatus(DictConstant.DICT_B_SCHEDULE_STATUS_ONE);
//            }
//
//            ibScheduleService.save(scheduleVo);

            // 更新调度的数量
//            bScheduleMapper.updateScheduleQty(monitor.getSchedule_id());
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMonitorInUnload(BMonitorVo vo) {
        if (SystemConstants.MONITOR.B_MONITOR_IN.equals(vo.getMonitorInVo().getType())) {
            BMonitorInEntity monitorInEntity = monitorInMapper.selectById(vo.getMonitorInVo().getId());
            BMonitorEntity monitor = mapper.selectById(monitorInEntity.getMonitor_id());

            // 校验, 如果是入库已审核, 则不能修改
            if (DictConstant.DICT_B_MONITOR_AUDIT_STATUS_FOUR.equals(monitor.getAudit_status()) || DictConstant.DICT_B_MONITOR_AUDIT_STATUS_TWO.equals(monitor.getAudit_status())) {
                throw new BusinessException("监管任务已入库审核, 无法修改");
            }

            monitorInEntity.setQty(vo.getMonitorInVo().getQty());
            monitorInEntity.setTare_weight(vo.getMonitorInVo().getTare_weight());
            monitorInEntity.setNet_weight(vo.getMonitorInVo().getNet_weight());
            monitorInEntity.setGross_weight(vo.getMonitorInVo().getGross_weight());

//            monitorInMapper.updateById(monitorInEntity);

            // 保存逻辑
            saveIn(vo.getMonitorInVo(), monitorInEntity, monitor);

//            monitorInMapper.updateById(monitorInEntity);

            if (Objects.equals(Boolean.TRUE, vo.getMonitorInVo().getIs_container()) && vo.getMonitorInVo().getContainerInfos() != null) {
                // 若为集装箱 保存集装箱信息
                for (BContainerInfoVo containerInfoVo : vo.getMonitorInVo().getContainerInfos()) {
                    BContainerInfoEntity containerInfoEntity = ibContainerInfoService.selectById(containerInfoVo.getId());
                    BeanUtilsSupport.copyProperties(containerInfoVo, containerInfoEntity, new String[]{"file_one", "file_two", "file_three", "file_four"});
//                    saveContainerFile(containerInfoVo);
                    containerInfoEntity.setSerial_id(monitorInEntity.getId());
                    containerInfoEntity.setSerial_type(SystemConstants.SERIAL_TYPE.B_MONITOR_IN);
                    ibContainerInfoService.saveOrUpdate(containerInfoEntity);
                }
            }

            monitor.setRemark("备注");
            mapper.updateById(monitor);
            monitorInMapper.updateById(monitorInEntity);

            // 更新调度单
//            BScheduleVo bScheduleVo = new BScheduleVo();
//            bScheduleVo.setId(monitor.getSchedule_id());
//            BScheduleVo scheduleVo = ibScheduleService.selectByScheduleId(bScheduleVo);

//            // 待发货数量，已发货数量修改
//            scheduleVo.setOut_operated_qty(scheduleVo.getOut_qty());
//            if (BigDecimal.ZERO.compareTo(scheduleVo.getOut_balance()) > 0) {
//                scheduleVo.setOut_balance_qty(BigDecimal.ZERO);
//            } else {
//                scheduleVo.setOut_balance_qty(scheduleVo.getOut_balance());
//            }
//
//
//            // 待收货数量，已收货数量修改
//            scheduleVo.setIn_operated_qty(scheduleVo.getIn_qty());
//            if (BigDecimal.ZERO.compareTo(scheduleVo.getIn_balance()) > 0) {
//                scheduleVo.setIn_balance_qty(BigDecimal.ZERO);
//            } else {
//                scheduleVo.setIn_balance_qty(scheduleVo.getIn_balance());
//            }



//            ibScheduleService.save(scheduleVo);

            // 更新调度的数量
//            bScheduleMapper.updateScheduleQty(monitor.getSchedule_id());
        }

        if (SystemConstants.MONITOR.B_MONITOR_UNLOAD.equals(vo.getMonitorInVo().getType())) {
            BMonitorUnloadEntity monitorUnloadEntity = monitorUnloadMapper.selectById(vo.getMonitorInVo().getId());
            BMonitorEntity monitor = mapper.selectById(monitorUnloadEntity.getMonitor_id());
            monitorUnloadEntity.setQty(vo.getMonitorInVo().getQty());
            monitorUnloadEntity.setTare_weight(vo.getMonitorInVo().getTare_weight());
            monitorUnloadEntity.setNet_weight(vo.getMonitorInVo().getNet_weight());
            monitorUnloadEntity.setGross_weight(vo.getMonitorInVo().getGross_weight());

            monitorUnloadMapper.updateById(monitorUnloadEntity);

            // 保存逻辑
            saveUnload(vo.getMonitorInVo(), monitorUnloadEntity, monitor);

            if (Objects.equals(Boolean.TRUE, vo.getMonitorInVo().getIs_container()) && vo.getMonitorInVo().getContainerInfos() != null) {
                // 若为集装箱 保存集装箱信息
                for (BContainerInfoVo containerInfoVo : vo.getMonitorInVo().getContainerInfos()) {
                    BContainerInfoEntity containerInfoEntity = ibContainerInfoService.selectById(containerInfoVo.getId());
                    BeanUtilsSupport.copyProperties(containerInfoVo, containerInfoEntity, new String[]{"file_one", "file_two", "file_three", "file_four"});
//                    saveContainerFile(containerInfoVo);
                    containerInfoEntity.setSerial_id(monitorUnloadEntity.getId());
                    containerInfoEntity.setSerial_type(SystemConstants.SERIAL_TYPE.B_MONITOR_UNLOAD);
                    ibContainerInfoService.saveOrUpdate(containerInfoEntity);
                }
            }

            mapper.updateById(monitor);

//            // 更新调度单
//            BScheduleVo bScheduleVo = new BScheduleVo();
//            bScheduleVo.setId(monitor.getSchedule_id());
//            BScheduleVo scheduleVo = ibScheduleService.selectByScheduleId(bScheduleVo);
//
//            // 待发货数量，已发货数量修改
//            scheduleVo.setOut_operated_qty(scheduleVo.getOut_qty());
//            if (BigDecimal.ZERO.compareTo(scheduleVo.getOut_balance()) > 0) {
//                scheduleVo.setOut_balance_qty(BigDecimal.ZERO);
//            } else {
//                scheduleVo.setOut_balance_qty(scheduleVo.getOut_balance());
//            }
//
//            // 待收货数量，已收货数量修改
//            scheduleVo.setIn_operated_qty(scheduleVo.getIn_qty());
//            if (BigDecimal.ZERO.compareTo(scheduleVo.getIn_balance()) > 0) {
//                scheduleVo.setIn_balance_qty(BigDecimal.ZERO);
//            } else {
//                scheduleVo.setIn_balance_qty(scheduleVo.getIn_balance());
//            }
//
//            ibScheduleService.save(scheduleVo);

            // 更新调度的数量
//            bScheduleMapper.updateScheduleQty(monitor.getSchedule_id());
        }


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMonitor(BMonitorVo searchCondition) {
        if (SystemConstants.MONITOR.B_MONITOR_OUT.equals(searchCondition.getMonitorOutVo().getType())) {
            BMonitorOutEntity monitorOutEntity = monitorOutMapper.selectById(searchCondition.getMonitorOutVo().getId());
            BeanUtilsSupport.copyProperties(searchCondition.getMonitorOutVo(), monitorOutEntity);
            monitorOutMapper.updateById(monitorOutEntity);
        } else if (SystemConstants.MONITOR.B_MONITOR_DELIVERY.equals(searchCondition.getMonitorOutVo().getType())) {
            BMonitorDeliveryEntity monitorDeliveryEntity = monitorDeliveryMapper.selectById(searchCondition.getMonitorOutVo().getId());
            BeanUtilsSupport.copyProperties(searchCondition.getMonitorOutVo(), monitorDeliveryEntity);
            monitorDeliveryMapper.updateById(monitorDeliveryEntity);
        }

        for (BContainerInfoVo bContainerInfoVo : searchCondition.getMonitorOutVo().getContainerInfos()) {
            BContainerInfoEntity containerInfoEntity = new BContainerInfoEntity();
            BeanUtilsSupport.copyProperties(bContainerInfoVo, containerInfoEntity);
            ibContainerInfoService.updateById(containerInfoEntity);
        }

        if (searchCondition.getMonitorInVo() != null) {
            if (SystemConstants.MONITOR.B_MONITOR_IN.equals(searchCondition.getMonitorInVo().getType())) {
                BMonitorInEntity monitorInEntity = monitorInMapper.selectById(searchCondition.getMonitorInVo().getId());
                BeanUtilsSupport.copyProperties(searchCondition.getMonitorInVo(), monitorInEntity);
                monitorInMapper.updateById(monitorInEntity);
            } else if (SystemConstants.MONITOR.B_MONITOR_UNLOAD.equals(searchCondition.getMonitorInVo().getType())) {
                BMonitorUnloadEntity monitorUnloadEntity = monitorUnloadMapper.selectById(searchCondition.getMonitorInVo().getId());
                BeanUtilsSupport.copyProperties(searchCondition.getMonitorInVo(), monitorUnloadEntity);
                monitorUnloadMapper.updateById(monitorUnloadEntity);
            }

            if (searchCondition.getMonitorInVo().getContainerInfos() != null) {
                for (BContainerInfoVo bContainerInfoVo : searchCondition.getMonitorInVo().getContainerInfos()) {
                    BContainerInfoEntity containerInfoEntity = new BContainerInfoEntity();
                    BeanUtilsSupport.copyProperties(bContainerInfoVo, containerInfoEntity);
                    ibContainerInfoService.updateById(containerInfoEntity);
                }
            }
        }


    }

//    @Override
//    public void export(List<BMonitorVo> searchCondition, HttpServletResponse response) throws Exception{
//
////        SConfigEntity config = isConfigService.selectByKey(SystemConstants.EXPORT_TEMP_DIR);
////        String temp
//
//        String uuid = UuidUtil.randomUUID();
//        String fileName = LocalDateTimeUtils.formatNow(DateTimeUtil.YYYYMMDD) + ".zip";
//        String outDir = "temp" + File.separator + uuid + File.separator + fileName;
//        String path = "";
//        log.debug("----------------------导出开始------------------------------"+LocalDateTime.now());
//        List<String> srcDir = new ArrayList<>();
//        for (BMonitorVo bMonitorVo : searchCondition) {
//            log.debug("查询监管任务明细开始"+LocalDateTime.now());
//            BMonitorVo vo = this.getDetail(bMonitorVo);
//            log.debug("查询监管任务明细结束"+LocalDateTime.now());
//            List<BMonitorFileVo> list = getBMonitorFileVo(vo);
//            log.debug("下载文件开始"+LocalDateTime.now());
//            path = getSrcDir(list, uuid);
//            log.debug("下载文件结束"+LocalDateTime.now());
//            if (StringUtils.isEmpty(path)) {
//                String emptyDir = "temp" + File.separator + uuid + File.separator + LocalDateTimeUtils.formatTime(LocalDateTime.now(), DateTimeUtil.YYYYMMDD) + File.separator + vo.getVehicle_no()+"-"+ LocalDateTimeUtils.formatTime(vo.getC_time(), DateTimeUtil.YYYYMMDD) + "-" + vo.getCode();
//                File file = new File(emptyDir);
//                file.mkdirs();
//            }
//        }
//        srcDir.add(path);
//        log.debug("压缩开始"+LocalDateTime.now());
//        ZipUtil.toZip(srcDir.toArray(new String[srcDir.size()]), outDir, Boolean.TRUE);
//        log.debug("压缩结束"+LocalDateTime.now());
//        // 返回压缩包
//        log.debug("下载压缩包开始"+LocalDateTime.now());
//        CommonUtil.downloadNormal(outDir, fileName, response);
//        log.debug("下载压缩包结束"+LocalDateTime.now());
//
//        deleteDirectoryLegacyIO(new File("temp" + File.separator + uuid));
//
//        log.debug("----------------------导出结束------------------------------"+LocalDateTime.now());
//    }

//    @Override
//    public String export(List<BMonitorVo> searchCondition) throws Exception{
//
//        SConfigEntity exportUrl = isConfigService.selectByKey(SystemConstants.EXPORT_URL);
//
//        List<BMonitorFileVo> list = new ArrayList<>();
//        List<String> srcDir = new ArrayList<>();
//        for (BMonitorVo bMonitorVo : searchCondition) {
//            BMonitorVo vo = this.getDetail(bMonitorVo);
//            list.addAll(getBMonitorFileVo(vo));
//        }
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("items", list);
//        map.put("app_key", systemConfigProperies.getApp_key());
//        map.put("secret_key", systemConfigProperies.getSecret_key());
//        HttpHeaders headers = new HttpHeaders();
//
//        //postForEntity  -》 直接传递map参数
//        ResponseEntity<String> response = restTemplate.postForEntity(exportUrl.getValue(), map, String.class);
//
//        return response.getBody();
//    }

    @Override
    public List<BMonitorFileApiVo> export(List<BMonitorVo> searchCondition) throws Exception {
        List<BMonitorFileApiVo> list = new ArrayList<>();

        BMonitorFileExportSettingsEntity settingsVo = exportSettingsService.getOne(Wrappers.<BMonitorFileExportSettingsEntity>lambdaQuery()
                .eq(BMonitorFileExportSettingsEntity::getStaff_id, SecurityUtil.getStaff_id())
                .eq(BMonitorFileExportSettingsEntity::getType, DictConstant.DICT_B_MONITOR_FILE_EXPORT_SETTINGS_TYPE_ZERO));
        if (null == settingsVo) {
            throw new BusinessException("未配置导出文件");
        }
        JSONObject jsonObject = JSON.parseObject(settingsVo.getConfig_json());

        List<BMonitorFileExportVo> result = mapper.getMonitorFiles(searchCondition);
        for (BMonitorFileExportVo bMonitorFileExportVo : result) {
            list.addAll(getBMonitorFileVo(bMonitorFileExportVo, jsonObject));
        }

        return list;
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

    @Override
    public void exportAll(HttpServletResponse response) throws Exception {
        BMonitorVo vo = new BMonitorVo();
        List<BMonitorVo> list = this.selectList(vo);
        this.export(list);
    }

    @Override
    public SFileInfoVo saveMonitorFile(BMonitorFileSaveVo searchCondition) {
        BMonitorVo bMonitorVo = new BMonitorVo();
        bMonitorVo.setId(searchCondition.getMonitor_id());
//        BMonitorVo vo = this.getDetail(bMonitorVo);
//        BMonitorOutDeliveryVo monitorOutDeliveryVo = vo.getMonitorOutVo();
//        BMonitorInUnloadVo monitorInUnloadVo = vo.getMonitorInVo();

        BMonitorOutDeliveryVo monitorOutDeliveryVo = monitorOutMapper.selectOutDeliveryByMonitorId(searchCondition.getMonitor_id());
        BMonitorInUnloadVo monitorInUnloadVo = monitorInMapper.selectMonitorInUnloadByMonitorId(searchCondition.getMonitor_id());

        // 设置返回页面附件对象
        if (monitorOutDeliveryVo != null) {
            // 设置返回页面附件对象
            setFile(monitorOutDeliveryVo);
            monitorOutDeliveryVo.setContainerInfos(ibContainerInfoService.selectContainerInfos(monitorOutDeliveryVo.getId(), monitorOutDeliveryVo.getType()));
            bMonitorVo.setMonitorOutVo(monitorOutDeliveryVo);
        } else {
            bMonitorVo.setMonitorOutVo(new BMonitorOutDeliveryVo());
        }

        if (monitorInUnloadVo != null) {
            setFile(monitorInUnloadVo);
            monitorInUnloadVo.setContainerInfos(ibContainerInfoService.selectContainerInfos(monitorInUnloadVo.getId(), monitorInUnloadVo.getType()));
            // 获取监管出库详情
            bMonitorVo.setMonitorInVo(monitorInUnloadVo);
        } else {
            bMonitorVo.setMonitorInVo(new BMonitorInUnloadVo());
        }


        // 附件主表
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(monitorOutDeliveryVo.getId());
        fileEntity.setSerial_type(monitorOutDeliveryVo.getType());
        // 附件从表
        SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
        insertFile(searchCondition.getFile(), fileEntity, fileInfoEntity);

        SFileInfoVo fileInfoVo = getFileInfo(fileEntity.getId());

        switch (searchCondition.getType()) {
            case SystemConstants.MONITOR_FILE_TYPE.OUT_ONE:
                monitorOutDeliveryVo.setOne_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_TWO:
                monitorOutDeliveryVo.setTwo_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_FOURTEEN:
                monitorOutDeliveryVo.setFourteen_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_THREE:
                monitorOutDeliveryVo.setThree_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_FOUR:
                monitorOutDeliveryVo.setFour_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_TWELVE:
                monitorOutDeliveryVo.setTwelve_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_THIRTEEN:
                monitorOutDeliveryVo.setThirteen_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_FIVE:
                monitorOutDeliveryVo.setFive_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_SIX:
                monitorOutDeliveryVo.setSix_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_SEVEN:
                monitorOutDeliveryVo.setSeven_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_EIGHT:
                monitorOutDeliveryVo.setEight_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_NINE:
                monitorOutDeliveryVo.setNine_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_TEN:
                monitorOutDeliveryVo.setTen_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_ELEVEN:
                monitorOutDeliveryVo.setEleven_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_CONTAINER_ONE:
                monitorOutDeliveryVo.getContainerInfos().get(0).setFile_one(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_CONTAINER_TWO:
                monitorOutDeliveryVo.getContainerInfos().get(0).setFile_two(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_CONTAINER_THREE:
                monitorOutDeliveryVo.getContainerInfos().get(0).setFile_three(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_CONTAINER_FOUR:
                monitorOutDeliveryVo.getContainerInfos().get(0).setFile_four(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_CONTAINER_FIVE:
                monitorOutDeliveryVo.getContainerInfos().get(1).setFile_one(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_CONTAINER_SIX:
                monitorOutDeliveryVo.getContainerInfos().get(1).setFile_two(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_CONTAINER_SEVEN:
                monitorOutDeliveryVo.getContainerInfos().get(1).setFile_three(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.OUT_CONTAINER_EIGHT:
                monitorOutDeliveryVo.getContainerInfos().get(1).setFile_four(fileEntity.getId());
                break;

            case SystemConstants.MONITOR_FILE_TYPE.IN_ONE:
                monitorInUnloadVo.setOne_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.IN_TWO:
                monitorInUnloadVo.setTwo_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.IN_THREE:
                monitorInUnloadVo.setThree_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.IN_FOUR:
                monitorInUnloadVo.setFour_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.IN_FIVE:
                monitorInUnloadVo.setFive_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.IN_SIX:
                monitorInUnloadVo.setSix_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.IN_SEVEN:
                monitorInUnloadVo.setSeven_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.IN_EIGHT:
                monitorInUnloadVo.setEight_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.IN_NINE:
                monitorInUnloadVo.setNine_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.IN_TEN:
                monitorInUnloadVo.setTen_file(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.IN_CONTAINER_ONE:
                monitorInUnloadVo.getContainerInfos().get(0).setFile_one(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.IN_CONTAINER_TWO:
                monitorInUnloadVo.getContainerInfos().get(0).setFile_two(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.IN_CONTAINER_THREE:
                monitorInUnloadVo.getContainerInfos().get(0).setFile_three(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.IN_CONTAINER_FOUR:
                monitorInUnloadVo.getContainerInfos().get(0).setFile_four(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.IN_CONTAINER_FIVE:
                monitorInUnloadVo.getContainerInfos().get(1).setFile_one(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.IN_CONTAINER_SIX:
                monitorInUnloadVo.getContainerInfos().get(1).setFile_two(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.IN_CONTAINER_SEVEN:
                monitorInUnloadVo.getContainerInfos().get(1).setFile_three(fileEntity.getId());
                break;
            case SystemConstants.MONITOR_FILE_TYPE.IN_CONTAINER_EIGHT:
                monitorInUnloadVo.getContainerInfos().get(1).setFile_four(fileEntity.getId());
                break;
            default:
                break;
        }

        this.saveMonitor(bMonitorVo);

        return fileInfoVo;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshTrack(BMonitorVo vo) {
        if (vo.getTrack_end_time().compareTo(vo.getTrack_start_time()) <= 0) {
            throw new AppBusinessException("轨迹开始开始时间不能大于结束时间！");
        }

        BTrackVo bTrackVo = new BTrackVo();
        bTrackVo.setWaybill_no(vo.getCode());
        bTrackVo.setVehicle_no(vo.getVehicle_no());
        bTrackVo.setStart_time(vo.getTrack_start_time());
        bTrackVo.setEnd_time(vo.getTrack_end_time());

        SConfigEntity config = isConfigService.selectByKey(SystemConstants.TRACK_CONFIG);
        if ("1".equals(config.getValue())) {
          ibTrackGsh56Service.refreshGsh56Track(bTrackVo);
        } else if ("2".equals(config.getValue())){
            bestFriendService.refreshGsh56Track(bTrackVo);
        }

        BMonitorEntity monitor = mapper.selectById(vo.getId());
        monitor.setTrack_start_time(vo.getTrack_start_time());
        monitor.setTrack_end_time(vo.getTrack_end_time());
        mapper.updateById(monitor);

    }

    /**
     * 查询 监管 合计
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t4.in_warehouse_id,t4.out_warehouse_id")
    public BMonitorSumVo selectSum(BMonitorVo searchCondition) {
        // 判断是否是预警逻辑
        if (checkMonitorAlarmStaff(searchCondition)) {
            return new BMonitorSumVo();
        }
        return mapper.selectListSum(searchCondition);
    }

    @Override
    public BMonitorOutDeliveryVo selectByOutId(Integer id, String type) {
        return monitorOutMapper.selectOutDeliveryById(id, type);
    }

    /**
     * 损耗报表 and 在途报表
     * 1. 损耗报表： 汇总监管状态为卸货完成的监管任务单
     * 2. 在途报表： 汇总监管状态为装货完成、重车过磅、正在装货的监管任务单
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public IPage<BContractReportVo> queryQtyLossList(BContractReportVo searchCondition) {
//        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        // 分页条件
        Page<BMonitorEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.queryQtyLossList(searchCondition, pageCondition);
    }

    /**
     * 损耗报表 求和
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public BContractReportVo queryQtyLossListSum(BContractReportVo param) {
//        param.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.queryQtyLossListSum(param);
    }

    /**
     * 损耗报表 全部导出
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public List<BQtyLossReportExportVo> queryQtyLossListExportAll(BContractReportVo param) {
//        param.setStaff_id(SecurityUtil.getStaff_id());
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectLossDetailExportNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryQtyLossListExportAll(param);
    }

    /**
     * 损耗报表 部分导出
     *
     * @param param
     * @return
     */
    @Override
    public List<BQtyLossReportExportVo> queryQtyLossListExport(List<BContractReportVo> param) {
        return mapper.queryQtyLossListExport(param);
    }

    /**
     * 物流订单损耗明细
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public IPage<BQtyLossScheduleReportVo> queryScheduleList(BQtyLossScheduleReportVo searchCondition) {
//        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        // 分页条件
        Page<BQtyLossScheduleReportVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.queryScheduleList(searchCondition, pageCondition);
    }

    /**
     * 物流订单损耗明细 合计
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public BQtyLossScheduleReportVo queryScheduleListSum(BQtyLossScheduleReportVo param) {
//        param.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.queryScheduleListSum(param);
    }

    /**
     * 物流订单损耗明细 全部导出
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public List<BQtyLossScheduleDetailExportVo> queryScheduleListExportAll(BQtyLossScheduleReportVo param) {
//        param.setStaff_id(SecurityUtil.getStaff_id());
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectScheduleExportNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryScheduleListExportAll(param);
    }

    /**
     * 物流订单损耗明细 部分导出
     *
     * @param param
     * @return
     */
    @Override
    public List<BQtyLossScheduleDetailExportVo> queryScheduleListExport(List<BQtyLossScheduleReportVo> param) {
        return mapper.queryScheduleListExport(param);
    }

    /**
     * 监管任务损耗明细
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public IPage<BQtyLossScheduleReportVo> queryMonitorList(BQtyLossScheduleReportVo searchCondition) {
//        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        // 分页条件
        Page<BMonitorEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.queryMonitorList(searchCondition, pageCondition);
    }

    /**
     * 监管任务损耗明细 合计
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public BQtyLossScheduleReportVo queryMonitorListSum(BQtyLossScheduleReportVo param) {
//        param.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.queryMonitorListSum(param);
    }

    /**
     * 监管任务， 全部导出
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public List<BQtyLossMonitorDetailExportVo> queryMonitorListExportAll(BQtyLossScheduleReportVo param) {
//        param.setStaff_id(SecurityUtil.getStaff_id());
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectMonitorExportNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryMonitorListExportAll(param);
    }

    /**
     * 监管任务， 部分导出
     *
     * @param param
     * @return
     */
    @Override
    public List<BQtyLossMonitorDetailExportVo> queryMonitorListExport(List<BQtyLossScheduleReportVo> param) {
        return mapper.queryMonitorListExport(param);
    }

    /**
     * 在途报表 全部导出
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public List<BInTransitReportExportVo> queryOnWayListExportAll(BContractReportVo param) {
        param.setStaff_id(SecurityUtil.getStaff_id());
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectLossDetailExportNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryOnWayListExportAll(param);
    }

    /**
     * 在途报表 部分导出
     *
     * @param param
     * @return
     */
    @Override
    public List<BInTransitReportExportVo> queryOnWayListExport(List<BContractReportVo> param) {
        return mapper.queryOnWayListExport(param);
    }


    /**
     * 物流订单在途明细 全部导出
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public List<BScheduleLossInTransitExportVo> queryScheduleListWayExportAll(BQtyLossScheduleReportVo param) {
//        param.setStaff_id(SecurityUtil.getStaff_id());
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectScheduleExportNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryScheduleListWayExportAll(param);
    }

    /**
     * 物流订单在途明细 部分导出
     *
     * @param param
     * @return
     */
    @Override
    public List<BScheduleLossInTransitExportVo> queryScheduleListWayExport(List<BQtyLossScheduleReportVo> param) {
        return mapper.queryScheduleListWayExport(param);
    }

    /**
     * 监管任务在途明细 全部导出
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public List<BMonitorLossInTransitExportVo> queryMonitorWayListExportAll(BQtyLossScheduleReportVo param) {
//        param.setStaff_id(SecurityUtil.getStaff_id());
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectMonitorExportNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryMonitorWayListExportAll(param);
    }

    /**
     * 监管任务在途明细 部分导出
     *
     * @param param
     * @return
     */
    @Override
    public List<BMonitorLossInTransitExportVo> queryMonitorWayListExport(List<BQtyLossScheduleReportVo> param) {
        return mapper.queryMonitorWayListExport(param);
    }

    /**
     * 在途 / 损耗 汇总
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public IPage<BContractReportVo> queryQtyTotalList(BContractReportVo searchCondition) {
//        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        // 分页条件
        Page<BMonitorEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.queryQtyTotalList(searchCondition, pageCondition);
    }

    /**
     * 在途 / 损耗 汇总求和
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public BContractReportVo queryQtyTotalSumList(BContractReportVo param) {
//        param.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.queryQtyTotalSumList(param);
    }

    /**
     * 损耗报表导出 全部
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public List<BQtyLossExportVo> queryQtyLossAllExportAll(BContractReportVo param) {
        param.setStaff_id(SecurityUtil.getStaff_id());
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectLossExportNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryQtyLossAllExportAll(param);
    }

    /**
     * 损耗报表导出 部分
     *
     * @param param
     * @return
     */
    @Override
    public List<BQtyLossExportVo> queryQtyLossExport(List<BContractReportVo> param) {
        return mapper.queryQtyLossExport(param);
    }

    /**
     * 在途报表导出 全部
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public List<BQtyInTransitExportVo> queryQtyOnWayAllExportAll(BContractReportVo param) {
//        param.setStaff_id(SecurityUtil.getStaff_id());
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectLossExportNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryQtyOnWayAllExportAll(param);
    }

    /**
     * 在途报表导出 部分
     *
     * @param param
     * @return
     */
    @Override
    public List<BQtyInTransitExportVo> queryQtyOnWayExport(List<BContractReportVo> param) {
        return mapper.queryQtyOnWayExport(param);
    }

    /**
     * 当日累计调度统计
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t24.in_warehouse_id,t24.out_warehouse_id")
    public BQtyLossScheduleReportVo getScheduleStatistics(BQtyLossScheduleReportVo param) {
        param.setStaff_id(SecurityUtil.getStaff_id());
        BQtyLossScheduleReportVo scheduleStatistics = mapper.getScheduleStatistics(param);
        // 查询损耗, 时间是完成时间, 上面的时间是out_time
        BigDecimal qty_loss = mapper.getScheduleLossStatistics(param);
        scheduleStatistics.setQty_loss(qty_loss);
        return scheduleStatistics;
    }

    /**
     * 更新验车状态
     *
     * @param vo 入参
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveValidatVehicle(BMonitorVo vo) {
        BMonitorEntity monitor = mapper.selectById(vo.getId());
        monitor.setValidate_vehicle("1".equals(vo.getValidate_vehicle()));
        monitor.setValidate_time(LocalDateTime.now());
        monitor.setValidate_vehicle_type(1);
        monitor.setDriver_id(vo.getDriver_id());
        monitor.setDriver_code(vo.getDriver_code());
        monitor.setVehicle_code(vo.getVehicle_code());
        monitor.setVehicle_id(vo.getVehicle_id());
        monitor.setContainer_id(vo.getContainer_id());
//        monitor.setContainer_code(vo.getContainer_code());
        mapper.updateById(monitor);
    }

    /**
     * 校验 物流订单下的 监管任务
     *
     * @param scheduleId 物流订单ID
     */
    @Override
    public void checkMonitorStatus(Integer scheduleId) {
        List<BMonitorEntity> list = mapper.selectList(new LambdaQueryWrapper<BMonitorEntity>().eq(BMonitorEntity::getSchedule_id, scheduleId));
        // 如果list 不为空, 判断status
        if (!CollectionUtils.isEmpty(list)) {
            // 判断集合中是否有没有作废的
            boolean b = list.stream().anyMatch(item -> !Objects.equals(item.getStatus(), DictConstant.DICT_B_MONITOR_STATUS_EIGHT));
            if (b) {
                throw new BusinessException("该物流订单已被调度，无法作废，请先作废监管任务！");
            }
        }
      /*  if (StringUtils.isNotNull(entity) && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_MONITOR_STATUS_EIGHT)) {
            throw new BusinessException("该物流订单已被调度，无法作废，请先作废监管任务！");
        }*/
    }

    /**
     * 查询 列表数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t4.in_warehouse_id,t4.out_warehouse_id")
    public List<BMonitorVo> selectListByParam(BMonitorVo searchCondition) {

        // 判断是否是预警逻辑
       if (checkMonitorAlarmStaff(searchCondition)) {
           return new ArrayList<>();
       }

        String defaultSort = "";

        String sort = searchCondition.getPageCondition().getSort();
        String sortType = "DESC";
        if (StringUtils.isNotEmpty(sort)) {
            if (sort.startsWith("-")) {
                sort = sort.substring(1);
            } else {
                sortType = "ASC";
            }

            // 默认增加一个按u_time倒序
            if (!sort.contains("_time")) {
                defaultSort = ", u_time desc";
            }
        }

        return mapper.selectListByParam(searchCondition, sort, sortType, defaultSort);
    }

    /**
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t4.in_warehouse_id,t4.out_warehouse_id")
    public BMonitorVo selectCount(BMonitorVo searchCondition) {
        BMonitorVo result = new BMonitorVo();
        Long count = 0L;
        // 判断是否是预警逻辑
        if (!checkMonitorAlarmStaff(searchCondition)) {
            count = mapper.selectPageMyCount(searchCondition);
        }
        result.setCount(count);
        PageCondition pageCondition =(PageCondition) BeanUtilsSupport.copyProperties(searchCondition.getPageCondition(), PageCondition.class);
        result.setPageCondition(pageCondition);
        return result;
    }

    /**
     * 根据 箱号查询未作废的监管任务
     *
     * @param id 箱号 id
     * @return
     */
    @Override
    public List<Integer> selectActiveMonitorByContainerId(Integer id) {
        return mapper.selectActiveMonitorByContainerId(id);
    }

    /**
     * 监管任务 删除
     *
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<BMonitorVo> vo) {
        // 校验是否作废
        for (BMonitorVo bMonitorVo : vo) {
            BMonitorEntity entity = mapper.selectById(bMonitorVo.getId());
            checkLogic(entity, CheckResultAo.DELETE_CHECK_TYPE);

//            // 删除 b_monitor_out 表
//            monitorOutMapper.delete(new LambdaQueryWrapper<BMonitorOutEntity>().eq(BMonitorOutEntity::getMonitor_id, bMonitorVo.getId()));
//
//            // 删除 b_monitor_delivery 表
//            monitorDeliveryMapper.delete(new LambdaQueryWrapper<BMonitorDeliveryEntity>().eq(BMonitorDeliveryEntity::getMonitor_id, bMonitorVo.getId()));
//
//            // 删除 b_monitor_in 表
//            monitorInMapper.delete(new LambdaQueryWrapper<BMonitorInEntity>().eq(BMonitorInEntity::getMonitor_id, bMonitorVo.getId()));
//
//            // 删除 b_monitor_unload 表
//            monitorUnloadMapper.delete(new LambdaQueryWrapper<BMonitorUnloadEntity>().eq(BMonitorUnloadEntity::getMonitor_id, bMonitorVo.getId()));
//
//            // 删除 b_monitor 表
//            mapper.deleteById(bMonitorVo.getId());

            /**
             * 适配数据变更日志 修改
             */
            BMonitorOutEntity monitorOutEntity = monitorOutMapper.selectOne(new LambdaQueryWrapper<BMonitorOutEntity>().eq(BMonitorOutEntity::getMonitor_id, bMonitorVo.getId()));
            BMonitorDeliveryEntity monitorDeliveryEntity = monitorDeliveryMapper.selectOne(new LambdaQueryWrapper<BMonitorDeliveryEntity>().eq(BMonitorDeliveryEntity::getMonitor_id, bMonitorVo.getId()));
            BMonitorInEntity monitorInEntity = monitorInMapper.selectOne(new LambdaQueryWrapper<BMonitorInEntity>().eq(BMonitorInEntity::getMonitor_id, bMonitorVo.getId()));
            BMonitorUnloadEntity monitorUnloadEntity = monitorUnloadMapper.selectOne(new LambdaQueryWrapper<BMonitorUnloadEntity>().eq(BMonitorUnloadEntity::getMonitor_id, bMonitorVo.getId()));
            // 删除 b_monitor_out 表
            if (monitorOutEntity != null) {
                monitorOutMapper.deleteById(monitorOutEntity.getId());
            }

            // 删除 b_monitor_delivery 表
            if (monitorDeliveryEntity != null) {
                monitorDeliveryMapper.deleteById(monitorDeliveryEntity.getId());
            }

            // 删除 b_monitor_in 表
            if (monitorInEntity != null) {
                monitorInMapper.deleteById(monitorInEntity.getId());
            }

            // 删除 b_monitor_unload 表
            if (monitorUnloadEntity != null) {
                monitorUnloadMapper.deleteById(monitorUnloadEntity.getId());
            }

            // 删除 b_monitor 表
            mapper.deleteById(bMonitorVo.getId());

            // 更新派车数
            updateScheduleMonitorCount(entity.getSchedule_id());
        }
    }

    /**
     * 更新 派车数
     * @param schedule_id 监管任务id
     */
    private void updateScheduleMonitorCount(Integer schedule_id) {
        // 更新监管任务数量
        List<Integer> monitorIds = mapper.selectNotCancelMonitorByScheduleId(schedule_id);
        int count = 0;
        if (!CollectionUtils.isEmpty(monitorIds)) {
            count = monitorIds.size();
        }
        bScheduleMapper.update(new LambdaUpdateWrapper<BScheduleEntity>().eq(BScheduleEntity::getId, schedule_id)
                .set(BScheduleEntity::getMonitor_count,count));
    }

    /**
     * 查询可以同步的监管任务
     *
     * @param searchConditionList
     * @return
     */
    @Override
    public List<BMonitorVo> selectSyncData(List<BMonitorVo> searchConditionList) {
        return mapper.selectSyncData(searchConditionList);
    }

    /**
     * 根据 codeList 查询
     *
     * @param searchConditionList
     * @return
     */
    @Override
    public List<BMonitorVo> selectListByCodeList(List<String> searchConditionList) {
        return mapper.selectListByCodeList(searchConditionList);
    }

    /**
     * 查询 验车 和 规格 日志
     *
     * @param id
     * @return
     */
    @Override
    public BMonitorVo selectValidateAndTrackLog(Integer id) {
        return mapper.selectValidateAndTrackLog(id);
    }

    /**
     * 查询入库单 id
     *
     * @param id
     * @return
     */
    @Override
    public BMonitorVo selectInByMonitorId(Integer id) {
        return mapper.selectInByMonitorId(id);
    }

    /**
     * 执行状态回滚待审核
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> statusRollback(List<BMonitorVo> searchConditionList) {
        List<BMonitorEntity> list = mapper.selectIdsIn(searchConditionList);

        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException("未找到该参数");
        }

        if (searchConditionList.size() != list.size()) {
            throw new BusinessException("勾选参数不一致");
        }

        for (BMonitorEntity bMonitorEntity : list) {
            if (bMonitorEntity.getAudit_status().equals(DictConstant.DICT_B_MONITOR_AUDIT_STATUS_ZERO)) {
                throw new BusinessException("监管任务待审核无法回滚");
            }

            bMonitorEntity.setAudit_status(DictConstant.DICT_B_MONITOR_AUDIT_STATUS_ZERO);
            bMonitorEntity.setSettlement_status(DictConstant.DICT_B_MONITOR_SETTLEMENT_STATUS_ZERO);
            int i = mapper.updateById(bMonitorEntity);
            if (i == 0) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }

        return UpdateResultUtil.OK(true);
    }

    /**
     * 查询监管任务详情
     *
     * @param id 监管任务 ID
     * @return BMonitorSyncVo
     */
    @Override
    public ApiMonitorVo selectMonitor2Sync(Integer id) {
        ApiMonitorVo apiMonitorVo = mapper.selectMonitor2Sync(id);
        if (null != apiMonitorVo) {

            // 监管任务同步计算退货数量
            BReturnRelationEntity returnRelationEntity = bReturnRelationMapper.selectBySerialIdAndSerialType(id, SystemConstants.SERIAL_TYPE.B_MONITOR);
            if (returnRelationEntity != null) {
                apiMonitorVo.setSendCount(apiMonitorVo.getSendCount().subtract(returnRelationEntity.getQty()));
                apiMonitorVo.setLossCount(apiMonitorVo.getLossCount().subtract(returnRelationEntity.getQty()));
            }

            ApiMonitorItemVo apiMonitorItemVo = new ApiMonitorItemVo();
            apiMonitorItemVo.setGoodsName(apiMonitorVo.getGoodsName());
            apiMonitorItemVo.setGoodsCode(apiMonitorVo.getGoodsCode());
            apiMonitorVo.setItems(Lists.newArrayList(apiMonitorItemVo));
            // 中林 和 青润 不一样
//            if (RuntimeEnvUtil.getEnv().equals(RuntimeEnvUtil.ENV.QINGRUN)) {
//                apiMonitorVo.setType(2);
//                apiMonitorVo.setTypeName("托运");
//            } else if (RuntimeEnvUtil.getEnv().equals(RuntimeEnvUtil.ENV.ZHONGLIN) || RuntimeEnvUtil.getEnv().equals(RuntimeEnvUtil.ENV.YS)) {
//                apiMonitorVo.setType(1);
//                apiMonitorVo.setTypeName("承运");
//            } else if (RuntimeEnvUtil.getEnv().equals(RuntimeEnvUtil.ENV.DEV)) {
//                apiMonitorVo.setType(2);
//                apiMonitorVo.setTypeName("托运");
//            }
        }
        return apiMonitorVo;
    }

    /**
     * 全部同步, 只同步同步失败的和未同步的
     *
     * @return
     */
//    @Override
//    public List<BMonitorVo> selectAll2Sync() {
//        return mapper.selectAll2Sync();
//    }

    private String getSrcDir(List<BMonitorFileApiVo> list, String uuid) throws Exception {
        // 文件临时保存目录
        String path = "temp" + File.separator + uuid + File.separator + LocalDateTimeUtils.formatTime(LocalDateTime.now(), DateTimeUtil.YYYYMMDD);
        String src = "";
        try {
            for (BMonitorFileApiVo vo : list) {
                // 获取文件
                byte[] bytes = NetUtil.downloadBytes(vo.getUrl().toString());
                String pathStr = path + File.separator + vo.getDirName() + File.separator + vo.getFileName();
                FileUtils.writeByteArrayToFile(new File(pathStr), bytes);
                src = path;
            }

        } catch (Exception e) {
            log.error("getSrcDir error", e);
        }
        return src;
    }

//    private List<BMonitorFileVo> getBMonitorFileVo(BMonitorVo vo) {
//        List<BMonitorFileVo> list = new ArrayList<>();
//
//        BMonitorFileVo bMonitorFileVo;
//        String dirName = "/" + LocalDateTimeUtils.formatTime(LocalDateTime.now(), DateTimeUtil.YYYYMMDD) + "/" + vo.getVehicle_no()+"-"+ LocalDateTimeUtils.formatTime(vo.getC_time(), DateTimeUtil.YYYYMMDD) + "-" + vo.getCode();
//
//        // 空车过磅 start
//        if (vo.getMonitorOutVo() != null && vo.getMonitorOutVo().getOne_file() != null) {
//            // 空车过磅-司机车头照片
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorOutVo().getOne_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("空车过磅-司机车头照片"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//
//        if (vo.getMonitorOutVo() != null && vo.getMonitorOutVo().getTwo_file() != null) {
//            // 空车过磅-司机车尾照片
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorOutVo().getTwo_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("空车过磅-司机车尾照片"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//
//        if (vo.getMonitorOutVo() != null && vo.getMonitorOutVo().getThree_file() != null) {
//            // 空车过磅-司机承诺书
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorOutVo().getThree_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("空车过磅-司机承诺书"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//
//        if (vo.getMonitorOutVo() != null && vo.getMonitorOutVo().getFour_file() != null) {
//            // 空车过磅-三证拍照
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorOutVo().getFour_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("空车过磅-三证拍照"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//        // 空车过磅 end
//
//
//        // 正在装货 start
//        if (vo.getMonitorOutVo() != null && vo.getMonitorOutVo().getFive_file() != null) {
//            // 正在装货-司机车头照片
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorOutVo().getFive_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("正在装货-司机车头照片"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//
//        if (vo.getMonitorOutVo() != null && vo.getMonitorOutVo().getSix_file() != null) {
//            // 正在装货-司机车尾照片
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorOutVo().getSix_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("正在装货-司机车尾照片"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//
//        if (vo.getMonitorOutVo() != null && vo.getMonitorOutVo().getSeven_file() != null) {
//            // 正在装货-车侧身照片
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorOutVo().getSeven_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("正在装货-车侧身照片"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//
//        if (vo.getMonitorOutVo() != null && vo.getMonitorOutVo().getEight_file() != null) {
//            // 正在装货-装货视频
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorOutVo().getEight_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("正在装货-装货视频"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//        // 正在装货 end
//
//        // 重车出库 start
//        if (vo.getMonitorOutVo() != null && vo.getMonitorOutVo().getNine_file() != null) {
//            // 重车出库-司机车头照片
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorOutVo().getNine_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("重车出库-司机车头照片"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//
//        if (vo.getMonitorOutVo() != null && vo.getMonitorOutVo().getTen_file() != null) {
//            // 重车出库-司机车头照片
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorOutVo().getTen_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("重车出库-司机车尾照片"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//
//        if (vo.getMonitorOutVo() != null && vo.getMonitorOutVo().getEleven_file() != null) {
//            // 重车出库-磅单
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorOutVo().getEleven_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("重车出库-磅单"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//        // 重车出库 end
//
//        // 发货集装箱 start
//        if (vo.getMonitorOutVo() != null && vo.getMonitorOutVo().getContainerInfos() != null) {
//            for (int i = 1; i <= vo.getMonitorOutVo().getContainerInfos().size(); i++) {
//                BContainerInfoVo bContainerInfoVo = vo.getMonitorOutVo().getContainerInfos().get(i-1);
//                // 正在装货-集装箱箱号照片
//                if (bContainerInfoVo.getFile_one() != null) {
//                    bMonitorFileVo = new BMonitorFileVo();
//                    bMonitorFileVo.setDirName(dirName);
//                    bMonitorFileVo.setUrl(getPath(bContainerInfoVo.getFile_oneVo().getUrl().replaceAll("\\\\","/")));
//                    bMonitorFileVo.setFileName("正在装货-集装箱箱号照片"+i+lastName(bContainerInfoVo.getFile_oneVo().getUrl()));
//                    list.add(bMonitorFileVo);
//                }
//
//                if (bContainerInfoVo.getFile_twoVo() != null) {
//                    // 正在装货-集装箱箱号照片
//                    bMonitorFileVo = new BMonitorFileVo();
//                    bMonitorFileVo.setDirName(dirName);
//                    bMonitorFileVo.setUrl(getPath(bContainerInfoVo.getFile_twoVo().getUrl().replaceAll("\\\\","/")));
//                    bMonitorFileVo.setFileName("正在装货-集装箱内部空箱照片"+i+lastName(bContainerInfoVo.getFile_twoVo().getUrl()));
//                    list.add(bMonitorFileVo);
//                }
//
//                if (bContainerInfoVo.getFile_threeVo() != null) {
//                    // 正在装货-集装箱箱号照片
//                    bMonitorFileVo = new BMonitorFileVo();
//                    bMonitorFileVo.setDirName(dirName);
//                    bMonitorFileVo.setUrl(getPath(bContainerInfoVo.getFile_threeVo().getUrl().replaceAll("\\\\","/")));
//                    bMonitorFileVo.setFileName("正在装货-集装箱装货视频"+i+lastName(bContainerInfoVo.getFile_threeVo().getUrl()));
//                    list.add(bMonitorFileVo);
//                }
//
//                if (bContainerInfoVo.getFile_fourVo() != null) {
//                    // 正在装货-集装箱箱号照片
//                    bMonitorFileVo = new BMonitorFileVo();
//                    bMonitorFileVo.setDirName(dirName);
//                    bMonitorFileVo.setUrl(getPath(bContainerInfoVo.getFile_fourVo().getUrl().replaceAll("\\\\","/")));
//                    bMonitorFileVo.setFileName("正在装货-磅单"+i+"(司机签字)"+lastName(bContainerInfoVo.getFile_fourVo().getUrl()));
//                    list.add(bMonitorFileVo);
//                }
//            }
//
//        }
//        // 发货集装箱 end
//
//
//        // 重车过磅 start
//        if (vo.getMonitorInVo() != null && vo.getMonitorInVo().getOne_file() != null) {
//            // 重车出库-司机车头照片
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorInVo().getOne_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("重车过磅-司机车头照片"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//
//        if (vo.getMonitorInVo() != null && vo.getMonitorInVo().getTwo_file() != null) {
//            // 重车出库-司机车尾照片
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorInVo().getTwo_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("重车过磅-司机车尾照片"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//
//        if (vo.getMonitorInVo() != null && vo.getMonitorInVo().getTen_file() != null) {
//            // 重车出库-司机车尾照片
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorInVo().getTen_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("重车过磅-行车轨迹"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//        // 重车过磅 end
//
//
//        // 正在卸货 start
//        if (vo.getMonitorInVo() != null && vo.getMonitorInVo().getThree_file() != null) {
//            // 正在卸货-司机车头照片
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorInVo().getThree_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("正在卸货-司机车头照片"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//
//        if (vo.getMonitorInVo() != null && vo.getMonitorInVo().getFour_file() != null) {
//            // 正在卸货-司机车尾照片
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorInVo().getFour_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("正在卸货-司机车尾照片"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//
//
//        if (vo.getMonitorInVo() != null && vo.getMonitorInVo().getFive_file() != null) {
//            // 正在卸货-车侧身照片
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorInVo().getFive_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("正在卸货-车侧身照片"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//
//        if (vo.getMonitorInVo() != null && vo.getMonitorInVo().getSix_file() != null) {
//            // 正在卸货-卸货视频
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorInVo().getSix_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("正在卸货-卸货视频"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//        // 正在卸货 end
//
//        // 空车出库 start
//        if (vo.getMonitorInVo() != null && vo.getMonitorInVo().getSeven_file() != null) {
//            // 正在装货-车侧身照片
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorInVo().getSeven_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("空车出库-司机车头照片"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//
//        if (vo.getMonitorInVo() != null && vo.getMonitorInVo().getEight_file() != null) {
//            // 正在装货-装货视频
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorInVo().getEight_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("空车出库-司机车尾照片"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//
//        if (vo.getMonitorInVo() != null && vo.getMonitorInVo().getNine_file() != null) {
//            // 重车出库-司机车头照片
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getMonitorInVo().getNine_fileVo().getUrl().replaceAll("\\\\","/")));
//            bMonitorFileVo.setFileName("空车出库-磅单"+lastName(bMonitorFileVo.getUrl()));
//            list.add(bMonitorFileVo);
//        }
//        // 空车出库 end
//
//        // 发货集装箱 start
//        if (vo.getMonitorInVo() != null && vo.getMonitorInVo().getContainerInfos() != null) {
//            for (int i = 1; i <= vo.getMonitorInVo().getContainerInfos().size(); i++) {
//                BContainerInfoVo bContainerInfoVo = vo.getMonitorInVo().getContainerInfos().get(i-1);
//                // 正在装货-集装箱箱号照片
//                if (bContainerInfoVo.getFile_one() != null) {
//                    bMonitorFileVo = new BMonitorFileVo();
//                    bMonitorFileVo.setDirName(dirName);
//                    bMonitorFileVo.setUrl(getPath(bContainerInfoVo.getFile_oneVo().getUrl().replaceAll("\\\\","/")));
//                    bMonitorFileVo.setFileName("正在卸货-集装箱箱号照片"+i+lastName(bContainerInfoVo.getFile_oneVo().getUrl()));
//                    list.add(bMonitorFileVo);
//                }
//
//                // 正在装货-集装箱箱号照片
//                if (bContainerInfoVo.getFile_twoVo() != null) {
//                    bMonitorFileVo = new BMonitorFileVo();
//                    bMonitorFileVo.setDirName(dirName);
//                    bMonitorFileVo.setUrl(getPath(bContainerInfoVo.getFile_twoVo().getUrl().replaceAll("\\\\","/")));
//                    bMonitorFileVo.setFileName("正在卸货-集装箱内部空箱照片"+i+lastName(bContainerInfoVo.getFile_twoVo().getUrl()));
//                    list.add(bMonitorFileVo);
//                }
//
//                // 正在装货-集装箱箱号照片
//                if (bContainerInfoVo.getFile_threeVo() != null) {
//                    bMonitorFileVo = new BMonitorFileVo();
//                    bMonitorFileVo.setDirName(dirName);
//                    bMonitorFileVo.setUrl(getPath(bContainerInfoVo.getFile_threeVo().getUrl().replaceAll("\\\\","/")));
//                    bMonitorFileVo.setFileName("正在卸货-集装箱卸货视频"+i+lastName(bContainerInfoVo.getFile_threeVo().getUrl()));
//                    list.add(bMonitorFileVo);
//                }
//
//                // 正在装货-集装箱箱号照片
//                if (bContainerInfoVo.getFile_fourVo() != null) {
//                    bMonitorFileVo = new BMonitorFileVo();
//                    bMonitorFileVo.setDirName(dirName);
//                    bMonitorFileVo.setUrl(getPath(bContainerInfoVo.getFile_fourVo().getUrl().replaceAll("\\\\","/")));
//                    bMonitorFileVo.setFileName("正在卸货-磅单"+i+"(司机签字)"+lastName(bContainerInfoVo.getFile_fourVo().getUrl()));
//                    list.add(bMonitorFileVo);
//                }
//            }
//
//        }
//        // 发货集装箱 end
//
//        if (list.size() == 0) {
//            bMonitorFileVo = new BMonitorFileVo();
//            bMonitorFileVo.setDirName(dirName);
//            list.add(bMonitorFileVo);
//        }
//
//        return list;
//    }

    private List<BMonitorFileApiVo> getBMonitorFileVo(BMonitorFileExportVo vo, JSONObject jsonObject) {
        List<BMonitorFileApiVo> list = new ArrayList<>();

        BMonitorFileApiVo bMonitorFileVo;
        String dirName = "/" + LocalDateTimeUtils.formatTime(LocalDateTime.now(), DateTimeUtil.YYYYMMDD) + "/" + vo.getNo() + "-" + LocalDateTimeUtils.formatTime(vo.getC_time(), DateTimeUtil.YYYYMMDD) + "-" + vo.getCode();

        // 空车过磅 start
        if (StringUtils.isNotEmpty(vo.getFile_1().getUrl()) && jsonObject.getJSONObject("file_1") != null && jsonObject.getJSONObject("file_1").getBoolean("value")) {
            // 空车过磅-司机车头照片
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_1().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_1().getUrl());
            bMonitorFileVo.setFileName("空车过磅-司机车头照片" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_2().getUrl()) && jsonObject.getJSONObject("file_2") != null && jsonObject.getJSONObject("file_2").getBoolean("value")) {
            // 空车过磅-司机车尾照片
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_2().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_2().getUrl());
            bMonitorFileVo.setFileName("空车过磅-司机车尾照片" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_40().getUrl()) && jsonObject.getJSONObject("file_40") != null && jsonObject.getJSONObject("file_40").getBoolean("value")) {
            // 空车过磅-车厢情况照片
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_2().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_40().getUrl());
            bMonitorFileVo.setFileName("空车过磅-车厢情况照片" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_3().getUrl()) && jsonObject.getJSONObject("file_3") != null && jsonObject.getJSONObject("file_3").getBoolean("value")) {
            // 空车过磅-司机承诺书
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_3().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_3().getUrl());
            bMonitorFileVo.setFileName("空车过磅-司机承诺书" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_4().getUrl()) && jsonObject.getJSONObject("file_4") != null && jsonObject.getJSONObject("file_4").getBoolean("value")) {
            // 空车过磅-司机身份证
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_4().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_4().getUrl());
            bMonitorFileVo.setFileName("空车过磅-司机身份证" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_38().getUrl()) && jsonObject.getJSONObject("file_38") != null && jsonObject.getJSONObject("file_38").getBoolean("value")) {
            // 空车过磅-司机驾驶证
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
            bMonitorFileVo.setUrl(vo.getFile_38().getUrl());
            bMonitorFileVo.setFileName("空车过磅-司机驾驶证" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_39().getUrl()) && jsonObject.getJSONObject("file_39") != null && jsonObject.getJSONObject("file_39").getBoolean("value")) {
            // 空车过磅-车辆行驶证
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
            bMonitorFileVo.setUrl(vo.getFile_39().getUrl());
            bMonitorFileVo.setFileName("空车过磅-车辆行驶证" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }
        // 空车过磅 end

        // 正在装货 start
        if (StringUtils.isNotEmpty(vo.getFile_5().getUrl()) && jsonObject.getJSONObject("file_5") != null &&jsonObject.getJSONObject("file_5").getBoolean("value")) {
            // 正在装货-司机车头照片
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_5().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_5().getUrl());
            bMonitorFileVo.setFileName("正在装货-司机车头照片" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_6().getUrl()) && jsonObject.getJSONObject("file_6") != null && jsonObject.getJSONObject("file_6").getBoolean("value")) {
            // 正在装货-司机车尾照片
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_6().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_6().getUrl());
            bMonitorFileVo.setFileName("正在装货-司机车尾照片" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_7().getUrl()) && jsonObject.getJSONObject("file_7") != null && jsonObject.getJSONObject("file_7").getBoolean("value")) {
            // 正在装货-车侧身照片
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_7().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_7().getUrl());
            bMonitorFileVo.setFileName("正在装货-车侧身照片" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_8().getUrl()) && jsonObject.getJSONObject("file_8") != null && jsonObject.getJSONObject("file_8").getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_8().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_8().getUrl());
            bMonitorFileVo.setFileName("正在装货-装货视频" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }
        // 正在装货 end

        // 发货集装箱 start
        if (StringUtils.isNotEmpty(vo.getFile_9().getUrl()) && jsonObject.getJSONObject("file_9") != null && jsonObject.getJSONObject("file_9") .getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_9().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_9().getUrl());
            bMonitorFileVo.setFileName("正在装货-集装箱箱号照片1" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_10().getUrl()) && jsonObject.getJSONObject("file_10") != null && jsonObject.getJSONObject("file_10").getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_10().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_10().getUrl());
            bMonitorFileVo.setFileName("正在装货-集装箱内部空箱照片1" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_11().getUrl()) && jsonObject.getJSONObject("file_11") != null && jsonObject.getJSONObject("file_11").getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_11().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_11().getUrl());
            bMonitorFileVo.setFileName("正在装货-集装箱装货视频1" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_12().getUrl()) && jsonObject.getJSONObject("file_12") != null && jsonObject.getJSONObject("file_12").getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_12().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_12().getUrl());
            bMonitorFileVo.setFileName("正在装货-磅单1(司机签字)" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_13().getUrl()) && jsonObject.getJSONObject("file_13") != null && jsonObject.getJSONObject("file_13").getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_13().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_13().getUrl());
            bMonitorFileVo.setFileName("正在装货-集装箱箱号照片2" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_14().getUrl()) && jsonObject.getJSONObject("file_14") != null && jsonObject.getJSONObject("file_14").getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_14().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_14().getUrl());
            bMonitorFileVo.setFileName("正在装货-集装箱内部空箱照片2" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_15().getUrl()) && jsonObject.getJSONObject("file_15") != null && jsonObject.getJSONObject("file_15").getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_15().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_15().getUrl());
            bMonitorFileVo.setFileName("正在装货-集装箱装货视频2" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_16().getUrl()) && jsonObject.getJSONObject("file_16") != null && jsonObject.getJSONObject("file_16").getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_16().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_16().getUrl());
            bMonitorFileVo.setFileName("正在装货-磅单2(司机签字)" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }
        // 发货集装箱 end


        // 重车出库 start
        if (StringUtils.isNotEmpty(vo.getFile_17().getUrl()) && jsonObject.getJSONObject("file_17") != null && jsonObject.getJSONObject("file_17").getBoolean("value")) {
            // 重车出库-司机车头照片
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_17().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_17().getUrl());
            bMonitorFileVo.setFileName("重车出库-司机车头照片" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_18().getUrl()) && jsonObject.getJSONObject("file_18") != null && jsonObject.getJSONObject("file_18").getBoolean("value")) {
            // 重车出库-司机车头照片
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_18().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_18().getUrl());
            bMonitorFileVo.setFileName("重车出库-司机车尾照片" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_19().getUrl()) && jsonObject.getJSONObject("file_19") != null && jsonObject.getJSONObject("file_19").getBoolean("value")) {
            // 重车出库-磅单
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_19().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_19().getUrl());
            bMonitorFileVo.setFileName("重车出库-磅单" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }
        // 重车出库 end

        // 重车过磅 start
        if (StringUtils.isNotEmpty(vo.getFile_20().getUrl()) && jsonObject.getJSONObject("file_20") != null && jsonObject.getJSONObject("file_20").getBoolean("value")) {
            // 重车出库-司机车头照片
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_20().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_20().getUrl());
            bMonitorFileVo.setFileName("重车过磅-司机车头照片" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_21().getUrl()) && jsonObject.getJSONObject("file_21") != null && jsonObject.getJSONObject("file_21").getBoolean("value")) {
            // 重车出库-司机车尾照片
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_21().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_21().getUrl());
            bMonitorFileVo.setFileName("重车过磅-司机车尾照片" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_22().getUrl()) && jsonObject.getJSONObject("file_21") != null && jsonObject.getJSONObject("file_22").getBoolean("value")) {
            // 重车出库-司机车尾照片
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_22().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_22().getUrl());
            bMonitorFileVo.setFileName("重车过磅-行车轨迹" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }
        // 重车过磅 end


        // 正在卸货 start
        if (StringUtils.isNotEmpty(vo.getFile_23().getUrl()) && jsonObject.getJSONObject("file_23") != null && jsonObject.getJSONObject("file_23") .getBoolean("value")) {
            // 正在卸货-司机车头照片
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_23().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_23().getUrl());
            bMonitorFileVo.setFileName("正在卸货-司机车头照片" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_24().getUrl()) && jsonObject.getJSONObject("file_24")  != null && jsonObject.getJSONObject("file_24").getBoolean("value")) {
            // 正在卸货-司机车尾照片
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_24().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_24().getUrl());
            bMonitorFileVo.setFileName("正在卸货-司机车尾照片" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }


        if (StringUtils.isNotEmpty(vo.getFile_25().getUrl()) && jsonObject.getJSONObject("file_25") != null && jsonObject.getJSONObject("file_25").getBoolean("value")) {
            // 正在卸货-车侧身照片
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_25().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_25().getUrl());
            bMonitorFileVo.setFileName("正在卸货-车侧身照片" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_26().getUrl()) && jsonObject.getJSONObject("file_26") != null && jsonObject.getJSONObject("file_26").getBoolean("value")) {
            // 正在卸货-卸货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_26().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_26().getUrl());
            bMonitorFileVo.setFileName("正在卸货-卸货视频" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }
        // 正在卸货 end

        // 收货集装箱 start
        if (StringUtils.isNotEmpty(vo.getFile_27().getUrl()) && jsonObject.getJSONObject("file_27") != null && jsonObject.getJSONObject("file_27") .getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_27().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_27().getUrl());
            bMonitorFileVo.setFileName("正在卸货-集装箱箱号照片1" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_28().getUrl()) && jsonObject.getJSONObject("file_28") != null && jsonObject.getJSONObject("file_28").getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_28().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_28().getUrl());
            bMonitorFileVo.setFileName("正在卸货-集装箱内部空箱照片1" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_29().getUrl()) && jsonObject.getJSONObject("file_29") != null && jsonObject.getJSONObject("file_29").getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_29().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_29().getUrl());
            bMonitorFileVo.setFileName("正在卸货-集装箱卸货视频1" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_30().getUrl()) && jsonObject.getJSONObject("file_30") != null && jsonObject.getJSONObject("file_30").getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_30().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_30().getUrl());
            bMonitorFileVo.setFileName("正在卸货-磅单1(司机签字)" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_31().getUrl()) && jsonObject.getJSONObject("file_31") != null && jsonObject.getJSONObject("file_31").getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_31().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_31().getUrl());
            bMonitorFileVo.setFileName("正在卸货-集装箱箱号照片2" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_32().getUrl()) && jsonObject.getJSONObject("file_32") != null && jsonObject.getJSONObject("file_32").getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_32().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_32().getUrl());
            bMonitorFileVo.setFileName("正在卸货-集装箱内部空箱照片2" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_33().getUrl()) && jsonObject.getJSONObject("file_33") != null && jsonObject.getJSONObject("file_33").getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_33().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_33().getUrl());
            bMonitorFileVo.setFileName("正在卸货-集装箱卸货视频2" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_34().getUrl()) && jsonObject.getJSONObject("file_34") != null && jsonObject.getJSONObject("file_34").getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_34().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_34().getUrl());
            bMonitorFileVo.setFileName("正在卸货-磅单2(司机签字)" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }
        // 收货集装箱 end

        // 空车出库 start
        if (StringUtils.isNotEmpty(vo.getFile_35().getUrl()) && jsonObject.getJSONObject("file_35") != null && jsonObject.getJSONObject("file_35").getBoolean("value")) {
            // 正在装货-车侧身照片
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_35().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_35().getUrl());
            bMonitorFileVo.setFileName("空车出库-司机车头照片" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_36().getUrl()) && jsonObject.getJSONObject("file_36") != null && jsonObject.getJSONObject("file_36").getBoolean("value")) {
            // 正在装货-装货视频
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_36().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_36().getUrl());
            bMonitorFileVo.setFileName("空车出库-司机车尾照片" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        if (StringUtils.isNotEmpty(vo.getFile_37().getUrl()) && jsonObject.getJSONObject("file_37") != null && jsonObject.getJSONObject("file_37").getBoolean("value")) {
            // 重车出库-司机车头照片
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_37().getUrl().replaceAll("\\\\","/")));
            bMonitorFileVo.setUrl(vo.getFile_37().getUrl());
            bMonitorFileVo.setFileName("空车出库-磅单" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }
        // 空车出库 end


        if (list.size() == 0) {
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
            list.add(bMonitorFileVo);
        }

        return list;
    }

    // 获取后缀名
    private String lastName(String fileName) {
        if (fileName.lastIndexOf(".") == -1) {
            return "";//文件没有后缀名的情况
        }
        //此时返回的是带有 . 的后缀名，
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 设置返回页面入库监管附件对象
     */
    public void setFile(BMonitorInUnloadVo vo) {
        SFileInfoVo fileInfoVo;
        // 车头车尾带司机附件
        if (vo.getOne_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getOne_file());
            vo.setOne_fileVo(fileInfoVo);
        } else {
            vo.setOne_fileVo(new SFileInfoVo());
        }
        // 重车过磅附件
        if (vo.getTwo_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getTwo_file());
            vo.setTwo_fileVo(fileInfoVo);
        } else {
            vo.setTwo_fileVo(new SFileInfoVo());
        }
        // 卸货照片附件
        if (vo.getThree_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getThree_file());
            vo.setThree_fileVo(fileInfoVo);
        } else {
            vo.setThree_fileVo(new SFileInfoVo());
        }
        // 卸货视频附件
        if (vo.getFour_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getFour_file());
            vo.setFour_fileVo(fileInfoVo);
        } else {
            vo.setFour_fileVo(new SFileInfoVo());
        }
        // 车头车尾带司机附件
        if (vo.getFive_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getFive_file());
            vo.setFive_fileVo(fileInfoVo);
        } else {
            vo.setFive_fileVo(new SFileInfoVo());
        }
        // 磅单(司机签字)附件
        if (vo.getSix_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getSix_file());
            vo.setSix_fileVo(fileInfoVo);
        } else {
            vo.setSix_fileVo(new SFileInfoVo());
        }
        if (vo.getSeven_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getSeven_file());
            vo.setSeven_fileVo(fileInfoVo);
        } else {
            vo.setSeven_fileVo(new SFileInfoVo());
        }
        if (vo.getEight_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getEight_file());
            vo.setEight_fileVo(fileInfoVo);
        } else {
            vo.setEight_fileVo(new SFileInfoVo());
        }
        if (vo.getNine_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getNine_file());
            vo.setNine_fileVo(fileInfoVo);
        } else {
            vo.setNine_fileVo(new SFileInfoVo());
        }
        if (vo.getTen_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getTen_file());
            vo.setTen_fileVo(fileInfoVo);
        } else {
            vo.setTen_fileVo(new SFileInfoVo());
        }

        if (vo.getEleven_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getEleven_file());
            vo.setEleven_fileVo(fileInfoVo);
        } else {
            vo.setEleven_fileVo(new SFileInfoVo());
        }

        if (vo.getTwelve_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getTwelve_file());
            vo.setTwelve_fileVo(fileInfoVo);
        } else {
            vo.setTwelve_fileVo(new SFileInfoVo());
        }
    }

    /**
     * 查询附件对象
     */
    private SFileInfoVo getFileInfo(Integer id) {
//        SFileEntity file = fileMapper.selectById(id);
//        SFileInfoEntity fileInfo = fileInfoMapper.selectFIdEntity(file.getId());
//        SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);

        //        fileInfoVo.setFileName(fileInfoVo.getFile_name());

        return fileInfoMapper.selectFId(id);
    }

    /**
     * 设置返回页面出库监管附件对象
     */
    public void setFile(BMonitorOutDeliveryVo vo) {
        SFileInfoVo fileInfoVo;
        // 空车过磅附件
        if (vo.getOne_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getOne_file());
            vo.setOne_fileVo(fileInfoVo);
        } else {
            vo.setOne_fileVo(new SFileInfoVo());
        }
        // 车头车尾带司机附件
        if (vo.getTwo_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getTwo_file());
            vo.setTwo_fileVo(fileInfoVo);
        } else {
            vo.setTwo_fileVo(new SFileInfoVo());
        }
        // 车辆行驶证
        if (vo.getFourteen_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getFourteen_file());
            vo.setFourteen_fileVo(fileInfoVo);
        } else {
            vo.setFourteen_fileVo(new SFileInfoVo());
        }
        // 司机承诺书附件
        if (vo.getThree_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getThree_file());
            vo.setThree_fileVo(fileInfoVo);
        } else {
            vo.setThree_fileVo(new SFileInfoVo());
        }
        // 司机身份证
        if (vo.getFour_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getFour_file());
            vo.setFour_fileVo(fileInfoVo);
        } else {
            vo.setFour_fileVo(new SFileInfoVo());
        }
        // 司机驾驶证
        if (vo.getTwelve_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getTwelve_file());
            vo.setTwelve_fileVo(fileInfoVo);
        } else {
            vo.setTwelve_fileVo(new SFileInfoVo());
        }
        // 车辆行驶证
        if (vo.getThirteen_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getThirteen_file());
            vo.setThirteen_fileVo(fileInfoVo);
        } else {
            vo.setThirteen_fileVo(new SFileInfoVo());
        }
        // 车头过磅附件
        if (vo.getFive_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getFive_file());
            vo.setFive_fileVo(fileInfoVo);
        } else {
            vo.setFive_fileVo(new SFileInfoVo());
        }
        // 装货视频附件
        if (vo.getSix_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getSix_file());
            vo.setSix_fileVo(fileInfoVo);
        } else {
            vo.setSix_fileVo(new SFileInfoVo());
        }
        // 重车过磅附件
        if (vo.getSeven_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getSeven_file());
            vo.setSeven_fileVo(fileInfoVo);
        } else {
            vo.setSeven_fileVo(new SFileInfoVo());
        }
        // 车头车尾带司机附件(重车)
        if (vo.getEight_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getEight_file());
            vo.setEight_fileVo(fileInfoVo);
        } else {
            vo.setEight_fileVo(new SFileInfoVo());
        }
        // 磅单附件
        if (vo.getNine_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getNine_file());
            vo.setNine_fileVo(fileInfoVo);
        } else {
            vo.setNine_fileVo(new SFileInfoVo());
        }
        if (vo.getTen_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getTen_file());
            vo.setTen_fileVo(fileInfoVo);
        } else {
            vo.setTen_fileVo(new SFileInfoVo());
        }
        if (vo.getEleven_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getEleven_file());
            vo.setEleven_fileVo(fileInfoVo);
        } else {
            vo.setEleven_fileVo(new SFileInfoVo());
        }

        if (vo.getFifteen_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getFifteen_file());
            vo.setFifteen_fileVo(fileInfoVo);
        } else {
            vo.setFifteen_fileVo(new SFileInfoVo());
        }

    }

    /**
     * check逻辑
     *
     * @return
     */
    private void checkLogic(BMonitorEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.CANCEL_CHECK_TYPE:
                // 是否已经作废
                if (Objects.equals(entity.getStatus(), DictConstant.DICT_B_MONITOR_STATUS_EIGHT)) {
                    throw new BusinessException(entity.getCode() + ":无法重复作废");
                }
                // 选中审核状态是审核通过，或结算状态是已结算的监管任务，作废也需灰掉
                if (Objects.equals(entity.getAudit_status(), DictConstant.DICT_B_MONITOR_AUDIT_STATUS_TWO)
                                || Objects.equals(entity.getSettlement_status(), DictConstant.DICT_B_MONITOR_SETTLEMENT_STATUS_ONE)) {
                    throw new BusinessException(entity.getCode() + ":当前状态无法作废");
                }
                break;
            case CheckResultAo.AUDIT_CHECK_TYPE:
            case CheckResultAo.AUDIT_CHECK_TYPE_IN:
                // 是否已经审核
                if (Objects.equals(entity.getAudit_status(), DictConstant.DICT_B_MONITOR_AUDIT_STATUS_TWO)) {
                    throw new BusinessException(entity.getCode() + ":无法重复审核");
                }
                break;
            case CheckResultAo.REJECT_CHECK_TYPE:
                // 是否已经作废
                if (Objects.equals(entity.getAudit_status(), DictConstant.DICT_B_MONITOR_AUDIT_STATUS_ONE)) {
                    throw new BusinessException(entity.getCode() + ":无法重复驳回");
                }
                break;
            case CheckResultAo.SETTLE_CHECK_TYPE:
                // 是否已经作废
                if (Objects.equals(entity.getSettlement_status(), DictConstant.DICT_B_MONITOR_SETTLEMENT_STATUS_ONE)) {
                    throw new BusinessException(entity.getCode() + ":无法重复结算");
                }
                break;
            case CheckResultAo.AUDIT_CHECK_TYPE_OUT:
                // 是否已经审核
                if (Objects.equals(entity.getAudit_status(), DictConstant.DICT_B_MONITOR_AUDIT_STATUS_THREE)) {
                    throw new BusinessException(entity.getCode() + ":无法重复审核");
                }
                // 是否已经审核
                if (Objects.equals(entity.getAudit_status(), DictConstant.DICT_B_MONITOR_AUDIT_STATUS_TWO)) {
                    throw new BusinessException(entity.getCode() + ":无法重复审核");
                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:
                // 是否已作废
                if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_MONITOR_STATUS_EIGHT)) {
                    throw new BusinessException(entity.getCode() + ":未作废, 删除失败!");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 入库监管任务保存逻辑
     */
    public void saveIn(BMonitorInUnloadVo vo, BMonitorInEntity monitorInEntity, BMonitorEntity monitor) {
//        // 附件主表
//        SFileEntity fileEntity = new SFileEntity();
//        fileEntity.setSerial_id(monitorInEntity.getId());
//        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR_IN);
//        // 附件从表
//        SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
//        SFileInfoVo file;
//        // 司机车头照片
//        file = vo.getOne_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorInEntity.setOne_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 司机车尾照片
//        file = vo.getTwo_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorInEntity.setTwo_file(fileEntity.getId());
//        fileEntity.setId(null);
//    // 正在卸货
//        monitorInEntity.setIs_container(vo.getIs_container());
//
//        // 车头照片
//        file = vo.getThree_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorInEntity.setThree_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 车尾照片
//        file = vo.getFour_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorInEntity.setFour_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 车侧身照片
//        file = vo.getFive_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorInEntity.setFive_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 卸货视频
//        file = vo.getSix_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorInEntity.setSix_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//    // 空车出库
//        // 司机车头照片
//        file = vo.getSeven_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorInEntity.setSeven_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 司机车尾照片
//        file = vo.getEight_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorInEntity.setEight_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 磅单
//        file = vo.getNine_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorInEntity.setNine_file(fileEntity.getId());
//        fileEntity.setId(null);


        if (monitorInEntity.getIn_id() != null) {
            insertIn(vo, monitorInEntity);
        } else if (monitorInEntity.getDelivery_id() != null) {
            insertInByDelivery(vo, monitorInEntity);
        }
    }


    /**
     * 新增监管任务附件
     */
    public void insertFile(SFileInfoVo file, SFileEntity fileEntity, SFileInfoEntity fileInfoEntity) {
        // 主表新增
        if (null != file) {
            fileEntity.setId(null);
            fileMapper.insert(fileEntity);
            file.setF_id(fileEntity.getId());
            BeanUtilsSupport.copyProperties(file, fileInfoEntity);
            fileInfoEntity.setFile_name(file.getFileName());
            fileInfoEntity.setId(null);
            fileInfoMapper.insert(fileInfoEntity);
        }
    }

    private void saveContainerFile(BContainerInfoVo vo) {
        // 每个附件对象非空判断，不为空的则是要进行修改的附件
        if (vo.getFile_oneVo() != null) {
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_type(SystemConstants.SERIAL_TYPE.B_CONTAINER_INFO);
            fileEntity.setSerial_id(vo.getId());
            fileMapper.insert(fileEntity);

            SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
            fileInfoEntity.setF_id(fileEntity.getId());
            fileInfoEntity.setUrl(vo.getFile_oneVo().getUrl());
            fileInfoEntity.setFile_name(vo.getFile_oneVo().getFileName());
            fileInfoEntity.setInternal_url(vo.getFile_oneVo().getInternal_url());
            fileInfoMapper.insert(fileInfoEntity);
            vo.setFile_one(fileEntity.getId());
        }

        if (vo.getFile_twoVo() != null) {
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_type(SystemConstants.SERIAL_TYPE.B_CONTAINER_INFO);
            fileEntity.setSerial_id(vo.getId());
            fileMapper.insert(fileEntity);

            SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
            fileInfoEntity.setF_id(fileEntity.getId());
            fileInfoEntity.setUrl(vo.getFile_twoVo().getUrl());
            fileInfoEntity.setFile_name(vo.getFile_twoVo().getFileName());
            fileInfoEntity.setInternal_url(vo.getFile_twoVo().getInternal_url());
            fileInfoMapper.insert(fileInfoEntity);
            vo.setFile_two(fileEntity.getId());
        }

        if (vo.getFile_threeVo() != null) {
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_type(SystemConstants.SERIAL_TYPE.B_CONTAINER_INFO);
            fileEntity.setSerial_id(vo.getId());
            fileMapper.insert(fileEntity);

            SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
            fileInfoEntity.setF_id(fileEntity.getId());
            fileInfoEntity.setUrl(vo.getFile_threeVo().getUrl());
            fileInfoEntity.setFile_name(vo.getFile_threeVo().getFileName());
            fileInfoEntity.setInternal_url(vo.getFile_threeVo().getInternal_url());
            fileInfoMapper.insert(fileInfoEntity);
            vo.setFile_three(fileEntity.getId());
        }

        if (vo.getFile_fourVo() != null) {
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_type(SystemConstants.SERIAL_TYPE.B_CONTAINER_INFO);
            fileEntity.setSerial_id(vo.getId());
            fileMapper.insert(fileEntity);

            SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
            fileInfoEntity.setF_id(fileEntity.getId());
            fileInfoEntity.setUrl(vo.getFile_fourVo().getUrl());
            fileInfoEntity.setFile_name(vo.getFile_fourVo().getFileName());
            fileInfoEntity.setInternal_url(vo.getFile_fourVo().getInternal_url());
            fileInfoMapper.insert(fileInfoEntity);
            vo.setFile_four(fileEntity.getId());
        }
    }

    /**
     * 新增入库单、入库单从表，更新入库计划明细逻辑
     */
    public void insertIn(BMonitorInUnloadVo vo, BMonitorInEntity monitorInEntity) {

//        if (monitorInEntity.getIn_id() != null) {
//            BInEntity inEntity = inMapper.selectById(monitorInEntity.getIn_id());
//            if (!Objects.equals(inEntity.getStatus(), DictConstant.DICT_B_IN_STATUS_TWO)) {
//                throw new BusinessException("请先作废入库单【" + inEntity.getCode() + "】");
//            }
//
//            BInPlanDetailEntity inPlanDetailEntity = inPlanDetailMapper.selectMonitorInId(monitorInEntity.getId());
//
//            // 除了类型是销售出库的出库计划生成的调度单,监管任务才会生成入库单
//            // 新增入库单逻辑
//            SUnitVo sUnitVo = isUnitService.selectByCode(SystemConstants.DEFAULT_UNIT.CODE);
//            BInEntity bInEntity = inMapper.selectByMonitorInId(monitorInEntity.getId());
//            bInEntity.setStatus(DictConstant.DICT_B_IN_STATUS_TWO);
//            bInEntity.setCode(inCode.autoCode().getCode());
//            bInEntity.setInbound_time(LocalDateTime.now());
//            // 车辆
//            bInEntity.setVehicle_no(vo.getVehicle_no());
//            // 查询单位换算关系
//            bInEntity.setUnit_id(inPlanDetailEntity.getUnit_id());
//            bInEntity.setTgt_unit_id(sUnitVo.getId());
//            bInEntity.setCalc(BigDecimal.ONE);
//            bInEntity.setActual_count(vo.getQty());
//            bInEntity.setActual_weight(vo.getQty());
//            bInEntity.setActual_volume(BigDecimal.ZERO);
//
//            // 皮重, 毛重
//            bInEntity.setGross_weight(vo.getGross_weight());
//            bInEntity.setTare_weight(vo.getTare_weight());
//            inMapper.insert(bInEntity);
//            // 入库单提交
//            bInEntity.setStatus(DictConstant.DICT_B_IN_STATUS_SUBMITTED);
//            bInEntity.setE_dt(null);
//            bInEntity.setE_id(null);
//            bInEntity.setE_opinion(null);
//            bInEntity.setInventory_account_id(null);
//
////            BInEntity newBInEntity = inMapper.selectById(monitorInEntity.getIn_id());
////            bInEntity.setDbversion(newBInEntity.getDbversion());
//            inMapper.updateById(bInEntity);
//
//            monitorInEntity.setIn_id(bInEntity.getId());
//
//            // 更新库存
//            iCommonInventoryLogicService.updWmsStockByInBill(bInEntity.getId());
//            // 入库单审核
//            bInEntity.setStatus(DictConstant.DICT_B_IN_STATUS_PASSED);
//            Long staffId = SecurityUtil.getStaff_id();
//            if (staffId != null) {
//                bInEntity.setE_id(staffId.intValue());
//            }
//
//            bInEntity.setE_dt(LocalDateTime.now());
//            bInEntity.setInventory_account_id(null);
//            bInEntity.setE_opinion(DictConstant.DICT_AUDIT_INFO_TYPE_FALSE);
//            BInEntity newBInEntity1 = inMapper.selectById(monitorInEntity.getIn_id());
//            bInEntity.setDbversion(newBInEntity1.getDbversion());
//            inMapper.updateById(bInEntity);
//            // 更新库存
//            iCommonInventoryLogicService.updWmsStockByInBill(bInEntity.getId());
//
//            // 新增入库单明细
//            BInExtraEntity inExtraEntity = inExtraMapper.selectByMonitorInId(monitorInEntity.getId());
//            if (inExtraEntity == null) {
//                inExtraEntity = new BInExtraEntity();
//            }
//            inExtraEntity.setIn_id(bInEntity.getId());
//            inExtraEntity.setPrimary_quantity(monitorInEntity.getQty());
//            inExtraMapper.insert(inExtraEntity);
//            // 更新入库计划待入库数量和重量、已入库数量和重量
//            updateInDetailCount(inPlanDetailEntity, bInEntity);
//
//
//
//            // 更新调度单
//            BScheduleEntity scheduleEntity = bScheduleMapper.selectByMonitorInId(monitorInEntity.getId());
//            monitorInEntity.setGross_weight(vo.getGross_weight());
//            monitorInEntity.setNet_weight(vo.getNet_weight());
//            monitorInEntity.setTare_weight(vo.getTare_weight());
//
//        }

    }

    /**
     * 新增提货单、提货单从表，更新入库计划明细逻辑
     */
    public void insertInByDelivery(BMonitorInUnloadVo vo, BMonitorInEntity monitorInEntity) {

//        if (monitorInEntity.getDelivery_id() != null) {
//            BDeliveryEntity bDeliveryEntityIf = bDeliveryMapper.selectById(monitorInEntity.getDelivery_id());
//            if (!Objects.equals(bDeliveryEntityIf.getStatus(), DictConstant.DICT_B_DELIVERY_STATUS_CANCEL)) {
//                throw new BusinessException("请先作废入库单【" + bDeliveryEntityIf.getCode() + "】");
//            }
//
//            BInPlanDetailEntity inPlanDetailEntity = inPlanDetailMapper.selectMonitorInId(monitorInEntity.getId());
//
//            // 除了类型是销售出库的出库计划生成的调度单,监管任务才会生成入库单
//            // 新增入库单逻辑
//            SUnitVo sUnitVo = isUnitService.selectByCode(SystemConstants.DEFAULT_UNIT.CODE);
//
//            BDeliveryEntity bDeliveryEntity = bDeliveryMapper.selectByMonitorInId(monitorInEntity.getDelivery_id());
//            bDeliveryEntity.setStatus(DictConstant.DICT_B_DELIVERY_STATUS_SAVED);
//            bDeliveryEntity.setCode(deliveryCode.autoCode().getCode());
//            bDeliveryEntity.setInbound_time(LocalDateTime.now());
//            // 车辆
//            bDeliveryEntity.setVehicle_no(vo.getVehicle_no());
//            // 查询单位换算关系
//            bDeliveryEntity.setUnit_id(inPlanDetailEntity.getUnit_id());
//            bDeliveryEntity.setTgt_unit_id(sUnitVo.getId());
//            bDeliveryEntity.setCalc(BigDecimal.ONE);
//            bDeliveryEntity.setActual_count(vo.getQty());
//            bDeliveryEntity.setActual_weight(vo.getQty());
//            bDeliveryEntity.setActual_volume(BigDecimal.ZERO);
//
//            // 皮重, 毛重
//            bDeliveryEntity.setGross_weight(vo.getGross_weight());
//            bDeliveryEntity.setTare_weight(vo.getTare_weight());
//            bDeliveryMapper.insert(bDeliveryEntity);
//            // 入库单提交
//            bDeliveryEntity.setStatus(DictConstant.DICT_B_DELIVERY_STATUS_SUBMITTED);
//            bDeliveryEntity.setE_dt(null);
//            bDeliveryEntity.setE_id(null);
//            bDeliveryEntity.setE_opinion(null);
//            bDeliveryEntity.setInventory_account_id(null);
//
//            bDeliveryMapper.updateById(bDeliveryEntity);
//
//            monitorInEntity.setDelivery_id(bDeliveryEntity.getId());
//
//            // 入库单审核
//            bDeliveryEntity.setStatus(DictConstant.DICT_B_DELIVERY_STATUS_PASSED);
//            Long staffId = SecurityUtil.getStaff_id();
//            if (staffId != null) {
//                bDeliveryEntity.setE_id(staffId.intValue());
//            }
//
//            bDeliveryEntity.setE_dt(LocalDateTime.now());
//            bDeliveryEntity.setInventory_account_id(null);
//            bDeliveryEntity.setE_opinion(DictConstant.DICT_AUDIT_INFO_TYPE_FALSE);
//
//            BDeliveryEntity newBInEntity1 = bDeliveryMapper.selectById(bDeliveryEntity.getId());
//            bDeliveryEntity.setDbversion(newBInEntity1.getDbversion());
//            bDeliveryMapper.updateById(bDeliveryEntity);
//
//            // 新增入库单明细
//            BDeliveryExtraEntity bDeliveryExtraEntity = bDeliveryExtraMapper.selectByMonitorInId(monitorInEntity.getId());
//            if (bDeliveryExtraEntity == null) {
//                bDeliveryExtraEntity = new BDeliveryExtraEntity();
//            }
//            bDeliveryExtraEntity.setDelivery_id(bDeliveryEntity.getId());
//            bDeliveryExtraEntity.setPrimary_quantity(monitorInEntity.getQty());
//            bDeliveryExtraMapper.insert(bDeliveryExtraEntity);
//
//            // 查询出库计划明细，更新已处理和待处理数量
//            BInPlanDetailVo bInPlanDetailVo = inPlanDetailMapper.selectPlanDetailCountByDelivery(inPlanDetailEntity.getId());
//            inPlanDetailEntity.setPending_count(bInPlanDetailVo.getPending_count()); // 更新待处理数量
//            inPlanDetailEntity.setPending_weight(bInPlanDetailVo.getPending_weight()); // 更新待处理重量
//            inPlanDetailEntity.setHas_handle_count(bInPlanDetailVo.getHas_handle_count()); // 更新已处理数量
//            inPlanDetailEntity.setHas_handle_weight(bInPlanDetailVo.getHas_handle_weight()); // 更新已处理重量
//            inPlanDetailMapper.updateById(inPlanDetailEntity);
//
//            // 更新调度单
//            BScheduleEntity scheduleEntity = bScheduleMapper.selectByMonitorInId(monitorInEntity.getId());
//            monitorInEntity.setGross_weight(vo.getGross_weight());
//            monitorInEntity.setNet_weight(vo.getNet_weight());
//            monitorInEntity.setTare_weight(vo.getTare_weight());
//
//        }

    }

    /**
     * 新增入库单、入库单从表，更新入库计划明细逻辑
     */
    public void insertUnload(BMonitorInUnloadVo vo, BMonitorUnloadEntity monitorUnloadEntity) {
        // 更新调度单
        BScheduleEntity scheduleEntity = bScheduleMapper.selectByMonitorInId(monitorUnloadEntity.getId());
        monitorUnloadEntity.setGross_weight(vo.getGross_weight());
        monitorUnloadEntity.setNet_weight(vo.getNet_weight());
        monitorUnloadEntity.setTare_weight(vo.getTare_weight());
//        // 已入库数量是否大于计划数量
//        if(scheduleEntity.getIn_operated_qty().add(vo.getQty()).compareTo(scheduleEntity.getIn_schedule_qty()) > 0) {
//            // 待入库=0
//            scheduleEntity.setIn_balance_qty(BigDecimal.ZERO);
//        }else{
//            scheduleEntity.setIn_balance_qty(scheduleEntity.getIn_balance_qty().subtract(vo.getQty()));
//        }
//        scheduleEntity.setIn_operated_qty(scheduleEntity.getIn_operated_qty().add(vo.getQty()));
//        bScheduleMapper.updateById(scheduleEntity);
    }

    /**
     * 更新入库计划待入库数量和重量、已入库数量和重量
     */
    public void updateInDetailCount(BInPlanDetailEntity inPlanDetailEntity, BInEntity inEntity) {

//        BInPlanDetailVo bInPlanDetailVo = inPlanDetailMapper.selectPlanDetailCount(inPlanDetailEntity.getId());
//
//        inPlanDetailEntity.setPending_count(bInPlanDetailVo.getPending_count()); // 更新待处理数量
//
//        inPlanDetailEntity.setPending_weight(bInPlanDetailVo.getPending_weight()); // 更新待处理重量
//
//        inPlanDetailEntity.setHas_handle_count(bInPlanDetailVo.getHas_handle_count()); // 更新已处理数量
//
//        inPlanDetailEntity.setHas_handle_weight(bInPlanDetailVo.getHas_handle_weight()); // 更新已处理重量
//
//        inPlanDetailMapper.updateById(inPlanDetailEntity);
    }

    /**
     * 卸货监管任务保存逻辑
     */
    public void saveUnload(BMonitorInUnloadVo vo, BMonitorUnloadEntity monitorUnloadEntity, BMonitorEntity monitor) {
        // 附件主表
//        SFileEntity fileEntity = new SFileEntity();
//        fileEntity.setSerial_id(monitorUnloadEntity.getId());
//        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR_IN);
//        // 附件从表
//        SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
//        SFileInfoVo file;
//        // 司机车头照片
//        file = vo.getOne_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorUnloadEntity.setOne_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 司机车尾照片
//        file = vo.getTwo_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorUnloadEntity.setTwo_file(fileEntity.getId());
//        fileEntity.setId(null);
//    // 正在卸货
//        monitorUnloadEntity.setIs_container(vo.getIs_container());
//
//        // 车头照片
//        file = vo.getThree_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorUnloadEntity.setThree_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 车尾照片
//        file = vo.getFour_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorUnloadEntity.setFour_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 车侧身照片
//        file = vo.getFive_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorUnloadEntity.setFive_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 卸货视频
//        file = vo.getSix_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorUnloadEntity.setSix_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//    // 空车出库
//        // 司机车头照片
//        file = vo.getSeven_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorUnloadEntity.setSeven_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 司机车尾照片
//        file = vo.getEight_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorUnloadEntity.setEight_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 磅单
//        file = vo.getNine_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorUnloadEntity.setNine_file(fileEntity.getId());
//        fileEntity.setId(null);

    }

    /**
     * 提货监管任务保存逻辑
     */
    public void saveDelivery(BMonitorOutDeliveryVo vo, BMonitorDeliveryEntity monitorDeliveryEntity, BMonitorEntity monitor) {
//        // 附件主表
//        SFileEntity fileEntity = new SFileEntity();
//        fileEntity.setSerial_id(monitorDeliveryEntity.getId());
//        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR_OUT);
//        // 附件从表
//        SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
//        SFileInfoVo file;
//        // 车头照片磅附件新增逻辑
//        file = vo.getOne_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorDeliveryEntity.setOne_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 车尾照片附件新增逻辑
//        file = vo.getTwo_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorDeliveryEntity.setTwo_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 司机承诺书附件新增逻辑
//        file = vo.getThree_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorDeliveryEntity.setThree_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 三证拍照附件新增逻辑
//        file = vo.getFour_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorDeliveryEntity.setFour_file(fileEntity.getId());
//        fileEntity.setId(null);
//    // 正在装货
//        monitorDeliveryEntity.setIs_container(vo.getIs_container());
//
//        // 车头照片附件新增逻辑
//        file = vo.getFive_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorDeliveryEntity.setFive_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 车尾照片新增逻辑
//        file = vo.getSix_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorDeliveryEntity.setSix_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 车侧身附件新增逻辑
//        file = vo.getSeven_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorDeliveryEntity.setSeven_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 装货视频附件新增逻辑
//        file = vo.getEight_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorDeliveryEntity.setEight_file(fileEntity.getId());
//        fileEntity.setId(null);
//    // 重车出库
//        // 车头照片附件新增逻辑
//        file = vo.getNine_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorDeliveryEntity.setNine_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 车尾照片附件新增逻辑
//        file = vo.getTen_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorDeliveryEntity.setTen_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 磅单附件新增逻辑
//        file = vo.getEleven_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorDeliveryEntity.setEleven_file(fileEntity.getId());
//        fileEntity.setId(null);
    }

    /**
     * 新增出库单、出库单从表、监管任务_出库/提货逻辑，更新出库计划明细逻辑
     */
//    public void insertDelivery(BMonitorOutDeliveryVo vo,BMonitorDeliveryEntity monitorDeliveryEntity) {
//        SUnitVo sUnitVo = isUnitService.selectByCode(SystemConstants.DEFAULT_UNIT.CODE);
//        BScheduleEntity scheduleEntity = bScheduleMapper.selectByMonitorDeliveryId(monitorDeliveryEntity.getId());
//
//        if (Objects.equals(DictConstant.DICT_B_SCHEDULE_IN_TYPE_IN, scheduleEntity.getIn_type())) {
//            // 新增监管任务_入库
//            BMonitorInEntity monitorInEntity = new BMonitorInEntity();
//            monitorInEntity.setStatus(DictConstant.DICT_B_MONITOR_IN_STATUS_HEAVY);
//            monitorInEntity.setMonitor_id(monitorDeliveryEntity.getMonitor_id());
//            monitorInEntity.setIs_container(vo.getIs_container());
//            monitorInEntity.setType(SystemConstants.SERIAL_TYPE.B_MONITOR_IN);
//            monitorInMapper.insert(monitorInEntity);
//        } else if (Objects.equals(DictConstant.DICT_B_SCHEDULE_IN_TYPE_UNLOAD, scheduleEntity.getIn_type())) {
//            // 新增监管任务_卸货
//            BMonitorUnloadEntity monitorUnloadEntity = new BMonitorUnloadEntity();
//            monitorUnloadEntity.setStatus(DictConstant.DICT_B_MONITOR_IN_STATUS_HEAVY);
//            monitorUnloadEntity.setMonitor_id(monitorDeliveryEntity.getMonitor_id());
//            monitorUnloadEntity.setIs_container(vo.getIs_container());
//            monitorUnloadEntity.setType(SystemConstants.SERIAL_TYPE.B_MONITOR_UNLOAD);
//            monitorUnloadMapper.insert(monitorUnloadEntity);
//        }
//
//        // 更新调度单
//
//        monitorDeliveryEntity.setGross_weight(vo.getGross_weight());
//        monitorDeliveryEntity.setNet_weight(vo.getNet_weight());
//        monitorDeliveryEntity.setTare_weight(vo.getTare_weight());
//
//        // 净重加已出库数量是否大于计划出库数量
//        if(scheduleEntity.getOut_operated_qty().add(vo.getQty()).compareTo(scheduleEntity.getOut_schedule_qty()) > 0) {
//            // 待出库=0，已出库=调度数量
//            scheduleEntity.setOut_balance_qty(BigDecimal.ZERO);
//        }else{
//            scheduleEntity.setOut_balance_qty(scheduleEntity.getOut_balance_qty().subtract(vo.getQty()));
//        }
//        scheduleEntity.setOut_operated_qty(scheduleEntity.getOut_operated_qty().add(vo.getQty()));
//
//        bScheduleMapper.updateById(scheduleEntity);
//    }

    /**
     * 新增出库单、出库单从表、监管任务_出库/提货逻辑，更新出库计划明细逻辑
     */
    public void insertOut(BMonitorOutDeliveryVo vo, BMonitorOutEntity monitorOutEntity) {

        if (monitorOutEntity.getOut_id() != null) {
            BOutEntity outEntity = outMapper.selectById(monitorOutEntity.getOut_id());
            if (!Objects.equals(outEntity.getStatus(), DictConstant.DICT_B_OUT_STATUS_CANCEL)) {
                throw new BusinessException("请先作废出库单【" + outEntity.getCode() + "】");
            }

            BOutPlanDetailEntity outPlanDetailEntity = outPlanDetailMapper.selectMonitorOutId(monitorOutEntity.getId());

            SUnitVo sUnitVo = isUnitService.selectByCode(SystemConstants.DEFAULT_UNIT.CODE);
//            BScheduleEntity scheduleEntity = bScheduleMapper.selectByMonitorOutId(monitorOutEntity.getId());

            // 新增出库单逻辑
            BOutEntity bOutEntity = outMapper.selectByMonitorOutId(monitorOutEntity.getId());
            bOutEntity.setCode(outCode.autoCode().getCode());
            bOutEntity.setOutbound_time(LocalDateTime.now());
            bOutEntity.setUnit_id(outPlanDetailEntity.getUnit_id());
            bOutEntity.setVehicle_no(vo.getVehicle_no());
            bOutEntity.setTgt_unit_id(sUnitVo.getId());
            bOutEntity.setCalc(BigDecimal.ONE);
            bOutEntity.setE_dt(null);
            bOutEntity.setE_id(null);
            bOutEntity.setE_opinion(null);
            bOutEntity.setInventory_account_id(null);

            // 皮重, 毛重
            bOutEntity.setTare_weight(vo.getTare_weight());
            bOutEntity.setGross_weight(vo.getGross_weight());
            outMapper.insert(bOutEntity);

            monitorOutEntity.setOut_id(bOutEntity.getId());

            // 更新库存
            iCommonInventoryLogicService.updWmsStockByOutBill(bOutEntity.getId());
            // 出库单审核
            bOutEntity.setStatus(DictConstant.DICT_B_OUT_STATUS_PASSED);
            Long staffId = SecurityUtil.getStaff_id();
            if (staffId != null) {
                bOutEntity.setE_id(staffId.intValue());
            }
            bOutEntity.setE_dt(LocalDateTime.now());
            bOutEntity.setInventory_account_id(null);
            bOutEntity.setE_opinion(DictConstant.DICT_AUDIT_INFO_TYPE_FALSE);
            BOutEntity newBOutEntity = outMapper.selectById(bOutEntity.getId());
            bOutEntity.setDbversion(newBOutEntity.getDbversion());
            outMapper.updateById(bOutEntity);

            // 更新库存
            iCommonInventoryLogicService.updWmsStockByOutBill(bOutEntity.getId());

            // 新增出库单明细
            BOutExtraEntity outExtraEntity = outExtraMapper.selectByMonitorOutId(monitorOutEntity.getId());
            if (outExtraEntity == null) {
                outExtraEntity = new BOutExtraEntity();
            }
            outExtraEntity.setOut_id(bOutEntity.getId());
            outExtraMapper.insert(outExtraEntity);

            // 更新出库计划待出库数量和重量、已出库数量和重量
            updateOutDetailCount(outPlanDetailEntity, bOutEntity);

            // 更新调度单
            monitorOutEntity.setGross_weight(vo.getGross_weight());
            monitorOutEntity.setNet_weight(vo.getNet_weight());
            monitorOutEntity.setTare_weight(vo.getTare_weight());

        }


    }

    /**
     * 新增收货单、出库单从表、监管任务_出库/提货逻辑，更新出库计划明细逻辑
     */
    public void insertReceive(BMonitorOutDeliveryVo vo, BMonitorOutEntity monitorOutEntity) {

        if (monitorOutEntity.getReceive_id() != null) {
            BReceiveEntity bReceiveEntityIf = bReceiveMapper.selectById(monitorOutEntity.getReceive_id());
            if (!Objects.equals(bReceiveEntityIf.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_CANCEL)) {
                throw new BusinessException("请先作废收货单【" + bReceiveEntityIf.getCode() + "】");
            }

            BOutPlanDetailEntity outPlanDetailEntity = outPlanDetailMapper.selectMonitorOutId(monitorOutEntity.getId());

            SUnitVo sUnitVo = isUnitService.selectByCode(SystemConstants.DEFAULT_UNIT.CODE);

            // 新增收货单逻辑
            BReceiveEntity bReceiveEntity = bReceiveMapper.selectByMonitorOutId(monitorOutEntity.getId());
            bReceiveEntity.setCode(receiveCode.autoCode().getCode());
            bReceiveEntity.setOutbound_time(LocalDateTime.now());
            bReceiveEntity.setUnit_id(outPlanDetailEntity.getUnit_id());
            bReceiveEntity.setVehicle_no(vo.getVehicle_no());
            bReceiveEntity.setTgt_unit_id(sUnitVo.getId());
            bReceiveEntity.setCalc(BigDecimal.ONE);
            bReceiveEntity.setE_dt(null);
            bReceiveEntity.setE_id(null);
            bReceiveEntity.setE_opinion(null);
            bReceiveEntity.setInventory_account_id(null);

            // 皮重, 毛重
            bReceiveEntity.setTare_weight(vo.getTare_weight());
            bReceiveEntity.setGross_weight(vo.getGross_weight());
            bReceiveMapper.insert(bReceiveEntity);

            monitorOutEntity.setOut_id(bReceiveEntity.getId());

            // 出库单审核
            bReceiveEntity.setStatus(DictConstant.DICT_B_OUT_STATUS_PASSED);
            Long staffId = SecurityUtil.getStaff_id();
            if (staffId != null) {
                bReceiveEntity.setE_id(staffId.intValue());
            }
            bReceiveEntity.setE_dt(LocalDateTime.now());
            bReceiveEntity.setInventory_account_id(null);
            bReceiveEntity.setE_opinion(DictConstant.DICT_AUDIT_INFO_TYPE_FALSE);

            BReceiveEntity newBOutEntity = bReceiveMapper.selectById(bReceiveEntity.getId());
            bReceiveEntity.setDbversion(newBOutEntity.getDbversion());
            bReceiveMapper.updateById(bReceiveEntity);

            // 新增出库单明细
            BReceiveExtraEntity bReceiveExtraEntity = bReceiveExtraMapper.selectByMonitorOutId(monitorOutEntity.getId());
            if (bReceiveExtraEntity == null) {
                bReceiveExtraEntity = new BReceiveExtraEntity();
            }
            bReceiveExtraEntity.setReceive_id(bReceiveEntity.getId());
            bReceiveExtraMapper.insert(bReceiveExtraEntity);

            // 查询出库计划明细，更新已处理和待处理数量
            BOutPlanDetailEntity detail = outPlanDetailMapper.selectById(bReceiveEntity.getPlan_detail_id());
            BOutPlanDetailVo bOutPlanDetailVo = outPlanDetailMapper.selectPlanDetailCountByReceive(detail.getId());
            detail.setPending_count(bOutPlanDetailVo.getPending_count()); // 更新待处理数量
            detail.setPending_weight(bOutPlanDetailVo.getPending_weight()); // 更新待处理重量
            detail.setHas_handle_count(bOutPlanDetailVo.getHas_handle_count()); // 更新已处理数量
            detail.setHas_handle_weight(bOutPlanDetailVo.getHas_handle_weight()); // 更新已处理重量
            outPlanDetailMapper.updateById(detail);

            // 更新调度单
            monitorOutEntity.setGross_weight(vo.getGross_weight());
            monitorOutEntity.setNet_weight(vo.getNet_weight());
            monitorOutEntity.setTare_weight(vo.getTare_weight());
        }
    }

    /**
     * 更新出库计划待出库、已出库数量
     */
    public void updateOutDetailCount(BOutPlanDetailEntity outPlanDetailEntity, BOutEntity outEntity) {
        BOutPlanDetailVo bOutPlanDetailVo = outPlanDetailMapper.selectPlanDetailCount(outPlanDetailEntity.getId());

        outPlanDetailEntity.setPending_count(bOutPlanDetailVo.getPending_count()); // 更新待处理数量

        outPlanDetailEntity.setPending_weight(bOutPlanDetailVo.getPending_weight()); // 更新待处理重量

        outPlanDetailEntity.setHas_handle_count(bOutPlanDetailVo.getHas_handle_count()); // 更新已处理数量

        outPlanDetailEntity.setHas_handle_weight(bOutPlanDetailVo.getHas_handle_weight()); // 更新已处理重量

        outPlanDetailMapper.updateById(outPlanDetailEntity);
    }

    /**
     * 出库监管任务保存逻辑
     */
    public void saveOut(BMonitorOutDeliveryVo vo, BMonitorOutEntity monitorOutEntity, BMonitorEntity monitor) {
//        // 附件主表
//        SFileEntity fileEntity = new SFileEntity();
//        fileEntity.setSerial_id(monitorOutEntity.getId());
//        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR_OUT);
//        // 附件从表
//        SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
//        SFileInfoVo file;
//        // 车头照片磅附件新增逻辑
//        file = vo.getOne_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorOutEntity.setOne_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 车尾照片附件新增逻辑
//        file = vo.getTwo_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorOutEntity.setTwo_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 司机承诺书附件新增逻辑
//        file = vo.getThree_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorOutEntity.setThree_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 三证拍照附件新增逻辑
//        file = vo.getFour_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorOutEntity.setFour_file(fileEntity.getId());
//        fileEntity.setId(null);
//    // 正在装货
//        monitorOutEntity.setIs_container(vo.getIs_container());
//
//        // 车头照片附件新增逻辑
//        file = vo.getFive_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorOutEntity.setFive_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 车尾照片新增逻辑
//        file = vo.getSix_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorOutEntity.setSix_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 车侧身附件新增逻辑
//        file = vo.getSeven_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorOutEntity.setSeven_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 装货视频附件新增逻辑
//        file = vo.getEight_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorOutEntity.setEight_file(fileEntity.getId());
//        fileEntity.setId(null);
//    // 重车出库
//        // 车头照片附件新增逻辑
//        file = vo.getNine_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorOutEntity.setNine_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 车尾照片附件新增逻辑
//        file = vo.getTen_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorOutEntity.setTen_file(fileEntity.getId());
//        fileEntity.setId(null);
//
//        // 磅单附件新增逻辑
//        file = vo.getEleven_fileVo();
//        insertFile(file,fileEntity,fileInfoEntity);
//        monitorOutEntity.setEleven_file(fileEntity.getId());
//        fileEntity.setId(null);

        if (monitorOutEntity.getOut_id() != null) {
            insertOut(vo, monitorOutEntity);
        } else if (monitorOutEntity.getReceive_id() != null) {
            insertReceive(vo, monitorOutEntity);
        }
    }

//    @SysLogAnnotion("7、查询出库单是否可以作废")
    private void callOutCanceledAppCode10Api(List<BOutVo> list, String code) {
        SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.OUT_CANCELED);
        String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
        for (BOutVo vo : list) {
            BOutVo bOutVo = ibOutService.selectById(vo.getId());
            if (StringUtils.isEmpty(bOutVo.getExtra_code())) {
                continue;
            }

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> requestEntity = new HttpEntity(bOutVo.getCode(), headers);
            ResponseEntity<JSONObject> result = restTemplate.postForEntity(url, requestEntity, JSONObject.class);

//            ApiCanceledVo apiCanceledVo = JSONObject.toJavaObject((JSONObject) JSONObject.toJSON(result.getBody().getJSONObject("data")), ApiCanceledVo.class);
            ApiCanceledVo apiCanceledVo = JSONObject.from(result.getBody().getJSONObject("data")).toJavaObject(ApiCanceledVo.class);
            if (apiCanceledVo.getData() != null) {
                for (ApiCanceledDataVo apiCanceledDataVo : apiCanceledVo.getData()) {
                    if (apiCanceledDataVo.getCancel() != null && !apiCanceledDataVo.getCancel()) {
                        throw new BusinessException(String.format("出库单%s已结算，不能作废!", code));
                    }
                }
            }

        }
    }


    /**
     * 响应结果处理回调方法
     *
     * @param result
     */
    public void callback(String result) {
        log.debug("=============同步出库信息result=============" + result);
    }

    /**
     * 拼接中台同步数据url
     *
     * @param uri
     * @param appCode
     * @return
     */
    protected String getBusinessCenterUrl(String uri, String appCode) {
        try {
            SAppConfigEntity sAppConfigEntity = isAppConfigService.getDataByAppCode(appCode);
            String app_key = sAppConfigEntity.getApp_key();
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
     * 判断是否是预警 5, 如果是, 查看当前查询人员是否在预警人员/组内, 如果不在, 返回空集合,  在 继续查询 则返回数据
     * @return
     */
    private Boolean checkMonitorAlarmStaff(BMonitorVo searchCondition) {
        Boolean flag = false;
        if ("6".equals(searchCondition.getActive_tabs_index()) || "7".equals(searchCondition.getActive_tabs_index())) {
            flag = true;
            // 判断是否是预警 5, 如果是, 查看当前查询人员是否在预警人员/组内, 如果不在, 返回空集合,  在 继续查询 则返回数据
            List<BAlarmRulesBo> bAlarmRulesBoList = alarmRulesService.selectStaffAlarm(DictConstant.DICT_B_ALARM_RULES_TYPE_0);
            if (!CollectionUtils.isEmpty(bAlarmRulesBoList)) {
                Boolean b = bAlarmRulesBoList.stream().anyMatch(item -> Long.valueOf(item.getStaff_id().toString()).equals(SecurityUtil.getStaff_id()));
                flag = !b;
            }
        }
        return flag;
    }

//    @SysLogAnnotion("6、查询入库单是否可以作废")
    private void callInCanceledAppCode10Api(List<BInVo> list) {
//        for (BInVo vo : list) {
//            BInVo bInVo = inMapper.selectId(vo.getId());
//            if (StringUtils.isEmpty(bInVo.getExtra_code())) {
//                continue;
//            }
//            SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.IN_CANCELED);
//            String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
//            HttpHeaders headers = new HttpHeaders();
//
//            HttpEntity<String> requestEntity = new HttpEntity(bInVo.getCode(), headers);
//            ResponseEntity<JSONObject> result = restTemplate.postForEntity(url, requestEntity, JSONObject.class);
////            ApiCanceledVo apiCanceledVo = JSONObject.toJavaObject((JSONObject)JSONObject.toJSON(result.getBody().getJSONObject("data")), ApiCanceledVo.class);
//            ApiCanceledVo apiCanceledVo = JSONObject.from(result.getBody().getJSONObject("data")).toJavaObject(ApiCanceledVo.class);
//            if (apiCanceledVo.getData() != null) {
//                for(ApiCanceledDataVo apiCanceledDataVo: apiCanceledVo.getData()) {
//                    if (apiCanceledDataVo.getCancel() != null && !apiCanceledDataVo.getCancel()) {
//                        throw new BusinessException(String.format("入库单%s已结算，不能作废", bInVo.getCode()));
//                    }
//                }
//            }
//        }
    }

    /**
     * 取消收货单
     */
    private void cancelReceive(Integer receive_id, String remark) {
        if (receive_id == null) {
            return;
        }
        BReceiveEntity entity = bReceiveMapper.selectById(receive_id);
        if (Objects.equals(entity.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_CANCEL)) {
            return;
        }

        // 查询收货单是否可以作废
        BReceiveVo bReceiveVo  = new BReceiveVo();
        bReceiveVo.setId(entity.getId());
        callReceiveCanceledAppCode10Api(List.of(bReceiveVo));

        entity.setStatus(DictConstant.DICT_B_RECEIVE_STATUS_CANCEL);
        entity.setInventory_account_id(null);
        bReceiveMapper.updateById(entity);

        // 更新出库计划中的已出库数量，待出库数量
        if(entity.getPlan_detail_id() != null && !DictConstant.DICT_B_RECEIVE_STATUS_SAVED.equals(entity.getStatus())) {
            BOutPlanDetailEntity detail = outPlanDetailMapper.selectById(entity.getPlan_detail_id());
            BOutPlanDetailVo bOutPlanDetailVo = outPlanDetailMapper.selectPlanDetailCountByReceive(detail.getId());
            detail.setPending_count(bOutPlanDetailVo.getPending_count()); // 更新待处理数量
            detail.setPending_weight(bOutPlanDetailVo.getPending_weight()); // 更新待处理重量
            detail.setHas_handle_count(bOutPlanDetailVo.getHas_handle_count()); // 更新已处理数量
            detail.setHas_handle_weight(bOutPlanDetailVo.getHas_handle_weight()); // 更新已处理重量
            outPlanDetailMapper.updateById(detail);
        }

        // 作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(entity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_RECEIVE);
        mCancelVo.setRemark(remark);
        mCancelService.insert(mCancelVo);
    }

    @SysLogAnnotion("查询收货单是否可以作废")
    private void callReceiveCanceledAppCode10Api(List<BReceiveVo> list) {
        SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.RECEIVE_CANCELED);
        String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
        for (BReceiveVo vo : list) {
            BReceiveVo bReceiveVo = ibReceiveService.selectById(vo.getId());
            if (StringUtils.isEmpty(bReceiveVo.getExtra_code())) {
                continue;
            }

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> requestEntity = new HttpEntity(bReceiveVo.getCode(), headers);
            ResponseEntity<JSONObject> result = restTemplate.postForEntity(url, requestEntity, JSONObject.class);

            ApiCanceledVo apiCanceledVo = JSONObject.from(result.getBody().getJSONObject("data")).toJavaObject(ApiCanceledVo.class);

            if (apiCanceledVo.getData() != null) {
                for(ApiCanceledDataVo apiCanceledDataVo: apiCanceledVo.getData()) {
                    if (apiCanceledDataVo.getCancel() != null && !apiCanceledDataVo.getCancel()) {
                        throw new BusinessException(String.format("收货单%s已结算，不能作废!",bReceiveVo.getCode()));
                    }
                }
            }

        }
    }


    /**
     * 更新提货表
     */
    private void cancelDelivery(Integer delivery_id, String remark) {
        if (delivery_id == null) {
            return;
        }

        BDeliveryEntity entity = bDeliveryMapper.selectById(delivery_id);
        if (Objects.equals(entity.getStatus(), DictConstant.DICT_B_DELIVERY_STATUS_CANCEL)) {
            return;
        }

        // 查询 业务中台, 是否可以结算
        BDeliveryVo bDeliveryVo = new BDeliveryVo();
        bDeliveryVo.setId(delivery_id);
        callDeliveryCanceledAppCode10Api(List.of(bDeliveryVo));

        entity.setStatus(DictConstant.DICT_B_DELIVERY_STATUS_CANCEL);
        entity.setInventory_account_id(null);
        bDeliveryMapper.updateById(entity);

        // 查询入库计划明细，更新已处理和待处理数量
        if(entity.getPlan_detail_id() != null && !DictConstant.DICT_B_DELIVERY_STATUS_SAVED.equals(entity.getStatus())) {
//            BInPlanDetailEntity detail = inPlanDetailMapper.selectById(entity.getPlan_detail_id());
//            BInPlanDetailVo bInPlanDetailVo = inPlanDetailMapper.selectPlanDetailCountByDelivery(detail.getId());
//            detail.setPending_count(bInPlanDetailVo.getPending_count()); // 更新待处理数量
//            detail.setPending_weight(bInPlanDetailVo.getPending_weight()); // 更新待处理重量
//            detail.setHas_handle_count(bInPlanDetailVo.getHas_handle_count()); // 更新已处理数量
//            detail.setHas_handle_weight(bInPlanDetailVo.getHas_handle_weight()); // 更新已处理重量
//            inPlanDetailMapper.updateById(detail);
        }

        // 作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(entity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_DELIVERY);
        mCancelVo.setRemark(remark);
        mCancelService.insert(mCancelVo);
    }


    @SysLogAnnotion("6、查询提货单是否可以作废")
    private void callDeliveryCanceledAppCode10Api(List<BDeliveryVo> list) {
//        for (BDeliveryVo vo : list) {
//            BDeliveryVo bDeliveryVo = ibDeliveryService.selectById(vo.getId());
//            if (StringUtils.isEmpty(bDeliveryVo.getExtra_code())) {
//                continue;
//            }
//            SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.DELIVERY_CANCELED);
//            String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
//            HttpHeaders headers = new HttpHeaders();
//
//            HttpEntity<String> requestEntity = new HttpEntity(bDeliveryVo.getCode(), headers);
//            ResponseEntity<JSONObject> result = restTemplate.postForEntity(url, requestEntity, JSONObject.class);
////            ApiCanceledVo apiCanceledVo = JSONObject.toJavaObject((JSONObject)JSONObject.toJSON(result.getBody().getJSONObject("data")), ApiCanceledVo.class);
//            ApiCanceledVo apiCanceledVo = JSONObject.from(result.getBody().getJSONObject("data")).toJavaObject(ApiCanceledVo.class);
//            if (apiCanceledVo.getData() != null) {
//                for(ApiCanceledDataVo apiCanceledDataVo: apiCanceledVo.getData()) {
//                    if (apiCanceledDataVo.getCancel() != null && !apiCanceledDataVo.getCancel()) {
//                        throw new BusinessException(String.format("提货单%s已结算，不能作废!",bDeliveryVo.getCode()));
//                    }
//                }
//            }
//        }
    }


    /**
     * 在途 包含铁路虚拟库
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public IPage<BContractReportVo> queryQtyInventorTotalList(BContractReportVo searchCondition) {
        // 分页条件
        Page<BMonitorEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.queryQtyInventorTotalList(searchCondition, pageCondition);
    }

    /**
     * 在途报表明细包含铁路港口码头虚拟库
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public IPage<BContractReportVo> queryQtyInventorLossList(BContractReportVo searchCondition) {
//        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        // 分页条件
        Page<BMonitorEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.queryQtyInventorLossList(searchCondition, pageCondition);
    }


    /**
     * 在途报表明细包含铁路港口码头虚拟库 合计
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public BContractReportVo queryQtyInventorLossListSum(BContractReportVo param) {
        return mapper.queryQtyInventorLossListSum(param);
    }

    /**
     * 在途报表包含铁路港口码头虚拟库求和
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public BContractReportVo queryQtyInventorSumList(BContractReportVo param) {
        return mapper.queryQtyInventorSumList(param);
    }

    /**
     * 在途报表汇总包含铁路港口码头虚拟库 全部导出
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public List<BQtyInTransitExportVo> queryQtyOnWayByInventorAllExportAll(BContractReportVo param) {
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectLossExportNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryQtyOnWayByInventorAllExportAll(param);
    }

    /**
     * 在途报表汇总包含铁路港口码头虚拟库 部分导出
     *
     * @param param
     * @return
     */
    @Override
    public List<BQtyInTransitExportVo> queryQtyOnWayByInventorExport(List<BContractReportVo> param) {
        return mapper.queryQtyOnWayByInventorExport(param);
    }

    /**
     * 在途报表 全部导出
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t1.in_warehouse_id,t1.out_warehouse_id")
    public List<BInTransitReportExportVo> queryOnWayByInventorListExportAll(BContractReportVo param) {
        param.setStaff_id(SecurityUtil.getStaff_id());
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectLossDetailExportNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryOnWayByInventorListExportAll(param);
    }

    /**
     * 在途报表 部分导出
     *
     * @param param
     * @return
     */
    @Override
    public List<BInTransitReportExportVo> queryOnWayByInventorListExport(List<BContractReportVo> param) {
        return mapper.queryOnWayByInventorListExport(param);
    }

    /**
     * 直采直销审核
     *
     * @param searchConditionList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> auditDirect(List<BMonitorVo> searchConditionList) {
        int updCount = 0;
        List<BMonitorEntity> list = mapper.selectIdsIn(searchConditionList);
        for (BMonitorEntity bMonitorEntity : list) {
            BScheduleEntity bScheduleEntity = bScheduleMapper.selectById(bMonitorEntity.getSchedule_id());

            if(bScheduleEntity == null
                    || (!bScheduleEntity.getType().equals(DictConstant.DICT_B_SCHEDULE_TYPE_4)
                    &&  !bScheduleEntity.getType().equals(DictConstant.DICT_B_SCHEDULE_TYPE_5))){
                    throw new BusinessException("直采直销异常，无法审核");
            }

            // 直采入库审核
            if (bScheduleEntity != null && bScheduleEntity.getType().equals(DictConstant.DICT_B_SCHEDULE_TYPE_4)) {
                checkLogic(bMonitorEntity, CheckResultAo.AUDIT_CHECK_TYPE_IN);
                // 监管任务审核页面，点击入出库审核，需判断该监管任务关联的入出库单若已作废需报提示，http://yirunscm.com:8080/issue/WMS-975
                BMonitorInUnloadVo monitorInVo = monitorInMapper.selectMonitorInUnloadByMonitorId(bMonitorEntity.getId());
                if (monitorInVo != null && monitorInVo.getIn_id() != null) {
                    BInEntity inEntity =  (BInEntity) ((BaseMapper)bInMapper).selectById(monitorInVo.getIn_id());
                    if (DictConstant.DICT_B_IN_STATUS_TWO.equals(inEntity.getStatus())) {
                        throw new BusinessException("入库单【" + inEntity.getCode() + "】已作废，无法审核");
                    }
                }

                bMonitorEntity.setAudit_status(DictConstant.DICT_B_MONITOR_AUDIT_STATUS_TWO);
                bMonitorEntity.setIn_audit_id(SecurityUtil.getStaff_id());
                bMonitorEntity.setIn_audit_time(LocalDateTime.now());
            }


            // 直销出库审核
            if (bScheduleEntity != null && bScheduleEntity.getType().equals(DictConstant.DICT_B_SCHEDULE_TYPE_5)) {
                checkLogic(bMonitorEntity, CheckResultAo.AUDIT_CHECK_TYPE_OUT);
                // 监管任务审核页面，点击入出库审核，需判断该监管任务关联的入出库单若已作废需报提示，http://yirunscm.com:8080/issue/WMS-975
                BMonitorOutDeliveryVo monitorOutVo = monitorOutMapper.selectOutDeliveryByMonitorId(bMonitorEntity.getId());
                if (monitorOutVo != null && monitorOutVo.getOut_id() != null) {
                    BOutEntity outEntity = bOutMapper.selectById(monitorOutVo.getOut_id());
                    if (DictConstant.DICT_B_OUT_STATUS_CANCEL.equals(outEntity.getStatus())) {
                        throw new BusinessException("出库单【" + outEntity.getCode() + "】已作废，无法审核");
                    }
                }

                bMonitorEntity.setAudit_status(DictConstant.DICT_B_MONITOR_AUDIT_STATUS_TWO);
                bMonitorEntity.setOut_audit_id(SecurityUtil.getStaff_id());
                bMonitorEntity.setOut_audit_time(LocalDateTime.now());
            }

            updCount = mapper.updateById(bMonitorEntity);

            if (updCount == 0) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }
        return UpdateResultUtil.OK(true);
    }

    @Override
    public List<BMonitorFileApiVo> exportDirect(List<BMonitorVo> searchCondition) {
        List<BMonitorFileApiVo> list = new ArrayList<>();

        BMonitorFileExportSettingsEntity settingsVo = exportSettingsService.getOne(Wrappers.<BMonitorFileExportSettingsEntity>lambdaQuery()
                .eq(BMonitorFileExportSettingsEntity::getStaff_id, SecurityUtil.getStaff_id())
                .eq(BMonitorFileExportSettingsEntity::getType, DictConstant.DICT_B_MONITOR_FILE_EXPORT_SETTINGS_TYPE_ONE));
        if (null == settingsVo) {
            throw new BusinessException("未配置导出文件");
        }
        JSONObject jsonObject = JSON.parseObject(settingsVo.getConfig_json());

        List<BMonitorFileExportVo> result = mapper.getMonitorFiles(searchCondition);
        for (BMonitorFileExportVo bMonitorFileExportVo : result) {
            list.addAll(getBMonitorDirectFileVo(bMonitorFileExportVo, jsonObject));
        }

        return list;
    }


    /**
     * 直采直销附件
     */
    private List<BMonitorFileApiVo> getBMonitorDirectFileVo(BMonitorFileExportVo vo, JSONObject jsonObject) {
        List<BMonitorFileApiVo> list = new ArrayList<>();

        BMonitorFileApiVo bMonitorFileVo;
        String dirName = "/" + LocalDateTimeUtils.formatTime(LocalDateTime.now(), DateTimeUtil.YYYYMMDD) + "/" + vo.getNo() + "-" + LocalDateTimeUtils.formatTime(vo.getC_time(), DateTimeUtil.YYYYMMDD) + "-" + vo.getCode();


        // （直采）空车出库-司机车头照片
        if (StringUtils.isNotEmpty(vo.getFile_35().getUrl()) && jsonObject.getJSONObject("file_35") != null && jsonObject.getJSONObject("file_35").getBoolean("value")) {
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
            bMonitorFileVo.setUrl(vo.getFile_35().getUrl());
            bMonitorFileVo.setFileName("空车出库-司机车头照片" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        // （直采）空车出库-司机驾驶证
        if (StringUtils.isNotEmpty(vo.getFile_41().getUrl()) && jsonObject.getJSONObject("file_41") != null && jsonObject.getJSONObject("file_41").getBoolean("value")) {
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
            bMonitorFileVo.setUrl(vo.getFile_41().getUrl());
            bMonitorFileVo.setFileName("空车出库-司机驾驶证" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        // （直采）空车出库-商品近照
        if (StringUtils.isNotEmpty(vo.getFile_42().getUrl()) && jsonObject.getJSONObject("file_42") != null && jsonObject.getJSONObject("file_42").getBoolean("value")) {
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
            bMonitorFileVo.setUrl(vo.getFile_42().getUrl());
            bMonitorFileVo.setFileName("空车出库-商品近照" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        // （直采）空车出库-磅单
        if (StringUtils.isNotEmpty(vo.getFile_37().getUrl()) && jsonObject.getJSONObject("file_37") != null && jsonObject.getJSONObject("file_37").getBoolean("value")) {
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
            bMonitorFileVo.setUrl(vo.getFile_37().getUrl());
            bMonitorFileVo.setFileName("空车出库-磅单" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        // （直销）重车出库-司机车头照片
        if (StringUtils.isNotEmpty(vo.getFile_17().getUrl()) && jsonObject.getJSONObject("file_17") != null && jsonObject.getJSONObject("file_17").getBoolean("value")) {
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
            bMonitorFileVo.setUrl(vo.getFile_17().getUrl());
            bMonitorFileVo.setFileName("重车出库-司机车头照片" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        // （直销）重车出库-司机驾驶证
        if (StringUtils.isNotEmpty(vo.getFile_38().getUrl()) && jsonObject.getJSONObject("file_38") != null && jsonObject.getJSONObject("file_38").getBoolean("value")) {
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
            bMonitorFileVo.setUrl(vo.getFile_38().getUrl());
            bMonitorFileVo.setFileName("重车出库-司机驾驶证" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        // （直销）重车出库-商品近照
        if (StringUtils.isNotEmpty(vo.getFile_43().getUrl()) && jsonObject.getJSONObject("file_43") != null && jsonObject.getJSONObject("file_43").getBoolean("value")) {
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
            bMonitorFileVo.setUrl(vo.getFile_43().getUrl());
            bMonitorFileVo.setFileName("重车出库-商品近照" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }

        // （直销）重车出库-磅单
        if (StringUtils.isNotEmpty(vo.getFile_19().getUrl()) && jsonObject.getJSONObject("file_19") != null && jsonObject.getJSONObject("file_19").getBoolean("value")) {
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
            bMonitorFileVo.setUrl(vo.getFile_19().getUrl());
            bMonitorFileVo.setFileName("重车出库-磅单" + lastName(bMonitorFileVo.getUrl()));
            list.add(bMonitorFileVo);
        }


        if (list.size() == 0) {
            bMonitorFileVo = new BMonitorFileApiVo();
            bMonitorFileVo.setDirName(dirName);
            list.add(bMonitorFileVo);
        }
        return list;
    }


    /**
     * 监管任务数据导出
     */
    @Override
    public List<BMonitorDirectExportVo> exportDirectData(BMonitorVo searchCondition) {
        return mapper.exportDirectData(searchCondition);
    }

    /**
     * 监管任务数据导出
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t4.in_warehouse_id,t4.out_warehouse_id")
    public List<BMonitorDirectExportVo> exportDirectDataAll(BMonitorVo searchCondition) {
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            Long count = mapper.selectPageMyCount(searchCondition);
            if (StringUtils.isNotNull(count) && count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.exportDirectDataAll(searchCondition);
    }

}
