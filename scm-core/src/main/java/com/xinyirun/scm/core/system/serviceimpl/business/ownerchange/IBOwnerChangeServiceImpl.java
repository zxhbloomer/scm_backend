package com.xinyirun.scm.core.system.serviceimpl.business.ownerchange;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.ownerchange.BOwnerChangeDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.ownerchange.BOwnerChangeEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MBinEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.vo.business.adjust.BAdjustDetailVo;
import com.xinyirun.scm.bean.system.vo.business.adjust.BAdjustVo;
import com.xinyirun.scm.bean.system.vo.business.ownerchange.BOwnerChangeVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.ownerchange.BOwnerChangeDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.ownerchange.BOwnerChangeMapper;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MBinMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.business.adjust.IBAdjustDetailService;
import com.xinyirun.scm.core.system.service.business.adjust.IBAdjustService;
import com.xinyirun.scm.core.system.service.business.ownerchange.IBOwnerChangeService;
import com.xinyirun.scm.core.system.service.master.inventory.IMInventoryService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.business.todo.TodoService;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 库存调整 服务实现类
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Service
public class IBOwnerChangeServiceImpl extends BaseServiceImpl<BOwnerChangeMapper, BOwnerChangeEntity> implements IBOwnerChangeService {

    @Autowired
    private BOwnerChangeMapper mapper;

    @Autowired
    private BOwnerChangeDetailMapper ownerChangeDetailMapper;

    @Autowired
    private TodoService todoService;

    @Autowired
    private MBinMapper binMapper;

    @Autowired
    private IMInventoryService imInventoryService;

    @Autowired
    private IBAdjustService ibAdjustService;

    @Autowired
    private IBAdjustDetailService ibAdjustDetailService;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    /**
     * 查询分页列表
     */
    @Override
    public IPage<BOwnerChangeVo> selectPage(BOwnerChangeVo searchCondition) {
        // 分页条件
        Page<BOwnerChangeDetailEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        searchCondition.setStaff_id(SecurityUtil.getStaff_id());

        // 查询入库计划page
        IPage<BOwnerChangeVo> result = mapper.selectPage(pageCondition, searchCondition);
        return result;
    }

    /**
     * 查询调整单数据
     */
    @Override
    public BOwnerChangeVo get(BOwnerChangeVo vo) {
        // 查询调整单page
        BOwnerChangeVo ownerChangeVo = mapper.get(vo.getId());
        // 查询调整单明细list
        ownerChangeVo.setDetailList(ownerChangeDetailMapper.getOwnerChangeDetailList(ownerChangeVo));

        if(ownerChangeVo.getFiles() != null) {
            SFileEntity file = fileMapper.selectById(ownerChangeVo.getFiles());
            List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id",file.getId()));
            ownerChangeVo.setFile_files(new ArrayList<>());
            for(SFileInfoEntity fileInfo:fileInfos) {
                SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                fileInfoVo.setFileName(fileInfoVo.getFile_name());
                ownerChangeVo.getFile_files().add(fileInfoVo);
            }
        }

        return ownerChangeVo;
    }


    /**
     * id查询返回入库计划更新对象
     */
    @Override
    public BOwnerChangeVo selectById(int id) {
        return mapper.selectId(id);
    }

  /** 审核 */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void audit(List<BOwnerChangeVo> searchCondition) {
    int updCount = 0;

    List<BOwnerChangeDetailEntity> list = mapper.selectIds(searchCondition);

    for (BOwnerChangeDetailEntity entity : list) {
        BOwnerChangeVo ownerChangeVo = new BOwnerChangeVo();
        ownerChangeVo.setId(entity.getId());
        BOwnerChangeVo bOwnerChangeVo = get(ownerChangeVo);

        // check
        checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE);
        entity.setStatus(DictConstant.DICT_B_OWNER_CHANGE_STATUS_PASSED);
        entity.setE_id(SecurityUtil.getStaff_id().intValue());
        entity.setE_dt(LocalDateTime.now());
        updCount = ownerChangeDetailMapper.updateById(entity);
        if (updCount == 0) {
          throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }

        // 旧库存信息生产库存调整
        BAdjustVo bAdjustVo = new BAdjustVo();
        bAdjustVo.setOwner_id(bOwnerChangeVo.getOut_owner_id());
        bAdjustVo.setOwner_code(bOwnerChangeVo.getOut_owner_code());
        bAdjustVo.setType(DictConstant.DICT_B_ADJUST_TYPE_ONE);
        bAdjustVo.setRemark("货权转移-旧库存信息");

        BAdjustDetailVo bAdjustDetailVo = new BAdjustDetailVo();
        MBinEntity binEntity = binMapper.selecBinByWarehouseId(bOwnerChangeVo.getOut_warehouse_id());

        bAdjustDetailVo.setWarehouse_id(bOwnerChangeVo.getOut_warehouse_id());
        bAdjustDetailVo.setLocation_id(binEntity.getLocation_id());
        bAdjustDetailVo.setBin_id(binEntity.getId());
        bAdjustDetailVo.setSku_code(entity.getSku_code());
        bAdjustDetailVo.setSku_id(entity.getSku_id());

        MInventoryVo inventoryVo = new MInventoryVo();
        inventoryVo.setOwner_id(bAdjustVo.getOwner_id());
        inventoryVo.setSku_id(bAdjustDetailVo.getSku_id());
        inventoryVo.setWarehouse_id(bAdjustDetailVo.getWarehouse_id());
        MInventoryVo mInventoryVo = imInventoryService.getInventoryInfo(inventoryVo);
        BigDecimal price = mInventoryVo.getPrice();

        bAdjustDetailVo.setQty(mInventoryVo.getQty_avaible());
        bAdjustDetailVo.setQty_adjust(mInventoryVo.getQty_avaible().subtract(entity.getQty()));
        bAdjustDetailVo.setQty_diff(BigDecimal.ZERO.subtract(entity.getQty()));
        bAdjustDetailVo.setAdjusted_price(price);
        bAdjustDetailVo.setAdjusted_rule(DictConstant.DICT_B_ADJUST_RULE_TWO);

        List<BAdjustDetailVo> bAdjustDetailVoList = new ArrayList<>();
        bAdjustDetailVoList.add(bAdjustDetailVo);

        bAdjustVo.setDetailList(bAdjustDetailVoList);
        ibAdjustDetailService.insertAudit(bAdjustVo);

        // 新库存信息生产库存调整
        bAdjustVo = new BAdjustVo();
        bAdjustVo.setOwner_id(bOwnerChangeVo.getIn_owner_id());
        bAdjustVo.setOwner_code(bOwnerChangeVo.getIn_owner_code());
        bAdjustVo.setRemark("货权转移-新库存信息");
        bAdjustVo.setType(DictConstant.DICT_B_ADJUST_TYPE_ONE);

        bAdjustDetailVo = new BAdjustDetailVo();
        binEntity = binMapper.selecBinByWarehouseId(bOwnerChangeVo.getIn_warehouse_id());

        bAdjustDetailVo.setWarehouse_id(bOwnerChangeVo.getIn_warehouse_id());
        bAdjustDetailVo.setLocation_id(binEntity.getLocation_id());
        bAdjustDetailVo.setBin_id(binEntity.getId());
        bAdjustDetailVo.setSku_code(entity.getSku_code());
        bAdjustDetailVo.setSku_id(entity.getSku_id());

        inventoryVo = new MInventoryVo();
        inventoryVo.setOwner_id(bAdjustVo.getOwner_id());
        inventoryVo.setSku_id(bAdjustDetailVo.getSku_id());
        inventoryVo.setWarehouse_id(bAdjustDetailVo.getWarehouse_id());
        mInventoryVo = imInventoryService.getInventoryInfo(inventoryVo);

        if (null == mInventoryVo) {
            bAdjustDetailVo.setQty(BigDecimal.ZERO);
            bAdjustDetailVo.setQty_adjust(entity.getQty());
            bAdjustDetailVo.setQty_diff(entity.getQty());
            bAdjustDetailVo.setAdjusted_price(price);
        } else {
            bAdjustDetailVo.setQty(mInventoryVo.getQty_avaible());
            bAdjustDetailVo.setQty_adjust(mInventoryVo.getQty_avaible().add(entity.getQty()));
            bAdjustDetailVo.setQty_diff(entity.getQty());
            bAdjustDetailVo.setAdjusted_price(mInventoryVo.getPrice());
        }
        bAdjustDetailVo.setAdjusted_rule(DictConstant.DICT_B_ADJUST_RULE_TWO);

        bAdjustDetailVoList = new ArrayList<>();
        bAdjustDetailVoList.add(bAdjustDetailVo);

        bAdjustVo.setDetailList(bAdjustDetailVoList);
        ibAdjustDetailService.insertAudit(bAdjustVo);

      // 生成已办
      todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_OWNERCHANGE_DETAIL, SystemConstants.PERMS.B_OWNER_CHANGE_AUDIT);

    }

  }

    /**
     * 删除
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<BOwnerChangeVo> searchCondition) {
        int updCount = 0;

        List<BOwnerChangeDetailEntity> list = mapper.selectIds(searchCondition);
        for(BOwnerChangeDetailEntity entity : list) {
            // check
            checkLogic(entity,CheckResultAo.DELETE_CHECK_TYPE);
            updCount = ownerChangeDetailMapper.deleteById(entity.getId());
            if(updCount == 0){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }
    }

    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(BOwnerChangeDetailEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.AUDIT_CHECK_TYPE:
                // 是否已提交状态
                if( Objects.equals(entity.getStatus(), DictConstant.DICT_B_OWNER_CHANGE_STATUS_PASSED)) {
                    throw new BusinessException("无法重复审核");
                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:
                // 是否已提交状态
                if( Objects.equals(entity.getStatus(), DictConstant.DICT_B_OWNER_CHANGE_STATUS_PASSED)) {
                    throw new BusinessException("审核已通过的数据无法删除");
                }
                break;
            default:
                break;
        }
        return CheckResultUtil.OK();
    }

}
