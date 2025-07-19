package com.xinyirun.scm.core.api.serviceimpl.business.v1.in;

import com.xinyirun.scm.bean.api.vo.business.in.ApiBInOrderGoodsVo;
import com.xinyirun.scm.bean.api.vo.business.in.ApiBInOrderVo;
import com.xinyirun.scm.bean.api.vo.business.in.ApiBWkPoDetailVo;
import com.xinyirun.scm.bean.api.vo.business.in.ApiBWkPoVo;
import com.xinyirun.scm.bean.api.vo.master.customer.ApiCustomerVo;
import com.xinyirun.scm.bean.entity.busniess.wms.in.order.BInOrderEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.in.order.temp.BWkPoDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.in.order.temp.BWkPoEntity;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.entity.master.customer.MOwnerEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MBusinessTypeVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.common.exception.api.ApiBusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.api.service.business.v1.in.ApiIBInOrderGoodsService;
import com.xinyirun.scm.core.api.service.business.v1.in.ApiIBInOrderService;
import com.xinyirun.scm.core.system.mapper.business.wms.in.order.BInOrderMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.in.order.temp.BWkPoMapper;
import com.xinyirun.scm.core.system.mapper.master.customer.MCustomerMapper;
import com.xinyirun.scm.core.system.mapper.master.customer.MOwnerMapper;
import com.xinyirun.scm.core.system.mapper.master.goods.MBusinessTypeMapper;
import com.xinyirun.scm.core.system.mapper.master.goods.MGoodsSpecMapper;
import com.xinyirun.scm.core.system.mapper.master.goods.unit.MUnitMapper;
import com.xinyirun.scm.core.system.service.business.order.IBOrderService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 入库订单 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-11-02
 */
@Service
@Slf4j
public class ApiIBInOrderServiceImpl extends BaseServiceImpl<BInOrderMapper, BInOrderEntity> implements ApiIBInOrderService {

    @Autowired
    private BInOrderMapper mapper;

    @Autowired
    private ApiIBInOrderGoodsService ibInOrderGoodsService;

    @Autowired
    private MCustomerMapper mCustomerMapper;

    @Autowired
    private MOwnerMapper mOwnerMapper;

    @Autowired
    private MBusinessTypeMapper mBusinessTypeMapper;

    @Autowired
    private MUnitMapper mUnitMapper;

    @Autowired
    private MGoodsSpecMapper mGoodsSpecMapper;

    @Autowired
    private IBOrderService ibOrderService;

//    @Autowired
//    private IBWkPoService ibWkPoService;
//
//    @Autowired
//    private IBWkPoDetailService ibWkPoDetailService;

    @Autowired
    private BWkPoMapper bWkPoMapper;

    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(ApiBInOrderVo vo) {

        // 插入逻辑保存
        BInOrderEntity entity = (BInOrderEntity) BeanUtilsSupport.copyProperties(vo, BInOrderEntity.class);
        vo.setId(entity.getId());

        if (StringUtils.isNotEmpty(vo.getSupplier_credit_no())) {
            ApiCustomerVo apiCustomerVo = new ApiCustomerVo();
            apiCustomerVo.setCredit_no(vo.getSupplier_credit_no());
            MCustomerEntity supplier = mCustomerMapper.selectByCustomerCreditNo(apiCustomerVo);
            if (supplier == null) {
                throw new ApiBusinessException(vo.getSupplier_credit_no()+":供应商未同步");
            }
            entity.setSupplier_id(supplier.getId());
            entity.setSupplier_code(supplier.getCode());
        }

        if (StringUtils.isNotEmpty(vo.getOwner_credit_no())) {
            ApiCustomerVo apiCustomerVo = new ApiCustomerVo();
            apiCustomerVo.setCredit_no(vo.getOwner_credit_no());
            MOwnerEntity owner = mOwnerMapper.selectByOwnerCreditNo(apiCustomerVo);
            if (owner == null) {
                throw new ApiBusinessException(vo.getOwner_credit_no()+":货主未同步");
            }
            entity.setOwner_id(owner.getId());
            entity.setOwner_code(owner.getCode());
        }

        if (StringUtils.isNotEmpty(vo.getBusiness_type_code())) {
            MBusinessTypeVo businessTypeVo = mBusinessTypeMapper.selectBusinessByCode(vo.getBusiness_type_code());
            if (businessTypeVo == null) {
                throw new ApiBusinessException(vo.getBusiness_type_code()+":业务板块未同步");
            }
            entity.setBusiness_type_id(businessTypeVo.getId());
            entity.setBusiness_type_code(businessTypeVo.getCode());
        }

        int rtn = mapper.insert(entity);

        if (vo.getDetailListData() != null) {
            for (ApiBInOrderGoodsVo bInOrderGoodsVo : vo.getDetailListData()) {

                MUnitVo mUnitVo = mUnitMapper.selectByCode(bInOrderGoodsVo.getUnit_code());
                if (mUnitVo != null) {
                    bInOrderGoodsVo.setUnit_id(mUnitVo.getId());
                    bInOrderGoodsVo.setUnit_code(mUnitVo.getCode());
                    bInOrderGoodsVo.setUnit_name(mUnitVo.getName());
                }

                MGoodsSpecVo mGoodsSpecVo = mGoodsSpecMapper.selectByCode(bInOrderGoodsVo.getSku_code());
                if (mGoodsSpecVo == null) {
                    throw new ApiBusinessException(bInOrderGoodsVo.getSku_code()+":规格未同步");
                }
                bInOrderGoodsVo.setSku_id(mGoodsSpecVo.getId());
                bInOrderGoodsVo.setSku_name(mGoodsSpecVo.getSpec());
                bInOrderGoodsVo.setSku_code(mGoodsSpecVo.getCode());

                bInOrderGoodsVo.setOrder_id(entity.getId());
                ibInOrderGoodsService.insert(bInOrderGoodsVo);
            }
        }

        BOrderVo bOrderVo = new BOrderVo();
        bOrderVo.setSerial_id(entity.getId());
        bOrderVo.setSerial_type(SystemConstants.ORDER.B_IN_ORDER);
        ibOrderService.insert(bOrderVo);

        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(ApiBInOrderVo vo) {
        // 插入逻辑保存
        BInOrderEntity entity = mapper.selectByOrderNo(vo.getOrder_no());
        BeanUtilsSupport.copyProperties(vo, entity, new String[]{"id"});
        vo.setId(entity.getId());

        if (StringUtils.isNotEmpty(vo.getSupplier_credit_no())) {
            ApiCustomerVo apiCustomerVo = new ApiCustomerVo();
            apiCustomerVo.setCredit_no(vo.getSupplier_credit_no());
            MCustomerEntity supplier = mCustomerMapper.selectByCustomerCreditNo(apiCustomerVo);
            if (supplier == null) {
                throw new ApiBusinessException(vo.getSupplier_credit_no()+":供应商未同步");
            }
            entity.setSupplier_id(supplier.getId());
            entity.setSupplier_code(supplier.getCode());
        }

        if (StringUtils.isNotEmpty(vo.getOwner_credit_no())) {
            ApiCustomerVo apiCustomerVo = new ApiCustomerVo();
            apiCustomerVo.setCredit_no(vo.getOwner_credit_no());
            MOwnerEntity owner = mOwnerMapper.selectByOwnerCreditNo(apiCustomerVo);
            if (owner == null) {
                throw new ApiBusinessException(vo.getOwner_credit_no()+":货主未同步");
            }
            entity.setOwner_id(owner.getId());
            entity.setOwner_code(owner.getCode());
        }

        if (StringUtils.isNotEmpty(vo.getBusiness_type_code())) {
            MBusinessTypeVo businessTypeVo = mBusinessTypeMapper.selectBusinessByCode(vo.getBusiness_type_code());
            if (businessTypeVo == null) {
                throw new ApiBusinessException(vo.getBusiness_type_code()+":业务板块未同步");
            }
            entity.setBusiness_type_id(businessTypeVo.getId());
            entity.setBusiness_type_code(businessTypeVo.getCode());
        }

        int rtn = mapper.updateById(entity);

        ibInOrderGoodsService.delete(entity.getId());
        if (vo.getDetailListData() != null) {
            for (ApiBInOrderGoodsVo bInOrderGoodsVo : vo.getDetailListData()) {

                MUnitVo mUnitVo = mUnitMapper.selectByCode(bInOrderGoodsVo.getUnit_code());
                if (mUnitVo != null) {
                    bInOrderGoodsVo.setUnit_id(mUnitVo.getId());
                    bInOrderGoodsVo.setUnit_name(mUnitVo.getName());
                }

                MGoodsSpecVo mGoodsSpecVo = mGoodsSpecMapper.selectByCode(bInOrderGoodsVo.getSku_code());
                if (mGoodsSpecVo == null) {
                    throw new ApiBusinessException(bInOrderGoodsVo.getSku_code()+":规格未同步");
                }
                bInOrderGoodsVo.setSku_id(mGoodsSpecVo.getId());
                bInOrderGoodsVo.setSku_name(mGoodsSpecVo.getSpec());
                bInOrderGoodsVo.setSku_code(mGoodsSpecVo.getCode());

                bInOrderGoodsVo.setOrder_id(entity.getId());
                ibInOrderGoodsService.insert(bInOrderGoodsVo);
            }
        }


        // 插入逻辑保存
        return UpdateResultUtil.OK(rtn);
    }


//    @Override
//    public void sync(List<ApiBInOrderVo> list) {
//        // 插入前check
//        check(list);
//
//        for (ApiBInOrderVo vo: list) {
//            // 插入逻辑保存
//            BInOrderEntity entity = mapper.selectByOrderNo(vo.getOrder_no());
//            if (entity != null) {
//                update(vo);
//            } else {
//                insert(vo);
//            }
//
//        }
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sync(List<ApiBInOrderVo> list) {
        // 锁表
        bWkPoMapper.lockB_wk_po_detail10();
        bWkPoMapper.lockB_wk_po_detail11();

        // 清空数据
        bWkPoMapper.deleteB_wk_poWor00k();
        bWkPoMapper.deleteB_wk_po_detail01();

        List<BWkPoEntity> orderList = new ArrayList<>();
        List<BWkPoDetailEntity> orderDetailList = new ArrayList<>();
        for (ApiBInOrderVo vo: list) {
            BWkPoEntity bWkPoEntity = new BWkPoEntity();
            BeanUtilsSupport.copyProperties(vo, bWkPoEntity, new String[]{"id"});
            bWkPoEntity.setOver_inventory_policy(vo.getFloat_controled());
            bWkPoEntity.setOver_inventory_lower(vo.getFloat_down());
            bWkPoEntity.setOver_inventory_upper(vo.getFloat_up());
            orderList.add(bWkPoEntity);
            if (vo.getDetailListData() != null) {
                for (ApiBInOrderGoodsVo bInOrderGoodsVo : vo.getDetailListData()) {
                    BWkPoDetailEntity bWkPoDetailEntity = new BWkPoDetailEntity();
                    BeanUtilsSupport.copyProperties(bInOrderGoodsVo, bWkPoDetailEntity, new String[]{"id"});
                    bWkPoDetailEntity.setOrder_no(vo.getOrder_no());
                    orderDetailList.add(bWkPoDetailEntity);
                }
            }
        }
//        ibWkPoService.saveBatch(orderList);
//        ibWkPoDetailService.saveBatch(orderDetailList, 10000);
        // check
        ApiBWkPoVo apiBWkPoVo = bWkPoMapper.checkB_wk_po20();
        if (apiBWkPoVo != null) {
            switch (apiBWkPoVo.getFlag()) {
                // 错误类型 1订单编号为空 2业务类型为空 3供应商位同步 4货主未同步 5业务板块未同步
                case "1":
                    throw new ApiBusinessException("订单编号为空");
                case "2":
                    throw new ApiBusinessException("业务类型为空");
                case "3":
                    throw new ApiBusinessException("请先同步供应商【"+apiBWkPoVo.getSupplier_credit_no()+":"+apiBWkPoVo.getSupplier_name()+"】");
                case "4":
                    throw new ApiBusinessException("请先同步货主【"+apiBWkPoVo.getOwner_credit_no()+":"+apiBWkPoVo.getOwner_name()+"】");
                case "5":
                    throw new ApiBusinessException("请先同步业务板块【"+apiBWkPoVo.getBusiness_type_code()+":"+apiBWkPoVo.getBusiness_type_name()+"】");
                default:
                    break;
            }
        }
        // check detail
        ApiBWkPoDetailVo apiBWkPoDetailVo = bWkPoMapper.checkB_wk_po_detail21();
        if (apiBWkPoDetailVo != null) {
            switch (apiBWkPoDetailVo.getFlag()) {
                // 错误类型 1规格code为空 2未同步规格 3单位未同步
                case "1":
                    throw new ApiBusinessException("规格code为空");
                case "2":
                    throw new ApiBusinessException("请先同步规格【"+apiBWkPoDetailVo.getSku_code()+"】");
                case "3":
                    throw new ApiBusinessException("请先同步单位【"+apiBWkPoDetailVo.getUnit_code()+":"+apiBWkPoDetailVo.getUnit_name()+"】");
                default:
                    break;
            }
        }

        // 更新插入数据
        bWkPoMapper.updateB_wk_po30();
        bWkPoMapper.insertB_wk_po30();

        // 明细数据全删全插
        // 删除明细数据
        bWkPoMapper.deleteB_wk_po_detail32();
        // 插入明细数据
//        bWkPoMapper.updateB_wk_po_detail31();
        bWkPoMapper.insertB_wk_po_detail31();

        // 插入b_order
        bWkPoMapper.updateB_wk_po33();
        bWkPoMapper.insertB_wk_po33();
    }

    /**
     * check逻辑
     */
    public void check(List<ApiBInOrderVo> list) {

        // 内部check 列表check
        List<String> codeList = list.stream().map(ApiBInOrderVo::getOrder_no).collect(Collectors.toList());
        long codeCount = codeList.stream().distinct().count();
        if (list.size() != codeCount) {
            throw new ApiBusinessException(ApiResultEnum.ORDER_PARAM_CODE_REPEAT);
        }

        for (ApiBInOrderVo vo : list) {
            if (StringUtils.isEmpty(vo.getOrder_no())) {
                throw new ApiBusinessException(ApiResultEnum.ORDER_PARAM_CODE_NULL);
            }

            if (StringUtils.isEmpty(vo.getBill_type())) {
                throw new ApiBusinessException(ApiResultEnum.ORDER_PARAM_BILL_TYPE_NULL);
            }

//            if (StringUtils.isEmpty(vo.getSupplier_code())) {
//                throw new ApiBusinessException(ApiResultEnum.ORDER_PARAM_SUPPLIER_CODE_NULL);
//            }
//
//            if (StringUtils.isEmpty(vo.getOwner_code())) {
//                throw new ApiBusinessException(ApiResultEnum.ORDER_PARAM_OWNER_CODE_NULL);
//            }


//            BInOrderVo checkOrderVo = (BInOrderVo)BeanUtilsSupport.copyProperties(vo,BInOrderVo.class);
//            // 按合同编号和订单编号查询是否存在数据
//            List<BInOrderEntity> selectByOrder = mapper.selectOrderByContract(checkOrderVo);
//
//            if (selectByOrder.size() > 1) {
//                throw new ApiBusinessException(ApiResultEnum.ORDER_PARAM_CODE_REPEAT);
//            }
        }

    }
}
