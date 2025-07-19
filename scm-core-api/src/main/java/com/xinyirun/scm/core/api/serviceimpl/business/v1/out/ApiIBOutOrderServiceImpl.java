package com.xinyirun.scm.core.api.serviceimpl.business.v1.out;

import com.xinyirun.scm.bean.api.vo.business.out.ApiBOutOrderGoodsVo;
import com.xinyirun.scm.bean.api.vo.business.out.ApiBOutOrderVo;
import com.xinyirun.scm.bean.api.vo.business.out.ApiBWkSoDetailVo;
import com.xinyirun.scm.bean.api.vo.business.out.ApiBWkSoVo;
import com.xinyirun.scm.bean.api.vo.master.customer.ApiCustomerVo;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutOrderEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.out.order.temp.BWkSoDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.out.order.temp.BWkSoEntity;
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
import com.xinyirun.scm.core.api.service.business.v1.out.ApiIBOutOrderGoodsService;
import com.xinyirun.scm.core.api.service.business.v1.out.ApiIBOutOrderService;
import com.xinyirun.scm.core.system.mapper.business.wms.out.order.BOutOrderMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.out.order.temp.BWkSoMapper;
import com.xinyirun.scm.core.system.mapper.master.customer.MCustomerMapper;
import com.xinyirun.scm.core.system.mapper.master.customer.MOwnerMapper;
import com.xinyirun.scm.core.system.mapper.master.goods.MBusinessTypeMapper;
import com.xinyirun.scm.core.system.mapper.master.goods.MGoodsSpecMapper;
import com.xinyirun.scm.core.system.mapper.master.goods.unit.MUnitMapper;
import com.xinyirun.scm.core.system.service.business.order.IBOrderService;
import com.xinyirun.scm.core.system.service.business.wms.out.order.temp.IBWkSoDetailService;
import com.xinyirun.scm.core.system.service.business.wms.out.order.temp.IBWkSoService;
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
public class ApiIBOutOrderServiceImpl extends BaseServiceImpl<BOutOrderMapper, BOutOrderEntity> implements ApiIBOutOrderService {

    @Autowired
    private BOutOrderMapper mapper;

    @Autowired
    private ApiIBOutOrderGoodsService ibOutOrderGoodsService;

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

    @Autowired
    private IBWkSoService ibWkSoService;

    @Autowired
    private IBWkSoDetailService ibWkSoDetailService;

    @Autowired
    private BWkSoMapper bWkSoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(ApiBOutOrderVo vo) {

        // 插入逻辑保存
        BOutOrderEntity entity = (BOutOrderEntity) BeanUtilsSupport.copyProperties(vo, BOutOrderEntity.class);

        vo.setId(entity.getId());

        if (StringUtils.isNotEmpty(vo.getClient_credit_no())) {
            ApiCustomerVo apiCustomerVo = new ApiCustomerVo();
            apiCustomerVo.setCredit_no(vo.getClient_credit_no());
            MCustomerEntity client = mCustomerMapper.selectByCustomerCreditNo(apiCustomerVo);
            if (client == null) {
                throw new ApiBusinessException(vo.getClient_credit_no()+":客户未同步");
            }
            entity.setClient_id(client.getId());
            entity.setClient_code(client.getCode());
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
            for (ApiBOutOrderGoodsVo bOutOrderGoodsVo : vo.getDetailListData()) {
                MUnitVo mUnitVo = mUnitMapper.selectByCode(bOutOrderGoodsVo.getUnit_code());
                if (mUnitVo != null) {
                    bOutOrderGoodsVo.setUnit_id(mUnitVo.getId());
                    bOutOrderGoodsVo.setUnit_name(mUnitVo.getName());
                }

                MGoodsSpecVo mGoodsSpecVo = mGoodsSpecMapper.selectByCode(bOutOrderGoodsVo.getSku_code());
                if (mGoodsSpecVo == null) {
                    throw new ApiBusinessException(bOutOrderGoodsVo.getSku_code()+":规格未同步");
                }
                bOutOrderGoodsVo.setSku_id(mGoodsSpecVo.getId());
                bOutOrderGoodsVo.setSku_name(mGoodsSpecVo.getSpec());
                bOutOrderGoodsVo.setSku_code(mGoodsSpecVo.getCode());

                bOutOrderGoodsVo.setOrder_id(entity.getId());
                ibOutOrderGoodsService.insert(bOutOrderGoodsVo);
            }
        }

        BOrderVo bOrderVo = new BOrderVo();
        bOrderVo.setSerial_id(entity.getId());
        bOrderVo.setSerial_type(SystemConstants.ORDER.B_OUT_ORDER);
        ibOrderService.insert(bOrderVo);

        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(ApiBOutOrderVo vo) {
        // 插入逻辑保存
        BOutOrderEntity entity = mapper.selectByOrderNo(vo.getOrder_no());
        BeanUtilsSupport.copyProperties(vo, entity, new String[]{"id"});
        vo.setId(entity.getId());

        if (StringUtils.isNotEmpty(vo.getClient_credit_no())) {
            ApiCustomerVo apiCustomerVo = new ApiCustomerVo();
            apiCustomerVo.setCredit_no(vo.getClient_credit_no());
            MCustomerEntity client = mCustomerMapper.selectByCustomerCreditNo(apiCustomerVo);
            if (client == null) {
                throw new ApiBusinessException(vo.getClient_credit_no()+":客户未同步");
            }
            entity.setClient_id(client.getId());
            entity.setClient_code(client.getCode());
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

        ibOutOrderGoodsService.delete(entity.getId());
        if (vo.getDetailListData() != null) {
            for (ApiBOutOrderGoodsVo bOutOrderGoodsVo : vo.getDetailListData()) {

                MUnitVo mUnitVo = mUnitMapper.selectByCode(bOutOrderGoodsVo.getUnit_code());
                if (mUnitVo != null) {
                    bOutOrderGoodsVo.setUnit_id(mUnitVo.getId());
                    bOutOrderGoodsVo.setUnit_name(mUnitVo.getName());
                    bOutOrderGoodsVo.setUnit_code(mUnitVo.getCode());
                }

                MGoodsSpecVo mGoodsSpecVo = mGoodsSpecMapper.selectByCode(bOutOrderGoodsVo.getSku_code());
                if (mGoodsSpecVo == null) {
                    throw new ApiBusinessException(bOutOrderGoodsVo.getSku_code()+":规格未同步");
                }
                bOutOrderGoodsVo.setSku_id(mGoodsSpecVo.getId());
                bOutOrderGoodsVo.setSku_name(mGoodsSpecVo.getSpec());
                bOutOrderGoodsVo.setSku_code(mGoodsSpecVo.getCode());

                bOutOrderGoodsVo.setOrder_id(entity.getId());
                ibOutOrderGoodsService.insert(bOutOrderGoodsVo);
            }
        }

        // 插入逻辑保存
        return UpdateResultUtil.OK(rtn);
    }


//    @Override
//    public void sync(List<ApiBOutOrderVo> list) {
//        // 插入前check
//        check(list);
//
//        for (ApiBOutOrderVo vo: list) {
//
//            BOutOrderEntity entity = mapper.selectByOrderNo(vo.getOrder_no());
//            if (entity == null) {
//                insert(vo);
//            } else {
//                update(vo);
//            }
//
//        }
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sync(List<ApiBOutOrderVo> list) {
        // 锁表
        bWkSoMapper.lockB_wk_so_detail10();
        bWkSoMapper.lockB_wk_so_detail11();

        // 清空数据
        bWkSoMapper.deleteB_wk_soWork00();
        bWkSoMapper.deleteB_wk_so_detail01();

        List<BWkSoEntity> orderList = new ArrayList<>();
        List<BWkSoDetailEntity> orderDetailList = new ArrayList<>();
        for (ApiBOutOrderVo vo: list) {
            BWkSoEntity bWkSoEntity = new BWkSoEntity();
            BeanUtilsSupport.copyProperties(vo, bWkSoEntity, new String[]{"id"});
            bWkSoEntity.setOver_inventory_policy(vo.getFloat_controled());
            bWkSoEntity.setOver_inventory_lower(vo.getFloat_down());
            bWkSoEntity.setOver_inventory_upper(vo.getFloat_up());
            orderList.add(bWkSoEntity);
            if (vo.getDetailListData() != null) {
                for (ApiBOutOrderGoodsVo bOutOrderGoodsVo : vo.getDetailListData()) {
                    BWkSoDetailEntity bWkSoDetailEntity = new BWkSoDetailEntity();
                    BeanUtilsSupport.copyProperties(bOutOrderGoodsVo, bWkSoDetailEntity, new String[]{"id"});
                    bWkSoDetailEntity.setOrder_no(vo.getOrder_no());
                    orderDetailList.add(bWkSoDetailEntity);
                }
            }
        }
        ibWkSoService.saveBatch(orderList);
        ibWkSoDetailService.saveBatch(orderDetailList, 10000);
        // check
        ApiBWkSoVo apiBWkSoVo = bWkSoMapper.checkB_wk_so20();
        if (apiBWkSoVo != null) {
            switch (apiBWkSoVo.getFlag()) {
                // 错误类型 1订单编号为空 2业务类型为空 3供应商位同步 4货主未同步 5业务板块未同步
                case "1":
                    throw new ApiBusinessException("订单编号为空");
                case "2":
                    throw new ApiBusinessException("业务类型为空");
                case "3":
                    throw new ApiBusinessException("请先同步客户【"+apiBWkSoVo.getClient_credit_no()+":"+apiBWkSoVo.getClient_name()+"】");
                case "4":
                    throw new ApiBusinessException("请先同步货主【"+apiBWkSoVo.getOwner_credit_no()+":"+apiBWkSoVo.getOwner_name()+"】");
                case "5":
                    throw new ApiBusinessException("请先同步业务板块【"+apiBWkSoVo.getBusiness_type_code()+":"+apiBWkSoVo.getBusiness_type_name()+"】");
                default:
                    break;
            }
        }
        // check detail
        ApiBWkSoDetailVo apiBWkSoDetailVo = bWkSoMapper.checkB_wk_so_detail21();
        if (apiBWkSoDetailVo != null) {
            switch (apiBWkSoDetailVo.getFlag()) {
                // 错误类型 1规格code为空 2未同步规格 3单位未同步
                case "1":
                    throw new ApiBusinessException("规格code为空");
                case "2":
                    throw new ApiBusinessException("请先同步规格【"+apiBWkSoDetailVo.getSku_code()+"】");
                case "3":
                    throw new ApiBusinessException("请先同步单位【"+apiBWkSoDetailVo.getUnit_code()+":"+apiBWkSoDetailVo.getUnit_name()+"】");
                default:
                    break;
            }
        }

        // 更新数据
        bWkSoMapper.updateB_wk_so30();
        bWkSoMapper.insertB_wk_so30();

        // 明细数据全删全插
        // 删除明细数据
        bWkSoMapper.deleteB_wk_so_detail32();
        // 插入明细数据
        bWkSoMapper.insertB_wk_so_detail31();

        // 插入b_order
        bWkSoMapper.updateB_wk_so33();
        bWkSoMapper.insertB_wk_so33();
    }

    /**
     * check逻辑
     */
    public void check(List<ApiBOutOrderVo> list) {

        // 内部check 列表check
        List<String> codeList = list.stream().map(ApiBOutOrderVo::getOrder_no).collect(Collectors.toList());
        long codeCount = codeList.stream().distinct().count();
        if (list.size() != codeCount) {
            throw new ApiBusinessException(ApiResultEnum.ORDER_PARAM_CODE_REPEAT);
        }

        for (ApiBOutOrderVo vo : list) {
            if (StringUtils.isEmpty(vo.getOrder_no())) {
                throw new ApiBusinessException(ApiResultEnum.ORDER_PARAM_CODE_NULL);
            }

//            if (StringUtils.isEmpty(vo.getContract_no())) {
//                throw new ApiBusinessException(ApiResultEnum.ORDER_PARAM_CONTRACT_NO_NULL);
//            }

            if (StringUtils.isEmpty(vo.getBill_type())) {
                throw new ApiBusinessException(ApiResultEnum.ORDER_PARAM_BILL_TYPE_NULL);
            }

//            if (StringUtils.isEmpty(vo.getClient_code())) {
//                throw new ApiBusinessException(ApiResultEnum.ORDER_PARAM_CLIENT_CODE_NULL);
//            }
//
//            if (StringUtils.isEmpty(vo.getOwner_code())) {
//                throw new ApiBusinessException(ApiResultEnum.ORDER_PARAM_OWNER_CODE_NULL);
//            }

        }

    }
}
