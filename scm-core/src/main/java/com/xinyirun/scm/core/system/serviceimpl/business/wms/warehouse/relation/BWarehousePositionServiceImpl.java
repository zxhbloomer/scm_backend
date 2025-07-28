package com.xinyirun.scm.core.system.serviceimpl.business.wms.warehouse.relation;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.wms.warehouse.position.BWarehousePositionEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.position.BWarehousePositionDataVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.position.BWarehousePositionVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.position.MWarehousePositionTransferVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.position.MWarehouseTransferVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.warehouse.relation.BWarehousePositionMapper;
import com.xinyirun.scm.core.system.service.business.warehouse.relation.IBWarehousePositionService;
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
public class BWarehousePositionServiceImpl extends ServiceImpl<BWarehousePositionMapper, BWarehousePositionEntity> implements IBWarehousePositionService {

    @Autowired
    private BWarehousePositionMapper mapper;

    /**
     * 更新仓库关系数据）
     * @param vo 实体对象
     * @return
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Boolean> setWarehouse(BWarehousePositionDataVo vo) {
        // 按岗位删除数据，全删全插
        vo.setSerial_type("m_position");
        mapper.deleteBySerialId(vo.getSerial_id(), vo.getSerial_type());

        // 批量插入
        List<BWarehousePositionEntity> entities = new ArrayList<>();
        for (BWarehousePositionVo data: vo.getDatas()) {
            data.setSerial_id(vo.getSerial_id());
            data.setSerial_type(vo.getSerial_type());
            BWarehousePositionEntity entity = (BWarehousePositionEntity) BeanUtilsSupport.copyProperties(data, BWarehousePositionEntity.class);
            entities.add(entity);
        }
        return InsertResultUtil.OK(super.saveBatch(entities, 500));
    }

    /**
     * 获取所有数据
     */
    @Override
    public List<BWarehousePositionVo> select(BWarehousePositionVo searchCondition) {
        return null;
    }

    @Override
    public Integer selectCountByWarehouseCode(BWarehousePositionVo searchCondition) {
        return mapper.selectCountByWarehouseCode(searchCondition);
    }

    /**
     * 获取穿梭框数据
     * @return
     */
    @Override
    public MWarehousePositionTransferVo getWarehouseTransferList(MWarehouseTransferVo condition) {

        MWarehousePositionTransferVo rtn = new MWarehousePositionTransferVo();
        // 获取全部用户
        rtn.setWarehouse_all(mapper.getAllWarehouseTransferList(condition));
        // 获取该该关系已经设置过得仓库
        List<Long> rtnList = mapper.getUsedWarehouseTransferList(condition);
        rtn.setWarehouses(rtnList.toArray(new Long[rtnList.size()]));
        return rtn;
    }

    /**
     * 保存穿梭框数据，仓库组设置
     * @param bean 仓库id list
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String setWarehouseTransfer(MWarehouseTransferVo bean) {
        // 删除剔除的仓库
        mapper.realDeleteByCode(bean);
        // 增加选择的仓库
        List<BWarehousePositionEntity> lists = new ArrayList<>();
        for(int i=0; i < bean.getWarehouses().length; i++){
            BWarehousePositionEntity entity = new BWarehousePositionEntity();
            entity.setSerial_id(bean.getPosition_id());
            entity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_M_POSITION);
            entity.setWarehouse_id(bean.getWarehouses()[i]);
            lists.add(entity);
        }
        super.saveBatch(lists);
        return "OK";
    }
}
