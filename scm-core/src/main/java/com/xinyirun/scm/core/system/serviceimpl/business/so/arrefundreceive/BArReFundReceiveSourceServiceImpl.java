package com.xinyirun.scm.core.system.serviceimpl.business.so.arrefundreceive;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.so.arrefundreceive.BArReFundReceiveSourceEntity;
import com.xinyirun.scm.bean.system.vo.business.so.arrefundreceive.BArReFundReceiveSourceVo;
import com.xinyirun.scm.core.system.mapper.business.so.arrefundreceive.BArReFundReceiveSourceMapper;
import com.xinyirun.scm.core.system.service.business.so.arrefundreceive.IBArReFundReceiveSourceService;
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
public class BArReFundReceiveSourceServiceImpl extends ServiceImpl<BArReFundReceiveSourceMapper, BArReFundReceiveSourceEntity> implements IBArReFundReceiveSourceService {

    @Override
    public List<BArReFundReceiveSourceVo> selectList(BArReFundReceiveSourceVo vo) {
        return baseMapper.selectList(vo);
    }

    @Override
    public List<BArReFundReceiveSourceEntity> selectByArRefundReceiveId(Integer arRefundReceiveId) {
        return baseMapper.selectByArRefundReceiveId(arRefundReceiveId);
    }

    @Override
    public List<BArReFundReceiveSourceEntity> selectByArRefundId(Integer arRefundId) {
        return baseMapper.selectByArRefundId(arRefundId);
    }

    @Override
    public List<BArReFundReceiveSourceEntity> selectBySoContractId(Integer soContractId) {
        return baseMapper.selectBySoContractId(soContractId);
    }

    @Override
    public List<BArReFundReceiveSourceEntity> selectBySoOrderId(Integer soOrderId) {
        return baseMapper.selectBySoOrderId(soOrderId);
    }

    @Override
    public int deleteByArRefundReceiveId(Integer arRefundReceiveId) {
        return baseMapper.deleteByArRefundReceiveId(arRefundReceiveId);
    }

    @Override
    public boolean saveBatch(Integer arRefundReceiveId, List<BArReFundReceiveSourceEntity> list) {
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
    public int insertBatch(List<BArReFundReceiveSourceEntity> list) {
        return baseMapper.insertBatch(list);
    }

}