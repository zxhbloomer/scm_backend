package com.xinyirun.scm.core.system.serviceimpl.business.check;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.check.BCheckResultDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.check.BCheckResultEntity;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.vo.business.adjust.BAdjustVo;
import com.xinyirun.scm.bean.system.vo.business.check.BCheckResultVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.mapper.business.check.BCheckResultDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.check.BCheckResultMapper;
import com.xinyirun.scm.core.system.mapper.master.inventory.MInventoryMapper;
import com.xinyirun.scm.core.system.service.business.check.IBCheckResultService;
import com.xinyirun.scm.core.system.serviceimpl.business.adjust.BAdjustDetailServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 盘盈盘亏 服务实现类
 * </p>
 *
 * @author wwl
 * @since 2021-12-29
 */
@Service
public class BCheckResultServiceImpl extends ServiceImpl<BCheckResultMapper, BCheckResultEntity> implements IBCheckResultService {

    @Autowired
    private BCheckResultMapper bCheckResultMapper;

    @Autowired
    private BCheckResultDetailMapper bCheckResultDetailMapper;

    @Autowired
    private MInventoryMapper inventoryMapper;

    @Autowired
    private BAdjustDetailServiceImpl bAdjustDetailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(BCheckResultVo vo) {
        return null;
    }

    @Override
    public IPage<BCheckResultVo> selectPage(BCheckResultVo searchCondition) {
        // 分页条件
        Page<BCheckResultEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return bCheckResultMapper.selectPage(pageCondition ,searchCondition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(List<BCheckResultVo> searchCondition) {
        List<BCheckResultEntity> list = bCheckResultMapper.selectIds(searchCondition);

        for(BCheckResultEntity entity : list) {
            // check
            checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE);
            entity.setStatus(DictConstant.DICT_B_CHECK_RESULT_STATUS_PASSED);
            entity.setE_id(SecurityUtil.getUpdateUser_id().intValue());
            entity.setE_time(LocalDateTime.now());
            int updCount = bCheckResultMapper.updateById(entity);

            if(updCount == 0){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            List<BCheckResultDetailEntity> checkResultDetailEntityList = bCheckResultDetailMapper.selectList(entity.getId());
            for (BCheckResultDetailEntity checkResultDetailEntity : checkResultDetailEntityList) {
                // 审核以后生库存调整单

                // 库存信息
                MInventoryEntity inventoryEntity = inventoryMapper.selectById(checkResultDetailEntity.getInventory_id());
                BAdjustVo adjustVo = new BAdjustVo();
                adjustVo.setOwner_id(entity.getOwner_id());
                adjustVo.setOwner_code(entity.getOwner_code());
                adjustVo.setWarehouse_id(entity.getWarehouse_id());

                adjustVo.setSku_id(inventoryEntity.getSku_id());
                adjustVo.setLocation_id(inventoryEntity.getLocation_id());
                adjustVo.setBin_id(inventoryEntity.getBin_id());
                adjustVo.setSku_id(inventoryEntity.getSku_id());
                adjustVo.setQty(checkResultDetailEntity.getQty());
                adjustVo.setQty_adjust(checkResultDetailEntity.getQty_check());
                bAdjustDetailService.insert(adjustVo);
            }

        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(List<BCheckResultVo> searchCondition) {
        List<BCheckResultEntity> list = bCheckResultMapper.selectIds(searchCondition);

        for(BCheckResultEntity entity : list) {
            // check
            checkLogic(entity, CheckResultAo.CANCEL_CHECK_TYPE);
            entity.setStatus(DictConstant.DICT_B_CHECK_RESULT_STATUS_CANCEL);
            int updCount = bCheckResultMapper.updateById(entity);

            if(updCount == 0){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }
    }

    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(BCheckResultEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.AUDIT_CHECK_TYPE:
                // 是否已提交状态
                if( !Objects.equals(entity.getStatus(), DictConstant.DICT_B_IN_PLAN_STATUS_ZERO)) {
                    throw new BusinessException(entity.getCode()+":无法审核，该单据制单状态");
                }
                break;
            case CheckResultAo.CANCEL_CHECK_TYPE:
                // 是否已经作废
                if(Objects.equals(entity.getStatus(), DictConstant.DICT_B_CHECK_STATUS_CANCEL)) {
                    throw new BusinessException(entity.getCode()+":无法重复作废");
                }

                // 是否已经审核
                if(Objects.equals(entity.getStatus(), DictConstant.DICT_B_CHECK_STATUS_PASSED)) {
                    throw new BusinessException(entity.getCode()+":审核通过的单据无法作废");
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 是否已提交状态
                if(!Objects.equals(entity.getStatus(), DictConstant.DICT_B_IN_PLAN_STATUS_ZERO)) {
                    throw new BusinessException(entity.getCode()+":无法驳回，该单据不是制单状态");
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }
}
