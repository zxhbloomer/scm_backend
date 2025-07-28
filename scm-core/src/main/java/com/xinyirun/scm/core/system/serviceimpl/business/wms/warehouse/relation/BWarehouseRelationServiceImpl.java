package com.xinyirun.scm.core.system.serviceimpl.business.wms.warehouse.relation;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.wms.warehouse.relation.BWarehouseRelationEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.BWarehouseRelationDataVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.BWarehouseRelationVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.warehouse.relation.BWarehouseRelationMapper;
import com.xinyirun.scm.core.system.service.business.warehouse.relation.IBWarehouseRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
@Service
public class BWarehouseRelationServiceImpl extends ServiceImpl<BWarehouseRelationMapper, BWarehouseRelationEntity> implements IBWarehouseRelationService {

    @Autowired
    private BWarehouseRelationMapper mapper;

    /**
     * 更新仓库关系数据）
     * @param vo 实体对象
     * @return
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Boolean> setRelation(BWarehouseRelationDataVo vo) {
        // 按岗位删除数据，全删全插
        vo.setSerial_type("m_position");
        mapper.deleteBySerialId(vo.getSerial_id(), vo.getSerial_type());

        // 批量插入
        List<BWarehouseRelationEntity> entities = new ArrayList<>();
        for (BWarehouseRelationVo data: vo.getDatas()) {
            data.setSerial_id(vo.getSerial_id());
            data.setSerial_type(vo.getSerial_type());
            BWarehouseRelationEntity entity = (BWarehouseRelationEntity) BeanUtilsSupport.copyProperties(data, BWarehouseRelationEntity.class);
            entities.add(entity);
        }
        return InsertResultUtil.OK(super.saveBatch(entities, 500));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(BWarehouseRelationVo vo) {
        BWarehouseRelationEntity entity = new BWarehouseRelationEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        return InsertResultUtil.OK(mapper.insert(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByStaffId(Integer staffId) {
        mapper.deleteByStaffId(staffId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByPositionId(Integer positionId) {
        mapper.deleteByPositionId(positionId);
    }

    /**
     * 获取所有数据
     */
    @Override
    public List<BWarehouseRelationVo> select(BWarehouseRelationVo searchCondition) {
        return null;
    }

    @Override
    public Integer selectCountByRelationCode(BWarehouseRelationVo searchCondition) {
        return mapper.selectCountByRelationCode(searchCondition);
    }
}
