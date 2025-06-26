package com.xinyirun.scm.core.system.serviceimpl.business.pp;

import cn.hutool.json.JSONObject;
import cn.idev.excel.EasyExcel;
import cn.idev.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.pp.BPpEntity;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BReleaseOrderDetailEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.bo.inventory.warehouse.MBLWBo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.pp.BPpExportVo;
import com.xinyirun.scm.bean.system.vo.business.pp.BPpMaterialVo;
import com.xinyirun.scm.bean.system.vo.business.pp.BPpProductVo;
import com.xinyirun.scm.bean.system.vo.business.pp.BPpVo;
import com.xinyirun.scm.bean.system.vo.business.releaseorder.BReleaseOrderDetailVo;
import com.xinyirun.scm.bean.system.vo.business.releaseorder.BReleaseOrderVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoExportUtilVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoExportVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoMaterialVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoProductVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitCalcVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.pp.BPpMapper;
import com.xinyirun.scm.core.system.service.business.pp.IBPpMaterialService;
import com.xinyirun.scm.core.system.service.business.pp.IBPpProductService;
import com.xinyirun.scm.core.system.service.business.pp.IBPpService;
import com.xinyirun.scm.core.system.service.business.releaseorder.IBReleaseOrderDetailService;
import com.xinyirun.scm.core.system.service.business.releaseorder.IBReleaseOrderService;
import com.xinyirun.scm.core.system.service.business.wo.IBWoProductService;
import com.xinyirun.scm.core.system.service.business.wo.IBWoService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.master.warehouse.IMWarehouseService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.business.todo.TodoService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BPpAutoCodeServiceImpl;
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
 * 生产计划表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-18
 */
@Service
public class BPpServiceImpl extends ServiceImpl<BPpMapper, BPpEntity> implements IBPpService {

    @Autowired
    private BPpMapper bPpMapper;

    @Autowired
    private IBWoService ibWoService;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private IBReleaseOrderService orderService;

    @Autowired
    private BPpAutoCodeServiceImpl autoCodeService;

    @Autowired
    private IBPpMaterialService ibPpMaterialService;

    @Autowired
    private IBPpProductService ibPpProductService;

    @Autowired
    private IMWarehouseService warehouseService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private IBReleaseOrderDetailService releaseOrderDetailService;

    @Autowired
    @Lazy
    private IBWoProductService productService;

    @Autowired
    private MCancelService cancelService;


    /**
     * 分页查询
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t9.warehouse_id")
    public IPage<BPpVo> selectPageList(BPpVo bPpVo) {
        // 分页条件
        Page<BPpVo> pageCondition = new Page(bPpVo.getPageCondition().getCurrent(), bPpVo.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, bPpVo.getPageCondition().getSort());

        bPpVo.setStaff_id(SecurityUtil.getStaff_id());

        IPage<BPpVo> pagelist = bPpMapper.selectPageList(bPpVo, pageCondition);
        return pagelist;
    }

    /**
     * 新增生产计划
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BPpVo> insert(BPpVo bPpVo) {
        // 1.数据校验
        checkBppInsert(bPpVo);
        // 2.单位管理
        checkGoodsUnit(bPpVo);
        // 3.公式数据
        List<Map<String, String>> error_msg = check(bPpVo);
        if (error_msg.size() != 0) {
            throw new BusinessException(error_msg.get(0).get("error_msg"));
        }

        //4.组装数据
        BPpEntity bPpEntity = new BPpEntity();

        //订单信息
        if (null != bPpVo.getRelease_order_id()) {
            updateEntity(bPpEntity, bPpVo.getRelease_order_id(), bPpVo.getRelease_sku_code());
        }

        // 货主信息
        bPpEntity.setOwner_id(bPpVo.getOwner_id());
        bPpEntity.setOwner_code(bPpVo.getOwner_code());
        // 生成工单编号
        bPpEntity.setCode(autoCodeService.autoCode().getCode());

        //配方信息
        bPpEntity.setRouter_id(bPpVo.getRouter_id());
        bPpEntity.setRouter_code(bPpVo.getRouter_code());
        // 默认制单状态
        bPpEntity.setStatus(DictConstant.DICT_B_PP_STATUS_SAVED);
        bPpEntity.setPre_status(DictConstant.DICT_B_PP_STATUS_SAVED);

//        //仓库信息
//        bPpEntity.setWarehouse_id(bPpVo.getWarehouse_id());
//        bPpEntity.setWarehouse_code(bPpVo.getWarehouse_code());

        bPpEntity.setPlan_time(bPpVo.getPlan_time());
        bPpEntity.setPlan_end_time(bPpVo.getPlan_end_time());
        int insertResult = bPpMapper.insert(bPpEntity);

        // 新增生产管理产成品, 副产品
        ibPpProductService.insertAll(bPpVo.getProduct_list(), bPpEntity.getId());
        // 新增原材料
        ibPpMaterialService.insertAll(bPpVo.getMaterial_list(), bPpEntity.getId());

        List<BPpProductVo> productVoList = ibPpProductService.selectByWoId(bPpEntity.getId());
        List<BPpMaterialVo> materialVoList = ibPpMaterialService.selectByWoId(bPpEntity.getId());
        bPpEntity.setJson_product_list(JSON.toJSONString(productVoList));
        bPpEntity.setJson_material_list(JSON.toJSONString(materialVoList));
        bPpMapper.updateById(bPpEntity);

        // 添加待办
        todoService.insertTodo(bPpEntity.getId(), SystemConstants.SERIAL_TYPE.B_PP, SystemConstants.PERMS.B_PP_AUDIT);

        // 根据ID 查询, 返回
        return InsertResultUtil.OK(getDetail(bPpEntity.getId()));
    }


    /**
     * @param bPpEntity         实体类
     * @param delivery_order_id 放货指令 id
     */
    private void updateEntity(BPpEntity bPpEntity, Integer delivery_order_id, String release_sku_code) {
        // 查询订单信息
        BReleaseOrderVo releaseOrderVo = orderService.get(new BReleaseOrderVo(delivery_order_id));
        // 根据仓库编码查询仓库
        List<BReleaseOrderDetailVo> detailList = releaseOrderVo.getDetailList();
        Assert.notEmpty(detailList, "订单数据异常");
        BReleaseOrderDetailVo orderDetailVo = null;
        // 选定商品
        for (BReleaseOrderDetailVo bReleaseOrderDetailVo : detailList) {
            if (release_sku_code.equals(bReleaseOrderDetailVo.getCommodity_spec_code())) {
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
        bPpEntity.setWarehouse_id(mblwBo.getWarehouse_id());
        bPpEntity.setWarehouse_code(mblwBo.getWarehouse_code());
        bPpEntity.setLocation_id(mblwBo.getLocation_id());
        bPpEntity.setLocation_code(mblwBo.getLocation_code());
        bPpEntity.setBin_id(mblwBo.getBin_id());
        bPpEntity.setBin_code(mblwBo.getBin_code());
        // 添加订单信息
        bPpEntity.setRelease_order_id(delivery_order_id);
        bPpEntity.setRelease_order_code(releaseOrderVo.getCode());
        bPpEntity.setRelease_order_detail_id(orderDetailVo.getId());

    }


    /**
     * 新增计划表数据校验
     */
    private void checkBppInsert(BPpVo bPpVo) {
        //1.提交信息校验
        for (BPpMaterialVo bWoMaterialVo : bPpVo.getMaterial_list()) {
            if (StringUtils.isEmpty(bWoMaterialVo.getWarehouse_code()))
                throw new BusinessException("原材料未选择仓库");
            if (Objects.isNull(bWoMaterialVo.getSku_id()))
                throw new BusinessException("原材料规格不能为空");
        }
        for (BPpProductVo bWoProductVo : bPpVo.getProduct_list()) {
            if (Objects.isNull(bWoProductVo.getSku_id()))
                throw new BusinessException("产成品或副产品没有选择规格");
            if (StringUtils.isEmpty(bWoProductVo.getWarehouse_code()))
                throw new BusinessException("产成品, 副产品未选择仓库");
            if (Objects.isNull(bWoProductVo.getQty()))
                throw new BusinessException("产成品, 副产品未输入生产入库数量");
            // 校验产成品生产入库数量是否大于订单数量
            if (DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C.equals(bWoProductVo.getType())) {
                // 产成品不能为空
                if (bWoProductVo.getQty().compareTo(BigDecimal.ZERO) == 0) {
                    throw new BusinessException("产成品生产入库数量不能为0");
                }
                // 如果没有选择放货指令, 不进行此判断
                if (null != bPpVo.getRelease_order_detail_id()) {
                    checkHasProductNum(bWoProductVo, bPpVo.getRelease_order_detail_id());
                }
            }
        }
    }


    /**
     * 单位管理
     * 1. 商品存在单位, 但是没有选择, 默认选择为 吨
     * 2. 商品没有单位换算单位, 默认新增单位换算关系, 吨:吨
     */
    private void checkGoodsUnit(BPpVo bPpVo) {
        // 原材料
        for (BPpMaterialVo bWoMaterialVo : bPpVo.getMaterial_list()) {
            if (null == bWoMaterialVo.getUnit_id()) {
                // 查询单位换算关系
                List<MGoodsUnitCalcVo> goodsUnitCalcVoList = ibWoService.getGoodsUnitCalc(bWoMaterialVo.getSku_id());
                if (CollectionUtils.isEmpty(goodsUnitCalcVoList)) {
                    // 为空, 新增默认关系
                    MGoodsUnitCalcVo calcVo = ibWoService.insertGoodsUnitCalc(bWoMaterialVo.getSku_id());
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
        for (BPpProductVo bWoProductVo : bPpVo.getProduct_list()) {
            if (null == bWoProductVo.getUnit_id()) {
                // 查询单位换算关系
                List<MGoodsUnitCalcVo> goodsUnitCalcVoList = ibWoService.getGoodsUnitCalc(bWoProductVo.getSku_id());
                if (CollectionUtils.isEmpty(goodsUnitCalcVoList)) {
                    // 为空, 新增默认关系
                    MGoodsUnitCalcVo calcVo = ibWoService.insertGoodsUnitCalc(bWoProductVo.getSku_id());
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
     * 公式校验, 键值 error_msg
     *
     * @return
     */
    public List<Map<String, String>> check(BPpVo bPpVo) {
        List<Map<String, String>> result = new LinkedList<>();

        checkQty(bPpVo);

        // 校验 产成品, 副产品配比是不是 100%
        BigDecimal bPpProductRouter = bPpVo.getProduct_list().stream().map(BPpProductVo::getPp_router).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (bPpProductRouter.compareTo(new BigDecimal(100)) != 0) {
            Map<String, String> msg = new HashMap<>();
            msg.put("error_msg", "产成品、副产品配比之和不是100%");
            result.add(msg);
        }

        // 校验 原材料 配比是不是 100%
        BigDecimal bPpMaterialRouter = bPpVo.getMaterial_list().stream().map(BPpMaterialVo::getPp_router).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (bPpMaterialRouter.compareTo(new BigDecimal(100)) != 0) {
            Map<String, String> msg = new HashMap<>();
            msg.put("error_msg", "原材料配比之和不是 100%");
            result.add(msg);
        }
        // 校验产成品, 副产品产量 == 原材料消耗数量
        BigDecimal materialQty = bPpVo.getMaterial_list().stream().map(BPpMaterialVo::getQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal productQty = bPpVo.getProduct_list().stream().map(BPpProductVo::getQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (materialQty.setScale(4, RoundingMode.HALF_UP).compareTo(productQty.setScale(4, RoundingMode.HALF_UP)) != 0 || materialQty.compareTo(BigDecimal.ZERO) == 0 || productQty.compareTo(BigDecimal.ZERO) == 0) {
            Map<String, String> msg = new HashMap<>();
            msg.put("error_msg", "原材料消耗数量需等于产成品、副产品产量");
            result.add(msg);
        }
        return result;
    }

    /**
     * 计算
     *
     * @param param
     * @return
     */
    @Override
    public BPpVo checkQty(BPpVo param) {
        // 计算总数
        BigDecimal productAllNum = BigDecimal.ZERO;
        BigDecimal productAllNumWo = BigDecimal.ZERO;
        for (BPpProductVo bWoProductVo : param.getProduct_list()) {
            if (DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C.equals(bWoProductVo.getType())) {
                // 如果配比是0, 会报错
                if (BigDecimal.ZERO.compareTo(bWoProductVo.getPp_router()) != 0) {
                    // 计算总产量
                    productAllNum = bWoProductVo.getQty().divide(bWoProductVo.getPp_router().divide(BigDecimal.valueOf(100)), 4, RoundingMode.HALF_UP);
                    productAllNumWo = bWoProductVo.getWo_qty().divide(bWoProductVo.getPp_router().divide(BigDecimal.valueOf(100)), 4, RoundingMode.HALF_UP);
                }
            }
        }
        // 计算副产品数量
        for (BPpProductVo bWoProductVo : param.getProduct_list()) {
            if (DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_F.equals(bWoProductVo.getType())) {
                // 计算总产量
                BigDecimal decimal = productAllNum.multiply(bWoProductVo.getPp_router().divide(BigDecimal.valueOf(100)));
                bWoProductVo.setQty(decimal);

                BigDecimal decimalWo = productAllNumWo.multiply(bWoProductVo.getPp_router().divide(BigDecimal.valueOf(100)));
                bWoProductVo.setWo_qty(decimalWo);
            }
        }
        // 计算原材料的数量
        for (BPpMaterialVo bWoMaterialVo : param.getMaterial_list()) {
            // 计算总产量
            BigDecimal decimal = productAllNum.multiply(bWoMaterialVo.getPp_router().divide(BigDecimal.valueOf(100)));
            bWoMaterialVo.setQty(decimal);

            // 计算总产量
            BigDecimal decimalWo = productAllNumWo.multiply(bWoMaterialVo.getPp_router().divide(BigDecimal.valueOf(100)));
            bWoMaterialVo.setWo_qty(decimalWo);
        }
        // 计算总量
        BigDecimal productReduce = param.getProduct_list().stream().map(BPpProductVo::getQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal materialReduce = param.getMaterial_list().stream().map(BPpMaterialVo::getQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        param.setProduct_actual(productReduce);
        param.setMaterial_actual(materialReduce);

        // 计算总量
        BigDecimal productReduce_wo = param.getProduct_list().stream().map(BPpProductVo::getWo_qty).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal materialReduce_wo = param.getMaterial_list().stream().map(BPpMaterialVo::getWo_qty).reduce(BigDecimal.ZERO, BigDecimal::add);
        param.setProduct_actual_wo(productReduce_wo);
        param.setMaterial_actual_wo(materialReduce_wo);
        return param;
    }

    /**
     * 状态修改已提交
     *
     * @param bPpVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(List<BPpVo> bPpVo) {

        for (BPpVo bPpVo1 : bPpVo) {
            BPpEntity entity = bPpMapper.selectOne(new LambdaQueryWrapper<BPpEntity>().eq(BPpEntity::getId, bPpVo1.getId()));
            // 查询产成品, 校验产成品数量
            List<BPpProductVo> productVoList = ibPpProductService.selectByWoId(entity.getId());
            for (BPpProductVo bPpProductVo : productVoList) {
                if (entity.getRelease_order_detail_id() != null && DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C.equals(bPpProductVo.getType())) {
                    // 产成品检验数量
                    checkHasProductNum(bPpProductVo, entity.getRelease_order_detail_id());
                }
            }

            //检验状态
            checkLogic(entity, CheckResultAo.SUBMIT_CHECK_TYPE);

            // 判断 提交之前的状态,
            if (DictConstant.DICT_B_PP_STATUS_SAVED.equals(entity.getStatus())) {
                // 生成已办,兼容 未提交的
                todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_PP, SystemConstants.PERMS.B_PP_SUBMIT);
            } else if (DictConstant.DICT_B_PP_STATUS_RETURN.equals(entity.getStatus())) {
                // 生成已办, 改了之后,只有审核驳回的可以重新提交
                todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_PP, SystemConstants.PERMS.B_PP_REJECT);
            }
            // 添加待办
            todoService.insertTodo(entity.getId(), SystemConstants.SERIAL_TYPE.B_PP, SystemConstants.PERMS.B_PP_AUDIT);

            entity.setStatus(DictConstant.DICT_B_PP_STATUS_SUBMITTED);
            bPpMapper.updateById(entity);
        }
    }

    /**
     * 状态修改 作废
     *
     * @param bPpVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(BPpVo bPpVo) {
        BPpEntity entity = bPpMapper.selectOne(new LambdaQueryWrapper<BPpEntity>().eq(BPpEntity::getId, bPpVo.getId()));
        // 校验状态
        checkLogic(entity, CheckResultAo.CANCEL_CHECK_TYPE);

        entity.setPre_status(entity.getStatus());
        entity.setStatus(DictConstant.DICT_B_PP_STATUS_CANCEL);
        bPpMapper.updateById(entity);

        // 作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(entity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_PP);
        mCancelVo.setRemark(bPpVo.getRemark());
        cancelService.insert(mCancelVo);

        // 生成已办
        todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_PP, SystemConstants.PERMS.B_PP_CANCEL);
    }

    /**
     * 状态修改 审核通过
     *
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(List<BPpVo> param) {

        // 校验状态, 只有已提交可以审核通过
        for (BPpVo bPpVo : param) {
            BPpEntity entity = bPpMapper.selectOne(new LambdaQueryWrapper<BPpEntity>().eq(BPpEntity::getId, bPpVo.getId()));
            checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE);

            // 更新状态
            entity.setPre_status(entity.getStatus());
            entity.setStatus(DictConstant.DICT_B_PP_STATUS_PASSED);
            entity.setAudit_id(SecurityUtil.getStaff_id().intValue());
            entity.setAudit_time(LocalDateTime.now());
            bPpMapper.updateById(entity);

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_PP, SystemConstants.PERMS.B_PP_AUDIT);
        }
    }

    /**
     * 状态修改 驳回
     *
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(List<BPpVo> param) {
        // 校验状态, 只有已提交可以审核通过
        for (BPpVo bPpVo : param) {
            BPpEntity entity = bPpMapper.selectOne(new LambdaQueryWrapper<BPpEntity>().eq(BPpEntity::getId, bPpVo.getId()));
            checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE);

            // 更新状态
            entity.setPre_status(entity.getStatus());
            entity.setStatus(DictConstant.DICT_B_PP_STATUS_RETURN);
            entity.setAudit_id(SecurityUtil.getStaff_id().intValue());
            entity.setAudit_time(LocalDateTime.now());
            bPpMapper.updateById(entity);

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_PP, SystemConstants.PERMS.B_PP_REJECT);
        }
    }

    /**
     * 修改状态已完成
     *
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finish(List<BPpVo> param) {
        // 校验状态, 只有已提交可以审核通过
        for (BPpVo bPpVo : param) {
            BPpEntity entity = bPpMapper.selectOne(new LambdaQueryWrapper<BPpEntity>().eq(BPpEntity::getId, bPpVo.getId()));
            checkLogic(entity, CheckResultAo.FINISH_CHECK_TYPE);

            // 更新状态
            entity.setPre_status(entity.getStatus());
            entity.setStatus(DictConstant.DICT_B_PP_STATUS_FINISH);
            bPpMapper.updateById(entity);

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_PP, SystemConstants.PERMS.B_PP_FINISH);
        }
    }

    /**
     * 获取生产计划详情
     *
     * @param id
     */
    @Override
    public BPpVo getDetail(Integer id) {
        BPpVo bPpVo = bPpMapper.selectById(id);

        List<BPpProductVo> productVoList = ibPpProductService.selectByWoId(bPpVo.getId());
        bPpVo.setProduct_list(productVoList);

        List<BPpMaterialVo> materialVoList = ibPpMaterialService.selectByWoId(bPpVo.getId());
        bPpVo.setMaterial_list(materialVoList);

        return bPpVo;
    }

    /**
     * 修改生产计划
     *
     * @param bPpVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BPpVo> updateParam(BPpVo bPpVo) {
        Integer id = bPpVo.getId();
        Assert.notNull(id, "ID 不能为空");

        // 1.数据校验
        checkBppInsert(bPpVo);

        // 2.单位管理
        checkGoodsUnit(bPpVo);
        // 3.公式数据
        List<Map<String, String>> error_msg = check(bPpVo);
        if (error_msg.size() != 0) {
            throw new BusinessException(error_msg.get(0).get("error_msg"));
        }

        //4.组装数据
        BPpEntity bPpEntity = bPpMapper.selectOne(new LambdaQueryWrapper<BPpEntity>().eq(BPpEntity::getId, id));

        //订单信息
        if (null != bPpVo.getRelease_order_id() && !bPpVo.getRelease_order_id().equals(bPpEntity.getRelease_order_id())) {
            updateEntity(bPpEntity, bPpVo.getRelease_order_id(), bPpVo.getRelease_sku_code());
        }

        // 货主信息
        bPpEntity.setOwner_id(bPpVo.getOwner_id());
        bPpEntity.setOwner_code(bPpVo.getOwner_code());

        //配方信息
        bPpEntity.setRouter_id(bPpVo.getRouter_id());
        bPpEntity.setRouter_code(bPpVo.getRouter_code());

        bPpEntity.setPlan_time(bPpVo.getPlan_time());
        bPpEntity.setPlan_end_time(bPpVo.getPlan_end_time());

        // 如果是 审核驳回 状态, 状态更新为 提交
        if (DictConstant.DICT_B_PP_STATUS_RETURN.equals(bPpEntity.getStatus())) {
            bPpEntity.setPre_status(bPpEntity.getStatus());
            bPpEntity.setStatus(DictConstant.DICT_B_PP_STATUS_SUBMITTED);
            // 生成已办, 改了之后,只有审核驳回的可以重新提交
            todoService.insertAlreadyDo(bPpEntity.getId(), SystemConstants.SERIAL_TYPE.B_PP, SystemConstants.PERMS.B_PP_REJECT);
        } else {
            // 生成已办, 改了之后,只有审核驳回的可以重新提交
            todoService.insertAlreadyDo(bPpEntity.getId(), SystemConstants.SERIAL_TYPE.B_PP, SystemConstants.PERMS.B_PP_SUBMIT);
        }

        // 添加待办
        todoService.insertTodo(bPpEntity.getId(), SystemConstants.SERIAL_TYPE.B_PP, SystemConstants.PERMS.B_PP_AUDIT);

//        int insertResult = bPpMapper.updateById(bPpEntity);
//        if (insertResult == 0) {
//            throw new UpdateErrorException("更新失败");
//        }

        // 全删，全增 生产管理产成品, 副产品
        ibPpProductService.deleteByPpId(id);
        ibPpProductService.insertAll(bPpVo.getProduct_list(), bPpEntity.getId());
        List<BPpProductVo> productVoList = ibPpProductService.selectByWoId(bPpEntity.getId());
        bPpEntity.setJson_product_list(JSON.toJSONString(productVoList));

        // 全删，全增 新增原材料
        ibPpMaterialService.deleteByPpId(id);
        ibPpMaterialService.insertAll(bPpVo.getMaterial_list(), bPpEntity.getId());
        List<BPpMaterialVo> materialVoList = ibPpMaterialService.selectByWoId(bPpEntity.getId());
        bPpEntity.setJson_material_list(JSON.toJSONString(materialVoList));

        bPpMapper.updateById(bPpEntity);

        return UpdateResultUtil.OK(getDetail(bPpEntity.getId()));
    }

    /**
     * 查询待办数量
     *
     * @param bPpVo
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t9.warehouse_id")
    public Integer selectTodoCount(BPpVo bPpVo) {
        bPpVo.setStaff_id(SecurityUtil.getStaff_id());
        return bPpMapper.selectTodoCount(bPpVo);
    }

    /**
     * 查询统计数量
     *
     * @param param
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t3.warehouse_id")
    public BPpVo selectListSum(BPpVo param) {
        param.setStaff_id(SecurityUtil.getStaff_id());
        return bPpMapper.selectListSum(param);
    }

    /**
     * 导出
     *
     * @param bPpVo
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t9.warehouse_id")
    public List<BPpVo> exportList(BPpVo bPpVo) {
        // 导出条数限制控制
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (Objects.isNull(bPpVo.getIds()) && !Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            Long count = bPpMapper.selectExportCount(bPpVo);

            if (count != null && count > Long.parseLong(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
       return bPpMapper.exportList(bPpVo);

    }

    /**
     * 关联放货订单的情况下
     * 新增, 提交, 修改时校验 改商品的生产数量 是否 大于对应的已生产数量(生产配方 和 配比 的已提交 和 已审核的)
     *
     * @param bPpProductVo
     * @param release_order_detail_id
     */
    private void checkHasProductNum(BPpProductVo bPpProductVo, Integer release_order_detail_id) {

        // 查询订单
        BReleaseOrderDetailEntity detail = releaseOrderDetailService.getById(release_order_detail_id);

        // 查询当前订单关联的已生产数量
        BigDecimal hasProductNum = productService.selectHasProductNum(null, release_order_detail_id);
        if ((hasProductNum.add(bPpProductVo.getQty()).compareTo(detail.getQty()) > 0)) {
            BigDecimal subtract = detail.getQty().subtract(hasProductNum);
            throw new BusinessException(String.format("商品%s生产数量已超过放货指令最大生产数量，本次最大生产数量为%s"
                    , bPpProductVo.getGoods_name(), subtract));
        }

    }

    /**
     * 检验 状态
     *
     * @param entity     实体类
     * @param moduleType 校验类型
     */
    private void checkLogic(BPpEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新, 制单状态, 审核驳回状态
                if (!DictConstant.DICT_B_PP_STATUS_SAVED.equals(entity.getStatus()) && !DictConstant.DICT_B_PP_STATUS_RETURN.equals(entity.getStatus())) {
                    throw new BusinessException("修改失败, 当前状态无法修改");
                }
                break;
            case CheckResultAo.SUBMIT_CHECK_TYPE:
                // 更新, 制单状态
                if (!DictConstant.DICT_B_PP_STATUS_SAVED.equals(entity.getStatus()) && !DictConstant.DICT_B_PP_STATUS_RETURN.equals(entity.getStatus())) {
                    throw new BusinessException(entity.getCode() + ": 提交失败, 当前状态无法提交");
                }
                break;
            case CheckResultAo.CANCEL_CHECK_TYPE:
                // 作废, 不能重复作废
                if (DictConstant.DICT_B_PP_STATUS_CANCEL.equals(entity.getStatus())) {
                    throw new BusinessException("不可重复作废");
                }
                break;
            case CheckResultAo.AUDIT_CHECK_TYPE:
                // 作废, 不能重复作废
                if (!DictConstant.DICT_B_PP_STATUS_SUBMITTED.equals(entity.getStatus())) {
                    throw new BusinessException(entity.getCode() + ": 未提交, 无法审核!");
                }
                break;
            case CheckResultAo.REJECT_CHECK_TYPE:
                if (!DictConstant.DICT_B_PP_STATUS_SUBMITTED.equals(entity.getStatus())) {
                    throw new BusinessException(entity.getCode() + ": 未提交, 审核驳回失败!");
                }
            case CheckResultAo.FINISH_CHECK_TYPE:
                if (!DictConstant.DICT_B_PP_STATUS_PASSED.equals(entity.getStatus())) {
                    throw new BusinessException(entity.getCode() + ": 完成失败, 当前状态无法完成!");
                }
            default:
                break;
        }
    }

}
