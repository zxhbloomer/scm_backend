package com.xinyirun.scm.core.system.serviceimpl.business.check;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.check.BCheckOperateDetailEntity;
import com.xinyirun.scm.bean.entity.business.check.BCheckOperateEntity;
import com.xinyirun.scm.bean.entity.business.check.BCheckResultDetailEntity;
import com.xinyirun.scm.bean.entity.business.check.BCheckResultEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.check.BCheckOperateDetailVo;
import com.xinyirun.scm.bean.system.vo.business.check.BCheckOperateVo;
import com.xinyirun.scm.bean.system.vo.master.customer.MOwnerVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.check.BCheckOperateDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.check.BCheckOperateMapper;
import com.xinyirun.scm.core.system.mapper.business.check.BCheckResultDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.check.BCheckResultMapper;
import com.xinyirun.scm.core.system.service.business.check.IBCheckOperateService;
import com.xinyirun.scm.core.system.service.master.customer.IMOwnerService;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 盘点 服务实现类
 * </p>
 *
 * @author wwl
 * @since 2021-12-29
 */
@Service
public class BCheckOperateServiceImpl extends ServiceImpl<BCheckOperateMapper, BCheckOperateEntity> implements IBCheckOperateService {

    @Autowired
    BCheckOperateMapper mapper;

    @Autowired
    BCheckOperateDetailMapper bCheckOperateDetailMapper;

    @Autowired
    BCheckOperateDetailServiceImpl bCheckOperateDetailService;

    @Autowired
    BCheckResultMapper bCheckResultMapper;

    @Autowired
    BCheckResultDetailMapper bCheckResultDetailMapper;

    @Autowired
    private IMOwnerService ownerService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(BCheckOperateVo vo) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(BCheckOperateVo vo) {
        BCheckOperateEntity entity = mapper.selectById(vo.getId());

        // check逻辑
        checkLogic(entity,CheckResultAo.OPERATE_CHECK_TYPE);

        BeanUtilsSupport.copyProperties(vo, entity);
        List<BCheckOperateDetailEntity> detailEntityList = new ArrayList<>();
        for (BCheckOperateDetailVo detailVo : vo.getDetailList()) {
            BCheckOperateDetailEntity detailEntity;
            if (null == detailVo.getId()) {
                detailEntity = new BCheckOperateDetailEntity();
            } else {
                detailEntity = bCheckOperateDetailMapper.selectById(detailVo.getId());
            }
            BeanUtilsSupport.copyProperties(detailVo, detailEntity);
            detailEntityList.add(detailEntity);
        }
        bCheckOperateDetailService.saveOrUpdateBatch(detailEntityList);

        int rtn = mapper.updateById(entity);
        return UpdateResultUtil.OK(rtn);
    }

    @Override
    public IPage<BCheckOperateVo> selectPage(BCheckOperateVo searchCondition) {
        // 分页条件
        Page<BCheckOperateEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    public BCheckOperateVo selectDetail(int id) {
        BCheckOperateEntity entity = mapper.selectById(id);
        BCheckOperateVo vo = new BCheckOperateVo();
        BeanUtilsSupport.copyProperties(entity, vo);

        // 查询仓库名称
        MOwnerVo mOwnerVo = ownerService.selectById(entity.getOwner_id());
        if (null != mOwnerVo) {
            vo.setOwner_name(mOwnerVo.getShort_name());
        }

        // 明细列表
        List<BCheckOperateDetailVo> detailList = bCheckOperateDetailMapper.selectList(vo);
        vo.setDetailList(detailList);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void start(List<BCheckOperateVo> searchCondition) {
        int updCount = 0;

        List<BCheckOperateEntity> list = mapper.selectIds(searchCondition);
        for (BCheckOperateEntity entity : list) {
            // check逻辑
            checkLogic(entity,CheckResultAo.START_CHECK_TYPE);

            entity.setStatus(DictConstant.DICT_B_CHECK_OPERATE_STATUS_CHECKING);

            updCount = mapper.updateById(entity);
            if(updCount == 0){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finish(List<BCheckOperateVo> searchCondition) {
        int updCount = 0;

        List<BCheckOperateEntity> list = mapper.selectIds(searchCondition);
        for (BCheckOperateEntity entity : list) {
            // check逻辑
            checkLogic(entity,CheckResultAo.FINISH_CHECK_TYPE);

            entity.setStatus(DictConstant.DICT_B_CHECK_OPERATE_STATUS_CHECKED);

            updCount = mapper.updateById(entity);
            if(updCount == 0){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            // 完成以后生成盘盈盘亏
            BCheckResultEntity checkResultEntity = new BCheckResultEntity();
            checkResultEntity.setOwner_id(entity.getOwner_id());
            checkResultEntity.setOwner_code(entity.getOwner_code());
            checkResultEntity.setWarehouse_id(entity.getWarehouse_id());
            checkResultEntity.setWarehouse_code(entity.getWarehouse_code());
            checkResultEntity.setStatus(DictConstant.DICT_B_CHECK_RESULT_STATUS_SAVED);
            checkResultEntity.setCheck_operate_id(entity.getId());
            bCheckResultMapper.insert(checkResultEntity);

            // 审核以后生成盘点操作单明细
            List<BCheckOperateDetailEntity> checkOperateDetailEntityList = bCheckOperateDetailMapper.selectListById(entity.getId());
            for (BCheckOperateDetailEntity checkOperateDetailEntity : checkOperateDetailEntityList) {
                BCheckResultDetailEntity checkResultDetailEntity = new BCheckResultDetailEntity();
                checkResultDetailEntity.setCheck_result_id(checkResultEntity.getId());
                checkResultDetailEntity.setInventory_id(checkOperateDetailEntity.getInventory_id());
                checkResultDetailEntity.setSku_id(checkOperateDetailEntity.getSku_id());
                checkResultDetailEntity.setQty(checkOperateDetailEntity.getQty());
                checkResultDetailEntity.setQty_check(checkOperateDetailEntity.getQty_check());
                checkResultDetailEntity.setQty_diff(checkOperateDetailEntity.getQty_diff());
                bCheckResultDetailMapper.insert(checkResultDetailEntity);
            }
        }
    }

    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(BCheckOperateEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.START_CHECK_TYPE:
                // 是否制单或者驳回状态
                if( !StringUtils.equals(entity.getStatus(), DictConstant.DICT_B_CHECK_OPERATE_STATUS_CHECK)) {
                    throw new BusinessException(entity.getCode()+":该单据状态不为【未盘点】,不可进行盘点启动操作");
                }
                break;
            case CheckResultAo.OPERATE_CHECK_TYPE:
                // 是否盘点中
                if (!StringUtils.equals(entity.getStatus(), DictConstant.DICT_B_CHECK_OPERATE_STATUS_CHECKING)) {
                    throw new BusinessException(entity.getCode()+":该单据状态不为【盘点中】,不可进行盘点操作！");
                }

                break;
            case CheckResultAo.FINISH_CHECK_TYPE:
                // 是否盘点中
                if (!StringUtils.equals(entity.getStatus(), DictConstant.DICT_B_CHECK_OPERATE_STATUS_CHECKING)) {
                    throw new BusinessException(entity.getCode()+":该单据状态不为【盘点中】,不可完成！");
                }
                break;
            default:
                break;
        }
        return CheckResultUtil.OK();
    }
}
