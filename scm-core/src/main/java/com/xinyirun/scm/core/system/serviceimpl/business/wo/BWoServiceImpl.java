package com.xinyirun.scm.core.system.serviceimpl.business.wo;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.in.BInEntity;
import com.xinyirun.scm.bean.entity.busniess.inplan.BInPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.inplan.BInPlanEntity;
import com.xinyirun.scm.bean.entity.busniess.order.BOrderEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutExtraEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutPlanEntity;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BReleaseOrderDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.wo.BWoEntity;
import com.xinyirun.scm.bean.entity.busniess.wo.BWoMaterialEntity;
import com.xinyirun.scm.bean.entity.busniess.wo.BWoProductEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.bo.inventory.warehouse.MBLWBo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanListVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutVo;
import com.xinyirun.scm.bean.system.vo.business.pp.BPpVo;
import com.xinyirun.scm.bean.system.vo.business.releaseorder.BReleaseOrderDetailVo;
import com.xinyirun.scm.bean.system.vo.business.releaseorder.BReleaseOrderVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoMaterialVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoProductVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.bean.system.vo.master.customer.MCustomerVo;
import com.xinyirun.scm.bean.system.vo.master.customer.MOwnerVo;
import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitCalcVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.out.BOutExtraMapper;
import com.xinyirun.scm.core.system.mapper.business.wo.BWoMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.inventory.ICommonInventoryLogicService;
import com.xinyirun.scm.core.system.service.wms.in.IBInService;
import com.xinyirun.scm.core.system.service.wms.inplan.IBInPlanDetailService;
import com.xinyirun.scm.core.system.service.wms.inplan.IBInPlanService;
import com.xinyirun.scm.core.system.service.business.order.IBOrderService;
import com.xinyirun.scm.core.system.service.business.out.IBOutPlanDetailService;
import com.xinyirun.scm.core.system.service.business.out.IBOutPlanService;
import com.xinyirun.scm.core.system.service.business.out.IBOutService;
import com.xinyirun.scm.core.system.service.business.pp.IBPpService;
import com.xinyirun.scm.core.system.service.business.releaseorder.IBReleaseOrderDetailService;
import com.xinyirun.scm.core.system.service.business.releaseorder.IBReleaseOrderService;
import com.xinyirun.scm.core.system.service.business.wo.IBWoMaterialService;
import com.xinyirun.scm.core.system.service.business.wo.IBWoProductService;
import com.xinyirun.scm.core.system.service.business.wo.IBWoService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.master.customer.IMCustomerService;
import com.xinyirun.scm.core.system.service.master.customer.IMOwnerService;
import com.xinyirun.scm.core.system.service.master.goods.unit.IMGoodsUnitCalcService;
import com.xinyirun.scm.core.system.service.master.inventory.IMInventoryService;
import com.xinyirun.scm.core.system.service.master.user.IMStaffService;
import com.xinyirun.scm.core.system.service.master.warehouse.IMWarehouseService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.business.todo.TodoService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.*;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-29
 */
@Service
public class BWoServiceImpl extends ServiceImpl<BWoMapper, BWoEntity> implements IBWoService {

    @Autowired
    private BWoMapper mapper;

    @Autowired
    private IBWoProductService productService;

    @Autowired
    private IBWoMaterialService materialService;

    @Autowired
    private IBReleaseOrderService orderService;

    @Autowired
    private IBReleaseOrderDetailService releaseOrderDetailService;

    @Autowired
    private IMOwnerService ownerService;

    @Autowired
    private BWoAutoCodeServiceImpl autoCodeService;

    @Autowired
    private IMWarehouseService warehouseService;

    @Autowired
    private IMInventoryService inventoryService;

    @Autowired
    private IMCustomerService customerService;

    @Autowired
    private BOutPlanAutoCodeServiceImpl outPlanAutoCodeService;

    @Autowired
    private IBOutPlanService outPlanService;

    @Autowired
    private IMGoodsUnitCalcService imGoodsUnitCalcService;

    @Autowired
    private BOutPlanDetailAutoCodeServiceImpl bOutPlanDetailAutoCodeService;

    @Autowired
    private IBOutPlanDetailService outPlanDetailService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private BOutAutoCodeServiceImpl outAutoCodeService;

    @Autowired
    private IBOutService outService;

    @Autowired
    private BOutExtraMapper bOutExtraMapper;

    @Autowired
    private ICommonInventoryLogicService iCommonInventoryLogicService;

    @Autowired
    private BInPlanAutoCodeServiceImpl inPlanAutoCodeService;

    @Autowired
    private IBInPlanService inPlanService;

    @Autowired
    private BInPlanDetailAutoCodeServiceImpl inPlanDetailAutoCodeService;

    @Autowired
    private IBInPlanDetailService inPlanDetailService;

    @Autowired
    private BInAutoCodeServiceImpl inAutoService;

    @Autowired
    private IBInService inService;

    @Autowired
    private MCancelService cancelService;

    @Autowired
    private IBOrderService ibOrderService;

    @Autowired
    private IMStaffService staffService;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private IMGoodsUnitCalcService unitCalcService;

    @Autowired
    @Lazy
    private IBPpService iBPpService;

    /**
     * 新增
     *
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BWoVo> insert(BWoVo param) {
        // 校验基本数据信息
        checkBasicMessage(param);
        // 单位管理
        checkGoodsUnit(param);
        // 检验数据
        List<Map<String, String>> error_msg = check(param);
        if (error_msg.size() != 0) {
            throw new BusinessException(error_msg.get(0).get("error_msg"));
        }
        // 库存校验
        checkInventory(param.getMaterial_list(), param.getOwner_id());

        BWoEntity entity = new BWoEntity();
        // 不选择订单的情况下
        if (null != param.getDelivery_order_id()) {
            updateEntity(entity, param.getDelivery_order_id(), param.getDelivery_sku_code());
        }
        entity.setOwner_id(param.getOwner_id());
        // 添加工单信息
        entity.setCode(autoCodeService.autoCode().getCode());
        entity.setRouter_id(param.getRouter_id());
        // 默认制单状态
        entity.setStatus(DictConstant.DICT_B_WO_STATUS_2);

        //生产计划ID
        entity.setPp_id(param.getPp_id());
        int rtn = mapper.insert(entity);
        if (rtn == 0) {
            throw new InsertErrorException("新增失败");
        }
        // 新增生产管理产成品, 副产品
        productService.insertAll(param.getProduct_list(), entity.getId());
        // 新增原材料
        materialService.insertAll(param.getMaterial_list(), entity.getId());

        List<BWoProductVo> productVoList = productService.selectByWoId(entity.getId());
        List<BWoMaterialVo> materialVoList = materialService.selectByWoId(entity.getId());
        entity.setJson_product_list(JSON.toJSONString(productVoList));
        entity.setJson_material_list(JSON.toJSONString(materialVoList));
        mapper.updateById(entity);
        // 添加 b_order 表
        BOrderEntity order = new BOrderEntity();
        order.setSerial_id(entity.getId());
        order.setSerial_type(SystemConstants.SERIAL_TYPE.B_WO);
        ibOrderService.save(order);

        // 添加待办
        todoService.insertTodo(entity.getId(), SystemConstants.SERIAL_TYPE.B_WO, SystemConstants.PERMS.B_WO_AUDIT);

        // 根据ID 查询, 返回
        BWoVo bWoVo = mapper.selectById(entity.getId());
        return InsertResultUtil.OK(bWoVo);
    }



    /**
     * 单位管理
     * 1. 商品存在单位, 但是没有选择, 默认选择为 吨
     * 2. 商品没有单位换算单位, 默认新增单位换算关系, 吨:吨
     * @param param
     */
    private void checkGoodsUnit(BWoVo param) {
        // 原材料
        for (BWoMaterialVo bWoMaterialVo : param.getMaterial_list()) {
            if (null == bWoMaterialVo.getUnit_id()) {
                // 查询单位换算关系
                List<MGoodsUnitCalcVo> goodsUnitCalcVoList = getGoodsUnitCalc(bWoMaterialVo.getSku_id());
                if (CollectionUtils.isEmpty(goodsUnitCalcVoList)) {
                    // 为空, 新增默认关系
                    MGoodsUnitCalcVo calcVo = insertGoodsUnitCalc(bWoMaterialVo.getSku_id());
                    bWoMaterialVo.setUnit_id(calcVo.getSrc_unit_id());
                    bWoMaterialVo.setUnit_name(calcVo.getSrc_unit());
                } else {
                    // 不为空, 默认选择 吨 为单位
                    if (goodsUnitCalcVoList.size() == 1) {
                        MGoodsUnitCalcVo calcVo = goodsUnitCalcVoList.get(0);
                        bWoMaterialVo.setUnit_id(calcVo.getSrc_unit_id());
                        bWoMaterialVo.setUnit_name(calcVo.getSrc_unit());
                    } else if (goodsUnitCalcVoList.size() > 1) {
                        SConfigEntity config = isConfigService.selectByKey(SystemConstants.DEFAULT_GOODS_UNIT_CALC);
                        if (null == config) {
                            throw new InsertErrorException("保存失败, 未配置默认单位换算关系");
                        }
                        JSONObject jsonObject = new JSONObject(config.getValue());
                        // 如果有两个, 取吨
                        Optional<MGoodsUnitCalcVo> vo = goodsUnitCalcVoList.stream().filter(item -> Objects.equals(item.getSrc_unit_id(), jsonObject.getInt("src_unit_id"))).findFirst();
                        if (vo.isPresent()) {
                            // 如果有吨, 选择 吨的
                            bWoMaterialVo.setUnit_id(vo.get().getSrc_unit_id());
                            bWoMaterialVo.setUnit_name(vo.get().getSrc_unit());
                        } else {
                            // 如果没有吨, 也选择第一个
                            MGoodsUnitCalcVo calcVo = goodsUnitCalcVoList.get(0);
                            bWoMaterialVo.setUnit_id(calcVo.getSrc_unit_id());
                            bWoMaterialVo.setUnit_name(calcVo.getSrc_unit());
                        }
                    }
                }
            }
        }
        // 产成品, 副产品
        for (BWoProductVo bWoProductVo : param.getProduct_list()) {
            if (null == bWoProductVo.getUnit_id()) {
                // 查询单位换算关系
                List<MGoodsUnitCalcVo> goodsUnitCalcVoList = getGoodsUnitCalc(bWoProductVo.getSku_id());
                if (CollectionUtils.isEmpty(goodsUnitCalcVoList)) {
                    // 为空, 新增默认关系
                    MGoodsUnitCalcVo calcVo = insertGoodsUnitCalc(bWoProductVo.getSku_id());
                    bWoProductVo.setUnit_id(calcVo.getSrc_unit_id());
                    bWoProductVo.setUnit_name(calcVo.getSrc_unit());
                } else {
                    // 不为空, 默认选择 吨 为单位
                    if (goodsUnitCalcVoList.size() == 1) {
                        MGoodsUnitCalcVo calcVo = goodsUnitCalcVoList.get(0);
                        bWoProductVo.setUnit_id(calcVo.getSrc_unit_id());
                        bWoProductVo.setUnit_name(calcVo.getSrc_unit());
                    } else if (goodsUnitCalcVoList.size() > 1) {
                        SConfigEntity config = isConfigService.selectByKey(SystemConstants.DEFAULT_GOODS_UNIT_CALC);
                        if (null == config) {
                            throw new InsertErrorException("保存失败, 未配置默认单位换算关系");
                        }
                        JSONObject jsonObject = new JSONObject(config.getValue());
                        // 如果有两个, 取吨
                        Optional<MGoodsUnitCalcVo> vo = goodsUnitCalcVoList.stream().filter(item -> Objects.equals(item.getSrc_unit_id(), jsonObject.getInt("src_unit_id"))).findFirst();
                        if (vo.isPresent()) {
                            // 如果有吨, 选择 吨的
                            bWoProductVo.setUnit_id(vo.get().getSrc_unit_id());
                            bWoProductVo.setUnit_name(vo.get().getSrc_unit());
                        } else {
                            // 如果没有吨, 也选择第一个
                            MGoodsUnitCalcVo calcVo = goodsUnitCalcVoList.get(0);
                            bWoProductVo.setUnit_id(calcVo.getSrc_unit_id());
                            bWoProductVo.setUnit_name(calcVo.getSrc_unit());
                        }
                    }
                }
            }
        }
    }

    /**
     * 新增默认关系
     * @param skuId
     */
    public MGoodsUnitCalcVo insertGoodsUnitCalc(Integer skuId) {
        SConfigEntity config = isConfigService.selectByKey(SystemConstants.DEFAULT_GOODS_UNIT_CALC);
        if (null == config) {
            throw new InsertErrorException("保存失败, 未配置默认单位换算关系");
        }
        JSONObject jsonObject = new JSONObject(config.getValue());
        // 查询配置
        MGoodsUnitCalcVo vo = new MGoodsUnitCalcVo();
        vo.setSku_id(skuId);
        vo.setSrc_unit(jsonObject.getStr("src_unit"));
        vo.setSrc_unit_id(jsonObject.getInt("src_unit_id"));
        vo.setSrc_unit_code(jsonObject.getStr("src_unit_code"));
        vo.setCalc(jsonObject.getBigDecimal("calc"));
        vo.setRemark(jsonObject.getStr("remark"));
//        vo.setTgt_unit_id(jsonObject.getInt("tgt_unit_id"));
//        vo.setTgt_unit_code(jsonObject.getStr("tgt_unit_code"));
//        vo.setTgt_unit(jsonObject.getStr("tgt_unit"));
//        vo.setEnable(jsonObject.getBool("enable"));
        return unitCalcService.insert(vo).getData();
    }

    /**
     * 查询单位换算关系
     * @param sku_id
     * @return
     */
    public List<MGoodsUnitCalcVo> getGoodsUnitCalc(Integer sku_id) {
        MGoodsUnitCalcVo calcVo = new MGoodsUnitCalcVo();
        calcVo.setSku_id(sku_id);
        return unitCalcService.selectList(calcVo);
    }

    /**
     * 基础数据校验, 库存校验
     *
     * @param param
     */
    private void    checkBasicMessage(BWoVo param) {
        List<BWoMaterialVo> material_list = param.getMaterial_list();
        for (BWoMaterialVo bWoMaterialVo : material_list) {
            if (StringUtils.isEmpty(bWoMaterialVo.getWarehouse_code()))
                throw new BusinessException("原材料未选择仓库");
            if (Objects.isNull(bWoMaterialVo.getSku_id()))
                throw new BusinessException("原材料规格不能为空");
        }
        for (BWoProductVo bWoProductVo : param.getProduct_list()) {
            if (Objects.isNull(bWoProductVo.getSku_id()))
                throw new BusinessException("产成品或副产品没有选择规格");
            if (StringUtils.isEmpty(bWoProductVo.getWarehouse_code()))
                throw new BusinessException("产成品, 副产品未选择仓库");
            if (Objects.isNull(bWoProductVo.getWo_qty()))
                throw new BusinessException("产成品, 副产品未输入生产入库数量");
            // 校验产成品生产入库数量是否大于订单数量
            if (DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C.equals(bWoProductVo.getType())) {
                // 产成品不能为空
                if (bWoProductVo.getWo_qty().compareTo(BigDecimal.ZERO) == 0) {
                    throw new BusinessException("产成品生产入库数量不能为0");
                }
                // 如果没有选择放货指令, 不进行此判断
                if (null != param.getDelivery_order_detail_id()) {
                    checkHasProductNum(bWoProductVo, param.getId(), param.getDelivery_order_detail_id());
                }
                if (param.getPp_id() != null) {
                    //校验生产计划  生产入库数量，需>0,且最大不可超过该生产计划.(计划生产入库数量-已生产数量)
                    BPpVo bPpVo = iBPpService.getDetail(param.getPp_id());
                    BigDecimal planPP = bPpVo.getProduct_list().stream().filter(k -> DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C.equals(k.getType())).toList().get(0).getQty();

                    if ((planPP.subtract(bPpVo.getHas_product_num())).compareTo(bWoProductVo.getWo_qty())<0) {
                        throw new BusinessException(String.format("生产入库数量不可超过该生产计划最大可生产数量%s",planPP.subtract(bPpVo.getHas_product_num())));
                    }
                }
            }
        }
    }

    /**
     * 公式校验, 键值 error_msg
     *
     * @param param
     * @return
     */
    @Override
    public List<Map<String, String>> check(BWoVo param) {
        BWoVo bWoVo = checkQty(param);
        List<Map<String, String>> result = new LinkedList<>();
        // 校验 产成品, 副产品配比是不是 100%
        productService.checkProductRouter(bWoVo.getProduct_list(), result);
        // 校验 原材料 配比是不是 100%
        materialService.checkMaterialRouter(bWoVo.getMaterial_list(), result);
        // 校验产成品, 副产品产量 == 原材料消耗数量
        checkIsEquals(bWoVo.getMaterial_list(), bWoVo.getProduct_list(), result);
        return result;
    }

    /**
     * 更新
     *
     * @param param
     */
    @Override
    public UpdateResultAo<BWoVo> updateParam(BWoVo param) {
        Integer id = param.getId();
        Assert.notNull(id, "ID 不能为空");
        // 校验基础信息
        checkBasicMessage(param);
        // 单位管理
        checkGoodsUnit(param);
        // 查询
        BWoEntity entity = mapper.selectOne(new LambdaQueryWrapper<BWoEntity>().eq(BWoEntity::getId, id));
        // 校验状态
        checkLogic(entity, CheckResultAo.UPDATE_CHECK_TYPE);
        // 检验数据
        List<Map<String, String>> error_msg = check(param);
        if (error_msg.size() != 0) {
            throw new BusinessException(error_msg.get(0).get("error_msg"));
        }
        // 库存校验
        checkInventory(param.getMaterial_list(), param.getOwner_id());

        if (null != param.getDelivery_order_id() && !param.getDelivery_order_id().equals(entity.getDelivery_order_id())) {
            updateEntity(entity, param.getDelivery_order_id(), param.getDelivery_sku_code());
        }
        entity.setOwner_id(param.getOwner_id());
        // 如果是 审核驳回 状态, 状态更新为 提交
        if (DictConstant.DICT_B_WO_STATUS_4.equals(entity.getStatus())) {
            entity.setStatus(DictConstant.DICT_B_WO_STATUS_2);
            // 生成已办, 改了之后,只有审核驳回的可以重新提交
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_WO, SystemConstants.PERMS.B_WO_REJECT);
        } else {
            // 生成已办, 改了之后,只有审核驳回的可以重新提交
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_WO, SystemConstants.PERMS.B_WO_SUBMIT);
        }
        // 添加待办
        todoService.insertTodo(entity.getId(), SystemConstants.SERIAL_TYPE.B_WO, SystemConstants.PERMS.B_WO_AUDIT);
        entity.setRouter_id(param.getRouter_id());
        int rtn = mapper.updateById(entity);
        if (rtn == 0) {
            throw new UpdateErrorException("更新失败");
        }
        // 先删除
        productService.remove(new LambdaQueryWrapper<BWoProductEntity>().eq(BWoProductEntity::getWo_id, id));
        materialService.remove(new LambdaQueryWrapper<BWoMaterialEntity>().eq(BWoMaterialEntity::getWo_id, id));
        // 新增生产管理产成品, 副产品
        productService.insertAll(param.getProduct_list(), id);
        // 新增原材料
        materialService.insertAll(param.getMaterial_list(), id);

        List<BWoProductVo> productVoList = productService.selectByWoId(entity.getId());
        List<BWoMaterialVo> materialVoList = materialService.selectByWoId(entity.getId());
        entity.setJson_product_list(JSON.toJSONString(productVoList));
        entity.setJson_material_list(JSON.toJSONString(materialVoList));
        mapper.updateById(entity);

        // 根据ID 查询, 返回
        BWoVo bWoVo = mapper.selectById(entity.getId());
        return UpdateResultUtil.OK(bWoVo);
    }

    /**
     * 提交, 提交时判断原材料库存是否足够
     *
     * @param param 入参
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(List<BWoVo> param) {
        for (BWoVo bWoVo : param) {
            BWoEntity entity = mapper.selectOne(new LambdaQueryWrapper<BWoEntity>().eq(BWoEntity::getId, bWoVo.getId()));
            // 查询产成品, 校验产成品数量
            List<BWoProductVo> bWoProductVos = productService.selectByWoId(entity.getId());
            for (BWoProductVo bWoProductVo : bWoProductVos) {

                //校验逻辑说明:1、生产订单、审核驳回状态的生产订单，点击提交，需判断，1.1、生产入库数量，需>0,且最大不可超过该生产计划.(计划生产入库数量-已生产数量)
                if (entity.getPp_id() != null) {
                    BPpVo bPpVo = iBPpService.getDetail(entity.getPp_id());
                    BigDecimal planPP = bPpVo.getProduct_list().stream().filter(k -> DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C.equals(k.getType())).toList().get(0).getQty();

                    if ((planPP.subtract(bPpVo.getHas_product_num())).compareTo(bWoProductVo.getWo_qty())<0) {
                        throw new BusinessException(String.format("生产入库数量不可超过该生产计划最大可生产数量%s",planPP.subtract(bPpVo.getHas_product_num())));
                    }
                }

                if (entity.getDelivery_order_detail_id() != null && DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C.equals(bWoProductVo.getType())) {
                    // 产成品检验数量
                    checkHasProductNum(bWoProductVo,null, entity.getDelivery_order_detail_id());
                }
            }

            // 查询原材料, 校验库存
            List<BWoMaterialVo> bWoMaterialVos = materialService.selectByWoId(entity.getId());
            for (BWoMaterialVo bWoMaterialVo : bWoMaterialVos) {
                checkInventory(bWoMaterialVo, entity.getOwner_id());
//                if (inventoryInfo.getQty_avaible().compareTo(bWoMaterialVo.getWo_qty()) < 0) {
//                    throw new BusinessException(String.format("【%s】仓库，【%s】商品库存不足。当前库存【%s】吨，生产所需【%s】吨", bWoMaterialVo.getWarehouse_name(), bWoMaterialVo.getGoods_name(), inventoryInfo.getQty_avaible(), bWoMaterialVo.getWo_qty()));
//                }
            }
            checkLogic(entity, CheckResultAo.SUBMIT_CHECK_TYPE);

            // 判断 提交之前的状态,
            if (DictConstant.DICT_B_WO_STATUS_1.equals(entity.getStatus())) {
                // 生成已办,兼容 未提交的
                todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_WO, SystemConstants.PERMS.B_WO_SUBMIT);
            } else if (DictConstant.DICT_B_WO_STATUS_4.equals(entity.getStatus())) {
                // 生成已办, 改了之后,只有审核驳回的可以重新提交
                todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_WO, SystemConstants.PERMS.B_WO_REJECT);
            }
            // 添加待办
            todoService.insertTodo(entity.getId(), SystemConstants.SERIAL_TYPE.B_WO, SystemConstants.PERMS.B_WO_AUDIT);


            entity.setStatus(DictConstant.DICT_B_WO_STATUS_2);
            mapper.updateById(entity);
        }

    }

    /**
     * 关联放货订单的情况下
     * 新增, 提交, 修改时校验 改商品的生产数量 是否 大于对应的已生产数量(生产配方 和 配比 的已提交 和 已审核的)
     * @param bWoProductVo
     * @param wo_id
     * @param delivery_order_detail_id
     */
    private void checkHasProductNum(BWoProductVo bWoProductVo, Integer wo_id, Integer delivery_order_detail_id) {
        // 查询订单
        BReleaseOrderDetailEntity detail = releaseOrderDetailService.getById(delivery_order_detail_id);

        // 查询已生产数量, 排除更新时的数据
        BigDecimal hasProductNum = productService.selectHasProductNum(wo_id, delivery_order_detail_id);
        if ((hasProductNum.add(bWoProductVo.getWo_qty()).compareTo(detail.getQty()) > 0)) {
            BigDecimal subtract = detail.getQty().subtract(hasProductNum);
            throw new BusinessException(String.format("商品%s生产数量已超过放货指令最大生产数量，本次最大生产数量为%s"
                    ,bWoProductVo.getGoods_name(), subtract));
        }

    }

    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t10.warehouse_id")
    public IPage<BWoVo> selectPageList(BWoVo param) {
        // 分页条件
        Page<BWoVo> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, param.getPageCondition().getSort());

        param.setStaff_id(SecurityUtil.getStaff_id());

        IPage<BWoVo> pagelist = mapper.selectPageList(param, pageCondition);
        return pagelist;
    }

    /**
     * 根据 id 查询详情
     *
     * @param id
     * @return BWoVo
     */
    @Override
    public BWoVo getDetail(Integer id) {
        // 查询基本信息
        BWoVo detail = mapper.getDetail(id);
        // 转换类型
//        detail.setMaterial_list(JSONObject.parseArray(JSONObject.toJSONString(detail.getMaterial_list()), BWoMaterialVo.class));
//         查询副产品 中商品的 库存

        List<BWoProductVo> product_list = productService.selectByWoId(id);
        List<BWoMaterialVo> material_list = materialService.selectByWoId(id);
        detail.setProduct_list(product_list);
        detail.setMaterial_list(material_list);

        this.calcInventory(detail);
        return detail;
    }

    /**
     * 作废
     *
     * @param param 入参
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(BWoVo param) {
        BWoEntity entity = mapper.selectOne(new LambdaQueryWrapper<BWoEntity>().eq(BWoEntity::getId, param.getId()));
        // 校验状态
        checkLogic(entity, CheckResultAo.CANCEL_CHECK_TYPE);
        // 判断当前状态, 如果状态是审核通过, 需要作废出库单, 入库单, 释放库存
        if (DictConstant.DICT_B_WO_STATUS_3.equals(entity.getStatus())) {
            // 作废出库计划, 出库计划单
            cancelOut(entity.getId());
            cancelIn(entity.getId());
        }
        entity.setStatus(DictConstant.DICT_B_WO_STATUS_5);
        mapper.updateById(entity);

        // 作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(entity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_WO);
        mCancelVo.setRemark(param.getRemark());
        cancelService.insert(mCancelVo);

        // 生成已办
        todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_WO, SystemConstants.PERMS.B_WO_CANCEL);
    }

    /**
     * 作废入库单, 入库计划
     *
     * @param id
     */
    private void cancelIn(Integer id) {
        List<BWoProductVo> bWoProductVos = productService.selectByWoId(id);
        List<Integer> plan_id = bWoProductVos.stream().map(BWoProductVo::getB_in_plan_id).collect(Collectors.toList());
//        List<BInVo> inList = inService.selectIdsByPlanIds(plan_id);
//        inService.cancelDirect(inList);
        // 作废入库计划
//        List<BInPlanListVo> detailList = inPlanDetailService.selectByPlanIds(plan_id);
//        if (!CollectionUtils.isEmpty(detailList)) {
//            inPlanService.cancelDirect(detailList);
//        }
    }

    /**
     * 作废出库单, 出库计划
     *
     * @param id
     */
    private void cancelOut(Integer id) {
        List<BWoMaterialVo> bWoMaterialVos = materialService.selectByWoId(id);
        List<Integer> plan_id = bWoMaterialVos.stream().map(BWoMaterialVo::getB_out_plan_id).collect(Collectors.toList());
        List<BOutVo> outList = outService.selectIdsByOutPlanIds(plan_id);
        outService.cancelDirect(outList);
        // 作废计划单
        List<BOutPlanListVo> detailList = outPlanDetailService.selectByPlanIds(plan_id);
        if (!CollectionUtils.isEmpty(detailList)) {
            outPlanService.cancelDirect(detailList);
        }
    }

    /**
     * 审核通过
     *
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(List<BWoVo> param) {
        // 校验状态, 只有已提交可以审核通过
        for (BWoVo bWoVo : param) {
            BWoEntity entity = mapper.selectOne(new LambdaQueryWrapper<BWoEntity>().eq(BWoEntity::getId, bWoVo.getId()));
            checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE);
            // 校验原材料库存
            List<BWoMaterialVo> bWoMaterialVos = materialService.selectByWoId(entity.getId());
            for (BWoMaterialVo bWoMaterialVo : bWoMaterialVos) {
                checkInventory(bWoMaterialVo, entity.getOwner_id());
//                if (inventoryInfo.getQty_avaible().compareTo(bWoMaterialVo.getWo_qty()) < 0) {
//                    throw new BusinessException(String.format("【%s】仓库，【%s】商品库存不足。当前库存【%s】吨，生产所需【%s】吨", bWoMaterialVo.getWarehouse_name(), bWoMaterialVo.getGoods_name(), inventoryInfo.getQty_avaible(), bWoMaterialVo.getWo_qty()));
//                }
                // 原材料领料出库
                insertOutPlan(bWoMaterialVo, entity);
            }
            // 查询产成品, 副产品
            List<BWoProductVo> bWoProductVos = productService.selectByWoId(entity.getId());
            for (BWoProductVo bWoProductVo : bWoProductVos) {
                insertInPlan(bWoProductVo, entity);
            }
            // 更新状态
            entity.setStatus(DictConstant.DICT_B_WO_STATUS_3);
            entity.setE_id(SecurityUtil.getStaff_id().intValue());
            entity.setE_time(LocalDateTime.now());
            mapper.updateById(entity);

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_WO, SystemConstants.PERMS.B_WO_AUDIT);

        }



    }

    /**
     * 审核驳回, 已提交的单据
     *
     * @param param
     */
    @Override
    public void reject(List<BWoVo> param) {
        for (BWoVo bWoVo : param) {
            BWoEntity entity = mapper.selectOne(new LambdaQueryWrapper<BWoEntity>().eq(BWoEntity::getId, bWoVo.getId()));
            checkLogic(entity, CheckResultAo.REJECT_CHECK_TYPE);
            entity.setStatus(DictConstant.DICT_B_WO_STATUS_4);
            mapper.updateById(entity);

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_WO, SystemConstants.PERMS.B_WO_REJECT);
        }
    }

    /**
     * 计算
     *
     * @param param
     * @return
     */
    @Override
    public BWoVo checkQty(BWoVo param) {
        // 计算总数
        BigDecimal productAllNum = BigDecimal.ZERO;
        for (BWoProductVo bWoProductVo : param.getProduct_list()) {
            if (DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C.equals(bWoProductVo.getType())) {
                // 如果配比是0, 会报错
                if (BigDecimal.ZERO.compareTo(bWoProductVo.getWo_router()) != 0) {
                    // 计算总产量
                    productAllNum = bWoProductVo.getWo_qty().divide(bWoProductVo.getWo_router().divide(BigDecimal.valueOf(100)), 4, RoundingMode.HALF_UP);
                }
            }
        }
        // 计算副产品数量
        for (BWoProductVo bWoProductVo : param.getProduct_list()) {
            if (DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_F.equals(bWoProductVo.getType())) {
                // 计算总产量
                BigDecimal decimal = productAllNum.multiply(bWoProductVo.getWo_router().divide(BigDecimal.valueOf(100)));
                bWoProductVo.setWo_qty(decimal);
            }
        }
        // 计算原材料的数量
        for (BWoMaterialVo bWoMaterialVo : param.getMaterial_list()) {
            // 计算总产量
            BigDecimal decimal = productAllNum.multiply(bWoMaterialVo.getWo_router().divide(BigDecimal.valueOf(100)));
            bWoMaterialVo.setWo_qty(decimal);
        }
        // 计算总量
        BigDecimal productReduce = param.getProduct_list().stream().map(BWoProductVo::getWo_qty).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal materialReduce = param.getMaterial_list().stream().map(BWoMaterialVo::getWo_qty).reduce(BigDecimal.ZERO, BigDecimal::add);
        param.setProduct_actual(productReduce);
        param.setMaterial_actual(materialReduce);
        return param;
    }

    /**
     * 获取原材料库存
     *
     * @param param
     * @return
     */
    @Override
    public BWoVo calcInventory(BWoVo param) {
        Integer ownerId = param.getOwner_id();
        // 查询原材料 中商品的 库存
        for (BWoMaterialVo material : param.getMaterial_list()) {
            BigDecimal avaible = inventoryService.getQtyAvaibleBySWO(material.getSku_id() ,material.getWarehouse_id(), ownerId);
            material.setQty_avaible(avaible);
        }
        return param;
    }

    /**
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t10.warehouse_id")
    public List<BWoVo> exportList(BWoVo param) {
        // 导出条数限制控制
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (Objects.isNull(param.getIds()) && !Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            Long count = mapper.selectExportCount(param);

            if (count != null && count > Long.parseLong(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }

        }
        return mapper.exportList(param);
    }

    /**
     * 查询待办数量
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t10.warehouse_id")
    public Integer selectTodoCount(BWoVo param) {
        param.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.selectTodoCount(param);
    }

    /**
     * 求和
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t10.warehouse_id")
    public BWoVo selectListSum(BWoVo param) {
        return mapper.selectListSum(param);
    }

    /**
     * 新增 入库计划
     *
     * @param bWoProductVo
     * @param entity
     */
    private void insertInPlan(BWoProductVo bWoProductVo, BWoEntity entity) {
        BInPlanEntity in = new BInPlanEntity();
        in.setCode(inPlanAutoCodeService.autoCode().getCode());
        in.setPlan_time(LocalDateTime.now());
        in.setType(DictConstant.DICT_B_IN_PLAN_TYPE_SC);
        in.setOwner_id(entity.getOwner_id());
        // 查询货主
        MOwnerVo mOwnerVo = ownerService.selectById(entity.getOwner_id());
        // 查询委托方
        MCustomerVo mCustomerVo = customerService.selectByCreditNo(mOwnerVo.getCredit_no());
        in.setOwner_code(mOwnerVo.getCode());
        in.setConsignor_id(mCustomerVo.getId());
        in.setConsignor_code(mCustomerVo.getCode());
        inPlanService.save(in);

        // 更新 b_in_plan_id
        LambdaUpdateWrapper<BWoProductEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BWoProductEntity::getId, bWoProductVo.getId());
        updateWrapper.set(BWoProductEntity::getB_in_plan_id, in.getId());
        productService.update(updateWrapper);

        insertInPlanDetail(in, bWoProductVo);


    }

    /**
     * 新增 入库计划
     *
     * @param in
     * @param bWoProductVo
     */
    private void insertInPlanDetail(BInPlanEntity in, BWoProductVo bWoProductVo) {
        BInPlanDetailEntity entity = new BInPlanDetailEntity();
        entity.setNo(1);
//        entity.setPlan_id(in.getId());
//        entity.setStatus(DictConstant.DICT_B_IN_PLAN_STATUS_APPROVED);
        entity.setCode(inPlanDetailAutoCodeService.autoCode().getCode());
        entity.setSku_code(bWoProductVo.getSku_code());
        entity.setSku_id(bWoProductVo.getSku_id());
//        entity.setCount(bWoProductVo.getWo_qty());
        MGoodsUnitCalcVo mGoodsUnitCalcVo = new MGoodsUnitCalcVo();
        mGoodsUnitCalcVo.setSrc_unit_id(bWoProductVo.getUnit_id());
        mGoodsUnitCalcVo.setSku_id(bWoProductVo.getSku_id());
        MGoodsUnitCalcVo goodsUnitCalcVo = imGoodsUnitCalcService.selectOne(mGoodsUnitCalcVo);
        if (goodsUnitCalcVo == null) {
            entity.setWeight(bWoProductVo.getWo_qty());
//            entity.setHas_handle_weight(bWoProductVo.getWo_qty());
        } else {
            entity.setWeight(bWoProductVo.getWo_qty().multiply(goodsUnitCalcVo.getCalc()));
//            entity.setHas_handle_weight(bWoProductVo.getWo_qty().multiply(goodsUnitCalcVo.getCalc()));
        }
        entity.setVolume(BigDecimal.ZERO);
        entity.setWarehouse_id(bWoProductVo.getWarehouse_id());
        entity.setUnit_id(bWoProductVo.getUnit_id());
//        entity.setPending_count(BigDecimal.ZERO);
//        entity.setPending_weight(BigDecimal.ZERO);
//        entity.setPending_volume(BigDecimal.ZERO);
//        entity.setHas_handle_count(bWoProductVo.getWo_qty());
//        entity.setHas_handle_volume(BigDecimal.ZERO);

        // 系统自动审核
        Integer id = staffService.selectIdByStaffCode(SystemConstants.AUDIT_STAFF_CODE);
//        entity.setAudit_dt(LocalDateTime.now());
//        entity.setAudit_id(id);

        inPlanDetailService.save(entity);

        // 生成待办
        todoService.insertTodo(entity.getId(), SystemConstants.SERIAL_TYPE.B_IN_PLAN, SystemConstants.PERMS.B_IN_PLAN_DETAIL_OPERATE);

        // 生成入库单
        insertIn(entity, in, bWoProductVo);
    }

    /**
     * 生成入库DNA
     *
     * @param detailEntity
     * @param in
     */
    private void insertIn(BInPlanDetailEntity detailEntity, BInPlanEntity in, BWoProductVo vo) {
        BInEntity entity = new BInEntity();
        entity.setCode(inAutoService.autoCode().getCode());
        entity.setType(DictConstant.DICT_B_IN_TYPE_SC);
        entity.setStatus(DictConstant.DICT_B_IN_STATUS_SUBMITTED);
//        entity.setIs_settled(false);
        entity.setOwner_id(in.getOwner_id());
        entity.setPlan_id(in.getId());
        entity.setPlan_detail_id(detailEntity.getId());
        entity.setConsignor_id(in.getConsignor_id());
        entity.setOwner_code(in.getOwner_code());
        entity.setConsignor_code(in.getConsignor_code());
        entity.setWarehouse_id(detailEntity.getWarehouse_id());
        entity.setLocation_id(vo.getLocation_id());
        entity.setBin_id(vo.getBin_id());
        entity.setSku_id(detailEntity.getSku_id());
        entity.setSku_code(detailEntity.getSku_code());
//        entity.setPlan_count(detailEntity.getCount());
        entity.setPlan_weight(detailEntity.getWeight());
        entity.setPlan_volume(detailEntity.getVolume());
//        entity.setActual_count(detailEntity.getCount());
        entity.setActual_weight(detailEntity.getWeight());
        entity.setActual_volume(detailEntity.getVolume());
        entity.setAmount(BigDecimal.ZERO);
        // 转换关系
        MGoodsUnitCalcVo mGoodsUnitCalcVo = new MGoodsUnitCalcVo();
        mGoodsUnitCalcVo.setSku_id(detailEntity.getSku_id());
        mGoodsUnitCalcVo.setSrc_unit_id(detailEntity.getUnit_id());
        MGoodsUnitCalcVo goodsUnitCalcVo = imGoodsUnitCalcService.selectOne(mGoodsUnitCalcVo);
//        entity.setCalc(goodsUnitCalcVo.getCalc());
        // 转换后的单位id
//        entity.setTgt_unit_id(goodsUnitCalcVo.getTgt_unit_id());
        inService.save(entity);
        // 库存审核
        iCommonInventoryLogicService.updWmsStockByInBill(entity.getId());
        entity.setStatus(DictConstant.DICT_B_OUT_STATUS_PASSED);
//        entity.setE_opinion(DictConstant.DICT_AUDIT_INFO_TYPE_FALSE);

        // 系统自动审核
        Integer id = staffService.selectIdByStaffCode(SystemConstants.AUDIT_STAFF_CODE);
//        entity.setE_dt(LocalDateTime.now());
//        entity.setE_id(id);

        BInEntity byId = inService.getById(entity.getId());
        entity.setDbversion(byId.getDbversion());
//        entity.setInventory_account_id(null);
        inService.updateById(entity);
        // 库存计算
        iCommonInventoryLogicService.updWmsStockByInBill(entity.getId());

    }

    /**
     * 新增出库计划
     *
     * @param bWoMaterialVo
     * @param entity
     */
    private void insertOutPlan(BWoMaterialVo bWoMaterialVo, BWoEntity entity) {
        // 查询货主
        MOwnerVo mOwnerVo = ownerService.selectById(entity.getOwner_id());
        // 查询委托方
        MCustomerVo mCustomerVo = customerService.selectByCreditNo(mOwnerVo.getCredit_no());
        // 新增出库计划逻辑
        BOutPlanEntity plan = new BOutPlanEntity();
        plan.setOwner_id(entity.getOwner_id());
        plan.setRelease_order_code(entity.getDelivery_order_code());
        plan.setOwner_code(mOwnerVo.getCode());
        if (mCustomerVo != null) {
            plan.setConsignor_id(mCustomerVo.getId());
            plan.setConsignor_code(mCustomerVo.getOwner_code());
        }

        plan.setPlan_time(LocalDateTime.now());
        plan.setType(DictConstant.DICT_B_OUT_PLAN_TYPE_LL);
        // 生成出库计划单号
        String no = outPlanAutoCodeService.autoCode().getCode();
        plan.setCode(no);
        outPlanService.save(plan);
//        bWoMaterialVo.setB_out_plan_id(plan.getId());
        // 新增出库计划明细
        insertOutPlanDetail(plan, bWoMaterialVo);
        // 更新 b_out_plan_iud
        LambdaUpdateWrapper<BWoMaterialEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BWoMaterialEntity::getId, bWoMaterialVo.getId());
        updateWrapper.set(BWoMaterialEntity::getB_out_plan_id, plan.getId());
        materialService.update(updateWrapper);
    }

    private void insertOutPlanDetail(BOutPlanEntity plan, BWoMaterialVo bWoMaterialVo) {
        BOutPlanDetailEntity detailEntity = new BOutPlanDetailEntity();
        detailEntity.setPlan_id(plan.getId());
        detailEntity.setStatus(DictConstant.DICT_B_OUT_PLAN_STATUS_PASSED);
        detailEntity.setWarehouse_id(bWoMaterialVo.getWarehouse_id());
        detailEntity.setLocation_id(bWoMaterialVo.getLocation_id());
        detailEntity.setBin_id(bWoMaterialVo.getBin_id());

        detailEntity.setSku_id(bWoMaterialVo.getSku_id());
        detailEntity.setSku_code(bWoMaterialVo.getSku_code());

        detailEntity.setUnit_id(bWoMaterialVo.getUnit_id());
        MGoodsUnitCalcVo mGoodsUnitCalcVo = new MGoodsUnitCalcVo();
        mGoodsUnitCalcVo.setSrc_unit_id(bWoMaterialVo.getUnit_id());
        mGoodsUnitCalcVo.setSku_id(bWoMaterialVo.getSku_id());
        MGoodsUnitCalcVo goodsUnitCalcVo = imGoodsUnitCalcService.selectOne(mGoodsUnitCalcVo);
        if (goodsUnitCalcVo == null) {
//            detailEntity.setWeight(bWoMaterialVo.getWo_qty());
            detailEntity.setHas_handle_weight(bWoMaterialVo.getWo_qty());
        } else {
            detailEntity.setWeight(bWoMaterialVo.getWo_qty().multiply(goodsUnitCalcVo.getCalc()));
            detailEntity.setHas_handle_weight(bWoMaterialVo.getWo_qty().multiply(goodsUnitCalcVo.getCalc()));
        }

        detailEntity.setCount(bWoMaterialVo.getWo_qty());
        detailEntity.setVolume(BigDecimal.ZERO);
        detailEntity.setPending_volume(BigDecimal.ZERO);
        detailEntity.setPending_count(BigDecimal.ZERO);
        detailEntity.setHas_handle_count(bWoMaterialVo.getWo_qty());
        detailEntity.setPending_weight(BigDecimal.ZERO);
        detailEntity.setHas_handle_volume(BigDecimal.ZERO);
        // 设置序号
        detailEntity.setNo(1);
        // 明细单号
        detailEntity.setCode(bOutPlanDetailAutoCodeService.autoCode().getCode());

        // 审核人, 审核时间, 系统默认
        Integer id = staffService.selectIdByStaffCode(SystemConstants.AUDIT_STAFF_CODE);
        detailEntity.setAudit_dt(LocalDateTime.now());
        detailEntity.setAuditor_id(id);

        outPlanDetailService.save(detailEntity);

        // 待办事项
        todoService.insertTodo(detailEntity.getId(), SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL, SystemConstants.PERMS.B_OUT_PLAN_DETAIL_OPERATE);
        // 新增出库单
        insertOut(plan, detailEntity);

    }

    /**
     * 新增出库单
     *
     * @param plan
     * @param detailEntity
     */
    private void insertOut(BOutPlanEntity plan, BOutPlanDetailEntity detailEntity) {
        BOutEntity entity = new BOutEntity();
        entity.setCode(outAutoCodeService.autoCode().getCode());
        entity.setType(DictConstant.DICT_B_OUT_TYPE_LL);
        entity.setIs_settled(false);
        entity.setStatus(DictConstant.DICT_B_OUT_STATUS_SUBMITTED);
        entity.setOwner_id(plan.getOwner_id());
        entity.setOwner_code(plan.getOwner_code());
        entity.setConsignor_id(plan.getConsignor_id());
        entity.setConsignor_code(plan.getConsignor_code());
        entity.setPlan_id(plan.getId());
        entity.setPlan_detail_id(detailEntity.getId());
        entity.setOutbound_time(LocalDateTime.now());
        entity.setWarehouse_id(detailEntity.getWarehouse_id());
        entity.setLocation_id(detailEntity.getLocation_id());
        entity.setBin_id(detailEntity.getBin_id());
        entity.setSku_id(detailEntity.getSku_id());
        entity.setSku_code(detailEntity.getSku_code());
        entity.setPlan_count(detailEntity.getCount());
        entity.setPlan_weight(detailEntity.getWeight());
        entity.setPlan_volume(detailEntity.getVolume());
        entity.setActual_count(detailEntity.getCount());
        entity.setActual_weight(detailEntity.getWeight());
        entity.setActual_volume(detailEntity.getVolume());
        entity.setPrice(BigDecimal.ZERO);
        entity.setAmount(BigDecimal.ZERO);
        entity.setUnit_id(detailEntity.getUnit_id());
        // 转换关系
        MGoodsUnitCalcVo mGoodsUnitCalcVo = new MGoodsUnitCalcVo();
        mGoodsUnitCalcVo.setSku_id(detailEntity.getSku_id());
        mGoodsUnitCalcVo.setSrc_unit_id(detailEntity.getUnit_id());
        MGoodsUnitCalcVo goodsUnitCalcVo = imGoodsUnitCalcService.selectOne(mGoodsUnitCalcVo);
        entity.setCalc(goodsUnitCalcVo.getCalc());
        // 转换后的单位id
        entity.setTgt_unit_id(goodsUnitCalcVo.getTgt_unit_id());
        outService.save(entity);
        BOutExtraEntity extraEntity = new BOutExtraEntity();
        extraEntity.setOut_id(entity.getId());
        bOutExtraMapper.insert(extraEntity);
        // 计算库存
        // 库存计算
        iCommonInventoryLogicService.updWmsStockByOutBill(entity.getId());
        entity.setStatus(DictConstant.DICT_B_OUT_STATUS_PASSED);
        entity.setE_opinion(DictConstant.DICT_AUDIT_INFO_TYPE_FALSE);
//        entity.setE_dt(LocalDateTime.now());
//        entity.setE_id(SecurityUtil.getStaff_id().intValue());
        BOutEntity byId = outService.getById(entity.getId());
        entity.setDbversion(byId.getDbversion());
        entity.setInventory_account_id(null);

        // 系统自动审核
        Integer id = staffService.selectIdByStaffCode(SystemConstants.AUDIT_STAFF_CODE);
        entity.setE_dt(LocalDateTime.now());
        entity.setE_id(id);
        outService.updateById(entity);
        // 库存计算
        iCommonInventoryLogicService.updWmsStockByOutBill(entity.getId());
    }

    /**
     * 库存校验, 逻辑改为新增保存时校验
     * @param materialList
     * @param owner_id
     */
    private void checkInventory(List<BWoMaterialVo> materialList, Integer owner_id) {
        for (BWoMaterialVo bWoMaterialVo : materialList) {
            checkInventory(bWoMaterialVo, owner_id);

        }
    }

    private MInventoryVo checkInventory(BWoMaterialVo materialVo, Integer owner_id) {
        MInventoryVo mInventoryVo = new MInventoryVo();
        mInventoryVo.setOwner_id(owner_id);
        mInventoryVo.setWarehouse_id(materialVo.getWarehouse_id());
        mInventoryVo.setSku_id(materialVo.getSku_id());
        // 查询库存
        MInventoryVo inventoryInfo = inventoryService.getInventoryInfo(mInventoryVo);
        if (inventoryInfo == null) {
            // 查询仓库名
            throw new BusinessException(String.format("【%s】仓库，【%s】商品库存不足。当前库存【%s】吨，生产所需【%s】吨", materialVo.getWarehouse_name(), materialVo.getGoods_name(), 0, materialVo.getWo_qty()));
        }
        if (inventoryInfo.getQty_avaible().compareTo(materialVo.getWo_qty()) < 0) {
            throw new BusinessException(String.format("【%s】仓库，【%s】商品库存不足。当前库存【%s】吨，生产所需【%s】吨", materialVo.getWarehouse_name(), materialVo.getGoods_name(), inventoryInfo.getQty_avaible(), materialVo.getWo_qty()));
        }
        return inventoryInfo;
    }

    /**
     * @param entity            实体类
     * @param delivery_order_id 放货指令 id
     */
    private void updateEntity(BWoEntity entity, Integer delivery_order_id, String delivery_sku_code) {
        // 查询订单信息
        BReleaseOrderVo releaseOrderVo = orderService.get(new BReleaseOrderVo(delivery_order_id));
        // 根据仓库编码查询仓库
        List<BReleaseOrderDetailVo> detailList = releaseOrderVo.getDetailList();
        Assert.notEmpty(detailList, "订单数据异常");
        BReleaseOrderDetailVo orderDetailVo = null;
        // 选定商品
        for (BReleaseOrderDetailVo bReleaseOrderDetailVo : detailList) {
            if (delivery_sku_code.equals(bReleaseOrderDetailVo.getCommodity_spec_code())) {
                orderDetailVo = bReleaseOrderDetailVo;
            }
        }
        if (orderDetailVo == null) {
            throw new BusinessException("选择订单商品失败!");
        }

        // 查询仓库三大件
        List<MBLWBo> mblwBos = warehouseService.selectBLWByCode(orderDetailVo.getWarehouse_code());
        if (CollectionUtils.isEmpty(mblwBos)) {
            throw new BusinessException("仓库数据异常");
        }
        MBLWBo mblwBo = mblwBos.get(0);
        entity.setWc_warehouse_id(mblwBo.getWarehouse_id());
        entity.setWc_warehouse_code(mblwBo.getWarehouse_code());
        entity.setWc_location_id(mblwBo.getLocation_id());
        entity.setWc_bin_id(mblwBo.getBin_id());
        entity.setWc_location_code(mblwBo.getLocation_code());
        entity.setWc_bin_code(mblwBo.getBin_code());
        // 添加订单信息
        entity.setDelivery_order_id(delivery_order_id);
        entity.setDelivery_order_code(releaseOrderVo.getCode());
        entity.setDelivery_order_detail_id(orderDetailVo.getId());
        entity.setDelivery_order_detail_no(orderDetailVo.getNo());
        entity.setDelivery_order_detail_qty(orderDetailVo.getQty());
        entity.setDelivery_order_detail_sku_code(orderDetailVo.getCommodity_spec_code());
    }

    /**
     * 检验 状态
     *
     * @param entity     实体类
     * @param moduleType 校验类型
     */
    private void checkLogic(BWoEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新, 制单状态, 审核驳回状态
                if (!DictConstant.DICT_B_WO_STATUS_1.equals(entity.getStatus()) && !DictConstant.DICT_B_WO_STATUS_4.equals(entity.getStatus())) {
                    throw new BusinessException("修改失败, 当前状态无法修改");
                }
                break;
            case CheckResultAo.SUBMIT_CHECK_TYPE:
                // 更新, 制单状态
                if (DictConstant.DICT_B_WO_STATUS_2.equals(entity.getStatus()) || DictConstant.DICT_B_WO_STATUS_3.equals(entity.getStatus()) || DictConstant.DICT_B_WO_STATUS_5.equals(entity.getStatus())) {
                    throw new BusinessException(entity.getCode() + ": 提交失败, 当前状态无法提交");
                }
                break;
            case CheckResultAo.CANCEL_CHECK_TYPE:
                // 作废, 不能重复作废
                if (DictConstant.DICT_B_WO_STATUS_5.equals(entity.getStatus())) {
                    throw new BusinessException("不可重复作废");
                }
                break;
            case CheckResultAo.AUDIT_CHECK_TYPE:
                // 作废, 不能重复作废
                if (!DictConstant.DICT_B_WO_STATUS_2.equals(entity.getStatus())) {
                    throw new BusinessException(entity.getCode() + ": 未提交, 无法审核!");
                }
                break;
            case CheckResultAo.REJECT_CHECK_TYPE:
                if (!DictConstant.DICT_B_WO_STATUS_2.equals(entity.getStatus())) {
                    throw new BusinessException(entity.getCode() + ": 未提交, 审核驳回失败!");
                }
            default:
                break;
        }
    }

    /**
     * 校验产成品, 副产品产量 == 原材料消耗数量
     *
     * @param material_list 原材料列表
     * @param product_list  副产品列表
     * @param result        错误信息, 键值 error_msg
     */
    private void checkIsEquals(List<BWoMaterialVo> material_list, List<BWoProductVo> product_list, List<Map<String, String>> result) {
        BigDecimal materialQty = material_list.stream().map(BWoMaterialVo::getWo_qty).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal productQty = product_list.stream().map(BWoProductVo::getWo_qty).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (materialQty.setScale(4, RoundingMode.HALF_UP).compareTo(productQty.setScale(4, RoundingMode.HALF_UP)) != 0 || materialQty.compareTo(BigDecimal.ZERO) == 0 || productQty.compareTo(BigDecimal.ZERO) == 0) {
            Map<String, String> msg = new HashMap<>();
            msg.put("error_msg", "原材料消耗数量需等于产成品、副产品产量");
            result.add(msg);
        }
    }

}
