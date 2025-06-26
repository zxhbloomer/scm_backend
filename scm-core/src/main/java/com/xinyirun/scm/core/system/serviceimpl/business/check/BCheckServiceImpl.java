package com.xinyirun.scm.core.system.serviceimpl.business.check;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.check.BCheckDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.check.BCheckEntity;
import com.xinyirun.scm.bean.entity.busniess.check.BCheckOperateDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.check.BCheckOperateEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.vo.business.check.BCheckDetailVo;
import com.xinyirun.scm.bean.system.vo.business.check.BCheckVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.check.BCheckDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.check.BCheckMapper;
import com.xinyirun.scm.core.system.mapper.business.check.BCheckOperateDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.check.BCheckOperateMapper;
import com.xinyirun.scm.core.system.service.business.check.IBCheckService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BCheckAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BCheckOperateAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 盘点 服务实现类
 * </p>
 *
 * @author wwl
 * @since 2021-12-27
 */
@Service
public class BCheckServiceImpl extends BaseServiceImpl<BCheckMapper, BCheckEntity> implements IBCheckService {

    @Autowired
    private BCheckMapper bCheckMapper;

    @Autowired
    private BCheckDetailMapper bCheckDetailMapper;

    @Autowired
    private BCheckOperateMapper bCheckOperateMapper;

    @Autowired
    private BCheckOperateDetailMapper bCheckoperateDetailMapper;

    @Autowired
    private BCheckAutoCodeServiceImpl autoCodeService;

    @Autowired
    private BCheckOperateAutoCodeServiceImpl checkOperateAutoCodeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(BCheckVo vo) {

        BCheckEntity entity = new BCheckEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        entity.setStatus(DictConstant.DICT_B_CHECK_STATUS_SAVED);
        // 自动生成code
        entity.setCode(autoCodeService.autoCode().getCode());
        bCheckMapper.insert(entity);

        for (BCheckDetailVo detailVo : vo.getDetailList()) {
            BCheckDetailEntity detailEntity = new BCheckDetailEntity();
            BeanUtilsSupport.copyProperties(detailVo, detailEntity, new String[]{"id"});
            detailEntity.setCheck_id(entity.getId());
            bCheckDetailMapper.insert(detailEntity);
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(BCheckVo vo) {
        BCheckEntity entity = new BCheckEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        bCheckMapper.updateById(entity);

        for (BCheckDetailVo detailVo : vo.getDetailList()) {
            BCheckDetailEntity detailEntity = new BCheckDetailEntity();
            BeanUtilsSupport.copyProperties(detailVo, detailEntity);
            bCheckDetailMapper.updateById(detailEntity);
        }

        return null;
    }

    @Override
    public IPage<BCheckVo> selectPage(BCheckVo searchCondition) {
        // 分页条件
        Page<BCheckEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return bCheckMapper.selectPage(pageCondition ,searchCondition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(List<BCheckVo> searchCondition) {
        List<BCheckEntity> list = bCheckMapper.selectIds(searchCondition);

        for(BCheckEntity entity : list) {
            // check
            checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE);
            entity.setStatus(DictConstant.DICT_B_CHECK_STATUS_PASSED);
            entity.setE_id(SecurityUtil.getUpdateUser_id().intValue());
            entity.setE_time(LocalDateTime.now());
            int updCount = bCheckMapper.updateById(entity);

            if(updCount == 0){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }


            // 审核以后生成盘点操作单
            BCheckOperateEntity operateEntity = new BCheckOperateEntity();
            operateEntity.setOwner_id(entity.getOwner_id());
            operateEntity.setOwner_code(entity.getOwner_code());
            operateEntity.setStatus(DictConstant.DICT_B_CHECK_OPERATE_STATUS_CHECK);
            operateEntity.setWarehouse_id(entity.getWarehouse_id());
            operateEntity.setWarehouse_code(entity.getWarehouse_code());
            operateEntity.setCode(checkOperateAutoCodeService.autoCode().getCode());

            bCheckOperateMapper.insert(operateEntity);

            // 审核以后生成盘点操作单明细
            List<BCheckDetailEntity> bCheckDetailEntityList = bCheckDetailMapper.selectList(entity.getId());
            for (BCheckDetailEntity detailEntity : bCheckDetailEntityList) {
                BCheckOperateDetailEntity operateDetailEntity = new BCheckOperateDetailEntity();
                operateDetailEntity.setCheck_operate_id(operateEntity.getId());
                operateDetailEntity.setSku_id(detailEntity.getSku_id());
                operateDetailEntity.setInventory_id(detailEntity.getInventory_id());
                operateDetailEntity.setQty(detailEntity.getQty());
                operateDetailEntity.setQty_check(BigDecimal.ZERO);
                operateDetailEntity.setQty_diff(BigDecimal.ZERO);
                bCheckoperateDetailMapper.insert(operateDetailEntity);
            }

        }
    }

    @Override
    public void cancel(List<BCheckVo> searchCondition) {

    }

    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(BCheckEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.AUDIT_CHECK_TYPE:
                // 是否已提交状态
                if( !Objects.equals(entity.getStatus(), DictConstant.DICT_B_CHECK_STATUS_SAVED)) {
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
