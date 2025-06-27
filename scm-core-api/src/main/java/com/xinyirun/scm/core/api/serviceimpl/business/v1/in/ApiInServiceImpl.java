package com.xinyirun.scm.core.api.serviceimpl.business.v1.in;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xinyirun.scm.bean.api.vo.business.in.ApiInOrderVo;
import com.xinyirun.scm.bean.api.vo.business.in.ApiInPlanDetailVo;
import com.xinyirun.scm.bean.api.vo.business.in.ApiInPlanDisContinuedVo;
import com.xinyirun.scm.bean.api.vo.business.in.ApiInPlanVo;
import com.xinyirun.scm.bean.api.vo.master.customer.ApiCustomerVo;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiGoodsSpecVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiInPlanIdCodeVo;
import com.xinyirun.scm.bean.entity.busniess.in.BInEntity;
import com.xinyirun.scm.bean.entity.busniess.in.order.BInOrderEntity;
import com.xinyirun.scm.bean.entity.busniess.inplan.BInPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.inplan.BInPlanEntity;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorEntity;
import com.xinyirun.scm.bean.entity.busniess.order.BOrderEntity;
import com.xinyirun.scm.bean.entity.busniess.schedule.BScheduleEntity;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.entity.master.customer.MOwnerEntity;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MBinEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MLocationEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.wms.in.order.BInOrderVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.common.exception.api.ApiBusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.api.mapper.business.in.ApiInMapper;
import com.xinyirun.scm.core.api.mapper.business.in.ApiInPlanDetailMapper;
import com.xinyirun.scm.core.api.mapper.business.in.ApiInPlanMapper;
import com.xinyirun.scm.core.api.mapper.business.in.order.ApiInOrderMapper;
import com.xinyirun.scm.core.api.mapper.business.logistics.ApiBScheduleMapper;
import com.xinyirun.scm.core.api.mapper.business.monitor.ApiBMonitorMapper;
import com.xinyirun.scm.core.api.mapper.master.customer.ApiCustomerMapper;
import com.xinyirun.scm.core.api.mapper.master.customer.ApiOwnerMapper;
import com.xinyirun.scm.core.api.mapper.master.goods.ApiGoodsSpecMapper;
import com.xinyirun.scm.core.api.mapper.master.warehouse.ApiBinMapper;
import com.xinyirun.scm.core.api.mapper.master.warehouse.ApiLocationMapper;
import com.xinyirun.scm.core.api.service.business.v1.in.ApiIInService;
import com.xinyirun.scm.core.api.serviceimpl.base.v1.ApiBaseServiceImpl;
import com.xinyirun.scm.core.api.serviceimpl.common.v1.ApiInPlanAutoCodeServiceImpl;
import com.xinyirun.scm.core.api.serviceimpl.common.v1.ApiInPlanDetailAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.service.base.v1.common.inventory.ICommonInventoryLogicService;
import com.xinyirun.scm.core.system.service.master.goods.unit.IMUnitService;
import com.xinyirun.scm.core.system.service.master.user.IMStaffService;
import com.xinyirun.scm.core.system.serviceimpl.business.todo.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.xinyirun.scm.common.constant.DictConstant.DICT_B_MONITOR_STATUS_EIGHT;

/**
 * <p>
 * 入库计划 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class ApiInServiceImpl extends ApiBaseServiceImpl<ApiInPlanMapper, BInPlanEntity> implements ApiIInService {

    @Autowired
    private ApiInPlanMapper mapper;

    @Autowired
    private ApiInPlanDetailMapper inPlanDetailMapper;

    @Autowired
    private ApiInOrderMapper inOrderMapper;

    @Autowired
    private ApiLocationMapper locationMapper;

    @Autowired
    private ApiBinMapper binMapper;

    @Autowired
    private ApiCustomerMapper customerMapper;

    @Autowired
    private ApiOwnerMapper ownerMapper;

    @Autowired
    private ApiGoodsSpecMapper goodsSpecMapper;

    @Autowired
    private ApiInPlanAutoCodeServiceImpl autoCode;

    @Autowired
    private ApiInPlanDetailAutoCodeServiceImpl autoCodeDetail;

    @Autowired
    private IMUnitService imUnitService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private IMStaffService staffService;

    @Autowired
    private ApiInMapper inMapper;

    @Autowired
    private ApiBScheduleMapper scheduleMapper;

    @Autowired
    private ApiBMonitorMapper monitorMapper;

    @Autowired
    private ICommonInventoryLogicService iCommonInventoryLogicService;


    /**
     * 同步新增入库计划
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<ApiInPlanIdCodeVo> save(ApiInPlanVo vo) {
        // 新增入库计划数据
        BInPlanEntity inPlanEntity = insert(vo);
        ApiInPlanIdCodeVo rtn =new ApiInPlanIdCodeVo();
        rtn.setPlan_id(inPlanEntity.getId());
        rtn.setPlan_code(inPlanEntity.getCode());
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 入库通知中止
     *
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void discontinue(ApiInPlanDisContinuedVo param) {
//        if (StringUtils.isBlank(param.getCode())) {
//            throw new ApiBusinessException(ApiResultEnum.IN_PLAN_DISCONTINUED_CODE_NULL);
//        }
//
//        BInPlanEntity inPlanEntity = mapper.selectOne(Wrappers.<BInPlanEntity>lambdaQuery()
//                .eq(BInPlanEntity::getExtra_code, param.getCode()));
//        if (null == inPlanEntity) {
//            throw new ApiBusinessException(ApiResultEnum.IN_PLAN_DISCONTINUED_ENTITY_NULL);
//        }
//
//        BInPlanDetailEntity inPlanDetailEntity = inPlanDetailMapper.selectOne(Wrappers.<BInPlanDetailEntity>lambdaQuery()
//                .eq(BInPlanDetailEntity::getPlan_id, inPlanEntity.getId()));
//
//        // 入库计划作废审核中的, 需报错
//        if (DictConstant.DICT_B_IN_PLAN_STATUS_CANCEL_BEING_AUDITED.equals(inPlanDetailEntity.getStatus())) {
//            throw new ApiBusinessException(String.format("该入库通知向下有审核中的单据：入库计划(%s)!", inPlanEntity.getCode()));
//        }
//
//        // 查询入库计划对应的入库单
//        List<BInEntity> inEntities = inMapper.selectList(Wrappers.<BInEntity>lambdaQuery().eq(BInEntity::getPlan_id, inPlanEntity.getId()));
//
//        // 校验
//        discontinueCheck(inPlanDetailEntity, inEntities);
//
//        // 更新入库单状态
//        if (!CollectionUtils.isEmpty(inEntities)) {
//            // 入库单作废,制单, 已提交,审核驳回状态, 作废
//            inMapper.updateStatus2Cancel(inEntities);
//            // 更新待办数据状态
//            inMapper.updateTodoData(inEntities);
//            // 更新库存数据
//            for (BInEntity inEntity : inEntities) {
//                // 已提交数据释放可用库存
//                if (DictConstant.DICT_B_OUT_STATUS_SUBMITTED.equals(inEntity.getStatus()) || DictConstant.DICT_B_OUT_STATUS_RETURN.equals(inEntity.getStatus())) {
//                    iCommonInventoryLogicService.updWmsStockByInBill(inEntity.getId());
//                }
//            }
//        }
//
//        // 中止入库计划
//        inPlanDetailMapper.discontinuedInPlan(inPlanDetailEntity.getId());

    }

    /**
     * 中止校验
     */
    private void discontinueCheck(BInPlanDetailEntity entity, List<BInEntity> inEntities) {

        // 查询入库计划对应的入库单, 不能是作废审核中的
        if (!CollectionUtils.isEmpty(inEntities)) {
            for (BInEntity inEntity : inEntities) {
                if (DictConstant.DICT_B_IN_STATUS_TWO.equals(inEntity.getStatus())) {
                    throw new ApiBusinessException(String.format("该入库通知向下有审核中的单据：入库单(%s)!", inEntity.getCode()));
                }
            }
        }

        // 该入库通知关联的入库计划关联了“制单/已提交/已驳回/待调度”状态的物流订单/物流调度单，需报错
        List<BScheduleEntity> scheduleEntities = scheduleMapper.selectList(Wrappers.<BScheduleEntity>lambdaQuery()
                .eq(BScheduleEntity::getIn_plan_detail_id, entity.getId()));
        if (!CollectionUtils.isEmpty(scheduleEntities)) {
            for (BScheduleEntity scheduleEntity : scheduleEntities) {
                if (!DictConstant.DICT_B_SCHEDULE_STATUS_ONE.equals(scheduleEntity.getStatus()) &&
                        !DictConstant.DICT_B_SCHEDULE_STATUS_FIVE.equals(scheduleEntity.getStatus())) {
                    throw new ApiBusinessException(String.format("该入库通知向下有未完成的单据：物流订单(%s)不是已完成/已作废状态",
                            scheduleEntity.getCode()));
                }
            }

            // 若该入库通知关联的入库计划关联了“空车过磅/正在装货/重车出库/重车过磅/正在卸货/空车出库”状态的监管任务，需报错
            Set<Integer> scheduleIds = scheduleEntities.stream().map(BScheduleEntity::getId).collect(Collectors.toSet());
            List<BMonitorEntity> monitorEntities = monitorMapper.selectList(Wrappers.<BMonitorEntity>lambdaQuery().in(BMonitorEntity::getSchedule_id, scheduleIds)
                    .notIn(BMonitorEntity::getStatus, Arrays.asList(DictConstant.DICT_B_MONITOR_STATUS_SEVEN, DICT_B_MONITOR_STATUS_EIGHT)));
            if (!CollectionUtils.isEmpty(monitorEntities)) {
                for (BMonitorEntity monitorEntity : monitorEntities) {
                    throw new ApiBusinessException(String.format("该入库通知向下有未完成的单据：监管任务(%s)不是卸货完成/作废",
                            monitorEntity.getCode()));
                }
            }
        }
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public List<ApiInPlanResultBo> syncInsert(ApiInPlanVo vo) {
//        // 新增入库计划数据
//        BInPlanEntity inPlanEntity = insert(vo);
//
//        // 定义查询 bean
//        ApiInPlanIdCodeVo apiInPlanIdCodeVo = new ApiInPlanIdCodeVo();
//        apiInPlanIdCodeVo.setPlan_code(inPlanEntity.getCode());
//        apiInPlanIdCodeVo.setApp_code(inPlanEntity.getApp_code());
//        apiInPlanIdCodeVo.setPlan_id(inPlanEntity.getId());
//        // 设置返回数据
//        List<ApiInPlanResultBo> apiInPlanResultBoList = getSyncInResultAppCode10(apiInPlanIdCodeVo);
//        return apiInPlanResultBoList;
//    }

    /**
     * 新增入库计划数据
     */
    public BInPlanEntity insert(ApiInPlanVo vo) {
        // 必输check
        check(vo);

        // 生成入库计划单号
        String no = autoCode.autoCode().getCode();

        // 查询货主
        MOwnerEntity owner = getOwner(vo);

        // 查询委托方
        MCustomerEntity consignor = getConsignor(vo);

        // 查询供应商
//        MCustomerEntity supplier = customerMapper.selectByCode(vo.getOrderVo().getSupplier_code());
//        if(supplier == null) {
//            throw new ApiBusinessException(ApiResultEnum.SUPPLIER_NULL);
//        }

        // 更新入库订单
//        BInOrderEntity bInOrderEntity = saveOrder(vo,supplier);
        BOrderEntity bOrderEntity = inOrderMapper.selectOrderByOrderNo(vo.getOrderVo().getOrder_no());
        if (bOrderEntity == null) {
            throw new ApiBusinessException("订单【"+vo.getOrderVo().getOrder_no()+"】不存在，请先同步订单数据");
        }

        // 新增入库计划逻辑
        BInPlanEntity plan = (BInPlanEntity)BeanUtilsSupport.copyProperties(vo, BInPlanEntity.class);
        plan.setRemark(vo.getRemark());

//        plan.setExtra_code(vo.getCode());
        plan.setCode(no);
        plan.setOwner_id(owner.getId());
        plan.setOwner_code(owner.getCode());
        plan.setConsignor_id(consignor.getId());
        plan.setConsignor_code(consignor.getCode());

        // 中台同步的类型为0：采购入库，1：销售退货入库 2=提货单
        if("1".equals(vo.getType())) {
            plan.setType(DictConstant.DICT_B_IN_PLAN_TYPE_TH);
        }else if ("0".equals(vo.getType())){
            plan.setType(DictConstant.DICT_B_IN_PLAN_TYPE_CG);
        }else if ("2".equals(vo.getType())){
            plan.setType(DictConstant.DICT_B_IN_PLAN_TYPE_TIH);
        }

        mapper.insert(plan);

        // 新增入库计划明细逻辑
        insetPlanDetail(vo,plan,bOrderEntity);

        return plan;
    }

    /**
     * 新增入库计划明细
     */
    public void insetPlanDetail(ApiInPlanVo vo,BInPlanEntity plan,BOrderEntity bOrderEntity) {
        int no = 1;
        for(ApiInPlanDetailVo detail:vo.getDetailList()) {
            // 查询物料规格数据
            MGoodsSpecEntity goodsSpecEntity = getGoodsSpec(vo,detail);

//            // 查询单位换算数据
//            MGoodsUnitConvertVo unitConvert = getGoodsUnitConvert(goodsSpecEntity,detail);
            // 查询单位數據
            MUnitVo unitVo = imUnitService.selectByCode(detail.getUnit());

            // 查询库区
            MLocationEntity location = locationMapper.selectByWarehouseId(detail.getWarehouse_id());

            // 查询库位
            MBinEntity bin = binMapper.selectByWarehouseId(detail.getWarehouse_id());

            // 根据 staff_code 查询 staff_id
            Integer id = staffService.selectIdByStaffCode(SystemConstants.AUDIT_STAFF_CODE);

            // 赋值新增入库计划详情
            BInPlanDetailEntity detailEntity = (BInPlanDetailEntity)BeanUtilsSupport.copyProperties(detail,BInPlanDetailEntity.class);
            detailEntity.setRemark(detail.getRemark());

//            detailEntity.setOrder_detail_no(detail.getNo());
//            detailEntity.setAlias(detail.getNickname());
//            detailEntity.setStatus(DictConstant.DICT_B_IN_PLAN_STATUS_APPROVED);
//            detailEntity.setAudit_id(id);
//            detailEntity.setAudit_dt(LocalDateTime.now());
            detailEntity.setSku_id(goodsSpecEntity.getId());
            detailEntity.setWarehouse_id(detail.getWarehouse_id());
//            detailEntity.setPlan_id(plan.getId());
            detailEntity.setSku_code(detail.getSpec_code());
//            detailEntity.setOrder_goods_code(detail.getOrder_commodity_code());
            detailEntity.setWeight(detail.getCount());
//            detailEntity.setPending_volume(BigDecimal.ZERO);
//            detailEntity.setPending_count(detail.getCount());
//            detailEntity.setPending_weight(detail.getCount());
//            detailEntity.setHas_handle_count(BigDecimal.ZERO);
//            detailEntity.setHas_handle_weight(BigDecimal.ZERO);
//            detailEntity.setHas_handle_volume(BigDecimal.ZERO);
            detailEntity.setVolume(BigDecimal.ZERO);
//            detailEntity.setOrder_id(bOrderEntity.getSerial_id());
//            detailEntity.setOrder_type(bOrderEntity.getSerial_type());
            if(unitVo != null) {
                detailEntity.setUnit_id(unitVo.getId());
            }

            // 外部系统单号
//            detailEntity.setExtra_code(detail.getCode());
            // 明细单号
            detailEntity.setCode(autoCodeDetail.autoCode().getCode());
//            detailEntity.setOver_inventory_policy(detail.getFloat_controled());
//            detailEntity.setOver_inventory_lower(detail.getFloat_down());
//            detailEntity.setOver_inventory_upper(detail.getFloat_up());
            // 设置序号
            detailEntity.setNo(no);

            no++;
            inPlanDetailMapper.insert(detailEntity);

            // 生成待办
            todoService.insertTodo(detailEntity.getId(), SystemConstants.SERIAL_TYPE.B_IN_PLAN, SystemConstants.PERMS.B_IN_PLAN_DETAIL_SUBMIT);

        }
    }

    /**
     * 必输check
     */
    public void check(ApiInPlanVo vo) {
        // ApiInPlanVo属性必输check
        if(vo.getPlan_time() == null) {
            throw new ApiBusinessException(ApiResultEnum.IN_PLAN_PARAM_PLAN_TIME_NULL);
        }
        if(StringUtils.isEmpty(vo.getType())) {
            throw new ApiBusinessException(ApiResultEnum.IN_PLAN_PARAM_TYPE_NULL);
        }
        if(StringUtils.isEmpty(vo.getBill_type())) {
            throw new ApiBusinessException(ApiResultEnum.IN_PLAN_PARAM_BILL_TYPE_NULL);
        }
        if(StringUtils.isEmpty(vo.getCode())) {
            throw new ApiBusinessException(ApiResultEnum.IN_PLAN_PARAM_CODE_NULL);
        }
        if(StringUtils.isEmpty(vo.getOwner_code())) {
            throw new ApiBusinessException(ApiResultEnum.IN_PLAN_PARAM_OWNER_CODE_NULL);
        }
        if(StringUtils.isEmpty(vo.getConsignor_code())) {
            throw new ApiBusinessException(ApiResultEnum.IN_PLAN_PARAM_CONSIGNOR_CODE_NULL);
        }

        // ApiInPlanDetailVo是否为空
        if(vo.getDetailList().size() > 0 ) {
            for(ApiInPlanDetailVo detailVo:vo.getDetailList()){
                if(detailVo != null){
                    // ApiInPlanDetailVo必输属性check
                    planDetailCheck(detailVo);
                }else{
                    throw new ApiBusinessException(ApiResultEnum.IN_PLAN_PARAM_DETAIL_LIST_NULL);
                }
            }
        }else{
            throw new ApiBusinessException(ApiResultEnum.IN_PLAN_PARAM_DETAIL_LIST_NULL);
        }

        // ApiInOrderVo是否为空
        if(vo.getOrderVo() == null) {
            throw new ApiBusinessException(ApiResultEnum.IN_PLAN_PARAM_ORDER_VO_NULL);
        }
        // ApiInOrderVo必输属性check
        orderCheck(vo.getOrderVo());
    }

    /**
     * 入库详情必输check
     */
    public void planDetailCheck(ApiInPlanDetailVo vo) {
        if(StringUtils.isEmpty(vo.getSpec_code())) {
            throw new ApiBusinessException(ApiResultEnum.IN_PLAN_DETAIL_PARAM_SPEC_CODE_NULL);
        }
        if(vo.getPrice() == null) {
            throw new ApiBusinessException(ApiResultEnum.IN_PLAN_DETAIL_PARAM_PRICE_NULL);
        }
        if(vo.getCount() == null) {
            throw new ApiBusinessException(ApiResultEnum.IN_PLAN_DETAIL_PARAM_COUNT_NULL);
        }
        if(StringUtils.isEmpty(vo.getUnit())) {
            throw new ApiBusinessException(ApiResultEnum.IN_PLAN_DETAIL_PARAM_UNIT_NULL);
        }
        if(vo.getWarehouse_id() == null) {
            throw new ApiBusinessException(ApiResultEnum.IN_PLAN_DETAIL_PARAM_COUNT_NULL);
        }
    }

    /**
     * 入库订单必输check
     */
    public void orderCheck(ApiInOrderVo vo) {
        if(StringUtils.isEmpty(vo.getOrder_no())) {
            throw new ApiBusinessException(ApiResultEnum.IN_ORDER_PARAM_ORDER_NO_NULL);
        }
    }

    /**
     * 查询货主
     */
    public MOwnerEntity getOwner(ApiInPlanVo vo) {
        ApiCustomerVo apiCustomerVo = new ApiCustomerVo();
        apiCustomerVo.setCode(vo.getOwner_code());
        MOwnerEntity ownerEntity = ownerMapper.selectByCodeAppCode(apiCustomerVo);
        if(ownerEntity == null) {
            throw new ApiBusinessException(ApiResultEnum.OWNER_NULL);
        }
        return ownerEntity;
    }

    /**
     * 查询委托方
     */
    public MCustomerEntity getConsignor(ApiInPlanVo vo) {
        ApiCustomerVo apiCustomerVo = new ApiCustomerVo();
        apiCustomerVo.setCode(vo.getConsignor_code());
        MCustomerEntity customerEntity = customerMapper.selectByCodeAppCode(apiCustomerVo);
        if(customerEntity == null) {
            throw new ApiBusinessException(ApiResultEnum.CUSTOMER_NULL);
        }
        return customerEntity;
    }

    /**
     * 查询入库订单
     */
    public BInOrderEntity saveOrder(ApiInPlanVo vo,MCustomerEntity supplier) {
        // b_in_order
        BInOrderVo inOrder = new BInOrderVo();
        inOrder.setOrder_no(vo.getOrderVo().getOrder_no());
        BInOrderEntity bInOrderEntity = inOrderMapper.selectOrderByContract(inOrder);

        // 入库订单新增或修改
        if (bInOrderEntity == null) {
            // 若为空 则为新增
            bInOrderEntity = new BInOrderEntity();
            BeanUtilsSupport.copyProperties(vo.getOrderVo(), bInOrderEntity);
            bInOrderEntity.setSupplier_id(supplier.getId());
            bInOrderEntity.setBill_type(vo.getBill_type());
            inOrderMapper.insert(bInOrderEntity);
        } else {
            // 若不为空 则为修改
            BeanUtilsSupport.copyProperties(vo.getOrderVo(), bInOrderEntity);
            bInOrderEntity.setSupplier_id(supplier.getId());
            bInOrderEntity.setBill_type(vo.getBill_type());
            inOrderMapper.updateById(bInOrderEntity);
        }
        return bInOrderEntity;
    }

    /**
     * 查询规格数据
     */
    public MGoodsSpecEntity getGoodsSpec(ApiInPlanVo vo,ApiInPlanDetailVo detail) {
        ApiGoodsSpecVo specVo = new ApiGoodsSpecVo();
        specVo.setCode(detail.getSpec_code());
        MGoodsSpecEntity goodsSpecEntity = goodsSpecMapper.selectByCodeAppCode(specVo);
        if(goodsSpecEntity == null) {
            throw new ApiBusinessException(ApiResultEnum.GOODS_SPEC_NULL);
        }
        return goodsSpecEntity;
    }

}
