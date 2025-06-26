package com.xinyirun.scm.core.api.serviceimpl.business.v1.out.v2;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutOrderVo;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutPlanDetailVo;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutPlanDiscontinueVo;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutPlanVo;
import com.xinyirun.scm.bean.api.vo.master.customer.ApiCustomerVo;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiGoodsSpecVo;
import com.xinyirun.scm.bean.entity.busniess.order.BOrderEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutOrderEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutPlanEntity;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.entity.master.customer.MOwnerEntity;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MBinEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MLocationEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.business.monitor.BMonitorVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutOrderVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanListVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutVo;
import com.xinyirun.scm.bean.system.vo.business.schedule.BScheduleVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.common.exception.api.ApiBusinessException;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.api.mapper.business.out.ApiOutPlanDetailMapper;
import com.xinyirun.scm.core.api.mapper.business.out.ApiOutPlanMapper;
import com.xinyirun.scm.core.api.mapper.business.out.order.ApiOutOrderMapper;
import com.xinyirun.scm.core.api.mapper.master.customer.ApiCustomerMapper;
import com.xinyirun.scm.core.api.mapper.master.customer.ApiOwnerMapper;
import com.xinyirun.scm.core.api.mapper.master.goods.ApiGoodsSpecMapper;
import com.xinyirun.scm.core.api.mapper.master.warehouse.ApiBinMapper;
import com.xinyirun.scm.core.api.mapper.master.warehouse.ApiLocationMapper;
import com.xinyirun.scm.core.api.service.business.v1.out.v2.ApiIOutV2Service;
import com.xinyirun.scm.core.api.serviceimpl.base.v1.ApiBaseServiceImpl;
import com.xinyirun.scm.core.api.serviceimpl.common.v1.ApiOutPlanAutoCodeServiceImpl;
import com.xinyirun.scm.core.api.serviceimpl.common.v1.ApiOutPlanDetailAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.mapper.business.out.BOutMapper;
import com.xinyirun.scm.core.system.mapper.business.out.BOutPlanMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.inventory.ICommonInventoryLogicService;
import com.xinyirun.scm.core.system.service.business.out.IBOutPlanService;
import com.xinyirun.scm.core.system.service.business.out.IBOutService;
import com.xinyirun.scm.core.system.service.business.schedule.IBScheduleService;
import com.xinyirun.scm.core.system.service.master.goods.unit.IMUnitService;
import com.xinyirun.scm.core.system.service.master.user.IMStaffService;
import com.xinyirun.scm.core.system.serviceimpl.business.todo.TodoService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApiOutServiceV2Impl extends ApiBaseServiceImpl<ApiOutPlanMapper, BOutPlanEntity> implements ApiIOutV2Service {

    @Autowired
    private ApiOutPlanMapper mapper;

    @Autowired
    private ApiOutPlanDetailMapper outPlanDetailMapper;

    @Autowired
    private ApiOutOrderMapper outOrderMapper;

    @Autowired
    private ApiCustomerMapper customerMapper;

    @Autowired
    private ApiOwnerMapper ownerMapper;

    @Autowired
    private ApiBinMapper binMapper;

    @Autowired
    private ApiLocationMapper locationMapper;

    @Autowired
    private ApiGoodsSpecMapper goodsSpecMapper;

    @Autowired
    private ApiOutPlanAutoCodeServiceImpl autoCode;

    @Autowired
    private ApiOutPlanDetailAutoCodeServiceImpl autoCodeDetail;

    @Autowired
    private IMUnitService imUnitService;

    @Autowired
    private BOutMapper bOutMapper;

    @Autowired
    private ICommonInventoryLogicService iCommonInventoryLogicService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private IBOutService ibOutService;

    @Autowired
    private IBOutPlanService ibOutPlanService;

    @Autowired
    private IMStaffService staffService;

    @Autowired
    private BOutPlanMapper bOutPlanMapper;

    @Autowired
    private IBScheduleService iBScheduleService;

    /**
     * 同步出库计划
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BOutPlanVo> save(ApiOutPlanVo vo) {
        // 同步新增出库计划数据
        BOutPlanEntity outPlanEntity = insert(vo);

        BOutPlanVo rtn = (BOutPlanVo) BeanUtilsSupport.copyProperties(outPlanEntity, BOutPlanVo.class);
        rtn.setPlan_id(outPlanEntity.getId());
        rtn.setPlan_code(outPlanEntity.getCode());
        return InsertResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void expires(String code) {
        // check参数
        checkExpires(code);
        outPlanDetailMapper.expiresOutPlan(code);
        // 未审核的出库单直接改为过期状态
        List<BOutVo> bOutVos = bOutMapper.selectOutByExtraCode(code);
        bOutMapper.expiresOut(code);
        // 更新库存数据
        for (BOutVo bOutVo: bOutVos) {
            // 已提交数据释放可用库存
            if (DictConstant.DICT_B_OUT_STATUS_SUBMITTED.equals(bOutVo.getStatus())) {
                iCommonInventoryLogicService.updWmsStockByOutBill(bOutVo.getId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BOutPlanListVo> discontinue(ApiOutPlanDiscontinueVo vo) {
        // check参数
        checkDiscontinue(vo.getCode());
        outPlanDetailMapper.discontinueOutPlan(vo.getCode());

        List<BOutPlanEntity> bOutPlanEntities = bOutPlanMapper.selectByExtraCode(vo.getCode());
        String discontinueJson = JSON.toJSONString(vo);
        for (BOutPlanEntity bOutPlanEntity: bOutPlanEntities) {
            bOutPlanEntity.setDiscontinue_json(discontinueJson);
            bOutPlanMapper.updateById(bOutPlanEntity);
        }

        List<BOutPlanListVo> beans = outPlanDetailMapper.selectOutPlanByExtraCode(vo.getCode());
        // 未审核的出库单直接作废
        List<BOutVo> bOutVos = bOutMapper.selectOutByExtraCode(vo.getCode());
        bOutMapper.discontinueOut(vo.getCode());
        // 更新待办数据状态
        bOutMapper.updateTodoData(vo.getCode());

        // 更新库存数据
        for (BOutVo bOutVo: bOutVos) {

            BOutPlanDetailEntity bOutPlanDetailEntity = outPlanDetailMapper.selectById(bOutVo.getPlan_detail_id());
            bOutPlanDetailEntity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_DISCONTINUE);
            outPlanDetailMapper.updateById(bOutPlanDetailEntity);
            // 已提交数据释放可用库存
            if (DictConstant.DICT_B_OUT_STATUS_SUBMITTED.equals(bOutVo.getStatus())) {
                iCommonInventoryLogicService.updWmsStockByOutBill(bOutVo.getId());
            }
        }
        // WMS中对应的物流订单状态 制单/已提交/已驳回/已作废->已作废  待调度/已完成->已完成
        completeSchedule(vo.getCode());
        return beans;
    }

    /**
     * 完成物流调度 http://yirunscm.com:8080/issue/WMS-862
     * @param code 出库单 外部关联单号 extra_code
     */
    private void completeSchedule(String code) {
        List<Integer> scheduleIds = bOutMapper.selectScheduleIdByOutExtraCode(code);
        if (CollectionUtils.isNotEmpty(scheduleIds)) {
            iBScheduleService.completeSchedule(scheduleIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finish(String code) {
        // check参数
        checkFinish(code);

        outPlanDetailMapper.finishOutPlan(code);
    }

    @Override
    public void cancelable(String code) {
//         if (!"".equals(code)) {
//            throw new ApiBusinessException("测试:不可作废");
//        }
        checkCancel(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BOutPlanListVo> cancel(String code) {
        // check参数
        checkCancel(code);

        // 需手动作废出库单
//        // 作废放货通知下的所有入库单
//        List<BOutVo> bOutVos = bOutMapper.selectOutByExtraCode(code);
//
//        ibOutService.cancel(bOutVos);

        // 作废出库计划
        List<BOutPlanListVo> bOutPlanListVos = outPlanDetailMapper.selectOutPlanByExtraCode(code);

        ibOutPlanService.cancelDirect(bOutPlanListVos);

        return bOutPlanListVos;
    }

    private void checkDiscontinue(String code) {
        if (StringUtils.isEmpty(code)) {
            throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_PARAM_CODE_NULL);
        }

        List<ApiOutPlanVo> voList = mapper.selectPlanByExtraCode(code);
        if (voList == null || voList.size() == 0) {
            throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_PARAM_CODE_ERROR);
        }

        // 该放货指令对应WMS中有作废审核中的出库计划，则报错
        List<BOutPlanListVo> bOutPlanListVos = outPlanDetailMapper.selectOutPlanByExtraCode(code);
        if (CollectionUtils.isNotEmpty(bOutPlanListVos)) {
            for (BOutPlanListVo bOutPlanListVo : bOutPlanListVos) {
                if (DictConstant.DICT_B_OUT_PLAN_STATUS_CANCEL_BEING_AUDITED.equals(bOutPlanListVo.getStatus())) {
                    throw new ApiBusinessException("该放货指令向下有审核中的单据：出库计划("+bOutPlanListVo.getCode()+")");
                }
            }
        }

        // 该放货指令对应WMS中有作废审核中的出库单，则报错
        List<BOutVo> bOutVos = bOutMapper.selectAllOutByExtraCode(code);
        if (CollectionUtils.isNotEmpty(bOutVos)) {
            for (BOutVo bOutVo : bOutVos) {
                if (DictConstant.DICT_B_OUT_STATUS_CANCEL_BEING_AUDITED.equals(bOutVo.getStatus())) {
                    throw new ApiBusinessException("该放货指令向下有审核中的单据：出库单("+bOutVo.getCode()+")");
                }
            }
        }

//        BScheduleVo scheduleCount = bOutMapper.selectScheduleCount(code);
//        if (scheduleCount != null) {
//            throw new BusinessException("物流订单【"+scheduleCount.getCode()+"】还未完成!");
//        }

        BMonitorVo monitorCount = bOutMapper.selectMonitorCount(code);
        if (monitorCount != null) {
            throw new ApiBusinessException("监管任务【"+monitorCount.getCode()+"】还未重车过磅、正在卸货、空车出库、卸货完成或作废!");
        }
    }

    private void checkFinish(String code) {
        if (StringUtils.isEmpty(code)) {
            throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_PARAM_CODE_NULL);
        }

        List<ApiOutPlanVo> voList = mapper.selectPlanByExtraCode(code);
        if (voList == null || voList.size() == 0) {
            throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_PARAM_CODE_ERROR);
        }

    }

    private void checkCancel(String code) {
        if (StringUtils.isEmpty(code)) {
            throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_PARAM_CODE_NULL);
        }

        List<ApiOutPlanVo> voList = mapper.selectPlanByExtraCode(code);
        if (voList == null || voList.size() == 0) {
            throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_PARAM_CODE_ERROR);
        }

        BScheduleVo scheduleCount = bOutMapper.selectScheduleCount(code);
        if (scheduleCount != null) {
            throw new BusinessException("物流订单【"+scheduleCount.getCode()+"】还未完成!");
        }

        BMonitorVo monitorCount = bOutMapper.selectMonitorCount1(code);
        if (monitorCount != null) {
            throw new BusinessException("监管任务【"+monitorCount.getCode()+"】还未作废!");
        }

        BOutVo outCount = bOutMapper.selectUnAuditCount(code);
        if (outCount != null) {
            throw new BusinessException("出库单【"+outCount.getCode()+"】还未作废!");
        }
    }

    private void checkExpires(String code) {
        if (StringUtils.isEmpty(code)) {
            throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_PARAM_CODE_NULL);
        }

        List<ApiOutPlanVo> voList = mapper.selectPlanByExtraCode(code);
        if (voList == null || voList.size() == 0) {
            throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_PARAM_CODE_ERROR);
        }
    }

    /**
     * 新增出库计划逻辑
     */
    public BOutPlanEntity insert(ApiOutPlanVo vo) {
        // check
        check(vo);
        // 新增入库计划逻辑
        BOutPlanEntity plan = (BOutPlanEntity)BeanUtilsSupport.copyProperties(vo,BOutPlanEntity.class);
        // 查询货主
        MOwnerEntity owner = getOwner(vo);
        plan.setOwner_id(owner.getId());
        plan.setOwner_code(owner.getCode());
        // 查询委托方
        MCustomerEntity consignor = getConsignor(vo);
        plan.setConsignor_id(consignor.getId());
        plan.setConsignor_code(consignor.getCode());
        plan.setPlan_time(vo.getPlan_time());
        // 生成出库计划单号
        String no = autoCode.autoCode().getCode();
        plan.setCode(no);
        // 外部系统单号
        plan.setExtra_code(vo.getCode());
        plan.setRelease_order_code(vo.getRelease_order_code());
        // 中台同步的类型为0：销售出库，1：是退货出库类型 2=直采
        if("1".equals(vo.getType())) {
            plan.setType(DictConstant.DICT_B_OUT_PLAN_TYPE_TH);
        }else if("0".equals(vo.getType())){
            plan.setType(DictConstant.DICT_B_OUT_PLAN_TYPE_XS);
        }else if("2".equals(vo.getType())){
            plan.setType(DictConstant.DICT_B_OUT_PLAN_TYPE_ZC);
        }
        mapper.insert(plan);

        BOrderEntity bOrderEntity = outOrderMapper.selectOrderByOrderNo(vo.getOrderVo().getOrder_no());
        if (bOrderEntity == null) {
            throw new ApiBusinessException("订单【"+vo.getOrderVo().getOrder_no()+"】不存在，请先同步订单数据");
        }

        // 新增出库计划明细数据
        insertOutPlanDetail(vo,bOrderEntity,plan);
        return plan;
    }

    /**
     * 查询货主
     */
    public MOwnerEntity getOwner(ApiOutPlanVo vo) {
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
    public MCustomerEntity getConsignor(ApiOutPlanVo vo) {
        ApiCustomerVo apiCustomerVo = new ApiCustomerVo();
        apiCustomerVo.setCode(vo.getConsignor_code());
        MCustomerEntity customerEntity = customerMapper.selectByCodeAppCode(apiCustomerVo);
        if(customerEntity == null) {
            throw new ApiBusinessException(ApiResultEnum.CUSTOMER_NULL);
        }
        return customerEntity;
    }

    /**
     * 查询出库订单
     */
    public BOutOrderEntity getOrder(ApiOutPlanVo vo,MCustomerEntity client){
        BOutOrderVo outOrder = new BOutOrderVo();
        outOrder.setOrder_no(vo.getOrderVo().getOrder_no());
        // 数据库查询是否存在数据
        BOutOrderEntity outOrderList = outOrderMapper.selectOrderByContract(outOrder);
        BOutOrderEntity bOutOrderEntity;
        if (outOrderList == null) {
            // 若为空 则为新增
            bOutOrderEntity = new BOutOrderEntity();
            BeanUtilsSupport.copyProperties(vo.getOrderVo(), bOutOrderEntity);
            if(client != null) {
                bOutOrderEntity.setClient_id(client.getId());
            }
            bOutOrderEntity.setBill_type(vo.getBill_type());
            outOrderMapper.insert(bOutOrderEntity);
        } else {
            // 若不为空 则为修改
            bOutOrderEntity = outOrderList;
            BeanUtilsSupport.copyProperties(vo.getOrderVo(), bOutOrderEntity);
            if(client != null) {
                bOutOrderEntity.setClient_id(client.getId());
            }
            bOutOrderEntity.setBill_type(vo.getBill_type());
            outOrderMapper.updateById(bOutOrderEntity);
        }
        return bOutOrderEntity;
    }

    /**
     * 查询物料规格
     */
    public MGoodsSpecEntity getGoodsSpec(ApiOutPlanVo vo,ApiOutPlanDetailVo detail) {
        ApiGoodsSpecVo specVo = new ApiGoodsSpecVo();
        specVo.setCode(detail.getSpec_code());
        MGoodsSpecEntity goodsSpecEntity = goodsSpecMapper.selectByCodeAppCode(specVo);
        if(goodsSpecEntity == null) {
            throw new ApiBusinessException(ApiResultEnum.GOODS_SPEC_NULL);
        }
        return goodsSpecEntity;
    }

    /**
     * 新增出库计划明细
     */
    public void insertOutPlanDetail(ApiOutPlanVo vo,BOrderEntity bOrderEntity,BOutPlanEntity plan) {
        int no = 1;
        for(ApiOutPlanDetailVo detail:vo.getDetailList()) {
            // 库存
//            MInventoryEntity inventory =  inventoryMapper.selectById(detail.getInventory_id());

            // 赋值新增出库计划详情数据
            BOutPlanDetailEntity detailEntity = (BOutPlanDetailEntity)BeanUtilsSupport.copyProperties(detail,BOutPlanDetailEntity.class);
            if (null == detailEntity.getOver_release()) {
                detailEntity.setOver_release(Boolean.FALSE);
            }

            // 根据 staff_code 查询 staff_id
            Integer id = staffService.selectIdByStaffCode(SystemConstants.AUDIT_STAFF_CODE);

            detailEntity.setAlias(detail.getNickname());
            detailEntity.setOrder_detail_no(detail.getNo());
            detailEntity.setAuditor_id(id);
            detailEntity.setAudit_dt(LocalDateTime.now());
            detailEntity.setPlan_id(plan.getId());
            detailEntity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_PASSED);
            detailEntity.setWarehouse_id(detail.getWarehouse_id());
            // 查询库区
            MLocationEntity locationEntity = locationMapper.selectByWarehouseId(detail.getWarehouse_id());
            detailEntity.setLocation_id(locationEntity.getId());
            // 查询库位
            MBinEntity binEntity = binMapper.selectByWarehouseId(detail.getWarehouse_id());
            detailEntity.setBin_id(binEntity.getId());
            // 查询物料规格
            MGoodsSpecEntity goodsSpecEntity = getGoodsSpec(vo,detail);
            detailEntity.setSku_id(goodsSpecEntity.getId());
            detailEntity.setSku_code(goodsSpecEntity.getCode());
            detailEntity.setOrder_goods_code(detail.getOrder_commodity_code());
            detailEntity.setOrder_id(bOrderEntity.getSerial_id());
            detailEntity.setOrder_type(bOrderEntity.getSerial_type());
            detailEntity.setPrice(detail.getPrice());
//            // 查询单位换算数据
//            MGoodsUnitConvertVo unitConvert = getGoodsUnitConvert(goodsSpecEntity);
            // 查询单位數據
            MUnitVo unitVo = imUnitService.selectByCode(detail.getUnit());
            detailEntity.setUnit_id(unitVo.getId());
            detailEntity.setCount(detail.getCount());
            detailEntity.setWeight(detail.getCount());
            detailEntity.setVolume(BigDecimal.ZERO);
            detailEntity.setPending_volume(BigDecimal.ZERO);
            detailEntity.setPending_count(detail.getCount());
            detailEntity.setPending_weight(detail.getCount());
            detailEntity.setHas_handle_count(BigDecimal.ZERO);
            detailEntity.setHas_handle_weight(BigDecimal.ZERO);
            detailEntity.setHas_handle_volume(BigDecimal.ZERO);
            // 外部系统单号
            detailEntity.setExtra_code(detail.getCode());
            // 明细单号
            detailEntity.setCode(autoCodeDetail.autoCode().getCode());

            detailEntity.setOver_inventory_policy(detail.getFloat_controled());
            detailEntity.setOver_inventory_lower(detail.getFloat_down());
            detailEntity.setOver_inventory_upper(detail.getFloat_up());
            detailEntity.setNo(no);

            outPlanDetailMapper.insert(detailEntity);

            no++;
            // 生成待办
            todoService.insertTodo(detailEntity.getId(), SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL, SystemConstants.PERMS.B_OUT_PLAN_DETAIL_SUBMIT);
        }
    }

    /**
     * 必输check
     * @param  vo
     */
    public void check(ApiOutPlanVo vo) {
        // ApiInPlanVo属性必输check
        // 计划时间
        if (vo.getPlan_time() == null) {
            throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_PARAM_PLAN_TIME_NULL);
        }
        // 出库类型
        if(StringUtils.isEmpty(vo.getType())) {
            throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_PARAM_TYPE_NULL);
        }
        // 单据类型
        if(StringUtils.isEmpty(vo.getBill_type())) {
            throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_PARAM_BILL_TYPE_NULL);
        }
        // 出库计划单号
        if(StringUtils.isEmpty(vo.getCode())) {
            throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_PARAM_CODE_NULL);
        }
        // 货主编号
        if(StringUtils.isEmpty(vo.getOwner_code())) {
            throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_PARAM_OWNER_CODE_NULL);
        }
        // 委托方编号
        if(StringUtils.isEmpty(vo.getConsignor_code())) {
            throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_PARAM_CONSIGNOR_CODE_NULL);
        }


        // ApiOutPlanDetailVo是否为空
        if(vo.getDetailList() == null || vo.getDetailList().size() == 0 ) {
            throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_PARAM_DETAIL_LIST_NULL);
        }

        planDetailCheck(vo.getDetailList());

        // ApiInOrderVo是否为空
        if(vo.getOrderVo() == null) {
            throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_PARAM_ORDER_VO_NULL);
        }
        // ApiInOrderVo必输属性check
        orderCheck(vo.getOrderVo());
    }

    /**
     * 出库详情必输check
     */
    public void planDetailCheck(List<ApiOutPlanDetailVo> voList) {
        for(ApiOutPlanDetailVo vo:voList){
            // 单价
            if(vo.getPrice() == null) {
                throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_DETAIL_PARAM_PRICE_NULL);
            }
            // 数量
            if(vo.getCount() == null) {
                throw new ApiBusinessException(ApiResultEnum.OUT_PLAN_DETAIL_PARAM_COUNT_NULL);
            }
            // 单位
            if(StringUtils.isEmpty(vo.getUnit())) {
                throw new ApiBusinessException(ApiResultEnum.IN_PLAN_DETAIL_PARAM_UNIT_NULL);
            }
            // 库存id
//            if(vo.getInventory_id() == null) {
//                throw new ApiBusinessException(ApiResultEnum.IN_PLAN_DETAIL_PARAM_COUNT_NULL);
//            }
        }

    }

    /**
     * 出库订单必输check
     */
    public void orderCheck(ApiOutOrderVo vo) {
        if(StringUtils.isEmpty(vo.getOrder_no())) {
            throw new ApiBusinessException(ApiResultEnum.OUT_ORDER_PARAM_ORDER_NO_NULL);
        }
    }

}
