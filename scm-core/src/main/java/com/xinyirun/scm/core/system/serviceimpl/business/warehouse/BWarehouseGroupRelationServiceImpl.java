package com.xinyirun.scm.core.system.serviceimpl.business.warehouse;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xinyirun.scm.bean.entity.busniess.warehouse.BWarehouseGroupRelationEntity;
import com.xinyirun.scm.bean.system.vo.business.warehouse.BWarehouseGroupOperationVo;
import com.xinyirun.scm.bean.system.vo.business.warehouse.BWarehouseGroupTransferVo;
import com.xinyirun.scm.bean.system.vo.business.warehouse.BWarehouseTransferVo;
import com.xinyirun.scm.common.utils.ArrayPfUtil;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.warehouse.BWarehouseGroupRelationMapper;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MWarehouseMapper;
import com.xinyirun.scm.core.system.service.business.warehouse.IBWarehouseGroupRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 仓库关系表-一级 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-01-30
 */
@Service
public class BWarehouseGroupRelationServiceImpl extends ServiceImpl<BWarehouseGroupRelationMapper, BWarehouseGroupRelationEntity> implements IBWarehouseGroupRelationService {

    @Autowired
    private BWarehouseGroupRelationMapper mapper;

    @Autowired
    private MWarehouseMapper mWarehouseMapper;

    @Override
    public BWarehouseGroupTransferVo getWarehouseTransferList(BWarehouseTransferVo condition) {
        BWarehouseGroupTransferVo rtn = new BWarehouseGroupTransferVo();

        // 获取全部仓库
        rtn.setWarehouse_all(mWarehouseMapper.getAllWarehouseTransferList(new BWarehouseTransferVo()));
        // 获取该仓库已经设置过的仓库
        List<Integer> rtnList = mWarehouseMapper.getWarehouseGroupOneTransferList(condition);
        rtn.setGroup_warehouse(rtnList.toArray(new Integer[rtnList.size()]));
        rtn.setGroup_warehouse_count(rtnList.size());

        return rtn;
    }

    @Override
    public BWarehouseGroupTransferVo setWarehouseTransfer(BWarehouseTransferVo bean) {
        // 查询出需要剔除的权限list
        List<BWarehouseGroupOperationVo> deleteMemberList = mWarehouseMapper.selectDeleteMemberOne(bean);
        // 查询出需要添加的权限list
        List<BWarehouseGroupOperationVo> insertMemberList = mWarehouseMapper.selectInsertMemberOne(bean);

        // 执行保存逻辑，并返回权限数量
        return this.saveMemberList(deleteMemberList, insertMemberList, bean);
    }

    /**
     * 保存员工关系，删除剔除的员工，增加选择的员工
     * @param deleteMemberList
     * @param insertMemberList
     * @param bean
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public BWarehouseGroupTransferVo saveMemberList(List<BWarehouseGroupOperationVo> deleteMemberList, List<BWarehouseGroupOperationVo> insertMemberList, BWarehouseTransferVo bean) {

        // 删除剔除的权限
        List<BWarehouseGroupRelationEntity> delete_list = BeanUtilsSupport.copyProperties(deleteMemberList, BWarehouseGroupRelationEntity.class, new String[] {"c_time", "u_time"});
        List<Integer> ids = Lists.newArrayList();
        delete_list.forEach(beans -> {
            ids.add(beans.getId());
        });
        if (ArrayPfUtil.isNotEmpty(ids)) {
            this.removeByIds(ids);
        }

        // 增加选择的仓库
        Integer[] warehouse_ids = new Integer[insertMemberList.size()];
        int i = 0;
        List<BWarehouseGroupRelationEntity> insert_list = new ArrayList<>();
        for( BWarehouseGroupOperationVo vo : insertMemberList ) {
            BWarehouseGroupRelationEntity entity = new BWarehouseGroupRelationEntity();
            entity.setWarehouse_id(vo.getId());
            entity.setWarehouse_group_id(bean.getWarehouse_group_id());
            insert_list.add(entity);

            warehouse_ids[i] = vo.getId();
            i = i + 1;
        }

        this.saveBatch(insert_list);

        // 查询最新数据并返回
        // 获取该岗位已经设置过得用户
        List<Integer> rtnList = mWarehouseMapper.getWarehouseGroupOneTransferList(bean);
        BWarehouseGroupTransferVo bWarehouseGroupTransferVo = new BWarehouseGroupTransferVo();
        bWarehouseGroupTransferVo.setGroup_warehouse_count(rtnList.size());
        return bWarehouseGroupTransferVo;
    }
}
