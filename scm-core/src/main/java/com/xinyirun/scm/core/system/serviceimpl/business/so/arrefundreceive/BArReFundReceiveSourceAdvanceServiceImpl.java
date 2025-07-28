package com.xinyirun.scm.core.system.serviceimpl.business.so.arrefundreceive;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.so.arrefundreceive.BArReFundReceiveSourceAdvanceEntity;
import com.xinyirun.scm.bean.system.vo.business.so.arrefundreceive.BArReFundReceiveSourceAdvanceVo;
import com.xinyirun.scm.core.system.mapper.business.so.arrefundreceive.BArReFundReceiveSourceAdvanceMapper;
import com.xinyirun.scm.core.system.service.business.so.arrefundreceive.IBArReFundReceiveSourceAdvanceService;
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
public class BArReFundReceiveSourceAdvanceServiceImpl extends ServiceImpl<BArReFundReceiveSourceAdvanceMapper, BArReFundReceiveSourceAdvanceEntity> implements IBArReFundReceiveSourceAdvanceService {

    @Override
    public List<BArReFundReceiveSourceAdvanceVo> selectList(BArReFundReceiveSourceAdvanceVo vo) {
        return baseMapper.selectList(vo);
    }

    @Override
    public List<BArReFundReceiveSourceAdvanceEntity> selectByArRefundReceiveId(Integer arRefundReceiveId) {
        return baseMapper.selectByArRefundReceiveId(arRefundReceiveId);
    }

    @Override
    public List<BArReFundReceiveSourceAdvanceEntity> selectByArRefundId(Integer arRefundId) {
        return baseMapper.selectByArRefundId(arRefundId);
    }

    @Override
    public List<BArReFundReceiveSourceAdvanceEntity> selectBySoContractId(Integer soContractId) {
        return baseMapper.selectBySoContractId(soContractId);
    }

    @Override
    public List<BArReFundReceiveSourceAdvanceEntity> selectBySoOrderId(Integer soOrderId) {
        return baseMapper.selectBySoOrderId(soOrderId);
    }

    @Override
    public int deleteByArRefundReceiveId(Integer arRefundReceiveId) {
        return baseMapper.deleteByArRefundReceiveId(arRefundReceiveId);
    }

    @Override
    public boolean saveBatch(Integer arRefundReceiveId, List<BArReFundReceiveSourceAdvanceEntity> list) {
        // 先删除原有的关联记录
        deleteByArRefundReceiveId(arRefundReceiveId);
        
        // 如果列表为空，则直接返回成功
        if (list == null || list.isEmpty()) {
            return true;
        }
        
        // 批量插入新的关联记录
        int insertCount = baseMapper.insertBatch(list);
        return insertCount > 0;
    }

    @Override
    public int insertBatch(List<BArReFundReceiveSourceAdvanceEntity> list) {
        return baseMapper.insertBatch(list);
    }

}