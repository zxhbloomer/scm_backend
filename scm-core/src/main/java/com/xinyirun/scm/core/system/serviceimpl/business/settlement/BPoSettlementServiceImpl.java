package com.xinyirun.scm.core.system.serviceimpl.business.settlement;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.busniess.pocontract.BPoContractEntity;
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
import com.xinyirun.scm.core.system.mapper.business.pocontract.BPoContractMapper;
import com.xinyirun.scm.core.system.mapper.business.settlement.BPoSettlementAttachMapper;
import com.xinyirun.scm.core.system.mapper.business.settlement.BPoSettlementDetailSourceInboundMapper;
import com.xinyirun.scm.core.system.mapper.business.settlement.BPoSettlementDetailSourceMapper;
import com.xinyirun.scm.core.system.mapper.business.settlement.BPoSettlementMapper;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.total.ICommonPoTotalService;
import com.xinyirun.scm.core.system.service.business.settlement.IBPoSettlementService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BPoSettlementAutoCodeServiceImpl;
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
    
    @Autowired
    private BPoContractMapper bPoContractMapper;
    
    @Autowired
    private BPoSettlementAutoCodeServiceImpl bPoSettlementAutoCodeService;
    
    @Autowired
    private ICommonPoTotalService commonPoTotalService;

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
        
        // 重新计算PO订单合计数据
        commonPoTotalService.reCalculateAllTotalDataByPoSettlementId(entity.getId());

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
        
        // 设置状态为待审批
        entity.setStatus(DictConstant.DICT_B_PO_SETTLEMENT_STATUS_ONE);
        
        // 生成自动编码
        entity.setCode(bPoSettlementAutoCodeService.autoCode().getCode());
        
        // 设置删除标识
        entity.setIs_del(Boolean.FALSE);
        
        // 设置流程名称
        entity.setBpm_process_name("新增采购结算审批");
        
        // 确保ID为null，避免插入时使用旧ID
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
    private void saveDetailList(BPoSettlementVo vo, BPoSettlementEntity entity) {
        List<BPoSettlementDetailSourceInboundVo> detailListData = vo.getDetailListData();
        if (detailListData != null && !detailListData.isEmpty()) {
            for (BPoSettlementDetailSourceInboundVo detailVo : detailListData) {
                // 1. 保存到 b_po_settlement_detail_source 表
                BPoSettlementDetailSourceEntity sourceEntity = new BPoSettlementDetailSourceEntity();
                sourceEntity.setPo_settlement_id(entity.getId());
                sourceEntity.setPo_settlement_code(entity.getCode());
                sourceEntity.setSettle_type(vo.getSettle_type());
                sourceEntity.setBill_type(vo.getBill_type());
                sourceEntity.setPo_order_detail_id(detailVo.getPo_order_detail_id());
                        
                // 通过合同ID查询合同信息获取project_code
                if (detailVo.getPo_contract_id() != null) {
                    BPoContractEntity contractEntity = bPoContractMapper.selectById(detailVo.getPo_contract_id());
                    if (contractEntity != null) {
                        sourceEntity.setProject_code(contractEntity.getProject_code());
                    }
                }
                sourceEntity.setPo_contract_id(detailVo.getPo_contract_id());
                sourceEntity.setPo_contract_code(detailVo.getPo_contract_code());
                sourceEntity.setPo_order_id(detailVo.getPo_order_id());
                sourceEntity.setPo_order_code(detailVo.getPo_order_code());
                
                int sourceResult = bPoSettlementDetailSourceMapper.insert(sourceEntity);
                if (sourceResult == 0) {
                    throw new BusinessException("结算明细源单新增失败");
                }
                
                // 2. 保存到 b_po_settlement_detail_source_inbound 表
                BPoSettlementDetailSourceInboundEntity inboundEntity = new BPoSettlementDetailSourceInboundEntity();
                BeanUtils.copyProperties(detailVo, inboundEntity);
                inboundEntity.setPo_settlement_id(entity.getId());
                inboundEntity.setPo_settlement_code(entity.getCode());
                
                int inboundResult = bPoSettlementDetailSourceInboundMapper.insert(inboundEntity);
                if (inboundResult == 0) {
                    throw new BusinessException("结算明细入库单新增失败");
                }
            }
        }
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
        
        // 重新计算PO订单合计数据
        commonPoTotalService.reCalculateAllTotalDataByPoSettlementId(entity.getId());

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
    public UpdateResultAo<Integer> cancel(BPoSettlementVo vo) {
        // 作废前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BPoSettlementVo _data = mapper.selectById(vo.getId());
        BPoSettlementEntity entity = new BPoSettlementEntity();
        BeanUtils.copyProperties(_data, entity);

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(entity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_SETTLEMENT);
        fileEntity = insertCancelFile(fileEntity, vo);

        entity.setBpm_cancel_process_name("作废采购结算审批");
        entity.setStatus(DictConstant.DICT_B_PO_SETTLEMENT_STATUS_FOUR);
        int result = mapper.updateById(entity);
        if (result == 0) {
            throw new UpdateErrorException("修改失败");
        }

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(entity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_PO_SETTLEMENT);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(vo.getRemark());
        mCancelService.insert(mCancelVo);

        // 重新计算PO订单合计数据
        commonPoTotalService.reCalculateAllTotalDataByPoSettlementId(entity.getId());

        // 3.启动审批流程
        startFlowProcess(vo, SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_SETTLEMENT_CANCEL);

        return UpdateResultUtil.OK(result);
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
     * 根据状态设置处理相关字段
     * @param vo 结算VO对象
     * @param status 状态
     */
    private void setProcessingFields(BPoSettlementVo vo, String status) {
        if (vo.getDetailListData() != null && !vo.getDetailListData().isEmpty()) {
            BigDecimal zero = BigDecimal.ZERO;
            
            for (BPoSettlementDetailSourceInboundVo detail : vo.getDetailListData()) {
                // 根据状态设置字段值
                if (DictConstant.DICT_B_PO_SETTLEMENT_STATUS_ZERO.equals(status) || 
                    DictConstant.DICT_B_PO_SETTLEMENT_STATUS_THREE.equals(status) || 
                    DictConstant.DICT_B_PO_SETTLEMENT_STATUS_FOUR.equals(status)) {
                    // 状态0、3、4：待审批、驳回、作废审批中
                    detail.setProcessing_qty(zero);
                    detail.setProcessing_weight(zero);
                    detail.setProcessing_volume(zero);
                    detail.setUnprocessed_qty(detail.getPlanned_qty() != null ? detail.getPlanned_qty() : zero);
                    detail.setUnprocessed_weight(detail.getPlanned_weight() != null ? detail.getPlanned_weight() : zero);
                    detail.setUnprocessed_volume(detail.getPlanned_volume() != null ? detail.getPlanned_volume() : zero);
                    detail.setProcessed_qty(zero);
                    detail.setProcessed_weight(zero);
                    detail.setProcessed_volume(zero);
                    
                } else if (DictConstant.DICT_B_PO_SETTLEMENT_STATUS_ONE.equals(status)) {
                    // 状态1：审批中
                    detail.setProcessing_qty(detail.getPlanned_qty() != null ? detail.getPlanned_qty() : zero);
                    detail.setProcessing_weight(detail.getPlanned_weight() != null ? detail.getPlanned_weight() : zero);
                    detail.setProcessing_volume(detail.getPlanned_volume() != null ? detail.getPlanned_volume() : zero);
                    detail.setUnprocessed_qty(zero);
                    detail.setUnprocessed_weight(zero);
                    detail.setUnprocessed_volume(zero);
                    detail.setProcessed_qty(zero);
                    detail.setProcessed_weight(zero);
                    detail.setProcessed_volume(zero);
                    
                } else if (DictConstant.DICT_B_PO_SETTLEMENT_STATUS_TWO.equals(status) || 
                           DictConstant.DICT_B_PO_SETTLEMENT_STATUS_SIX.equals(status)) {
                    // 状态2、6：执行中、已完成
                    detail.setProcessing_qty(zero);
                    detail.setProcessing_weight(zero);
                    detail.setProcessing_volume(zero);
                    detail.setUnprocessed_qty(zero);
                    detail.setUnprocessed_weight(zero);
                    detail.setUnprocessed_volume(zero);
                    detail.setProcessed_qty(detail.getPlanned_qty() != null ? detail.getPlanned_qty() : zero);
                    detail.setProcessed_weight(detail.getPlanned_weight() != null ? detail.getPlanned_weight() : zero);
                    detail.setProcessed_volume(detail.getPlanned_volume() != null ? detail.getPlanned_volume() : zero);
                    
                } else if (DictConstant.DICT_B_PO_SETTLEMENT_STATUS_FIVE.equals(status)) {
                    // 状态5：已作废
                    detail.setProcessing_qty(zero);
                    detail.setProcessing_weight(zero);
                    detail.setProcessing_volume(zero);
                    detail.setUnprocessed_qty(zero);
                    detail.setUnprocessed_weight(zero);
                    detail.setUnprocessed_volume(zero);
                    detail.setProcessed_qty(zero);
                    detail.setProcessed_weight(zero);
                    detail.setProcessed_volume(zero);
                }
            }
        }
    }

    /**
     * 作废附件处理
     */
    public SFileEntity insertCancelFile(SFileEntity fileEntity, BPoSettlementVo vo) {
        // 作废附件新增
        if (vo.getCancel_files() != null && vo.getCancel_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo cancel_file : vo.getCancel_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                cancel_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(cancel_file, fileInfoEntity);
                fileInfoEntity.setFile_name(cancel_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
        }
        return fileEntity;
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
     * BPM回调-创建流程时更新bpm实例汇总数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(BPoSettlementVo searchCondition) {
        log.debug("====》审批流程创建成功，更新开始《====");
        BPoSettlementVo vo = selectById(searchCondition.getId());

        /**
         * 更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 供应商：xxx，主体企业：xxx，结算金额:1000
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
        log.debug("====》采购结算[{}]审批流程通过，更新开始《====", searchCondition.getId());
        BPoSettlementVo vo = selectById(searchCondition.getId());
        BPoSettlementEntity entity = new BPoSettlementEntity();
        BeanUtils.copyProperties(vo, entity);
        
        entity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        entity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        entity.setStatus(DictConstant.DICT_B_PO_SETTLEMENT_STATUS_TWO); // 设置为审批通过状态
        entity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        
        // 设置处理相关字段
        setProcessingFields(vo, entity.getStatus());
        
        int result = mapper.updateById(entity);
        if (result <= 0) {
            throw new BusinessException("更新审核状态失败");
        }
        
        // 重新计算PO订单合计数据
        commonPoTotalService.reCalculateAllTotalDataByPoSettlementId(entity.getId());
        
        log.debug("====》采购结算[{}]审批流程通过,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * BPM回调-审批拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(BPoSettlementVo searchCondition) {
        log.debug("====》采购结算[{}]审批流程拒绝，更新开始《====", searchCondition.getId());
        BPoSettlementVo vo = selectById(searchCondition.getId());
        
        BPoSettlementEntity entity = new BPoSettlementEntity();
        BeanUtils.copyProperties(vo, entity);
        entity.setId(searchCondition.getId());
        entity.setStatus(DictConstant.DICT_B_PO_SETTLEMENT_STATUS_THREE);
        entity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);
        
        // 设置处理相关字段
        setProcessingFields(vo, entity.getStatus());
        
        int result = mapper.updateById(entity);
        if (result <= 0) {
            log.error("采购结算审批拒绝更新失败，ID: {}", searchCondition.getId());
            throw new BusinessException("采购结算审批拒绝更新失败");
        }
        
        // 重新计算PO订单合计数据
        commonPoTotalService.reCalculateAllTotalDataByPoSettlementId(entity.getId());
        
        log.debug("====》采购结算[{}]审批流程拒绝,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * BPM回调-审批取消
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(BPoSettlementVo searchCondition) {
        log.debug("====》采购结算[{}]审批流程取消，更新开始《====", searchCondition.getId());
        BPoSettlementVo vo = selectById(searchCondition.getId());
        
        BPoSettlementEntity entity = new BPoSettlementEntity();
        BeanUtils.copyProperties(vo, entity);
        entity.setId(searchCondition.getId());
        entity.setStatus(DictConstant.DICT_B_PO_SETTLEMENT_STATUS_ZERO);
        entity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);
        
        // 设置处理相关字段
        setProcessingFields(vo, entity.getStatus());
        
        int result = mapper.updateById(entity);
        if (result <= 0) {
            log.error("采购结算审批取消更新失败，ID: {}", searchCondition.getId());
            throw new BusinessException("采购结算审批取消更新失败");
        }
        
        // 重新计算PO订单合计数据
        commonPoTotalService.reCalculateAllTotalDataByPoSettlementId(entity.getId());
        
        log.debug("====》采购结算[{}]审批流程取消,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * BPM回调-保存最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(BPoSettlementVo searchCondition) {
        log.debug("====》采购结算[{}]审批流程更新最新审批人，更新开始《====", searchCondition.getId());
        
        BPoSettlementVo vo = selectById(searchCondition.getId());
        BPoSettlementEntity entity = new BPoSettlementEntity();
        BeanUtils.copyProperties(vo, entity);
        entity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        entity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        entity.setNext_approve_name(searchCondition.getNext_approve_name());
        
        int result = mapper.updateById(entity);
        if (result <= 0) {
            log.error("采购结算更新最新审批人失败，ID: {}", searchCondition.getId());
            throw new BusinessException("采购结算更新最新审批人失败");
        }
        
        log.debug("====》采购结算[{}]审批流程更新最新审批人,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * BPM作废回调-创建流程时更新bpm实例汇总数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BPoSettlementVo searchCondition) {
        log.debug("====》采购结算[{}]作废流程创建成功，更新开始《====", searchCondition.getId());
        BPoSettlementVo vo = selectById(searchCondition.getId());
        
        /**
         * 更新bpm_instance的摘要数据
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("结算单编号：", vo.getCode());
        jsonObject.put("供应商：", vo.getSupplier_name());
        jsonObject.put("主体企业：", vo.getPurchaser_name());
        jsonObject.put("结算日期：", vo.getSettlement_date());
        jsonObject.put("结算类型：", vo.getType_name());
        jsonObject.put("作废理由：", vo.getCancel_reason());
        
        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(vo.getBpm_cancel_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);
        
        log.debug("====》采购结算[{}]作废流程创建成功，更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(0);
    }

    /**
     * BPM作废回调-审批通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(BPoSettlementVo searchCondition) {
        log.debug("====》采购结算[{}]作废审批流程通过，更新开始《====", searchCondition.getId());
        BPoSettlementVo vo = selectById(searchCondition.getId());
        
        BPoSettlementEntity entity = new BPoSettlementEntity();
        BeanUtils.copyProperties(vo, entity);

        entity.setBpm_cancel_instance_id(searchCondition.getBpm_instance_id());
        entity.setBpm_cancel_instance_code(searchCondition.getBpm_instance_code());

        entity.setStatus(DictConstant.DICT_B_PO_SETTLEMENT_STATUS_FIVE); // 使用状态5表示已作废
        entity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        entity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        entity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        
        // 设置处理相关字段
        setProcessingFields(vo, entity.getStatus());
        
        int result = mapper.updateById(entity);
        if (result <= 0) {
            log.error("采购结算作废审批通过更新失败，ID: {}", searchCondition.getId());
            throw new BusinessException("采购结算作废审批通过更新失败");
        }
        
        // 重新计算PO订单合计数据
        commonPoTotalService.reCalculateAllTotalDataByPoSettlementId(entity.getId());
        
        log.debug("====》采购结算[{}]作废审批流程通过,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * BPM作废回调-审批拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(BPoSettlementVo searchCondition) {
        log.debug("====》采购结算[{}]作废审批流程拒绝，更新开始《====", searchCondition.getId());
        BPoSettlementVo vo = selectById(searchCondition.getId());
        
        BPoSettlementEntity entity = new BPoSettlementEntity();
        BeanUtils.copyProperties(vo, entity);
        // 作废拒绝，恢复到正常状态
        entity.setStatus(DictConstant.DICT_B_PO_SETTLEMENT_STATUS_TWO); // 恢复到审批通过状态
        entity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        
        // 设置处理相关字段
        setProcessingFields(vo, entity.getStatus());
        
        int result = mapper.updateById(entity);
        if (result <= 0) {
            log.error("采购结算作废审批拒绝更新失败，ID: {}", searchCondition.getId());
            throw new BusinessException("采购结算作废审批拒绝更新失败");
        }
        
        // 重新计算PO订单合计数据
        commonPoTotalService.reCalculateAllTotalDataByPoSettlementId(entity.getId());
        
        // 删除作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(entity.getId());
        mCancelVo.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_SETTLEMENT);
        mCancelService.delete(mCancelVo);
        
        log.debug("====》采购结算[{}]作废审批流程拒绝,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * BPM作废回调-审批取消
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(BPoSettlementVo searchCondition) {
        log.debug("====》采购结算[{}]作废审批流程取消，更新开始《====", searchCondition.getId());
        BPoSettlementVo vo = selectById(searchCondition.getId());
        
        BPoSettlementEntity entity = new BPoSettlementEntity();
        BeanUtils.copyProperties(vo, entity);
        // 作废取消，恢复到正常状态
        entity.setStatus(DictConstant.DICT_B_PO_SETTLEMENT_STATUS_TWO); // 恢复到审批通过状态
        entity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        
        // 设置处理相关字段
        setProcessingFields(vo, entity.getStatus());
        
        int result = mapper.updateById(entity);
        if (result <= 0) {
            log.error("采购结算作废审批取消更新失败，ID: {}", searchCondition.getId());
            throw new BusinessException("采购结算作废审批取消更新失败");
        }
        
        // 重新计算PO订单合计数据
        commonPoTotalService.reCalculateAllTotalDataByPoSettlementId(entity.getId());
        
        // 删除作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(entity.getId());
        mCancelVo.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_SETTLEMENT);
        mCancelService.delete(mCancelVo);
        
        log.debug("====》采购结算[{}]作废审批流程取消,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * BPM作废回调-保存最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(BPoSettlementVo searchCondition) {
        log.debug("====》采购结算[{}]作废流程保存最新审批人《====", searchCondition.getId());
        BPoSettlementVo vo = selectById(searchCondition.getId());
        
        BPoSettlementEntity entity = new BPoSettlementEntity();
        BeanUtils.copyProperties(vo, entity);

        entity.setBpm_cancel_instance_id(searchCondition.getBpm_instance_id());
        entity.setBpm_cancel_instance_code(searchCondition.getBpm_instance_code());
        entity.setNext_approve_name(searchCondition.getNext_approve_name());
        
        int result = mapper.updateById(entity);
        if (result <= 0) {
            log.error("采购结算作废流程保存最新审批人失败，ID: {}", searchCondition.getId());
            throw new BusinessException("采购结算作废流程保存最新审批人失败");
        }
        
        log.debug("====》采购结算[{}]作废流程保存最新审批人结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }
} 