package com.xinyirun.scm.core.api.serviceimpl.business.v1.carriage.order;

import com.xinyirun.scm.bean.api.vo.business.in.ApiBInOrderVo;
import com.xinyirun.scm.bean.api.vo.business.monitor.ApiBWkCoDetailVo;
import com.xinyirun.scm.bean.api.vo.business.monitor.ApiBWkCoVo;
import com.xinyirun.scm.bean.api.vo.business.monitor.ApiCarriageOrderGoodsVo;
import com.xinyirun.scm.bean.api.vo.business.monitor.ApiCarriageOrderVo;
import com.xinyirun.scm.bean.entity.busniess.monitor.BCarriageOrderEntity;
import com.xinyirun.scm.bean.entity.busniess.monitor.BWkCoDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.monitor.BWkCoEntity;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.common.exception.api.ApiBusinessException;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.api.mapper.business.logistics.ApiBScheduleMapper;
import com.xinyirun.scm.core.api.service.business.v1.carriage.ApiICarriageOrderService;
import com.xinyirun.scm.core.system.mapper.business.monitor.BCarriageOrderMapper;
import com.xinyirun.scm.core.system.mapper.business.monitor.BWkCoMapper;
import com.xinyirun.scm.core.system.service.business.monitor.temp.IBWkCoDetailService;
import com.xinyirun.scm.core.system.service.business.monitor.temp.IBWkCoService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wwl
 */
@Service
@Slf4j
public class ApiCarriageServiceImpl extends BaseServiceImpl<BCarriageOrderMapper, BCarriageOrderEntity> implements ApiICarriageOrderService {

    @Autowired
    private BCarriageOrderMapper mapper;

    @Autowired
    private IBWkCoService ibWkCoService;

    @Autowired
    private IBWkCoDetailService ibWkCoDetailService;

    @Autowired
    private BWkCoMapper bWkCoMapper;

    @Autowired
    private ApiBScheduleMapper apiBScheduleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sync(List<ApiCarriageOrderVo> list) {
        // 锁表
        bWkCoMapper.lockB_wk_co_detail10();
        bWkCoMapper.lockB_wk_co_detail11();

        // 清空数据
        bWkCoMapper.deleteB_wk_coWor00k();
        bWkCoMapper.deleteB_wk_co_detail01();

        List<BWkCoEntity> orderList = new ArrayList<>();
        List<BWkCoDetailEntity> orderDetailList = new ArrayList<>();
        for (ApiCarriageOrderVo vo: list) {
            BWkCoEntity bWkPoEntity = new BWkCoEntity();
            BeanUtilsSupport.copyProperties(vo, bWkPoEntity, new String[]{"id"});
            bWkPoEntity.setHaul_distance(vo.getHaulDistance());
            bWkPoEntity.setSales_contract_code(vo.getSales_contract_code());
            bWkPoEntity.setPay_type(vo.getPayType());
            BigDecimal total_amount = BigDecimal.ZERO;
            if (vo.getCommodityList() != null) {
                for (ApiCarriageOrderGoodsVo apiCarriageOrderGoodsVo : vo.getCommodityList()) {
                    BWkCoDetailEntity bWkCoDetailEntity = new BWkCoDetailEntity();
                    total_amount = total_amount.add(apiCarriageOrderGoodsVo.getAmount());
                    if (!Objects.isNull(apiCarriageOrderGoodsVo.getNo())) {
                        bWkCoDetailEntity.setNo(apiCarriageOrderGoodsVo.getNo().toString());
                    }
                    bWkCoDetailEntity.setOrder_no(vo.getOrder_no());
                    BeanUtilsSupport.copyProperties(apiCarriageOrderGoodsVo, bWkCoDetailEntity, new String[]{"id"});
                    orderDetailList.add(bWkCoDetailEntity);
                }
            }
            bWkPoEntity.setTotal_amount(total_amount);
            orderList.add(bWkPoEntity);
        }
        ibWkCoService.saveBatch(orderList);
        ibWkCoDetailService.saveBatch(orderDetailList, 10000);
        // check
        ApiBWkCoVo apiBWkcoVo = bWkCoMapper.checkB_wk_co20();
        if (apiBWkcoVo != null) {
            switch (apiBWkcoVo.getFlag()) {
                // 错误类型 1订单编号为空 2业务类型为空 3承运人 4托运人
                case "1":
                    throw new ApiBusinessException("订单编号为空");
                case "2":
                    throw new ApiBusinessException("合同编号为空");
//                case "3":
//                    throw new ApiBusinessException("请先同步承运人【"+apiBWkcoVo.getCompany_credit_no()+":"+apiBWkcoVo.getCompany_name()+"】");
//                case "4":
//                    throw new ApiBusinessException("请先同步托运人【"+apiBWkcoVo.getOrg_credit_no()+":"+apiBWkcoVo.getOrg_name()+"】");
                default:
                    break;
            }
        }
        // check detail
        ApiBWkCoDetailVo apiBWkCoDetailVo = bWkCoMapper.checkB_wk_co_detail21();
        if (apiBWkCoDetailVo != null) {
            switch (apiBWkCoDetailVo.getFlag()) {
                // 错误类型 1规格code为空 2未同步规格
                case "1":
                    throw new ApiBusinessException("规格code为空");
                case "2":
                    throw new ApiBusinessException("请先同步规格【"+apiBWkCoDetailVo.getSku_code()+"】");
                default:
                    break;
            }
        }

        // 更新插入数据
        bWkCoMapper.updateB_wk_co30();
        bWkCoMapper.insertB_wk_co30();

        // 明细数据全删全插
        // 删除明细数据
        bWkCoMapper.deleteB_wk_co_detail32();
        // 插入明细数据
        bWkCoMapper.insertB_wk_co_detail31();

        // 插入b_order
        bWkCoMapper.updateB_wk_co33();
        bWkCoMapper.insertB_wk_co33();
    }

    @Override
    public void check(ApiCarriageOrderVo vo) {
        String code = apiBScheduleMapper.checkCarriageOrder(vo.getOrder_no());
        if (StringUtils.isNotEmpty(code)) {
            throw  new BusinessException("该承运订单已被【"+code+"】使用，请先作废该物流订单");
        }
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


        }

    }
}
