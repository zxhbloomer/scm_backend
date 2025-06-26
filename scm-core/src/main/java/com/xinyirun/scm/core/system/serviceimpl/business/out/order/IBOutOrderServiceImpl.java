package com.xinyirun.scm.core.system.serviceimpl.business.out.order;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.order.BOrderEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutOrderEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutOrderGoodsEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.inventory.BContractReportVo;
import com.xinyirun.scm.bean.system.vo.business.inventory.BDirectlyWarehouseVo;
import com.xinyirun.scm.bean.system.vo.business.inventory.BOutContractReportExportVo;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutOrderExportVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutOrderGoodsVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutOrderVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanListVo;
import com.xinyirun.scm.bean.system.vo.business.schedule.BScheduleVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.out.order.BOutOrderMapper;
import com.xinyirun.scm.core.system.service.wms.inplan.IBInPlanDetailService;
import com.xinyirun.scm.core.system.service.business.order.IBOrderService;
import com.xinyirun.scm.core.system.service.business.out.IBOutPlanDetailService;
import com.xinyirun.scm.core.system.service.business.out.order.IBOutOrderGoodsService;
import com.xinyirun.scm.core.system.service.business.out.order.IBOutOrderService;
import com.xinyirun.scm.core.system.service.business.schedule.IBScheduleService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BOutOrderAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 出库订单 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-11-02
 */
@Service
public class IBOutOrderServiceImpl extends BaseServiceImpl<BOutOrderMapper, BOutOrderEntity> implements IBOutOrderService {

    @Autowired
    private BOutOrderMapper mapper;

    @Autowired
    private IBOrderService ibOrderService;

    @Autowired
    private IBOutOrderGoodsService ibOutOrderGoodsService;

    @Autowired
    private BOutOrderAutoCodeServiceImpl autoCodeService;

    @Autowired
    private ISConfigService configService;

    @Autowired
    private IBInPlanDetailService inPlanDetailService;

    @Autowired
    private IBOutPlanDetailService outPlanDetailService;

    @Autowired
    private IBScheduleService scheduleService;

    /**
     * 查询分页列表
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<BOutOrderVo> selectPage(BOutOrderVo searchCondition) {
        // 分页条件
        Page<BOutOrderEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        IPage<BOutOrderVo> pageList = mapper.selectPage(pageCondition, searchCondition);

//        List<BOutOrderVo> bOutOrderVoList = new ArrayList<>();
//        for (BOutOrderVo vo : pageList.getRecords()) {
//            BOutOrderGoodsVo bOutOrderGoodsVo = new BOutOrderGoodsVo();
//            bOutOrderGoodsVo.setOrder_id(vo.getId());
//            List<BOutOrderGoodsVo> inOrderGoodsVoList = ibOutOrderGoodsService.list(bOutOrderGoodsVo);
//            vo.setDetailListData(inOrderGoodsVoList);
//            bOutOrderVoList.add(vo);
//        }
//        pageList.setRecords(bOutOrderVoList);

        return pageList;
    }

    @Override
    public IPage<BOutOrderVo> selectList(BOutOrderVo searchCondition) {
        // 分页条件
        Page<BOutOrderEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        IPage<BOutOrderVo> pageList = mapper.selectList(pageCondition, searchCondition);

        return pageList;
    }

    @Override
    public BOutOrderVo get(BOutOrderVo searchCondition) {
        BOutOrderVo vo = mapper.selectId(searchCondition.getId());

        BOutOrderGoodsVo bOutOrderGoodsVo = new BOutOrderGoodsVo();
        bOutOrderGoodsVo.setOrder_id(searchCondition.getId());
        List<BOutOrderGoodsVo> inOrderGoodsVoList = ibOutOrderGoodsService.list(bOutOrderGoodsVo);
        vo.setDetailListData(inOrderGoodsVoList);

        return vo;
    }

    @Override
    public BOutOrderVo selectById(int id) {
        return mapper.selectId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(BOutOrderVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        BOutOrderEntity entity = (BOutOrderEntity) BeanUtilsSupport.copyProperties(vo, BOutOrderEntity.class);
        entity.setOrder_no(autoCodeService.autoCode().getCode());
        entity.setSource_type(DictConstant.DICT_B_ORDER_SOURCE_TYPE_WMS);
        entity.setStatus(DictConstant.DICT_B_OUT_ORDER_STATUS_TWO);
        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());

        if (vo.getDetailListData() != null) {
            for (BOutOrderGoodsVo bOutOrderGoodsVo : vo.getDetailListData()) {
                bOutOrderGoodsVo.setOrder_id(entity.getId());
                bOutOrderGoodsVo.setRate(bOutOrderGoodsVo.getRate().divide(BigDecimal.valueOf(100)));
                ibOutOrderGoodsService.insert(bOutOrderGoodsVo);
            }
        }

        BOrderVo bOrderVo = new BOrderVo();
        BeanUtilsSupport.copyProperties(entity, bOrderVo);
        bOrderVo.setSerial_id(entity.getId());
        bOrderVo.setSerial_type(SystemConstants.ORDER.B_OUT_ORDER);
        bOrderVo.setCustomer_id(entity.getClient_id());
        bOrderVo.setId(null);
        ibOrderService.insert(bOrderVo);

        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(BOutOrderVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BOutOrderEntity entity = (BOutOrderEntity) BeanUtilsSupport.copyProperties(vo, BOutOrderEntity.class);
        int updCount = mapper.updateById(entity);
        if (updCount == 0) {
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }

        // 更新 b_order 表
        // 更新 b_order 表
        BOrderEntity orderEntity = ibOrderService.getOne(new LambdaQueryWrapper<BOrderEntity>()
                .eq(BOrderEntity::getSerial_id, vo.getId())
                .eq(BOrderEntity::getSerial_type, SystemConstants.ORDER.B_OUT_ORDER));
        orderEntity.setSerial_id(entity.getId());
        orderEntity.setSerial_type(SystemConstants.ORDER.B_OUT_ORDER);
        orderEntity.setCustomer_id(vo.getClient_id());
        orderEntity.setOrder_no(vo.getOrder_no());
        orderEntity.setSource_type(vo.getSource_type());
        orderEntity.setBill_type(vo.getBill_type());
        orderEntity.setContract_no(vo.getContract_no());
        orderEntity.setShip_name(vo.getShip_name());
        orderEntity.setContract_dt(vo.getContract_dt());
        orderEntity.setContract_num(vo.getContract_num());
        orderEntity.setBusiness_type_id(vo.getBusiness_type_id());
        orderEntity.setBusiness_type_code(vo.getBusiness_type_code());
        ibOrderService.updateById(orderEntity);

        // 全删全增
        ibOutOrderGoodsService.remove(new LambdaQueryWrapper<BOutOrderGoodsEntity>()
                .eq(BOutOrderGoodsEntity::getOrder_id, vo.getId()));

        // 新增
        for (BOutOrderGoodsVo bOutOrderGoodsVo : vo.getDetailListData()) {
            bOutOrderGoodsVo.setOrder_id(entity.getId());
            bOutOrderGoodsVo.setRate(bOutOrderGoodsVo.getRate().divide(BigDecimal.valueOf(100)));
            ibOutOrderGoodsService.insert(bOutOrderGoodsVo);
        }


   /*     // 判断是否有删除的
        QueryWrapper<BOutOrderGoodsEntity> eq = new QueryWrapper<BOutOrderGoodsEntity>().eq("order_id", vo.getId());
        List<BOutOrderGoodsEntity> list = ibOutOrderGoodsService.list(eq);
        // 如果 查出来的有， 前端传过来的没有， 视为删除 前端没有传过来的数据
        List<Integer> old = list.stream().map(BOutOrderGoodsEntity::getId).collect(Collectors.toList());
        List<Integer> newList = vo.getDetailListData().stream().filter(item -> null != item.getId()).map(BOutOrderGoodsVo::getId)
                .collect(Collectors.toList());
        // 判断两个集合元素是否相同
        if (CollectionUtils.containsAny(old, newList)) {
            Collection subtract = org.apache.commons.collections.CollectionUtils.subtract(old, newList);
            ibOutOrderGoodsService.remove(new LambdaQueryWrapper<BOutOrderGoodsEntity>().in(BOutOrderGoodsEntity::getId, subtract));
        }
        // 执行删除操作

        if (vo.getDetailListData() != null) {
            for (BOutOrderGoodsVo bOutOrderGoodsVo : vo.getDetailListData()) {
                bOutOrderGoodsVo.setOrder_id(entity.getId());
                bOutOrderGoodsVo.setRate(bOutOrderGoodsVo.getRate().divide(BigDecimal.valueOf(100)));
                if (bOutOrderGoodsVo.getId() == null) {
                    ibOutOrderGoodsService.insert(bOutOrderGoodsVo);
                } else {
                    ibOutOrderGoodsService.update(bOutOrderGoodsVo);
                }
            }
        }*/
        return UpdateResultUtil.OK(updCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<BOutOrderVo> bean) {
        // 判断是否是wms创建的订单, 如果是业务中台传过来的, 不能删除
        for (BOutOrderVo vo : bean) {
            Integer orderId = vo.getId();
            // 根据 ID 查询
            BOutOrderEntity outOrderEntity = mapper.selectById(orderId);
            if (null == outOrderEntity) {
                throw new RuntimeException("存在已被删除的订单,请刷新页面重试");
            } else {
                // 只删除 wms 自己创建的
                if (DictConstant.DICT_B_ORDER_SOURCE_TYPE_WMS.equals(outOrderEntity.getSource_type())) {
                    // 查询采购订单下是否有非作废的入库计划
//                    List<BInPlanDetailVo> planDetailList = inPlanDetailService.selectByOrderIdAndOrderType(orderId, DictConstant.DICT_SYS_CODE_TYPE_B_OUT_ORDER);
//                    if (!CollectionUtils.isEmpty(planDetailList)) {
//                        throw new RuntimeException("删除失败, 存在没有作废的入库计划");
//                    }
                    // 查询采购订单下是否有非作废的入库单
//                    List<BInPlanDetailVo> inList = inPlanDetailService.selectInByOrderIdAndOrderType(orderId, DictConstant.DICT_SYS_CODE_TYPE_B_OUT_ORDER);
//                    if (!CollectionUtils.isEmpty(inList)) {
//                        throw new RuntimeException("删除失败, 存在没有作废的入库单");
//                    }
                    // 查询采购订单下是否有非作废的出库计划
                    List<BOutPlanListVo> outPlanList = outPlanDetailService.selectOutPlanByOrderIdAndOrderType(orderId, DictConstant.DICT_SYS_CODE_TYPE_B_OUT_ORDER);
                    if (!CollectionUtils.isEmpty(outPlanList)) {
                        throw new RuntimeException("删除失败, 存在没有作废的出库计划");
                    }
                    List<BOutPlanListVo> outList = outPlanDetailService.selectOutByOrderIdAndOrderType(orderId, DictConstant.DICT_SYS_CODE_TYPE_B_OUT_ORDER);
                    if (!CollectionUtils.isEmpty(outList)) {
                        throw new RuntimeException("删除失败, 存在没有作废的出库单");
                    }
                    // 查询采购订单下是否有非作废的物流订单
                    List<BScheduleVo> bScheduleVos = scheduleService.selectScheduleByOrderId(orderId, DictConstant.DICT_SYS_CODE_TYPE_B_OUT_ORDER);
                    if (!CollectionUtils.isEmpty(bScheduleVos)) {
                        throw new RuntimeException("删除失败, 存在没有作废的物流订单");
                    }
                    log.debug("删除的数据-->" + JSONObject.toJSONString(vo));
                    // 删除
                    mapper.deleteById(orderId);
                    // 删除详情
                    ibOutOrderGoodsService.remove(new LambdaQueryWrapper<BOutOrderGoodsEntity>().eq(BOutOrderGoodsEntity::getOrder_id, orderId));
//                    // 删除订单
                    ibOrderService.remove(new LambdaQueryWrapper<BOrderEntity>().eq(BOrderEntity::getSerial_id, orderId)
                            .eq(BOrderEntity::getSerial_type, DictConstant.DICT_SYS_CODE_TYPE_B_OUT_ORDER));
                } else {
                    throw new RuntimeException("删除失败, 存在从业务中台传过来的订单");
                }
            }
        }
      /*  int delCount = mapper.deleteById(vo.getId());
        if(delCount == 0){
            throw new UpdateErrorException("您提交的数据不存在，请查询后重新操作。");
        }
        return DeleteResultUtil.OK(delCount);*/
    }

    /**
     * 销售合同 汇总
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<BContractReportVo> queryOutContractList(BContractReportVo searchCondition) {
        // 分页条件
        Page<BContractReportVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.queryOutContractList(pageCondition, searchCondition);
    }

    /**
     * 销售合同 汇总 求和
     *
     * @param param
     * @return
     */
    @Override
    public BContractReportVo queryOutContractListSum(BContractReportVo param) {
        return mapper.queryOutContractListSum(param);
    }

    /**
     * 销售合同导出， 部分导出
     *
     * @param param
     * @return
     */
    @Override
    public List<BOutContractReportExportVo> queryOutContractListExport(List<BContractReportVo> param) {
        return mapper.queryOutContractListExport(param);
    }

    /**
     * 销售合同导出， 全部导出
     *
     * @param param
     * @return
     */
    @Override
    public List<BOutContractReportExportVo> queryOutContractListExportAll(BContractReportVo param) {
        // 导出限制开关
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectExportNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.queryOutContractListExportAll(param);
    }

    /**
     * 直属库合同统计
     *
     * @param param
     * @return
     */
    @Override
    public IPage<BDirectlyWarehouseVo> getDirectlyWarehouseList(BDirectlyWarehouseVo param) {
        // 分页条件
        Page<BContractReportVo> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, param.getPageCondition().getSort());
        return mapper.getDirectlyWarehouseList(param, pageCondition);
    }

    /**
     * 求和
     *
     * @param searchCondition 参数
     * @return BOutOrderVo
     */
    @Override
    public BOutOrderVo getListSum(BOutOrderVo searchCondition) {
        return mapper.getListSum(searchCondition);
    }

    /**
     * 导出
     *
     * @param param
     * @return
     */
    @Override
    public List<BOutOrderExportVo> exportOutOrder(BOutOrderVo param) {
        // 全部导出条数限制
        if (StringUtils.isEmpty(param.getIds())) {
            SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
            // 查询导出条数
            if (StringUtils.isNotNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
                int count = mapper.exportCount(param);
                if (count > Integer.parseInt(sConfigEntity.getExtra1()))
                    throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.selectExportList(param);
    }

    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(BOutOrderVo vo, String moduleType) {
        BOutOrderVo checkOrderVo = (BOutOrderVo) BeanUtilsSupport.copyProperties(vo, BOutOrderVo.class);
        // 按合同编号和订单编号查询是否存在数据
        List<BOutOrderEntity> selectByOrder = mapper.selectOrderByContract(checkOrderVo);

        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
//                if (selectByOrder.size() >= 1) {
//                    return CheckResultUtil.NG("新增保存出错：订单编号："+vo.getOrder_no()+",合同编号："+vo.getContract_no()+"出现重复");
//                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (selectByOrder.size() >= 2) {
                    return CheckResultUtil.NG("新增保存出错：订单编号：" + vo.getOrder_no() + ",合同编号：" + vo.getContract_no() + "出现重复");
                }
                // 业务中台推过来的不可修改
                if (!DictConstant.DICT_B_ORDER_SOURCE_TYPE_WMS.equals(selectByOrder.get(0).getSource_type())){
                    return CheckResultUtil.NG("新增保存出错：订单编号：" + vo.getOrder_no() + ",合同编号：" + vo.getContract_no() + "不可修改");
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }
}
