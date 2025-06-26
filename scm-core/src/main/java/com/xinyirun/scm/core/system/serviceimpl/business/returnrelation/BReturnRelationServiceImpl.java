//package com.xinyirun.scm.core.system.serviceimpl.business.returnrelation;
//
//import cn.hutool.core.net.url.UrlBuilder;
//import com.alibaba.fastjson2.JSONObject;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.google.common.collect.Lists;
//import com.xinyirun.scm.bean.api.vo.business.ApiCanceledDataVo;
//import com.xinyirun.scm.bean.api.vo.business.ApiCanceledVo;
//import com.xinyirun.scm.bean.entity.busniess.in.BInEntity;
//import com.xinyirun.scm.bean.entity.busniess.inplan.BInPlanDetailEntity;
//import com.xinyirun.scm.bean.entity.busniess.inplan.BInPlanEntity;
//import com.xinyirun.scm.bean.entity.busniess.out.BOutEntity;
//import com.xinyirun.scm.bean.entity.busniess.out.BOutPlanDetailEntity;
//import com.xinyirun.scm.bean.entity.busniess.returnrelation.BReturnRelationEntity;
//import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigEntity;
//import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
//import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
//import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
//import com.xinyirun.scm.bean.system.vo.business.monitor.*;
//import com.xinyirun.scm.bean.system.vo.business.order.BOrderVo;
//import com.xinyirun.scm.bean.system.vo.business.returnrelation.BReturnRelationExportVo;
//import com.xinyirun.scm.bean.system.vo.business.returnrelation.BReturnRelationVo;
//import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyVo;
//import com.xinyirun.scm.bean.system.vo.master.warhouse.MWarehouseLocationBinVo;
//import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigDetailVo;
//import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
//import com.xinyirun.scm.bean.utils.security.SecurityUtil;
//import com.xinyirun.scm.common.constant.DictConstant;
//import com.xinyirun.scm.common.constant.SystemConstants;
//import com.xinyirun.scm.common.exception.system.BusinessException;
//import com.xinyirun.scm.common.utils.DateUtils;
//import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
//import com.xinyirun.scm.common.utils.string.StringUtils;
//import com.xinyirun.scm.core.system.mapper.business.inplan.BInMapper;
//import com.xinyirun.scm.core.system.mapper.business.inplan.BInPlanDetailMapper;
//import com.xinyirun.scm.core.system.mapper.business.inplan.BInPlanMapper;
//import com.xinyirun.scm.core.system.mapper.business.monitor.BMonitorInMapper;
//import com.xinyirun.scm.core.system.mapper.business.monitor.BMonitorMapper;
//import com.xinyirun.scm.core.system.mapper.business.monitor.BMonitorOutMapper;
//import com.xinyirun.scm.core.system.mapper.business.order.BOrderMapper;
//import com.xinyirun.scm.core.system.mapper.business.out.BOutMapper;
//import com.xinyirun.scm.core.system.mapper.business.out.BOutPlanDetailMapper;
//import com.xinyirun.scm.core.system.mapper.business.returnrelation.BReturnRelationMapper;
//import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
//import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
//import com.xinyirun.scm.core.system.service.base.v1.common.inventory.ICommonInventoryLogicService;
//import com.xinyirun.scm.core.system.service.business.in.IBInService;
//import com.xinyirun.scm.core.system.service.business.returnrelation.IBReturnRelationService;
//import com.xinyirun.scm.core.system.service.master.warehouse.IMWarehouseService;
//import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigDetailService;
//import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigService;
//import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
//import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBDailyProductV2Service;
//import com.xinyirun.scm.core.system.service.track.bestfriend.IBTrackBestFriendService;
//import com.xinyirun.scm.core.system.serviceimpl.common.autocode.*;
//import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
//import org.apache.commons.collections.CollectionUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.math.BigDecimal;
//import java.net.InetAddress;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
///**
// * <p>
// * 退货表 服务实现类
// * </p>
// *
// * @author xinyirun
// * @since 2024-07-26
// */
//@Service
//public class BReturnRelationServiceImpl extends ServiceImpl<BReturnRelationMapper, BReturnRelationEntity> implements IBReturnRelationService {
//
//    @Autowired
//    private ICommonInventoryLogicService iCommonInventoryLogicService;
//
//    @Autowired
//    private BInPlanDetailMapper inPlanDetailMapper;
//
//    @Autowired
//    protected RestTemplate restTemplate;
//
//    @Autowired
//    public WebClient webClient;
//
//    @Autowired
//    IBTrackBestFriendService bestFriendService;
//
//    @Autowired
//    private BOutPlanDetailMapper bOutPlanDetailMapper;
//
//    @Autowired
//    private BInPlanDetailAutoCodeServiceImpl bInPlanDetailAutoCodeService;
//
//    @Autowired
//    private BInAutoCodeServiceImpl bInAutoCodeService;
//
//    @Autowired
//    private BInPlanAutoCodeServiceImpl bInPlanAutoCodeService;
//
//    @Autowired
//    private BInPlanMapper inPlanMapper;
//
//    @Autowired
//    private IMWarehouseService imWarehouseService;
//
//    @Autowired
//    private BReturnRelationMapper bReturnRelationMapper;
//
//    @Autowired
//    private BMonitorMapper bMonitorMapper;
//
//    @Autowired
//    private BMonitorOutMapper monitorOutMapper;
//
//    @Autowired
//    private BMonitorInMapper monitorInMapper;
//
//    @Autowired
//    private BInMapper inMapper;
//
//    @Autowired
//    private BOutMapper outMapper;
//
//    @Autowired
//    private SFileMapper fileMapper;
//
//    @Autowired
//    private SFileInfoMapper fileInfoMapper;
//
//    @Autowired
//    private ISAppConfigDetailService isAppConfigDetailService;
//
//    @Autowired
//    private ISAppConfigService isAppConfigService;
//
//    @Autowired
//    private BReturnRelationAutoCodeServiceImpl bReturnRelationAutoCodeService;
//
//    @Value("${server.port}")
//    private int port;
//
//    @Autowired
//    private IBInService ibInService;
//
//    @Autowired
//    private ISBDailyProductV2Service dailyProductV2Service;
//
//    @Autowired
//    private ISConfigService isConfigService;
//
//    @Autowired
//    private BOrderMapper bOrderMapper;
//
//    /**
//     * 监管任务新增退货单
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public BMonitorVo insertReturnRelation(BMonitorVo searchCondition) {
//        int eId = SecurityUtil.getStaff_id().intValue();
//        BigDecimal return_qty = searchCondition.getReturnRelationVo().getQty();
//        String quantityReason = searchCondition.getReturnRelationVo().getQuantity_reason();
//        List<SFileInfoVo> files = searchCondition.getReturnRelationVo().getFiles();
//
//        BMonitorVo bMonitorVo = bMonitorMapper.selectId(searchCondition.getId());
//        BigDecimal decimal = BigDecimal.ZERO;
//
//        // 获取监管出库详情
//        BMonitorOutDeliveryVo monitorOutVo = monitorOutMapper.selectOutDeliveryByMonitorId(searchCondition.getId());
//        if (monitorOutVo != null && monitorOutVo.getNet_weight() != null) {
//            decimal = monitorOutVo.getNet_weight();
//        }
//
//        // 获取监管入库详情
//        BMonitorInUnloadVo monitorInVo = monitorInMapper.selectMonitorInUnloadByMonitorId(searchCondition.getId());
//        if (monitorInVo != null && monitorInVo.getNet_weight() != null) {
//            decimal = decimal.subtract(monitorInVo.getNet_weight());
//        }
//
//        // 1.不可填负数、0，且<监管任务.出库净重(吨)-监管任务.入库净重(吨)
//        if (return_qty == null || return_qty == BigDecimal.ZERO || return_qty.compareTo(decimal) > 0) {
//            throw new BusinessException("不可填负数、0，且<监管任务.出库净重(吨)-监管任务.入库净重(吨)");
//        }
//
//        //2.判断关联非作废的关联退货单
//        BReturnRelationEntity returnRelationEntity = bReturnRelationMapper.selectBySerialIdAndSerialType(searchCondition.getId(), SystemConstants.SERIAL_TYPE.B_MONITOR);
//        if (returnRelationEntity != null) {
//            throw new BusinessException("存在关联退货单,新增失败");
//        }
//
//        // 3.出库单已结算，提示“该监管任务对应出库单xxxxx已被结算，无法进行退货”；
//        if ((monitorOutVo.getIs_settled() != null && monitorOutVo.getIs_settled().equals("1")) || callOutCanceledAppCode10Api(monitorOutVo)) {
//            throw new BusinessException(String.format("该监管任务对应出库单%s已被结算，无法进行退货", monitorOutVo.getOut_code()));
//        }
//
//        // 4.若监管任务已被业务中台结算，点击确定，提示“该监管任务已被结算，无法进行退货”
//        if ((bMonitorVo.getSettlement_status() != null && bMonitorVo.getSettlement_status().equals("1")) || callMonitorCanceledAppCode120Api(bMonitorVo)) {
//            throw new BusinessException("该监管任务已被结算，无法进行退货");
//        }
//
//        // 5.生成"审核通过"的入库类型是监管退货的入库计划、入库单，该监管退货的入库计划、入库单的委托方、货主、仓库、合同信息取值该监管任务.物流订单对应的发货委托方、发货货主、发货仓库、合同信息；
//
//
//        // 更新出库单
//        BOutEntity outEntity = outMapper.selectById(monitorOutVo.getOut_id());
//        outEntity.setReturn_qty(return_qty);
//        outMapper.updateById(outEntity);
//
//        // 统计所有出库单的退货数量
//        BigDecimal count = outMapper.selectByPlanIdCount(outEntity.getPlan_id());
//
//        // 更新出库计划的退货数量
//        BOutPlanDetailEntity bOutPlanDetailEntity = bOutPlanDetailMapper.selectMonitorOutId(monitorOutVo.getId());
//        bOutPlanDetailEntity.setReturn_qty(count);
//        bOutPlanDetailMapper.updateById(bOutPlanDetailEntity);
//
//        //合同信息
//        BOrderVo bOrderVo = new BOrderVo();
//        bOrderVo.setId(bMonitorVo.getOrder_id());
//        bOrderVo.setContract_no(bMonitorVo.getContract_no());
//        bOrderVo = bOrderMapper.selectDetailByContractNo(bOrderVo);
//
//        // 增加入库计划
//        BInPlanEntity inPlanEntity = new BInPlanEntity();
//        inPlanEntity.setCode(bInPlanAutoCodeService.autoCode().getCode());
//        inPlanEntity.setOwner_id(outEntity.getOwner_id());
//        inPlanEntity.setOwner_code(outEntity.getOwner_code());
//        inPlanEntity.setConsignor_id(outEntity.getConsignor_id());
//        inPlanEntity.setConsignor_code(outEntity.getConsignor_code());
//        inPlanEntity.setPlan_time(LocalDateTime.now());
//        //inPlanEntity.setWaybill_code(monitorOutVo.getWaybill_code());
//        inPlanEntity.setType(DictConstant.DICT_B_IN_PLAN_TYPE_JG_TH);
//        inPlanMapper.insert(inPlanEntity);
//
//        // 增加入库计划附表
//        BInPlanDetailEntity inPlanDetail = new BInPlanDetailEntity();
//        inPlanDetail.setNo(1);
//        inPlanDetail.setStatus(DictConstant.DICT_B_IN_STATUS_PASSED);
//        inPlanDetail.setPre_status(DictConstant.DICT_B_IN_STATUS_PASSED);
//        inPlanDetail.setCode(bInPlanDetailAutoCodeService.autoCode().getCode());
//        inPlanDetail.setType_gauge(bOutPlanDetailEntity.getType_gauge());
//        inPlanDetail.setAlias(bOutPlanDetailEntity.getAlias());
//        inPlanDetail.setSku_id(bOutPlanDetailEntity.getSku_id());
//        inPlanDetail.setSku_code(bOutPlanDetailEntity.getSku_code());
//        inPlanDetail.setOrder_goods_code(bOutPlanDetailEntity.getOrder_goods_code());
//        inPlanDetail.setPrice(bOutPlanDetailEntity.getPrice() != null ?bOutPlanDetailEntity.getPrice():BigDecimal.ZERO);
//        inPlanDetail.setCount(return_qty);
//        inPlanDetail.setWeight(return_qty);
//        inPlanDetail.setVolume(BigDecimal.ZERO);
//        inPlanDetail.setWarehouse_id(bOutPlanDetailEntity.getWarehouse_id());
//        inPlanDetail.setUnit_id(bOutPlanDetailEntity.getUnit_id());
//        inPlanDetail.setPending_count(BigDecimal.ZERO);
//        inPlanDetail.setPending_weight(BigDecimal.ZERO);
//        inPlanDetail.setPending_volume(BigDecimal.ZERO);
//        inPlanDetail.setHas_handle_count(return_qty);
//        inPlanDetail.setHas_handle_weight(return_qty);
//        inPlanDetail.setHas_handle_volume(BigDecimal.ZERO);
//        inPlanDetail.setAudit_id(eId);
//        inPlanDetail.setAudit_dt(LocalDateTime.now());
//        inPlanDetail.setIs_agree(Boolean.TRUE);
//        inPlanDetail.setOver_inventory_policy(bOutPlanDetailEntity.getOver_inventory_policy());
//        inPlanDetail.setOver_inventory_upper(bOutPlanDetailEntity.getOver_inventory_upper());
//        inPlanDetail.setOver_inventory_lower(bOutPlanDetailEntity.getOver_inventory_lower());
//        inPlanDetail.setPlan_id(inPlanEntity.getId());
//        inPlanDetail.setOrder_id(bOrderVo != null ? bOrderVo.getSerial_id() : null);
//        inPlanDetail.setOrder_type(bOrderVo != null ? bOrderVo.getSerial_type() : null);
//        inPlanDetail.setOrder_detail_no(bOrderVo != null ? bOrderVo.getOrder_detail_no() : null);
//        inPlanDetailMapper.insert(inPlanDetail);
//
//        // 增加入库单
//        BInEntity bInEntity = new BInEntity();
//        bInEntity.setCode(bInAutoCodeService.autoCode().getCode());
//        bInEntity.setType(DictConstant.DICT_B_IN_TYPE_JG_TH);
//        bInEntity.setStatus(DictConstant.DICT_B_IN_STATUS_SUBMITTED);
//        bInEntity.setPre_status(DictConstant.DICT_B_IN_STATUS_SUBMITTED);
//        bInEntity.setIs_settled(Boolean.FALSE);
//        // bInEntity.setSettle_code();
//        bInEntity.setOwner_id(inPlanEntity.getOwner_id());
//        bInEntity.setPlan_id(inPlanEntity.getId());
//        bInEntity.setPlan_detail_id(inPlanDetail.getId());
//        bInEntity.setConsignor_id(inPlanEntity.getConsignor_id());
//        bInEntity.setOwner_code(inPlanEntity.getOwner_code());
//        bInEntity.setConsignor_code(inPlanEntity.getConsignor_code());
//        bInEntity.setWarehouse_id(inPlanDetail.getWarehouse_id());
//        //  保存换算比例
//
//        MWarehouseLocationBinVo warehouseLocationBinVo = imWarehouseService.selectWarehouseLocationBin(inPlanDetail.getWarehouse_id());
//        bInEntity.setLocation_id(warehouseLocationBinVo.getLocation_id());
//        bInEntity.setBin_id(warehouseLocationBinVo.getBin_id());
//        bInEntity.setSku_id(inPlanDetail.getSku_id());
//        bInEntity.setSku_code(inPlanDetail.getSku_code());
//        bInEntity.setPlan_count(inPlanDetail.getCount());
//        bInEntity.setPlan_weight(inPlanDetail.getPending_weight());
//        bInEntity.setPlan_volume(inPlanDetail.getPending_volume());
//        bInEntity.setActual_count(inPlanDetail.getCount());
//        bInEntity.setActual_weight(inPlanDetail.getCount());
//        bInEntity.setActual_volume(inPlanDetail.getPending_volume());
//        bInEntity.setPrice(inPlanDetail.getPrice());
//        bInEntity.setAmount(inPlanDetail.getPrice().multiply(inPlanDetail.getCount()));
//        bInEntity.setUnit_id(inPlanDetail.getUnit_id());
//        bInEntity.setTgt_unit_id(outEntity.getTgt_unit_id());
//        bInEntity.setCalc(outEntity.getCalc());
//        bInEntity.setInbound_time(LocalDateTime.now());
//        //bInEntity.setReceive_order_id();
//        //bInEntity.setInventory_account_id();
//        bInEntity.setE_id(eId);
//        bInEntity.setE_dt(LocalDateTime.now());
//        //bInEntity.setVehicle_no();
//        bInEntity.setTare_weight(BigDecimal.ZERO);
//        bInEntity.setGross_weight(inPlanDetail.getWeight());
//        inMapper.insert(bInEntity);
//
//        // 增加入库单附表
//        BInExtraEntity bInExtraEntity = new BInExtraEntity();
//        bInExtraEntity.setIn_id(bInEntity.getId());
//        bInExtraEntity.setPrice(inPlanDetail.getPrice());
//        bInExtraEntity.setTotal_price(inPlanDetail.getPrice().multiply(inPlanDetail.getCount()));
//        bInExtraEntity.setPrimary_quantity(inPlanDetail.getCount());
//        bInExtraEntity.setCar_count(1);
//        bInExtraEntity.setOrder_id(bOrderVo != null ? bOrderVo.getSerial_id() : null);
//        bInExtraEntity.setOrder_type(bOrderVo != null ? bOrderVo.getSerial_type() : null);
//        bInExtraMapper.insert(bInExtraEntity);
//
//        // 新增退货单
//        BReturnRelationEntity bReturnRelationEntity = new BReturnRelationEntity();
//        bReturnRelationEntity.setCode(bReturnRelationAutoCodeService.autoCode().getCode());
//        bReturnRelationEntity.setIn_plan_id(inPlanEntity.getId());
//        bReturnRelationEntity.setIn_plan_code(inPlanEntity.getCode());
//        bReturnRelationEntity.setIn_id(bInEntity.getId());
//        bReturnRelationEntity.setIn_code(bInEntity.getCode());
//        bReturnRelationEntity.setQty(return_qty);
//        bReturnRelationEntity.setQuantity_reason(quantityReason);
//        bReturnRelationEntity.setUnit_id(outEntity.getUnit_id());
//        bReturnRelationEntity.setSerial_id(bMonitorVo.getId());
//        bReturnRelationEntity.setSerial_code(bMonitorVo.getCode());
//        bReturnRelationEntity.setSerial_type(SystemConstants.SERIAL_TYPE.B_MONITOR);
//        bReturnRelationEntity.setSerial_type_name("监管退货");
//        bReturnRelationEntity.setStatus("1");
//        bReturnRelationMapper.insert(bReturnRelationEntity);
//
//        if (CollectionUtils.isNotEmpty(files)) {
//            SFileEntity fileEntity = new SFileEntity();
//            fileEntity.setSerial_type(SystemConstants.SERIAL_TYPE.B_RETURN_RELATION);
//            fileEntity.setSerial_id(bReturnRelationEntity.getId());
//            fileMapper.insert(fileEntity);
//
//            // 附件保存
//            files.forEach(k -> {
//
//                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
//                fileInfoEntity.setF_id(fileEntity.getId());
//                fileInfoEntity.setUrl(k.getUrl());
//                fileInfoEntity.setFile_name(k.getFileName());
//                fileInfoEntity.setInternal_url(k.getInternal_url());
//                fileInfoMapper.insert(fileInfoEntity);
//            });
//
//            bReturnRelationEntity.setFiles_id(fileEntity.getId());
//            bReturnRelationMapper.updateById(bReturnRelationEntity);
//        }
//
//        // 调用预扣减库存
//        iCommonInventoryLogicService.updWmsStockByInBill(bInEntity.getId());
//
//        //入库单状态审核通过，调用共通
//        bInEntity.setStatus(DictConstant.DICT_B_IN_STATUS_PASSED);
//        bInEntity.setE_dt(LocalDateTime.now());
//        bInEntity.setInventory_account_id(null);
//        bInEntity.setE_opinion(DictConstant.DICT_AUDIT_INFO_TYPE_FALSE);
//        BInEntity newBInEntity1 = inMapper.selectById(bInEntity.getId());
//        bInEntity.setDbversion(newBInEntity1.getDbversion());
//        inMapper.updateById(bInEntity);
//
//        iCommonInventoryLogicService.updWmsStockByInBill(bInEntity.getId());
//        return searchCondition;
//    }
//
//    /**
//     * 修改退货单
//     *
//     * @param searchCondition
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public BMonitorVo updateReturnRelation(BMonitorVo searchCondition) {
//        int eId = SecurityUtil.getStaff_id().intValue();
//        BigDecimal return_qty = searchCondition.getReturnRelationVo().getQty();
//        String quantityReason = searchCondition.getReturnRelationVo().getQuantity_reason();
//        List<SFileInfoVo> files = searchCondition.getReturnRelationVo().getFiles();
//
//        BMonitorVo bMonitorVo = bMonitorMapper.selectId(searchCondition.getId());
//        BigDecimal decimal = BigDecimal.ZERO;
//
//        // 获取监管出库详情
//        BMonitorOutDeliveryVo monitorOutVo = monitorOutMapper.selectOutDeliveryByMonitorId(searchCondition.getId());
//        if (monitorOutVo != null && monitorOutVo.getNet_weight() != null) {
//            decimal = monitorOutVo.getNet_weight();
//        }
//
//        // 获取监管入库详情
//        BMonitorInUnloadVo monitorInVo = monitorInMapper.selectMonitorInUnloadByMonitorId(searchCondition.getId());
//        if (monitorInVo != null && monitorInVo.getNet_weight() != null) {
//            decimal = decimal.subtract(monitorInVo.getNet_weight());
//        }
//
//        // 1.不可填负数、0，且<监管任务.出库净重(吨)-监管任务.入库净重(吨)
//        if (return_qty == null || return_qty.compareTo(decimal) >= 0) {
//            throw new BusinessException("不可填负数、0，且<监管任务.出库净重(吨)-监管任务.入库净重(吨)");
//        }
//
//        // 2.出库单已结算，提示“该监管任务对应出库单xxxxx已被结算，无法进行退货”；
//        if ((monitorOutVo.getIs_settled() != null && monitorOutVo.getIs_settled().equals("1")) || callOutCanceledAppCode10Api(monitorOutVo)) {
//            throw new BusinessException(String.format("该监管任务对应出库单%s已被结算，无法进行退货", monitorOutVo.getOut_code()));
//        }
//
//        // 3.若监管任务已被业务中台结算，点击确定，提示“该监管任务已被结算，无法进行退货”
//        if ((bMonitorVo.getSettlement_status() != null && bMonitorVo.getSettlement_status().equals("1")) || callMonitorCanceledAppCode120Api(bMonitorVo)) {
//            throw new BusinessException("该监管任务已被结算，无法进行退货");
//        }
//
//        // 4.作废之前的入库计划，入库单，退货单
//        toVoidInPlan(searchCondition.getId());
//
//        // 更新出库单
//        BOutEntity outEntity = outMapper.selectById(monitorOutVo.getOut_id());
//        outEntity.setReturn_qty(return_qty);
//        outMapper.updateById(outEntity);
//
//        // 统计所有出库单的退货数量
//        BigDecimal count = outMapper.selectByPlanIdCount(outEntity.getPlan_id());
//
//        // 更新出库计划的退货数量
//        BOutPlanDetailEntity bOutPlanDetailEntity = bOutPlanDetailMapper.selectMonitorOutId(monitorOutVo.getId());
//        bOutPlanDetailEntity.setReturn_qty(count);
//        bOutPlanDetailMapper.updateById(bOutPlanDetailEntity);
//
//        // 5.生成"审核通过"的入库类型是监管退货的入库计划、入库单，该监管退货的入库计划、入库单的委托方、货主、仓库、合同信息取值该监管任务.物流订单对应的发货委托方、发货货主、发货仓库、合同信息；
//        if (return_qty != BigDecimal.ZERO) {
//
//            //合同信息
//            BOrderVo bOrderVo = new BOrderVo();
//            bOrderVo.setId(bMonitorVo.getOrder_id());
//            bOrderVo.setContract_no(bMonitorVo.getContract_no());
//            bOrderVo = bOrderMapper.selectDetailByContractNo(bOrderVo);
//
//            // 增加入库计划
//            BInPlanEntity inPlanEntity = new BInPlanEntity();
//            inPlanEntity.setCode(bInPlanAutoCodeService.autoCode().getCode());
//            inPlanEntity.setOwner_id(outEntity.getOwner_id());
//            inPlanEntity.setOwner_code(outEntity.getOwner_code());
//            inPlanEntity.setConsignor_id(outEntity.getConsignor_id());
//            inPlanEntity.setConsignor_code(outEntity.getConsignor_code());
//            inPlanEntity.setPlan_time(LocalDateTime.now());
//            //inPlanEntity.setWaybill_code(monitorOutVo.getWaybill_code());
//            inPlanEntity.setType(DictConstant.DICT_B_IN_PLAN_TYPE_JG_TH);
//            inPlanMapper.insert(inPlanEntity);
//
//            // 增加入库计划附表
//            BInPlanDetailEntity inPlanDetail = new BInPlanDetailEntity();
//            inPlanDetail.setNo(1);
//            inPlanDetail.setStatus(DictConstant.DICT_B_IN_STATUS_PASSED);
//            inPlanDetail.setPre_status(DictConstant.DICT_B_IN_STATUS_PASSED);
//            inPlanDetail.setCode(bInPlanDetailAutoCodeService.autoCode().getCode());
//            inPlanDetail.setType_gauge(bOutPlanDetailEntity.getType_gauge());
//            inPlanDetail.setAlias(bOutPlanDetailEntity.getAlias());
//            inPlanDetail.setSku_id(bOutPlanDetailEntity.getSku_id());
//            inPlanDetail.setSku_code(bOutPlanDetailEntity.getSku_code());
//            inPlanDetail.setOrder_goods_code(bOutPlanDetailEntity.getOrder_goods_code());
//            inPlanDetail.setPrice(bOutPlanDetailEntity.getPrice()!=null?bOutPlanDetailEntity.getPrice():BigDecimal.ZERO);
//            inPlanDetail.setCount(return_qty);
//            inPlanDetail.setWeight(return_qty.multiply(outEntity.getCalc()));
//            inPlanDetail.setVolume(BigDecimal.ZERO);
//            inPlanDetail.setWarehouse_id(bOutPlanDetailEntity.getWarehouse_id());
//            inPlanDetail.setUnit_id(bOutPlanDetailEntity.getUnit_id());
//            inPlanDetail.setPending_count(BigDecimal.ZERO);
//            inPlanDetail.setPending_weight(BigDecimal.ZERO);
//            inPlanDetail.setPending_volume(BigDecimal.ZERO);
//            inPlanDetail.setHas_handle_count(return_qty);
//            inPlanDetail.setHas_handle_weight(return_qty.multiply(outEntity.getCalc()));
//            inPlanDetail.setHas_handle_volume(BigDecimal.ZERO);
//            inPlanDetail.setAudit_id(eId);
//            inPlanDetail.setAudit_dt(LocalDateTime.now());
//            inPlanDetail.setIs_agree(Boolean.TRUE);
//            inPlanDetail.setOver_inventory_policy(bOutPlanDetailEntity.getOver_inventory_policy());
//            inPlanDetail.setOver_inventory_upper(bOutPlanDetailEntity.getOver_inventory_upper());
//            inPlanDetail.setOver_inventory_lower(bOutPlanDetailEntity.getOver_inventory_lower());
//            inPlanDetail.setPlan_id(inPlanEntity.getId());
//            inPlanDetail.setOrder_id(bOrderVo != null ? bOrderVo.getSerial_id() : null);
//            inPlanDetail.setOrder_type(bOrderVo != null ? bOrderVo.getSerial_type() : null);
//            inPlanDetail.setOrder_detail_no(bOrderVo != null ? bOrderVo.getOrder_detail_no() : null);
//            inPlanDetailMapper.insert(inPlanDetail);
//
//            // 增加入库单
//            BInEntity bInEntity = new BInEntity();
//            bInEntity.setCode(bInAutoCodeService.autoCode().getCode());
//            bInEntity.setType(DictConstant.DICT_B_IN_TYPE_JG_TH);
//            bInEntity.setStatus(DictConstant.DICT_B_IN_STATUS_SUBMITTED);
//            bInEntity.setPre_status(DictConstant.DICT_B_IN_STATUS_SUBMITTED);
//            bInEntity.setIs_settled(Boolean.FALSE);
//            // bInEntity.setSettle_code();
//            bInEntity.setOwner_id(inPlanEntity.getOwner_id());
//            bInEntity.setPlan_id(inPlanEntity.getId());
//            bInEntity.setPlan_detail_id(inPlanDetail.getId());
//            bInEntity.setConsignor_id(inPlanEntity.getConsignor_id());
//            bInEntity.setOwner_code(inPlanEntity.getOwner_code());
//            bInEntity.setConsignor_code(inPlanEntity.getConsignor_code());
//            bInEntity.setWarehouse_id(inPlanDetail.getWarehouse_id());
//
//            MWarehouseLocationBinVo warehouseLocationBinVo = imWarehouseService.selectWarehouseLocationBin(inPlanDetail.getWarehouse_id());
//            bInEntity.setLocation_id(warehouseLocationBinVo.getLocation_id());
//            bInEntity.setBin_id(warehouseLocationBinVo.getBin_id());
//            bInEntity.setSku_id(inPlanDetail.getSku_id());
//            bInEntity.setSku_code(inPlanDetail.getSku_code());
//            bInEntity.setPlan_count(inPlanDetail.getCount());
//            bInEntity.setPlan_weight(inPlanDetail.getPending_weight());
//            bInEntity.setPlan_volume(inPlanDetail.getPending_volume());
//            bInEntity.setActual_count(inPlanDetail.getCount());
//            bInEntity.setActual_weight(inPlanDetail.getWeight());
//            bInEntity.setActual_volume(inPlanDetail.getPending_volume());
//            bInEntity.setPrice(inPlanDetail.getPrice());
//            bInEntity.setAmount(inPlanDetail.getPrice().multiply(inPlanDetail.getCount()));
//            bInEntity.setUnit_id(inPlanDetail.getUnit_id());
//            bInEntity.setTgt_unit_id(outEntity.getTgt_unit_id());
//            bInEntity.setCalc(outEntity.getCalc());
//            bInEntity.setInbound_time(LocalDateTime.now());
//            //bInEntity.setReceive_order_id();
//            //bInEntity.setInventory_account_id();
//            bInEntity.setE_id(eId);
//            bInEntity.setE_dt(LocalDateTime.now());
//            //bInEntity.setVehicle_no();
//            bInEntity.setTare_weight(BigDecimal.ZERO);
//            bInEntity.setGross_weight(inPlanDetail.getWeight());
//            inMapper.insert(bInEntity);
//
//            // 增加入库单附表
//            BInExtraEntity bInExtraEntity = new BInExtraEntity();
//            bInExtraEntity.setIn_id(bInEntity.getId());
//            bInExtraEntity.setPrice(inPlanDetail.getPrice());
//            bInExtraEntity.setTotal_price(inPlanDetail.getPrice().multiply(inPlanDetail.getCount()));
//            bInExtraEntity.setPrimary_quantity(inPlanDetail.getCount());
//            bInExtraEntity.setCar_count(1);
//            bInExtraEntity.setOrder_id(bOrderVo != null ? bOrderVo.getSerial_id() : null);
//            bInExtraEntity.setOrder_type(bOrderVo != null ? bOrderVo.getSerial_type() : null);
//            bInExtraMapper.insert(bInExtraEntity);
//
//            // 新增退货单
//            BReturnRelationEntity bReturnRelationEntity = new BReturnRelationEntity();
//            bReturnRelationEntity.setCode(bReturnRelationAutoCodeService.autoCode().getCode());
//            bReturnRelationEntity.setIn_plan_id(inPlanEntity.getId());
//            bReturnRelationEntity.setIn_plan_code(inPlanEntity.getCode());
//            bReturnRelationEntity.setIn_id(bInEntity.getId());
//            bReturnRelationEntity.setIn_code(bInEntity.getCode());
//            bReturnRelationEntity.setQty(return_qty);
//            bReturnRelationEntity.setQuantity_reason(quantityReason);
//            bReturnRelationEntity.setUnit_id(outEntity.getUnit_id());
//            bReturnRelationEntity.setSerial_id(bMonitorVo.getId());
//            bReturnRelationEntity.setSerial_code(bMonitorVo.getCode());
//            bReturnRelationEntity.setSerial_type(SystemConstants.SERIAL_TYPE.B_MONITOR);
//            bReturnRelationEntity.setSerial_type_name("监管退货");
//            bReturnRelationEntity.setStatus("1");
//            bReturnRelationMapper.insert(bReturnRelationEntity);
//
//            if (CollectionUtils.isNotEmpty(files)) {
//                SFileEntity fileEntity = new SFileEntity();
//                fileEntity.setSerial_type(SystemConstants.SERIAL_TYPE.B_RETURN_RELATION);
//                fileEntity.setSerial_id(bReturnRelationEntity.getId());
//                fileMapper.insert(fileEntity);
//
//                // 附件保存
//                files.forEach(k -> {
//
//                    SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
//                    fileInfoEntity.setF_id(fileEntity.getId());
//                    fileInfoEntity.setUrl(k.getUrl());
//                    fileInfoEntity.setFile_name(k.getFileName());
//                    fileInfoEntity.setInternal_url(k.getInternal_url());
//                    fileInfoMapper.insert(fileInfoEntity);
//                });
//
//                bReturnRelationEntity.setFiles_id(fileEntity.getId());
//                bReturnRelationMapper.updateById(bReturnRelationEntity);
//            }
//
//            // 调用预扣减库存
//            iCommonInventoryLogicService.updWmsStockByInBill(bInEntity.getId());
//
//            //入库单状态审核通过，调用共通
//            bInEntity.setStatus(DictConstant.DICT_B_IN_STATUS_PASSED);
//            bInEntity.setE_dt(LocalDateTime.now());
//            bInEntity.setInventory_account_id(null);
//            bInEntity.setE_opinion(DictConstant.DICT_AUDIT_INFO_TYPE_FALSE);
//            BInEntity newBInEntity1 = inMapper.selectById(bInEntity.getId());
//            bInEntity.setDbversion(newBInEntity1.getDbversion());
//            inMapper.updateById(bInEntity);
//
//            iCommonInventoryLogicService.updWmsStockByInBill(bInEntity.getId());
//
//        }
//
//        return searchCondition;
//    }
//
//    /**
//     * 分页列表
//     */
//    @Override
//    public IPage<BReturnRelationVo> selectPageList(BReturnRelationVo searchCondition) {
//        // 分页条件
//        Page<BInEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
//
//        // 替换分页插件自动count sql 因为该sql执行速度非常慢
//        pageCondition.setCountId("selectPageMyCount");
//
//        // 通过page进行排序
//        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
//
//        return bReturnRelationMapper.selectPageList(pageCondition, searchCondition);
//    }
//
//    /**
//     * 查询详情
//     */
//    @Override
//    public BReturnRelationVo getDetail(BReturnRelationVo returnRelationVo) {
//        // 获取退货单信息
//        BReturnRelationVo BReturnRelationVo = bReturnRelationMapper.getDetail(returnRelationVo.getId());
//        if (BReturnRelationVo !=null){
//            if(BReturnRelationVo.getFiles_id() != null) {
//                SFileEntity file = fileMapper.selectById(BReturnRelationVo.getFiles_id());
//                BReturnRelationVo.setFiles(new ArrayList<>());
//                List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id",file.getId()));
//                for(SFileInfoEntity fileInfo:fileInfos) {
//                    SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
//                    fileInfoVo.setFileName(fileInfo.getFile_name());
//                    BReturnRelationVo.getFiles().add(fileInfoVo);
//                }
//            }
//        }
//        return BReturnRelationVo;
//    }
//
//    /**
//     * 部分导出
//     *
//     * @param searchCondition
//     */
//    @Override
//    public List<BReturnRelationExportVo> selectExportList(List<BReturnRelationVo> searchCondition) {
//        return bReturnRelationMapper.selectExportList(searchCondition);
//    }
//
//    /**
//     * 全部导出
//     *
//     * @param searchCondition
//     */
//    @Override
//    public List<BReturnRelationExportVo> selectExportAll(BReturnRelationVo searchCondition) {
//        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
//        // 导出限制开关
//        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
//        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
//            Long count = bReturnRelationMapper.selectPageMyCount(searchCondition);
//            if (StringUtils.isNotNull(count) && count > Long.parseLong(sConfigEntity.getExtra1())) {
//                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
//            }
//        }
//        return bReturnRelationMapper.selectExportAll(searchCondition);
//    }
//
//    /**
//     * 作废之前的入库计划，入库单，退货单
//     */
//    @Override
//    public void toVoidInPlan(Integer id) {
//        // 作废退货单
//        BReturnRelationEntity returnRelationEntity = bReturnRelationMapper.selectBySerialIdAndSerialType(id, SystemConstants.SERIAL_TYPE.B_MONITOR);
//        if (returnRelationEntity==null)return;
//        returnRelationEntity.setStatus("2");
//        bReturnRelationMapper.updateById(returnRelationEntity);
//
//        // 作废入库计划
//        BInPlanDetailEntity inPlanDetailEntity = inPlanDetailMapper.selectPlanId(returnRelationEntity.getIn_plan_id());
//        if (inPlanDetailEntity==null)return;
//        inPlanDetailEntity.setPre_status(inPlanDetailEntity.getStatus());
//        inPlanDetailEntity.setStatus(DictConstant.DICT_B_IN_STATUS_CANCEL);
//        inPlanDetailMapper.updateById(inPlanDetailEntity);
//
//        // 作废入库单
//        List<BInVo> searchConditionList = new ArrayList<>();
//        BInVo vo = new BInVo();
//        vo.setId(returnRelationEntity.getIn_id());
//        vo.setRemark(returnRelationEntity.getQuantity_reason());
//        searchConditionList.add(vo);
//        // 执行作废入库单
//        ibInService.cancelDirect(searchConditionList);
//        // 重新计算 生产日报表 从当前作废数据的审核通过时间到 t-1 的数据
//        recreateProductDaily(searchConditionList);
//    }
//
//    /**
//     * 重新计算 生产日报表 从当前作废数据的审核通过时间到 t-1 的数据
//     *
//     * @param searchConditionList
//     */
//    private void recreateProductDaily(List<BInVo> searchConditionList) {
//        List<String> allCode = Lists.newArrayList("zlsd-0100509", "zlsd-0100510", "zlsd-0100508", "19", "CM-001",
//                "zlsd-0100511", "zlsd-0100505", "zlsd-0100506", "zlsd-0100507-3");
//        BInVo inVo = ibInService.selectEdtAndGoodsCode(searchConditionList.get(0).getId());
//        BProductDailyVo vo = new BProductDailyVo();
//        if (inVo.getE_dt() != null && allCode.contains(inVo.getGoods_code())) {
//            vo.setInit_time(inVo.getE_dt().format(DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD)));
//            try {
//                dailyProductV2Service.recreate2Cancel(vo);
//            } catch (Exception e) {
//                log.error("作废重置日加共报表出错, 参数--> {}");
//                log.error("recreateProductDaily error", e);
//            }
//        }
//    }
//
//
//    /**
//     * 查询业务中台是否结算
//     */
//    private Boolean callOutCanceledAppCode10Api(BMonitorOutDeliveryVo monitorOutVo) {
//        if (StringUtils.isEmpty(monitorOutVo.getOut_extra_code())) {
//            return Boolean.FALSE;
//        }
//
//        SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.OUT_CANCELED);
//        String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
//        HttpHeaders headers = new HttpHeaders();
//        HttpEntity<String> requestEntity = new HttpEntity(monitorOutVo.getOut_code(), headers);
//        ResponseEntity<JSONObject> result = restTemplate.postForEntity(url, requestEntity, JSONObject.class);
//
//        ApiCanceledVo apiCanceledVo = JSONObject.from(result.getBody().getJSONObject("data")).toJavaObject(ApiCanceledVo.class);
//
//        if (apiCanceledVo.getData() != null) {
//            for (ApiCanceledDataVo apiCanceledDataVo : apiCanceledVo.getData()) {
//                if (apiCanceledDataVo.getCancel() != null && !apiCanceledDataVo.getCancel()) {
//                    return Boolean.TRUE;
//                }
//            }
//
//        }
//        return Boolean.FALSE;
//    }
//
//    /**
//     * 监管任务业务中台是否结算
//     * true, 已结算, false 未结算
//     *
//     * @return
//     */
//    private boolean callMonitorCanceledAppCode120Api(BMonitorVo bMonitorVo) {
//        SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.MONITOR_CANCELED);
//        String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
//
//        HttpHeaders headers = new HttpHeaders();
//        HttpEntity<String> requestEntity = new HttpEntity(bMonitorVo.getCode(), headers);
//        ResponseEntity<JSONObject> result = restTemplate.postForEntity(url, requestEntity, JSONObject.class);
//
//        ApiCanceledVo apiCanceledVo = JSONObject.from(result.getBody().getJSONObject("data")).toJavaObject(ApiCanceledVo.class);
//        if ("0".equals(apiCanceledVo.getCode())) {
//            // 未结算
//            return false;
//        } else {
//            // 业务中台不想改, 就先判断报错信息了
//            if (StringUtils.isNotBlank(apiCanceledVo.getMsg()) && apiCanceledVo.getMsg().contains("不存在")) {
//                return false;
//            }
//            return true;
//        }
//    }
//
//    /**
//     * 拼接中台同步数据url
//     *
//     * @param uri
//     * @param appCode
//     * @return
//     */
//    protected String getBusinessCenterUrl(String uri, String appCode) {
//        try {
//            SAppConfigEntity sAppConfigEntity = isAppConfigService.getDataByAppCode(appCode);
//            String app_key = sAppConfigEntity.getApp_key();
//            String secret_key = sAppConfigEntity.getSecret_key();
//            String host = InetAddress.getLocalHost().getHostAddress();
//
//            String url = UrlBuilder.create()
//                    .setScheme("http")
//                    .setHost(host)
//                    .setPort(port)
//                    .addPath(uri)
//                    .addQuery("app_key", app_key)
//                    .addQuery("secret_key", secret_key)
//                    .build();
//            return url.replaceAll("%2F", "/");
//        } catch (Exception e) {
//            log.error("getBusinessCenterUrl error", e);
//        }
//        return "";
//    }
//}
//
