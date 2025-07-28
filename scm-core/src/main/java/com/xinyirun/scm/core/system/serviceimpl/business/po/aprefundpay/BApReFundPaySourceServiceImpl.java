package com.xinyirun.scm.core.system.serviceimpl.business.po.aprefundpay;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.po.aprefundpay.BApReFundPaySourceEntity;
import com.xinyirun.scm.bean.system.vo.business.po.aprefundpay.BApReFundPaySourceVo;
import com.xinyirun.scm.core.system.mapper.business.po.aprefundpay.BApReFundPaySourceMapper;
import com.xinyirun.scm.core.system.service.business.po.aprefundpay.IBApReFundPaySourceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 退款单关联单据表-源单 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-17
 */
@Service
public class BApReFundPaySourceServiceImpl extends ServiceImpl<BApReFundPaySourceMapper, BApReFundPaySourceEntity> implements IBApReFundPaySourceService {

    @Override
    public List<BApReFundPaySourceVo> selectList(BApReFundPaySourceVo vo) {
        return baseMapper.selectList(vo);
    }

    @Override
    public List<BApReFundPaySourceEntity> selectByApRefundPayId(Integer apRefundPayId) {
        return baseMapper.selectByApRefundPayId(apRefundPayId);
    }

    @Override
    public List<BApReFundPaySourceEntity> selectByApRefundId(Integer apRefundId) {
        return baseMapper.selectByApRefundId(apRefundId);
    }

    @Override
    public List<BApReFundPaySourceEntity> selectByPoContractId(Integer poContractId) {
        return baseMapper.selectByPoContractId(poContractId);
    }

    @Override
    public List<BApReFundPaySourceEntity> selectByPoOrderId(Integer poOrderId) {
        return baseMapper.selectByPoOrderId(poOrderId);
    }

    @Override
    public int deleteByApRefundPayId(Integer apRefundPayId) {
        return baseMapper.deleteByApRefundPayId(apRefundPayId);
    }

    @Override
    public boolean saveBatch(Integer apRefundPayId, List<BApReFundPaySourceEntity> list) {
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
    public int insertBatch(List<BApReFundPaySourceEntity> list) {
        return baseMapper.insertBatch(list);
    }

}