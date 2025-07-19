package com.xinyirun.scm.core.system.serviceimpl.business.out;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutCheckVo;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutExtraEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutPlanEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.out.receive.BReceiveEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.out.receive.BReceiveExtraEntity;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecEntity;
import com.xinyirun.scm.bean.entity.master.org.MStaffOrgEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderVo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.*;
import com.xinyirun.scm.bean.system.vo.business.wms.out.receive.BReceiveVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.excel.out.BOutPlanExportVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.app.AppBusinessException;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.wms.out.BOutExtraMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.out.BOutMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.out.BOutPlanDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.out.BOutPlanMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.out.receive.BReceiveExtraMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.out.receive.BReceiveMapper;
import com.xinyirun.scm.core.system.mapper.master.goods.MGoodsSpecMapper;
import com.xinyirun.scm.core.system.mapper.master.org.MStaffOrgMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.business.order.IBOrderService;
import com.xinyirun.scm.core.system.service.business.wms.out.IBOutPlanDetailService;
import com.xinyirun.scm.core.system.service.business.wms.out.IBOutPlanService;
import com.xinyirun.scm.core.system.service.business.todo.IBAlreadyDoService;
import com.xinyirun.scm.core.system.service.business.todo.IBTodoService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.master.inventory.IMInventoryService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.business.todo.TodoService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BOutAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BReceiveAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 出库计划 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class BOutPlanServiceImpl extends BaseServiceImpl<BOutPlanMapper, BOutPlanEntity> implements IBOutPlanService {

    @Autowired
    private BOutPlanMapper mapper;

    @Autowired
    private BOutMapper outMapper;

    @Autowired
    private BOutExtraMapper outExtraMapper;

    @Autowired
    private BOutPlanDetailMapper outPlanDetailMapper;

    @Autowired
    private BOutAutoCodeServiceImpl outCode;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private MStaffOrgMapper staffOrgMapper;

    @Autowired
    private IBTodoService ibTodoService;

    @Autowired
    private IBAlreadyDoService ibAlreadyDoService;

    @Autowired
    private MGoodsSpecMapper goodsSpecMapper;

    @Autowired
    private IMInventoryService imInventoryService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private MCancelService mCancelService;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private IBOrderService ibOrderService;

    @Autowired
    private IBOutPlanDetailService ibOutPlanDetailService;

    @Autowired
    private BReceiveAutoCodeServiceImpl receiveCode;

    @Autowired
    private BReceiveMapper bReceiveMapper;

    @Autowired
    private BReceiveExtraMapper bReceiveExtraMapper;

    /**
     * 查询分页列表
     * @param searchCondition 查询条件
     * @return IPage<BOutPlanListVo>
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public IPage<BOutPlanListVo> selectPage(BOutPlanListVo searchCondition) {
//        // 查询待办已办数据
//        this.toDoAlreadyDo(searchCondition);
        // 分页条件
        Page<BOutPlanDetailEntity> pageCondition =  new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        searchCondition.setStaff_id(SecurityUtil.getStaff_id());

        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public Integer selectTodoCount(BOutPlanListVo searchCondition) {
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.selectTodoCount(searchCondition);
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public BOutPlanSumVo selectSumData(BOutPlanListVo searchCondition) {
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());

        return mapper.selectSumData(searchCondition);
    }

    @Override
    public List<BOutPlanListVo> selectList(BOutPlanListVo searchCondition) {
        // 查询入库计划list
        return mapper.selectList(searchCondition);
    }

    /**
     * 查询出库计划数据
     * @param vo 入库计划参数
     * @return BOutPlanSaveVo
     */
    @Override
    public BOutPlanSaveVo get(BOutPlanSaveVo vo) {
        // 查询出库计划
        BOutPlanSaveVo outPlanSaveVo = mapper.get(vo.getId());
        // 查询出库物料明细list
        outPlanSaveVo.setDetailList(outPlanDetailMapper.selectOutGoodsList(outPlanSaveVo));

        return outPlanSaveVo;
    }

    /**
     * 查询出库操作页面
     * @param vo 入库计划查询条件
     * @return BOutPlanDetailVo
     */
    @Override
    public BOutPlanDetailVo getPlanDetail(BOutPlanDetailVo vo) {
        BOutPlanDetailVo result = mapper.getPlanDetail(vo);
        SConfigEntity pc = isConfigService.selectByKey(SystemConstants.OVER_RELEASE);
        SConfigEntity app = isConfigService.selectByKey(SystemConstants.APP_OVER_RELEASE);
        result.setOver_inventory_policy("1".equals(pc.getValue()) || "1".equals(app.getValue()));
        return result;
    }

    /**
     * id查询出库计划,返回更新对象
     * @param id 主键
     * @return BOutPlanVo
     */
    @Override
    public BOutPlanVo selectById(Integer id) {
        return mapper.selectId(id);
    }

    /**
     * id查询出库计划,返回出库操作对象
     * @param id 主键id
     * @return BOutPlanOperateVo
     */
    @Override
    public BOutPlanOperateVo selectByOperateId(int id) {
        return mapper.selectByOperateId(id);
    }

    /**
     * id查询出库计划,返回更新对象
     * @param id 主键
     * @return List<BOutPlanListVo>
     */
    @Override
    public List<BOutPlanListVo> selectBySaveId(int id) {
        return mapper.selectBySaveId(id);
    }

    /**
     * 提交
     * @param searchCondition 查询条件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> submit(List<BOutPlanListVo> searchCondition) {
//        int updCount;
        Boolean updateFlag = false;

//        List<ApiOutPlanResultBo> apiOutPlanResultBoList = new ArrayList<>();
//        List<BOutPlanDetailEntity> list = mapper.selectIdsIn(searchCondition);
        List<BOutPlanDetailEntity> list = mapper.selectByPlanIds(searchCondition);
        for(BOutPlanDetailEntity entity : list) {
            // check
            checkLogic(entity,CheckResultAo.SUBMIT_CHECK_TYPE);
            entity.setPre_status(entity.getStatus());
            entity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_SUBMITTED);
            entity.setAudit_dt(null);
            entity.setAuditor_id(null);
            entity.setE_opinion(null);
//            updCount = outPlanDetailMapper.updateById(entity);
            updateFlag = ibOutPlanDetailService.updateById(entity);
            log.debug("更新后updateFlag: "+updateFlag);

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL, SystemConstants.PERMS.B_OUT_PLAN_DETAIL_SUBMIT);

            // 生成待办
            todoService.insertTodo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL, SystemConstants.PERMS.B_OUT_PLAN_DETAIL_AUDIT);

            if(!updateFlag){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }
        return UpdateResultUtil.OK(true);
    }

    /**
     * 审核
     * @param searchCondition 查询条件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> audit(List<BOutPlanListVo> searchCondition) {
//        int updCount;
        Boolean updateFlag = false;
//        List<ApiOutPlanResultBo> apiOutPlanResultBoList = new ArrayList<>();
        List<BOutPlanDetailEntity> list = mapper.selectByPlanIds(searchCondition);
//        List<BOutPlanDetailEntity> list = mapper.selectIdsIn(searchCondition);
        for(BOutPlanDetailEntity entity : list) {
            // check
            checkLogic(entity,CheckResultAo.AUDIT_CHECK_TYPE);
            entity.setPre_status(entity.getStatus());
            entity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_PASSED);
            entity.setAuditor_id(SecurityUtil.getUpdateUser_id().intValue());
            entity.setAudit_dt(LocalDateTime.now());
            entity.setE_opinion(DictConstant.DICT_AUDIT_INFO_TYPE_FALSE);
//            updCount = outPlanDetailMapper.updateById(entity);
            updateFlag = ibOutPlanDetailService.updateById(entity);
            log.debug("更新后updateFlag: "+updateFlag);


            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL, SystemConstants.PERMS.B_OUT_PLAN_DETAIL_AUDIT);

            // 生成待办
            todoService.insertTodo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL, SystemConstants.PERMS.B_OUT_PLAN_DETAIL_OPERATE);

            if(!updateFlag){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }
        return UpdateResultUtil.OK(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> cancelAudit(List<BOutPlanListVo> searchCondition) {
        int updCount;
        String cancel_remark = searchCondition.get(0).getRemark();
        List<BOutPlanDetailEntity> list = mapper.selectByPlanIds(searchCondition);
        for(int i = 0; i < list.size(); i++) {
            BOutPlanDetailEntity entity = list.get(i);
            // check
            checkLogic(entity,CheckResultAo.AUDIT_CHECK_TYPE);
            // 查询关联入库单是否还有未作废数据
            List<BOutEntity> outList = outMapper.selectList(new QueryWrapper<BOutEntity>().eq("plan_detail_id",entity.getId()));
            for(BOutEntity outEntity:outList){
                // 出库单check
                checkLogic(outEntity,entity.getCode());
            }

            entity.setCancel_audit_dt(LocalDateTime.now());
            entity.setCancel_audit_id(SecurityUtil.getStaff_id().intValue());
            entity.setPre_status(entity.getStatus());
            entity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_CANCEL);
            entity.setCancel_audit_dt(LocalDateTime.now());
            entity.setCancel_audit_id(SecurityUtil.getUpdateUser_id().intValue());
            updCount = outPlanDetailMapper.updateById(entity);
            if(updCount == 0){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL, SystemConstants.PERMS.B_OUT_CANCEL);
        }
        return UpdateResultUtil.OK(true);
    }

    /**
     * 出库操作
     * @param vo 实体对象
     * @return InsertResultAo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> operate(BOutPlanOperateVo vo) {
        // 计划明细数据
        BOutPlanDetailEntity outPlanDetailEntity = outPlanDetailMapper.selectById(vo.getId());
        // 出库计划数据
//        BOutPlanEntity outPlanEntity = mapper.selectById(outPlanDetailEntity.getPlan_id());

        // check
        checkLogic(outPlanDetailEntity,CheckResultAo.OUT_OPERATE_CHECK_TYPE);

        // 上浮
        SConfigEntity orderOverRelease = isConfigService.selectByKey(SystemConstants.OVER_RELEASE);
        // 只要开启超发, 就要控制, 没有就是不允许超发, 上浮比例为 0
        if ("1".equals(orderOverRelease.getValue())) {

            BOrderVo orderVo = ibOrderService.selectOrder(outPlanDetailEntity.getOrder_type(), outPlanDetailEntity.getOrder_id());
            BigDecimal upper = BigDecimal.ZERO;
            // 上浮百分比, 1. 详情里有, 取详情的(wms新增的详情才有), 2. 详情没有, 取合同的(业务中台推过来的), 3. 都没有, 取0
            if (orderVo != null && null != orderVo.getOver_inventory_upper()) {
                upper = orderVo.getOver_inventory_upper();
            }
            if (null != outPlanDetailEntity.getOver_inventory_upper()) {
                upper = outPlanDetailEntity.getOver_inventory_upper();
            }

            // 1 按出库计划, 2按合同
            if ("1".equals(orderOverRelease.getExtra1())) {
                controlOverUpperByOutPlan(outPlanDetailEntity, orderOverRelease, upper, vo.getActual_count());
            } else if ("2".equals(orderOverRelease.getExtra1())) {
//                BOrderVo orderVo = ibOrderService.selectOrder(outPlanDetailEntity.getOrder_type(), outPlanDetailEntity.getOrder_id());

                // 如果按合同时没有选择合同， 则默认为 按 出库计划
                if (null == orderVo) {
                    controlOverUpperByOutPlan(outPlanDetailEntity, orderOverRelease, upper, vo.getActual_count());
                } else {
                    // 按合同
                    BigDecimal count = outPlanDetailMapper.selectWaitOperatedCount(outPlanDetailEntity.getOrder_id(), outPlanDetailEntity.getOrder_type());
                    // 最大出库量
                    BigDecimal floatUpWeight = orderVo.getContract_num().multiply(BigDecimal.ONE.add(upper)).stripTrailingZeros();

                    // 不可超发 验证出库数量是否大于待出库数量
                    if ((count.add(vo.getActual_count())).compareTo(floatUpWeight) > 0) {
                        throw new AppBusinessException(String.format(orderOverRelease.getExtra2(), floatUpWeight.subtract(count)));
                    }
                }
            }
        }

     /*   if ("1".equals(orderOverRelease.getValue()) && orderVo != null) {

            if (orderVo.getOver_inventory_upper() == null) {
                orderVo.setOver_inventory_upper(BigDecimal.ZERO);
            }

            // 已出库数量
            BigDecimal weight;
            BigDecimal floatUpWeight;
            if ("1".equals(orderOverRelease.getExtra1())) {
                // 按出库计划
                weight = outPlanDetailMapper.selectWaitOperatedCount1(outPlanDetailEntity.getId());

                // 最大出库量
                floatUpWeight = outPlanDetailEntity.getWeight().multiply(BigDecimal.ONE.add(orderVo.getOver_inventory_upper())).stripTrailingZeros();

                // 不可超发 验证出库数量是否大于待出库数量
                if ((weight.add(vo.getActual_weight())).compareTo(floatUpWeight) > 0) {
                    throw new AppBusinessException(String.format(orderOverRelease.getExtra3(), floatUpWeight.subtract(weight)));
                }
            } else if ("2".equals(orderOverRelease.getExtra1())) {
                // 按合同
                weight = outPlanDetailMapper.selectWaitOperatedCount(outPlanDetailEntity.getOrder_id(), outPlanDetailEntity.getOrder_type());
                // 最大出库量
                floatUpWeight = orderVo.getContract_num().multiply(BigDecimal.ONE.add(orderVo.getOver_inventory_upper())).stripTrailingZeros();

                // 不可超发 验证出库数量是否大于待出库数量
                if ((weight.add(vo.getActual_weight())).compareTo(floatUpWeight) > 0) {
                    throw new AppBusinessException(String.format(orderOverRelease.getExtra2(), floatUpWeight.subtract(weight)));
                }

            }
        } else if ("1".equals(orderOverRelease.getValue()) && orderVo == null) {
            BigDecimal weight;
            BigDecimal floatUpWeight;
            // 上浮百分比
            BigDecimal over_inventory_upper = outPlanDetailEntity.getOver_inventory_upper() == null ? BigDecimal.ZERO
                    : outPlanDetailEntity.getOver_inventory_upper();

            if ("1".equals(orderOverRelease.getExtra1())) {
                // 按出库计划
                weight = outPlanDetailMapper.selectWaitOperatedCount1(outPlanDetailEntity.getId());

                // 最大出库量
                floatUpWeight = outPlanDetailEntity.getWeight().multiply(BigDecimal.ONE.add(over_inventory_upper)).stripTrailingZeros();

                // 不可超发 验证出库数量是否大于待出库数量
                if ((weight.add(vo.getActual_weight())).compareTo(floatUpWeight) > 0) {
                    throw new AppBusinessException(String.format(orderOverRelease.getExtra3(), floatUpWeight.subtract(weight)));
                }
            } else if ("2".equals(orderOverRelease.getExtra1())) {
                // 此种情况没有合同
            }
        }*/
//        // 是否可超发
//        SConfigEntity config = isConfigService.selectByKey(SystemConstants.OVER_RELEASE);
//        if (Objects.equals(config.getValue(), "1")) {
//            // 不可超发 验证出库数量是否大于待出库数量
//            // 已出库数量
//            BigDecimal weight = outPlanDetailMapper.selectWaitOperatedCount1(outPlanDetailEntity.getId());
//            // 最大出库量
//            BigDecimal floatUpWeight = outPlanDetailEntity.getWeight();
//
//            if ((weight.add(vo.getActual_weight())).compareTo(floatUpWeight) > 0) {
//                throw new BusinessException(outPlanEntity.getCode()+":超过最大出库量，最大出库量为："+floatUpWeight);
//            }
//        }
        // 是否锁库存
        if (Objects.equals(Boolean.FALSE, outPlanDetailEntity.getLock_inventory())) {
            // 可用库存>所有的sum(出库计划（库存开关为开启），待出库数量) 数量
            // 查询库存数据
            MInventoryVo mInventoryVo = new MInventoryVo();
            mInventoryVo.setOwner_id(vo.getOwner_id());
            mInventoryVo.setWarehouse_id(vo.getWarehouse_id());
            mInventoryVo.setSku_id(vo.getSku_id());
            MInventoryVo inventoryInfo = imInventoryService.getInventoryInfo(mInventoryVo);
            BigDecimal weight = outPlanDetailMapper.selectWaitOperateCount(outPlanDetailEntity.getId());
            if (inventoryInfo != null && inventoryInfo.getQty_avaible().compareTo(weight) < 0) {
                throw new BusinessException(outPlanDetailEntity.getCode()+":库存不足!");
            }
        }

        BOutEntity out = (BOutEntity)BeanUtilsSupport.copyProperties(vo,BOutEntity.class);

        MGoodsSpecEntity specEntity = goodsSpecMapper.selectById(vo.getSku_id());
        out.setSku_code(specEntity.getCode());
        out.setRemark(vo.getDetail_remark());

        // 自动生成出库单编号
        out.setCode(outCode.autoCode().getCode());
        out.setStatus(DictConstant.DICT_B_OUT_STATUS_SAVED);
        // 出库计划id
        out.setPlan_id(vo.getPlan_id());
        // 出库计划明细id
        out.setPlan_detail_id(vo.getId());
        // 计划数量
        out.setPlan_count(vo.getCount());
        // 计划重量
        out.setPlan_weight(vo.getWeight());
        // 转换后的单位id
        out.setTgt_unit_id(vo.getUnitData().getTgt_unit_id());
        // 转换关系
        out.setCalc(vo.getUnitData().getCalc());
        out.setPrice(vo.getPrice());
        out.setAmount(vo.getAmount());
        out.setId(null);
        // 下推新增出库单
        int result = outMapper.insert(out);

        // 新增出库单从表数据
        BOutExtraEntity extra = (BOutExtraEntity)BeanUtilsSupport.copyProperties(vo,BOutExtraEntity.class);
        extra.setId(null);
        // 新增附件
        insertFiles(out,vo,extra);
        extra.setOut_id(out.getId());
        // 实收车数为Null的话同步中台接口会报错
        if(extra.getCar_count() == null) {
            extra.setCar_count(SystemConstants.CAR_COUNT);
        }
        outExtraMapper.insert(extra);


        // 生成待办
        todoService.insertTodo(out.getId(), SystemConstants.SERIAL_TYPE.B_OUT, SystemConstants.PERMS.B_OUT_SUBMIT);

        return InsertResultUtil.OK(result);
    }

    public void insertFile(BOutEntity out,SFileEntity fileEntity) {
        fileEntity.setSerial_id(out.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_OUT);
        // 主表新增
        fileMapper.insert(fileEntity);
    }

    /**
     * 超发 ，按出库计划控制
     * @param entity 详情
     * @param configEntity 配置
     * @param upper 上浮比例
     * @param actual_weight 待发货数量
     */
    private void controlOverUpperByOutPlan(BOutPlanDetailEntity entity, SConfigEntity configEntity, BigDecimal upper, BigDecimal actual_weight) {
        // 按出库计划
        BigDecimal count = outPlanDetailMapper.selectWaitOperatedCount1(entity.getId());

        // 最大出库量
        BigDecimal floatUpWeight = entity.getCount().multiply(BigDecimal.ONE.add(upper)).stripTrailingZeros();

        // 不可超发 验证出库数量是否大于待出库数量
        if ((count.add(actual_weight)).compareTo(floatUpWeight) > 0) {
            throw new AppBusinessException(String.format(configEntity.getExtra3(), floatUpWeight.subtract(count)));
        }
    }

    /**
     * 新增出库单附件逻辑
     */
    public void insertFiles(BOutEntity out,BOutPlanOperateVo vo,BOutExtraEntity extra) {
        // 磅单附件新增
        if(vo.getPound_files() != null && vo.getPound_files().size() > 0) {
            // 附件主表
            SFileEntity fileEntity = new SFileEntity();
            insertFile(out,fileEntity);
            // 详情表新增
            for(SFileInfoVo fileInfoVo:vo.getPound_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                fileInfoEntity.setF_id(fileEntity.getId());
                insertFileInfo(fileInfoEntity,fileInfoVo);
            }
            // 磅单附件id
            extra.setPound_file(fileEntity.getId());
        }

        // 出库明细附件新增
        if(vo.getOut_photo_files() != null && vo.getOut_photo_files().size() > 0) {
            // 附件主表
            SFileEntity fileEntity = new SFileEntity();
            insertFile(out,fileEntity);
            // 详情表新增
            for(SFileInfoVo fileInfoVo:vo.getOut_photo_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                fileInfoEntity.setF_id(fileEntity.getId());
                insertFileInfo(fileInfoEntity,fileInfoVo);
            }
            // 出库明细附件id
            extra.setOut_photo_file(fileEntity.getId());
        }
    }

    /**
     * 新增附件明细数据
     */
    public void insertFileInfo(SFileInfoEntity fileInfoEntity,SFileInfoVo fileInfoVo) {
        fileInfoEntity.setUrl(fileInfoVo.getUrl());
        fileInfoEntity.setFile_name(fileInfoVo.getFileName());
        fileInfoEntity.setFile_size(fileInfoVo.getFile_size());
        fileInfoEntity.setInternal_url(fileInfoVo.getInternal_url());
        fileInfoEntity.setTimestamp(fileInfoVo.getTimestamp());
        fileInfoMapper.insert(fileInfoEntity);
    }

    /**
     * 作废, 状态改为作废审核
     * @param searchCondition 查询条件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> cancel(List<BOutPlanListVo> searchCondition) {
        int updCount;
//        List<BOutPlanDetailEntity> list = mapper.selectIdsIn(searchCondition);
        // 目前只有能选择一条作废单, 共享作废理由
        String cancel_remark = searchCondition.get(0).getRemark();
        List<BOutPlanDetailEntity> list = mapper.selectByPlanIds(searchCondition);
        for(int i = 0; i < list.size(); i++) {
            BOutPlanDetailEntity entity = list.get(i);
            // check
            checkLogic(entity,CheckResultAo.CANCEL_CHECK_TYPE);
            // 查询关联入库单是否还有未作废数据
            List<BOutEntity> outList = outMapper.selectList(new QueryWrapper<BOutEntity>().eq("plan_detail_id",entity.getId()));
            for(BOutEntity outEntity:outList){
                // 出库单check
                checkLogic(outEntity,entity.getCode());
            }

            entity.setPre_status(entity.getStatus());

            // 制单和驳回状态直接作废
            if(DictConstant.DICT_B_OUT_PLAN_STATUS_SAVED.equals(entity.getStatus()) || DictConstant.DICT_B_OUT_PLAN_STATUS_RETURN.equals(entity.getStatus())) {
                entity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_CANCEL);
            } else {
                entity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_CANCEL_BEING_AUDITED);
            }

            updCount = outPlanDetailMapper.updateById(entity);
            if(updCount == 0){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            // 作废记录
            MCancelVo mCancelVo = new MCancelVo();
            mCancelVo.setSerial_id(entity.getId());
            mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL);
            mCancelVo.setRemark(cancel_remark);
            mCancelService.insert(mCancelVo);

            // 生成已办 制单和驳回状态不生成
            if(!DictConstant.DICT_B_IN_PLAN_STATUS_ZERO.equals(entity.getStatus()) && !DictConstant.DICT_B_IN_PLAN_STATUS_THREE.equals(entity.getStatus())){
                todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL, SystemConstants.PERMS.B_OUT_CANCEL);
            }
        }
        return UpdateResultUtil.OK(true);
    }

    /**
     * 作废, 直接作废
     * @param searchCondition 查询条件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelDirect(List<BOutPlanListVo> searchCondition) {
        int updCount;
        // 目前只有能选择一条作废单, 共享作废理由
        String cancel_remark = searchCondition.get(0).getRemark();
        List<BOutPlanDetailEntity> list = mapper.selectByPlanIds(searchCondition);
        for(int i = 0; i < list.size(); i++) {
            BOutPlanDetailEntity entity = list.get(i);
            // check
            checkLogic(entity,CheckResultAo.CANCEL_CHECK_TYPE);
            // 查询关联入库单是否还有未作废数据
            List<BOutEntity> outList = outMapper.selectList(new QueryWrapper<BOutEntity>().eq("plan_detail_id",entity.getId()));
            for(BOutEntity outEntity:outList){
                // 出库单check
                checkLogic(outEntity,entity.getCode());
            }

            entity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_CANCEL);
            updCount = outPlanDetailMapper.updateById(entity);
            if(updCount == 0){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            // 作废记录
            MCancelVo mCancelVo = new MCancelVo();
            mCancelVo.setSerial_id(entity.getId());
            mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL);
            mCancelVo.setRemark(cancel_remark);
            mCancelService.insert(mCancelVo);

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL, SystemConstants.PERMS.B_OUT_CANCEL);
        }
    }

    /**
     * 驳回
     * @param searchCondition 查询条件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> reject(List<BOutPlanListVo> searchCondition) {
//        int updCount;
        Boolean updateFlag;
//        List<ApiOutPlanResultBo> apiOutPlanResultBoList = new ArrayList<>();
//        List<BOutPlanDetailEntity> list = mapper.selectIdsIn(searchCondition);
        List<BOutPlanDetailEntity> list = mapper.selectByPlanIds(searchCondition);
        for(BOutPlanDetailEntity entity : list) {
            // check
            checkLogic(entity,CheckResultAo.REJECT_CHECK_TYPE);
            entity.setPre_status(entity.getStatus());
            entity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_RETURN);
            entity.setE_opinion(DictConstant.DICT_AUDIT_INFO_TYPE_TRUE);
//            updCount = outPlanDetailMapper.updateById(entity);
            updateFlag = ibOutPlanDetailService.updateById(entity);
            log.debug("更新后updateFlag: "+updateFlag);
            if(!updateFlag){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL, SystemConstants.PERMS.B_OUT_REJECT);
        }
        return UpdateResultUtil.OK(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> cancelReject(List<BOutPlanListVo> searchCondition) {
        Boolean updateFlag = false;

        List<BOutPlanDetailEntity> list = mapper.selectByPlanIds(searchCondition);
        for(BOutPlanDetailEntity entity : list) {
            // check
            checkLogic(entity,CheckResultAo.REJECT_CHECK_TYPE);

            // 作废驳回
            entity.setStatus(entity.getPre_status());

            updateFlag = ibOutPlanDetailService.updateById(entity);

            // 删除对应作废理由
            MCancelVo mCancelVo = new MCancelVo();
            mCancelVo.setSerial_id(entity.getId());
            mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL);
            mCancelService.delete(mCancelVo);
            log.debug("更新后updateFlag: "+updateFlag);
            if(!updateFlag){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            // 生成待办
            todoService.insertTodo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL, SystemConstants.PERMS.B_OUT_PLAN_DETAIL_AUDIT);

        }
        return UpdateResultUtil.OK(true);
    }

    @Override
    public UpdateResultAo<Boolean> finish(List<BOutPlanListVo> searchCondition) {
//        int updCount;
        Boolean updateFlag = false;
//        List<ApiOutPlanResultBo> apiOutPlanResultBoList = new ArrayList<>();
//        List<BOutPlanDetailEntity> list = mapper.selectIdsIn(searchCondition);
        List<BOutPlanDetailEntity> list = mapper.selectByPlanIds(searchCondition);
        for(BOutPlanDetailEntity entity : list) {
            // check
            checkLogic(entity,CheckResultAo.FINISH_CHECK_TYPE);
            entity.setPre_status(entity.getStatus());
            entity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_FINISH);
//            updCount = outPlanDetailMapper.updateById(entity);
            updateFlag = ibOutPlanDetailService.updateById(entity);
            log.debug("更新后updateFlag: "+updateFlag);
            if(!updateFlag){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
//            BOutPlanEntity bOutPlanEntity = mapper.selectById(entity.getPlan_id());
//            // 返回数据给中台
//            apiOutService.getResultApi(bOutPlanEntity,apiOutPlanResultBoList);
        }
        return UpdateResultUtil.OK(true);
    }

    @Override
    public List<BOutPlanExportVo> selectExportList(List<BOutPlanListVo> searchCondition) {
        return mapper.selectExportList(searchCondition);
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public List<BOutPlanExportVo> selectExportAllList(BOutPlanListVo searchCondition) {
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectExportNum(searchCondition);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.selectAllList(searchCondition);
    }

    /**
     * 根据 plan_id 查询有几条明细
     *
     * @param searchCondition
     * @return
     */
    @Override
    public Integer getDetailCount(List<BOutPlanListVo> searchCondition) {
        for (BOutPlanListVo bInPlanListVo : searchCondition) {
            List<Integer> ids = outPlanDetailMapper.selectOutGoodsIdList(bInPlanListVo.getPlan_id());
            if (ids.size() > 1) {
                return ids.size();
            } else  {
                return 1;
            }
        }
        return 1;
    }

    @Override
    public ApiOutCheckVo selectOutCheckVo(Integer id) {
        ApiOutCheckVo apiOutCheckVo = mapper.selectOutCheckVo(id);
        return apiOutCheckVo;
    }

    @Override
    public List<ApiOutCheckVo> selectOutCheckVoByOutBill(List<BOutVo> beans) {
        return mapper.selectOutCheckVoByOutBill(beans);
    }

    /**
     * 查询出库单列表, 不查询总数量
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public List<BOutPlanListVo> selectPageListNotCount(BOutPlanListVo searchCondition) {
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
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
        return mapper.selectPageListNotCount(searchCondition, sort, sortType, defaultSort);
    }

    /**
     * 查询出库计划单 总条数
     *
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public BOutPlanListVo selectPageListCount(BOutPlanListVo searchCondition) {
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());

        BOutPlanListVo result = new BOutPlanListVo();
        Integer count = mapper.selectPageListCount(searchCondition);

        result.setTotal_count(count);
        PageCondition pageCondition =(PageCondition) BeanUtilsSupport.copyProperties(searchCondition.getPageCondition(), PageCondition.class);
        result.setPageCondition(pageCondition);
        return result;
    }

    /**
     * check逻辑
     *
     * @return CheckResultAo
     */
    public CheckResultAo checkLogic(BOutPlanDetailEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 是否制单或者驳回状态
                if( !Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_SAVED) && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_RETURN)) {
                    throw new BusinessException(entity.getCode()+":无法提交，该单据不是制单或驳回状态");
                }
                break;
            case CheckResultAo.AUDIT_CHECK_TYPE:
                // 是否已提交状态
                if(!Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_SUBMITTED) && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_CANCEL_BEING_AUDITED)) {
                    throw new BusinessException(entity.getCode()+":无法审核，该单据不是已提交状态");
                }
                break;
            case CheckResultAo.OUT_OPERATE_CHECK_TYPE:
                // 是否审核通过状态
                if(!Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_PASSED)) {
                    throw new BusinessException(entity.getCode()+":无法出库，该单据不是已审核状态");
                }
                break;
            case CheckResultAo.CANCEL_CHECK_TYPE:
                // 是否已经过期
                if(Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_EXPIRES)) {
                    throw new BusinessException(entity.getCode()+":已过期，无法作废");
                }
                // 是否已经作废
                if(Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_CANCEL) || Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_CANCEL_BEING_AUDITED)) {
                    throw new BusinessException(entity.getCode()+":无法重复作废");
                }
                // 是否已提交
                if (Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_SUBMITTED)) {
                    throw new BusinessException("出库计划：" + entity.getCode() + " 已提交，无法作废");
                }
                BOutPlanEntity bOutPlanEntity = mapper.selectById(entity.getPlan_id());
                // 如果单据是审核通过或已完成，且勾选了需要调度无法作废
                if((Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_PASSED) ||
                        Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_FINISH)) &&
                        Objects.equals(bOutPlanEntity.getSchedule_status(), DictConstant.DICT_B_OUT_PLAN_SCHEDULE_STATUS_TRUE)) {
                    throw new BusinessException(entity.getCode()+":该出库计划需要调度，无法作废");
                }

                break;
            case CheckResultAo.REJECT_CHECK_TYPE:
                // 是否已提交状态
                if(!Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_SUBMITTED) && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_OUT_PLAN_STATUS_CANCEL_BEING_AUDITED)) {
                    throw new BusinessException(entity.getCode()+":无法驳回，该单据不是已提交状态");
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 入库单check逻辑
     */
    public void checkLogic(BOutEntity outEntity,String code) {
        if(!Objects.equals(outEntity.getStatus(), DictConstant.DICT_B_OUT_STATUS_CANCEL)) {
            throw new BusinessException(code+":该出库明细下还有关联出库单未作废");
        }
    }

    /**
     * 待办已办数据查询
     */
    public void toDoAlreadyDo(BOutPlanListVo searchCondition) {
        // 待办
        if(SystemConstants.SERIAL_TYPE.TO_DO_STATUS.equals(searchCondition.getTodo_status())) {
            // 查询岗位员工关联表数据集合
            List<MStaffOrgEntity> list = staffOrgMapper.selectList(new QueryWrapper<MStaffOrgEntity>().eq
                    ("staff_id",SecurityUtil.getStaff_id()).eq
                    ("serial_type",DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE));
            if(list != null && list.size() > 0) {
                searchCondition.setSerial_ids(new ArrayList<>());
                for(MStaffOrgEntity staffOrgEntity:list) {
                    searchCondition.getSerial_ids().add(staffOrgEntity.getSerial_id());
                }
                // 根据岗位查询待办入库id集合
                List<Integer> ids = ibTodoService.selectTodoIdList(SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL,
                        searchCondition.getSerial_ids().toArray(new Long[searchCondition.getSerial_ids().size()]));
                // 设置待办id集合
                if (ids.size() == 0) {
                    ids.add(0);
                }
                searchCondition.setTodo_ids(ids.toArray(new Integer[ids.size()]));
            }
        }
        // 已办
        if(SystemConstants.SERIAL_TYPE.ALREADY_DO_STATUS.equals(searchCondition.getTodo_status())) {
            // 根据人员查询已办入库id集合
            List<Integer> ids = ibAlreadyDoService.selectAlreadyDoIdList(SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL, SecurityUtil.getStaff_id());
            // 设置待办id集合
            searchCondition.setAlready_do_ids(ids.toArray(new Integer[ids.size()]));
        }
    }


    /**
     * 收货操作
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> operateDelivery(BOutPlanOperateVo vo) {
        // 计划明细数据
        BOutPlanDetailEntity outPlanDetailEntity = outPlanDetailMapper.selectById(vo.getId());

        // check
        checkLogic(outPlanDetailEntity,CheckResultAo.OUT_OPERATE_CHECK_TYPE);

        // 上浮
        SConfigEntity orderOverRelease = isConfigService.selectByKey(SystemConstants.OVER_RELEASE);
        // 只要开启超发, 就要控制, 没有就是不允许超发, 上浮比例为 0
        if ("1".equals(orderOverRelease.getValue())) {

            BOrderVo orderVo = ibOrderService.selectOrder(outPlanDetailEntity.getOrder_type(), outPlanDetailEntity.getOrder_id());
            BigDecimal upper = BigDecimal.ZERO;
            // 上浮百分比, 1. 详情里有, 取详情的(wms新增的详情才有), 2. 详情没有, 取合同的(业务中台推过来的), 3. 都没有, 取0
            if (orderVo != null && null != orderVo.getOver_inventory_upper()) {
                upper = orderVo.getOver_inventory_upper();
            }
            if (null != outPlanDetailEntity.getOver_inventory_upper()) {
                upper = outPlanDetailEntity.getOver_inventory_upper();
            }

            // 1 按出库计划, 2按合同
            if ("1".equals(orderOverRelease.getExtra1())) {
                controlOverUpperByOutPlan(outPlanDetailEntity, orderOverRelease, upper, vo.getActual_count());
            } else if ("2".equals(orderOverRelease.getExtra1())) {
//                BOrderVo orderVo = ibOrderService.selectOrder(outPlanDetailEntity.getOrder_type(), outPlanDetailEntity.getOrder_id());

                // 如果按合同时没有选择合同， 则默认为 按 出库计划
                if (null == orderVo) {
                    controlOverUpperByOutPlan(outPlanDetailEntity, orderOverRelease, upper, vo.getActual_count());
                } else {
                    // 按合同
                    BigDecimal count = outPlanDetailMapper.selectWaitOperatedCount(outPlanDetailEntity.getOrder_id(), outPlanDetailEntity.getOrder_type());
                    // 最大出库量
                    BigDecimal floatUpWeight = orderVo.getContract_num().multiply(BigDecimal.ONE.add(upper)).stripTrailingZeros();

                    // 不可超发 验证出库数量是否大于待出库数量
                    if ((count.add(vo.getActual_count())).compareTo(floatUpWeight) > 0) {
                        throw new AppBusinessException(String.format(orderOverRelease.getExtra2(), floatUpWeight.subtract(count)));
                    }
                }
            }
        }

        BReceiveEntity bReceiveEntity = (BReceiveEntity)BeanUtilsSupport.copyProperties(vo,BReceiveEntity.class);

        MGoodsSpecEntity specEntity = goodsSpecMapper.selectById(vo.getSku_id());
        bReceiveEntity.setSku_code(specEntity.getCode());
        bReceiveEntity.setRemark(vo.getDetail_remark());

        // 自动生成出库单编号
        bReceiveEntity.setCode(receiveCode.autoCode().getCode());
        bReceiveEntity.setStatus(DictConstant.DICT_B_RECEIVE_STATUS_SAVED);
        // 出库计划id
        bReceiveEntity.setPlan_id(vo.getPlan_id());
        // 出库计划明细id
        bReceiveEntity.setPlan_detail_id(vo.getId());
        // 计划数量
        bReceiveEntity.setPlan_count(vo.getCount());
        // 计划重量
        bReceiveEntity.setPlan_weight(vo.getWeight());
        // 转换后的单位id
        bReceiveEntity.setTgt_unit_id(vo.getUnitData().getTgt_unit_id());
        // 转换关系
        bReceiveEntity.setCalc(vo.getUnitData().getCalc());
        bReceiveEntity.setPrice(vo.getPrice());
        bReceiveEntity.setAmount(vo.getAmount());
        bReceiveEntity.setId(null);
        // 下推新增出库单
        int result = bReceiveMapper.insert(bReceiveEntity);

        // 新增出库单从表数据
        BReceiveExtraEntity extra = (BReceiveExtraEntity)BeanUtilsSupport.copyProperties(vo,BReceiveExtraEntity.class);
        extra.setId(null);
        // 新增附件
        insertFiles(bReceiveEntity,vo,extra);
        extra.setReceive_id(bReceiveEntity.getId());
        // 实收车数为Null的话同步中台接口会报错
        if(extra.getCar_count() == null) {
            extra.setCar_count(SystemConstants.CAR_COUNT);
        }
        bReceiveExtraMapper.insert(extra);

        // 生成待办
        todoService.insertTodo(bReceiveEntity.getId(), SystemConstants.SERIAL_TYPE.B_RECEIVE, SystemConstants.PERMS.B_RECEIVE_SUBMIT);

        return InsertResultUtil.OK(result);
    }

    /**
     * 新增出库单附件逻辑
     */
    public void insertFiles(BReceiveEntity bReceiveEntity,BOutPlanOperateVo vo,BReceiveExtraEntity extra) {
        // 磅单附件新增
        if(vo.getPound_files() != null && vo.getPound_files().size() > 0) {
            // 附件主表
            SFileEntity fileEntity = new SFileEntity();
            insertFile(bReceiveEntity,fileEntity);
            // 详情表新增
            for(SFileInfoVo fileInfoVo:vo.getPound_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                fileInfoEntity.setF_id(fileEntity.getId());
                insertFileInfo(fileInfoEntity,fileInfoVo);
            }
            // 磅单附件id
            extra.setPound_file(fileEntity.getId());
        }

        // 出库明细附件新增
        if(vo.getOut_photo_files() != null && vo.getOut_photo_files().size() > 0) {
            // 附件主表
            SFileEntity fileEntity = new SFileEntity();
            insertFile(bReceiveEntity,fileEntity);
            // 详情表新增
            for(SFileInfoVo fileInfoVo:vo.getOut_photo_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                fileInfoEntity.setF_id(fileEntity.getId());
                insertFileInfo(fileInfoEntity,fileInfoVo);
            }
            // 出库明细附件id
            extra.setOut_photo_file(fileEntity.getId());
        }
    }

    public void insertFile(BReceiveEntity out,SFileEntity fileEntity) {
        fileEntity.setSerial_id(out.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_RECEIVE);
        // 主表新增
        fileMapper.insert(fileEntity);
    }

    @Override
    public List<ApiOutCheckVo> selectReceiveCheckVoByOutBill(List<BReceiveVo> beans) {
        return mapper.selectReceiveCheckVoByOutBill(beans);
    }

    /**
     * 查询出库计划数据
     * @param vo 入库计划参数
     * @return BOutPlanSaveVo
     */
    @Override
    public BOutPlanSaveVo newGet(BOutPlanSaveVo vo) {
        // 查询出库计划
        BOutPlanSaveVo outPlanSaveVo = mapper.newGet(vo.getId());
        // 查询出库物料明细list
        outPlanSaveVo.setDetailList(outPlanDetailMapper.newSelectOutGoodsList(vo.getId()));
        return outPlanSaveVo;
    }
}
