package com.xinyirun.scm.core.system.serviceimpl.business.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.wms.inplan.BInPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.inplan.BInPlanEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutPlanEntity;
import com.xinyirun.scm.bean.entity.busniess.schedule.BScheduleEntity;
import com.xinyirun.scm.bean.entity.busniess.schedule.BScheduleInfoEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MBinEntity;
import com.xinyirun.scm.bean.entity.mongo.monitor.v1.BMonitorDataMongoEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderVo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutPlanDetailVo;
import com.xinyirun.scm.bean.system.vo.business.schedule.BScheduleInfoVo;
import com.xinyirun.scm.bean.system.vo.business.schedule.BScheduleSumVo;
import com.xinyirun.scm.bean.system.vo.business.schedule.BScheduleVo;
import com.xinyirun.scm.bean.system.vo.excel.schedule.BScheduleExcelVo;
import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitCalcVo;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.wms.in.order.BInOrderMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.inplan.BInPlanDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.inplan.BInPlanMapper;
import com.xinyirun.scm.core.system.mapper.business.order.BOrderMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.out.BOutPlanDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.out.BOutPlanMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.out.order.BOutOrderMapper;
import com.xinyirun.scm.core.system.mapper.business.schedule.BScheduleMapper;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MBinMapper;
import com.xinyirun.scm.core.system.service.business.wms.inplan.IBInPlanDetailService;
import com.xinyirun.scm.core.system.service.business.wms.out.IBOutPlanDetailService;
import com.xinyirun.scm.core.system.service.business.schedule.IBScheduleInfoService;
import com.xinyirun.scm.core.system.service.business.schedule.IBScheduleService;
import com.xinyirun.scm.core.system.service.master.goods.unit.IMGoodsUnitCalcService;
import com.xinyirun.scm.core.system.service.master.user.IMStaffService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.business.todo.TodoService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.*;
import com.xinyirun.scm.core.system.serviceimpl.sys.unit.SUnitServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import com.xinyirun.scm.mongodb.service.monitor.v1.IMonitorDataMongoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 调度服务实现类
 * </p>
 *
 * @author wwl
 * @since 2022-01-10
 */
@Service
@Slf4j
public class BScheduleServiceImpl extends BaseServiceImpl<BScheduleMapper, BScheduleEntity> implements IBScheduleService {

    @Autowired
    private BScheduleMapper mapper;

    @Autowired
    private BInPlanAutoCodeServiceImpl inPlanAutoCode;

    @Autowired
    private BOutPlanAutoCodeServiceImpl outPlanAutoCode;

    @Autowired
    private BInPlanMapper bInPlanMapper;

    @Autowired
    private BInPlanDetailMapper bInPlanDetailMapper;

    @Autowired
    private BInPlanDetailAutoCodeServiceImpl bInPlanDetailAutoCodeService;

    @Autowired
    private BOutPlanMapper bOutPlanMapper;

    @Autowired
    private BOutPlanDetailMapper bOutPlanDetailMapper;

    @Autowired
    private BOutPlanDetailAutoCodeServiceImpl bOutPlanDetailAutoCodeService;

    @Autowired
    private MBinMapper binMapper;

    @Autowired
    private IBOutPlanDetailService ibOutPlanDetailService;

    @Autowired
    private IBInPlanDetailService ibInPlanDetailService;

    @Autowired
    private BOrderMapper orderMapper;

    @Autowired
    BInOrderMapper bInOrderMapper;

    @Autowired
    BOutOrderMapper bOutOrderMapper;

    @Autowired
    SUnitServiceImpl sUnitServiceImpl;

    @Autowired
    private BScheduleAutoCodeServiceImpl autoCode;

    @Autowired
    private IBScheduleInfoService ibScheduleInfoService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private IMGoodsUnitCalcService imGoodsUnitCalcService;

    @Autowired
    private ISConfigService configService;

    @Resource
    @Lazy // 延迟加载, 解决循环依赖
//    private IBMonitorService monitorService;

    @Autowired
    private IMonitorDataMongoService mongoService;

    @Autowired
    private IMStaffService staffService;

    @Autowired
    private ISConfigService isConfigService;

    /**
     * 查询分页数据
     */
    @Override
//    @DataScopeAnnotion(type = "02", type02_condition = "t.in_warehouse_id,t.out_warehouse_id")
    @DataScopeAnnotion(type = "02", type02_condition = "t.in_warehouse_id,t.out_warehouse_id")
    public IPage<BScheduleVo> selectPage(BScheduleVo searchCondition) {
        // 分页条件
        Page<BScheduleEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    public BScheduleVo get(BScheduleVo searchCondition) {
        return mapper.getDetail(searchCondition);
    }

    @Override
    public BScheduleVo selectByScheduleId(BScheduleVo searchCondition) {
        return mapper.selectByScheduleId(searchCondition.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BScheduleVo> insert1(BScheduleVo vo) {
        BScheduleEntity entity = new BScheduleEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        entity.setType("1");
        entity.setIn_schedule_qty(vo.getActual_count());
        entity.setIn_balance_qty(vo.getActual_count());
        entity.setIn_operated_qty(BigDecimal.ZERO);
        entity.setOut_schedule_qty(vo.getActual_count());
        entity.setOut_balance_qty(vo.getActual_count());
        entity.setOut_operated_qty(BigDecimal.ZERO);
        entity.setCode(autoCode.autoCode().getCode());

        // 查询发货库位
        MBinEntity binEntity = binMapper.selecBinByWarehouseId(vo.getOut_warehouse_id());
        entity.setOut_bin_id(binEntity.getId());
        vo.setOut_bin_id(binEntity.getId());

        // 查询发货库区
//        MLocationEntity locationEntity = locationMapper.selectLocationByWarehouseId(vo.getOut_warehouse_id());
        entity.setOut_location_id(binEntity.getLocation_id());
        vo.setOut_location_id(binEntity.getLocation_id());

        // 查询收货库区
//        locationEntity = locationMapper.selectLocationByWarehouseId(vo.getIn_warehouse_id());
        // 查询收货库位
        binEntity = binMapper.selecBinByWarehouseId(vo.getIn_warehouse_id());
        entity.setIn_location_id(binEntity.getLocation_id());
        vo.setIn_location_id(binEntity.getLocation_id());

        entity.setIn_bin_id(binEntity.getId());
        vo.setIn_bin_id(binEntity.getId());

        entity.setStatus(DictConstant.DICT_B_SCHEDULE_STATUS_TWO);

//        BOutPlanDetailEntity outPlanDetail = null;
        BOutPlanDetailVo outPlanDetailVo = null;
        if (Objects.equals(entity.getOut_type(), DictConstant.DICT_B_MONITOR_TYPE_OUT_OUT)) {
            if (!Objects.equals(vo.getOut_rule(), DictConstant.DICT_B_LOGISTICS_OUT_RULE_0)) {
                // 系统生成入库计划, 改审核后生成
//                outPlanDetail = insertOutPlan(vo, entity);
//                log.info("系统生成入库计划");
//                entity.setOut_plan_detail_id(outPlanDetail.getId());
//                entity.setOut_plan_detail_code(outPlanDetail.getCode());
//            } else {
                // 手动选择出库计划
                outPlanDetailVo = ibOutPlanDetailService.selectById(vo.getOut_plan_detail_id());
                entity.setOut_plan_detail_id(outPlanDetailVo.getId());
                entity.setOut_plan_detail_code(outPlanDetailVo.getCode());
            }
        }

        // 手动选择入库计划, 绑定
        if (Objects.equals(entity.getIn_type(), DictConstant.DICT_B_MONITOR_TYPE_IN_IN)) {
            if (!Objects.equals(vo.getIn_rule(), DictConstant.DICT_B_LOGISTICS_IN_RULE_0)) {
//                BInPlanDetailVo bInPlanDetailVo = ibInPlanDetailService.selectById(vo.getIn_plan_detail_id());
//                entity.setIn_plan_detail_id(bInPlanDetailVo.getId());
//                entity.setIn_plan_detail_code(bInPlanDetailVo.getCode());
            }
        }


//        BInPlanDetailEntity inPlanDetail = null;
//        BInPlanDetailVo inPlanDetailVo = null;
//        if (Objects.equals(entity.getIn_type(), DictConstant.DICT_B_MONITOR_TYPE_IN_IN)) {
//            // 生成入库计划, 此处入库计划在审核出生成
//            // 系统生成入库计划
//            inPlanDetail = insertInPlan(vo, entity);
//
//            entity.setIn_plan_detail_id(inPlanDetail.getId());
//            entity.setIn_plan_detail_code(inPlanDetail.getCode());
//
//            if (outPlanDetailVo != null) {
//                inPlanDetail.setOrder_detail_no(outPlanDetailVo.getOrder_detail_no());
//                bInPlanDetailMapper.updateById(inPlanDetail);
//            }
//
//        }

        entity.setId(null);

        int rtn = mapper.insert(entity);

        BScheduleInfoVo bScheduleInfoVo = new BScheduleInfoVo();
        bScheduleInfoVo.setSchedule_id(entity.getId());
        bScheduleInfoVo.setWaybill_contract_no(vo.getWaybill_contract_no());
        bScheduleInfoVo.setCustomer_code(vo.getCustomer_code());
        bScheduleInfoVo.setCustomer_name(vo.getCustomer_name());
        bScheduleInfoVo.setCustomer_id(vo.getCustomer_id());
        ibScheduleInfoService.insert(bScheduleInfoVo);
        // 更新后查询 更新数据
        BScheduleVo bScheduleVo = mapper.selectListById(entity.getId());
        return InsertResultUtil.OK(bScheduleVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BScheduleVo> insert2(BScheduleVo vo) {
        BScheduleEntity entity = new BScheduleEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        entity.setType("2");
        entity.setIn_schedule_qty(vo.getActual_count());
        entity.setIn_balance_qty(vo.getActual_count());
        entity.setIn_operated_qty(BigDecimal.ZERO);
        entity.setOut_schedule_qty(vo.getActual_count());
        entity.setOut_balance_qty(vo.getActual_count());
        entity.setOut_operated_qty(BigDecimal.ZERO);
        entity.setCode(autoCode.autoCode().getCode());

        // 查询发货库位
        MBinEntity binEntity = binMapper.selecBinByWarehouseId(vo.getOut_warehouse_id());
        entity.setOut_bin_id(binEntity.getId());
        vo.setOut_bin_id(binEntity.getId());

        // 查询发货库区
//        MLocationEntity locationEntity = locationMapper.selectLocationByWarehouseId(vo.getOut_warehouse_id());
        entity.setOut_location_id(binEntity.getLocation_id());
        vo.setOut_location_id(binEntity.getLocation_id());

        // 查询收货库区
//        locationEntity = locationMapper.selectLocationByWarehouseId(vo.getIn_warehouse_id());
        // 查询收货库位
        binEntity = binMapper.selecBinByWarehouseId(vo.getIn_warehouse_id());
        entity.setIn_location_id(binEntity.getLocation_id());
        vo.setIn_location_id(binEntity.getLocation_id());

        entity.setIn_bin_id(binEntity.getId());
        vo.setIn_bin_id(binEntity.getId());

        entity.setStatus(DictConstant.DICT_B_SCHEDULE_STATUS_TWO);

        BOutPlanDetailEntity outPlanDetail = null;
        BOutPlanDetailVo outPlanDetailVo = null;
        if (Objects.equals(entity.getOut_type(), DictConstant.DICT_B_MONITOR_TYPE_OUT_OUT)) {
            if (!Objects.equals(vo.getOut_rule(), DictConstant.DICT_B_LOGISTICS_OUT_RULE_0)) {
                // 系统生成入库计划, 同 insert1
//                outPlanDetail = insertOutPlan(vo, entity);

//                entity.setOut_plan_detail_id(outPlanDetail.getId());
//                entity.setOut_plan_detail_code(outPlanDetail.getCode());
//            } else {
                // 手动选择出库计划
                outPlanDetailVo = ibOutPlanDetailService.selectById(vo.getOut_plan_detail_id());
                entity.setOut_plan_detail_id(outPlanDetailVo.getId());
                entity.setOut_plan_detail_code(outPlanDetailVo.getCode());
            }
        }

//        BInPlanDetailEntity inPlanDetail = null;
//        BInPlanDetailVo inPlanDetailVo = null;
        if (Objects.equals(entity.getIn_type(), DictConstant.DICT_B_MONITOR_TYPE_IN_IN)) {
            // 生成入库计划
            if (Objects.equals(entity.getIn_type(), DictConstant.DICT_B_MONITOR_TYPE_IN_IN)) {
                if (Objects.equals(vo.getIn_rule(), DictConstant.DICT_B_LOGISTICS_IN_RULE_0)) {
                    // 系统生成入库计划
                /*    inPlanDetail = insertInPlan(vo, entity);

                    entity.setIn_plan_detail_id(inPlanDetail.getId());
                    entity.setIn_plan_detail_code(inPlanDetail.getCode());

                    if (outPlanDetailVo != null) {
                        inPlanDetail.setOrder_detail_no(outPlanDetailVo.getOrder_detail_no());
                        bInPlanDetailMapper.updateById(inPlanDetail);
                    }*/
                } else {
                    // 手动选择出库计划
//                    inPlanDetailVo = ibInPlanDetailService.selectById(vo.getIn_plan_detail_id());
//                    entity.setIn_plan_detail_id(outPlanDetailVo.getId());
//                    entity.setIn_plan_detail_code(outPlanDetailVo.getCode());
                }
            }

        }

        entity.setId(null);

        int rtn = mapper.insert(entity);

        BScheduleInfoVo bScheduleInfoVo = new BScheduleInfoVo();
        bScheduleInfoVo.setSchedule_id(entity.getId());
        bScheduleInfoVo.setWaybill_contract_no(vo.getWaybill_contract_no());
        bScheduleInfoVo.setCustomer_code(vo.getCustomer_code());
        bScheduleInfoVo.setCustomer_name(vo.getCustomer_name());
        bScheduleInfoVo.setCustomer_id(vo.getCustomer_id());
        ibScheduleInfoService.insert(bScheduleInfoVo);
        // 更新后查询 更新数据
        BScheduleVo bScheduleVo = mapper.selectListById(entity.getId());
        return InsertResultUtil.OK(bScheduleVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> save(BScheduleVo vo) {
        BScheduleEntity entity = mapper.selectById(vo.getId());
        BeanUtilsSupport.copyProperties(vo, entity);
        int rtn = mapper.updateById(entity);
        return UpdateResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> submit(List<BScheduleVo> searchCondition) {
        int updCount = 0;

        List<BScheduleEntity> list = mapper.selectIdsIn(searchCondition);
        for (BScheduleEntity entity : list) {
            checkLogic(entity, CheckResultAo.SUBMIT_CHECK_TYPE);
            entity.setStatus(DictConstant.DICT_B_SCHEDULE_STATUS_THREE);
            updCount = mapper.updateById(entity);

            if (updCount == 0) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }

        return UpdateResultUtil.OK(updCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> audit(List<BScheduleVo> searchCondition) {
        int updCount = 0;

        List<BScheduleEntity> list = mapper.selectIdsIn(searchCondition);
        // 此处判断是否是 系统生成 出库计划, 如果是, 审核的时候生成出库计划
        for (BScheduleEntity entity : list) {
            // 兼容旧数据, 已经在创建时生成出库计划和入库计划的不在添加
            BOutPlanDetailEntity bOutPlanDetailEntity = new BOutPlanDetailEntity();
            switch (entity.getType()) {
                // 直采入库（只生成入库计划）
                case DictConstant.DICT_B_SCHEDULE_TYPE_4:
                    if (StringUtils.isNull(entity.getIn_plan_detail_id()) && DictConstant.DICT_B_SCHEDULE_IN_TYPE_IN.equals(entity.getIn_type())) {
                            // 生成入库计划
                        BInPlanDetailEntity inPlanDetail = insertInPlan(entity);
                        entity.setIn_plan_detail_id(inPlanDetail.getId());
                        entity.setIn_plan_detail_code(inPlanDetail.getCode());
//                        inPlanDetail.setOrder_detail_no(bOutPlanDetailEntity.getOrder_detail_no());
                        bInPlanDetailMapper.updateById(inPlanDetail);

                    }
                    break;
                // 直销出库 （只生成出库计划）
                case DictConstant.DICT_B_SCHEDULE_TYPE_5:
                    if (StringUtils.isNull(entity.getOut_plan_detail_id()) && DictConstant.DICT_B_LOGISTICS_OUT_RULE_0.equals(entity.getOut_rule())) {

                        // 生成出库计划
                        bOutPlanDetailEntity = insertOutPlanType5(entity);
                        entity.setOut_plan_detail_id(bOutPlanDetailEntity.getId());
                        entity.setOut_plan_detail_code(bOutPlanDetailEntity.getCode());
                    }
                    break;
                default:
                    if (StringUtils.isNull(entity.getOut_plan_detail_id())) {
                        if (DictConstant.DICT_B_LOGISTICS_OUT_RULE_0.equals(entity.getOut_rule())) {
                            // 生成出库计划
                            bOutPlanDetailEntity = insertOutPlan(entity);
                            entity.setOut_plan_detail_id(bOutPlanDetailEntity.getId());
                            entity.setOut_plan_detail_code(bOutPlanDetailEntity.getCode());
                        }
                    }
                    if (StringUtils.isNull(entity.getIn_plan_detail_id())) {
                        if (DictConstant.DICT_B_SCHEDULE_IN_TYPE_IN.equals(entity.getIn_type())) {
                            // 生成入库计划
                            BInPlanDetailEntity inPlanDetail = insertInPlan(entity);
                            entity.setIn_plan_detail_id(inPlanDetail.getId());
                            entity.setIn_plan_detail_code(inPlanDetail.getCode());
//                            inPlanDetail.setOrder_detail_no(bOutPlanDetailEntity.getOrder_detail_no());
                            bInPlanDetailMapper.updateById(inPlanDetail);
                        }
                    }
                    break;
            }
            //
            checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE);
            entity.setStatus(DictConstant.DICT_B_SCHEDULE_STATUS_ZERO);
            updCount = mapper.updateById(entity);

            if (updCount == 0) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }

        return UpdateResultUtil.OK(updCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> reject(List<BScheduleVo> searchCondition) {
        int updCount = 0;

        List<BScheduleEntity> list = mapper.selectIdsIn(searchCondition);
        for (BScheduleEntity entity : list) {
            checkLogic(entity, CheckResultAo.REJECT_CHECK_TYPE);
            entity.setStatus(DictConstant.DICT_B_SCHEDULE_STATUS_FOUR);
            updCount = mapper.updateById(entity);

            if (updCount == 0) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }

        return UpdateResultUtil.OK(updCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> finish(List<BScheduleVo> searchCondition) {
        int updCount = 0;

        List<BScheduleEntity> list = mapper.selectIdsIn(searchCondition);
        for (BScheduleEntity entity : list) {
            checkLogic(entity, CheckResultAo.FINISH_CHECK_TYPE);
            entity.setStatus(DictConstant.DICT_B_SCHEDULE_STATUS_ONE);
            updCount = mapper.updateById(entity);

            if (updCount == 0) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }

        return UpdateResultUtil.OK(updCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> enable(List<BScheduleVo> searchCondition) {
        int updCount = 0;

        List<BScheduleEntity> list = mapper.selectIdsIn(searchCondition);
        for (BScheduleEntity entity : list) {
            checkLogic(entity, CheckResultAo.ENABLE_CHECK_TYPE);

            BOutPlanDetailEntity bOutPlanDetailEntity = bOutPlanDetailMapper.selectById(entity.getOut_plan_detail_id());
            if (bOutPlanDetailEntity != null) {
                if (bOutPlanDetailEntity.getStatus().equals(DictConstant.DICT_B_OUT_PLAN_STATUS_FINISH)
                        || bOutPlanDetailEntity.getStatus().equals(DictConstant.DICT_B_OUT_PLAN_STATUS_DISCONTINUE)) {
                    throw new UpdateErrorException("该物流订单关联的出库计划已中止/已完成，无法重启");
                }
            }


            entity.setStatus(DictConstant.DICT_B_SCHEDULE_STATUS_ZERO);
            updCount = mapper.updateById(entity);

            if (updCount == 0) {
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }

        return UpdateResultUtil.OK(updCount);
    }

    /**
     * get
     * 导出
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<BScheduleExcelVo> selectList(List<BScheduleVo> searchCondition) {
        return mapper.selectExportList(searchCondition);
    }

    /**
     * 导出全部
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t.in_warehouse_id,t.out_warehouse_id")
    public List<BScheduleExcelVo> selectListExportAll(BScheduleVo searchCondition) {
        // 导出限制开关
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectExportNum(searchCondition);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.selectExportListAll(searchCondition);
    }

    /**
     * 物流订单增加合计
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "02", type02_condition = "t.in_warehouse_id,t.out_warehouse_id")
    public BScheduleSumVo sumData(BScheduleVo searchCondition) {
        return mapper.selectSumData(searchCondition);
    }

    /**
     * 根据 sku_id, out_owner_id, out_warehouse_id 查询可调度库存
     *
     * @param searchCondition
     * @return
     */
    @Override
    public BigDecimal getScheduleQty(BScheduleVo searchCondition) {
//        //直采出库  运输数量 取值 发货数量
//        if (searchCondition.getType()!=null&&searchCondition.getType().equals(DictConstant.DICT_B_SCHEDULE_TYPE_4)){
//            return mapper.getScheduleByTypeQty(searchCondition);
//        }

        return mapper.getScheduleQty(searchCondition);
    }

    /**
     * 更新物流订单
     *
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BScheduleVo> update1(BScheduleVo vo) {
        Integer id = vo.getId();
        Assert.notNull(id, "ID 不能为空");
        // 查询旧数据
//        BScheduleVo detail = mapper.getDetail(vo);
        // 检查状态是否可修改
        BScheduleEntity entity = new BScheduleEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        checkLogic(entity, CheckResultAo.UPDATE_CHECK_TYPE);
        entity.setIn_schedule_qty(vo.getActual_count());
        entity.setIn_balance_qty(vo.getActual_count());
        entity.setIn_operated_qty(BigDecimal.ZERO);
        entity.setOut_schedule_qty(vo.getActual_count());
        entity.setOut_balance_qty(vo.getActual_count());
        entity.setOut_operated_qty(BigDecimal.ZERO);

        // 设置发货库位
        MBinEntity binEntity = binMapper.selecBinByWarehouseId(vo.getOut_warehouse_id());
        entity.setOut_bin_id(binEntity.getId());
        vo.setOut_bin_id(binEntity.getId());

        // 设置发货库区
        entity.setOut_location_id(binEntity.getLocation_id());
        vo.setOut_location_id(binEntity.getLocation_id());

        // 设置收货库位
        binEntity = binMapper.selecBinByWarehouseId(vo.getIn_warehouse_id());
        entity.setIn_location_id(binEntity.getLocation_id());
        vo.setIn_location_id(binEntity.getLocation_id());

        // 设置收货库区
        entity.setIn_bin_id(binEntity.getId());
        vo.setIn_bin_id(binEntity.getId());

        BOutPlanDetailVo outPlanDetailVo = null;

        if (Objects.equals(entity.getOut_type(), DictConstant.DICT_B_MONITOR_TYPE_OUT_OUT)) {
            if (!Objects.equals(vo.getOut_rule(), DictConstant.DICT_B_LOGISTICS_OUT_RULE_0)) {
                // 手动选择出库计划
                outPlanDetailVo = ibOutPlanDetailService.selectById(vo.getOut_plan_detail_id());
                entity.setOut_plan_detail_id(outPlanDetailVo.getId());
                entity.setOut_plan_detail_code(outPlanDetailVo.getCode());
            }
        }
        // 手动选择 入库计划
        if (Objects.equals(entity.getIn_type(), DictConstant.DICT_B_MONITOR_TYPE_IN_IN)) {
            if (!Objects.equals(vo.getIn_rule(), DictConstant.DICT_B_LOGISTICS_IN_RULE_0)) {
//                BInPlanDetailVo bInPlanDetailVo = ibInPlanDetailService.selectById(vo.getIn_plan_detail_id());
//                entity.setIn_plan_detail_id(bInPlanDetailVo.getId());
//                entity.setIn_plan_detail_code(bInPlanDetailVo.getCode());
            }
        }

        // 更新状态
        updateScheduleStatus(entity);
        mapper.updateById(entity);
        updateScheduleInfo(vo);
        // 更新后查询 更新数据
        BScheduleVo bScheduleVo = mapper.selectListById(entity.getId());
        return UpdateResultUtil.OK(bScheduleVo);
    }

    /**
     * 更新状态
     * 已驳回 -> 制单, 其他不变
     *
     * @param entity
     */
    private void updateScheduleStatus(BScheduleEntity entity) {
        if (DictConstant.DICT_B_SCHEDULE_STATUS_FOUR.equals(entity.getStatus())) {
            entity.setStatus(DictConstant.DICT_B_SCHEDULE_STATUS_TWO);
        }
    }

    /**
     * 更新 物流详情
     *
     * @param vo
     */
    private void updateScheduleInfo(BScheduleVo vo) {
        BScheduleInfoEntity infoEntity = ibScheduleInfoService.getOne(new LambdaQueryWrapper<BScheduleInfoEntity>()
                .eq(BScheduleInfoEntity::getSchedule_id, vo.getId()));
        infoEntity.setWaybill_contract_no(vo.getWaybill_contract_no());
        infoEntity.setCustomer_code(vo.getCustomer_code());
        infoEntity.setCustomer_name(vo.getCustomer_name());
        infoEntity.setCustomer_id(vo.getCustomer_id());
        ibScheduleInfoService.updateById(infoEntity);
    }

    /**
     * 更新物流调度
     *
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BScheduleVo> update2(BScheduleVo vo) {
        Integer id = vo.getId();
        Assert.notNull(id, "ID 不能为空");
        // 查询旧数据
//        BScheduleVo detail = mapper.getDetail(vo);
        // 检查状态是否可修改
        BScheduleEntity entity = new BScheduleEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        checkLogic(entity, CheckResultAo.UPDATE_CHECK_TYPE);
        entity.setIn_schedule_qty(vo.getActual_count());
        entity.setIn_balance_qty(vo.getActual_count());
        entity.setIn_operated_qty(BigDecimal.ZERO);
        entity.setOut_schedule_qty(vo.getActual_count());
        entity.setOut_balance_qty(vo.getActual_count());
        entity.setOut_operated_qty(BigDecimal.ZERO);
        entity.setCode(autoCode.autoCode().getCode());

        // 查询发货库位
        MBinEntity binEntity = binMapper.selecBinByWarehouseId(vo.getOut_warehouse_id());
        entity.setOut_bin_id(binEntity.getId());
        vo.setOut_bin_id(binEntity.getId());

        entity.setOut_location_id(binEntity.getLocation_id());
        vo.setOut_location_id(binEntity.getLocation_id());

        // 查询收货库位
        binEntity = binMapper.selecBinByWarehouseId(vo.getIn_warehouse_id());
        entity.setIn_location_id(binEntity.getLocation_id());
        vo.setIn_location_id(binEntity.getLocation_id());

        entity.setIn_bin_id(binEntity.getId());
        vo.setIn_bin_id(binEntity.getId());

        BOutPlanDetailVo outPlanDetailVo = null;
        if (Objects.equals(entity.getOut_type(), DictConstant.DICT_B_MONITOR_TYPE_OUT_OUT)) {
            if (!Objects.equals(vo.getOut_rule(), DictConstant.DICT_B_LOGISTICS_OUT_RULE_0)) {
                // 手动选择出库计划
                outPlanDetailVo = ibOutPlanDetailService.selectById(vo.getOut_plan_detail_id());
                entity.setOut_plan_detail_id(outPlanDetailVo.getId());
                entity.setOut_plan_detail_code(outPlanDetailVo.getCode());
            }
        }

//        BInPlanDetailVo inPlanDetailVo = null;
//        if (Objects.equals(entity.getIn_type(), DictConstant.DICT_B_MONITOR_TYPE_IN_IN)) {
//            // 生成入库计划
//            if (Objects.equals(entity.getIn_type(), DictConstant.DICT_B_MONITOR_TYPE_IN_IN)) {
//                if (!Objects.equals(vo.getIn_rule(), DictConstant.DICT_B_LOGISTICS_IN_RULE_0)) {
//                    // 手动选择入库计划
//                    inPlanDetailVo = ibInPlanDetailService.selectById(vo.getIn_plan_detail_id());
//
//                    entity.setIn_plan_detail_id(inPlanDetailVo.getId());
//                    entity.setIn_plan_detail_code(inPlanDetailVo.getCode());
//                }
//            }
//        }
        // 更新状态
        updateScheduleStatus(entity);
        mapper.updateById(entity);
        // 更新详情
        updateScheduleInfo(vo);
        // 更新后查询 更新数据
        BScheduleVo bScheduleVo = mapper.selectListById(entity.getId());
        return UpdateResultUtil.OK(bScheduleVo);
    }

    /**
     * 作废
     *
     * @param bean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(BScheduleVo bean) {
        Integer id = bean.getId();
        // 校验状态
        BScheduleEntity entity = mapper.selectById(id);
        checkLogic(entity, CheckResultAo.CANCEL_CHECK_TYPE);
        // 校验入库计划是否作废
        if (DictConstant.DICT_B_LOGISTICS_IN_RULE_0.equals(entity.getIn_rule()) && StringUtils.isNotNull(entity.getIn_plan_detail_id())) {
//            ibInPlanDetailService.checkPalnStatus(entity.getIn_plan_detail_id());
        }
        // 校验出库计划是否作废, 只校验系统自动生成的
        if (StringUtils.isNotNull(entity.getOut_plan_detail_id()) && DictConstant.DICT_B_LOGISTICS_OUT_RULE_0.equals(entity.getOut_rule())) {
            ibOutPlanDetailService.checkPalnStatus(entity.getOut_plan_detail_id());
        }
        // 校验物流订单是否有监管任务, 如果有, 判断监管任务是否是作废状态, 不是报错
//        monitorService.checkMonitorStatus(id);
        entity.setStatus(DictConstant.DICT_B_SCHEDULE_STATUS_FIVE);
        entity.setRemark(bean.getRemark());
        mapper.updateById(entity);
    }

    /**
     * 物流订单删除
     *
     * @param bean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<BScheduleVo> bean) {
        // 判断物流状态是否为已作废
        Set<Integer> ids = bean.stream().map(BScheduleVo::getId).collect(Collectors.toSet());
        List<BScheduleEntity> entities = mapper.selectBatchIds(ids);
        for (BScheduleEntity entity : entities) {
            StringBuilder sb = new StringBuilder("该物流订单已存在下发的");
            Boolean flag = false;
            // 判断是否存在入库计划
            if (entity.getIn_plan_detail_id() != null) {
                sb.append(" 入库计划(");
                sb.append(entity.getIn_plan_code());
                sb.append(")");
                flag = true;
            }
            // 判断是否存在出库计划
            if (DictConstant.DICT_B_LOGISTICS_OUT_RULE_0.equals(entity.getOut_rule()) && entity.getOut_plan_detail_id() != null) {
                sb.append(" 出库计划(");
                sb.append(entity.getOut_plan_code());
                sb.append(")");
                flag = true;
            }
            sb.append("，无法删除");
            if (flag) {
                throw new BusinessException(sb.toString());
            }
            checkLogic(entity, CheckResultAo.DELETE_CHECK_TYPE);
            entity.setIs_delete(DictConstant.DICT_B_IS_DELETE_TRUE);
        }
        // 执行删除
        this.updateBatchById(entities);
    }

    /**
     * @param orderId
     * @return
     */
    @Override
    public List<BScheduleVo> selectScheduleByOrderId(Integer orderId, String orderType) {
        return mapper.selectScheduleByOrderId(orderId, orderType);
    }

    /**
     * 查询 监管任务 是否有 备份
     *
     * @param bean
     * @return
     */
    @Override
    public BScheduleVo selectMonitorIsBackup(BScheduleVo bean) {
        BScheduleVo result = new BScheduleVo();
        // 查询 mysql  monitor表中是否有 物流ID
        List<Integer> ids = mapper.selectMonitorByScheduleId(bean.getSchedule_id());
        // 查询 mongodb 中是否有此物流 ID
        List<BMonitorDataMongoEntity> entity = mongoService.selectByScheduleId(bean.getSchedule_id());
        if ((!CollectionUtils.isEmpty(ids) && ids.size() != 0) && (!CollectionUtils.isEmpty(entity) && entity.size() != 0)) {
            result.setIs_backup("2");
        } else {
            if ((!CollectionUtils.isEmpty(ids) && ids.size() != 0) && (CollectionUtils.isEmpty(entity) || entity.size() == 0)) {
                result.setIs_backup("1");
            }
            if ((CollectionUtils.isEmpty(ids) || ids.size() == 0) && (!CollectionUtils.isEmpty(entity) && entity.size() != 0)) {
                result.setIs_backup("3");
            }
        }
        return result;
    }

    /**
     * 完成 物流订单Id
     *
     * @param scheduleIds 物流订单Id
     */
    @Override
    public void completeSchedule(List<Integer> scheduleIds) {
        mapper.selectBatchIds(scheduleIds).forEach(entity -> {
            if (DictConstant.DICT_B_SCHEDULE_STATUS_TWO.equals(entity.getStatus())
            || DictConstant.DICT_B_SCHEDULE_STATUS_THREE.equals(entity.getStatus())
            || DictConstant.DICT_B_SCHEDULE_STATUS_FOUR.equals(entity.getStatus())
            ) {
                entity.setStatus(DictConstant.DICT_B_SCHEDULE_STATUS_FIVE);
                mapper.updateById(entity);
            } else if (DictConstant.DICT_B_SCHEDULE_STATUS_ZERO.equals(entity.getStatus())) {
                entity.setStatus(DictConstant.DICT_B_SCHEDULE_STATUS_ONE);
                mapper.updateById(entity);
            }
        });
    }


    /**
     * 新增入库计划数据
     */
    public BInPlanDetailEntity insertInPlan(BScheduleEntity entity) {
//        entity.setId(null);
        // 生成入库计划单号
        String no = inPlanAutoCode.autoCode().getCode();

//        // 更新入库订单
//        BInOrderEntity bInOrderEntity = saveOrder(vo,supplier);

        // 新增入库计划逻辑
        BInPlanEntity plan = (BInPlanEntity) BeanUtilsSupport.copyProperties(entity, BInPlanEntity.class);
        plan.setId(null);
//        plan.setExtra_code(entity.getCode());
        plan.setCode(no);
        plan.setPlan_time(LocalDateTime.now());
        plan.setOwner_id(entity.getIn_owner_id());
        plan.setOwner_code(entity.getIn_owner_code());
        plan.setConsignor_id(entity.getIn_consignor_id());
        plan.setConsignor_code(entity.getIn_consignor_code());
        plan.setType(DictConstant.DICT_B_IN_PLAN_TYPE_JG);

        bInPlanMapper.insert(plan);
        entity.setIn_plan_code(plan.getCode());

        // 新增入库计划明细逻辑
        return insertInPlanDetail(entity, plan);
    }

    /**
     * 新增入库计划明细
     */
    public BInPlanDetailEntity insertInPlanDetail(BScheduleEntity vo, BInPlanEntity plan) {

        // 赋值新增入库计划详情
        BInPlanDetailEntity detailEntity = new BInPlanDetailEntity();
//        detailEntity.setStatus(DictConstant.DICT_B_IN_PLAN_STATUS_APPROVED);
        detailEntity.setSku_id(vo.getSku_id());
        detailEntity.setSku_code(vo.getSku_code());
        detailEntity.setWarehouse_id(vo.getIn_warehouse_id());
//        detailEntity.setPlan_id(plan.getId());

        MGoodsUnitCalcVo mGoodsUnitCalcVo = new MGoodsUnitCalcVo();
        mGoodsUnitCalcVo.setSrc_unit_id(vo.getIn_unit_id());
        mGoodsUnitCalcVo.setSku_id(vo.getSku_id());
        MGoodsUnitCalcVo goodsUnitCalcVo = imGoodsUnitCalcService.selectOne(mGoodsUnitCalcVo);
        if (goodsUnitCalcVo == null) {
            detailEntity.setWeight(vo.getIn_schedule_qty());
//            detailEntity.setPending_weight(vo.getIn_schedule_qty());
        } else {
            detailEntity.setWeight(vo.getIn_schedule_qty().multiply(goodsUnitCalcVo.getCalc()));
//            detailEntity.setPending_weight(vo.getIn_schedule_qty().multiply(goodsUnitCalcVo.getCalc()));
        }

//        detailEntity.setCount(vo.getIn_schedule_qty());
//        detailEntity.setPending_volume(BigDecimal.ZERO);
//        detailEntity.setPending_count(vo.getIn_schedule_qty());
//        detailEntity.setHas_handle_count(BigDecimal.ZERO);
//        detailEntity.setHas_handle_weight(BigDecimal.ZERO);
//        detailEntity.setHas_handle_volume(BigDecimal.ZERO);
        detailEntity.setVolume(BigDecimal.ZERO);
        detailEntity.setUnit_id(vo.getIn_unit_id());


        BOrderVo orderVo = new BOrderVo();
        orderVo.setId(vo.getOrder_id());
        BOrderVo order = orderMapper.selectDetail(orderVo);
//        if (Objects.equals(order.getSerial_type(), SystemConstants.ORDER.B_IN_ORDER)) {
//            detailEntity.setOrder_id(order.getSerial_id());
//        }
        if (order != null) {
//            detailEntity.setOrder_id(order.getSerial_id());
//            detailEntity.setOrder_type(order.getSerial_type());
//            detailEntity.setOver_inventory_upper(order.getOver_inventory_upper());
        }

        // 审核人, 审核时间, 系统默认
        Integer id = staffService.selectIdByStaffCode(SystemConstants.AUDIT_STAFF_CODE);
//        detailEntity.setAudit_id(id);
//        detailEntity.setAudit_dt(LocalDateTime.now());

        // 明细单号
        detailEntity.setCode(bInPlanDetailAutoCodeService.autoCode().getCode());
        // 设置序号
        detailEntity.setNo(1);

        bInPlanDetailMapper.insert(detailEntity);

        // 生成待办
        todoService.insertTodo(detailEntity.getId(), SystemConstants.SERIAL_TYPE.B_IN_PLAN, SystemConstants.PERMS.B_IN_PLAN_DETAIL_OPERATE);

        return detailEntity;
    }


    /**
     * 新增出库计划逻辑
     */
    public BOutPlanDetailEntity insertOutPlan(BScheduleEntity entity) {
        // 新增出库计划逻辑
        BOutPlanEntity plan = new BOutPlanEntity();
        plan.setOwner_id(entity.getOut_owner_id());
        plan.setOwner_code(entity.getOut_owner_code());
        plan.setConsignor_id(entity.getOut_consignor_id());
        plan.setConsignor_code(entity.getOut_consignor_code());
        plan.setPlan_time(LocalDateTime.now());
        plan.setType(DictConstant.DICT_B_OUT_PLAN_TYPE_JG);
        // 生成出库计划单号
        String no = outPlanAutoCode.autoCode().getCode();
        plan.setCode(no);
        bOutPlanMapper.insert(plan);
        entity.setOut_plan_code(plan.getCode());

        // 新增出库计划明细数据
        return insertOutPlanDetail(entity, plan);
    }

    /**
     * 新增出库计划明细
     */
    public BOutPlanDetailEntity insertOutPlanDetail(BScheduleEntity entity, BOutPlanEntity plan) {
        // 库存
//            MInventoryEntity inventory =  inventoryMapper.selectById(detail.getInventory_id());

        BOutPlanDetailEntity detailEntity = new BOutPlanDetailEntity();
        detailEntity.setPlan_id(plan.getId());
        detailEntity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_PASSED);
        detailEntity.setWarehouse_id(entity.getOut_warehouse_id());
        detailEntity.setLocation_id(entity.getOut_location_id());
        detailEntity.setBin_id(entity.getOut_bin_id());

        // 查询物料规格
//        MGoodsSpecEntity goodsSpecEntity = getGoodsSpec(vo,detail);
        detailEntity.setSku_id(entity.getSku_id());
        detailEntity.setSku_code(entity.getSku_code());

//            // 查询单位换算数据
//            MGoodsUnitConvertVo unitConvert = getGoodsUnitConvert(goodsSpecEntity);
        // 查询单位數據
//        MUnitVo unitVo = imUnitService.selectByCode(detail.getUnit());
        detailEntity.setUnit_id(entity.getOut_unit_id());
        MGoodsUnitCalcVo mGoodsUnitCalcVo = new MGoodsUnitCalcVo();
        mGoodsUnitCalcVo.setSrc_unit_id(entity.getOut_unit_id());
        mGoodsUnitCalcVo.setSku_id(entity.getSku_id());
        MGoodsUnitCalcVo goodsUnitCalcVo = imGoodsUnitCalcService.selectOne(mGoodsUnitCalcVo);
        if (goodsUnitCalcVo == null) {
            detailEntity.setWeight(entity.getIn_schedule_qty());
            detailEntity.setPending_weight(entity.getIn_schedule_qty());
        } else {
            detailEntity.setWeight(entity.getIn_schedule_qty().multiply(goodsUnitCalcVo.getCalc()));
            detailEntity.setPending_weight(entity.getIn_schedule_qty().multiply(goodsUnitCalcVo.getCalc()));
        }


        detailEntity.setCount(entity.getIn_schedule_qty());
        detailEntity.setWeight(entity.getIn_schedule_qty());
        detailEntity.setVolume(BigDecimal.ZERO);
        detailEntity.setPending_volume(BigDecimal.ZERO);
        detailEntity.setPending_count(entity.getIn_schedule_qty());
        detailEntity.setPending_weight(entity.getIn_schedule_qty());
        detailEntity.setHas_handle_count(BigDecimal.ZERO);
        detailEntity.setHas_handle_weight(BigDecimal.ZERO);
        detailEntity.setHas_handle_volume(BigDecimal.ZERO);
        // 设置序号
        detailEntity.setNo(1);

        BOrderVo orderVo = new BOrderVo();
        orderVo.setId(entity.getOrder_id());
        BOrderVo order = orderMapper.selectDetail(orderVo);
//        if (Objects.equals(order.getSerial_type(), SystemConstants.ORDER.B_OUT_ORDER)) {
//            detailEntity.setOrder_id(order.getSerial_id());
//        }
        if (order != null) {
            detailEntity.setOrder_id(order.getSerial_id());
            detailEntity.setOrder_type(order.getSerial_type());
            detailEntity.setOrder_detail_no(order.getOrder_detail_no());
            detailEntity.setOver_inventory_upper(order.getOver_inventory_upper());
        }

        // 明细单号
        detailEntity.setCode(bOutPlanDetailAutoCodeService.autoCode().getCode());

        // 审核人, 审核时间, 系统默认
        Integer id = staffService.selectIdByStaffCode(SystemConstants.AUDIT_STAFF_CODE);
        detailEntity.setAuditor_id(id);
        detailEntity.setAudit_dt(LocalDateTime.now());

        bOutPlanDetailMapper.insert(detailEntity);

        // 待办事项
        todoService.insertTodo(detailEntity.getId(), SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL, SystemConstants.PERMS.B_OUT_PLAN_DETAIL_OPERATE);

        return detailEntity;
    }

    /**
     * 直销出库 新增出库计划逻辑
     */
    public BOutPlanDetailEntity insertOutPlanType5(BScheduleEntity entity) {
        // 新增出库计划逻辑
        BOutPlanEntity plan = new BOutPlanEntity();
        plan.setOwner_id(entity.getOut_owner_id());
        plan.setOwner_code(entity.getOut_owner_code());
        plan.setConsignor_id(entity.getOut_consignor_id());
        plan.setConsignor_code(entity.getOut_consignor_code());
        plan.setPlan_time(LocalDateTime.now());
        plan.setType(DictConstant.DICT_B_OUT_PLAN_TYPE_JG);
        // 生成出库计划单号
        String no = outPlanAutoCode.autoCode().getCode();
        plan.setCode(no);
        bOutPlanMapper.insert(plan);
        entity.setOut_plan_code(plan.getCode());

        // 新增出库计划明细数据
        return insertOutPlanDetailType5(entity, plan);
    }

    /**
     * 直销出库 新增出库计划明细
     */
    public BOutPlanDetailEntity insertOutPlanDetailType5(BScheduleEntity entity, BOutPlanEntity plan) {

        BOutPlanDetailEntity detailEntity = new BOutPlanDetailEntity();
        detailEntity.setPlan_id(plan.getId());
        detailEntity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_PASSED);
        detailEntity.setWarehouse_id(entity.getOut_warehouse_id());
        detailEntity.setLocation_id(entity.getOut_location_id());
        detailEntity.setBin_id(entity.getOut_bin_id());

        // 查询物料规格
        detailEntity.setSku_id(entity.getSku_id());
        detailEntity.setSku_code(entity.getSku_code());

        // 查询单位數據
        detailEntity.setUnit_id(entity.getOut_unit_id());
        MGoodsUnitCalcVo mGoodsUnitCalcVo = new MGoodsUnitCalcVo();
        mGoodsUnitCalcVo.setSrc_unit_id(entity.getOut_unit_id());
        mGoodsUnitCalcVo.setSku_id(entity.getSku_id());
        MGoodsUnitCalcVo goodsUnitCalcVo = imGoodsUnitCalcService.selectOne(mGoodsUnitCalcVo);
        if (goodsUnitCalcVo == null) {
            detailEntity.setWeight(entity.getOut_schedule_qty());
            detailEntity.setPending_weight(entity.getOut_schedule_qty());
        } else {
            detailEntity.setWeight(entity.getOut_schedule_qty().multiply(goodsUnitCalcVo.getCalc()));
            detailEntity.setPending_weight(entity.getOut_schedule_qty().multiply(goodsUnitCalcVo.getCalc()));
        }

        detailEntity.setCount(entity.getOut_schedule_qty());
        detailEntity.setWeight(entity.getOut_schedule_qty());
        detailEntity.setVolume(BigDecimal.ZERO);
        detailEntity.setPending_volume(BigDecimal.ZERO);
        detailEntity.setPending_count(entity.getOut_schedule_qty());
        detailEntity.setPending_weight(entity.getOut_schedule_qty());
        detailEntity.setHas_handle_count(BigDecimal.ZERO);
        detailEntity.setHas_handle_weight(BigDecimal.ZERO);
        detailEntity.setHas_handle_volume(BigDecimal.ZERO);
        // 设置序号
        detailEntity.setNo(1);

        BOrderVo orderVo = new BOrderVo();
        orderVo.setId(entity.getOrder_id());
        BOrderVo order = orderMapper.selectDetail(orderVo);

        if (order != null) {
            detailEntity.setOrder_id(order.getSerial_id());
            detailEntity.setOrder_type(order.getSerial_type());
            detailEntity.setOrder_detail_no(order.getOrder_detail_no());
            detailEntity.setOver_inventory_upper(order.getOver_inventory_upper());
        }

        // 明细单号
        detailEntity.setCode(bOutPlanDetailAutoCodeService.autoCode().getCode());

        // 审核人, 审核时间, 系统默认
        Integer id = staffService.selectIdByStaffCode(SystemConstants.AUDIT_STAFF_CODE);
        detailEntity.setAuditor_id(id);
        detailEntity.setAudit_dt(LocalDateTime.now());

        bOutPlanDetailMapper.insert(detailEntity);

        // 待办事项
        todoService.insertTodo(detailEntity.getId(), SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL, SystemConstants.PERMS.B_OUT_PLAN_DETAIL_OPERATE);

        return detailEntity;
    }

    /**
     * check逻辑
     *
     * @return
     */
    public void checkLogic(BScheduleEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.SUBMIT_CHECK_TYPE:
                // 是否制单或驳回状态
                if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_SCHEDULE_STATUS_TWO) && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_SCHEDULE_STATUS_FOUR)) {
                    throw new BusinessException(entity.getCode() + ":无法提交，该单据不是制单或驳回状态");
                }
                break;
            case CheckResultAo.AUDIT_CHECK_TYPE:
                // 是否已提交状态
                if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_SCHEDULE_STATUS_THREE)) {
                    throw new BusinessException(entity.getCode() + ":无法审核，该单据不是已提交状态");
                }
                break;
            case CheckResultAo.REJECT_CHECK_TYPE:
                // 是否已提交状态
                if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_SCHEDULE_STATUS_THREE)) {
                    throw new BusinessException(entity.getCode() + ":无法驳回，该单据不是已提交状态");
                }
                break;
            case CheckResultAo.FINISH_CHECK_TYPE:
                // 是否待调度状态
                if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_SCHEDULE_STATUS_ZERO)) {
                    throw new BusinessException(entity.getCode() + ":完成操作失败，该单据不是待调度状态");
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 是否制单或驳回状态
                if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_SCHEDULE_STATUS_TWO) && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_SCHEDULE_STATUS_FOUR)) {
                    throw new BusinessException(entity.getCode() + ":无法更新，该单据不是制单或驳回状态");
                }
                break;
            case CheckResultAo.CANCEL_CHECK_TYPE:
                // 作废, 必须是待调度状态
                if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_SCHEDULE_STATUS_ZERO)) {
                    throw new BusinessException(entity.getCode() + ":无法作废，该单据不是待调度状态！");
                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:
                // 删除, 制单、已提交、已驳回状态的物流订单可以删除,
                if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_SCHEDULE_STATUS_TWO)
                        && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_SCHEDULE_STATUS_THREE)
                        && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_SCHEDULE_STATUS_FOUR)) {
                    throw new BusinessException(entity.getCode() + ":无法删除");
                }
                // 删除, 无法重复删除
                if (Objects.equals(entity.getIs_delete(), DictConstant.DICT_B_IS_DELETE_TRUE)) {
                    throw new BusinessException(entity.getCode() + ": 无法重复删除");
                }
                break;
            case CheckResultAo.ENABLE_CHECK_TYPE:
                // 重新启用, 必须是待调度状态
                if (!Objects.equals(entity.getStatus(), DictConstant.DICT_B_SCHEDULE_STATUS_ONE)) {
                    throw new BusinessException(entity.getCode() + ":无法重新启用，该单据不是已完成状态！");
                }

                SConfigEntity config = isConfigService.selectByKey(SystemConstants.KEY_LOGISTICS_MAX_OUT);
                if (config != null && "1".equals(config.getValue()) && new BigDecimal(config.getExtra1()).compareTo(entity.getOut_operated_qty().divide(entity.getOut_schedule_qty(), 4, BigDecimal.ROUND_HALF_UP)) <= 0) {
                    throw new BusinessException(entity.getCode() + "超过最大出库比例，无法重新启用！");
                }
                break;
            default:
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BScheduleVo> insert3(BScheduleVo vo) {
        BScheduleEntity entity = new BScheduleEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        entity.setType("3");
        entity.setIn_schedule_qty(vo.getActual_count());
        entity.setIn_balance_qty(vo.getActual_count());
        entity.setIn_operated_qty(BigDecimal.ZERO);
        entity.setOut_schedule_qty(vo.getActual_count());
        entity.setOut_balance_qty(vo.getActual_count());
        entity.setOut_operated_qty(BigDecimal.ZERO);
        entity.setCode(autoCode.autoCode().getCode());

        // 查询发货库位
        MBinEntity binEntity = binMapper.selecBinByWarehouseId(vo.getOut_warehouse_id());
        entity.setOut_bin_id(binEntity.getId());
        vo.setOut_bin_id(binEntity.getId());

        entity.setOut_location_id(binEntity.getLocation_id());
        vo.setOut_location_id(binEntity.getLocation_id());

        // 查询收货库位
        binEntity = binMapper.selecBinByWarehouseId(vo.getIn_warehouse_id());
        entity.setIn_location_id(binEntity.getLocation_id());
        vo.setIn_location_id(binEntity.getLocation_id());

        entity.setIn_bin_id(binEntity.getId());
        vo.setIn_bin_id(binEntity.getId());

        entity.setStatus(DictConstant.DICT_B_SCHEDULE_STATUS_TWO);

        BOutPlanDetailVo outPlanDetailVo = null;
        if (Objects.equals(entity.getOut_type(), DictConstant.DICT_B_MONITOR_TYPE_OUT_OUT)) {
            if (!Objects.equals(vo.getOut_rule(), DictConstant.DICT_B_LOGISTICS_OUT_RULE_0)) {
                outPlanDetailVo = ibOutPlanDetailService.selectById(vo.getOut_plan_detail_id());
                entity.setOut_plan_detail_id(outPlanDetailVo.getId());
                entity.setOut_plan_detail_code(outPlanDetailVo.getCode());
            }
        }

        // 手动选择入库计划, 绑定
        if (Objects.equals(entity.getIn_type(), DictConstant.DICT_B_MONITOR_TYPE_IN_IN)) {
            if (!Objects.equals(vo.getIn_rule(), DictConstant.DICT_B_LOGISTICS_IN_RULE_0)) {
//                BInPlanDetailVo bInPlanDetailVo = ibInPlanDetailService.selectById(vo.getIn_plan_detail_id());
//                entity.setIn_plan_detail_id(bInPlanDetailVo.getId());
//                entity.setIn_plan_detail_code(bInPlanDetailVo.getCode());
            }
        }

        entity.setId(null);

        int rtn = mapper.insert(entity);

        BScheduleInfoVo bScheduleInfoVo = new BScheduleInfoVo();
        bScheduleInfoVo.setSchedule_id(entity.getId());
        bScheduleInfoVo.setWaybill_contract_no(vo.getWaybill_contract_no());
        bScheduleInfoVo.setCustomer_code(vo.getCustomer_code());
        bScheduleInfoVo.setCustomer_name(vo.getCustomer_name());
        bScheduleInfoVo.setCustomer_id(vo.getCustomer_id());
        ibScheduleInfoService.insert(bScheduleInfoVo);
        // 更新后查询 更新数据
        BScheduleVo bScheduleVo = mapper.selectListById(entity.getId());
        return InsertResultUtil.OK(bScheduleVo);
    }

    /**
     * 更新物流调度
     *
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BScheduleVo> update3(BScheduleVo vo) {
        Integer id = vo.getId();
        Assert.notNull(id, "ID 不能为空");

        // 检查状态是否可修改
        BScheduleEntity entity = new BScheduleEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        checkLogic(entity, CheckResultAo.UPDATE_CHECK_TYPE);
        entity.setIn_schedule_qty(vo.getActual_count());
        entity.setIn_balance_qty(vo.getActual_count());
        entity.setIn_operated_qty(BigDecimal.ZERO);
        entity.setOut_schedule_qty(vo.getActual_count());
        entity.setOut_balance_qty(vo.getActual_count());
        entity.setOut_operated_qty(BigDecimal.ZERO);

        // 设置发货库位
        MBinEntity binEntity = binMapper.selecBinByWarehouseId(vo.getOut_warehouse_id());
        entity.setOut_bin_id(binEntity.getId());
        vo.setOut_bin_id(binEntity.getId());

        // 设置发货库区
        entity.setOut_location_id(binEntity.getLocation_id());
        vo.setOut_location_id(binEntity.getLocation_id());

        // 设置收货库位
        binEntity = binMapper.selecBinByWarehouseId(vo.getIn_warehouse_id());
        entity.setIn_location_id(binEntity.getLocation_id());
        vo.setIn_location_id(binEntity.getLocation_id());

        // 设置收货库区
        entity.setIn_bin_id(binEntity.getId());
        vo.setIn_bin_id(binEntity.getId());

        BOutPlanDetailVo outPlanDetailVo = null;

        if (Objects.equals(entity.getOut_type(), DictConstant.DICT_B_MONITOR_TYPE_OUT_OUT)) {
            if (!Objects.equals(vo.getOut_rule(), DictConstant.DICT_B_LOGISTICS_OUT_RULE_0)) {
                // 手动选择出库计划
                outPlanDetailVo = ibOutPlanDetailService.selectById(vo.getOut_plan_detail_id());
                entity.setOut_plan_detail_id(outPlanDetailVo.getId());
                entity.setOut_plan_detail_code(outPlanDetailVo.getCode());
            }
        }
        // 手动选择 入库计划
        if (Objects.equals(entity.getIn_type(), DictConstant.DICT_B_MONITOR_TYPE_IN_IN)) {
            if (!Objects.equals(vo.getIn_rule(), DictConstant.DICT_B_LOGISTICS_IN_RULE_0)) {
//                BInPlanDetailVo bInPlanDetailVo = ibInPlanDetailService.selectById(vo.getIn_plan_detail_id());
//                entity.setIn_plan_detail_id(bInPlanDetailVo.getId());
//                entity.setIn_plan_detail_code(bInPlanDetailVo.getCode());
            }
        }

        // 更新状态
        updateScheduleStatus(entity);
        mapper.updateById(entity);
        updateScheduleInfo(vo);
        // 更新后查询 更新数据
        BScheduleVo bScheduleVo = mapper.selectListById(entity.getId());
        return UpdateResultUtil.OK(bScheduleVo);
    }

    /**
     * 物流直采单数据新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BScheduleVo> insert4(BScheduleVo vo) {
        BScheduleEntity entity = new BScheduleEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        entity.setType(DictConstant.DICT_B_SCHEDULE_TYPE_4);
        entity.setIn_schedule_qty(vo.getActual_count());
        entity.setIn_balance_qty(vo.getActual_count());
        entity.setIn_operated_qty(BigDecimal.ZERO);
        entity.setOut_schedule_qty(BigDecimal.ZERO);
        entity.setOut_balance_qty(BigDecimal.ZERO);
        entity.setOut_operated_qty(BigDecimal.ZERO);
        entity.setCode(autoCode.autoCode().getCode());

        // 查询收货库位
        MBinEntity binEntity = binMapper.selecBinByWarehouseId(vo.getIn_warehouse_id());
        entity.setIn_location_id(binEntity.getLocation_id());
        vo.setIn_location_id(binEntity.getLocation_id());

        entity.setIn_bin_id(binEntity.getId());
        vo.setIn_bin_id(binEntity.getId());

        entity.setStatus(DictConstant.DICT_B_SCHEDULE_STATUS_TWO);

        // 手动选择入库计划, 绑定
        if (Objects.equals(entity.getIn_type(), DictConstant.DICT_B_MONITOR_TYPE_IN_IN)) {
            if (!Objects.equals(vo.getIn_rule(), DictConstant.DICT_B_LOGISTICS_IN_RULE_0)) {
//                BInPlanDetailVo bInPlanDetailVo = ibInPlanDetailService.selectById(vo.getIn_plan_detail_id());
//                entity.setIn_plan_detail_id(bInPlanDetailVo.getId());
//                entity.setIn_plan_detail_code(bInPlanDetailVo.getCode());
            }
        }

        entity.setId(null);

        int rtn = mapper.insert(entity);

        BScheduleInfoVo bScheduleInfoVo = new BScheduleInfoVo();
        bScheduleInfoVo.setSchedule_id(entity.getId());
        bScheduleInfoVo.setWaybill_contract_no(vo.getWaybill_contract_no());
        bScheduleInfoVo.setCustomer_code(vo.getCustomer_code());
        bScheduleInfoVo.setCustomer_name(vo.getCustomer_name());
        bScheduleInfoVo.setCustomer_id(vo.getCustomer_id());
        ibScheduleInfoService.insert(bScheduleInfoVo);
        // 更新后查询 更新数据
        BScheduleVo bScheduleVo = mapper.selectListById(entity.getId());
        return InsertResultUtil.OK(bScheduleVo);
    }


    /**
     * 更新物流直采
     *
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BScheduleVo> update4(BScheduleVo vo) {
        Integer id = vo.getId();
        Assert.notNull(id, "ID 不能为空");

        // 检查状态是否可修改
        BScheduleEntity entity = new BScheduleEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        checkLogic(entity, CheckResultAo.UPDATE_CHECK_TYPE);
        entity.setIn_schedule_qty(vo.getActual_count());
        entity.setIn_balance_qty(vo.getActual_count());
        entity.setIn_operated_qty(BigDecimal.ZERO);
        entity.setOut_schedule_qty(BigDecimal.ZERO);
        entity.setOut_balance_qty(BigDecimal.ZERO);
        entity.setOut_operated_qty(BigDecimal.ZERO);

        // 设置收货库位
        MBinEntity binEntity = binMapper.selecBinByWarehouseId(vo.getIn_warehouse_id());
        entity.setIn_location_id(binEntity.getLocation_id());
        vo.setIn_location_id(binEntity.getLocation_id());

        // 设置收货库区
        entity.setIn_bin_id(binEntity.getId());
        vo.setIn_bin_id(binEntity.getId());

        // 手动选择 入库计划
        if (Objects.equals(entity.getIn_type(), DictConstant.DICT_B_MONITOR_TYPE_IN_IN)) {
            if (!Objects.equals(vo.getIn_rule(), DictConstant.DICT_B_LOGISTICS_IN_RULE_0)) {
//                BInPlanDetailVo bInPlanDetailVo = ibInPlanDetailService.selectById(vo.getIn_plan_detail_id());
//                entity.setIn_plan_detail_id(bInPlanDetailVo.getId());
//                entity.setIn_plan_detail_code(bInPlanDetailVo.getCode());
            }
        }

        // 更新状态
        updateScheduleStatus(entity);
        mapper.updateById(entity);
        updateScheduleInfo(vo);
        // 更新后查询 更新数据
        BScheduleVo bScheduleVo = mapper.selectListById(entity.getId());
        return UpdateResultUtil.OK(bScheduleVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BScheduleVo> insert5(BScheduleVo vo) {
        BScheduleEntity entity = new BScheduleEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        entity.setType(DictConstant.DICT_B_SCHEDULE_TYPE_5);
        entity.setIn_schedule_qty(BigDecimal.ZERO);
        entity.setIn_balance_qty(BigDecimal.ZERO);
        entity.setIn_operated_qty(BigDecimal.ZERO);
        entity.setOut_schedule_qty(vo.getActual_count());
        entity.setOut_balance_qty(vo.getActual_count());
        entity.setOut_operated_qty(BigDecimal.ZERO);
        entity.setCode(autoCode.autoCode().getCode());

        // 查询发货库位
        MBinEntity binEntity = binMapper.selecBinByWarehouseId(vo.getOut_warehouse_id());
        entity.setOut_bin_id(binEntity.getId());
        vo.setOut_bin_id(binEntity.getId());

        entity.setOut_location_id(binEntity.getLocation_id());
        vo.setOut_location_id(binEntity.getLocation_id());

        entity.setStatus(DictConstant.DICT_B_SCHEDULE_STATUS_TWO);

        BOutPlanDetailVo outPlanDetailVo = null;
        if (Objects.equals(entity.getOut_type(), DictConstant.DICT_B_MONITOR_TYPE_OUT_OUT)) {
            if (!Objects.equals(vo.getOut_rule(), DictConstant.DICT_B_LOGISTICS_OUT_RULE_0)) {
                outPlanDetailVo = ibOutPlanDetailService.selectById(vo.getOut_plan_detail_id());
                entity.setOut_plan_detail_id(outPlanDetailVo.getId());
                entity.setOut_plan_detail_code(outPlanDetailVo.getCode());
            }
        }

        entity.setId(null);

        int rtn = mapper.insert(entity);

        BScheduleInfoVo bScheduleInfoVo = new BScheduleInfoVo();
        bScheduleInfoVo.setSchedule_id(entity.getId());
        bScheduleInfoVo.setWaybill_contract_no(vo.getWaybill_contract_no());
        bScheduleInfoVo.setCustomer_code(vo.getCustomer_code());
        bScheduleInfoVo.setCustomer_name(vo.getCustomer_name());
        bScheduleInfoVo.setCustomer_id(vo.getCustomer_id());
        ibScheduleInfoService.insert(bScheduleInfoVo);
        // 更新后查询 更新数据
        BScheduleVo bScheduleVo = mapper.selectListById(entity.getId());
        return InsertResultUtil.OK(bScheduleVo);
    }

    /**
     * 物流直销单数据修改
     *
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BScheduleVo> update5(BScheduleVo vo) {
        Integer id = vo.getId();
        Assert.notNull(id, "ID 不能为空");

        // 检查状态是否可修改
        BScheduleEntity entity = new BScheduleEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        checkLogic(entity, CheckResultAo.UPDATE_CHECK_TYPE);
        entity.setIn_schedule_qty(BigDecimal.ZERO);
        entity.setIn_balance_qty(BigDecimal.ZERO);
        entity.setIn_operated_qty(BigDecimal.ZERO);
        entity.setOut_schedule_qty(vo.getActual_count());
        entity.setOut_balance_qty(vo.getActual_count());
        entity.setOut_operated_qty(BigDecimal.ZERO);

        // 设置发货库位
        MBinEntity binEntity = binMapper.selecBinByWarehouseId(vo.getOut_warehouse_id());
        entity.setOut_bin_id(binEntity.getId());
        vo.setOut_bin_id(binEntity.getId());

        // 设置发货库区
        entity.setOut_location_id(binEntity.getLocation_id());
        vo.setOut_location_id(binEntity.getLocation_id());

        BOutPlanDetailVo outPlanDetailVo = null;
        if (Objects.equals(entity.getOut_type(), DictConstant.DICT_B_MONITOR_TYPE_OUT_OUT)) {
            if (!Objects.equals(vo.getOut_rule(), DictConstant.DICT_B_LOGISTICS_OUT_RULE_0)) {
                // 手动选择出库计划
                outPlanDetailVo = ibOutPlanDetailService.selectById(vo.getOut_plan_detail_id());
                entity.setOut_plan_detail_id(outPlanDetailVo.getId());
                entity.setOut_plan_detail_code(outPlanDetailVo.getCode());
            }
        }

        // 更新状态
        updateScheduleStatus(entity);
        mapper.updateById(entity);
        updateScheduleInfo(vo);
        // 更新后查询 更新数据
        BScheduleVo bScheduleVo = mapper.selectListById(entity.getId());
        return UpdateResultUtil.OK(bScheduleVo);
    }
}
