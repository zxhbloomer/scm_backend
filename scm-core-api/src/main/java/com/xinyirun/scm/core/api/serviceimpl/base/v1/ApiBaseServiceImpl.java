package com.xinyirun.scm.core.api.serviceimpl.base.v1;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.api.bo.steel.*;
import com.xinyirun.scm.bean.api.vo.business.in.ApiInPlanVo;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutPlanVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiDeliveryPlanIdCodeVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiInPlanIdCodeVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiOutPlanIdCodeVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiReceivePlanIdCodeVo;
import com.xinyirun.scm.bean.entity.busniess.in.BInEntity;
import com.xinyirun.scm.bean.entity.busniess.in.delivery.BDeliveryEntity;
import com.xinyirun.scm.bean.entity.busniess.in.delivery.BDeliveryExtraEntity;
import com.xinyirun.scm.bean.entity.busniess.in.order.BInOrderEntity;
import com.xinyirun.scm.bean.entity.busniess.inplan.BInPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutExtraEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutOrderEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.out.receive.BReceiveEntity;
import com.xinyirun.scm.bean.entity.busniess.out.receive.BReceiveExtraEntity;
import com.xinyirun.scm.bean.entity.master.customer.MOwnerEntity;
import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MWarehouseEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.common.utils.LocalDateTimeUtils;
import com.xinyirun.scm.core.api.mapper.business.in.ApiInPlanMapper;
import com.xinyirun.scm.core.api.mapper.business.out.ApiOutPlanMapper;
import com.xinyirun.scm.core.api.service.base.v1.ApiIBaseService;
import com.xinyirun.scm.core.system.mapper.wms.in.delivery.BDeliveryExtraMapper;
import com.xinyirun.scm.core.system.mapper.wms.in.delivery.BDeliveryMapper;
import com.xinyirun.scm.core.system.mapper.wms.in.order.BInOrderMapper;
import com.xinyirun.scm.core.system.mapper.wms.in.BInMapper;
import com.xinyirun.scm.core.system.mapper.wms.inplan.BInPlanDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.out.BOutExtraMapper;
import com.xinyirun.scm.core.system.mapper.business.out.BOutMapper;
import com.xinyirun.scm.core.system.mapper.business.out.BOutPlanDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.out.order.BOutOrderMapper;
import com.xinyirun.scm.core.system.mapper.business.out.receive.BReceiveExtraMapper;
import com.xinyirun.scm.core.system.mapper.business.out.receive.BReceiveMapper;
import com.xinyirun.scm.core.system.mapper.master.customer.MOwnerMapper;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MWarehouseMapper;
import com.xinyirun.scm.core.system.mapper.sys.app.SAppConfigMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.business.out.IBOutPlanService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 扩展Mybatis-Plus接口
 *
 * @author
 */
public class ApiBaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements ApiIBaseService<T> {

//    @Autowired
//    private IBInPlanService IBInPlanService;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private BInPlanDetailMapper bInplanDetailMapper;

    @Autowired
    private BInOrderMapper inOrderMapper;

    @Autowired
    private MWarehouseMapper warehouseMapper;

    @Autowired
    private MOwnerMapper mOwnerMapper;

    @Autowired
    private BInMapper bInMapper;

    @Autowired
    private MStaffMapper staffMapper;

//    @Autowired
//    private BInExtraMapper inExtraMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private BOutPlanDetailMapper outPlanDetailMapper;

    @Autowired
    private BOutOrderMapper outOrderMapper;

    @Autowired
    private BOutMapper bOutMapper;

    @Autowired
    private BOutExtraMapper outExtraMapper;

    @Autowired
    private SAppConfigMapper appConfigMapper;

    @Autowired
    private IBOutPlanService iBOutPlanService;

    @Autowired
    private ApiInPlanMapper apiInPlanMapper;

    @Autowired
    private ApiOutPlanMapper apiOutPlanMapper;

    @Autowired
    private BDeliveryMapper bDeliveryMapper;

    @Autowired
    private BDeliveryExtraMapper deliveryExtraMapper;

    @Autowired
    private BReceiveMapper bReceiveMapper;

    @Autowired
    private BReceiveExtraMapper bReceiveExtraMapper;

    /**
     * 返回中台入数据
     */
    @Override
//    public void getSyncInResultAppCode10(BInPlanEntity entity, List<ApiInPlanResultBo> apiInPlanResultBoList) {
    public List<ApiInPlanResultBo> getSyncInResultAppCode10(ApiInPlanIdCodeVo vo) {
        List<ApiInPlanResultBo> apiInPlanResultBoList = new ArrayList<>();

        // 查询b_in_plan数据；
//        BInPlanVo bInPlanVo = IBInPlanService.selectById(vo.getPlan_id());
        ApiInPlanVo planVo = apiInPlanMapper.selectPlanById(vo.getPlan_id());
        // 返回入库计划
        ApiInPlanResultBo apiInPlanResultVo = new ApiInPlanResultBo();
        // 中台数据返回数据对象
        // 返回入库计划明细集合
        List<ApiInPlanDetailResultBo> apiInPlanDetailResultVoList = new ArrayList<>();
        // 返回入库单集合
        List<ApiInResultBo> apiInResultVoVoList = new ArrayList<>();
        // 赋值返回计划明细数据
        setInPlanDetail(planVo, apiInPlanDetailResultVoList, apiInResultVoVoList, apiInPlanResultVo, vo);
        // 赋值返回入库计划
        setInPlanResult(planVo, apiInPlanResultVo, apiInPlanDetailResultVoList, apiInResultVoVoList);
        apiInPlanResultBoList.add(apiInPlanResultVo);
        return apiInPlanResultBoList;
    }

    /**
     * 赋值返回计划明细数据
     */
    public void setInPlanDetail(ApiInPlanVo apiInPlanVo,
                                List<ApiInPlanDetailResultBo> apiInPlanDetailResultVoList,
                                List<ApiInResultBo> apiInResultVoVoList,
                                ApiInPlanResultBo apiInPlanResultVo,
                                ApiInPlanIdCodeVo vo
    ) {
        // 查询入库计划关联的计划明细数据
        List<BInPlanDetailEntity> inPlanDetailEntityList = bInplanDetailMapper.selectList(new QueryWrapper<BInPlanDetailEntity>().eq("plan_id", apiInPlanVo.getId()));
        // 入库计划明细
        for(BInPlanDetailEntity detailEntity:inPlanDetailEntityList) {
//            if (Objects.equals(detailEntity.getStatus(), DictConstant.DICT_B_IN_PLAN_STATUS_SAVED) || Objects.equals(detailEntity.getStatus(), DictConstant.DICT_B_IN_PLAN_STATUS_RETURN)) {
//                continue;
//            }
            apiInPlanResultVo.setPlan_detail_id(detailEntity.getId());
            apiInPlanResultVo.setPlan_detail_code(detailEntity.getCode());


            // 查询入库订单
//            if (Objects.equals(detailEntity.getOrder_type(), SystemConstants.ORDER.B_IN_ORDER)) {
//                BInOrderEntity inOrderEntity = inOrderMapper.selectById(detailEntity.getOrder_id());
//                apiInPlanResultVo.setOrderCode(inOrderEntity.getOrder_no());
//            } else if (Objects.equals(detailEntity.getOrder_type(), SystemConstants.ORDER.B_OUT_ORDER)) {
//                BOutOrderEntity outOrderEntity = outOrderMapper.selectById(detailEntity.getOrder_id());
//                apiInPlanResultVo.setOrderCode(outOrderEntity.getOrder_no());
//            }

            // 返回入库计划明细
            ApiInPlanDetailResultBo apiInPlanDetailResultBo = new ApiInPlanDetailResultBo();
//            ApiGoodsSpecVo apiGoodsSpecVo = new ApiGoodsSpecVo();
//            apiGoodsSpecVo.setCode(detailEntity.getSku_code());
//            apiGoodsSpecVo.setApp_code(SystemConstants.APP_CODE.ZT);
            // 查询仓库
            MWarehouseEntity warehouseEntity = warehouseMapper.selectById(detailEntity.getWarehouse_id());
            // 赋值返回入库计划明细
//            apiInPlanDetailResultBo.setPlanItemCode(detailEntity.getExtra_code());
//            apiInPlanDetailResultBo.setNoticeItemCode(detailEntity.getExtra_code());
//            apiInPlanDetailResultBo.setOrderCommodityCode(detailEntity.getOrder_goods_code());  // goods_code
            apiInPlanDetailResultBo.setGoodsSpecCode(detailEntity.getSku_code()); // sku_code
            apiInPlanDetailResultBo.setPlanPutNum(detailEntity.getWeight());
            apiInPlanDetailResultBo.setHouseId(warehouseEntity.getId());
            apiInPlanDetailResultBo.setHouseCode(warehouseEntity.getCode());
            apiInPlanDetailResultBo.setHouseName(warehouseEntity.getName());
            apiInPlanDetailResultBo.setPlan_detail_id(detailEntity.getId());
            apiInPlanDetailResultVoList.add(apiInPlanDetailResultBo);

            // 赋值入库计划状态
            setInStatus(detailEntity,apiInPlanResultVo);
        }

        // 返回入库单
        getInResult(apiInResultVoVoList, vo);
    }

    /**
     * 赋值返回入库计划
     * @param planVo
     * @param apiInPlanResultVo
     * @param apiInPlanDetailResultVoList
     * @param apiInResultVoVoList
     */
    public void setInPlanResult(ApiInPlanVo planVo,
                                ApiInPlanResultBo apiInPlanResultVo,
                                List<ApiInPlanDetailResultBo> apiInPlanDetailResultVoList,
                                List<ApiInResultBo> apiInResultVoVoList) {
        // 查询货主数据
        MOwnerEntity ownerEntity = mOwnerMapper.selectById(planVo.getOwner_id());
        // 赋值返回入库计划
        apiInPlanResultVo.setPlanCode(planVo.getExtra_code());
        apiInPlanResultVo.setNoticeCode(planVo.getExtra_code());
        apiInPlanResultVo.setPlan_code(planVo.getCode());
        apiInPlanResultVo.setPlan_id(planVo.getId());

//        apiInPlanResultVo.setTypeCode(planVo.getType());
        if(DictConstant.DICT_B_IN_PLAN_TYPE_CG.equals(planVo.getType())) {
            apiInPlanResultVo.setTypeCode(SystemConstants.STEEL_IN_TYPE_ZERO);
        }else if(DictConstant.DICT_B_IN_PLAN_TYPE_TH.equals(planVo.getType())){
            apiInPlanResultVo.setTypeCode(SystemConstants.STEEL_IN_TYPE_ONE);
        } else if(DictConstant.DICT_B_IN_PLAN_TYPE_TIH.equals(planVo.getType())){ // 提货单
            apiInPlanResultVo.setTypeCode(SystemConstants.STEEL_IN_TYPE_TWO);
        }
        apiInPlanResultVo.setOwnerCargoId(planVo.getOwner_id());
        apiInPlanResultVo.setOwnerCargoCode(planVo.getOwner_code());
        apiInPlanResultVo.setOwnerCargoName(ownerEntity.getName());
        apiInPlanResultVo.setAuditTime(LocalDateTimeUtils.formatTime(planVo.getU_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
        apiInPlanResultVo.setWmsHousePutPlanItemDtoList(apiInPlanDetailResultVoList);
        apiInPlanResultVo.setWmsHousePutDocDtoList(apiInResultVoVoList);
    }

    /**
     * 赋值返回入库单
     */
    public void getInResult(List<ApiInResultBo> apiInResultVoVoList, ApiInPlanIdCodeVo vo) {
        // 查询入库计划关联的入库单数据
        List<BInEntity> inEntityList = null;
        if (vo.getIn_id() != null) {
            inEntityList = bInMapper.selectList(new QueryWrapper<BInEntity>().eq("id",vo.getIn_id()));
        }
//        else {
//            inEntityList = bInMapper.selectList(new QueryWrapper<BInEntity>().eq("plan_id",vo.getPlan_id()));
//        }

        // 赋值返回入库单
        if(inEntityList != null && inEntityList.size() > 0) {
            for(BInEntity inEntity:inEntityList) {
                if (Objects.equals(inEntity.getStatus(), DictConstant.DICT_B_IN_STATUS_SAVED) || Objects.equals(inEntity.getStatus(), DictConstant.DICT_B_IN_STATUS_RETURN)) {
                    continue;
                }

                BInPlanDetailEntity detailEntity = bInplanDetailMapper.selectById(inEntity.getPlan_detail_id());
                // 查询创建人数据
                MStaffEntity staffEntity = staffMapper.selectById(inEntity.getC_id());
                // 查询入库单从表数据
//                BInExtraEntity inExtraEntity = inExtraMapper.selectByInId(inEntity.getId());
                // 返回入库单对象
                ApiInResultBo apiInResultVo = new ApiInResultBo();
                apiInResultVo.setPutDocCode(inEntity.getCode());
//                apiInResultVo.setPlanItemCode(detailEntity.getExtra_code());
                apiInResultVo.setPutTime(LocalDateTimeUtils.formatTime(inEntity.getInbound_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                apiInResultVo.setMakeUser(staffEntity.getName());
                apiInResultVo.setMakeTime(LocalDateTimeUtils.formatTime(inEntity.getC_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                apiInResultVo.setUpdateTime(LocalDateTimeUtils.formatTime(inEntity.getU_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));

                // 设置返回入库单状态
                setInStatus(inEntity,apiInResultVo);

                MWarehouseEntity warehouseEntity = warehouseMapper.selectById(inEntity.getWarehouse_id());
                apiInResultVo.setHouseId(warehouseEntity.getId());
                apiInResultVo.setHouseCode(warehouseEntity.getCode());
                apiInResultVo.setHouseName(warehouseEntity.getName());
//                if (inExtraEntity.getCar_count() == null) {
//                    apiInResultVo.setCarCount(0);
//                } else {
//                    apiInResultVo.setCarCount(inExtraEntity.getCar_count());
//                }

//                apiInResultVo.setPrimaryQuantity(inExtraEntity.getPrimary_quantity());
                apiInResultVo.setRealPutNum(inEntity.getActual_qty());
                apiInResultVo.setPutStockNum(inEntity.getActual_weight());
//                apiInResultVo.setCalc(inEntity.getCalc());
                apiInResultVo.setPrice(inEntity.getPrice());
                // 设置返回入库单附件
//                getApiFiles(apiInResultVo,inExtraEntity,inEntity);
                apiInResultVoVoList.add(apiInResultVo);
            }
        }
    }

    /**
     * 设置返回入库单状态
     */
    public void setInStatus(BInEntity inEntity,ApiInResultBo apiInResultVo) {
        // 入库单数据状态：制单、提交是进行中，审核通过是已完成，作废、驳回是作废
        if( inEntity.getStatus().equals(DictConstant.DICT_B_IN_STATUS_SUBMITTED)) {
            apiInResultVo.setStatusCode(SystemConstants.API_STATUS_PROGRESS);
        }
        if (inEntity.getStatus().equals(DictConstant.DICT_B_IN_STATUS_PASSED)){
            apiInResultVo.setStatusCode(SystemConstants.API_STATUS_OVER);
        }
        if(inEntity.getStatus().equals(DictConstant.DICT_B_IN_STATUS_CANCEL)) {
            apiInResultVo.setStatusCode(SystemConstants.API_STATUS_CANCEL);
        }
    }

    /**
     * 赋值入库计划状态
     */
    public void setInStatus(BInPlanDetailEntity detailEntity,ApiInPlanResultBo apiInPlanResultVo) {
        // 入库计划明细数据状态：提交是进行中，审核通过是已完成，作废是作废
//        if(detailEntity.getStatus().equals(DictConstant.DICT_B_IN_PLAN_STATUS_SUBMITTED) || detailEntity.getStatus().equals(DictConstant.DICT_B_IN_PLAN_STATUS_PASSED)) {
//            // 进行中
//            apiInPlanResultVo.setStatusCode(SystemConstants.API_STATUS_PROGRESS);
//        }
//        if (detailEntity.getStatus().equals(DictConstant.DICT_B_IN_PLAN_STATUS_FINISH)) {
//            // 已完成
//            apiInPlanResultVo.setStatusCode(SystemConstants.API_STATUS_OVER);
//        }
//        if(detailEntity.getStatus().equals(DictConstant.DICT_B_IN_PLAN_STATUS_CANCEL)) {
//            // 作废
//            apiInPlanResultVo.setStatusCode(SystemConstants.API_STATUS_CANCEL);
//        }
    }

    /**
     * 设置返回入库单附件
     */
//    private void getApiFiles(ApiInResultBo apiInResultVo, BInExtraEntity inExtraEntity,BInEntity inEntity) {
//        SAppConfigEntity appConfigEntity = appConfigMapper.getDataByCode(SystemConstants.APP_CODE.ZT);
//        // 磅单文件
//        if(inExtraEntity != null && inExtraEntity.getPound_file() != null) {
//            // 查询附件主表数据
//            SFileEntity file = fileMapper.selectById(inExtraEntity.getPound_file());
//            List<String> resultFiles = new ArrayList<>();
//            // 赋值附件数据返回中台
//            setFile(file,appConfigEntity,resultFiles);
//            apiInResultVo.setPoundFile(resultFiles);
//        }
//        // 入库单照片
//        if(inExtraEntity != null && inExtraEntity.getPhoto_file() != null) {
//            SFileEntity file = fileMapper.selectById(inExtraEntity.getPhoto_file());
//            List<String> resultFiles = new ArrayList<>();
//            // 赋值附件数据返回中台
//            setFile(file,appConfigEntity,resultFiles);
//            apiInResultVo.setPhotoFile(resultFiles);
//        }
//        // 检验单附件
//        if(inExtraEntity != null && inExtraEntity.getInspection_file() != null) {
//            SFileEntity file = fileMapper.selectById(inExtraEntity.getInspection_file());
//            List<String> resultFiles = new ArrayList<>();
//            // 赋值附件数据返回中台
//            setFile(file,appConfigEntity,resultFiles);
//            apiInResultVo.setInspectionFile(resultFiles);
//        }
//        // 物料明细表附件
//        if(inExtraEntity != null && inExtraEntity.getGoods_file() != null) {
//            SFileEntity file = fileMapper.selectById(inExtraEntity.getGoods_file());
//            List<String> resultFiles = new ArrayList<>();
//            // 赋值附件数据返回中台
//            setFile(file,appConfigEntity,resultFiles);
//            apiInResultVo.setGoodsFile(resultFiles);
//        }
//    }

    /**
     * 赋值附件数据返回中台
     */
    public void setFile(SFileEntity file,SAppConfigEntity appConfigEntity,List<String> resultFiles) {
        // 附件明细list
        List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id",file.getId()));
        for(SFileInfoEntity fileInfo:fileInfos) {
            // 设置附件url
            resultFiles.add(fileInfo.getUrl());
        }
    }

    /**
     * 返回中台数据
     */
//    public void getSyncInResultAppCode10(BOutPlanEntity entity, List<ApiOutPlanResultBo> apiOutPlanResultBoList) {
    @Override
    public List<ApiOutPlanResultBo> getSyncOutResultAppCode10(ApiOutPlanIdCodeVo vo) {
        List<ApiOutPlanResultBo> apiOutPlanResultBoList = new ArrayList<>();

        // 查询b_out_plan数据；
        ApiOutPlanVo planVo = apiOutPlanMapper.selectPlanById(vo.getPlan_id());

        // 出库计划
        ApiOutPlanResultBo apiOutPlanResultBo = new ApiOutPlanResultBo();

        // 出库计划明细集合
        List<ApiOutPlanDetailResultBo> apiOutPlanDetailResultVoList = new ArrayList<>();
        // 出库单集合
        List<ApiOutResultBo> apiOutResultVoList = new ArrayList<>();
        // 赋值返回出库计划明细对象
        setOutPlanDetailResult(apiOutPlanResultBo, apiOutPlanDetailResultVoList, apiOutResultVoList, planVo, vo);
        // 赋值返回出库计划
        setOutPlanResult(apiOutPlanResultBo, apiOutPlanDetailResultVoList, apiOutResultVoList, planVo);

        apiOutPlanResultBoList.add(apiOutPlanResultBo);

        return apiOutPlanResultBoList;
    }

    /**
     * 返回出库计划明细
     */
    public void setOutPlanDetailResult(ApiOutPlanResultBo apiOutPlanResultBo,
                                       List<ApiOutPlanDetailResultBo> apiOutPlanDetailResultVoList,
                                       List<ApiOutResultBo> apiOutResultVoVoList,
                                       ApiOutPlanVo planVo,
                                       ApiOutPlanIdCodeVo vo) {
        // 查询出库计划关联的所有明细数据
        List<BOutPlanDetailEntity> outPlanDetailEntityList = outPlanDetailMapper.selectList(new QueryWrapper<BOutPlanDetailEntity>().eq("plan_id", planVo.getId()));
        for(BOutPlanDetailEntity detailEntity:outPlanDetailEntityList) {

            if (Objects.equals(detailEntity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_SAVED) || Objects.equals(detailEntity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_RETURN)) {
                continue;
            }
            apiOutPlanResultBo.setPlan_detail_id(detailEntity.getId());
            apiOutPlanResultBo.setPlan_detail_code(detailEntity.getCode());

            // 查询出库订单数据
            if (Objects.equals(detailEntity.getOrder_type(), SystemConstants.ORDER.B_IN_ORDER)) {
                BInOrderEntity inOrderEntity = inOrderMapper.selectById(detailEntity.getOrder_id());
                apiOutPlanResultBo.setOrderCode(inOrderEntity.getOrder_no());
            } else if (Objects.equals(detailEntity.getOrder_type(), SystemConstants.ORDER.B_OUT_ORDER)) {
                BOutOrderEntity outOrderEntity = outOrderMapper.selectById(detailEntity.getOrder_id());
                apiOutPlanResultBo.setOrderCode(outOrderEntity.getOrder_no());
            }
            if(detailEntity.getAudit_dt() != null) {
                apiOutPlanResultBo.setAuditTime(LocalDateTimeUtils.formatTime(detailEntity.getAudit_dt(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
            }else{
                apiOutPlanResultBo.setAuditTime(LocalDateTimeUtils.formatNow(DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
            }
            // 返回出库计划明细对象
            ApiOutPlanDetailResultBo apiOutPlanDetailResultBo = new ApiOutPlanDetailResultBo();
            apiOutPlanDetailResultBo.setPlanItemCode(detailEntity.getExtra_code());
            apiOutPlanDetailResultBo.setNoticeItemCode(detailEntity.getExtra_code());
            apiOutPlanDetailResultBo.setOrderCommodityCode(detailEntity.getOrder_goods_code());
            apiOutPlanDetailResultBo.setGoodsSpecCode(detailEntity.getSku_code());
            apiOutPlanDetailResultBo.setPlanOutNum(detailEntity.getWeight());
            apiOutPlanDetailResultBo.setPlan_detail_id(detailEntity.getId());
            // 查询仓库数据
            MWarehouseEntity warehouseEntity = warehouseMapper.selectById(detailEntity.getWarehouse_id());
            apiOutPlanDetailResultBo.setHouseId(warehouseEntity.getId());
            apiOutPlanDetailResultBo.setHouseCode(warehouseEntity.getCode());
            apiOutPlanDetailResultBo.setHouseName(warehouseEntity.getName());
            apiOutPlanDetailResultVoList.add(apiOutPlanDetailResultBo);
            // 获取出库计划返回状态
            getOutPLanStatus(detailEntity,apiOutPlanResultBo);

            // 审核时间不能为空，否则调用中台接口返回数据会报错
            if(detailEntity.getAudit_dt() != null) {
                LocalDateTimeUtils.formatTime(detailEntity.getAudit_dt(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS);
            }else{
                apiOutPlanResultBo.setAuditTime(LocalDateTimeUtils.formatNow(DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
            }
        }
        // 返回出库单
        getOutResult(apiOutResultVoVoList, vo);
    }

    /**
     * 赋值返回出库计划数据
     */
    public void setOutPlanResult(ApiOutPlanResultBo apiOutPlanResultBo,
                                 List<ApiOutPlanDetailResultBo> apiOutPlanDetailResultVoList,
                                 List<ApiOutResultBo> apiOutResultVoVoList,
                                 ApiOutPlanVo planVo) {
        // 赋值返回出库计划数据
        apiOutPlanResultBo.setPlanCode(planVo.getExtra_code());
        apiOutPlanResultBo.setNoticeCode(planVo.getExtra_code());
        apiOutPlanResultBo.setPlan_code(planVo.getCode());
        apiOutPlanResultBo.setPlan_id(planVo.getId());
        if(DictConstant.DICT_B_OUT_PLAN_TYPE_XS.equals(planVo.getType())) {
            apiOutPlanResultBo.setTypeCode(SystemConstants.STEEL_OUT_TYPE_ZERO);
        } else if(DictConstant.DICT_B_OUT_PLAN_TYPE_TH.equals(planVo.getType())){
            apiOutPlanResultBo.setTypeCode(SystemConstants.STEEL_OUT_TYPE_ONE);
        } else if(DictConstant.DICT_B_OUT_PLAN_TYPE_ZC.equals(planVo.getType())){ // 直采出库
            apiOutPlanResultBo.setTypeCode(SystemConstants.STEEL_OUT_TYPE_TWO);
        }

        apiOutPlanResultBo.setOwnerCargoId(planVo.getOwner_id());
        apiOutPlanResultBo.setOwnerCargoCode(planVo.getOwner_code());
        // 查询货主
        MOwnerEntity ownerEntity = mOwnerMapper.selectById(planVo.getOwner_id());
        apiOutPlanResultBo.setOwnerCargoName(ownerEntity.getName());
        apiOutPlanResultBo.setWmsHouseOutPlanItemDtoList(apiOutPlanDetailResultVoList);
        apiOutPlanResultBo.setWmsHouseOutDocDtoList(apiOutResultVoVoList);
    }

    /**
     * 获取出库计划返回状态
     */
    public void getOutPLanStatus(BOutPlanDetailEntity detailEntity,ApiOutPlanResultBo apiOutPlanResultBo) {
        // 数据状态：提交是进行中，审核通过是已完成，作废是作废
        if(detailEntity.getStatus().equals(DictConstant.DICT_B_OUT_PLAN_STATUS_SUBMITTED) || detailEntity.getStatus().equals(DictConstant.DICT_B_OUT_STATUS_PASSED)) {
            apiOutPlanResultBo.setStatusCode(SystemConstants.API_STATUS_PROGRESS);
        } else if (detailEntity.getStatus().equals(DictConstant.DICT_B_OUT_PLAN_STATUS_FINISH)){
            apiOutPlanResultBo.setStatusCode(SystemConstants.API_STATUS_OVER);
        } else if(detailEntity.getStatus().equals(DictConstant.DICT_B_OUT_PLAN_STATUS_CANCEL)) {
            apiOutPlanResultBo.setStatusCode(SystemConstants.API_STATUS_CANCEL);
        } else if(detailEntity.getStatus().equals(DictConstant.DICT_B_OUT_PLAN_STATUS_DISCONTINUE)) {
            apiOutPlanResultBo.setStatusCode(SystemConstants.API_STATUS_DISCONTINUE);
        }
//
//        if(detailEntity.getStatus().equals(DictConstant.DICT_B_OUT_PLAN_STATUS_DISCONTINUE)) {
//            apiOutPlanResultBo.setStatusCode(SystemConstants.API_STATUS_DISCONTINUE);
//        }
    }

    /**
     * 返回出库单状态
     */
    public void getOutStatusResult(BOutEntity outEntity,ApiOutResultBo apiOutResultVo) {
        // 数据状态：提交是进行中，审核通过是已完成，作废是作废
        if(outEntity.getStatus().equals(DictConstant.DICT_B_OUT_STATUS_SUBMITTED)) {
            apiOutResultVo.setStatusCode(SystemConstants.API_STATUS_PROGRESS);
        }
        if(outEntity.getStatus().equals(DictConstant.DICT_B_OUT_STATUS_PASSED)) {
            apiOutResultVo.setStatusCode(SystemConstants.API_STATUS_OVER);
        }
        if(outEntity.getStatus().equals(DictConstant.DICT_B_OUT_STATUS_CANCEL)) {
            apiOutResultVo.setStatusCode(SystemConstants.API_STATUS_CANCEL);
        }
    }

    /**
     * 返回出库单
     */
    public void getOutResult(List<ApiOutResultBo> apiOutResultVoVoList, ApiOutPlanIdCodeVo vo) {
        // 明细id查询关联的所有出库单数据
        List<BOutEntity> outEntityList = null;
        if (vo.getOut_id() != null) {
            outEntityList = bOutMapper.selectList(new QueryWrapper<BOutEntity>().eq("id",vo.getOut_id()));
        }

        if(outEntityList != null && outEntityList.size() > 0) {
            for(BOutEntity outEntity:outEntityList) {
                // 制单状态和已驳回的数据不同步
                if (Objects.equals(outEntity.getStatus(), DictConstant.DICT_B_OUT_STATUS_SAVED) || Objects.equals(outEntity.getStatus(), DictConstant.DICT_B_OUT_STATUS_RETURN)) {
                    continue;
                }
                BOutPlanDetailEntity detailEntity = outPlanDetailMapper.selectById(outEntity.getPlan_detail_id());
                // 返回出库单对象
                ApiOutResultBo apiOutResultVo = new ApiOutResultBo();
                apiOutResultVo.setOutDocCode(outEntity.getCode());
                apiOutResultVo.setOutPrice(outEntity.getPrice());
                apiOutResultVo.setPlanItemCode(detailEntity.getExtra_code());
                // 查询出库单从表数据
                BOutExtraEntity outExtraEntity = outExtraMapper.selectByInId(outEntity.getId());
                apiOutResultVo.setOutTime(LocalDateTimeUtils.formatTime(outEntity.getOutbound_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                // 查询创建人数据
                MStaffEntity staffEntity = staffMapper.selectById(outEntity.getC_id());
                apiOutResultVo.setMakeUser(staffEntity.getName());
                apiOutResultVo.setMakeTime(LocalDateTimeUtils.formatTime(outEntity.getC_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                apiOutResultVo.setUpdateTime(LocalDateTimeUtils.formatTime(outEntity.getU_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                // 返回出库单状态
                getOutStatusResult(outEntity,apiOutResultVo);
                MWarehouseEntity warehouseEntity = warehouseMapper.selectById(outEntity.getWarehouse_id());
                apiOutResultVo.setHouseId(warehouseEntity.getId());
                apiOutResultVo.setHouseCode(warehouseEntity.getCode());
                apiOutResultVo.setHouseName(warehouseEntity.getName());
                apiOutResultVo.setRealOutNum(outEntity.getReturn_qty()!=null?outEntity.getActual_count().subtract(outEntity.getReturn_qty()):outEntity.getActual_count());
                apiOutResultVo.setOutStockNum(outEntity.getReturn_qty()!=null?outEntity.getActual_weight().subtract(outEntity.getReturn_qty().multiply(outEntity.getCalc())):outEntity.getActual_weight());
                apiOutResultVo.setCalc(outEntity.getCalc());
                apiOutResultVo.setPrice(outEntity.getPrice());
                // 返回出库单附件
                getApiFiles(apiOutResultVo,outExtraEntity,outEntity);
                apiOutResultVoVoList.add(apiOutResultVo);
            }
        }
    }

    /**
     * 返回中台出库单附件
     */
    private void getApiFiles(ApiOutResultBo apiOutResultVo, BOutExtraEntity outExtraEntity,BOutEntity outEntity) {
        SAppConfigEntity appConfigEntity = appConfigMapper.getDataByCode(SystemConstants.APP_CODE.ZT);
        // 磅单附件
        if(outExtraEntity != null && outExtraEntity.getPound_file() != null) {
            // 查询磅单附件主表对象
            SFileEntity file = fileMapper.selectById(outExtraEntity.getPound_file());
            List<String> resultFiles = new ArrayList<>();
            // 赋值附件数据返回中台
            setFile(file,appConfigEntity,resultFiles);
            apiOutResultVo.setPoundFile(resultFiles);
        }
        // 出库单附件
        if(outExtraEntity != null && outExtraEntity.getOut_photo_file() != null) {
            SFileEntity file = fileMapper.selectById(outExtraEntity.getOut_photo_file());
            List<String> resultFiles = new ArrayList<>();
            // 赋值附件数据返回中台
            setFile(file,appConfigEntity,resultFiles);
            apiOutResultVo.setOutPhotoFile(resultFiles);
        }
    }

    /**
     * 返回中台入数据
     */
    @Override
    public List<ApiInPlanResultBo> getSyncDeliveryResultAppCode10(ApiDeliveryPlanIdCodeVo vo) {
        List<ApiInPlanResultBo> apiInPlanResultBoList = new ArrayList<>();

        // 查询b_in_plan数据；
        ApiInPlanVo planVo = apiInPlanMapper.selectPlanById(vo.getPlan_id());
        // 返回入库计划
        ApiInPlanResultBo apiInPlanResultVo = new ApiInPlanResultBo();
        // 中台数据返回数据对象
        // 返回入库计划明细集合
        List<ApiInPlanDetailResultBo> apiInPlanDetailResultVoList = new ArrayList<>();
        // 返回入库单集合
        List<ApiInResultBo> apiInResultVoVoList = new ArrayList<>();
        // 赋值返回计划明细数据
        setInPlanDetailByDelivery(planVo, apiInPlanDetailResultVoList, apiInResultVoVoList, apiInPlanResultVo, vo);
        // 赋值返回入库计划
        setInPlanResult(planVo, apiInPlanResultVo, apiInPlanDetailResultVoList, apiInResultVoVoList);
        apiInPlanResultBoList.add(apiInPlanResultVo);
        return apiInPlanResultBoList;
    }

    /**
     * 赋值返回计划明细数据
     */
    public void setInPlanDetailByDelivery(ApiInPlanVo apiInPlanVo,
                                List<ApiInPlanDetailResultBo> apiInPlanDetailResultVoList,
                                List<ApiInResultBo> apiInResultVoVoList,
                                ApiInPlanResultBo apiInPlanResultVo,
                                          ApiDeliveryPlanIdCodeVo vo
    ) {
        // 查询入库计划关联的计划明细数据
        List<BInPlanDetailEntity> inPlanDetailEntityList = bInplanDetailMapper.selectList(new QueryWrapper<BInPlanDetailEntity>().eq("plan_id", apiInPlanVo.getId()));
        // 入库计划明细
        for(BInPlanDetailEntity detailEntity:inPlanDetailEntityList) {
//            if (Objects.equals(detailEntity.getStatus(), DictConstant.DICT_B_IN_PLAN_STATUS_SAVED) || Objects.equals(detailEntity.getStatus(), DictConstant.DICT_B_IN_PLAN_STATUS_RETURN)) {
//                continue;
//            }
            apiInPlanResultVo.setPlan_detail_id(detailEntity.getId());
            apiInPlanResultVo.setPlan_detail_code(detailEntity.getCode());


            // 查询入库订单
//            if (Objects.equals(detailEntity.getOrder_type(), SystemConstants.ORDER.B_IN_ORDER)) {
//                BInOrderEntity inOrderEntity = inOrderMapper.selectById(detailEntity.getOrder_id());
//                apiInPlanResultVo.setOrderCode(inOrderEntity.getOrder_no());
//            } else if (Objects.equals(detailEntity.getOrder_type(), SystemConstants.ORDER.B_OUT_ORDER)) {
//                BOutOrderEntity outOrderEntity = outOrderMapper.selectById(detailEntity.getOrder_id());
//                apiInPlanResultVo.setOrderCode(outOrderEntity.getOrder_no());
//            }

            // 返回入库计划明细
            ApiInPlanDetailResultBo apiInPlanDetailResultBo = new ApiInPlanDetailResultBo();

            // 查询仓库
            MWarehouseEntity warehouseEntity = warehouseMapper.selectById(detailEntity.getWarehouse_id());
            // 赋值返回入库计划明细
//            apiInPlanDetailResultBo.setPlanItemCode(detailEntity.getExtra_code());
//            apiInPlanDetailResultBo.setNoticeItemCode(detailEntity.getExtra_code());
//            apiInPlanDetailResultBo.setOrderCommodityCode(detailEntity.getOrder_goods_code());  // goods_code
            apiInPlanDetailResultBo.setGoodsSpecCode(detailEntity.getSku_code()); // sku_code
            apiInPlanDetailResultBo.setPlanPutNum(detailEntity.getWeight());
            apiInPlanDetailResultBo.setHouseId(warehouseEntity.getId());
            apiInPlanDetailResultBo.setHouseCode(warehouseEntity.getCode());
            apiInPlanDetailResultBo.setHouseName(warehouseEntity.getName());
            apiInPlanDetailResultBo.setPlan_detail_id(detailEntity.getId());
            apiInPlanDetailResultVoList.add(apiInPlanDetailResultBo);

            // 赋值入库计划状态
            setInStatus(detailEntity,apiInPlanResultVo);
        }

        // 返回入库单
        getInResult(apiInResultVoVoList, vo);
    }

    /**
     * 赋值返回提货单
     */
    public void getInResult(List<ApiInResultBo> apiInResultVoVoList, ApiDeliveryPlanIdCodeVo vo) {
        // 查询入库计划关联的入库单数据
        List<BDeliveryEntity> bDeliveryEntities = null;
        if (vo.getDelivery_id() != null) {
            bDeliveryEntities = bDeliveryMapper.selectList(new QueryWrapper<BDeliveryEntity>().eq("id",vo.getDelivery_id()));
        }
//        else {
//            inEntityList = bInMapper.selectList(new QueryWrapper<BInEntity>().eq("plan_id",vo.getPlan_id()));
//        }

        // 赋值返回提货单
        if(bDeliveryEntities != null && bDeliveryEntities.size() > 0) {
            for(BDeliveryEntity bDeliveryEntity:bDeliveryEntities) {
                if (Objects.equals(bDeliveryEntity.getStatus(), DictConstant.DICT_B_DELIVERY_STATUS_SAVED) || Objects.equals(bDeliveryEntity.getStatus(), DictConstant.DICT_B_DELIVERY_STATUS_RETURN)) {
                    continue;
                }

                BInPlanDetailEntity detailEntity = bInplanDetailMapper.selectById(bDeliveryEntity.getPlan_detail_id());
                // 查询创建人数据
                MStaffEntity staffEntity = staffMapper.selectById(bDeliveryEntity.getC_id());
                // 查询提货单从表数据
                BDeliveryExtraEntity bDeliveryExtraEntity = deliveryExtraMapper.selectByInId(bDeliveryEntity.getId());
                // 返回入库单对象
                ApiInResultBo apiInResultVo = new ApiInResultBo();
                apiInResultVo.setPutDocCode(bDeliveryEntity.getCode());
//                apiInResultVo.setPlanItemCode(detailEntity.getExtra_code());
                apiInResultVo.setPutTime(LocalDateTimeUtils.formatTime(bDeliveryEntity.getInbound_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                apiInResultVo.setMakeUser(staffEntity.getName());
                apiInResultVo.setMakeTime(LocalDateTimeUtils.formatTime(bDeliveryEntity.getC_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                apiInResultVo.setUpdateTime(LocalDateTimeUtils.formatTime(bDeliveryEntity.getU_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));

                // 设置返回入库单状态
                setInStatus(bDeliveryEntity,apiInResultVo);

                MWarehouseEntity warehouseEntity = warehouseMapper.selectById(bDeliveryEntity.getWarehouse_id());
                apiInResultVo.setHouseId(warehouseEntity.getId());
                apiInResultVo.setHouseCode(warehouseEntity.getCode());
                apiInResultVo.setHouseName(warehouseEntity.getName());
                if (bDeliveryExtraEntity.getCar_count() == null) {
                    apiInResultVo.setCarCount(0);
                } else {
                    apiInResultVo.setCarCount(bDeliveryExtraEntity.getCar_count());
                }

                apiInResultVo.setPrimaryQuantity(bDeliveryExtraEntity.getPrimary_quantity()==null? BigDecimal.ZERO:bDeliveryExtraEntity.getPrimary_quantity());
                apiInResultVo.setRealPutNum(bDeliveryEntity.getActual_count());
                apiInResultVo.setPutStockNum(bDeliveryEntity.getActual_weight());
                apiInResultVo.setCalc(bDeliveryEntity.getCalc());
                apiInResultVo.setPrice(bDeliveryEntity.getPrice());
                // 设置返回入库单附件
                getApiFiles(apiInResultVo,bDeliveryExtraEntity,bDeliveryEntity);
                apiInResultVoVoList.add(apiInResultVo);
            }
        }
    }

    /**
     * 设置返回入库单状态
     */
    public void setInStatus(BDeliveryEntity inEntity,ApiInResultBo apiInResultVo) {
        // 入库单数据状态：制单、提交是进行中，审核通过是已完成，作废、驳回是作废
        if( inEntity.getStatus().equals(DictConstant.DICT_B_DELIVERY_STATUS_SUBMITTED)) {
            apiInResultVo.setStatusCode(SystemConstants.API_STATUS_PROGRESS);
        }
        if (inEntity.getStatus().equals(DictConstant.DICT_B_DELIVERY_STATUS_PASSED)){
            apiInResultVo.setStatusCode(SystemConstants.API_STATUS_OVER);
        }
        if(inEntity.getStatus().equals(DictConstant.DICT_B_DELIVERY_STATUS_CANCEL)) {
            apiInResultVo.setStatusCode(SystemConstants.API_STATUS_CANCEL);
        }
    }


    /**
     * 设置返回入库单附件
     */
    private void getApiFiles(ApiInResultBo apiInResultVo, BDeliveryExtraEntity bDeliveryExtraEntity,BDeliveryEntity bDeliveryEntity) {
        SAppConfigEntity appConfigEntity = appConfigMapper.getDataByCode(SystemConstants.APP_CODE.ZT);
        // 磅单文件
        if(bDeliveryExtraEntity != null && bDeliveryExtraEntity.getPound_file() != null) {
            // 查询附件主表数据
            SFileEntity file = fileMapper.selectById(bDeliveryExtraEntity.getPound_file());
            List<String> resultFiles = new ArrayList<>();
            // 赋值附件数据返回中台
            setFile(file,appConfigEntity,resultFiles);
            apiInResultVo.setPoundFile(resultFiles);
        }
        // 入库单照片
        if(bDeliveryExtraEntity != null && bDeliveryExtraEntity.getPhoto_file() != null) {
            SFileEntity file = fileMapper.selectById(bDeliveryExtraEntity.getPhoto_file());
            List<String> resultFiles = new ArrayList<>();
            // 赋值附件数据返回中台
            setFile(file,appConfigEntity,resultFiles);
            apiInResultVo.setPhotoFile(resultFiles);
        }
        // 检验单附件
        if(bDeliveryExtraEntity != null && bDeliveryExtraEntity.getInspection_file() != null) {
            SFileEntity file = fileMapper.selectById(bDeliveryExtraEntity.getInspection_file());
            List<String> resultFiles = new ArrayList<>();
            // 赋值附件数据返回中台
            setFile(file,appConfigEntity,resultFiles);
            apiInResultVo.setInspectionFile(resultFiles);
        }
        // 物料明细表附件
        if(bDeliveryExtraEntity != null && bDeliveryExtraEntity.getGoods_file() != null) {
            SFileEntity file = fileMapper.selectById(bDeliveryExtraEntity.getGoods_file());
            List<String> resultFiles = new ArrayList<>();
            // 赋值附件数据返回中台
            setFile(file,appConfigEntity,resultFiles);
            apiInResultVo.setGoodsFile(resultFiles);
        }
    }

    /**
     * 返回中台数据
     */
    @Override
    public List<ApiOutPlanResultBo> getSyncReceiveResultAppCode10(ApiReceivePlanIdCodeVo vo) {
        List<ApiOutPlanResultBo> apiOutPlanResultBoList = new ArrayList<>();

        // 查询b_out_plan数据；
        ApiOutPlanVo planVo = apiOutPlanMapper.selectPlanById(vo.getPlan_id());

        // 出库计划
        ApiOutPlanResultBo apiOutPlanResultBo = new ApiOutPlanResultBo();

        // 出库计划明细集合
        List<ApiOutPlanDetailResultBo> apiOutPlanDetailResultVoList = new ArrayList<>();
        // 出库单集合
        List<ApiOutResultBo> apiOutResultVoList = new ArrayList<>();
        // 赋值返回出库计划明细对象
        setOutPlanDetailResult(apiOutPlanResultBo, apiOutPlanDetailResultVoList, apiOutResultVoList, planVo, vo);
        // 赋值返回出库计划
        setOutPlanResult(apiOutPlanResultBo, apiOutPlanDetailResultVoList, apiOutResultVoList, planVo);

        apiOutPlanResultBoList.add(apiOutPlanResultBo);

        return apiOutPlanResultBoList;
    }

    /**
     * 返回出库计划明细
     */
    public void setOutPlanDetailResult(ApiOutPlanResultBo apiOutPlanResultBo,
                                       List<ApiOutPlanDetailResultBo> apiOutPlanDetailResultVoList,
                                       List<ApiOutResultBo> apiOutResultVoVoList,
                                       ApiOutPlanVo planVo,
                                       ApiReceivePlanIdCodeVo vo) {
        // 查询出库计划关联的所有明细数据
        List<BOutPlanDetailEntity> outPlanDetailEntityList = outPlanDetailMapper.selectList(new QueryWrapper<BOutPlanDetailEntity>().eq("plan_id", planVo.getId()));
        for(BOutPlanDetailEntity detailEntity:outPlanDetailEntityList) {

            if (Objects.equals(detailEntity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_SAVED) || Objects.equals(detailEntity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_RETURN)) {
                continue;
            }
            apiOutPlanResultBo.setPlan_detail_id(detailEntity.getId());
            apiOutPlanResultBo.setPlan_detail_code(detailEntity.getCode());

            // 查询出库订单数据
            if (Objects.equals(detailEntity.getOrder_type(), SystemConstants.ORDER.B_IN_ORDER)) {
                BInOrderEntity inOrderEntity = inOrderMapper.selectById(detailEntity.getOrder_id());
                apiOutPlanResultBo.setOrderCode(inOrderEntity.getOrder_no());
            } else if (Objects.equals(detailEntity.getOrder_type(), SystemConstants.ORDER.B_OUT_ORDER)) {
                BOutOrderEntity outOrderEntity = outOrderMapper.selectById(detailEntity.getOrder_id());
                apiOutPlanResultBo.setOrderCode(outOrderEntity.getOrder_no());
            }
            if(detailEntity.getAudit_dt() != null) {
                apiOutPlanResultBo.setAuditTime(LocalDateTimeUtils.formatTime(detailEntity.getAudit_dt(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
            }else{
                apiOutPlanResultBo.setAuditTime(LocalDateTimeUtils.formatNow(DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
            }
            // 返回出库计划明细对象
            ApiOutPlanDetailResultBo apiOutPlanDetailResultBo = new ApiOutPlanDetailResultBo();
            apiOutPlanDetailResultBo.setPlanItemCode(detailEntity.getExtra_code());
            apiOutPlanDetailResultBo.setNoticeItemCode(detailEntity.getExtra_code());
            apiOutPlanDetailResultBo.setOrderCommodityCode(detailEntity.getOrder_goods_code());
            apiOutPlanDetailResultBo.setGoodsSpecCode(detailEntity.getSku_code());
            apiOutPlanDetailResultBo.setPlanOutNum(detailEntity.getWeight());
            apiOutPlanDetailResultBo.setPlan_detail_id(detailEntity.getId());
            // 查询仓库数据
            MWarehouseEntity warehouseEntity = warehouseMapper.selectById(detailEntity.getWarehouse_id());
            apiOutPlanDetailResultBo.setHouseId(warehouseEntity.getId());
            apiOutPlanDetailResultBo.setHouseCode(warehouseEntity.getCode());
            apiOutPlanDetailResultBo.setHouseName(warehouseEntity.getName());
            apiOutPlanDetailResultVoList.add(apiOutPlanDetailResultBo);
            // 获取出库计划返回状态
            getOutPLanStatus(detailEntity,apiOutPlanResultBo);

            // 审核时间不能为空，否则调用中台接口返回数据会报错
            if(detailEntity.getAudit_dt() != null) {
                LocalDateTimeUtils.formatTime(detailEntity.getAudit_dt(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS);
            }else{
                apiOutPlanResultBo.setAuditTime(LocalDateTimeUtils.formatNow(DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
            }
        }
        // 返回收货单
        getOutResult(apiOutResultVoVoList, vo);
    }

    /**
     * 返回收货单
     */
    public void getOutResult(List<ApiOutResultBo> apiOutResultVoVoList, ApiReceivePlanIdCodeVo vo) {
        // 明细id查询关联的所有出库单数据
        List<BReceiveEntity> bReceiveEntities = null;
        if (vo.getReceive_id() != null) {
            bReceiveEntities = bReceiveMapper.selectList(new QueryWrapper<BReceiveEntity>().eq("id",vo.getReceive_id()));
        }

        if(bReceiveEntities != null && bReceiveEntities.size() > 0) {
            for(BReceiveEntity bReceiveEntity:bReceiveEntities) {
                // 制单状态和已驳回的数据不同步
                if (Objects.equals(bReceiveEntity.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_SAVED) || Objects.equals(bReceiveEntity.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_RETURN)) {
                    continue;
                }
                BOutPlanDetailEntity detailEntity = outPlanDetailMapper.selectById(bReceiveEntity.getPlan_detail_id());
                // 返回出库单对象
                ApiOutResultBo apiOutResultVo = new ApiOutResultBo();
                apiOutResultVo.setOutDocCode(bReceiveEntity.getCode());
                apiOutResultVo.setOutPrice(bReceiveEntity.getPrice());
                apiOutResultVo.setPlanItemCode(detailEntity.getExtra_code());
                // 查询出库单从表数据
                BReceiveExtraEntity bReceiveExtraEntity = bReceiveExtraMapper.selectByInId(bReceiveEntity.getId());

                apiOutResultVo.setOutTime(LocalDateTimeUtils.formatTime(bReceiveEntity.getOutbound_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                // 查询创建人数据
                MStaffEntity staffEntity = staffMapper.selectById(bReceiveEntity.getC_id());
                apiOutResultVo.setMakeUser(staffEntity.getName());
                apiOutResultVo.setMakeTime(LocalDateTimeUtils.formatTime(bReceiveEntity.getC_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                apiOutResultVo.setUpdateTime(LocalDateTimeUtils.formatTime(bReceiveEntity.getU_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                // 返回出库单状态
                getOutStatusResult(bReceiveEntity,apiOutResultVo);
                MWarehouseEntity warehouseEntity = warehouseMapper.selectById(bReceiveEntity.getWarehouse_id());
                apiOutResultVo.setHouseId(warehouseEntity.getId());
                apiOutResultVo.setHouseCode(warehouseEntity.getCode());
                apiOutResultVo.setHouseName(warehouseEntity.getName());
                apiOutResultVo.setRealOutNum(bReceiveEntity.getActual_count());
                apiOutResultVo.setOutStockNum(bReceiveEntity.getActual_weight());
                apiOutResultVo.setCalc(bReceiveEntity.getCalc());
                apiOutResultVo.setPrice(bReceiveEntity.getPrice());
                // 返回出库单附件
                getApiFiles(apiOutResultVo,bReceiveExtraEntity,bReceiveEntity);
                apiOutResultVoVoList.add(apiOutResultVo);
            }
        }
    }

    /**
     * 返回收货单状态
     */
    public void getOutStatusResult(BReceiveEntity outEntity,ApiOutResultBo apiOutResultVo) {
        // 数据状态：提交是进行中，审核通过是已完成，作废是作废
        if(outEntity.getStatus().equals(DictConstant.DICT_B_RECEIVE_STATUS_SUBMITTED)) {
            apiOutResultVo.setStatusCode(SystemConstants.API_STATUS_PROGRESS);
        }
        if(outEntity.getStatus().equals(DictConstant.DICT_B_RECEIVE_STATUS_PASSED)) {
            apiOutResultVo.setStatusCode(SystemConstants.API_STATUS_OVER);
        }
        if(outEntity.getStatus().equals(DictConstant.DICT_B_RECEIVE_STATUS_CANCEL)) {
            apiOutResultVo.setStatusCode(SystemConstants.API_STATUS_CANCEL);
        }
    }

    /**
     * 返回中台收货单附件
     */
    private void getApiFiles(ApiOutResultBo apiOutResultVo, BReceiveExtraEntity bReceiveExtraEntity,BReceiveEntity bReceiveEntity) {
        SAppConfigEntity appConfigEntity = appConfigMapper.getDataByCode(SystemConstants.APP_CODE.ZT);
        // 磅单附件
        if(bReceiveExtraEntity != null && bReceiveExtraEntity.getPound_file() != null) {
            // 查询磅单附件主表对象
            SFileEntity file = fileMapper.selectById(bReceiveExtraEntity.getPound_file());
            List<String> resultFiles = new ArrayList<>();
            // 赋值附件数据返回中台
            setFile(file,appConfigEntity,resultFiles);
            apiOutResultVo.setPoundFile(resultFiles);
        }
        // 出库单附件
        if(bReceiveExtraEntity != null && bReceiveExtraEntity.getOut_photo_file() != null) {
            SFileEntity file = fileMapper.selectById(bReceiveExtraEntity.getOut_photo_file());
            List<String> resultFiles = new ArrayList<>();
            // 赋值附件数据返回中台
            setFile(file,appConfigEntity,resultFiles);
            apiOutResultVo.setOutPhotoFile(resultFiles);
        }
    }
}
