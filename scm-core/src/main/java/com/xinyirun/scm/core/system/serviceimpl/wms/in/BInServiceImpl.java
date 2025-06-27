package com.xinyirun.scm.core.system.serviceimpl.wms.in;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.busniess.in.BInAttachEntity;
import com.xinyirun.scm.bean.entity.busniess.in.BInEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.system.vo.wms.in.BInVo;
import com.xinyirun.scm.bean.system.vo.wms.inplan.BInPlanVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.bpm.service.business.IBpmInstanceSummaryService;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.mapper.wms.in.BInAttachMapper;
import com.xinyirun.scm.core.system.mapper.wms.in.BInMapper;
import com.xinyirun.scm.core.system.service.wms.in.IBInService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BInAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 入库单 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-26
 */
@Slf4j
@Service
public class BInServiceImpl extends ServiceImpl<BInMapper, BInEntity> implements IBInService {

    @Autowired
    private BInMapper mapper;

    @Autowired
    private BInAutoCodeServiceImpl bInAutoCodeService;
    
    @Autowired
    private BInAttachMapper bInAttachMapper;
    
    @Autowired
    private SFileMapper fileMapper;
    
    @Autowired
    private SFileInfoMapper fileInfoMapper;
    
    @Autowired
    private IBpmInstanceSummaryService iBpmInstanceSummaryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BInVo> startInsert(BInVo searchCondition) {
        return insert(searchCondition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BInVo> insert(BInVo bInVo) {
        // 1. 校验业务逻辑
        checkInsertLogic(bInVo);
        
        // 2. 保存主表信息
        BInEntity bInEntity = saveMainEntity(bInVo);
        
        // 3. 保存附件信息
        saveAttach(bInVo, bInEntity);
        
        // 4. 设置返回ID
        bInVo.setId(bInEntity.getId());
        
        return InsertResultUtil.OK(bInVo);
    }
    
    /**
     * 校验新增业务规则
     */
    private void checkInsertLogic(BInVo bInVo) {
        CheckResultAo cr = checkLogic(bInVo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }
    
    /**
     * 保存主表信息
     */
    private BInEntity saveMainEntity(BInVo bInVo) {
        BInEntity bInEntity = new BInEntity();
        BeanUtils.copyProperties(bInVo, bInEntity);
        bInEntity.setStatus(DictConstant.DICT_B_IN_STATUS_ONE);
        bInEntity.setCode(bInAutoCodeService.autoCode().getCode());
        bInEntity.setIs_del(Boolean.FALSE);
        
        int result = mapper.insert(bInEntity);
        if (result == 0) {
            throw new BusinessException("新增失败");
        }
        return bInEntity;
    }
    
    /**
     * 保存附件信息
     */
    private void saveAttach(BInVo bInVo, BInEntity bInEntity) {
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bInEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_IN);
        BInAttachEntity bInAttachEntity = insertFile(fileEntity, bInVo, new BInAttachEntity());
        bInAttachEntity.setIn_id(bInEntity.getId());
        int insert = bInAttachMapper.insert(bInAttachEntity);
        if (insert == 0) {
            throw new BusinessException("附件新增失败");
        }
    }
    
    /**
     * 附件文件处理
     */
    public BInAttachEntity insertFile(SFileEntity fileEntity, BInVo vo, BInAttachEntity extra) {
        // 入库单附件新增
        if (vo.getOne_file() != null && vo.getOne_file().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo doc_att_file : vo.getOne_file()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                doc_att_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(doc_att_file, fileInfoEntity);
                fileInfoEntity.setFile_name(doc_att_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 入库单附件id
            extra.setOne_file(fileEntity.getId());
            fileEntity.setId(null);
        } else {
            extra.setOne_file(null);
        }

        if (vo.getTwo_file() != null && vo.getTwo_file().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo doc_att_file : vo.getTwo_file()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                doc_att_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(doc_att_file, fileInfoEntity);
                fileInfoEntity.setFile_name(doc_att_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 入库单附件id
            extra.setTwo_file(fileEntity.getId());
            fileEntity.setId(null);
        } else {
            extra.setTwo_file(null);
        }

        if (vo.getThree_file() != null && vo.getThree_file().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo doc_att_file : vo.getThree_file()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                doc_att_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(doc_att_file, fileInfoEntity);
                fileInfoEntity.setFile_name(doc_att_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 入库单附件id
            extra.setThree_file(fileEntity.getId());
            fileEntity.setId(null);
        } else {
            extra.setThree_file(null);
        }

        if (vo.getFour_file() != null && vo.getFour_file().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo doc_att_file : vo.getFour_file()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                doc_att_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(doc_att_file, fileInfoEntity);
                fileInfoEntity.setFile_name(doc_att_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 入库单附件id
            extra.setFour_file(fileEntity.getId());
            fileEntity.setId(null);
        } else {
            extra.setFour_file(null);
        }


        return extra;
    }

    @Override
    public UpdateResultAo<BInVo> startUpdate(BInVo searchCondition) {
        return update(searchCondition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BInVo> update(BInVo bInVo) {
        // 1. 校验业务逻辑
        checkUpdateLogic(bInVo);
        
        // 2. 更新主表信息
        BInEntity bInEntity = new BInEntity();
        BeanUtils.copyProperties(bInVo, bInEntity);
        
        int result = mapper.updateById(bInEntity);
        if (result == 0) {
            throw new BusinessException("修改失败");
        }
        
        return UpdateResultUtil.OK(bInVo);
    }
    
    /**
     * 校验更新业务规则
     */
    private void checkUpdateLogic(BInVo bInVo) {
        CheckResultAo cr = checkLogic(bInVo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    @Override
    public IPage<BInVo> selectPage(BInVo searchCondition) {
        // 分页条件
        Page<BInVo> pageCondition = new Page<>(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    public BInVo selectById(Integer id) {
        if (id == null) {
            return null;
        }
        return mapper.selectById(id);
    }

    @Override
    public CheckResultAo checkLogic(BInVo bean, String checkType) {
        // 基础校验
        if (bean == null) {
            return CheckResultUtil.NG("入库单信息不能为空");
        }

        // 新增校验
        if (CheckResultAo.INSERT_CHECK_TYPE.equals(checkType)) {
            if (StringUtils.isEmpty(bean.getType())) {
                return CheckResultUtil.NG("入库类型不能为空");  
            }
            if (bean.getOwner_id() == null) {
                return CheckResultUtil.NG("货主不能为空");
            }
            if (bean.getSku_id() == null) {
                return CheckResultUtil.NG("商品规格编号不能为空");
            }
            if (bean.getWarehouse_id() == null) {
                return CheckResultUtil.NG("仓库不能为空");
            }
            if (bean.getQty() == null) {
                return CheckResultUtil.NG("入库数量不能为空");
            }
            if (bean.getInbound_time() == null) {
                return CheckResultUtil.NG("入库时间不能为空");
            }
            if (bean.getPrice() == null) {
                return CheckResultUtil.NG("入库单价不能为空");
            }
        }

        // 更新校验
        if (CheckResultAo.UPDATE_CHECK_TYPE.equals(checkType)) {
            if (bean.getId() == null) {
                return CheckResultUtil.NG("入库单ID不能为空");
            }
            if (bean.getDbversion() == null) {
                return CheckResultUtil.NG("数据版本不能为空");
            }
        }
        
        // 删除校验
        if (CheckResultAo.DELETE_CHECK_TYPE.equals(checkType)) {
            if (bean.getId() == null) {
                return CheckResultUtil.NG("入库单ID不能为空");
            }
            
            BInEntity bInEntity = (BInEntity) ((BaseMapper)mapper).selectById(bean.getId());
            if (bInEntity == null) {
                return CheckResultUtil.NG("单据不存在");
            }
            
            // 只有待审批或审批拒绝状态的单据才能删除
            if (!"0".equals(bInEntity.getStatus()) && !"3".equals(bInEntity.getStatus())) {
                return CheckResultUtil.NG("只有待审批或审批拒绝状态的单据才能删除");
            }
        }
        
        // 作废校验
        if (CheckResultAo.CANCEL_CHECK_TYPE.equals(checkType)) {
            if (bean.getId() == null) {
                return CheckResultUtil.NG("入库单ID不能为空");
            }
            
            BInEntity bInEntity = (BInEntity) ((BaseMapper)mapper).selectById(bean.getId());
            if (bInEntity == null) {
                return CheckResultUtil.NG("单据不存在");
            }
            
            // 只有审批通过状态的单据才能作废
            if (!"2".equals(bInEntity.getStatus())) {
                return CheckResultUtil.NG("只有审批通过状态的单据才能作废");
            }
        }
        
        // 完成校验
        if (CheckResultAo.FINISH_CHECK_TYPE.equals(checkType)) {
            if (bean.getId() == null) {
                return CheckResultUtil.NG("入库单ID不能为空");
            }
            
            BInEntity bInEntity = (BInEntity) ((BaseMapper)mapper).selectById(bean.getId());
            if (bInEntity == null) {
                return CheckResultUtil.NG("单据不存在");
            }
            
            // 只有审批通过状态的单据才能完成
            if (!"2".equals(bInEntity.getStatus())) {
                return CheckResultUtil.NG("只有审批通过状态的单据才能完成");
            }
        }

        return CheckResultUtil.OK();
    }

    @Override
    public List<BInVo> selectExportList(BInVo param) {
        return mapper.selectExportList(param);
    }

    @Override
    public BInVo querySum(BInVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    @Override
    public BInVo getPrintInfo(BInVo searchCondition) {
        return selectById(searchCondition.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<BInVo> searchCondition) {
        try {
            if (CollectionUtil.isEmpty(searchCondition)) {
                throw new BusinessException("删除数据不能为空");
            }

            int count = 0;
            for (BInVo vo : searchCondition) {
                if (vo.getId() != null) {
                    // 删除前check
                    CheckResultAo cr = checkLogic(vo, CheckResultAo.DELETE_CHECK_TYPE);
                    if (!cr.isSuccess()) {
                        throw new BusinessException(cr.getMessage());
                    }
                    
                    // 逻辑删除
                    BInEntity entity = new BInEntity();
                    entity.setId(vo.getId());
                    entity.setIs_del(true);
                    
                    int result = mapper.updateById(entity);
                    if (result > 0) {
                        count++;
                    }
                }
            }

            if (count > 0) {
                return DeleteResultUtil.OK(count);
            }
            throw new BusinessException("删除失败");
        } catch (Exception e) {
            log.error("入库单删除失败", e);
            throw new BusinessException("删除失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(BInVo bInVo) {
        // 删除前check
        CheckResultAo cr = checkLogic(bInVo, CheckResultAo.DELETE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        
        // 删除主表
        int result = mapper.deleteById(bInVo.getId());
        if (result == 0) {
            throw new BusinessException("删除失败");
        }
        
        return DeleteResultUtil.OK(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> cancel(BInVo searchCondition) {
        // 作废前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        
        BInEntity bInEntity = (BInEntity) ((BaseMapper)mapper).selectById(searchCondition.getId());
        
        // 设置作废状态
        bInEntity.setStatus("4"); // 设置为作废待审批状态
        int result = mapper.updateById(bInEntity);
        if (result == 0) {
            throw new BusinessException("修改失败");
        }
        
        return UpdateResultUtil.OK(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> finish(BInVo searchCondition) {
        // 完成前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.FINISH_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        
        BInEntity bInEntity = (BInEntity) ((BaseMapper)mapper).selectById(searchCondition.getId());
        bInEntity.setStatus("6"); // 设置为完成状态
        int update = mapper.updateById(bInEntity);
        if (update == 0) {
            throw new BusinessException("修改失败");
        }
        
        return UpdateResultUtil.OK(update);
    }

    /**
     * check批次是否重复
     *
     * @param lot
     * @return
     */
    @Override
    public boolean isDuplicate(String lot) {
        if (org.apache.commons.lang.StringUtils.isEmpty(lot)) {
            return false;
        }
        Integer count = mapper.countLot(lot);
        if (count == null) {
            return false;
        }
        // 发生重复
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * 悲观锁
     *
     * @param id
     * @return
     */
    @Override
    public BInEntity setBillInForUpdate(Integer id) {
        return mapper.setBillInForUpdate(id);
    }

    // ========== BPM回调方法 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(BInVo searchCondition) {
        log.debug("===》审批流程创建成功，更新开始《===");
        BInVo bInVo = selectById(searchCondition.getId());
        
        /**
         * 1、更新bpm_instance的摘要数据
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("入库单编号：", bInVo.getCode());
        jsonObject.put("入库时间：", bInVo.getInbound_time());
        jsonObject.put("类型：", bInVo.getType_name());
        
        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(bInVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);
        
        return UpdateResultUtil.OK(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(BInVo searchCondition) {
        log.debug("===》入库单[{}]审批流程更新最新审批人，更新开始《===", searchCondition.getId());
        
        BInEntity bInEntity = (BInEntity) ((BaseMapper)mapper).selectById(searchCondition.getId());
        bInEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bInEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bInEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bInEntity);
        
        log.debug("===》入库单[{}]审批流程更新最新审批人,更新结束《===", searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BInVo searchCondition) {
        // 作废流程创建逻辑
        log.debug("===》入库单[{}]作废流程创建成功《===", searchCondition.getId());
        return UpdateResultUtil.OK(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(BInVo searchCondition) {
        log.debug("===》入库单[{}]作废审批流程通过，更新开始《===", searchCondition.getId());
        BInEntity bInEntity = (BInEntity) ((BaseMapper)mapper).selectById(searchCondition.getId());
        
        bInEntity.setBpm_cancel_instance_id(searchCondition.getBpm_cancel_instance_id());
        bInEntity.setBpm_cancel_instance_code(searchCondition.getBpm_cancel_instance_code());
        bInEntity.setStatus("5"); // 设置为已作废状态
        
        int result = mapper.updateById(bInEntity);
        if (result == 0) {
            throw new BusinessException("更新作废状态失败");
        }
        
        log.debug("===》入库单[{}]作废审批流程通过,更新结束《===", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(BInVo searchCondition) {
        log.debug("===》入库单[{}]作废审批流程拒绝，更新开始《===", searchCondition.getId());
        BInEntity bInEntity = (BInEntity) ((BaseMapper)mapper).selectById(searchCondition.getId());
        
        // 作废拒绝，恢复到正常状态
        bInEntity.setStatus("2"); // 恢复到审批通过状态
        
        int result = mapper.updateById(bInEntity);
        if (result == 0) {
            throw new BusinessException("更新状态失败");
        }
        
        log.debug("===》入库单[{}]作废审批流程拒绝,更新结束《===", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(BInVo searchCondition) {
        log.debug("===》入库单[{}]作废审批流程取消，更新开始《===", searchCondition.getId());
        BInEntity bInEntity = (BInEntity) ((BaseMapper)mapper).selectById(searchCondition.getId());
        
        // 作废取消，恢复到正常状态
        bInEntity.setStatus("2"); // 恢复到审批通过状态
        
        int result = mapper.updateById(bInEntity);
        if (result == 0) {
            throw new BusinessException("更新状态失败");
        }
        
        log.debug("===》入库单[{}]作废审批流程取消,更新结束《===", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(BInVo searchCondition) {
        log.debug("===》入库单[{}]作废流程保存最新审批人《===", searchCondition.getId());
        BInEntity bInEntity = (BInEntity) ((BaseMapper)mapper).selectById(searchCondition.getId());
        
        bInEntity.setBpm_cancel_instance_id(searchCondition.getBpm_cancel_instance_id());
        bInEntity.setBpm_cancel_instance_code(searchCondition.getBpm_cancel_instance_code());
        bInEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        
        int result = mapper.updateById(bInEntity);
        log.debug("===》入库单[{}]作废流程保存最新审批人结束《===", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }
    
    // ============ BPM 审批回调方法实现 ============

    /**
     * 审批流程回调 - 审批通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(BInVo searchCondition) {
        log.debug("====》入库单[{}]审批流程通过，更新开始《====", searchCondition.getId());
        BInEntity bInEntity = (BInEntity) ((BaseMapper)mapper).selectById(searchCondition.getId());

        bInEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bInEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bInEntity.setStatus("2"); // 设置为审批通过状态
        bInEntity.setNext_approve_name("审批完成");

        int result = mapper.updateById(bInEntity);
        if (result == 0) {
            throw new BusinessException("更新审核状态失败");
        }

        log.debug("====》入库单[{}]审批流程通过,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 审批流程回调 - 审批拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(BInVo searchCondition) {
        log.debug("====》入库单[{}]审批流程拒绝，更新开始《====", searchCondition.getId());
        BInEntity bInEntity = (BInEntity) ((BaseMapper)mapper).selectById(searchCondition.getId());

        bInEntity.setStatus("3"); // 设置为审批拒绝状态
        bInEntity.setNext_approve_name("审批拒绝");

        int result = mapper.updateById(bInEntity);
        if (result == 0) {
            throw new BusinessException("更新审核状态失败");
        }

        log.debug("====》入库单[{}]审批流程拒绝,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 审批流程回调 - 审批取消
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(BInVo searchCondition) {
        log.debug("====》入库单[{}]审批流程取消，更新开始《====", searchCondition.getId());
        BInEntity bInEntity = (BInEntity) ((BaseMapper)mapper).selectById(searchCondition.getId());

        bInEntity.setStatus("0"); // 设置为待审批状态
        bInEntity.setNext_approve_name("审批取消");

        int result = mapper.updateById(bInEntity);
        if (result == 0) {
            throw new BusinessException("更新审核状态失败");
        }

        log.debug("====》入库单[{}]审批流程取消,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }
}
