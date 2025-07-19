package com.xinyirun.scm.core.system.serviceimpl.business.wms.inplan;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.inplan.BInPlanAttachEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.inplan.BInPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.inplan.BInPlanEntity;
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
import com.xinyirun.scm.bean.system.vo.wms.inplan.BInPlanAttachVo;
import com.xinyirun.scm.bean.system.vo.wms.inplan.BInPlanDetailVo;
import com.xinyirun.scm.bean.system.vo.wms.inplan.BInPlanVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.bpm.service.business.IBpmInstanceSummaryService;
import com.xinyirun.scm.core.bpm.serviceimpl.business.BpmProcessTemplatesServiceImpl;
import com.xinyirun.scm.core.system.mapper.business.wms.inplan.BInPlanAttachMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.inplan.BInPlanDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.inplan.BInPlanMapper;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.total.ICommonPoTotalService;
import com.xinyirun.scm.core.system.service.business.wms.inplan.IBInPlanService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BInPlanAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BInPlanDetailAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 入库计划 服务实现类
 * </p>
 *
 * @author system
 * @since 2025-06-19
 */
@Slf4j
@Service
public class BInPlanServiceImpl extends BaseServiceImpl<BInPlanMapper, BInPlanEntity> implements IBInPlanService {

    @Autowired
    private BInPlanMapper mapper;

    @Autowired
    private BInPlanDetailMapper bInPlanDetailMapper;

    @Autowired
    private BInPlanAutoCodeServiceImpl bInPlanAutoCodeService;

    @Autowired
    private BInPlanDetailAutoCodeServiceImpl bInPlanDetailAutoCodeService;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private BpmProcessTemplatesServiceImpl bpmProcessTemplatesService;

    @Autowired
    private IBpmInstanceSummaryService iBpmInstanceSummaryService;

    @Autowired
    private MCancelService mCancelService;

    @Autowired
    private ICommonPoTotalService iCommonPoTotalService;

    @Autowired
    private BInPlanAttachMapper bInPlanAttachMapper;

    @Autowired
    private ISFileService isFileService;

    @Autowired
    private MStaffMapper mStaffMapper;

    @Autowired
    private ICommonPoTotalService commonTotalService;

    /**
     * 入库计划新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BInPlanVo> insert(BInPlanVo bInPlanVo) {
        // 2. 保存主表信息
        BInPlanEntity bInPlanEntity = saveMainEntity(bInPlanVo);
        // 3. 保存明细信息
        saveDetailList(bInPlanVo, bInPlanEntity);
        // 4. 保存附件信息
        saveAttach(bInPlanVo, bInPlanEntity);
        // 5. 设置返回ID
        bInPlanVo.setId(bInPlanEntity.getId());
        // 6. 更新入库计划汇总数据
        commonTotalService.reCalculateAllTotalDataByPlanId(bInPlanEntity.getId());

        return InsertResultUtil.OK(bInPlanVo);
    }

    /**
     * 校验新增业务规则
     */
    private void checkInsertLogic(BInPlanVo bInPlanVo) {
        CheckResultAo cr = checkLogic(bInPlanVo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 保存主表信息
     */
    private BInPlanEntity saveMainEntity(BInPlanVo bInPlanVo) {
        BInPlanEntity bInPlanEntity = new BInPlanEntity();
        BeanUtils.copyProperties(bInPlanVo, bInPlanEntity);
        bInPlanEntity.setStatus(DictConstant.DICT_B_IN_PLAN_STATUS_ONE);
        bInPlanEntity.setCode(bInPlanAutoCodeService.autoCode().getCode());
        bInPlanEntity.setIs_del(0);
        bInPlanEntity.setBpm_process_name("新增入库计划审批");
        
        List<BInPlanDetailVo> detailListData = bInPlanVo.getDetailListData();
        calculatePlanAmounts(detailListData, bInPlanEntity);
        bInPlanEntity.setId(null); // 确保ID为null，避免插入时使用旧ID
        int result = mapper.insert(bInPlanEntity);
        if (result == 0){
            throw new BusinessException("新增失败");
        }
        return bInPlanEntity;
    }

    /**
     * 保存明细信息
     */
    private void saveDetailList(BInPlanVo bInPlanVo, BInPlanEntity bInPlanEntity) {
        List<BInPlanDetailVo> detailListData = bInPlanVo.getDetailListData();
        if (detailListData != null && !detailListData.isEmpty()) {
            int no = 1; // 序号从1开始累加
            for (BInPlanDetailVo detailVo : detailListData) {
                BInPlanDetailEntity bInPlanDetailEntity = new BInPlanDetailEntity();
                BeanUtils.copyProperties(detailVo, bInPlanDetailEntity);
                bInPlanDetailEntity.setIn_plan_id(bInPlanEntity.getId());
                bInPlanDetailEntity.setNo(no); // 设置累加序号
                
                // 使用自动编码生成 code
                bInPlanDetailEntity.setCode(bInPlanDetailAutoCodeService.autoCode().getCode());
                
                // 如果入库计划类型是采购入库，设置 serial 相关字段
                if (DictConstant.DICT_B_IN_PLAN_TYPE_CG.equals(bInPlanVo.getType())) {
                    bInPlanDetailEntity.setSerial_id(detailVo.getOrder_id());
                    bInPlanDetailEntity.setSerial_code(detailVo.getOrder_code());
                    bInPlanDetailEntity.setSerial_type(DictConstant.DICT_B_IN_PLAN_TYPE);
                }
                
                // 设置未处理数量、重量、体积
                bInPlanDetailEntity.setUnprocessed_qty(bInPlanDetailEntity.getQty());
                bInPlanDetailEntity.setUnprocessed_weight(BigDecimal.ZERO);
                bInPlanDetailEntity.setUnprocessed_volume(BigDecimal.ZERO);
                
                int result = bInPlanDetailMapper.insert(bInPlanDetailEntity);
                if (result == 0){
                    throw new BusinessException("明细新增失败");
                }
                no++; // 序号递增
            }
        }
    }

    /**
     * 保存附件信息
     */
    private void saveAttach(BInPlanVo bInPlanVo, BInPlanEntity bInPlanEntity) {
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bInPlanEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_IN_PLAN);
        BInPlanAttachEntity bInPlanAttachEntity = insertFile(fileEntity, bInPlanVo, new BInPlanAttachEntity());
        bInPlanAttachEntity.setIn_plan_id(bInPlanEntity.getId());
        int insert = bInPlanAttachMapper.insert(bInPlanAttachEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增附件失败");
        }
    }

    /**
     * 启动审批流新增入库计划
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BInPlanVo> startInsert(BInPlanVo bInPlanVo) {
        // 1. 校验业务规则
        checkInsertLogic(bInPlanVo);
        
        // 2.保存入库计划
        InsertResultAo<BInPlanVo> insertResultAo = insert(bInPlanVo);

        // 3.启动审批流程
        startFlowProcess(bInPlanVo, SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_IN_PLAN);

        return insertResultAo;
    }

    @Override
    public IPage<BInPlanVo> selectPage(BInPlanVo searchCondition) {
        // 分页条件
        Page<BInPlanVo> pageCondition = new Page<>(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询入库计划page
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 获取入库计划信息
     */
    @Override
    public BInPlanVo selectById(Integer id) {
        BInPlanVo bInPlanVo = mapper.selectId(id);
        if (bInPlanVo == null) {
            throw new BusinessException("入库计划不存在");
        }

        // 其他附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(bInPlanVo.getDoc_att_file());
        bInPlanVo.setDoc_att_files(doc_att_files);

        // 查询是否存在作废记录
        if (DictConstant.DICT_B_IN_PLAN_STATUS_FIVE.equals(bInPlanVo.getStatus())) {
            MCancelVo serialIdAndType = new MCancelVo();
            serialIdAndType.setSerial_id(bInPlanVo.getId());
            serialIdAndType.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_IN_PLAN);
            MCancelVo mCancelVo = mCancelService.selectBySerialIdAndType(serialIdAndType);
            if (mCancelVo != null) {
                // 作废理由
                bInPlanVo.setCancel_reason(mCancelVo.getRemark());
                // 作废附件信息
                if (mCancelVo.getFile_id() != null) {
                    List<SFileInfoVo> cancel_doc_att_files = isFileService.selectFileInfo(mCancelVo.getFile_id());
                    bInPlanVo.setCancel_doc_att_files(cancel_doc_att_files);
                }

                // 通过表m_staff获取作废提交人名称
                MStaffVo searchCondition = new MStaffVo();
                searchCondition.setId(mCancelVo.getC_id());
                MStaffVo staffVo = mStaffMapper.selectByid(searchCondition);
                if (staffVo != null) {
                    bInPlanVo.setCancel_name(staffVo.getName());
                }

                // 作废时间
                bInPlanVo.setCancel_time(mCancelVo.getC_time());
            }
        }

        return bInPlanVo;
    }

    /**
     * 修改入库计划
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BInPlanVo> update(BInPlanVo bInPlanVo) {

        // 2. 保存主表信息
        BInPlanEntity bInPlanEntity = new BInPlanEntity();
        BeanUtils.copyProperties(bInPlanVo, bInPlanEntity);
        
        List<BInPlanDetailVo> detailListData = bInPlanVo.getDetailListData();
        calculatePlanAmounts(detailListData, bInPlanEntity);

        bInPlanEntity.setStatus(DictConstant.DICT_B_PROJECT_STATUS_ONE);
        
        int result = mapper.updateById(bInPlanEntity);
        if (result == 0) {
            throw new UpdateErrorException("修改失败");
        }

        // 3. 删除旧明细
        bInPlanDetailMapper.deleteByInPlanId(bInPlanVo.getId());
        
        // 4. 保存新明细信息
        saveDetailList(bInPlanVo, bInPlanEntity);

        // 5. 更新附件信息
        updateAttach(bInPlanVo, bInPlanEntity);

        // 6. 更新入库计划汇总数据
//        iCommonTotalService.reCalculateAllTotalDataByInPlanId(bInPlanEntity.getId());

        return UpdateResultUtil.OK(bInPlanVo);
    }

    /**
     * 更新附件信息
     */
    private void updateAttach(BInPlanVo bInPlanVo, BInPlanEntity bInPlanEntity) {
        BInPlanAttachVo bInPlanAttachVo = bInPlanAttachMapper.selectByInPlanId(bInPlanEntity.getId());
        if (bInPlanAttachVo != null) {
            // 更新附件信息
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bInPlanEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_IN_PLAN);
            BInPlanAttachEntity bInPlanAttachEntity = (BInPlanAttachEntity) BeanUtilsSupport.copyProperties(bInPlanAttachVo, BInPlanAttachEntity.class);
            insertFile(fileEntity, bInPlanVo, bInPlanAttachEntity);
            bInPlanAttachEntity.setIn_plan_id(bInPlanEntity.getId());
            int update = bInPlanAttachMapper.updateById(bInPlanAttachEntity);
            if (update == 0) {
                throw new UpdateErrorException("更新附件信息失败");
            }
        } else {
            // 新增附件信息
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bInPlanEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_IN_PLAN);
            BInPlanAttachEntity bInPlanAttachEntity = new BInPlanAttachEntity();
            insertFile(fileEntity, bInPlanVo, bInPlanAttachEntity);
            bInPlanAttachEntity.setIn_plan_id(bInPlanEntity.getId());
            int insert = bInPlanAttachMapper.insert(bInPlanAttachEntity);
            if (insert == 0) {
                throw new UpdateErrorException("新增附件信息失败");
            }
        }
    }

    /**
     * 启动审批流修改入库计划
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BInPlanVo> startUpdate(BInPlanVo bInPlanVo) {
        // 1. 校验业务规则
        CheckResultAo cr = checkLogic(bInPlanVo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        
        // 2.修改入库计划
        UpdateResultAo<BInPlanVo> updateResultAo = update(bInPlanVo);

        // 3.启动审批流程
        startFlowProcess(bInPlanVo, SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_IN_PLAN);

        return updateResultAo;
    }

    /**
     * 删除入库计划
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(BInPlanVo bInPlanVo) {
        // 删除前check
        CheckResultAo cr = checkLogic(bInPlanVo, CheckResultAo.DELETE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 删除明细
        bInPlanDetailMapper.deleteByInPlanId(bInPlanVo.getId());
        
        // 删除主表
        int result = mapper.deleteById(bInPlanVo.getId());
        if (result == 0) {
            throw new BusinessException("删除失败");
        }

        return DeleteResultUtil.OK(result);
    }

    @Override
    public CheckResultAo checkLogic(BInPlanVo bean, String checkType) {
        BInPlanEntity bInPlanEntity = null;
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                if (bean.getDetailListData() == null || bean.getDetailListData().isEmpty()) {
                    return CheckResultUtil.NG("请添加明细数据！");
                }
                
                // 校验明细数量
                for (BInPlanDetailVo detailVo : bean.getDetailListData()) {
                    if (detailVo.getQty() == null || detailVo.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                        return CheckResultUtil.NG("校验出错：提交的货物明细中存在尚未完成设置的数据。");
                    }
                }
                break;
                
            case CheckResultAo.UPDATE_CHECK_TYPE:
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bInPlanEntity = mapper.selectById(bean.getId());
                if (bInPlanEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }
                
                // 是否待审批或者驳回状态
                if (!Objects.equals(bInPlanEntity.getStatus(), DictConstant.DICT_B_IN_PLAN_STATUS_ZERO)
                    && !Objects.equals(bInPlanEntity.getStatus(), DictConstant.DICT_B_IN_PLAN_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("修改失败，入库计划[%s]不是待审批,驳回状态,无法修改", bInPlanEntity.getCode()));
                }

                if (bean.getDetailListData() == null || bean.getDetailListData().isEmpty()) {
                    return CheckResultUtil.NG("至少添加一个明细");
                }
                break;
                
            // 删除校验
            case CheckResultAo.DELETE_CHECK_TYPE:
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bInPlanEntity = mapper.selectById(bean.getId());
                if (bInPlanEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }
                
                // 是否待审批或者驳回状态
                if (!Objects.equals(bInPlanEntity.getStatus(), DictConstant.DICT_B_IN_PLAN_STATUS_ZERO)
                    && !Objects.equals(bInPlanEntity.getStatus(), DictConstant.DICT_B_IN_PLAN_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("删除失败，入库计划[%s]不是待审批,驳回状态,无法删除", bInPlanEntity.getCode()));
                }
                break;
                
            // 作废校验
            case CheckResultAo.CANCEL_CHECK_TYPE:
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bInPlanEntity = mapper.selectById(bean.getId());
                if (bInPlanEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否已经作废
                if (Objects.equals(bInPlanEntity.getStatus(), DictConstant.DICT_B_IN_PLAN_STATUS_FIVE)
                    || Objects.equals(bInPlanEntity.getStatus(), DictConstant.DICT_B_IN_PLAN_STATUS_FOUR)) {
                    return CheckResultUtil.NG(String.format("作废失败，入库计划[%s]无法重复作废", bInPlanEntity.getCode()));
                }
                
                if (!Objects.equals(bInPlanEntity.getStatus(), DictConstant.DICT_B_IN_PLAN_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("作废失败，入库计划[%s]审核中，无法作废", bInPlanEntity.getCode()));
                }
                break;
                
            // 完成校验
            case CheckResultAo.FINISH_CHECK_TYPE:
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bInPlanEntity = mapper.selectById(bean.getId());
                if (bInPlanEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否审批通过
                if (!Objects.equals(bInPlanEntity.getStatus(), DictConstant.DICT_B_IN_PLAN_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("完成失败，入库计划[%s]未审批通过", bInPlanEntity.getCode()));
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    @Override
    public List<BInPlanVo> selectExportList(BInPlanVo param) {
        return mapper.selectExportList(param);
    }

    @Override
    public BInPlanVo querySum(BInPlanVo searchCondition) {
        return null;
    }

    @Override
    public BInPlanVo getPrintInfo(BInPlanVo searchCondition) {
        return null;
    }

    @Override
    public DeleteResultAo<Integer> delete(List<BInPlanVo> searchCondition) {
        return null;
    }

    /**
     * 作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> cancel(BInPlanVo searchCondition) {
        // 作废前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BInPlanEntity bInPlanEntity = mapper.selectById(searchCondition.getId());

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bInPlanEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_IN_PLAN);
        fileEntity = insertCancelFile(fileEntity, searchCondition);

        bInPlanEntity.setBpm_cancel_process_name("作废入库计划审批");
        bInPlanEntity.setStatus(DictConstant.DICT_B_IN_PLAN_STATUS_FOUR);
        int result = mapper.updateById(bInPlanEntity);
        if (result == 0) {
            throw new UpdateErrorException("修改失败");
        }

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bInPlanEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_IN_PLAN);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(searchCondition.getRemark());
        mCancelService.insert(mCancelVo);

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByPlanId(bInPlanEntity.getId());

        // 3.启动审批流程
        startFlowProcess(searchCondition, SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_IN_PLAN_CANCEL);

        return UpdateResultUtil.OK(result);
    }

    /**
     * 完成
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> finish(BInPlanVo searchCondition) {
        // 完成前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.FINISH_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BInPlanEntity bInPlanEntity = mapper.selectById(searchCondition.getId());
        bInPlanEntity.setStatus(DictConstant.DICT_B_IN_PLAN_STATUS_SIX);
        int update = mapper.updateById(bInPlanEntity);
        if (update == 0) {
            throw new UpdateErrorException("修改失败");
        }

        return UpdateResultUtil.OK(update);
    }

    /**
     * 初始化计划数据
     */
    @Override
    public List<BInPlanDetailVo> initPlanData(BInPlanDetailVo searchCondition) {
        return mapper.initPlanData(searchCondition);
    }

    /**
     * 计算入库计划金额
     */
    private void calculatePlanAmounts(List<BInPlanDetailVo> detailListData, BInPlanEntity bInPlanEntity) {
        BigDecimal planAmountSum = BigDecimal.ZERO;
        BigDecimal planTotal = BigDecimal.ZERO;
        
        if (detailListData != null && !detailListData.isEmpty()) {
            for (BInPlanDetailVo detail : detailListData) {
                BigDecimal qty = detail.getQty() != null ? detail.getQty() : BigDecimal.ZERO;
                BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
                
                // 计算总金额：sum(明细.qty * 明细.price)
                BigDecimal amount = qty.multiply(price);
                planAmountSum = planAmountSum.add(amount);
                
                // 计算总数量：sum(明细.qty)
                planTotal = planTotal.add(qty);
            }
        }
        
        // 设置计算结果到实体对象（如果主表有相关字段）
        // bInPlanEntity.setPlan_amount_sum(planAmountSum);
        // bInPlanEntity.setPlan_total(planTotal);
    }

    /**
     * 启动审批流
     */
    public void startFlowProcess(BInPlanVo bean, String type){
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
    public SFileEntity insertCancelFile(SFileEntity fileEntity, BInPlanVo vo) {
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
     * 附件文件处理
     */
    public BInPlanAttachEntity insertFile(SFileEntity fileEntity, BInPlanVo vo, BInPlanAttachEntity extra) {
        // 入库计划附件新增
        if (vo.getDoc_att_files() != null && vo.getDoc_att_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo doc_att_file : vo.getDoc_att_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                doc_att_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(doc_att_file, fileInfoEntity);
                fileInfoEntity.setFile_name(doc_att_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 入库计划附件id
            extra.setOne_file(fileEntity.getId());
            fileEntity.setId(null);
        } else {
            extra.setOne_file(null);
        }
        return extra;
    }

    // ============ BPM 回调方法实现 ============

    /**
     * 审批流程回调 - 创建流程
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(BInPlanVo searchCondition){
        log.debug("====》审批流程创建成功，更新开始《====");
        BInPlanVo bInPlanVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("入库计划编号：", bInPlanVo.getCode());
        jsonObject.put("计划时间：", bInPlanVo.getPlan_time());
        jsonObject.put("类型：", bInPlanVo.getType_name());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(bInPlanVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 审批流程回调 - 审批通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(BInPlanVo searchCondition) {
        log.debug("====》入库计划[{}]审批流程通过，更新开始《====", searchCondition.getId());
        BInPlanEntity bInPlanEntity = mapper.selectById(searchCondition.getId());

        bInPlanEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bInPlanEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bInPlanEntity.setStatus(DictConstant.DICT_B_IN_PLAN_STATUS_TWO);
        bInPlanEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        
        int result = mapper.updateById(bInPlanEntity);
        if (result == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByPlanId(bInPlanEntity.getId());

        log.debug("====》入库计划[{}]审批流程通过,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 审批流程回调 - 审批拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(BInPlanVo searchCondition) {
        log.debug("====》入库计划[{}]审批流程拒绝，更新开始《====", searchCondition.getId());
        BInPlanEntity bInPlanEntity = mapper.selectById(searchCondition.getId());

        bInPlanEntity.setStatus(DictConstant.DICT_B_IN_PLAN_STATUS_THREE);
        bInPlanEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);
        
        int result = mapper.updateById(bInPlanEntity);
        if (result == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByPlanId(bInPlanEntity.getId());

        log.debug("====》入库计划[{}]审批流程拒绝,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 审批流程回调 - 审批取消
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(BInPlanVo searchCondition) {
        log.debug("====》入库计划[{}]审批流程取消，更新开始《====", searchCondition.getId());
        BInPlanEntity bInPlanEntity = mapper.selectById(searchCondition.getId());

        bInPlanEntity.setStatus(DictConstant.DICT_B_IN_PLAN_STATUS_ZERO);
        bInPlanEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);
        
        int result = mapper.updateById(bInPlanEntity);
        if (result == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByPlanId(bInPlanEntity.getId());

        log.debug("====》入库计划[{}]审批流程取消,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    // ============ 作废BPM 回调方法实现 ============

    /**
     * 作废审批流程回调 - 创建流程
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BInPlanVo searchCondition){
        // 作废流程创建逻辑
        return UpdateResultUtil.OK(0);
    }

    /**
     * 作废审批流程回调 - 审批通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(BInPlanVo searchCondition) {
        log.debug("====》入库计划[{}]作废审批流程通过，更新开始《====", searchCondition.getId());
        BInPlanEntity bInPlanEntity = mapper.selectById(searchCondition.getId());

        bInPlanEntity.setBpm_cancel_instance_id(searchCondition.getBpm_cancel_instance_id());
        bInPlanEntity.setBpm_cancel_instance_code(searchCondition.getBpm_cancel_instance_code());
        bInPlanEntity.setStatus(DictConstant.DICT_B_IN_PLAN_STATUS_FIVE);
        bInPlanEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        
        int result = mapper.updateById(bInPlanEntity);
        if (result == 0) {
            throw new UpdateErrorException("更新作废状态失败");
        }

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByPlanId(bInPlanEntity.getId());

        log.debug("====》入库计划[{}]作废审批流程通过,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 作废审批流程回调 - 审批拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(BInPlanVo searchCondition) {
        log.debug("====》入库计划[{}]作废审批流程拒绝，更新开始《====", searchCondition.getId());
        BInPlanEntity bInPlanEntity = mapper.selectById(searchCondition.getId());

        // 作废拒绝，恢复到正常状态
        bInPlanEntity.setStatus(DictConstant.DICT_B_IN_PLAN_STATUS_TWO);
        bInPlanEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        
        int result = mapper.updateById(bInPlanEntity);
        if (result == 0) {
            throw new UpdateErrorException("更新状态失败");
        }

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByPlanId(bInPlanEntity.getId());

        // 删除作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bInPlanEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_IN_PLAN);
        mCancelService.delete(mCancelVo);


        log.debug("====》入库计划[{}]作废审批流程拒绝,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 作废审批流程回调 - 审批取消
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(BInPlanVo searchCondition) {
        log.debug("====》入库计划[{}]作废审批流程取消，更新开始《====", searchCondition.getId());
        BInPlanEntity bInPlanEntity = mapper.selectById(searchCondition.getId());

        // 作废取消，恢复到正常状态
        bInPlanEntity.setStatus(DictConstant.DICT_B_IN_PLAN_STATUS_TWO);
        bInPlanEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        
        int result = mapper.updateById(bInPlanEntity);
        if (result == 0) {
            throw new UpdateErrorException("更新状态失败");
        }

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByPlanId(bInPlanEntity.getId());

        // 删除作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bInPlanEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_IN_PLAN);
        mCancelService.delete(mCancelVo);

        log.debug("====》入库计划[{}]作废审批流程取消,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 作废审批流程回调 - 保存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(BInPlanVo searchCondition) {
        // 作废保存逻辑
        return UpdateResultUtil.OK(0);
    }

    /**
     * BPM回调-保存最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(BInPlanVo searchCondition) {
        log.debug("====》入库计划[{}]审批流程更新最新审批人，更新开始《====", searchCondition.getId());

        BInPlanEntity bInPlanEntity = mapper.selectById(searchCondition.getId());
        bInPlanEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bInPlanEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bInPlanEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bInPlanEntity);

        log.debug("====》入库计划[{}]审批流程更新最新审批人,更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }
}
