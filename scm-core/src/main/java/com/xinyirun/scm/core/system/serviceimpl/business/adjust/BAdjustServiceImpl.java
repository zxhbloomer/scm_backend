package com.xinyirun.scm.core.system.serviceimpl.business.adjust;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.adjust.BAdjustDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.adjust.BAdjustEntity;
import com.xinyirun.scm.bean.entity.busniess.inplan.BInPlanEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.vo.business.adjust.BAdjustVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.adjust.BAdjustDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.adjust.BAdjustMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.inventory.ICommonInventoryLogicService;
import com.xinyirun.scm.core.system.service.business.adjust.IBAdjustService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.business.todo.TodoService;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class BAdjustServiceImpl extends BaseServiceImpl<BAdjustMapper, BAdjustEntity> implements IBAdjustService {

    @Autowired
    private BAdjustMapper mapper;

    @Autowired
    private BAdjustDetailMapper adjustDetailMapper;

    @Autowired
    private ICommonInventoryLogicService iCommonInventoryLogicService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    /**
     * 查询分页列表
     */
    @Override
    public IPage<BAdjustVo> selectPage(BAdjustVo searchCondition) {
        // 分页条件
        Page<BInPlanEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        // 查询入库计划page
        IPage<BAdjustVo> result = mapper.selectPage(pageCondition, searchCondition);
        return result;
    }

    /**
     * 查询调整单数据
     */
    @Override
    public BAdjustVo get(BAdjustVo vo) {
        // 查询调整单page
        BAdjustVo adjustVo = mapper.get(vo.getId());
        // 查询调整单明细list
        adjustVo.setDetailList(adjustDetailMapper.getAdjustDetailList(adjustVo));

        if(adjustVo != null && adjustVo.getFiles_id() != null) {
            SFileEntity file = fileMapper.selectById(adjustVo.getFiles_id());
            List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id",file.getId()));
            adjustVo.setFiles(new ArrayList<>());
            for(SFileInfoEntity fileInfo:fileInfos) {
                SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                fileInfoVo.setFileName(fileInfoVo.getFile_name());
                adjustVo.getFiles().add(fileInfoVo);
            }
        }

        return adjustVo;
    }


    /**
     * id查询返回库存调整更新对象
     */
    @Override
    public List<BAdjustVo> selectById(int id) {
        return mapper.selectId(id);
    }

    /**
     * 审核提交
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public  void submit(List<BAdjustVo> searchCondition) {
        int updCount = 0;

        List<BAdjustDetailEntity> list = mapper.selectIds(searchCondition);
        for(BAdjustDetailEntity entity : list) {
            // check
            checkLogic(entity,CheckResultAo.SUBMIT_CHECK_TYPE);
            entity.setStatus(DictConstant.DICT_B_ADJUST_STATUS_SUBMITTED);
            entity.setE_dt(null);
            entity.setE_id(null);
            updCount = adjustDetailMapper.updateById(entity);
            if(updCount == 0){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_ADJUST_DETAIL, SystemConstants.PERMS.B_ADJUST_SUBMIT);

            // 生成待办
            todoService.insertTodo(entity.getId(), SystemConstants.SERIAL_TYPE.B_ADJUST_DETAIL, SystemConstants.PERMS.B_ADJUST_AUDIT);

        }
    }

    /**
     * 审核
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(List<BAdjustVo> searchCondition) {
        int updCount = 0;

        List<BAdjustDetailEntity> list = mapper.selectIds(searchCondition);
        for(BAdjustDetailEntity entity : list) {
            // check
            checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE);
            entity.setStatus(DictConstant.DICT_B_ADJUST_STATUS_PASSED);
            entity.setE_id(SecurityUtil.getUpdateUser_id().intValue());
            entity.setE_dt(LocalDateTime.now());
            updCount = adjustDetailMapper.updateById(entity);

            // 生成已办
            todoService.insertAlreadyDo(entity.getId(), SystemConstants.SERIAL_TYPE.B_ADJUST_DETAIL, SystemConstants.PERMS.B_ADJUST_AUDIT);

            // 计算库存
            iCommonInventoryLogicService.updWmsStockByAdjustBill(entity.getId());
            if(updCount == 0){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }
    }

    /**
     * 驳回
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<BAdjustVo> searchCondition) {
        int updCount = 0;

        List<BAdjustDetailEntity> list = mapper.selectIds(searchCondition);
        for(BAdjustDetailEntity entity : list) {
            // check
            checkLogic(entity,CheckResultAo.DELETE_CHECK_TYPE);
            updCount = adjustDetailMapper.deleteById(entity.getId());
            if(updCount == 0){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }
    }


    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(BAdjustDetailEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.SUBMIT_CHECK_TYPE:
                // 是否制单或者驳回状态
                if( !Objects.equals(entity.getStatus(), DictConstant.DICT_B_ADJUST_STATUS_SAVED) && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_ADJUST_STATUS_RETURN)) {
                    throw new BusinessException("无法提交，该单据不是制单或驳回状态");
                }
                break;
            case CheckResultAo.AUDIT_CHECK_TYPE:
                // 是否已提交状态
                if( !Objects.equals(entity.getStatus(), DictConstant.DICT_B_ADJUST_STATUS_SUBMITTED)) {
                    throw new BusinessException("无法审核，该单据不是已提交状态");
                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:
                // 是否已提交状态
                if(Objects.equals(entity.getStatus(), DictConstant.DICT_B_ADJUST_STATUS_PASSED)) {
                    throw new BusinessException("审核已通过的数据无法删除");
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }
}
