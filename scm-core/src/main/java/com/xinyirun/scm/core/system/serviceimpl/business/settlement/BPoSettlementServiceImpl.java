package com.xinyirun.scm.core.system.serviceimpl.business.settlement;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.busniess.pocontract.BPoContractAttachEntity;
import com.xinyirun.scm.bean.entity.busniess.settlement.BPoSettlementAttachEntity;
import com.xinyirun.scm.bean.entity.busniess.settlement.BPoSettlementDetailSourceEntity;
import com.xinyirun.scm.bean.entity.busniess.settlement.BPoSettlementDetailSourceInboundEntity;
import com.xinyirun.scm.bean.entity.busniess.settlement.BPoSettlementEntity;
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
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.bean.system.vo.business.settlement.BPoSettlementDetailSourceInboundVo;
import com.xinyirun.scm.bean.system.vo.business.settlement.BPoSettlementDetailSourceVo;
import com.xinyirun.scm.bean.system.vo.business.settlement.BPoSettlementVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.bpm.service.business.IBpmInstanceSummaryService;
import com.xinyirun.scm.core.bpm.serviceimpl.business.BpmProcessTemplatesServiceImpl;
import com.xinyirun.scm.core.system.mapper.business.settlement.BPoSettlementAttachMapper;
import com.xinyirun.scm.core.system.mapper.business.settlement.BPoSettlementDetailSourceInboundMapper;
import com.xinyirun.scm.core.system.mapper.business.settlement.BPoSettlementDetailSourceMapper;
import com.xinyirun.scm.core.system.mapper.business.settlement.BPoSettlementMapper;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.business.settlement.IBPoSettlementService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 采购结算表 服务实现类
 */
@Slf4j
@Service
public class BPoSettlementServiceImpl extends BaseServiceImpl<BPoSettlementMapper, BPoSettlementEntity> implements IBPoSettlementService {

    @Autowired
    private BPoSettlementMapper mapper;

    @Autowired
    private BPoSettlementDetailSourceMapper bPoSettlementDetailSourceMapper;

    @Autowired
    private BPoSettlementDetailSourceInboundMapper bPoSettlementDetailSourceInboundMapper;

    @Autowired
    private BPoSettlementAttachMapper bPoSettlementAttachMapper;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private ISFileService isFileService;

    @Autowired
    private BpmProcessTemplatesServiceImpl bpmProcessTemplatesService;

    @Autowired
    private ISPagesService isPagesService;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private IBpmInstanceSummaryService iBpmInstanceSummaryService;

    @Autowired
    private MCancelService mCancelService;

    @Autowired
    private MStaffMapper mStaffMapper;

    /**
     * 获取业务类型
     */
    @Override
    public List<BPoSettlementVo> getType() {
        List<BPoSettlementVo> typeList = new ArrayList<>();
        // 返回采购结算的业务类型
        return typeList;
    }

    /**
     * 新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BPoSettlementVo> startInsert(BPoSettlementVo searchCondition) {
        // 1.保存业务数据
        InsertResultAo<BPoSettlementVo> insertResultAo = insert(searchCondition);

        // 2.启动审批流程
        startFlowProcess(searchCondition, SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_SETTLEMENT);

        return insertResultAo;
    }

    /**
     * 内部新增方法
     */
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BPoSettlementVo> insert(BPoSettlementVo searchCondition) {
        // 1. 校验业务规则
        checkInsertLogic(searchCondition);
        
        // 2.保存主表信息
        BPoSettlementEntity entity = saveMainEntity(searchCondition);
        
        // 3.保存详情信息
        saveDetailList(searchCondition, entity);
        
        // 4.保存附件信息
        saveAttach(searchCondition, entity);

        searchCondition.setId(entity.getId());
        return InsertResultUtil.OK(searchCondition);
    }

    /**
     * 校验新增业务规则
     */
    private void checkInsertLogic(BPoSettlementVo searchCondition) {
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 保存主表信息
     */
    private BPoSettlementEntity saveMainEntity(BPoSettlementVo searchCondition) {
        BPoSettlementEntity entity = new BPoSettlementEntity();
        BeanUtils.copyProperties(searchCondition, entity);
        entity.setStatus(DictConstant.DICT_B_PO_SETTLEMENT_STATUS_ONE);
        entity.setIs_del(Boolean.FALSE);
        entity.setBpm_process_name("新增采购结算审批");
        entity.setId(null);
        
        int result = mapper.insert(entity);
        if (result <= 0) {
            throw new BusinessException("新增失败");
        }
        return entity;
    }

    /**
     * 保存详情信息
     */
    private void saveDetailList(BPoSettlementVo searchCondition, BPoSettlementEntity entity) {

    }

    /**
     * 保存附件信息
     */
    private void saveAttach(BPoSettlementVo searchCondition, BPoSettlementEntity entity) {
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(entity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_SETTLEMENT);
        BPoSettlementAttachEntity bPoSettlementAttachEntity = insertFile(fileEntity, searchCondition, new BPoSettlementAttachEntity());
        bPoSettlementAttachEntity.setPo_settlement_id(entity.getId());
        int insert = bPoSettlementAttachMapper.insert(bPoSettlementAttachEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }
    }

    /**
     * 更新
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BPoSettlementVo> startUpdate(BPoSettlementVo searchCondition) {
        // 1.执行更新逻辑
        UpdateResultAo<BPoSettlementVo> updateResultAo = update(searchCondition);

        // 2.启动审批流程
        startFlowProcess(searchCondition, SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_SETTLEMENT);

        return updateResultAo;
    }

    /**
     * 内部更新方法
     */
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BPoSettlementVo> update(BPoSettlementVo searchCondition) {
        // 1. 更新主表信息
        BPoSettlementEntity entity = updateMainEntity(searchCondition);
        
        // 2. 更新详情信息
        updateDetailList(searchCondition, entity);
        
        // 3. 更新附件信息
        updateAttach(searchCondition, entity);

        return UpdateResultUtil.OK(searchCondition);
    }

    /**
     * 校验更新业务规则
     */
    private void checkUpdateLogic(BPoSettlementVo searchCondition) {
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 更新主表信息
     */
    private BPoSettlementEntity updateMainEntity(BPoSettlementVo searchCondition) {
        BPoSettlementEntity entity = (BPoSettlementEntity) BeanUtilsSupport.copyProperties(searchCondition, BPoSettlementEntity.class);
        entity.setStatus(DictConstant.DICT_B_PO_SETTLEMENT_STATUS_ONE);
        entity.setBpm_process_name("修改采购结算审批");

        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return entity;
    }

    /**
     * 更新详情信息
     */
    private void updateDetailList(BPoSettlementVo searchCondition, BPoSettlementEntity entity) {

    }

    /**
     * 更新附件信息
     */
    private void updateAttach(BPoSettlementVo searchCondition, BPoSettlementEntity entity) {

    }

    /**
     * 分页查询
     */
    @Override
    public IPage<BPoSettlementVo> selectPage(BPoSettlementVo searchCondition) {
        // 分页条件
        Page<BPoSettlementVo> pageCondition = new Page<>(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询分页数据
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 根据id查询
     */
    @Override
    public BPoSettlementVo selectById(Integer id) {
        BPoSettlementVo vo = mapper.selectById(id);
        
        if (vo != null) {
            // 其他附件信息
            List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(vo.getDoc_att_file());
            vo.setDoc_att_files(doc_att_files);

            // 查询是否存在作废记录
            if (DictConstant.DICT_B_PO_SETTLEMENT_STATUS_FOUR.equals(vo.getStatus()) || 
                Objects.equals(vo.getStatus(), DictConstant.DICT_B_PO_SETTLEMENT_STATUS_FIVE)) {
                MCancelVo serialIdAndType = new MCancelVo();
                serialIdAndType.setSerial_id(vo.getId());
                serialIdAndType.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_SETTLEMENT);
                MCancelVo mCancelVo = mCancelService.selectBySerialIdAndType(serialIdAndType);
                
                if (mCancelVo != null) {
                    // 作废理由
                    vo.setCancel_reason(mCancelVo.getRemark());
                    // 作废附件信息
                    if (mCancelVo.getFile_id() != null) {
                        List<SFileInfoVo> cancel_doc_att_files = isFileService.selectFileInfo(mCancelVo.getFile_id());
                        vo.setCancel_doc_att_files(cancel_doc_att_files);
                    }

                    // 通过表m_staff获取作废提交人名称
                    MStaffVo searchCondition = new MStaffVo();
                    searchCondition.setId(mCancelVo.getC_id());
                    vo.setCancel_name(mStaffMapper.selectByid(searchCondition).getName());

                    // 作废时间
                    vo.setCancel_time(mCancelVo.getC_time());
                }
            }
        }
        
        return vo;
    }

    /**
     * 校验
     */
    @Override
    public CheckResultAo checkLogic(BPoSettlementVo searchCondition, String checkType) {
        BPoSettlementEntity entity = null;
        
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:

                break;
                
            case CheckResultAo.UPDATE_CHECK_TYPE:

                break;
                
            case CheckResultAo.DELETE_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                break;
                
            case CheckResultAo.CANCEL_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                break;
                
            case CheckResultAo.FINISH_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                break;
                
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 导出查询
     */
    @Override
    public List<BPoSettlementVo> selectExportList(BPoSettlementVo param) {
        return mapper.selectExportList(param);
    }

    /**
     * 获取报表系统参数，并组装打印参数
     */
    @Override
    public BPoSettlementVo getPrintInfo(BPoSettlementVo searchCondition) {
        // 获取打印所需的基础数据
        BPoSettlementVo printInfo = selectById(searchCondition.getId());
        
        // 组装打印参数
        // 可以在这里添加报表相关的参数设置
        
        return printInfo;
    }

    /**
     * 删除
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<BPoSettlementVo> searchCondition) {
        for (BPoSettlementVo vo : searchCondition) {
            // 校验删除条件
            CheckResultAo checkResult = checkLogic(vo, CheckResultAo.DELETE_CHECK_TYPE);
            if (!checkResult.isSuccess()) {
                throw new BusinessException(checkResult.getMessage());
            }
            
            // 执行逻辑删除
            BPoSettlementEntity entity = new BPoSettlementEntity();
            entity.setId(vo.getId());
            entity.setIs_del(Boolean.TRUE);
            
            int result = mapper.updateById(entity);
            if (result <= 0) {
                throw new UpdateErrorException("删除失败");
            }
        }
        
        return DeleteResultUtil.OK(1);
    }

    /**
     * 作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> cancel(BPoSettlementVo searchCondition) {
        // 校验作废条件
        CheckResultAo checkResult = checkLogic(searchCondition, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!checkResult.isSuccess()) {
            throw new BusinessException(checkResult.getMessage());
        }
        
        // 执行作废逻辑
        BPoSettlementEntity entity = new BPoSettlementEntity();
        entity.setId(searchCondition.getId());
        entity.setStatus(DictConstant.DICT_B_PO_SETTLEMENT_STATUS_FOUR);
        
        int result = mapper.updateById(entity);
        if (result <= 0) {
            throw new UpdateErrorException("作废失败");
        }
        
        return UpdateResultUtil.OK(1);
    }

    /**
     * 汇总查询
     */
    @Override
    public BPoSettlementVo querySum(BPoSettlementVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    /**
     * 完成
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> finish(BPoSettlementVo searchCondition) {
        // 校验完成条件
        CheckResultAo checkResult = checkLogic(searchCondition, CheckResultAo.FINISH_CHECK_TYPE);
        if (!checkResult.isSuccess()) {
            throw new BusinessException(checkResult.getMessage());
        }
        
        // 执行完成逻辑
        BPoSettlementEntity entity = new BPoSettlementEntity();
        entity.setId(searchCondition.getId());
        entity.setStatus(DictConstant.DICT_B_PO_SETTLEMENT_STATUS_SIX);
        
        int result = mapper.updateById(entity);
        if (result <= 0) {
            throw new UpdateErrorException("完成失败");
        }
        
        return UpdateResultUtil.OK(1);
    }

    /**
     * 启动审批流
     */
    public void startFlowProcess(BPoSettlementVo bean, String type) {
        // 未初始化审批流数据，不启动审批流
        if (StringUtils.isNotEmpty(bean.getInitial_process())) {
            // 启动审批流
            BBpmProcessVo bBpmProcessVo = new BBpmProcessVo();
            bBpmProcessVo.setCode(bpmProcessTemplatesService.getBpmFLowCodeByType(type));
            bBpmProcessVo.setSerial_type(type);
            bBpmProcessVo.setForm_data(bean.getForm_data());
            bBpmProcessVo.setForm_json(bean);
            bBpmProcessVo.setForm_class(bean.getClass().getName());
            bBpmProcessVo.setSerial_id(bean.getId());
            bBpmProcessVo.setInitial_process(bean.getInitial_process());
            bBpmProcessVo.setProcess_users(bean.getProcess_users());

            // 组装发起人信息
            OrgUserVo orgUserVo = new OrgUserVo();
            orgUserVo.setId(SecurityUtil.getStaff_id().toString());
            orgUserVo.setName(SecurityUtil.getUserSession().getStaff_info().getName());
            orgUserVo.setCode(SecurityUtil.getUserSession().getStaff_info().getCode());
            orgUserVo.setType("user");
            bBpmProcessVo.setOrgUserVo(orgUserVo);

            // 启动审批流
            bpmProcessTemplatesService.startProcess(bBpmProcessVo);
        }
    }

    /**
     * 附件处理
     */
    public BPoSettlementAttachEntity insertFile(SFileEntity fileEntity, BPoSettlementVo vo, BPoSettlementAttachEntity extra) {
        // 文件处理逻辑
        if (vo.getDoc_att_files() != null && vo.getDoc_att_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo other_file : vo.getDoc_att_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                other_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(other_file, fileInfoEntity);
                fileInfoEntity.setFile_name(other_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 其他附件id
            extra.setOne_file(fileEntity.getId());
            fileEntity.setId(null);
        }else {
            extra.setOne_file(null);
        }
        return extra;
    }

    // ================ BPM回调方法 ================

    /**
     * BPM回调-创建流程时更新bmp实例汇总数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(BPoSettlementVo searchCondition) {
        log.debug("====》审批流程创建成功，更新开始《====");
        BPoSettlementVo vo = selectById(searchCondition.getId());

        /**
         * 更新bmp_instance的摘要数据:
         * bmp_instance_summary:{}  // 供应商：xxx，主体企业：xxx，结算金额:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("供应商：", vo.getSupplier_name());
        jsonObject.put("主体企业：", vo.getPurchaser_name());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(vo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * BPM回调-审批通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(BPoSettlementVo searchCondition) {
        BPoSettlementEntity entity = new BPoSettlementEntity();
        entity.setId(searchCondition.getId());
        entity.setStatus(DictConstant.DICT_B_PO_SETTLEMENT_STATUS_TWO);
        mapper.updateById(entity);
        return UpdateResultUtil.OK(0);
    }

    /**
     * BPM回调-审批拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(BPoSettlementVo searchCondition) {
        BPoSettlementEntity entity = new BPoSettlementEntity();
        entity.setId(searchCondition.getId());
        entity.setStatus(DictConstant.DICT_B_PO_SETTLEMENT_STATUS_THREE);
        mapper.updateById(entity);
        return UpdateResultUtil.OK(0);
    }

    /**
     * BPM回调-审批取消
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(BPoSettlementVo searchCondition) {
        BPoSettlementEntity entity = new BPoSettlementEntity();
        entity.setId(searchCondition.getId());
        entity.setStatus(DictConstant.DICT_B_PO_SETTLEMENT_STATUS_ZERO);
        mapper.updateById(entity);
        return UpdateResultUtil.OK(0);
    }

    /**
     * BPM回调-保存最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(BPoSettlementVo searchCondition) {
        return UpdateResultUtil.OK(0);
    }

    /**
     * BPM作废回调-创建流程时更新bpm实例汇总数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BPoSettlementVo searchCondition) {
        return UpdateResultUtil.OK(0);
    }

    /**
     * BPM作废回调-审批通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(BPoSettlementVo searchCondition) {
        BPoSettlementEntity entity = new BPoSettlementEntity();
        entity.setId(searchCondition.getId());
        entity.setStatus(DictConstant.DICT_B_PO_SETTLEMENT_STATUS_FOUR);
        mapper.updateById(entity);
        return UpdateResultUtil.OK(0);
    }

    /**
     * BPM作废回调-审批拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(BPoSettlementVo searchCondition) {
        BPoSettlementEntity entity = new BPoSettlementEntity();
        entity.setId(searchCondition.getId());
        entity.setStatus(DictConstant.DICT_B_PO_SETTLEMENT_STATUS_TWO);
        mapper.updateById(entity);
        return UpdateResultUtil.OK(0);
    }

    /**
     * BPM作废回调-审批取消
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(BPoSettlementVo searchCondition) {
        return UpdateResultUtil.OK(0);
    }

    /**
     * BPM作废回调-保存最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(BPoSettlementVo searchCondition) {
        return UpdateResultUtil.OK(0);
    }
} 