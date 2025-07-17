package com.xinyirun.scm.core.system.serviceimpl.business.aprefundpay;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.aprefundpay.BApReFundPaySourceAdvanceEntity;
import com.xinyirun.scm.bean.system.vo.business.aprefundpay.BApReFundPaySourceAdvanceVo;
import com.xinyirun.scm.core.system.mapper.business.aprefundpay.BApReFundPaySourceAdvanceMapper;
import com.xinyirun.scm.core.system.service.business.aprefundpay.IBApReFundPaySourceAdvanceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 退款单关联单据表-源单-预收款 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-17
 */
@Service
public class BApReFundPaySourceAdvanceServiceImpl extends ServiceImpl<BApReFundPaySourceAdvanceMapper, BApReFundPaySourceAdvanceEntity> implements IBApReFundPaySourceAdvanceService {

    @Override
    public List<BApReFundPaySourceAdvanceVo> selectList(BApReFundPaySourceAdvanceVo vo) {
        return baseMapper.selectList(vo);
    }

    @Override
    public List<BApReFundPaySourceAdvanceEntity> selectByApRefundPayId(Integer apRefundPayId) {
        return baseMapper.selectByApRefundPayId(apRefundPayId);
    }

    @Override
    public List<BApReFundPaySourceAdvanceEntity> selectByApRefundId(Integer apRefundId) {
        return baseMapper.selectByApRefundId(apRefundId);
    }

    @Override
    public List<BApReFundPaySourceAdvanceEntity> selectByPoContractId(Integer poContractId) {
        return baseMapper.selectByPoContractId(poContractId);
    }

    @Override
    public List<BApReFundPaySourceAdvanceEntity> selectByPoOrderId(Integer poOrderId) {
        return baseMapper.selectByPoOrderId(poOrderId);
    }

    @Override
    public int deleteByApRefundPayId(Integer apRefundPayId) {
        return baseMapper.deleteByApRefundPayId(apRefundPayId);
    }

    @Override
    public boolean saveBatch(Integer apRefundPayId, List<BApReFundPaySourceAdvanceEntity> list) {
        // 先删除原有的关联记录
        deleteByApRefundPayId(apRefundPayId);
        
        // 如果列表为空，则直接返回成功
        if (list == null || list.isEmpty()) {
            return true;
        }
        
        // 批量插入新的关联记录
        int insertCount = baseMapper.insertBatch(list);
        return insertCount > 0;
    }

    @Override
    public int insertBatch(List<BApReFundPaySourceAdvanceEntity> list) {
        return baseMapper.insertBatch(list);
    }

}